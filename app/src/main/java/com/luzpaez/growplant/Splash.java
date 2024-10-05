package com.luzpaez.growplant;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        //Temporizador para mostrar el splash durante 3- segundos
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Redirigir al activity
                Intent intent = new Intent(Splash.this , LoginActivity.class);
                startActivity(intent);
                finish();
            }
        } , 3000); // 3000 ms = 3 segundos

    }
}