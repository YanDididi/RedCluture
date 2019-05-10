package com.moyu.redarmy.model;

import org.apache.ibatis.type.Alias;

import java.util.List;

@Alias("Room")
public class Room {
    int id;
    String name;
    int leaderId;
    int companyId;
    int status = 0;
    int createTime;
    int updateTime;
    int resourceId;
    List<Device> experiencers;
    Device leader;
    Resource resource;


    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public Device getLeader() {
        return leader;
    }

    public void setLeader(Device leader) {
        this.leader = leader;
    }

    public List<Device> getExperiencers() {
        return experiencers;
    }

    public void setExperiencers(List<Device> experiencers) {
        this.experiencers = experiencers;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(int leaderId) {
        this.leaderId = leaderId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCreateTime() {
        return createTime;
    }

    public void setCreateTime(int createTime) {
        this.createTime = createTime;
    }

    public int getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(int updateTime) {
        this.updateTime = updateTime;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }
}
