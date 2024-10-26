package com.luzpaez.growplant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class RegistroPlantasHome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro_plantas_home);

        ImageButton btnRegresarPrincipal = findViewById(R.id.regresar);
        FloatingActionButton fabAddPlant = findViewById(R.id.fab_add_plant);

        // Listener para el botón de regresar a la página principal
        btnRegresarPrincipal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistroPlantasHome.this, PrincipalMenu.class);
                startActivity(intent);
            }
        });

        // Listener para el botón flotante de agregar planta
        fabAddPlant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistroPlantasHome.this, AgregarPlanta.class);
                startActivity(intent);
            }
        });
    }
}