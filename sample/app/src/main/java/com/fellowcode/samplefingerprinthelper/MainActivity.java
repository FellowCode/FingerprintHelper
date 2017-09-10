package com.fellowcode.samplefingerprinthelper;

import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fellowcode.fingerprinthelper.FingerprintHelper;
import com.fellowcode.fingerprinthelper.FingerprintMethods;


public class MainActivity extends AppCompatActivity {

    FingerprintHelper fingerprintHelper;
    ImageView fingerprintImage;
    TextView notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fingerprintImage = (ImageView) findViewById(R.id.fingerprint);
        notification = (TextView) findViewById(R.id.notification);

        fingerprintHelper = new FingerprintHelper(this, new FingerprintMethods() {
            @Override
            public void onAuthenticationSucceeded() {
                Toast.makeText(MainActivity.this, "AuthSucceded", Toast.LENGTH_SHORT).show();
                new CountDownTimer(1000, 1000){
                    public void onTick(long millisUntilFinished){

                    }
                    public void onFinish(){
                        fingerprintHelper.prepareSensor();
                    }

                }.start();
            }

            @Override
            public void onAuthenticationError() {
                Toast.makeText(MainActivity.this, "AuthError", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationFailed() {
                Toast.makeText(MainActivity.this, "AuthFail", Toast.LENGTH_SHORT).show();
            }
        });
        if(!fingerprintHelper.checkFinger())                      //checkFinger may be not use
            notification.setText("No support fingerprint");
        //Set image may be not initialize
        fingerprintHelper.setImage(200, fingerprintImage,    //on Fail after 200 milliseconds image  return to base state
                R.mipmap.ic_fingerprint_base,                //replace on base
                R.mipmap.ic_fingerprint_succes,              //replace on succes
                R.mipmap.ic_fingerprint_fail);               //replace on fail
    }

    @Override
    public void onResume(){
        super.onResume();
        fingerprintHelper.prepareSensor();
    }
    @Override
    public void onStop(){
        super.onStop();
        fingerprintHelper.stopSensor();
    }
}
