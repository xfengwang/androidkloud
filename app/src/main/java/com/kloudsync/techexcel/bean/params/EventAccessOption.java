package com.kloudsync.techexcel.bean.params;

public class EventAccessOption {

    int access;
    String name;

    public int getAccess() {
        return access;
    }

    public void setAccess(int access) {
        this.access = access;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EventAccessOption(int access, String name) {
        this.access = access;
        this.name = name;
    }
}
