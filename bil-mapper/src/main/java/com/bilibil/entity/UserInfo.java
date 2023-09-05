package com.bilibil.entity;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

/**
 * Date:  2023/8/20
 */
@Document(indexName = "user-infos")
public class UserInfo {

    private Long id;

    private Long userId;
    @Field(type = FieldType.Text)
    private String nick;

    private String avatar;

    private String sign;

    private String gender;

    private String birth;
    @Field(type = FieldType.Date)
    private Date createTime;

    @Field(type = FieldType.Date)
    private Date updateTime;
    // true 是关注，false 是没有关注
    private Boolean followed;
    public UserInfo() {
    }

    public UserInfo(Long id, Long userId, String nick, String avatar, String sign, String gender, String birth, Date createTime, Date updateTime) {
        this.id = id;
        this.userId = userId;
        // 昵称
        this.nick = nick;
        this.avatar = avatar;
        this.sign = sign;
        this.gender = gender;
        this.birth = birth;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public Boolean getFollowed() {
        return followed;
    }

    public void setFollowed(Boolean followed) {
        this.followed = followed;
    }

    /**
     * 获取
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取
     * @return userId
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 设置
     * @param userId
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 获取
     * @return nick
     */
    public String getNick() {
        return nick;
    }

    /**
     * 设置
     * @param nick
     */
    public void setNick(String nick) {
        this.nick = nick;
    }

    /**
     * 获取
     * @return avatar
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     * 设置
     * @param avatar
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /**
     * 获取
     * @return sign
     */
    public String getSign() {
        return sign;
    }

    /**
     * 设置
     * @param sign
     */
    public void setSign(String sign) {
        this.sign = sign;
    }

    /**
     * 获取
     * @return gender
     */
    public String getGender() {
        return gender;
    }

    /**
     * 设置
     * @param gender
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * 获取
     * @return birth
     */
    public String getBirth() {
        return birth;
    }

    /**
     * 设置
     * @param birth
     */
    public void setBirth(String birth) {
        this.birth = birth;
    }

    /**
     * 获取
     * @return createTime
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置
     * @param createTime
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取
     * @return updateTime
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置
     * @param updateTime
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "UserInfo{id = " + id + ", userId = " + userId + ", nick = " + nick + ", avatar = " + avatar + ", sign = " + sign + ", gender = " + gender + ", birth = " + birth + ", createTime = " + createTime + ", updateTime = " + updateTime + "}";
    }
}
