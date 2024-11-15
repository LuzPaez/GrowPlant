package com.luzpaez.growplant;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class Informacion extends AppCompatActivity implements PlantAdapter2.OnPlantClickListener {

    private Spinner spinnerPreguntas;
    private Button btnEnviarPregunta;
    private RecyclerView recyclerView;
    private PlantAdapter2 plantAdapter; // Cambiado a PlantAdapter2
    private List<Plant> plantList;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private Plant selectedPlant; // Variable para guardar la planta seleccionada
    private TextView respuestaIA;
    private ImageButton regresarButton;
    private ImageView icono;
    private TextView titulo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacion);

        //animacion
        regresarButton = findViewById(R.id.regresar);
        icono = findViewById(R.id.iconInformacion);
        titulo = findViewById(R.id.titulo);

        // Animar el botón de regreso con desvanecimiento
        fadeInButton(regresarButton, 500);  // Desvanecimiento del botón de regreso

        // Animar el ícono y el título con desvanecimiento
        animateElement(icono, 700, 0f, 1f);  // Desvanecimiento del ícono
        animateElement(titulo, 900, 0f, 1f);  // Desvanecimiento del título

        // Configuración de Firebase Auth y referencia de base de datos
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference("plants").child(userId);
        }

        ImageButton btnRegresarPrincipal = findViewById(R.id.regresar);
        FloatingActionButton fabAddPlant = findViewById(R.id.fab_add_plant);

        // Listener para el botón de regresar
        btnRegresarPrincipal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Cierra la actividad y regresa a la anterior
            }
        });

        // Listener para el botón flotante que lleva a la actividad AgregarPlanta
        fabAddPlant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Informacion.this, AgregarPlanta.class);
                startActivity(intent);
            }
        });

        // Inicializar RecyclerView y lista de plantas
        recyclerView = findViewById(R.id.recycler_plantas_horizontal);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        plantList = new ArrayList<>();
        plantAdapter = new PlantAdapter2(this, plantList, this); // Usar PlantAdapter2 con el listener
        recyclerView.setAdapter(plantAdapter);

        // Inicializar Spinner usando el string-array desde strings.xml
        spinnerPreguntas = findViewById(R.id.spinner_preguntas);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.preguntas_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPreguntas.setAdapter(adapter);

        //inicializar respuesta ia
        respuestaIA = findViewById(R.id.respuesta_IA);

        // Configurar botón de enviar pregunta
        btnEnviarPregunta = findViewById(R.id.btn_enviar_pregunta);
        btnEnviarPregunta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPlant != null) {
                    String preguntaSeleccionada = spinnerPreguntas.getSelectedItem().toString();
                    generarTextoConIA(preguntaSeleccionada, selectedPlant);  // Llamar a la función con la pregunta y la planta
                } else {
                    Toast.makeText(Informacion.this, "Selecciona una planta primero", Toast.LENGTH_SHORT).show();
                }
            }
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

    // Implementa el método de la interfaz para manejar clics
    @Override
    public void onPlantClick(Plant plant) {
        selectedPlant = plant; // Guardar la planta seleccionada
        Toast.makeText(this, "Planta seleccionada: " + plant.getName(), Toast.LENGTH_SHORT).show();
    }

    // Método para cargar datos de plantas desde Firebase
    private void loadPlantData() {
        if (databaseReference != null) {
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    plantList.clear(); // Limpiar la lista antes de llenarla
                    for (DataSnapshot plantSnapshot : snapshot.getChildren()) {
                        String plantId = plantSnapshot.getKey();
                        String date = plantSnapshot.child("date").getValue(String.class);
                        String description = plantSnapshot.child("description").getValue(String.class);
                        String family = plantSnapshot.child("family").getValue(String.class);
                        String imageUrl = plantSnapshot.child("image_url").getValue(String.class);
                        String name = plantSnapshot.child("name").getValue(String.class);
                        int quantity = plantSnapshot.child("quantity").getValue(Integer.class);

                        Plant plant = new Plant(plantId, date, description, family, imageUrl, name, quantity);
                        plantList.add(plant);
                    }
                    plantAdapter.notifyDataSetChanged(); // Notificar al adaptador que los datos han cambiado
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(Informacion.this, "Error al cargar las plantas.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // Método para generar la respuesta con la IA
    private void generarTextoConIA(String pregunta, Plant selectedPlant) {
        // Preparar los datos de la planta para enviar a la IA
        String plantInfo = "Planta: " + selectedPlant.getName() + "\n" +
                "Descripción: " + selectedPlant.getDescription() + "\n" +
                "Familia: " + selectedPlant.getFamily() + "\n" +
                "Cantidad: " + selectedPlant.getQuantity();

        // Construir el texto completo para la IA
        String prompt = "Pregunta: " + pregunta + "\n" + plantInfo;

        // Inicializar el modelo generativo de Gemini
        GenerativeModel gm = new GenerativeModel("gemini-1.5-flash", "API_KEY");
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        Content content = new Content.Builder()
                .addText(prompt)
                .build();

        // Usar un Executor para manejar el hilo
        Executor executor = Executors.newSingleThreadExecutor();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        // Manejar la respuesta de manera asincrónica
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = result.getText();

                // Actualiza el TextView con el resultado de la IA
                runOnUiThread(() -> {
                    respuestaIA.setText(resultText);  // Muestra la respuesta en el TextView
                });
            }


            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                Toast.makeText(Informacion.this, "Error al generar la respuesta.", Toast.LENGTH_SHORT).show();
            }
        }, executor);
    }
}
