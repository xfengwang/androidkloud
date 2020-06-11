package com.kloudsync.techexcel.bean;

/**
 * Created by tonyan on 2020/5/18.
 */

public class EventSelfJoinCoachAudience {
    boolean isAccept;

    public EventSelfJoinCoachAudience(boolean isAccept) {
        this.isAccept = isAccept;
    }

    public boolean isAccept() {
        return isAccept;
    }

    public void setAccept(boolean accept) {
        isAccept = accept;
    }
}
