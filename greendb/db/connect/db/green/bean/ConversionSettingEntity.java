package connect.db.green.bean;

import org.greenrobot.greendao.annotation.*;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit.

/**
 * Entity mapped to table "CONVERSION_SETTING_ENTITY".
 */
@Entity
public class ConversionSettingEntity implements java.io.Serializable {

    @Id(autoincrement = true)
    private Long _id;
    private String identifier;
    private Long snap_time;
    private Integer disturb;

    @Generated
    public ConversionSettingEntity() {
    }

    public ConversionSettingEntity(Long _id) {
        this._id = _id;
    }

    @Generated
    public ConversionSettingEntity(Long _id, String identifier, Long snap_time, Integer disturb) {
        this._id = _id;
        this.identifier = identifier;
        this.snap_time = snap_time;
        this.disturb = disturb;
    }

    public Long get_id() {
        return _id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Long getSnap_time() {
        return snap_time;
    }

    public void setSnap_time(Long snap_time) {
        this.snap_time = snap_time;
    }

    public Integer getDisturb() {
        return disturb;
    }

    public void setDisturb(Integer disturb) {
        this.disturb = disturb;
    }

}
