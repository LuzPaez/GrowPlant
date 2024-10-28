package com.luzpaez.growplant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
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
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_plantas_home);

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

        // Configurar RecyclerView
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar PlantAdapter con plantList
        plantAdapter = new PlantAdapter(this, plantList);
        recyclerView.setAdapter(plantAdapter);

        // Cargar datos desde Firebase
        loadPlantData();
    }

    private void loadPlantData() {
        if (databaseReference != null) {
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    plantList.clear();
                    for (DataSnapshot plantSnapshot : snapshot.getChildren()) {
                        Plant plant = plantSnapshot.getValue(Plant.class);
                        if (plant != null) {
                            plantList.add(plant);
                        }
                    }
                    plantAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(RegistroPlantasHome.this, "Error al cargar las plantas.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
