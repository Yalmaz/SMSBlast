package com.funkymaster.smsblast;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


/**
 * Created by yalmazhasan on 01/03/2017.
 */

public class TextActivity extends AppCompatActivity {

    Button   funkBtn, cancelBtn;
    EditText textET;
    TextView repeatText_TV;
    SeekBar repeat_bar;
    RadioButton  whatsapp_radio, messenger_radio, google_radio, twitter_radio;


    String repeated_text;

    public void onCreate(Bundle savedBundleInstance){
        super.onCreate(savedBundleInstance);
        setContentView(R.layout.activity_text);

        //requesting for sms permission
        //required in only api 23+
        //if not granted restart activity
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},1);

        iniVariables();
        onClickWorkings();
        seekBarWorking();
        adBanner();

    }


    void seekBarWorking(){
        repeat_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                repeatText_TV.setText("Repeat Text : "+seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }



    void iniVariables(){

        funkBtn = (Button) findViewById(R.id.funk_text_Btn);
        cancelBtn = (Button) findViewById(R.id.cancel_text_Btn);

        textET = (EditText) findViewById(R.id.textET);


        repeatText_TV = (TextView) findViewById(R.id.repeat_text_TV);
        repeat_bar = (SeekBar) findViewById(R.id.repet_bar);


        whatsapp_radio = (RadioButton) findViewById(R.id.whatsapp_radio);
        messenger_radio = (RadioButton) findViewById(R.id.messenger_radio);
        google_radio = (RadioButton) findViewById(R.id.google_radio);
        twitter_radio = (RadioButton) findViewById(R.id.twitter_radio);

        //by default whatsapp radio is check so
        whatsapp_radio.setChecked(true);

        repeated_text = "";

    }
    //call vibrator
    public void  vibrate(int miliseconds){
        Vibrator v = (Vibrator) TextActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(miliseconds);
    }

    void onClickWorkings(){
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrate(10);
                Intent mainIntent = new Intent(TextActivity.this, MainActivity.class);
                startActivity(mainIntent);
                TextActivity.this.finish();
            }
        });



        funkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrate(15);
                if(textET.getText().toString().equalsIgnoreCase(""))
                    Toast.makeText(TextActivity.this, "Please fill both Text and Repeater !", Toast.LENGTH_SHORT).show();
                else{
                    repeat_text(textET.getText().toString(), repeat_bar.getProgress());
                    //adding delay
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (whatsapp_radio.isChecked())
                                send_message("com.whatsapp" , repeated_text);
                            else if(messenger_radio.isChecked())
                                send_message("com.facebook.orca" , repeated_text);
                            else if(google_radio.isChecked())
                                send_message("com.google.android.apps.plus" , repeated_text);
                            else
                                send_message("com.twitter.android" , repeated_text);

                            //resetting the layout after task finish
                            textET.setText("");
                            repeat_bar.setProgress(0);
                            whatsapp_radio.setChecked(true);
                        }
                    }, 10);
                }
            }
        });
    }

    void repeat_text(String text, int repeat){

        repeated_text = "";

        if(repeat == 0) {
            Toast.makeText(TextActivity.this, "Repeat text should not be 0", Toast.LENGTH_LONG).show();
            return;
        }

        for(int i=0 ; i < repeat ; i++){
            repeated_text = repeated_text+" "+text;
        }
    }


    void send_message(String intent, String message){
        PackageManager pm=getPackageManager();
        try {

            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("text/plain");

            PackageInfo info=pm.getPackageInfo(intent, PackageManager.GET_META_DATA);
            //Check if package exists or not. If not then code
            //in catch block will be called
            waIntent.setPackage(intent);

            waIntent.putExtra(Intent.EXTRA_TEXT, message);
            startActivity(Intent.createChooser(waIntent, "Share with"));

        } catch (PackageManager.NameNotFoundException e) {
            if(intent.contains("whatsapp"))
                Toast.makeText(this, "WhatsApp not Installed", Toast.LENGTH_LONG).show();
            else if(intent.contains("facebook.orca"))
                Toast.makeText(this, "Facebook Messenger not Installed",Toast.LENGTH_LONG).show();
            else if(intent.contains("twitter"))
                Toast.makeText(this, "Twitter not Installed",Toast.LENGTH_LONG).show();
            else
                Toast.makeText(this, "Google + not Installed",Toast.LENGTH_LONG).show();
        }
    }

    //setting adBanner
    public void adBanner(){
        AdView mAdView = (AdView) findViewById(R.id.activity_text_adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

}
