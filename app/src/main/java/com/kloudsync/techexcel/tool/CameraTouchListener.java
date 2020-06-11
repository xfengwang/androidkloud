package com.kloudsync.techexcel.tool;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by tonyan on 2020/4/12.
 */

public class CameraTouchListener implements View.OnTouchListener {

    private int startY;
    private int startX;
    private int endX, endY;
    private RelativeLayout cameraLayout;
    private FollowSpearkerTouchListener followSpearkerTouchListener;
    int screenWidth, screenHeight;


    public void setFollowSpearkerTouchListener(FollowSpearkerTouchListener followSpearkerTouchListener) {
        this.followSpearkerTouchListener = followSpearkerTouchListener;
    }

    public void setCameraLayout(RelativeLayout cameraLayout,Context context) {
        this.cameraLayout = cameraLayout;
        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        screenHeight = context.getResources().getDisplayMetrics().heightPixels;
    }

    boolean isMove;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.e("CameraTouchListener","onTouch:" + event.getRawX());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = (int) event.getRawX();
                startY = (int) event.getRawY();
                endX = startX;
                endY = startY;
                break;
            case MotionEvent.ACTION_MOVE:
                int moveX = (int) event.getRawX();
                int moveY = (int) event.getRawY();
                int move_bigX = moveX - startX;
                int move_bigY = moveY - startY;

                if (Math.abs(move_bigX) > 10 || Math.abs(move_bigY) > 10) {
                    //拿到当前控件未移动的坐标
                    int left = cameraLayout.getLeft();
                    int top = cameraLayout.getTop();
                    left += move_bigX;
                    top += move_bigY;
                    int right = left + cameraLayout.getWidth();
                    int bottom = top + cameraLayout.getHeight();
                    Log.e("check_move","left:" + left + ",top:" + top + ",right:" + right + ",bottom:" + bottom);

                    if (left > 0 && top > 0 && right < screenWidth && bottom < screenHeight) {
                        cameraLayout.layout(left, top, right, bottom);
                        isMove = true;
                    }
                }
                startX = moveX;
                startY = moveY;

                break;
            case MotionEvent.ACTION_UP:
                if(isMove){
                    isMove = false;
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.leftMargin = cameraLayout.getLeft();
                    params.topMargin = cameraLayout.getTop();
                    params.setMargins(cameraLayout.getLeft(), cameraLayout.getTop(), 0, 0);
                    cameraLayout.setLayoutParams(params);
                    if(followSpearkerTouchListener != null){
                        followSpearkerTouchListener.layoutSpeaker(cameraLayout.getLeft(),cameraLayout.getTop());
                    }
                    return true;
                }else {
                    return false;
                }

        }

        return false;
    }

    public void layoutCamera(int left ,int top){
        if(cameraLayout != null){
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.leftMargin = cameraLayout.getLeft();
            params.topMargin = cameraLayout.getTop();
            params.setMargins(left, top, 0, 0);
            cameraLayout.setLayoutParams(params);
        }

    }
}
