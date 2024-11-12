package com.luzpaez.growplant;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

public class Politica extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_politica);

        //  referencia al WebView
        WebView webView = findViewById(R.id.webView);

        // JavaScript para WebView
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Configuración el WebViewClient para manejar el mailto de html
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("mailto:")) {
                    // Crear un Intent para abrir la aplicación de correo
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                    emailIntent.setData(Uri.parse(url));
                    startActivity(emailIntent);
                    return true; // Indica que el WebView no debe manejar el enlace
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        // Carga el archivo HTML desde la carpeta assets
        String htmlPath = "file:///android_asset/politicaGrow.html"; // HTML esta en la carpeta assets
        webView.loadUrl(htmlPath);

        // Configuración el botón flotante para regresar a la actividad de Registro de Usuario
        AppCompatImageButton btnRegresarRegistro = findViewById(R.id.btnRegresarRegistro);
        btnRegresarRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Regresar a la actividad de Registro de Usuario
                Intent intent = new Intent(Politica.this, RegistroDeUsuario.class);
                startActivity(intent);
                finish(); // Finaliza la actividad actual para no dejarla en el stack
            }
        });
    }
}

