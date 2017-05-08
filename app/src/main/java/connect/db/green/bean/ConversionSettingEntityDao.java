package connect.db.green.bean;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "CONVERSION_SETTING_ENTITY".
*/
public class ConversionSettingEntityDao extends AbstractDao<ConversionSettingEntity, Long> {

    public static final String TABLENAME = "CONVERSION_SETTING_ENTITY";

    /**
     * Properties of entity ConversionSettingEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property _id = new Property(0, Long.class, "_id", true, "_ID");
        public final static Property Identifier = new Property(1, String.class, "identifier", false, "IDENTIFIER");
        public final static Property Snap_time = new Property(2, Long.class, "snap_time", false, "SNAP_TIME");
        public final static Property Disturb = new Property(3, Integer.class, "disturb", false, "DISTURB");
    }


    public ConversionSettingEntityDao(DaoConfig config) {
        super(config);
    }
    
    public ConversionSettingEntityDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"CONVERSION_SETTING_ENTITY\" (" + //
                "\"_ID\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: _id
                "\"IDENTIFIER\" TEXT," + // 1: identifier
                "\"SNAP_TIME\" INTEGER," + // 2: snap_time
                "\"DISTURB\" INTEGER);"); // 3: disturb
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"CONVERSION_SETTING_ENTITY\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, ConversionSettingEntity entity) {
        stmt.clearBindings();
 
        Long _id = entity.get_id();
        if (_id != null) {
            stmt.bindLong(1, _id);
        }
 
        String identifier = entity.getIdentifier();
        if (identifier != null) {
            stmt.bindString(2, identifier);
        }
 
        Long snap_time = entity.getSnap_time();
        if (snap_time != null) {
            stmt.bindLong(3, snap_time);
        }
 
        Integer disturb = entity.getDisturb();
        if (disturb != null) {
            stmt.bindLong(4, disturb);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, ConversionSettingEntity entity) {
        stmt.clearBindings();
 
        Long _id = entity.get_id();
        if (_id != null) {
            stmt.bindLong(1, _id);
        }
 
        String identifier = entity.getIdentifier();
        if (identifier != null) {
            stmt.bindString(2, identifier);
        }
 
        Long snap_time = entity.getSnap_time();
        if (snap_time != null) {
            stmt.bindLong(3, snap_time);
        }
 
        Integer disturb = entity.getDisturb();
        if (disturb != null) {
            stmt.bindLong(4, disturb);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public ConversionSettingEntity readEntity(Cursor cursor, int offset) {
        ConversionSettingEntity entity = new ConversionSettingEntity( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // _id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // identifier
            cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2), // snap_time
            cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3) // disturb
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, ConversionSettingEntity entity, int offset) {
        entity.set_id(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setIdentifier(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setSnap_time(cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2));
        entity.setDisturb(cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(ConversionSettingEntity entity, long rowId) {
        entity.set_id(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(ConversionSettingEntity entity) {
        if(entity != null) {
            return entity.get_id();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(ConversionSettingEntity entity) {
        return entity.get_id() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}