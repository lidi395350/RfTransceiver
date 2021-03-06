package com.source.parse;

import android.os.Handler;
import android.util.Log;

import com.rftransceiver.util.Constants;
import com.source.DataPacketOptions;

/**
 * Created by Rth on 2015/5/6.
 */
public class TextParser {

    private DataPacketOptions options;

    private Handler handler;

    /**
     * a cache to save text bytes
     */
    private byte[] textTemp = new byte[60 * 1024];

    /**
     * the real text bytes's length
     */
    private int length = 0;

    public void parseText(byte[] data,DataPacketOptions.TextType type) {
        if(data[options.getRealLenIndex()] == options.getRealLen()) {
            makeText(data,options.getLength() - options.getOffset()-1);
        }else {
            //this is the last packet
            int memberId = data[Constants.Group_Member_Id_index];
            makeText(data,data[options.getRealLenIndex()]);
            byte[] sendData = new byte[length];
            System.arraycopy(textTemp,0,sendData,0,length);
            length = 0;
            int tag = -1;
            switch (type) {
                case Words:
                    tag = Constants.READ_WORDS;
                    break;
                case Address:
                    tag = Constants.READ_ADDRESS;
                    break;
                case Image:
                    tag = Constants.READ_Image;
                    break;
            }
            if(tag != -1) {
                handler.obtainMessage(Constants.MESSAGE_READ,
                        tag,memberId,sendData).sendToTarget();
            }
            sendData = null;
        }

    }

    private void makeText(byte[] data,int len) {
        if(len > options.getLength() - options.getOffset() -1) {
            length = 0;
            return;
        }
        for(int i = options.getOffset();i < len + options.getOffset();i++) {
            textTemp[length++] = data[i];
        }
    }

    public void setOptions(DataPacketOptions options) {
        this.options = null;
        this.options = options;
    }


    public void setHandler(Handler han) {
        this.handler = null;
        this.handler = han;
    }
}
