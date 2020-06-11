package com.kloudsync.techexcel.tool;


import android.content.SharedPreferences;

import com.kloudsync.techexcel.config.AppConfig;

import java.util.ArrayList;

public class ServerTool {

    public static final int VERSION_TEST = 1;
    public static final int VERSION_PRODUCT = 2;
    public static String[] specailUsers = {"15000712803","18018000002"};

    public static void setServerVersion(int version, SharedPreferences sp){
        sp.edit().putInt("server_type",version).commit();
        if(version == VERSION_TEST){
            AppConfig.URL_PUBLIC = "https://testapi.peertime.cn/peertime/V1/";
            AppConfig.URL_MEETING_BASE = "https://testwss.peertime.cn/MeetingServer/";
            AppConfig.COURSE_SOCKET = "wss://testwss.peertime.cn:8443/MeetingServer/websocket";
            AppConfig.liveToken = "01427aa4-396e-44b7-82ab-84d802099bb0";
            AppConfig.wssServer = "https://testwss.peertime.cn:8443/MeetingServer";
        }else if(version == VERSION_PRODUCT){
            AppConfig.URL_PUBLIC = "https://api.peertime.cn/peertime/V1/";
            AppConfig.URL_MEETING_BASE = "https://wss.peertime.cn/MeetingServer/";
            AppConfig.COURSE_SOCKET = "wss://wss.peertime.cn:8443/MeetingServer/websocket";
            AppConfig.liveToken = "02912174-3dcb-49eb-b9fa-6d90b390d495";
            AppConfig.wssServer = "https://wss.peertime.cn:8443/MeetingServer";
        }
    }

    public static void setServerVersion(int version){
        if(version == VERSION_TEST){
            AppConfig.URL_PUBLIC = "https://testapi.peertime.cn/peertime/V1/";
            AppConfig.URL_MEETING_BASE = "https://testwss.peertime.cn/MeetingServer/";
            AppConfig.COURSE_SOCKET = "wss://testwss.peertime.cn:8443/MeetingServer/websocket";
            AppConfig.liveToken = "01427aa4-396e-44b7-82ab-84d802099bb0";
            AppConfig.wssServer = "https://testwss.peertime.cn:8443/MeetingServer";
        }else if(version == VERSION_PRODUCT){
            AppConfig.URL_PUBLIC = "https://api.peertime.cn/peertime/V1/";
            AppConfig.URL_MEETING_BASE = "https://wss.peertime.cn/MeetingServer/";
            AppConfig.COURSE_SOCKET = "wss://wss.peertime.cn:8443/MeetingServer/websocket";
            AppConfig.liveToken = "02912174-3dcb-49eb-b9fa-6d90b390d495";
            AppConfig.wssServer = "https://wss.peertime.cn:8443/MeetingServer";
        }
    }
}
