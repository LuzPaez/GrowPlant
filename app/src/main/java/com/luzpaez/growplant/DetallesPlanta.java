package com.luzpaez.growplant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class DetallesPlanta extends AppCompatActivity {
    private ImageView plantImage;
    private TextView tvNameComun, tvNameCientifico, tvFamilyDetail, tvDateDetail, tvQuantityDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_planta);

        ImageButton btnRegresarPrincipal = findViewById(R.id.regresar);

        // Listener para el botón de regresar a la página principal
        btnRegresarPrincipal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetallesPlanta.this, RegistroPlantasHome.class);
                startActivity(intent);
            }
        });


        // Inicializar vistas
        plantImage = findViewById(R.id.iv_planta);
        tvNameComun = findViewById(R.id.tv_name_comun);
        tvNameCientifico = findViewById(R.id.tv_name_cientifico);
        tvFamilyDetail = findViewById(R.id.tv_family_detail);
        tvDateDetail = findViewById(R.id.tv_date_detail);
        tvQuantityDetail = findViewById(R.id.tv_quantity_detail);

        // Obtener el objeto Plant del Intent
        Plant plant = getIntent().getParcelableExtra("plant_key");

        if (plant == null) {
            Toast.makeText(this, "Error: La planta es nula", Toast.LENGTH_SHORT).show();
            finish(); // Cierra la actividad si no se recibió el objeto Plant
            return;
        }

        // Mostrar los detalles de la planta en la interfaz
        tvNameComun.setText(plant.getDescription());  // Asigna la descripción como nombre común
        tvNameCientifico.setText(plant.getName());  // Muestra el nombre científico
        tvFamilyDetail.setText(plant.getFamily());
        tvDateDetail.setText(plant.getDate());
        tvQuantityDetail.setText(String.valueOf(plant.getQuantity())); // Muestra la cantidad

        // Cargar la imagen de la planta con Glide
        Glide.with(this)
                .load(plant.getImageUrl())
                .into(plantImage);
    }

}
