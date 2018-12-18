package com.audio.demo;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AudioActivity";

    EditText etTextContent;

    private TextToSpeech.OnInitListener onInitListener;

    private TextToSpeech speech;

    private MediaPlayer.OnCompletionListener onCompletionListener;

    private MediaPlayer player = new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etTextContent = findViewById(R.id.et_text_content);
        findViewById(R.id.btn_text_to_speech).setOnClickListener(this);
        findViewById(R.id.btn_play_single_sound).setOnClickListener(this);
        findViewById(R.id.btn_play_multi_sounds).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_text_to_speech:
                onInitListener = new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        Log.d(TAG, "status = " + status);
                        if (status == TextToSpeech.SUCCESS) {
                            int result = speech.setLanguage(Locale.ENGLISH);
                            if (result == TextToSpeech.LANG_MISSING_DATA
                                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                                ToastUtils.showShortToast("语言不支持");
                            } else {
                                String content = "This is a default voice";
                                if (!TextUtils.isEmpty(etTextContent.getText().toString())) {
                                    content = etTextContent.getText().toString();
                                }
                                Log.d(TAG, "TextContent = " + content);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    speech.speak(content, TextToSpeech.QUEUE_FLUSH, null, null);
                                } else {
                                    speech.speak(content, TextToSpeech.QUEUE_FLUSH, null);
                                }

                            }
                        }
                    }
                };
                speech = new TextToSpeech(this, onInitListener);
                break;
            case R.id.btn_play_single_sound:
                MediaPlayer player = MediaPlayer.create(this, R.raw.tts_success);
                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        Log.d(TAG, "语音播放完成");
                        if (mediaPlayer != null){
                            mediaPlayer.release();
                        }
                    }
                });
                player.start();
                break;
            case R.id.btn_play_multi_sounds:
                sendBroadcast(new Intent(this, VoiceBroadcastReceiver.class));
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (speech != null) {
            speech.shutdown();
        }
    }
}
