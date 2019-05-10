package com.moyu.redarmy.model;

public class MyFile {

    private String name;
    private long length;
    private long modifiedTime;//修改时间
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public long getLength() {
        return length;
    }
    public void setLength(long length) {
        this.length = length;
    }
    public long getModifiedTime() {
        return modifiedTime;
    }
    public void setModifiedTime(long modifiedTime) {
        this.modifiedTime = modifiedTime;
    }
    public MyFile() {
        super();
        // TODO Auto-generated constructor stub
    }
    public MyFile(String name, long length, long modifiedTime) {
        super();
        this.name = name;
        this.length = length;
        this.modifiedTime = modifiedTime;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (length ^ (length >>> 32));
        result = prime * result + (int) (modifiedTime ^ (modifiedTime >>> 32));
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MyFile other = (MyFile) obj;
        if (length != other.length)
            return false;
        if (modifiedTime != other.modifiedTime)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
    @Override
    public String toString() {
        return "MyFile [name=" + name + ", length=" + length + ", modifiedTime=" + modifiedTime + "]";
    }


}
