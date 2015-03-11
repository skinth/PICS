package com.is.pics;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


public class SplashScreenActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Thread logotimer = new Thread() {
            public void run(){
                try {
                    sleep(4*1000);
                    //dopo 4 secondi reindirizza un nuovo intento
                    Intent lanciaHome = new Intent(getBaseContext(),LoginActivity.class);
                    startActivity(lanciaHome);

                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
        logotimer.start();
    }
}
