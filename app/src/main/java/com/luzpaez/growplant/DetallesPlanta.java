package com.luzpaez.growplant;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DetallesPlanta extends AppCompatActivity {
    private static final String TAG = "DetallesPlanta"; // Etiqueta para los logs
    private ImageView plantImage;
    private Button eliminarPlanta,btnUpdatePlant;
    private TextView tvNameComun, tvNameCientifico, tvFamilyDetail, tvDateDetail;
    private EditText etQuantityDetail;
    private DatabaseReference databaseReference; // Declaración de la referencia a la base de datos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_planta);

        // Inicializa la referencia a la base de datos
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            // Obtener el objeto Plant del Intent
            Plant plant = getIntent().getParcelableExtra("plant_key");
            if (plant == null || plant.getPlantId() == null) {
                Toast.makeText(this, "Error: La planta es nula o el ID es nulo", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // Cambia a la referencia de plantas
            databaseReference = FirebaseDatabase.getInstance().getReference("plants").child(userId).child(plant.getPlantId());
        } else {
            Toast.makeText(this, "Error: Usuario no autenticado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ImageButton btnRegresarPrincipal = findViewById(R.id.regresar);

        // Listener para el botón de regresar
        btnRegresarPrincipal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Esto cierra la actividad actual y regresa a la anterior
            }
        });



        // Inicializar vistas
        plantImage = findViewById(R.id.iv_planta);
        tvNameComun = findViewById(R.id.tv_name_comun);
        tvNameCientifico = findViewById(R.id.tv_name_cientifico);
        tvFamilyDetail = findViewById(R.id.tv_family_detail);
        tvDateDetail = findViewById(R.id.tv_date_detail);
        etQuantityDetail = findViewById(R.id.et_quantity_detail);
        eliminarPlanta = findViewById(R.id.btnDeletePlant);
        btnUpdatePlant = findViewById(R.id.btnUpdatePlant);

        // Obtener el objeto Plant del Intent
        Plant plant = getIntent().getParcelableExtra("plant_key");

        if (plant == null || plant.getPlantId() == null) {
            Toast.makeText(this, "Error: La planta es nula o el ID es nulo", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Mostrar los detalles de la planta en la interfaz
        tvNameComun.setText(plant.getDescription());  // Asigna la descripción como nombre común
        tvNameCientifico.setText(plant.getName());  // Muestra el nombre científico
        tvFamilyDetail.setText(plant.getFamily());
        tvDateDetail.setText(plant.getDate());
        etQuantityDetail.setText(String.valueOf(plant.getQuantity())); // Muestra la cantidad

        // Cargar la imagen de la planta con Glide
        Glide.with(this)
                .load(plant.getImageUrl())
                .into(plantImage);


        // Listener para el botón de actualización
        btnUpdatePlant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etQuantityDetail.isEnabled()) {
                    // Guardar cambios en Firebase
                    String updatedQuantity = etQuantityDetail.getText().toString().trim();
                    if (!updatedQuantity.isEmpty()) {
                        databaseReference.child("quantity").setValue(Integer.parseInt(updatedQuantity))
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(DetallesPlanta.this, "Cantidad actualizada con éxito", Toast.LENGTH_SHORT).show();
                                    etQuantityDetail.setEnabled(false); // Deshabilitar después de actualizar
                                    btnUpdatePlant.setText("Actualizar"); // Cambiar el texto del botón de vuelta
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(DetallesPlanta.this, "Error al actualizar la cantidad", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(DetallesPlanta.this, "Por favor, ingresa una cantidad válida", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Habilitar el campo para editar
                    etQuantityDetail.setEnabled(true);
                    btnUpdatePlant.setText("Guardar"); // Cambiar el texto del botón a "Guardar"
                }
            }
        });

        eliminarPlanta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un diálogo de confirmación
                new AlertDialog.Builder(DetallesPlanta.this)
                        .setTitle("Confirmar eliminación")
                        .setMessage("¿Estás seguro de que deseas eliminar esta planta?")
                        .setPositiveButton("Sí", (dialog, which) -> {
                            Log.d(TAG, "Intentando eliminar la planta con ID: " + plant.getPlantId());

                            // Obtener la URL de la imagen
                            String imageUrl = plant.getImageUrl();

                            // Crear una referencia a la imagen en Storage
                            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);

                            // Eliminar la imagen del Storage
                            storageReference.delete().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // Si la imagen se eliminó con éxito, proceder a eliminar la planta de la base de datos
                                    databaseReference.removeValue()
                                            .addOnCompleteListener(task2 -> {
                                                if (task2.isSuccessful()) {
                                                    Log.d(TAG, "Planta eliminada con éxito");
                                                    Toast.makeText(DetallesPlanta.this, "Planta eliminada con éxito", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                } else {
                                                    Log.e(TAG, "Error al eliminar la planta: " + task2.getException());
                                                    Toast.makeText(DetallesPlanta.this, "Error al eliminar la planta", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(e -> {
                                                Log.e(TAG, "Error al eliminar la planta: " + e.getMessage());
                                                Toast.makeText(DetallesPlanta.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            });
                                } else {
                                    Log.e(TAG, "Error al eliminar la imagen: " + task.getException());
                                    Toast.makeText(DetallesPlanta.this, "Error al eliminar la imagen", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(e -> {
                                Log.e(TAG, "Error al eliminar la imagen: " + e.getMessage());
                                Toast.makeText(DetallesPlanta.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                            // El usuario canceló la eliminación
                            dialog.dismiss();
                        })
                        .show();
            }
        });



    }
}
