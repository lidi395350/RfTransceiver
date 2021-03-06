package com.rftransceiver.group;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.rftransceiver.customviews.CircleImageDrawable;
import com.rftransceiver.util.GroupUtil;

/**
 * Created by rantianhua on 15-5-30.
 */
public class GroupMember implements Parcelable{


    private String name;    //the name of group member
    private String path;    //the path of picture
    private int id; //the member's id in the group
    private Drawable drawable;  //the member's photo
    private Bitmap bitmap;
    private boolean addSucceed = false; //标识加组是否成功

    public GroupMember() {

    }

    public GroupMember(String name,int id) {
        this.id = id;
        this.name = name;
    }

    public GroupMember(String name,int id,String path) {
        this(name,id);
        this.path = path;
    }

    public GroupMember(String name,int id,Bitmap bitmap) {
        this(name, id);
        if(bitmap != null) {
            setBitmap(bitmap);
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public boolean isAddSucceed() {
        return addSucceed;
    }

    public void setAddSucceed(boolean addSucceed) {
        this.addSucceed = addSucceed;
    }

    public void setDrawable(Bitmap bitmap) {
        if(bitmap == null) return;
        this.drawable = new CircleImageDrawable(bitmap);
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        setDrawable(bitmap);
    }

    public static final Creator<GroupMember> CREATOR = new Creator<GroupMember>() {
        @Override
        public GroupMember createFromParcel(Parcel parcel) {
            GroupMember member = new GroupMember();
            member.setId(parcel.readInt());
            member.setName(parcel.readString());
            member.setPath(parcel.readString());
            Bundle bundle = parcel.readBundle();
            Bitmap bp = null;
            try{
                bp = bundle.getParcelable(BITMAP);
            }catch (Exception e) {
                e.printStackTrace();
            }
            if(bp != null) {
                member.setBitmap(bp);
            }
            return member;
        }

        @Override
        public GroupMember[] newArray(int i) {
            return new GroupMember[i];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeString(path);
        Bundle bundle = new Bundle();
        if(bitmap != null) {
            bundle.putParcelable(BITMAP,bitmap);
        }
        parcel.writeBundle(bundle);
    }

    public static final String BITMAP = "bitmap";

}
