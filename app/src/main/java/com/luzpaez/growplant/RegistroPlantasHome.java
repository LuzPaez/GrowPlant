package com.luzpaez.growplant;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RegistroPlantasHome extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PlantAdapter plantAdapter;
    private List<Plant> plantList;  // Asegúrate de declarar plantList aquí
    private List<Plant> originalPlantList;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private ImageButton regresarButton;
    private ImageView iconoRegistroPlantas;
    private TextView titulo;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_plantas_home);

        regresarButton = findViewById(R.id.regresar);
        iconoRegistroPlantas = findViewById(R.id.iconRegistroPlantas);
        titulo = findViewById(R.id.titulo);

        // Animar el botón de regreso con desvanecimiento
        fadeInButton(regresarButton, 1000);  // Desvanecimiento del botón de regreso

        // Animar el ícono y el título con desvanecimiento
        animateElement(iconoRegistroPlantas, 700, 0f, 1f);  // Desvanecimiento del ícono
        animateElement(titulo, 900, 0f, 1f);  // Desvanecimiento del título

        ImageButton btnRegresarPrincipal = findViewById(R.id.regresar);

        // Listener para el botón de regresar a la página principal
        btnRegresarPrincipal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistroPlantasHome.this, PrincipalMenu.class);
                startActivity(intent);
            }
        });

        FloatingActionButton fabAddPlant = findViewById(R.id.fab_add_plant);

        // Listener para el botón flotante que lleva a la actividad AgregarPlanta
        fabAddPlant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistroPlantasHome.this, AgregarPlanta.class);
                startActivity(intent);
            }
        });

        // Inicializar FirebaseAuth y DatabaseReference
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference("plants").child(userId);
        }

        // Inicializar la lista de plantas
        plantList = new ArrayList<>();
        originalPlantList = new ArrayList<>();

        // Configurar RecyclerView
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar PlantAdapter con plantList
        plantAdapter = new PlantAdapter(this, plantList ,originalPlantList);
        recyclerView.setAdapter(plantAdapter);


        EditText searchEditText = findViewById(R.id.BarraBusqueda);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                plantAdapter.filter(s.toString()); // Aplicar filtro mientras escribe
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });




        // Cargar datos desde Firebase
        loadPlantData();
    }

    // Método para animar el desvanecimiento del botón de regreso
    private void fadeInButton(View view, long delay) {
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);  // Desvanecimiento desde invisible (0f) a visible (1f)
        fadeIn.setStartDelay(delay);  // Retraso para la animación
        fadeIn.setDuration(1000);  // Duración del desvanecimiento
        fadeIn.start();
    }

    // Método genérico para animar el desvanecimiento (alpha) de otros elementos
    private void animateElement(View view, long delay, float startAlpha, float endAlpha) {
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(view, "alpha", startAlpha, endAlpha);
        fadeIn.setStartDelay(delay);  // Retraso para la animación secuencial
        fadeIn.setDuration(500); // Duración de la animación
        fadeIn.start();
    }

    private void loadPlantData() {
        if (databaseReference != null) {
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    plantList.clear(); // Limpiar la lista antes de llenarla
                    originalPlantList.clear();
                    for (DataSnapshot plantSnapshot : snapshot.getChildren()) {
                        // Obtener el ID de la planta
                        String plantId = plantSnapshot.getKey();
                        // Obtener otros campos
                        String date = plantSnapshot.child("date").getValue(String.class);
                        String description = plantSnapshot.child("description").getValue(String.class);
                        String family = plantSnapshot.child("family").getValue(String.class);
                        String imageUrl = plantSnapshot.child("image_url").getValue(String.class);
                        String name = plantSnapshot.child("name").getValue(String.class);
                        int quantity = plantSnapshot.child("quantity").getValue(Integer.class);

                        // Crear una instancia de Plant
                        Plant plant = new Plant(plantId, date, description, family, imageUrl, name, quantity);
                        // Agregar la planta a la lista
                        plantList.add(plant);
                        originalPlantList.add(plant); // Copia también en la lista original
                    }
                    plantAdapter.notifyDataSetChanged(); // Notificar al adaptador que los datos han cambiado
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(RegistroPlantasHome.this, "Error al cargar las plantas.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
