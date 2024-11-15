package com.luzpaez.growplant;

import android.os.Bundle;
import android.webkit.WebView;


import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DetalleRecursoActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_recursos);

        // Obtener el botón flotante
        FloatingActionButton fabRegresar = findViewById(R.id.fabRegresar);

        // Configurar el clic del FAB para regresar a la actividad anterior
        fabRegresar.setOnClickListener(view -> {
            // Regresar a la actividad anterior
            onBackPressed();
        });

        webView = findViewById(R.id.webViewDetalle);
        String fileName = getIntent().getStringExtra("fileName");

        // Cargar el archivo HTML desde la carpeta assets
        String filePath = "file:///android_asset/recursos/" + fileName;
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(filePath);
    }
}
