package connect.im.model;

import android.text.TextUtils;

import com.google.protobuf.ByteString;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import connect.db.SharedPreferenceUtil;
import connect.im.bean.Session;
import connect.im.bean.SocketACK;
import connect.utils.StringUtil;
import connect.utils.cryption.EncryptionUtil;
import connect.utils.cryption.SupportKeyUril;
import connect.utils.log.LogManager;
import protos.Connect;

/**
 * Assembly chat interface to send message
 * Created by gtq on 2016/12/3.
 */
public class ChatSendManager {

    private static String Tag="ChatSendManager";
    private static ChatSendManager chatSendManager;

    public static ChatSendManager getInstance() {
        if (chatSendManager == null) {
            synchronized (ChatSendManager.class) {
                if (chatSendManager == null) {
                    chatSendManager = new ChatSendManager();
                }
            }
        }
        return chatSendManager;
    }

    private static final int coreSize = 3;
    private static final int maxSize = 6;
    private static final int aliveSize = 1;

    private static BlockingQueue<Runnable> linkedBlockingQueue = new LinkedBlockingQueue<>();
    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(coreSize, maxSize, aliveSize, TimeUnit.DAYS, linkedBlockingQueue);

    /**
     * Other messages
     */
    public void sendMsgidMsg(SocketACK order, String msgid, ByteString bytes) {
        sendAckMsg(order, null, msgid, bytes);
    }

    /**
     * Robot news
     */
    public void sendRobotAckMsg(SocketACK order, String roomkey, Connect.MSMessage data) {
        sendAckMsg(order, roomkey, data.getMsgId(), data.toByteString());
    }

    /**
     * send byte[]
     *
     * @param order
     * @param data
     */
    public void sendChatAckMsg(SocketACK order, String roomkey, Connect.MessageData data) {
        String priKey = SharedPreferenceUtil.getInstance().getPriKey();

        //messagePost
        String postsign = SupportKeyUril.signHash(priKey, data.toByteArray());
        String mypubkey = SupportKeyUril.getPubKeyFromPriKey(priKey);
        Connect.MessagePost messagePost = Connect.MessagePost.newBuilder().
                setMsgData(data).setSign(postsign).
                setPubKey(mypubkey).build();

        sendAckMsg(order, roomkey, data.getMsgId(), messagePost.toByteString());
    }

    public void sendAckMsg(SocketACK order, String roomkey, String msgid, ByteString bytes) {
        if (!TextUtils.isEmpty(msgid)) {
            LogManager.getLogger().d(Tag, msgid);
        }

        SendChatRun sendChatRun = new SendChatRun(order, bytes);
        threadPoolExecutor.execute(sendChatRun);

        boolean canFailReSend = true;
        byte[] getOrder = order.getOrder();
        switch (getOrder[0]) {
            case 0x04:
                canFailReSend = false;
                break;
        }
        if (canFailReSend) {
            FailMsgsManager.getInstance().sendDelayFailMsg(roomkey, msgid, order, bytes);
        }
    }

    public void sendToMsg(SocketACK ack, ByteString byteString) {
        SendChatRun sendChatRun = new SendChatRun(false, ack, byteString);
        threadPoolExecutor.execute(sendChatRun);
    }

    protected class SendChatRun implements Runnable {
        private boolean transfer;
        private SocketACK ack;
        private ByteString bytes;

        SendChatRun(SocketACK ack, ByteString bytes) {
            this.transfer = true;
            this.ack = ack;
            this.bytes = bytes;
        }

        SendChatRun(boolean transfer, SocketACK ack, ByteString bytes) {
            this.transfer = transfer;
            this.ack = ack;
            this.bytes = bytes;
        }

        @Override
        public synchronized void run() {
            try {
                ByteBuffer byteBuffer = null;
                if (transfer) { // transferData,Encapsulating server checksum data
                    String priKey = SharedPreferenceUtil.getInstance().getPriKey();
                    Connect.GcmData gcmData = EncryptionUtil.encodeAESGCMStructData(SupportKeyUril.EcdhExts.NONE,
                            Session.getInstance().getUserCookie("TEMPCOOKIE").getSalt(), bytes);
                    String signHash = SupportKeyUril.signHash(priKey, gcmData.toByteArray());
                    Connect.IMTransferData transferData = Connect.IMTransferData.newBuilder().
                            setSign(signHash).setCipherData(gcmData).build();
                    byteBuffer = protoToByteBuffer(ack.getOrder(), transferData.toByteArray());
                } else {
                    byteBuffer = protoToByteBuffer(ack.getOrder(), bytes.toByteArray());
                }

                sendToBytes(byteBuffer);
            } catch (Exception e) {
                e.printStackTrace();
                String errInfo = e.getMessage();
                if (TextUtils.isEmpty(errInfo)) {
                    errInfo = "";
                }
                LogManager.getLogger().d(Tag, "exception order: [" + ack.getOrder()[0] + "][" + ack.getOrder()[1] + "]" + errInfo);
            }
        }

        private void sendToBytes(ByteBuffer byteBuffer) {
            boolean avaliableConnc = true;
            try {
                SocketChannel channel = ConnectManager.getInstance().getSocketChannel();
                avaliableConnc = ConnectManager.getInstance().avaliableConnect();
                if (avaliableConnc) {
                    while (byteBuffer.hasRemaining()) {
                        channel.write(byteBuffer);
                    }
                    LogManager.getLogger().d(Tag, "send message success");
                }
            } catch (IOException e) {
                e.printStackTrace();
                avaliableConnc = false;
            }

            if (!avaliableConnc) {
                ConnectManager.getInstance().reconDelay();
            }
        }

        /** version number */
        private static final byte MSG_VERSION = 0x01;
        /** Message length */
        private static final int MSG_BODY_LENGTH = 4;
        /** Message header length */
        private static final int MSG_HEADER_LENGTH = 13;

        /**
         * Assemble Command + message to ByteBuffer
         *
         * @param orders
         * @param msgBytes
         * @return
         */
        private ByteBuffer protoToByteBuffer(byte[] orders, byte[] msgBytes) {
            byte[] bytesLength = ByteBuffer.allocate(MSG_BODY_LENGTH).putInt(msgBytes.length).array();
            byte[] headArr = new byte[MSG_HEADER_LENGTH + msgBytes.length];

            byte[] randomBytes = SecureRandom.getSeed(4);

            ByteBuffer header = ByteBuffer.allocate(MSG_HEADER_LENGTH);
            header.put(orders[0]);
            header.put(bytesLength);
            header.put(orders[1]);
            header.put(randomBytes);
            header.put(StringUtil.MSG_HEADER_EXI);

            byte[] ext = null;
            try {
                ext = StringUtil.byteTomd5(header.array());
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            ByteBuffer buffer = ByteBuffer.wrap(headArr);
            buffer.put(MSG_VERSION);
            buffer.put(orders[0]);
            buffer.put(bytesLength);
            buffer.put(orders[1]);
            buffer.put(randomBytes);
            buffer.put(ext[0]);
            buffer.put(ext[1]);
            buffer.put(msgBytes);
            buffer.flip();
            return buffer;
        }
    }
}
