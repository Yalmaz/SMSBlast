package com.funkymaster.smsblast;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by yalmazhasan on 01/03/2017.
 */

public class SMSActivity extends AppCompatActivity {


     SeekBar no_of_times_bar, delay_bar;
     EditText contactET, messageET;
     Button funkBtn, cancelBtn, contactsBtn;
     TextView number_of_times_TV, delay_TV;

    //to get contact from contacts directory
    static final int PICK_CONTACT_REQUEST = 1;  // The request code

     public void onCreate(Bundle saveBundleInstance){
        super.onCreate(saveBundleInstance);
        setContentView(R.layout.activity_sms);

         //requesting for sms permission
         //required in only api 23+
         //if not granted restart activity
         ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},1);

         iniVariables();
         buttonWorkding();
         setSeekBarListener();
    }



    //call vibrator
    public void  vibrate(int miliseconds){
        Vibrator v = (Vibrator) SMSActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(miliseconds);
    }

    //initializes variables
    void iniVariables(){
        no_of_times_bar  = (SeekBar) findViewById(R.id.repetitionBar);
        delay_bar = (SeekBar) findViewById(R.id.dealyBar);
        contactET = (EditText) findViewById(R.id.contact_ET);
        messageET = (EditText) findViewById(R.id.messageET);
        funkBtn = (Button) findViewById(R.id.funkBtn);
        cancelBtn = (Button) findViewById(R.id.cancelBtn);
        contactsBtn = (Button) findViewById(R.id.contacts_btn);
        number_of_times_TV = (TextView) findViewById(R.id.number_of_times_TV);
        delay_TV  = (TextView) findViewById(R.id.delay_TV);
    }

    boolean checkForSMSPermissions(){
        PackageManager pm = this.getPackageManager();
        int hasPerm = pm.checkPermission(
                Manifest.permission.SEND_SMS,
                this.getPackageName());
        if (hasPerm == PackageManager.PERMISSION_GRANTED)
            return true;

        return false;
    }

    //all button working
    void buttonWorkding(){
        funkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrate(15);

                if(!checkForSMSPermissions()){
                    Toast.makeText(SMSActivity.this, "Please allow permission to send message! ", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SMSActivity.this , SMSActivity.class);
                    startActivity(intent);
                    SMSActivity.this.finish();
                }else {
                    if (contactET.getText().toString().equalsIgnoreCase(""))
                        Toast.makeText(SMSActivity.this, "Please enter a CONTACT to Blast !", Toast.LENGTH_LONG).show();
                    else if (no_of_times_bar.getProgress() == 0)
                        Toast.makeText(SMSActivity.this, "Select number of Times !", Toast.LENGTH_LONG).show();
                    else {
                        String message = messageET.getText().toString();
                        if(message.toString().equalsIgnoreCase(""))
                            message = " "; //because empty message gives error
                            sendSMS(contactET.getText().toString(), message, no_of_times_bar.getProgress(), delay_bar.getProgress());
                    }
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrate(15);
                Intent mainIntent = new Intent(SMSActivity.this, MainActivity.class);
                startActivity(mainIntent);
                SMSActivity.this.finish();
            }
        });

        contactsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrate(15);
                pickContact();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request it is that we're responding to
        if (requestCode == PICK_CONTACT_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // Get the URI that points to the selected contact
                Uri contactUri = data.getData();
                // We only need the NUMBER column, because there will be only one row in the result
                String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};

                // Perform the query on the contact to get the NUMBER column
                // We don't need a selection or sort order (there's only one result for the given URI)
                // CAUTION: The query() method should be called from a separate thread to avoid blocking
                // your app's UI thread. (For simplicity of the sample, this code doesn't do that.)
                // Consider using CursorLoader to perform the query.
                Cursor cursor = getContentResolver()
                        .query(contactUri, projection, null, null, null);
                cursor.moveToFirst();

                // Retrieve the phone number from the NUMBER column
                int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = cursor.getString(column);
                contactET.setText(number);

                // Do something with the phone number...
            }
        }
    }


    private void pickContact() {
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
        pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
        startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
    }


    void setSeekBarListener(){
        no_of_times_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                number_of_times_TV.setText("Number of Times : "+no_of_times_bar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        delay_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                delay_TV.setText("Delay (Seconds) : "+delay_bar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    void sendSMS(final String number, final String message,final int number_of_times,final int _delay) {

        AlertDialog.Builder builder = new AlertDialog.Builder(SMSActivity.this);
        builder.setCancelable(true);
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure you want to send "+number_of_times+" SMS to: "+number);
        builder.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int delay = _delay*1000; //to convert into seconds

                        Intent intent = new Intent(SMSActivity.this, MainActivity.class);
                        startActivity(intent);

                        final SmsManager smsManager = SmsManager.getDefault();
                        for (int i=1; i<=number_of_times ; i++) { //loop starting from one to show progress.
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    smsManager.sendTextMessage(number, null, message, null, null);
                                }
                            }, delay);
                        }
                        Toast.makeText(SMSActivity.this, "Process Initiated", Toast.LENGTH_LONG).show();

                        //ending this activity
                        SMSActivity.this.finish();
                    }
                });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();




    }



}
