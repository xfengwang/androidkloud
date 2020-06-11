package com.kloudsync.techexcel.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.TvDevice;
import com.ub.techexcel.tools.TvDevicesOperatorPopup;

import java.util.ArrayList;
import java.util.List;

public class TvDeviceAdapterV2 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<TvDevice> mlist = new ArrayList<>();
    private Context context;
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public interface OnRecyclerViewItemClickListener {
        void openTransfer(TvDevice tvDevice);

        void closeTransfer(TvDevice tvDevice);

        void logout(TvDevice tvDevice);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }


    public TvDeviceAdapterV2(Context context, List<TvDevice> devices) {
        this.mlist = devices;
        this.context = context;
    }


    public void setDevices(List<TvDevice> devices) {
        mlist.clear();
        mlist.addAll(devices);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_item_v2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        final ViewHolder holder = (ViewHolder) viewHolder;
        final TvDevice device = mlist.get(position);
        holder.nameText.setText(device.getDeviceName());
        holder.timeText.setText(device.getLoginTime());
        holder.syncImage.setImageResource(device.isEnableSync() ? R.mipmap.on : R.mipmap.off);
        switch (device.getDeviceType()) {
            case 0:
                holder.img_device.setImageResource(R.drawable.icon_web);
                holder.type.setText("Web");
                break;
            case 1:
            case 2:
                holder.img_device.setImageResource(R.drawable.icon_phone);
                holder.type.setText("Phone");
                break;
            case 3:
                holder.img_device.setImageResource(R.drawable.icon_tv);
                holder.type.setText("TV");
                break;
            default:
                break;
        }


        holder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TvDevicesOperatorPopup yinxiangOperatorPopup = new TvDevicesOperatorPopup();
                yinxiangOperatorPopup.getPopwindow(context);
                yinxiangOperatorPopup.setFavoritePoPListener(new TvDevicesOperatorPopup.FavoritePoPListener() {
                    @Override
                    public void openTransfer() {
                        device.setOpenVoice(true);
                        holder.img_device.setImageResource(R.drawable.icon_sound_in);
                        mOnItemClickListener.openTransfer(device);
                    }

                    @Override
                    public void closeTransfer() {
                        device.setOpenVoice(false);
                        holder.img_device.setImageResource(R.drawable.icon_sound_disable);
                        mOnItemClickListener.closeTransfer(device);
                    }

                    @Override
                    public void logout() {
                        mOnItemClickListener.logout(device);
                    }
                });

            }
        });

    }


    @Override
    public int getItemCount() {
        return mlist.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView nameText;
        TextView timeText;
        ImageView img_device;
        LinearLayout ll;
        TextView type;
        ImageView syncImage;

        ViewHolder(View view) {
            super(view);
            nameText = (TextView) view.findViewById(R.id.txt_name);
            timeText = (TextView) view.findViewById(R.id.txt_time);
            img_device = (ImageView) view.findViewById(R.id.img_device);
            ll = (LinearLayout) view.findViewById(R.id.ll);
            type = (TextView) view.findViewById(R.id.txt_type);
            syncImage = (ImageView) view.findViewById(R.id.image_sync);
        }
    }
}
