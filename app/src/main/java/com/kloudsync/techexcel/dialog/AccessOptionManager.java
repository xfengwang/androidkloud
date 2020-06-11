package com.kloudsync.techexcel.dialog;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.params.EventAccessOption;
import com.kloudsync.techexcel.tool.SocketMessageManager;

import org.greenrobot.eventbus.EventBus;


public class AccessOptionManager implements View.OnClickListener {

    private Context mContext;
    public static final int PUBLIC = 2;
    public static final int PROTECTED = 1;
    public static final int PRIVATE = 0;

    private AccessOptionManager(Context context) {
        this.mContext = context;
    }

    public static AccessOptionManager getManager(Context context) {
        return  new AccessOptionManager(context);
    }

    private LinearLayout publicll,protectll,privatell;
    private ImageView publicselect,protectselect,privateselect;
    private ImageView publicicon,protecticon,privateicon;
    private TextView publicname,protectname,privatename;
    private TextView publiccontent,protectcontent,privatecontent;

    public void initAccess(Activity activity, int aPrivate) {

        publicll=activity.findViewById(R.id.publicll);
        protectll=activity.findViewById(R.id.protectll);
        privatell=activity.findViewById(R.id.privatell);
        publicll.setOnClickListener(this);
        protectll.setOnClickListener(this);
        privatell.setOnClickListener(this);

        publicselect=activity.findViewById(R.id.publicselect);
        protectselect=activity.findViewById(R.id.protectselect);
        privateselect=activity.findViewById(R.id.privateselect);

        publicicon=activity.findViewById(R.id.publicicon);
        protecticon=activity.findViewById(R.id.protecticon);
        privateicon=activity.findViewById(R.id.privateicon);

        publicname=activity.findViewById(R.id.publicname);
        protectname=activity.findViewById(R.id.protectname);
        privatename=activity.findViewById(R.id.privatename);

        publiccontent=activity.findViewById(R.id.publiccontent);
        protectcontent=activity.findViewById(R.id.protectcontent);
        privatecontent=activity.findViewById(R.id.privatecontent);


        defaultView(aPrivate);

    }


    private void defaultView(int select){

        publicselect.setImageResource(R.drawable.accompany_unselect);
        protectselect.setImageResource(R.drawable.accompany_unselect);
        privateselect.setImageResource(R.drawable.accompany_unselect);

        publicname.setTextColor(mContext.getResources().getColor(R.color.black));
        protectname.setTextColor(mContext.getResources().getColor(R.color.black));
        privatename.setTextColor(mContext.getResources().getColor(R.color.black));

        publiccontent.setTextColor(mContext.getResources().getColor(R.color.defaultcolor));
        protectcontent.setTextColor(mContext.getResources().getColor(R.color.defaultcolor));
        privatecontent.setTextColor(mContext.getResources().getColor(R.color.defaultcolor));

        if(select==PUBLIC){
            publicselect.setImageResource(R.drawable.accompany_select);
            publicname.setTextColor(mContext.getResources().getColor(R.color.skyblue));
            publiccontent.setTextColor(mContext.getResources().getColor(R.color.skyblue));

        }else  if(select==PROTECTED){
            protectselect.setImageResource(R.drawable.accompany_select);
            protectname.setTextColor(mContext.getResources().getColor(R.color.skyblue));
            protectcontent.setTextColor(mContext.getResources().getColor(R.color.skyblue));

        }else  if(select==PRIVATE){
            privateselect.setImageResource(R.drawable.accompany_select);
            privatename.setTextColor(mContext.getResources().getColor(R.color.skyblue));
            privatecontent.setTextColor(mContext.getResources().getColor(R.color.skyblue));
        }
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.publicll:
                EventBus.getDefault().post(new EventAccessOption(PUBLIC,mContext.getResources().getString(R.string.access_public)));
                defaultView(PUBLIC);
                break;
            case R.id.protectll:
                EventBus.getDefault().post(new EventAccessOption(PROTECTED,mContext.getResources().getString(R.string.access_protect)));
                defaultView(PROTECTED);
                break;
            case R.id.privatell:
                EventBus.getDefault().post(new EventAccessOption(PRIVATE,mContext.getResources().getString(R.string.access_private)));
                defaultView(PRIVATE);
                break;
        }

    }


    public void sameAsTeam(int access) {
        initAccess((Activity) mContext,access);
        switch (access){
            case AccessOptionManager.PUBLIC:
                publicll.setVisibility(View.VISIBLE);
                protectll.setVisibility(View.GONE);
                privatell.setVisibility(View.GONE);
                break;
            case AccessOptionManager.PROTECTED:
                publicll.setVisibility(View.GONE);
                protectll.setVisibility(View.VISIBLE);
                privatell.setVisibility(View.GONE);
                break;
            case AccessOptionManager.PRIVATE:
                publicll.setVisibility(View.GONE);
                protectll.setVisibility(View.GONE);
                privatell.setVisibility(View.VISIBLE);
                break;
        }
    }


    public void unSameAsTeam(int access) {
        initAccess((Activity) mContext,access);
        publicll.setVisibility(View.VISIBLE);
        protectll.setVisibility(View.VISIBLE);
        privatell.setVisibility(View.VISIBLE);

    }
}
