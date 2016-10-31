package com.testdesign.voicerecorder;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private File recordAudioFile;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private TextView tv1;
    private ImageView imgV;
    private Button btnPlay, btnDelete;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }
    public void init(){
        context = this;

        btnPlay = (Button) findViewById(R.id.btnPlay);
        btnDelete = (Button) findViewById(R.id.btnDelete);

        tv1 = (TextView) findViewById(R.id.textView1);
        imgV = (ImageView) findViewById(R.id.imageView1);

        imgV.setOnTouchListener(new MyTouchListener());

        btnPlay.setEnabled(false);
        btnDelete.setEnabled(false);
    }

    public class MyTouchListener implements View.OnTouchListener{

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    imgV.setImageResource(R.drawable.media_record_on);

                    try{
                        recordAudioFile = File.createTempFile("raw",".amr",
                                Environment.getExternalStorageDirectory());

                        tv1.setText("路徑 : " + recordAudioFile.getAbsolutePath());
                        mediaRecorder = new MediaRecorder();
                        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
                        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                        mediaRecorder.setOutputFile(recordAudioFile.getAbsolutePath());
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    }catch (Exception e){
                        Log.d("MediaError",e.getMessage());
                }
                    Toast.makeText(context,"開始錄音",Toast.LENGTH_LONG).show();

                    break;
                case MotionEvent.ACTION_UP:
                    imgV.setImageResource(R.drawable.media_record_off);

                    if(mediaRecorder != null){
                        mediaRecorder.stop();
                        mediaRecorder.release();
                        mediaRecorder = null;

                        btnPlay.setEnabled(true);
                        // left, top, right, bottom
                        btnPlay.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.player_play),null,null);

                        btnDelete.setEnabled(true);
                        btnDelete.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.delete),null,null);

                        playAudioFile();
                        Toast.makeText(context, "開始播放", Toast.LENGTH_SHORT).show();

                    }

                    break;
            }
            return true;
        }
    }

    private void playAudioFile(){
        File f = new File(recordAudioFile.getAbsolutePath());
        Uri uri = Uri.fromFile(f);
        mediaPlayer = MediaPlayer.create(context, uri);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer player) {
                player.start();
            }
        });

    }

    private void deleteAudioFIle(){
        recordAudioFile.delete();
        tv1.setText("");
        btnPlay.setEnabled(false);
        btnPlay.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.player_play_gray),null,null);

        btnDelete.setEnabled(false);
        btnDelete.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.delete_gray),null,null);

    }

    public void onClick(View v){
        try{
            switch (v.getId()){
                case R.id.btnPlay:
                    playAudioFile();
                    Toast.makeText(context,"重播",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.btnDelete:
                    deleteAudioFIle();
                    Toast.makeText(context,"刪除檔案",Toast.LENGTH_SHORT).show();
                    break;
            }
        }catch (Exception e){
            Log.d("error",e.getMessage());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mediaPlayer != null) mediaPlayer.release();
    }
}
