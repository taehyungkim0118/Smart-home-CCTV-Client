package com.example.client2;


import android.graphics.BitmapFactory;
import android.os.Message;

import java.io.DataInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RecvThread extends Thread {

    public static final int HEADER_BITMAP = 0x11111111;

    public static final int HEADER_MESSAGE = 0x22222222;

    private DataInputStream mDataInputStream;

    public RecvThread(InputStream is) {
        mDataInputStream = new DataInputStream(is);
    }

    @Override
    public void run() {
        int header, length;
        byte[] byteArray;

        try {
            while (true) {
                // (1) 헤더를 읽는다.
                header = mDataInputStream.readInt();
                // (2) 데이터의 길이를 읽는다.
                length = mDataInputStream.readInt();
                // (3) 헤더의 타입에 따라 다르게 처리한다.

                switch (header) {
                    case HEADER_BITMAP:
                        byteArray = new byte[length];
                        mDataInputStream.readFully(byteArray);

                        Message msg1 = Message.obtain();
                        msg1.what = MainActivity.CMD_SHOW_BITMAP;
                        msg1.obj = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                        ClientThread.mMainHandler.sendMessage(msg1);
                        break;
                    case HEADER_MESSAGE:  //writeUTF로 보내고 readUTF로 받는다
                        String message = mDataInputStream.readUTF();

                       /* //Log.d("[TaeHyeong]", message);
                        Message msg2 = Message.obtain();
                        msg2.what = MainActivity.CMD_SHOW_MESSAGE;
                        msg2.obj = message;
                        ClientThread.mMainHandler.sendMessage(msg2);*/

                        Date today = new Date();
                        SimpleDateFormat date = new SimpleDateFormat("yy/MM/dd");
                        SimpleDateFormat time = new SimpleDateFormat("hh:mm:ss a");
                        ClientThread.doPrintln("[받은 메시지]   " + message + "\n" + date.format(today) + " " + time.format(today));

//                        if (message.equals("detect")) {
//                            Message msg = Message.obtain();
//                            msg.what = MainActivity.CMD_SET_TEXT;
//                            msg.obj = "detect";
//                            ClientThread.mMainHandler.sendMessage(msg);
//                        }

                        if (message.equals("고양이 감지") ) {  // text 업데이트
                            Message msg = Message.obtain();
                            msg.what = MainActivity.CMD_SET_VISION;
                            msg.obj = "고양이 감지";
                            ClientThread.mMainHandler.sendMessage(msg);
                        }

                        if (message.equals("강아지 감지") ) {  // text 업데이트
                            Message msg = Message.obtain();
                            msg.what = MainActivity.CMD_SET_VISION;
                            msg.obj = "강아지 감지";
                            ClientThread.mMainHandler.sendMessage(msg);
                        }

                        if (message.equals("사람 감지") ) {  // text 업데이트
                            Message msg = Message.obtain();
                            msg.what = MainActivity.CMD_SET_VISION;
                            msg.obj = "사람 감지";
                            ClientThread.mMainHandler.sendMessage(msg);
                        }

                        if (message.equals("사람 감지") ) {  //PUSH 알림
                            Message msg = Message.obtain();
                            msg.what = MainActivity.CMD_SHOW_MESSAGE;
                            msg.obj = "사람의 움직임이 감지되었습니다";
                            ClientThread.mMainHandler.sendMessage(msg);
                        }

                        if (message.equals("Face") ) {  //PUSH 알림
                            Message msg = Message.obtain();
                            msg.what = MainActivity.CMD_SHOW_MESSAGE;
                            msg.obj = "얼굴이 검출되었습니다";
                            ClientThread.mMainHandler.sendMessage(msg);
                        }



                        break;
                        /*byte[] buf = new byte[1024]; //버퍼생성하여 메시지 주고받는 코드
                        try {
                            int nbytes = mDataInputStream.read(buf);
                            if (nbytes > 0) {
                                String s = new String(buf, 0, nbytes);
                                ClientThread.doPrintln("[ Server ] :   " + s);
                            } else {
                                ClientThread.doPrintln(">> 서버가 연결 끊음!");
                                *//*if (SendThread.mHandler != null) {
                                    Message msg = Message.obtain();
                                    msg.what = 2;
                                    SendThread.mHandler.sendMessage(msg);
                                }*//*
                                break;
                            }
                        } catch (IOException e) {
                            ClientThread.doPrintln(e.getMessage());
                        }*/
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
