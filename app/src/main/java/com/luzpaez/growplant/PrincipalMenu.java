package com.luzpaez.growplant;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class PrincipalMenu extends AppCompatActivity implements TaskAdapter.OnTaskCheckChangeListener{

    private ImageView fotoperfil;
    private TextView nombreHuertero;
    private ImageButton btnSalir;

    private FirebaseAuth auth;
    private DatabaseReference userRef;
    private StorageReference storageRef;
    private DatabaseReference taskRef;

    private RecyclerView rvTareasPendientes;
    private TaskAdapter taskAdapter;
    private ArrayList<Task> taskList = new ArrayList<>();
    private ArrayList<Task> completedTaskList = new ArrayList<>();
    private TaskAdapter completedTaskAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal_menu);

        // Inicializar Firebase
        auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference("users").child(auth.getCurrentUser().getUid());
        taskRef = FirebaseDatabase.getInstance().getReference("recordatorios").child(userId).child("tasks");
        storageRef = FirebaseStorage.getInstance().getReference();


        // Inicializar vistas
        fotoperfil = findViewById(R.id.fotoperfil);
        nombreHuertero = findViewById(R.id.nombreHuertero);
        btnSalir = findViewById(R.id.SalirApp);

        // Configuración del  RecyclerView

        rvTareasPendientes = findViewById(R.id.rvListaTareas);
        rvTareasPendientes.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        rvTareasPendientes.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(this, taskList, this);
        rvTareasPendientes.setAdapter(taskAdapter);

        // Actualizar imagen y nombre de usuario
        cargarDatosUsuario();

        // Cargar tareas
        loadTasks();

        // Botón para redirigir a la actividad de configuración
        CardView cardConfiguracion = findViewById(R.id.IrConfiguración);
        cardConfiguracion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrincipalMenu.this, Configuracion.class);
                startActivity(intent);
            }
        });

        // Botón para redirigir a la actividad de RegistroPlantas
        CardView cardRegistroPlantasHome = findViewById(R.id.IrRegistroPlantasHome);
        cardRegistroPlantasHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrincipalMenu.this, RegistroPlantasHome.class);
                startActivity(intent);
            }
        });

        // Botón para redirigir a la actividad de Estadisticas
        CardView cardEstadisticas = findViewById(R.id.IrEstadisticas);
        cardEstadisticas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrincipalMenu.this, Estadisticas.class);
                startActivity(intent);
            }
        });

        // Botón para redirigir a la actividad de Informacion
        CardView cardInformacion = findViewById(R.id.IrInformacion);
        cardInformacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrincipalMenu.this, Informacion.class);
                startActivity(intent);
            }
        });

        // Botón para redirigir a la actividad de Recordatorio

        CardView cardRecordatorios = findViewById(R.id.IrRecordatorios);
        cardRecordatorios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrincipalMenu.this, RecordatoriosActivity.class);
                startActivity(intent);
            }
        });

        // Botón para redirigir a la actividad de Recursos

        CardView cardRecursos = findViewById(R.id.IrRecursos);
        cardRecursos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrincipalMenu.this, Recursos.class);
                startActivity(intent);
            }
        });


        //agregar boton de recursos

        // Botón para salir de la aplicación
        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarConfirmacionSalir();
            }
        });
    }

    // Método para cargar los datos del usuario (nombre e imagen de perfil)
    private void cargarDatosUsuario() {
        // Escuchar los cambios en el nombre de usuario en tiempo real
        userRef.child("nombreUsuario").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String nombre = dataSnapshot.getValue(String.class);
                if (nombre != null && !nombre.isEmpty()) {
                    nombreHuertero.setText(nombre);
                } else {
                    nombreHuertero.setText("Huertero");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                nombreHuertero.setText("Huertero");
            }
        });

        // Escuchar los cambios en la URL de la imagen de perfil en tiempo real
        userRef.child("fotoPerfilUrl").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String fotoUrl = dataSnapshot.getValue(String.class);
                if (fotoUrl != null) {
                    // Usar Glide para cargar la imagen en el ImageView
                    Glide.with(PrincipalMenu.this)
                            .load(fotoUrl)
                            .placeholder(R.drawable.fotoperfilpordefecto) // Imagen por defecto mientras se carga
                            .into(fotoperfil);
                } else {
                    fotoperfil.setImageResource(R.drawable.fotoperfilpordefecto);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                fotoperfil.setImageResource(R.drawable.fotoperfilpordefecto);
            }
        });
    }

    private void loadTasks() {
        taskRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                taskList.clear(); // Limpiar la lista antes de cargar los nuevos datos
                for (DataSnapshot taskSnapshot : snapshot.getChildren()) {
                    String taskId = taskSnapshot.getKey();
                    String plantName = taskSnapshot.child("plantName").getValue(String.class);
                    String taskDescription = taskSnapshot.child("taskDescription").getValue(String.class);
                    String dueDate = taskSnapshot.child("dueDate").getValue(String.class);
                    String imageUrl = taskSnapshot.child("imageUrl").getValue(String.class);
                    boolean isCompleted = Boolean.TRUE.equals(taskSnapshot.child("isCompleted").getValue(Boolean.class));

                    // Solo agregar tareas no completadas a la lista
                    if (!isCompleted) {
                        Task task = new Task(taskId, plantName, taskDescription, dueDate, imageUrl, isCompleted);
                        taskList.add(task);
                    }
                }
                taskAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PrincipalMenu.this, "Error al cargar las tareas.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onTaskCheckChange(Task task, boolean isChecked) {
        taskRef.child(task.getId()).child("isCompleted").setValue(isChecked)
                .addOnCompleteListener(taskUpdate -> {
                    if (taskUpdate.isSuccessful()) {
                        Toast.makeText(this, "Estado de tarea actualizado", Toast.LENGTH_SHORT).show();

                        // Si la tarea se completó, elimínala de la lista local
                        if (isChecked) {
                            taskList.remove(task);
                            taskAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(this, "Error al actualizar el estado de la tarea", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    // Método para mostrar un diálogo de confirmación antes de cerrar sesión
    private void mostrarConfirmacionSalir() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Estás seguro de que deseas cerrar sesión?")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Cerrar sesión de Firebase
                        FirebaseAuth.getInstance().signOut();
                        // Redirigir a la pantalla de inicio de sesión
                        Intent intent = new Intent(PrincipalMenu.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
    // Método para evitar que la aplicación se cierre al presionar el botón de atrás
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);  // Minimiza la aplicación en lugar de cerrarla
    }
}
