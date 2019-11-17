package hyperionics.com.playerservicesample;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.KeyEvent;

import static android.support.v4.media.session.PlaybackStateCompat.ACTION_PAUSE;
import static android.support.v4.media.session.PlaybackStateCompat.ACTION_PLAY;
import static android.support.v4.media.session.PlaybackStateCompat.ACTION_PLAY_PAUSE;

public class PlayerService extends Service {
    public static final String TAG = "MPS";
    private MediaSessionCompat mediaSession;

    Intent i_startscan;
    Intent i_stopscan;

    private final MediaSessionCompat.Callback mMediaSessionCallback
            = new MediaSessionCompat.Callback() {

        @Override
        public boolean onMediaButtonEvent(Intent mediaButtonEvent) {
            final String intentAction = mediaButtonEvent.getAction();
            if (Intent.ACTION_MEDIA_BUTTON.equals(intentAction)) {
                final KeyEvent event = mediaButtonEvent.getParcelableExtra(
                        Intent.EXTRA_KEY_EVENT);
                if (event == null) {
                    return super.onMediaButtonEvent(mediaButtonEvent);
                }
                final int keycode = event.getKeyCode();
                final int action = event.getAction();
                if (event.getRepeatCount() == 0 && action == KeyEvent.ACTION_DOWN) {
                    switch (keycode) {
                        /*
                        case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                            MainActivity.showText("KEYCODE_MEDIA_PLAY_PAUSE");
                            break;
                        case KeyEvent.KEYCODE_MEDIA_PAUSE:
                            MainActivity.showText("KEYCODE_MEDIA_PAUSE");
                            break;
                        case KeyEvent.KEYCODE_MEDIA_PLAY:
                            MainActivity.showText("KEYCODE_MEDIA_PLAY");
                            break;

                         */
                        case 79 : sendBroadcast(i_startscan);
                    }
                    startService(new Intent(getApplicationContext(), PlayerService.class));
                    return true;
                }
            }
            return false;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        ComponentName receiver = new ComponentName(getPackageName(), RemoteReceiver.class.getName());

        i_startscan = new Intent();
        i_startscan.setAction("com.motorolasolutions.emdk.datawedge.api.ACTION_SOFTSCANTRIGGER");
        i_startscan.putExtra("com.motorolasolutions.emdk.datawedge.api.EXTRA_PARAMETER", "START_SCANNING");

        i_stopscan = new Intent();
        i_stopscan.setAction("com.motorolasolutions.emdk.datawedge.api.ACTION_SOFTSCANTRIGGER");
        i_stopscan.putExtra("com.motorolasolutions.emdk.datawedge.api.EXTRA_PARAMETER", "STOP_SCANNING");

        mediaSession = new MediaSessionCompat(this, "PlayerService", receiver, null);
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSession.setPlaybackState(new PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_PAUSED, 0, 0)
                .setActions(ACTION_PLAY_PAUSE)
                .build());
//        mediaSession.setMetadata(new MediaMetadataCompat.Builder()
//                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, "Test Artist")
//                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, "Test Album")
//                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, "Test Track Name")
//                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 10000)
//                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART,
//                        BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
//                .build());
        mediaSession.setCallback(mMediaSessionCallback);

        mediaSession.setMediaButtonReceiver(
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                        this,
                        null,
                        ACTION_PLAY));


        mediaSession.setMediaButtonReceiver(
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                        this,
                        null,
                        ACTION_PAUSE ));


        mediaSession.setMediaButtonReceiver(
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                        this,
                        null,
                        ACTION_PLAY_PAUSE));

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.requestAudioFocus(new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {

               // MainActivity.showText("focusChange=" + focusChange);

                //a reason for focus change is the LONG PRESS of the heaset button that triggers Android Voice Assistant
                //since it's impossibile to avoid it, the goal here is to regain the lost focus
                //startservice sembrano risolvere la perdita di fuoco. forse basta il solo startservice.

                startService(new Intent(getApplicationContext(), PlayerService.class));
            }
        }, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        mediaSession.setActive(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            MediaButtonReceiver.handleIntent(mediaSession, intent);

        }

        playSoundHere();


/*
        if (mediaSession.getController().getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING) {
            MainActivity.showText("mediaSession set PAUSED state");
            mediaSession.setPlaybackState(new PlaybackStateCompat.Builder()
                    .setState(PlaybackStateCompat.STATE_PAUSED, 0, 0.0f)
                    .setActions(ACTION_PLAY_PAUSE).build());
        } else {
            MainActivity.showText("mediaSession set PLAYING state");
            mediaSession.setPlaybackState(new PlaybackStateCompat.Builder()
                    .setState(PlaybackStateCompat.STATE_PLAYING, 0, 1.0f)
                    .setActions(ACTION_PLAY_PAUSE).build());
        }

 */
        return START_NOT_STICKY; // super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaSession.release();
    }

    void playSoundHere(){
        //da verificare se è meglio fare play qui nel service per garantire routing evento
        final MediaPlayer mMediaPlayer;
        mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.beep);
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mMediaPlayer.release();
            }
        });
        mMediaPlayer.start();
    }
}