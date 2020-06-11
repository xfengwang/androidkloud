package com.kloudsync.techexcel.bean;

import java.io.Serializable;

/**
 * Created by tonyan on 2020/1/15.
 */

public class EverPen implements Serializable {
    private String macAddress;
    private String name;
    private boolean isConnected;
	private boolean isClick;
    private String penName;
    private String penType;
    private String similaPenSource;

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

	public boolean isClick() {
		return isClick;
	}

	public void setClick(boolean click) {
		isClick = click;
	}

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPenName() {
        penName = macAddress + name;
        return penName;
    }

    public void setPenName(String penName) {
        this.penName = penName;
    }

    public String getPenType() {
        return penType;
    }

    public void setPenType(String penType) {
        this.penType = penType;
    }

    public String getSimilaPenSource() {
        return similaPenSource;
    }

    public void setSimilaPenSource(String similaPenSource) {
        this.similaPenSource = similaPenSource;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EverPen everPen = (EverPen) o;

        return macAddress != null ? macAddress.equals(everPen.macAddress) : everPen.macAddress == null;
    }

    @Override
    public int hashCode() {
        return macAddress != null ? macAddress.hashCode() : 0;
    }

	public EverPen(String macAddress) {
        this.macAddress = macAddress;
    }

	public EverPen() {

    }
}
