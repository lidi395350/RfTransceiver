package com.rftransceiver.adapter;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rftransceiver.activity.MainActivity;
import com.rftransceiver.customviews.ListItemMapView;
import com.rftransceiver.customviews.SoundsTextView;
import com.rftransceiver.datasets.ConversationData;
import com.rftransceiver.R;
import com.rftransceiver.fragments.MapViewFragment;

import java.net.InterfaceAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.TimeoutException;

/**
 * Created by Rth on 2015/4/27.
 */
public class ListConversationAdapter extends BaseAdapter{

    private List<ConversationData> listData = new ArrayList<>();
    private LayoutInflater inflater = null;
    private FragmentManager fm;
    private static Handler mainHan;
    private OnStateClickListener stateClickListener;
    /**
     * parse expression data from content
     */
    private Html.ImageGetter imageGetter;

    public ListConversationAdapter(Context context,Html.ImageGetter imageGetter,FragmentManager fm) {
        inflater = LayoutInflater.from(context);
        this.imageGetter = imageGetter;
        this.fm = fm;
        mainHan = new Handler(Looper.getMainLooper());
    }

    @Override
    public int getViewTypeCount() {
        return 9;
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHodler hodler = null;
        final ConversationData data = listData.get(position);

        if(convertView == null) {
            hodler = new ViewHodler();
            switch (data.getConversationType()) {
                case LEFT_TEXT:
                    convertView = inflater.inflate(R.layout.list_left_text,null);
                    hodler.tvContent = (TextView) convertView.findViewById(R.id.tv_list_left);
                    hodler.imgPhoto = (ImageView) convertView.findViewById(R.id.img_conversation_photo);
                    break;
                case LEFT_PIC:
                    convertView = inflater.inflate(R.layout.list_left_pic,null);
                    hodler.imgData = (ImageView) convertView.findViewById(R.id.img_data_left);
                    hodler.imgPhoto = (ImageView) convertView.findViewById(R.id.img_conversation_photo);
                    break;
                case LEFT_ADDRESS:
                    convertView = inflater.inflate(R.layout.list_left_address,null);
                    hodler.listItemMapView = (ListItemMapView) convertView.findViewById(R.id.listmapview_left);
                    hodler.imgPhoto = (ImageView) convertView.findViewById(R.id.img_conversation_photo);
                    break;
                case LEFT_SOUNDS:
                    convertView = inflater.inflate(R.layout.list_left_sounds,null);
                    hodler.tvSounds = (SoundsTextView) convertView.findViewById(R.id.tv_left_sounds);
                    hodler.imgPhoto = (ImageView) convertView.findViewById(R.id.img_conversation_photo);
                    hodler.soundsTime=(TextView) convertView.findViewById(R.id.tv_sound_time_left);
                    break;
                case RIGHT_TEXT:
                    convertView = inflater.inflate(R.layout.list_right_text,null);
                    hodler.tvContent = (TextView)convertView.findViewById(R.id.tv_list_right);
                    hodler.imgTextStates = (ImageView) convertView.findViewById(R.id.img_text_states_right);
                    hodler.imgTextStatesFail = (ImageView) convertView.findViewById(R.id.img_text_states_fail);
                    break;
                case RIGHT_PIC:
                    convertView = inflater.inflate(R.layout.list_right_pic,null);
                    hodler.imgData = (ImageView) convertView.findViewById(R.id.img_data_right);
                    hodler.tvImgProgress = (TextView) convertView.findViewById(R.id.tv_img_progress);
                    break;
                case RIGHT_ADDRESS:
                    convertView = inflater.inflate(R.layout.list_right_address,null);
                    hodler.listItemMapView = (ListItemMapView) convertView.findViewById(R.id.listmapview_right);
                    hodler.imgAddressStates = (ImageView) convertView.findViewById(R.id.img_address_states_right);
                    hodler.imgAddressStatesFail = (ImageView) convertView.findViewById(R.id.img_address_states_fail);
                    break;
                case RIGHT_SOUNDS:
                    convertView = inflater.inflate(R.layout.list_right_sounds,null);
                    hodler.tvSounds = (SoundsTextView) convertView.findViewById(R.id.tv_right_sounds);
                    hodler.soundsTime=(TextView) convertView.findViewById(R.id.tv_sound_time_right);
                    break;
                case TIME:
                    convertView = inflater.inflate(R.layout.list_item_time,null);
                    hodler.tvTime = (TextView) convertView.findViewById(R.id.tv_chat_time);
                    break;
            }
            convertView.setTag(hodler);
        }else {
            hodler = (ViewHodler) convertView.getTag();
        }

        if(data.getConversationType().ordinal() <= 3) {
            if(data.getPhotoDrawable() != null) {
                hodler.imgPhoto.setImageDrawable(data.getPhotoDrawable());
            }
        }
        switch (data.getConversationType()) {
            case RIGHT_TEXT:
                Spanned spannable1 = Html.fromHtml(data.getContent(),imageGetter,null);
                hodler.tvContent.setText(spannable1);
                AnimationDrawable anim = (AnimationDrawable) hodler.imgTextStates.getBackground();

                if(!data.isFinished()){

                    hodler.imgTextStates.setVisibility(View.VISIBLE);
                    anim.start();
                    if(data.isFail()){
                        hodler.imgTextStates.setVisibility(View.INVISIBLE);
                        anim.stop();
                        hodler.imgTextStatesFail.setVisibility(View.VISIBLE);

                        hodler.imgTextStatesFail.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (data.isFail()) {
                                    stateClickListener.onclick(MainActivity.SendAction.Words,data.getContent());
                                }
                            }
                        });
                    }else{
                        hodler.imgTextStatesFail.setVisibility(View.INVISIBLE);
                    }
                }else {
                    hodler.imgTextStates.setVisibility(View.INVISIBLE);
                    anim.stop();
                }
                break;
            case LEFT_TEXT:
                Spanned spannable2 = Html.fromHtml(data.getContent(),imageGetter,null);
                hodler.tvContent.setText(spannable2);
                break;
            case LEFT_PIC:
                if(data.getBitmap() != null) {
                    hodler.imgData.setImageBitmap(data.getBitmap());
                }
                break;
            case RIGHT_PIC:
                if(data.getBitmap() != null) {
                    hodler.imgData.setImageBitmap(data.getBitmap());
                }
                if(data.getPercent() > 0 && data.getPercent() < 100) {
                    hodler.tvImgProgress.setVisibility(View.VISIBLE);
                    hodler.tvImgProgress.setText(data.getPercent() + "%");
                }else {
                    hodler.tvImgProgress.setVisibility(View.INVISIBLE);
                }
                break;
            case LEFT_ADDRESS:
                hodler.listItemMapView.setAddress(data.getAddress());
                break;
            case RIGHT_ADDRESS:
                hodler.listItemMapView.setAddress(data.getAddress());
                AnimationDrawable anim2 = (AnimationDrawable) hodler.imgAddressStates.getBackground();
                if(!data.isFinished()){
                    hodler.imgAddressStates.setVisibility(View.VISIBLE);
                    anim2.start();
                    if(data.isFail()){
                        hodler.imgAddressStates.setVisibility(View.INVISIBLE);
                        anim2.stop();
                        hodler.imgAddressStates.setVisibility(View.VISIBLE);

                        hodler.imgAddressStatesFail.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (data.isFail()) {
                                    stateClickListener.onclick(MainActivity.SendAction.Address, data.getAddress());
                                }
                            }
                        });
                    }else{
                        hodler.imgAddressStatesFail.setVisibility(View.INVISIBLE);
                    }
                }else {
                    hodler.imgAddressStates.setVisibility(View.INVISIBLE);
                    anim2.stop();
                }
                break;
            case LEFT_SOUNDS:
            case RIGHT_SOUNDS:
                hodler.tvSounds.setTag(data.getContent());
                hodler.tvSounds.setSoundsTime(data.getSoundsTime());
                hodler.soundsTime.setText(data.getSoundsTime() / 1000 + "." + (data.getSoundsTime() / 100) % 10 + "s");
                break;
            case TIME:
                hodler.tvTime.setText(data.getContent());
                break;
        }

        return convertView;
    }
    /**
     * 更新数据源
     * @param dataLists
     */
    public void updateData(List<ConversationData> dataLists) {
        this.listData.clear();
        if(dataLists.size() > 0) {
            this.listData.addAll(dataLists);
        }
        notifyDataSetChanged();
    }

    public interface OnStateClickListener{
        void onclick(MainActivity.SendAction action, String content);
    }

    public void setOnstateClickListener(OnStateClickListener listener){
        this.stateClickListener=listener;
    }

    class ViewHodler {
        TextView tvContent,tvImgProgress,tvTime,soundsTime;
        SoundsTextView tvSounds;
        ImageView imgPhoto,imgData,imgTextStates,imgAddressStates,imgTextStatesFail,imgAddressStatesFail;
        ListItemMapView listItemMapView;
    }

    public enum ConversationType{
        LEFT_TEXT,
        LEFT_PIC,
        LEFT_ADDRESS,
        LEFT_SOUNDS,
        RIGHT_TEXT,
        RIGHT_PIC,
        RIGHT_ADDRESS,
        RIGHT_SOUNDS,
        TIME
    }

    @Override
    public int getItemViewType(int position) {
        switch (listData.get(position).getConversationType()) {
            case LEFT_TEXT:
                return 0;
            case LEFT_PIC:
                return 1;
            case LEFT_ADDRESS:
                return 2;
            case LEFT_SOUNDS:
                return 3;
            case RIGHT_TEXT:
                return 4;
            case RIGHT_PIC:
                return 5;
            case RIGHT_ADDRESS:
                return 6;
            case RIGHT_SOUNDS:
                return 7;
            case TIME:
                return 8;
            default:
                return 0;
        }
    }
}
