package hyperionics.com.playerservicesample;

import static hyperionics.com.playerservicesample.PlayerService.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    TextToSpeech myTts;
    TextView mEventTxt;
    CheckBox mHackCb;
    private static MainActivity mCurrActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(this, PlayerService.class));
        mEventTxt = (TextView) findViewById(R.id.textView);
        mHackCb = (CheckBox) findViewById(R.id.hackCb);
        mCurrActivity = this;
/*
        myTts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    myTts.setLanguage(Locale.US);
                }
            }
        });

 */
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        showText("MainActivity onKeyDown() keyCode = " + keyCode);
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onDestroy() {
        mCurrActivity = null;
        super.onDestroy();
    }

    public void onClickTts(View v) {
        String toSpeak = "This is a sample text that should play with Android default TTS engine.";
        Toast.makeText(getApplicationContext(), toSpeak,Toast.LENGTH_SHORT).show();
        //if (mHackCb.isChecked()) {

            /*
            //col play qui non si riprende dopo sleep
            final MediaPlayer mMediaPlayer;
            mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.beep);
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mMediaPlayer.release();
                }
            });
            mMediaPlayer.start();

             */
        //}
        //myTts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
    }

    public void onClickHack(View v) {
        String s;
        if (mHackCb.isChecked()) {
            s = "Hack enabled, Media Player will play short silent WAV before speech.";
        }
        else {
            s = "Hack DISABLED, after using any music player, media buttons won't come here on Oreo";
        }
        Toast.makeText(getApplicationContext(),
                s,
                Toast.LENGTH_LONG).show();
    }

    static void showText(String s) {
        Log.d(TAG, s);
        if (mCurrActivity != null) {
            String s0 = mCurrActivity.mEventTxt.getText() + "\n";
            s0 += s;
            if (s0.length() > 256) {
                s0 = "(...) " + s0.substring(s0.length() - 250);
            }
            mCurrActivity.mEventTxt.setText(s0);
        }
    }
}
