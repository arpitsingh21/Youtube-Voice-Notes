package com.example.dell.myapplication;

import android.media.MediaRecorder;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

import static android.R.attr.path;

/**
 * Created by dell on 09-07-2017.
 */

public class AudioRecorder {
    private MediaRecorder recorder = new MediaRecorder();

    private File outfile = null;

    public AudioRecorder(){}

    public void startRecording(String audioFile) throws IOException {
        String state = android.os.Environment.getExternalStorageState();
        if(!state.equals(android.os.Environment.MEDIA_MOUNTED))  {
            throw new IOException("SD Card is not mounted.  It is " + state + ".");
        }

        // make sure the directory we plan to store the recording in exists
        File directory = new File(String.valueOf(path)).getParentFile();
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IOException("Path to file could not be created.");
        }
        try{
            File storageDir = new File(Environment
                    .getExternalStorageDirectory(), "/audio/");
            storageDir.mkdir();
            outfile= File.createTempFile(audioFile, ".wav",storageDir);
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setOutputFile(outfile.getAbsolutePath());
        }catch(IOException e){
            e.printStackTrace();
        }

        try{
            recorder.prepare();
        }catch(IllegalStateException e){
            e.printStackTrace();
        }

        recorder.start();
    }

    public void stop() throws IOException {
        recorder.stop();
        recorder.release();
    }
}
