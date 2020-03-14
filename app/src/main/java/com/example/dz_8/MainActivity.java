package com.example.dz_8;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener {
    private	final	static		int		SOUNDPOOL_MAX_STREAMS	= 1;
    private	final	static		String	APP_SHARED_PREFERENCES	= "my_appl_preferences";    //file
    private	final	static		String	PREF_LEFT_VOLUME		= "leftVolume"; //keys
    private	final	static		String	PREF_RIGHT_VOLUME		= "rightVolume";
    private	final	static		String	PREF_TRACK_RATE				= "trackRate";

   // private SoundPool soundPool;
    private  MediaPlayer player;
    private			int			trackSoundID;
    private			int			trackStreamID;
    private			float		trackLeftVolume	= 1.0f;
    private			float		trackRightVolume	= 1.0f;
    private			float		trackRate		= 1.0f;

    private int curPos = 4; //25%
    private static final int maxPos = 16;    //100%
    private SharedPreferences sharedPreferences;
    private TextView tvPercent;
    private static String TAG = "====MainActivity===";
    @SuppressLint({"ResourceAsColor", "ClickableViewAccessibility"})
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvPercent = findViewById(R.id.tvPercent);
        // ----- Получение объекта SharedPreferences ---------------------------
        sharedPreferences	= this.getSharedPreferences(MainActivity.APP_SHARED_PREFERENCES, Context.MODE_PRIVATE);

        // ----- track Sound ---------------------------------------------
        trackLeftVolume = sharedPreferences.getFloat(MainActivity.PREF_LEFT_VOLUME, (float) ((curPos/maxPos)*1.0));
        trackRightVolume = sharedPreferences.getFloat(MainActivity.PREF_RIGHT_VOLUME, (float) ((curPos/maxPos)*1.0));
        float num = (float) (100.0*curPos / maxPos);
        tvPercent.setText(String.valueOf(num));

        // ----- Создание объекта MediaPlayer ----------------
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();

        player = new MediaPlayer();         //MediaPlayer.create(this, R.raw.zhu_in_the_morning);  //raw
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                player.setDataSource(getAssets().openFd("zhu_in_the_morning.mp3"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.setAudioAttributes(audioAttributes);

        //------------------------------------
        Resources resources = this.getResources();
        BitmapDrawable bitmapDrawable = (BitmapDrawable) resources.getDrawable(R.drawable.my_down);
        ImageView ivLeft = findViewById(R.id.ivLeft);
        Bitmap bmp = bitmapDrawable.getBitmap();
        //bmp = bmp.copy(Bitmap.Config.ARGB_8888, true);
        ivLeft.setImageBitmap(bmp);

        final MyVolumControl myVolumControl = this.findViewById(R.id.myControl);
        myVolumControl.addOnVolumSelectListener(pos -> setVolume(myVolumControl, pos));

        final ImageView[] imageViews = new ImageView[]{findViewById(R.id.ivPlay), findViewById(R.id.ivPause),
                                                                                    findViewById(R.id.ivStop)};
        for (ImageView view: imageViews){
            view.setBackgroundColor(R.color.darkBrown);
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                        view.setBackgroundColor(Color.WHITE);
                    }
                    else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                        view.setBackgroundColor(R.color.darkBrown);
                    }
                    btnClick(view);
                    return true;
                }
            });
        }
    }

//-------обработ. регул. громкости-----------
    public void pointerClick(View view) {
        MyVolumControl volumControl = findViewById(R.id.myControl);
        tvPercent = findViewById(R.id.tvPercent);
        int id = view.getId();
        switch (id){
            case R.id.ivLeft:{
                Log.d("#####", "btn_LEFT_Click");
                if(curPos > 0) {
                    curPos = volumControl.getCurPos() - 1;
                }
                Log.e("##########", "ivLeft/curPos: " + curPos);
                setVolume(volumControl, curPos);
            }
            break;
            case R.id.ivRight:{
                Log.d("#####", "btn_RIGHT_Click");
                if(curPos < maxPos){
                    curPos = volumControl.getCurPos() + 1;
                }
                Log.e("##########", "ivRight/curPos: " + curPos);

                setVolume(volumControl, curPos);
            }
            break;
        }
    }
    //-------регулир. уровеня громкости-----------
    private void setVolume(MyVolumControl volumControl, int volPos) {
        volumControl.setCurPos(volPos);
        float log1=(float)(Math.log(volPos)/Math.log(maxPos));
        tvPercent.setText(String.valueOf(100*volPos / maxPos));
        player.setVolume(log1, log1);
        volumControl.resetDraw();
    }

    //-----play--pause--stop--------------
    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void btnClick(View view) {
        int id = view.getId();
        //view.setBackgroundColor(Color.WHITE); //R.color.white
        switch (id) {
            case R.id.ivPlay: {
                Log.d("#####", "music PLAY");
                if (!player.isPlaying()) {
                    //player.setVolume(trackLeftVolume, trackRightVolume);
                    Log.e("########", "trackLeftVolume: "+ trackLeftVolume);
                    player.start();
                }
            }
            break;

            case R.id.ivPause: {
                Log.d("#####", "music PAUSE");
                if(player.isPlaying()) {
                    player.pause();
                }

            }
            break;
            case R.id.ivStop: {
                Log.d("#####", "music STOP");
                player.stop();
                try {
                    player.prepare();
                    player.seekTo(0);
                }
                catch (Throwable t) {
                    Toast.makeText(this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("#######t", t.getMessage());
                }
            }
            break;
        }
    }
    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d("########", "onPrepared");
        mp.start();
    }
    @Override
    public	void	onPause()
    {
        super.onPause();
        // ----- Получение объекта SharedPreferences ---------------------------
        sharedPreferences	= this.getSharedPreferences(MainActivity.APP_SHARED_PREFERENCES, Context.MODE_PRIVATE);

        SharedPreferences.Editor	editor		= sharedPreferences.edit();
        editor.clear();
        // ----- Сохранение настроек для Bell Sound ----------------------------
        editor.putFloat(MainActivity.PREF_LEFT_VOLUME,	trackLeftVolume);
        editor.putFloat(MainActivity.PREF_RIGHT_VOLUME,	  trackRightVolume);
        editor.apply();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        Log.d("##########", "onCompletion");
        player.reset();
    }
}
