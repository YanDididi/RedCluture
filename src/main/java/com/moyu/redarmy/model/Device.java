package com.moyu.redarmy.model;

import org.apache.ibatis.type.Alias;

@Alias("Device")
public class Device {
    int id;
    String imei;
    int type;//1:体验者;2:领路人;
    String number;
    int status=1;//1:正常;0:禁用;
    int companyId;
    int createTime;
    int updateTime;
    Company company;

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    State state;


    public int getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(int updateTime) {
        this.updateTime = updateTime;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public enum DeviceType {
        EXPERIENCER(1),
        LEADER(2),
        USER(0);

        public int type;

        DeviceType(int type) {
            this.type = type;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public int getCreateTime() {
        return createTime;
    }

    public void setCreateTime(int createTime) {
        this.createTime = createTime;
    }
}
