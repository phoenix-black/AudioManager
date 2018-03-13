package com.hoyo.audiomanager;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Handler;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 03-02-2018.
 */

public abstract class AudioManager {
    private Context context;
    boolean flag = false;
    String case_id;

    private MediaRecorder mediaRecorder;

    private String AudioSavePathInDevice = null;


    public AudioManager(Context context) {
        this.context = context;
    }


    private void MediaRecorderReady() {

        try {
            mediaRecorder = new MediaRecorder();

            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);

            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile(AudioSavePathInDevice);
        } catch (Exception ex) {
            onRecordError(ex.getMessage());
        }

    }


    public void StartAudio(String case_id) {

        this.case_id = case_id;
        initializeAudio();
    }


    public void StartAudio(int time, String case_id) {
        this.case_id = case_id;
        initializeAudio();

        new Handler().postDelayed(stopRecordingRunnable,time);


        /*new Timer().schedule(new TimerTask() {
            @Override
            public void run() {

                Stop();
            }
        }, time);*/


    }

    private Runnable stopRecordingRunnable = new Runnable() {
        @Override
        public void run() {
            Stop();
        }
    };


    public void onPause(){
        Stop();
    }

    public void Stop() {

        if (mediaRecorder != null) {

            if (flag) {
                mediaRecorder.stop();
            }

            mediaRecorder.release();
            mediaRecorder = null;
        }
        try {
            if (AudioSavePathInDevice == null) {

                onRecordError("No path created for audio");

            } else {

                String path = AudioSavePathInDevice;//it contain your path of file..im using a temp string..
                String filename = path.substring(path.lastIndexOf("/") + 1);
                onRecordComplete(AudioSavePathInDevice);
            }
        } catch (Exception ex) {

            onRecordError(ex.getMessage());
        }
    }


    public abstract void onRecordComplete(String filename);

    public abstract void onRecordError(String message);


    private void initializeAudio() {

        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("MMddyyhhmss", Locale.getDefault());
        String currentDateTimeString = simpleDateFormat.format(new Date());
        // String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        String intStorageDirectory = context.getFilesDir().toString();
        File folder = new File(intStorageDirectory, case_id + "/" + "Audio");
        // File folder = new File(intStorageDirectory, "Audio");
        if (!folder.exists()) {
            boolean wasSuccessful = folder.mkdirs();
            if (!wasSuccessful) {
                onRecordError("Error while creating folder");
            }
        }


        AudioSavePathInDevice = folder + "/" + currentDateTimeString + "AudioRecording.mp3";

        MediaRecorderReady();
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            flag = true;


        } catch (IllegalStateException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            onRecordError(e.getMessage());
        } catch (Exception e) {
            onRecordError(e.getMessage());
        }

    }


    //random file creation

    private String CreateRandomAudioFileName(int string) {
        String RandomAudioFileName = "ABCDEFGHIJKLMNOP";
        Random random = new Random();
        try {

            StringBuilder stringBuilder = new StringBuilder(string);

            int i = 0;
            while (i < string) {

                stringBuilder.append(RandomAudioFileName.charAt(random.nextInt(RandomAudioFileName.length())));

                i++;
            }
            return stringBuilder.toString();

        } catch (Exception ex) {
            onRecordError(ex.getMessage());
            return null;
        }
    }


}



