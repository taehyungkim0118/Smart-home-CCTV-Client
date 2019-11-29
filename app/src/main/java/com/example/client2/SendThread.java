package com.example.client2;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SendThread extends Thread {

    public static final int CMD_SEND_MESSAGE = 3;
    public static final int CMD_SEND_DATA = 5;

    public static final int HEADER_MESSAGE_2 = 0x33333333;

    private DataOutputStream mDataOutputStream;
    public static Handler mHandler;

    public SendThread(OutputStream os) {
        mDataOutputStream = new DataOutputStream(os);
    }

    @Override
    public void run() {
        Looper.prepare();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                try {
                    switch (msg.what) {
                        case CMD_SEND_MESSAGE: // 데이터 송신 메시지
                            try {
                                String s = (String) msg.obj;
                                mDataOutputStream.writeInt(HEADER_MESSAGE_2);
                                mDataOutputStream.writeInt(s.length());
                                mDataOutputStream.writeUTF(s);
                                //mDataOutputStream.write(s.getBytes()); 헤더와 길이 없이 전송한 경우 사용했음
                                mDataOutputStream.flush();

                                Date today = new Date();
                                SimpleDateFormat date = new SimpleDateFormat("yy/MM/dd");
                                SimpleDateFormat time = new SimpleDateFormat("hh:mm:ss a");
                                ClientThread.doPrintln("[보낸 메시지] " + s + "\n" + date.format(today) + " " + time.format(today));
                            } catch (IOException e) {
                                ClientThread.doPrintln(e.getMessage());
                            }
                            break;
                        case CMD_SEND_DATA:
                            String s = (String) msg.obj;
                            mDataOutputStream.writeInt(HEADER_MESSAGE_2);
                            mDataOutputStream.writeInt(s.length());
                            mDataOutputStream.writeUTF(s);
                            mDataOutputStream.flush();
                            break;
                    }
                } catch (Exception e) {
                    getLooper().quit();
                }
            }
        };
        Looper.loop();
    }
}
/*case CMD_SEND_MESSAGE: // 데이터 송신 메시지
                            try {
                                String s = (String) msg.obj;
                                mDataOutputStream.write(s.getBytes());

                                Message msg2 = Message.obtain();
                                msg2.what = MainActivity.CMD_SHOW_MESSAGE;
                                msg2.obj = "[보낸 데이터] " + s + "\n";
                                ClientThread.mMainHandler.sendMessage(msg2);
                            } catch (IOException e) {
                                ClientThread.doPrintln(e.getMessage());
                            }
                            break;*/
/*

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class SendThread extends Thread {

    public static final int CMD_SEND_MESSAGE = 2;

    private DataOutputStream mDataOutputStream;
    public static Handler mHandler;

    public SendThread(OutputStream os) {
        mDataOutputStream = new DataOutputStream(os);
    }

    @Override
    public void run() {
        Looper.prepare();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                try {
                    switch (msg.what) {
                        case CMD_SEND_MESSAGE: // 데이터 송신 메시지
                            try {
                                String s = (String) msg.obj;
                                mDataOutputStream.write(s.getBytes());
                                ClientThread.doPrintln("[보낸 데이터] " + s);
                            } catch (IOException e) {
                                ClientThread.doPrintln(e.getMessage());
                            }
                            break;
                    }
                } catch (Exception e) {
                    getLooper().quit();
                }
            }
        };
        Looper.loop();
    }
}*/



