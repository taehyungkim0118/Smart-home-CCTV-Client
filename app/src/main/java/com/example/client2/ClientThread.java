package com.example.client2;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.net.Socket;

public class ClientThread extends Thread {

    private String mServAddr;
    public static Handler mMainHandler;

    public ClientThread(String servAddr, Handler mainHandler) { //MainActivity 에서 주소와 핸들러를 받아오는 생성자
        mServAddr = servAddr;
        mMainHandler = mainHandler;
    }

    @Override
    public void run() {
        Socket sock = null;
        try {
            Log.d("socket","11addr : " + mServAddr);
            sock = new Socket(mServAddr, 9000);
            Log.d("socket","22addr : " + mServAddr);
            doPrintln("SMART HOME CCTV 연결 성공");
            Log.d("socket","33addr : " + mServAddr);
            RecvThread recvThread = new RecvThread(sock.getInputStream());
            SendThread sendThread = new SendThread(sock.getOutputStream());
            recvThread.start();
            sendThread.start();
            recvThread.join();
            sendThread.join();
            Log.d("socket","44addr : " + mServAddr);
        } catch (Exception e) {
            doPrintln(e.getMessage());
        } finally {
            try {
                if (sock != null) {
                    sock.close();
                    doPrintln(">> 서버와 연결 종료!");
                }
//                enableConnectButton();
            } catch (Exception e) {
                doPrintln(e.getMessage());
            }
        }
    }

    public static void doPrintln(String str) {  //메인 핸들러에 메시지 보내서 TextView 문자띄우는 메서드
        Message msg = Message.obtain();
        msg.what = MainActivity.CMD_APPEND_TEXT;
        msg.obj = str + "\n";
        mMainHandler.sendMessage(msg);
    }

    private static void enableConnectButton() {
        Message msg = Message.obtain();
        msg.what = MainActivity.CMD_ENABLE_CONNECT_BUTTON;
        mMainHandler.sendMessage(msg);
    }
}
