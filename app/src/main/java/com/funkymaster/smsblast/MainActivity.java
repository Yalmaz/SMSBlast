package com.funkymaster.smsblast;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends AppCompatActivity {

    private ImageButton smsFunkBtn;
    private ImageButton textFunkBtn;

    private Button exitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iniVariables();
        onClickWorkings();
        adBanner();

        this.overridePendingTransition(R.anim.enter_in, R.anim.enter_out);
    }

    //initialize variables
    void iniVariables(){
        smsFunkBtn = (ImageButton) findViewById(R.id.smsFunk_btn);
        textFunkBtn = (ImageButton) findViewById(R.id.textFunk_btn);

        exitBtn = (Button) findViewById(R.id.exitBtn);
    }


    //setting adBanner
    public void adBanner(){
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    //call vibrator
    public void  vibrate(int miliseconds){
        Vibrator v = (Vibrator) MainActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(miliseconds);
    }


    //on click working of buttons
    public void onClickWorkings(){

        smsFunkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                smsFunkBtn.setBackgroundColor(Color.rgb(241,173,77));
                vibrate(15);
                Intent intent = new Intent(MainActivity.this , SMSActivity.class);
                MainActivity.this.startActivity(intent);
                MainActivity.this.overridePendingTransition(R.anim.drop_in, R.anim.drop_out);
                MainActivity.this.finish();
            }
        });
        smsFunkBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                smsFunkBtn.setBackgroundColor(Color.rgb(220,220,220));
                return false;
            }
        });


        textFunkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textFunkBtn.setBackgroundColor(Color.rgb(0,251,255));
                vibrate(15);
                Intent intent = new Intent(MainActivity.this , TextActivity.class);
                MainActivity.this.startActivity(intent);
                MainActivity.this.overridePendingTransition(R.anim.drop_in, R.anim.drop_out);
                MainActivity.this.finish();
            }
        });
        textFunkBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                textFunkBtn.setBackgroundColor(Color.rgb(220,220,220));
                return false;
            }
        });
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrate(15);
                MainActivity.this.finish();
            }
        });
    }


}


