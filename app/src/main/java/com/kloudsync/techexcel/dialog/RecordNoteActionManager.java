package com.kloudsync.techexcel.dialog;

import android.content.Context;
import android.util.Log;

import com.kloudsync.techexcel.bean.MeetingConfig;
import com.ub.techexcel.tools.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecordNoteActionManager {

    static volatile RecordNoteActionManager instance;
    private Context mContext;
    private SoundtrackRecordManager soundtrackRecordManager;
    public RecordNoteActionManager(Context context) {
        this.mContext = context;
        soundtrackRecordManager=SoundtrackRecordManager.getManager(mContext);
    }
    public static RecordNoteActionManager getManager(Context context) {
        if (instance == null) {
            synchronized (RecordNoteActionManager.class) {
                if (instance == null) {
                    instance = new RecordNoteActionManager(context);
                }
            }
        }
        return instance;
    }

    /**
     * 5.画线(前端)
     * {actionType:304,time:1,page:1,data:{duration:11,strokeId:"3243-2342-24-4"}}
     * @param noteId  笔记id
     * @param noteData 笔记单条数据  noteData
     */
    public void sendDrawActions(long noteId, String noteData) {
//        {actionType:304,time:1,page:1,data:{duration:11,strokeId:"3243-2342-24-4"}}
//        time: start time
//        duration:笔画的持续时长
//        strokeId: 线的唯一ID
        String strokeId;
        String linealldata= Tools.getFromBase64(noteData);
        try {
            JSONObject jsonObject=new JSONObject(linealldata);
            JSONArray linesjson=jsonObject.getJSONArray("lines");
            JSONObject submit=new JSONObject();
            for(int i=0;i<linesjson.length();i++){
                JSONObject linedata=linesjson.getJSONObject(i);
                strokeId=linedata.getString("id");
                int duration=0;
                JSONArray points=linedata.getJSONArray("points");
                if(points.length()>0){
                    JSONArray startpoint=points.getJSONArray(0);
                    double starttime=0;
                    if(startpoint.length()>0){
                         starttime=startpoint.getDouble(startpoint.length()-1);
                    }
                    JSONArray endpoint=points.getJSONArray(points.length()-1);
                    double endtime=0;
                    if(endpoint.length()>0){
                         endtime=endpoint.getDouble(endpoint.length()-1);
                    }
                     duration= (int) ((endtime-starttime)*1000);
                }
                submit.put("strokeId",strokeId);
                submit.put("duration",duration);
                break;
            }
            soundtrackRecordManager.recordNoteAction(NoteRecordType.DRAW_LINE,submit);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 1. 显示笔记到浮窗: (前端)
     * {actionType:300,time:1,page:1,data:{id:123,lastStrokeId:"1-2-3"}}
     * @param noteId
     * @param paintData
     */
    public  void sendDisplayPopupActions(long noteId, JSONObject paintData){
        //  {
        //	"PageTokenBackup": "002ff42a-6f76-4d67-bfd5-c216116b9b57",
        //	"PageToken": "3a986f8f-0d31-4241-8ac0-a85a2e75ebc6",
        //	"PaintData": {
        //		"address": "45.0.0.2",
        //		"lines": [{
        //					"id": "e4a3276e-f871-4736-a608-ea39a1a7c4ae",
        //					"pen": "1CA127B9-0E81-A668-D172-72F4AC8815B8",
        //					"user": 225014321,
        //					"points": [
        //						[758, 5735, 740, 1578967168],
//      {actionType:308,time:1,page:1,data:{id:123,lastStrokeId:"1-2-3"}}
        String strokeId="";
        try {
            JSONArray linesjson=paintData.getJSONArray("lines");
            if(linesjson.length()>0){
                JSONObject linedata=linesjson.getJSONObject(linesjson.length()-1);
                strokeId=linedata.getString("id");  //拿最后一条动作的id
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject jsonObject= null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("id",noteId);
            jsonObject.put("lastStrokeId",strokeId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        soundtrackRecordManager.recordNoteAction(NoteRecordType.DISPLAY_POPUP,jsonObject);
    }

    /**
     *
     * 9. 直接显示笔记到主界面: (前端,meeting:server)
     * {actionType:308,time:1,page:1,data:{id:123,lastStrokeId:"1-2-3"}}
     * @param noteId
     * @param paintData
     */
    public  void sendDisplayHomePageActions(long noteId, JSONObject paintData){
        //  {
        //	"PageTokenBackup": "002ff42a-6f76-4d67-bfd5-c216116b9b57",
        //	"PageToken": "3a986f8f-0d31-4241-8ac0-a85a2e75ebc6",
        //	"PaintData": {
        //		"address": "45.0.0.2",
        //		"lines": [{
        //					"id": "e4a3276e-f871-4736-a608-ea39a1a7c4ae",
        //					"pen": "1CA127B9-0E81-A668-D172-72F4AC8815B8",
        //					"user": 225014321,
        //					"points": [
        //						[758, 5735, 740, 1578967168],
//      {actionType:308,time:1,page:1,data:{id:123,lastStrokeId:"1-2-3"}}
        String strokeId="";
        try {
            JSONArray linesjson=paintData.getJSONArray("lines");
            if(linesjson.length()>0){
                JSONObject linedata=linesjson.getJSONObject(linesjson.length()-1);
                strokeId=linedata.getString("id");  //拿最后一条动作的id
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject jsonObject= null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("id",noteId);
            jsonObject.put("lastStrokeId",strokeId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        soundtrackRecordManager.recordNoteAction(NoteRecordType.DISPALY_HOMEPAGE,jsonObject);
    }


    /**
     * 2.笔记从浮窗显示到主界面(前端)
     * {actionType:301,time:1,page:1,data:{id:343,lastStrokeId:"1-2-3"}}
     */
    public  void sendDisplayPopupHomepageActions(long noteId,JSONObject paintData){
        soundtrackRecordManager.recordNoteAction(NoteRecordType.DISPLAY_POPUP_HOMEPAGE,getHomepagePopupLineData(noteId,paintData));
    }

    /**
     * 8.笔记从主界面显示到浮窗(前端)
     * {actionType:307,time:1,page:1,data:{id:343,lastStrokeId:"1-2-3"}}
     */
    public void sendDisplayHomepagePopupActions(long noteId,JSONObject paintData){
        soundtrackRecordManager.recordNoteAction(NoteRecordType.DISPLAY_HOMEPAGE_POPUP,getHomepagePopupLineData(noteId,paintData));
    }

    /**
     * 浮窗与主界面切换要录制的数据
     * @param noteId
     * @param paintData
     * @return
     */
    private JSONObject getHomepagePopupLineData(long noteId,JSONObject paintData){
        String strokeId="";
        try {
            JSONArray linesjson=paintData.getJSONArray("lines");
            if(linesjson.length()>0){
                JSONObject linedata=linesjson.getJSONObject(linesjson.length()-1);
                strokeId=linedata.getString("id");  //拿最后一条动作的id
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject jsonObject= null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("id",noteId);
            jsonObject.put("lastStrokeId",strokeId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }


    /**
     * 6.关闭浮窗笔记(前端)
     * {actionType:305,time:1,page:1,data:{id:123}}
     */
    public void sendClosePopupActons(long noteId){
        try {
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("id",noteId);
            soundtrackRecordManager.recordNoteAction(NoteRecordType.CLOSE_POPUP_NOTE,jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * 7.关闭主界面笔记(前端)
     * {actionType:306,time:1,page:1,data:{id:123,docId:345,pageNo:1}}
     */
    public void sendCloseHomePageActon(long noteId, boolean isMeetingRecord, MeetingConfig meetingConfig){
        // {actionType:306,time:1,page:1,data:{id:123,docId:345,pageNo:1}}
        // id: noteid
        // docId:关闭笔记后显示的文档id(会议录制增加)
        // pageNo:显示的文档页码(会议录制增加)
        try {
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("id",noteId);
            if(isMeetingRecord){
                jsonObject.put("docId",meetingConfig.getDocument().getItemID());
                jsonObject.put("pageNo",meetingConfig.getPageNumber());
            }
            soundtrackRecordManager.recordNoteAction(NoteRecordType.CLOSE_HOMEPAGE_NOTE,jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 3.笔记换页:(前端;meeting:1.笔在新一页写.2.手动换一个笔记)
     * {actionType:302,time:1,page:1,data:{newId:123,oldId:345,lastStrokeId:"1-2-3"}}}
     * newId: 切换到的笔记Id
     * oldId:切换之前的笔记Id
     */
    public void sendChangePageActions(int noteId, int oldNoteId, JSONObject lastjsonObject) {
        try {
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("newId",noteId);
            jsonObject.put("oldId",oldNoteId);
            jsonObject.put("lastStrokeId",lastjsonObject);
            soundtrackRecordManager.recordNoteAction(NoteRecordType.CLOSE_HOMEPAGE_NOTE,jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
