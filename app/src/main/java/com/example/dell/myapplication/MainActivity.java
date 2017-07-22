package com.example.dell.myapplication;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.speech.tts.TextToSpeech;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.myapplication.Config;
import com.example.dell.myapplication.R;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import net.gotev.speech.GoogleVoiceTypingDisabledException;
import net.gotev.speech.Speech;
import net.gotev.speech.SpeechDelegate;
import net.gotev.speech.SpeechRecognitionNotAvailable;
import net.gotev.speech.ui.SpeechProgressView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.ContentValues.TAG;
import static android.os.Environment.getExternalStorageDirectory;

public class MainActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener{
    private static final int RECOVERY_REQUEST = 1;
    private YouTubePlayerView youTubeView;
Button b;
    ListView listView ;
    ImageButton recordbtn;
    String h;
    int seek=0,count=0;
    File[] filelist;
    String[] values;

    ArrayAdapter<String> adapter;
    EditText t;
    ArrayList<String>audiolist;
    String text=null;
    TextView speechtext;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    private MyPlayerStateChangeListener playerStateChangeListener;
    private MyPlaybackEventListener playbackEventListener;
    private YouTubePlayer player;
    TextToSpeech t1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


































        youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        youTubeView.initialize(Config.YOUTUBE_API_KEY, MainActivity.this);
        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });

        audiolist = new ArrayList<String>();

        speechtext=(TextView)findViewById(R.id.textView2);

        listView = (ListView) findViewById(R.id.list);
        recordbtn=(ImageButton)findViewById(R.id.imageButton2);
        recordbtn.setClickable(false);
        adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, audiolist);
        recordbtn.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             askSpeechInput();
         }
           });
      // Toast.makeText(this, getExternalStorageDirectory().getAbsolutePath()+"", Toast.LENGTH_SHORT).show();
        File dir = new File(Environment.getExternalStorageDirectory()+"/Audiofiles/");
        if(!dir.exists())
        {
            dir.mkdirs();
        }

        filelist = dir.listFiles();

        if(filelist!=null) {
            String[] theNamesOfFiles = new String[filelist.length];
            for (int i = 0; i < theNamesOfFiles.length; i++) {
                audiolist.add(filelist[i].getName());

            }
            listView.setAdapter(adapter);

        }






        t=(EditText) findViewById(R.id.textView);
        b=(Button)findViewById(R.id.button);
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

                String h= (String)listView.getItemAtPosition(position);
                String videoid=h.substring(0,h.lastIndexOf("@"));

                String time=h.substring(h.indexOf("@")+1,h.indexOf("\n"));

                String speak= h.substring(h.indexOf("\n"));
               // Toast.makeText(MainActivity.this, time+"  "+videoid, Toast.LENGTH_SHORT).show();

                t1.speak(speak, TextToSpeech.QUEUE_FLUSH, null);
                // Toast.makeText(MainActivity.this,h+vi "", Toast.LENGTH_SHORT).show();
                seek=Integer.parseInt(time)*1000;
                  player.cueVideo(videoid);
                 player.seekToMillis(seek);
                player.play();





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
        } else if (requestCode==REQ_CODE_SPEECH_INPUT) {

            if (resultCode == RESULT_OK && null != data) {

                ArrayList<String> result = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                speechtext.setText(result.get(0));
                text=result.get(0);
player.play();
                recordbtn.setClickable(true);
                savedetails();



            }

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
       recordbtn.setClickable(false);
        }

        @Override
        public void onVideoStarted() {
            // Called when playback of the video starts.
            recordbtn.setClickable(true);
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






    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;

    }

    private void askSpeechInput() {
try {


    seek = player.getCurrentTimeMillis();
    player.pause();
    h = t.getText().toString();
    h = h.substring(h.indexOf("?v=") + 3);

    recordbtn.setClickable(false);


    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
    intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
            "Voice is on");
    try {
        startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
    } catch (ActivityNotFoundException a) {

    }


}catch (StringIndexOutOfBoundsException e)
{e.printStackTrace();}
}


  public void  savedetails() {


      if (!text.equals("")) {
          SharedPreferences.Editor editor = getSharedPreferences(h + "@" + seek, MODE_PRIVATE).edit();
          editor.putString("id", h);
          editor.putInt("time", seek);
          editor.putString("text", text);
          editor.apply();
          audiolist.add(h + "@" + seek/1000 + "\n" + text);
          adapter.notifyDataSetChanged();
         // player.play();
          File root = new File(Environment.getExternalStorageDirectory(), "Audiofiles");
          if (!root.exists()) {
              root.mkdirs();
          }

         try {
             File filepath = new File(root,h + "@" + seek/1000 + "\n" + text);
             FileWriter writer = new FileWriter(filepath);
             writer.append(text);
             writer.flush();
             writer.close();
         }
         catch(IOException e){e.printStackTrace();}

      }
      else{
          showMessage("Please try again");
      }
  }
    public void fetchdetails()
    {
        SharedPreferences prefs = getSharedPreferences(h+"@"+seek, MODE_PRIVATE);
        text = prefs.getString("text",null);//"No name defined" is the default value.
        seek = prefs.getInt("time",0); //0 is the default value.
        h=prefs.getString("id",null);


    }


}
