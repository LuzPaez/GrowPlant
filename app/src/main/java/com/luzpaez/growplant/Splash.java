package com.luzpaez.growplant;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.animation.AnimatorSet;

import android.view.View;
import android.widget.ImageView;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView logoSplash = findViewById(R.id.logoSplash);

        // Aplicar animación al logo
        animateLogo(logoSplash);

        // Temporizador para mostrar el splash durante 3 segundos
        new Handler().postDelayed(() -> {
            // Redirigir al siguiente activity
            Intent intent = new Intent(Splash.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }, 3000); // 3000 ms = 3 segundos
    }

    // Método para animar el logo con escalado y desvanecimiento
    private void animateLogo(View view) {
        // Animación de escalado (zoom in)
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0f, 1f);

        // Animación de desvanecimiento
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);

        // Configurar la duración para todas las animaciones
        scaleX.setDuration(1500); // 1.5 segundos
        scaleY.setDuration(1500); // 1.5 segundos
        fadeIn.setDuration(1500); // 1.5 segundos

        // Agrupar animaciones y reproducirlas juntas
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY, fadeIn);
        animatorSet.start();
    }
}
