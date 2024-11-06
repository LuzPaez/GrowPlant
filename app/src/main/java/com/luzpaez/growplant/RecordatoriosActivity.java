package com.luzpaez.growplant;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import java.util.Calendar;

public class RecordatoriosActivity extends AppCompatActivity implements PlantAdapter2.OnPlantClickListener, TaskAdapter.OnTaskCheckChangeListener {
    private RecyclerView rvSeleccionarImagenPlanta, rvTareas,rvTareasCompletadas;
    private PlantAdapter2 plantAdapter;
    private TaskAdapter taskAdapter;
    private ArrayList<Plant> plantList = new ArrayList<>();
    private ArrayList<Task> taskList = new ArrayList<>();
    private DatabaseReference databaseReference;
    private ArrayList<Task> completedTaskList = new ArrayList<>();
    private TaskAdapter completedTaskAdapter;
    private FloatingActionButton fabDeletePlants;


    private EditText etDescripcionTarea;
    private Button btnSeleccionarFecha, btnGuardarRecordatorio;
    private TextView tvFechaSeleccionada;
    private String fechaSeleccionada;

    private Plant plantaSeleccionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordatorios);

        // Inicializar la referencia a la base de datos
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference("plants").child(userId);
        } else {
            Toast.makeText(this, "Error: Usuario no autenticado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Inicialización de vistas
        rvSeleccionarImagenPlanta = findViewById(R.id.rvSeleccionarImagenPlanta);
        rvTareas = findViewById(R.id.rvListaTareas);

        rvTareasCompletadas = findViewById(R.id.rvTareasCompletadas);
        completedTaskAdapter = new TaskAdapter(this, completedTaskList, this);

        rvTareasCompletadas.setLayoutManager(new LinearLayoutManager(this));
        rvTareasCompletadas.setAdapter(completedTaskAdapter);

        etDescripcionTarea = findViewById(R.id.etDescripcionTarea);
        btnSeleccionarFecha = findViewById(R.id.btnSeleccionarFecha);
        btnGuardarRecordatorio = findViewById(R.id.btnGuardarRecordatorio);
        tvFechaSeleccionada = findViewById(R.id.tvFechaSeleccionada);

        // RecyclerView para seleccionar imagen de planta
        plantAdapter = new PlantAdapter2(this, plantList, this);
        rvSeleccionarImagenPlanta.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvSeleccionarImagenPlanta.setAdapter(plantAdapter);

        // RecyclerView para mostrar las tareas/task
        taskAdapter = new TaskAdapter(this, taskList, this); // Usar el nuevo adaptador
        rvTareas.setLayoutManager(new LinearLayoutManager(this));
        rvTareas.setAdapter(taskAdapter);

        loadPlantData(); // Cargar datos de plantas
        loadTasks(); // Cargar datos de tareas

        // Configurar botones
        btnSeleccionarFecha.setOnClickListener(v -> seleccionarFecha());
        btnGuardarRecordatorio.setOnClickListener(v -> guardarRecordatorio());
        fabDeletePlants = findViewById(R.id.fab_delete_plant);

        // Configurar el listener para el botón
        fabDeletePlants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCompletedTasks();
            }
        });
    }





    @Override
    public void onPlantClick(Plant plant) {
        plantaSeleccionada = plant;
        Toast.makeText(this, "Planta seleccionada: " + plant.getName(), Toast.LENGTH_SHORT).show();
    }

    private void loadPlantData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                plantList.clear();
                for (DataSnapshot plantSnapshot : snapshot.getChildren()) {
                    String plantId = plantSnapshot.getKey();
                    String date = plantSnapshot.child("date").getValue(String.class);
                    String description = plantSnapshot.child("description").getValue(String.class);
                    String family = plantSnapshot.child("family").getValue(String.class);
                    String imageUrl = plantSnapshot.child("image_url").getValue(String.class);
                    String name = plantSnapshot.child("name").getValue(String.class);
                    Integer quantity = plantSnapshot.child("quantity").getValue(Integer.class);

                    if (quantity == null) {
                        quantity = 0; // Valor por defecto
                    }

                    Plant plant = new Plant(plantId, date, description, family, imageUrl, name, quantity);
                    plantList.add(plant);
                }
                plantAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RecordatoriosActivity.this, "Error al cargar las plantas.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadTasks() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference tasksRef = FirebaseDatabase.getInstance().getReference("recordatorios").child(userId).child("tasks");

        tasksRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                taskList.clear(); // Limpia la lista de tareas
                completedTaskList.clear(); // Limpia la lista de tareas completadas
                for (DataSnapshot reminderSnapshot : snapshot.getChildren()) {
                    String reminderId = reminderSnapshot.getKey();
                    String plantName = reminderSnapshot.child("plantName").getValue(String.class);
                    String taskDescription = reminderSnapshot.child("taskDescription").getValue(String.class);
                    String dueDate = reminderSnapshot.child("dueDate").getValue(String.class);
                    String imageUrl = reminderSnapshot.child("imageUrl").getValue(String.class);
                    boolean isCompleted = Boolean.TRUE.equals(reminderSnapshot.child("isCompleted").getValue(Boolean.class));

                    // Crear un nuevo objeto Task
                    Task task = new Task(reminderId, plantName, taskDescription, dueDate, imageUrl, isCompleted);

                    if (isCompleted) {
                        completedTaskList.add(task); // Agregar a la lista de tareas completadas
                    } else {
                        taskList.add(task); // Agregar a la lista de tareas activas
                    }
                }
                taskAdapter.notifyDataSetChanged(); // Notificar al adaptador que los datos han cambiado
                completedTaskAdapter.notifyDataSetChanged(); // Notificar al adaptador de tareas completadas
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RecordatoriosActivity.this, "Error al cargar las tareas.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void seleccionarFecha() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    fechaSeleccionada = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    tvFechaSeleccionada.setText(fechaSeleccionada);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void guardarRecordatorio() {
        String descripcion = etDescripcionTarea.getText().toString().trim();
        if (descripcion.isEmpty() || fechaSeleccionada == null || plantaSeleccionada == null) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear un objeto de tarea
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String taskId = FirebaseDatabase.getInstance().getReference("recordatorios").child(userId).child("tasks").push().getKey(); // Generar un ID único en el nodo de recordatorios
        String plantName = plantaSeleccionada.getName(); // Obtener el nombre de la planta seleccionada
        String imageUrl = plantaSeleccionada.getImageUrl(); // Obtener la URL de la imagen de la planta

        Task newTask = new Task(taskId, plantName, descripcion, fechaSeleccionada, imageUrl, false);

        // Guardar en Firebase en el nodo de recordatorios
        FirebaseDatabase.getInstance().getReference("recordatorios").child(userId).child("tasks").child(taskId).setValue(newTask)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Recordatorio guardado", Toast.LENGTH_SHORT).show();
                        etDescripcionTarea.setText("");
                        fechaSeleccionada = null;
                        tvFechaSeleccionada.setText("");
                        plantaSeleccionada = null; // Reiniciar planta seleccionada
                        loadTasks(); // Recargar tareas
                    } else {
                        Toast.makeText(this, "Error al guardar el recordatorio", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private boolean isToastShown = false;

    @Override
    public void onTaskCheckChange(Task task, boolean isChecked) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference taskRef = FirebaseDatabase.getInstance().getReference("recordatorios").child(userId).child("tasks").child(task.getId());

        taskRef.child("isCompleted").setValue(isChecked) // Actualiza el estado de la tarea
                .addOnCompleteListener(taskUpdate -> {
                    if (taskUpdate.isSuccessful()) {
                        if (!isToastShown) {
                            Toast.makeText(RecordatoriosActivity.this, "Estado de tarea actualizado", Toast.LENGTH_SHORT).show();
                            isToastShown = true; // Evitar que se muestre de nuevo

                            // Restablecer isToastShown después de 2 segundos
                            new android.os.Handler().postDelayed(() -> isToastShown = false, 2000);
                        }
                        loadTasks(); // Recargar las tareas para actualizar ambas listas
                    } else {
                        Toast.makeText(RecordatoriosActivity.this, "Error al actualizar el estado de la tarea", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void deleteCompletedTasks() {
        // Diálogo de confirmación
        new AlertDialog.Builder(this)
                .setTitle("Eliminar tareas completadas")
                .setMessage("¿Estás seguro de que deseas eliminar todas las tareas completadas?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference tasksRef = FirebaseDatabase.getInstance().getReference("recordatorios").child(userId).child("tasks");

                    // Recorre la lista de tareas completadas y elimina cada tarea de Firebase
                    for (Task task : completedTaskList) {
                        tasksRef.child(task.getId()).removeValue();
                    }

                    // Limpiar la lista de tareas completadas
                    completedTaskList.clear();
                    completedTaskAdapter.notifyDataSetChanged(); // Notificar al adaptador que la lista se ha actualizado

                    Toast.makeText(this, "Tareas completadas eliminadas", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", (dialog, which) -> {
                    dialog.dismiss(); // Cierra el diálogo si se cancela
                })
                .show(); // Muestra el diálogo
    }


}
