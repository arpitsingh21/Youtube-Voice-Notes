package com.example.dell.myapplication;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.myapplication.Config;
import com.example.dell.myapplication.R;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import java.io.File;
import java.io.IOException;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.ContentValues.TAG;
import static android.os.Environment.getExternalStorageDirectory;

public class MainActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener{
    private static final int RECOVERY_REQUEST = 1;
    private YouTubePlayerView youTubeView;
Button b;
    ListView listView ;
    ImageButton play,stop;
    String h;
    int seek=0,count=0;
    File[] filelist;
    String[] values;
    ArrayAdapter<String> adapter;
    EditText t;
    MediaRecorder mediaRecorder;
    private MyPlayerStateChangeListener playerStateChangeListener;
    private MyPlaybackEventListener playbackEventListener;
    private YouTubePlayer player;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        youTubeView.initialize(Config.YOUTUBE_API_KEY, MainActivity.this);
        listView = (ListView) findViewById(R.id.list);
        mediaRecorder = new MediaRecorder();
        File f=new File(getExternalStorageDirectory().toString()+"/PlayerFiles/");
        f.mkdirs();
       // Toast.makeText(this, getExternalStorageDirectory().getAbsolutePath()+"", Toast.LENGTH_SHORT).show();
try {
      File dir = new File(getExternalStorageDirectory().getAbsolutePath()+"/PlayerFiles/");
      filelist = dir.listFiles();
      values = new String[filelist.length];
      for (int i = 0; i < values.length; i++) {
          values[i] = filelist[i].getName();
       }
      adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, values);
      listView.setAdapter(adapter);
     }
        catch(Exception e)
        {

            e.printStackTrace();
        }
        t=(EditText) findViewById(R.id.textView);
        b=(Button)findViewById(R.id.button);
        play=(ImageButton)findViewById(R.id.imageButton);
        play.setClickable(false);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play.setClickable(false);
                stop.setClickable(true);
                player.pause();
                int n=player.getCurrentTimeMillis();
                recordAudio(h+"@"+n/1000);
            }
        });
        stop=(ImageButton)findViewById(R.id.imageButton2) ;
        stop.setClickable(false);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            player.play();
                mediaRecorder.stop();
                stop.setEnabled(false);
                play.setEnabled(true);
                Toast.makeText(MainActivity.this, "Recording Completed", Toast.LENGTH_LONG).show();
                adapter.notifyDataSetChanged();
            }
        });
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(t.getText().toString().equals(""))
                {
                    Toast.makeText(MainActivity.this, "Enter the Url", Toast.LENGTH_SHORT).show();
                }
     else {
if(player==null)
{
   // Toast.makeText(MainActivity.this, "player is null", Toast.LENGTH_SHORT).show();
    youTubeView.initialize(Config.YOUTUBE_API_KEY, MainActivity.this);

}
else{
    h=t.getText().toString();
    h=h.substring(h.indexOf("?v=")+3);
//            Toast.makeText(MainActivity.this,h+"", Toast.LENGTH_SHORT).show();

    player.cueVideo(h);
seek=0;

}
                }
            }
        });

        playerStateChangeListener = new MyPlayerStateChangeListener();
        playbackEventListener = new MyPlaybackEventListener();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

               // playSong(filelist[position]+"",filelist[position].getName());

                try {
String name=filelist[position].getName();
                    t.setText("https://www.youtube.com/watch?v="+name.substring(0,name.indexOf("@")));

                  final  String h=name.substring(0,name.indexOf("@"));
                    // youTubeView.initialize(Config.YOUTUBE_API_KEY, MainActivity.this);

                   //
                    // player.cueVideo(name.substring(0,name.indexOf("@")));
//player.play();

                     seek=Integer.parseInt(name.substring(name.indexOf("@")+1,name.indexOf(".")))*1000;
                   // Toast.makeText(MainActivity.this, seek+"", Toast.LENGTH_SHORT).show();

            // Toast.makeText(this, songPath+"", Toast.LENGTH_SHORT).show();
            MediaPlayer mp = new MediaPlayer();
            mp.reset();

            mp.setDataSource(filelist[position]+"");

            mp.prepare();


            mp.start();
            int n= mp.getDuration();
//            Toast.makeText(this,name.substring(0,name.indexOf("@"))+""+n/1000, Toast.LENGTH_SHORT).show();

                    new CountDownTimer(n, 1000) {

                        public void onTick(long millisUntilFinished) {


                        }

                        public void onFinish() {

                            player.cueVideo(h);
                            player.seekToMillis(seek);
                        }
                    }.start();




                } catch (Exception e) {
                    Log.v(getString(R.string.app_name), e.getMessage());
                }



            }
        });
    }
    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
        this.player = player;

        player.setPlayerStateChangeListener(playerStateChangeListener);
        player.setPlaybackEventListener(playbackEventListener);
        if (!wasRestored) {
           // Plays https://www.youtube.com/watch?v=fhWaJi1Hsfo

        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_REQUEST).show();
        } else {


         //   String error = String.format(getString(R.string.player_error), errorReason.toString());
            Toast.makeText(this,"Cannot Play", Toast.LENGTH_LONG).show();
            //youTubeView.initialize(Config.YOUTUBE_API_KEY, MainActivity.this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(Config.YOUTUBE_API_KEY, this);
        }
    }



    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return youTubeView;
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private final class MyPlaybackEventListener implements YouTubePlayer.PlaybackEventListener {

        @Override
        public void onPlaying() {
            // Called when playback starts, either due to user action or call to play().
           // showMessage("Playing");
            play.setClickable(true);



        }

        @Override
        public void onPaused() {
            // Called when playback is paused, either due to user action or call to pause().


        }

        @Override
        public void onStopped() {
            // Called when playback stops for a reason other than being paused.
           // showMessage("Stopped");
//            player.seekToMillis(seek);
  //          seek=0;


        }

        @Override
        public void onBuffering(boolean b) {
            // Called when buffering starts or ends.
        }

        @Override
        public void onSeekTo(int i) {
            // Called when a jump in playback position occurs, either
            // due to user scrubbing or call to seekRelativeMillis() or seekToMillis()
        }
    }

    private final class MyPlayerStateChangeListener implements YouTubePlayer.PlayerStateChangeListener {

        @Override
        public void onLoading() {
            // Called when the player is loading a video
            // At this point, it's not ready to accept commands affecting playback such as play() or pause()
        }

        @Override
        public void onLoaded(String s) {
            // Called when a video is done loading.
            // Playback methods such as play(), pause() or seekToMillis(int) may be called after this callback.

            player.seekToMillis(seek);

        }

        @Override
        public void onAdStarted() {
            // Called when playback of an advertisement starts.
        }

        @Override
        public void onVideoStarted() {
            // Called when playback of the video starts.
        }

        @Override
        public void onVideoEnded() {
            // Called when the video reaches its end.
        }

        @Override
        public void onError(YouTubePlayer.ErrorReason errorReason) {
            // Called when an error occurs.

        }
    }


    public void recordAudio(String fileName) {


        if(checkPermission()) {

            String AudioSavePathInDevice =
                    getExternalStorageDirectory().getAbsolutePath() + "/PlayerFiles/" +fileName+".3gp";



            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
            mediaRecorder.setOutputFile(AudioSavePathInDevice);
            Toast.makeText(this, AudioSavePathInDevice+"", Toast.LENGTH_SHORT).show();
            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            Toast.makeText(MainActivity.this, "Recording started",
                    Toast.LENGTH_LONG).show();

        }
        else{
            Toast.makeText(MainActivity.this, "No permissions Granted",
                    Toast.LENGTH_LONG).show();


        }
        }
    private void playSong(String songPath,String name) {

    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;

    }



}
