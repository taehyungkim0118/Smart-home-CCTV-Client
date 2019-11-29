package com.example.client2;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements RecognitionListener {

    private SpeechRecognizer speech;

    private Intent recognizerIntent;
    private final int RESULT_SPEECH = 1000;

    public static final int CMD_APPEND_TEXT = 0;
    public static final int CMD_ENABLE_CONNECT_BUTTON = 1;
    public static final int CMD_SHOW_BITMAP = 2;
    public static final int CMD_SHOW_MESSAGE = 3;
//    public static final int CMD_SET_TEXT = 4;
    public static final int CMD_SET_VISION = 5;

    private TextToSpeech textToSpeech; //

    private ImageView mImageFrame;
//    private TextView mTextStatus;

//    private TextView mMotionStatus;
    private TextView mVisionResult;
    private ScrollView mScrollView;
    //    private EditText mEditIP;
    private EditText mEditData;
    private Button mBtnSend;
    //    private Button mBtnConnect;
    private ClientThread mClientThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,     //상태바 없애는 코드
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        /*getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);*/
        setContentView(R.layout.activity_main);
        Intent recIntent = getIntent();
        String addr = recIntent.getStringExtra("stringAddr");

        Log.d("onCreate", "addr : " + addr);

        mClientThread = new ClientThread(addr, mMainHandler);
        mClientThread.start();
        Log.d("oncreate", "스레드 시작");

        mImageFrame = (ImageView) findViewById(R.id.imageFrame);
        mVisionResult = (TextView) findViewById(R.id.visionResult);


        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() { //TTS
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    //사용할 언어를 설정
                    int result = textToSpeech.setLanguage(Locale.KOREA);
                    //언어 데이터가 없거나 혹은 언어가 지원하지 않으면...
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(MainActivity.this, "이 언어는 지원하지 않습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        //음성 톤
                        textToSpeech.setPitch(0.7f);
                        //읽는 속도
                        textToSpeech.setSpeechRate(1.2f);
                    }
                }
            }
        });


        speech = SpeechRecognizer.createSpeechRecognizer(this);
        speech.setRecognitionListener(this);

        findViewById(R.id.btn_stt).setOnClickListener(new View.OnClickListener() { //STT
            @Override
            public void onClick(View view) {
                recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "ko-KR"); //언어지정입니다.
                recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
                recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
                recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);   //검색을 말한 결과를 보여주는 갯수
                startActivityForResult(recognizerIntent, RESULT_SPEECH);

            }
        });

        Log.d("oncreate", "끝");
    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {

    }

    @Override
    public void onResults(Bundle results) {
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        for (int i = 0; i < matches.size(); i++) {
            Log.e("GoogleActivity", "onResults text : " + matches.get(i));
        }

    }

    @Override
    public void onError(int errorCode) {

        String message;

        switch (errorCode) {

            case SpeechRecognizer.ERROR_AUDIO:
                message = "오디오 에러";
                break;

            case SpeechRecognizer.ERROR_CLIENT:
                message = "클라이언트 에러";
                break;

            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "퍼미션없음";
                break;

            case SpeechRecognizer.ERROR_NETWORK:
                message = "네트워크 에러";
                break;

            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "네트웍 타임아웃";
                break;

            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "찾을수 없음";
                ;
                break;

            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "바쁘대";
                break;

            case SpeechRecognizer.ERROR_SERVER:
                message = "서버이상";
                ;
                break;

            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "말하는 시간초과";
                break;

            default:
                message = "알수없음";
                break;
        }

        Log.e("GoogleActivity", "SPEECH ERROR : " + message);

    }

    @Override
    public void onRmsChanged(float v) {

    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onEvent(int i, Bundle bundle) {

    }

    @Override
    public void onPartialResults(Bundle bundle) {

    }

    @Override
    public void onBufferReceived(byte[] bytes) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  //STT결과값 받아오기
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_SPEECH: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> text = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    for (int i = 0; i < text.size(); i++) {
                        Log.e("GoogleActivity", "onActivityResult text : " + text.get(i));

                        if (SendThread.mHandler != null) {
                            Message msg = Message.obtain();
                            msg.what = SendThread.CMD_SEND_MESSAGE;
                            msg.obj = text.get(i);
                            SendThread.mHandler.sendMessage(msg);
                            Log.d("GoogleActivity", text.get(i));
                        }
                    }
                }
                break;
            }
        }
    }  //Speech-to-text 끝


    @Override
    protected void onDestroy() {
        super.onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public void mOnClick(View v) {
        //NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        switch (v.getId()) {
            case R.id.autoDetection:
                Log.d("autoDetection", "autoDetection111");
                ToggleButton tb2 = (ToggleButton) findViewById(R.id.autoDetection);
                if (SendThread.mHandler != null) { //SendThread 에 mHandler 존재한다면 실행
                    if (tb2.isChecked()) {
                        Message msg = Message.obtain();
                        msg.what = SendThread.CMD_SEND_DATA;
                        msg.obj = "autoOn";
                        SendThread.mHandler.sendMessage(msg);  //작성한 메시지를 SendThread로 핸들러를 이용해 보냄
                    } else {
                        Message msg = Message.obtain();
                        msg.what = SendThread.CMD_SEND_DATA;
                        msg.obj = "autoOff";
                        SendThread.mHandler.sendMessage(msg);  //작성한 메시지를 SendThread로 핸들러를 이용해 보냄
                    }
                }
                Log.d("autoDetection", "autoDetection22");
                break;
            case R.id.btnQuit:
                finish();
                break;
            case R.id.btnUp:
                Log.d("TaeHyeong", "Debug1");
                if (SendThread.mHandler != null) { //SendThread 에 mHandler 존재한다면 실행
                    Message msg = Message.obtain();
                    msg.what = SendThread.CMD_SEND_MESSAGE;
                    msg.obj = "UP";
                    SendThread.mHandler.sendMessage(msg);  //작성한 메시지를 SendThread로 핸들러를 이용해 보냄
                    Log.d("TaeHyeong", "Debug2");
                }
                break;
            case R.id.btnDown:
                Log.d("TaeHyeong", "Debug1");
                if (SendThread.mHandler != null) { //SendThread 에 mHandler 존재한다면 실행
                    Message msg = Message.obtain();
                    msg.what = SendThread.CMD_SEND_MESSAGE;
                    msg.obj = "DOWN";
                    SendThread.mHandler.sendMessage(msg);  //작성한 메시지를 SendThread로 핸들러를 이용해 보냄
                    Log.d("TaeHyeong", "Debug2");
                }
                break;
            case R.id.btnLeft:
                Log.d("TaeHyeong", "Debug1");
                if (SendThread.mHandler != null) { //SendThread 에 mHandler 존재한다면 실행
                    Message msg = Message.obtain();
                    msg.what = SendThread.CMD_SEND_MESSAGE;
                    msg.obj = "LEFT";
                    SendThread.mHandler.sendMessage(msg);  //작성한 메시지를 SendThread로 핸들러를 이용해 보냄
                    Log.d("TaeHyeong", "Debug2");
                }
                break;
            case R.id.btnRight:
                Log.d("TaeHyeong", "Debug1");
                if (SendThread.mHandler != null) { //SendThread 에 mHandler 존재한다면 실행
                    Message msg = Message.obtain();
                    msg.what = SendThread.CMD_SEND_MESSAGE;
                    msg.obj = "RIGHT";
                    SendThread.mHandler.sendMessage(msg);  //작성한 메시지를 SendThread로 핸들러를 이용해 보냄
                    Log.d("TaeHyeong", "Debug2");
                }
                break;
        }
    }

    private Handler mMainHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CMD_ENABLE_CONNECT_BUTTON: // 연결 버튼 활성화
                    mClientThread = null;
                    Intent enableButton = new Intent(getApplicationContext(), start.class);
                    startActivity(enableButton);
                    break;
                case CMD_SHOW_BITMAP: // 비트맵 출력
                    Bitmap bitmap = (Bitmap) msg.obj;
                    mImageFrame.setImageBitmap(bitmap);
                    break;

                case CMD_SHOW_MESSAGE: //PUSH 메시지
                    //createNotification("Push 알림",(String) msg.obj);
                    createNotification2("알림", (String) msg.obj);
                    break;
                case CMD_SET_VISION:
                    if (msg.obj.equals("사람 감지")) {
                        mVisionResult.setTextColor(Color.RED);
                        mVisionResult.setText("사람");
                    } else if (msg.obj.equals("고양이 감지")) {
                        mVisionResult.setTextColor(Color.RED);
                        mVisionResult.setText("고양이");
                    } else if (msg.obj.equals("강아지 감지")) {
                        mVisionResult.setTextColor(Color.RED);
                        mVisionResult.setText("강아지");
                    }
                    break;

            }
        }
    };

    private void Speech(String edtSpeech) {    //Speech 메서드
        String text = edtSpeech;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
            // API 20
        else
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);

    }

    // 메모리 누출을 방지하게 위해 TTS를 중지
    @Override
    protected void onStop() {     //Speech 메서드
        super.onStop();
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

    public void createNotification(String s, String s2) {    //push알림 메서드

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");

        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(s);  //제목
        builder.setContentText(s2);  //내용

        builder.setColor(Color.RED);
        // 사용자가 탭을 클릭하면 자동 제거
        builder.setAutoCancel(true);

        // 알림 표시
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));
        }

        // id값은
        // 정의해야하는 각 알림의 고유한 int값
        notificationManager.notify(1, builder.build());
    }

    public void createNotification2(String s1, String s2) {    //push알림 메서드
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = "channel";
        String channelName = "Channel Name";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);

            notificationManager.createNotificationChannel(mChannel);

        }

        Intent resultIntent1 = new Intent(this, MainActivity.class);
        PendingIntent contentIntent1 = PendingIntent.getActivity(this, 0, resultIntent1, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder1 = new NotificationCompat.Builder(this, channelId);
        builder1.setSmallIcon(R.mipmap.ic_launcher);
        builder1.setContentTitle(s1);
        builder1.setContentText(s2);
        builder1.setDefaults(Notification.DEFAULT_VIBRATE);
        builder1.setContentIntent(contentIntent1);
        builder1.setAutoCancel(true);

        notificationManager.notify(0, builder1.build());
    }
}


