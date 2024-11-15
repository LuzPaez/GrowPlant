package com.luzpaez.growplant;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Configuracion extends AppCompatActivity {

    private TextInputEditText nombreUsuarioInput, passwordInput;
    private AutoCompleteTextView autoCompleteObjetivo;
    private Button btnActualizarDatos, btnGuardarObjetivo;
    private ImageView fotoPerfil;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private StorageReference storageReference;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int STORAGE_PERMISSION_CODE = 100;
    private Uri imageUri;

    private ImageButton regresarButton;
    private ImageView icono;
    private TextView titulo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);

        //animacion
        regresarButton = findViewById(R.id.regresar);
        icono = findViewById(R.id.iconConfig);
        titulo = findViewById(R.id.titulo);

        // Animar el botón de regreso con desvanecimiento
        fadeInButton(regresarButton, 500);  // Desvanecimiento del botón de regreso

        // Animar el ícono y el título con desvanecimiento
        animateElement(icono, 700, 0f, 1f);  // Desvanecimiento del ícono
        animateElement(titulo, 900, 0f, 1f);  // Desvanecimiento del título


        ImageButton btnRegresarPrincipal = findViewById(R.id.regresar);

        // Listener para el botón de regresar a la página principal
        btnRegresarPrincipal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Configuracion.this, PrincipalMenu.class);
                startActivity(intent);
            }
        });

        // Referencias a los elementos en la interfaz
        nombreUsuarioInput = findViewById(R.id.nombreUsuarioInput);
        passwordInput = findViewById(R.id.passwordInput);
        autoCompleteObjetivo = findViewById(R.id.autoCompleteObjetivo);
        btnActualizarDatos = findViewById(R.id.btn_buttonActualizarDatos);
        btnGuardarObjetivo = findViewById(R.id.btn_buttonGuardarObjetivo);
        fotoPerfil = findViewById(R.id.fotoperfil);

        // Inicializar Firebase
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        storageReference = FirebaseStorage.getInstance().getReference("profile_pics");

        // Cargar lista de objetivos en el AutoCompleteTextView
        String[] objetivos = getResources().getStringArray(R.array.objetivos_plantacion);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, objetivos);
        autoCompleteObjetivo.setAdapter(adapter);

        // Cargar la imagen de perfil al iniciar la actividad
        cargarImagenDePerfil();

        // Listener para el botón de actualizar datos
        btnActualizarDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizarDatosUsuario();
            }
        });

        // Listener para guardar el objetivo
        btnGuardarObjetivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarObjetivoCultivo();
            }
        });

        // Listener para la imagen de perfil
        fotoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(Configuracion.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Configuracion.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                } else {
                    abrirGaleria();
                }
            }
        });

        Button btnEliminarCuenta = findViewById(R.id.btn_EliminarCuenta);
        btnEliminarCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminarCuenta();
            }
        });

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
    // Método para cargar la imagen de perfil desde Firebase
    private void cargarImagenDePerfil() {
        String userId = auth.getCurrentUser().getUid();
        databaseReference.child(userId).child("fotoPerfilUrl").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String imageUrl = task.getResult().getValue(String.class);
                if (imageUrl != null) {
                    // Usar Glide para cargar la imagen desde la URL
                    Glide.with(this)
                            .load(imageUrl)
                            .placeholder(R.drawable.fotoperfilpordefecto) // Imagen por defecto
                            .into(fotoPerfil);
                }
            } else {
                Toast.makeText(Configuracion.this, "Error al cargar la imagen de perfil", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Abrir la galería
    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            fotoPerfil.setImageURI(imageUri);
            cargarImagenEnFirebase();
        }
    }

    // Cargar imagen en Firebase Storage
    private void cargarImagenEnFirebase() {
        if (imageUri != null) {
            String userId = auth.getCurrentUser().getUid();
            StorageReference fileReference = storageReference.child(userId + ".jpg");
            fileReference.putFile(imageUri)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                fileReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()) {
                                            String downloadUri = task.getResult().toString();
                                            // Guardar la URL en la base de datos
                                            databaseReference.child(userId).child("fotoPerfilUrl").setValue(downloadUri)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(Configuracion.this, "Imagen de perfil actualizada", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                Toast.makeText(Configuracion.this, "Error al guardar la URL de la imagen", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(Configuracion.this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    // Método para actualizar el nombre de usuario y la contraseña en Firebase
    private void actualizarDatosUsuario() {
        String nuevoNombre = nombreUsuarioInput.getText().toString().trim();
        String nuevaContrasena = passwordInput.getText().toString().trim();
        String userId = auth.getCurrentUser().getUid();

        // Verificar si el nombre de usuario está vacío
        if (nuevoNombre.isEmpty()) {
            nombreUsuarioInput.setError("El nombre de usuario no puede estar vacío");

            // Mostrar un AlertDialog
            mostrarAlerta("Por favor, completa todos los campos", "El campo del nombre de usuario no debe estar vacío.");
            return; // Evita que continúe si el nombre está vacío
        } else {
            databaseReference.child(userId).child("nombreUsuario").setValue(nuevoNombre)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(Configuracion.this, "Nombre de usuario actualizado", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Configuracion.this, "Error al actualizar el nombre de usuario", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        // Verificar si la contraseña está vacía
        if (nuevaContrasena.isEmpty()) {
            passwordInput.setError("La contraseña no puede estar vacía");

            // Mostrar un AlertDialog
            mostrarAlerta("Por favor, completa todos los campos", "El campo de la contraseña no debe estar vacío.");
            return; // Evita que continúe si la contraseña está vacía
        } else {
            auth.getCurrentUser().updatePassword(nuevaContrasena)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(Configuracion.this, "Contraseña actualizada", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Configuracion.this, "Error al actualizar contraseña", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    // Método para guardar el objetivo de cultivo en Firebase
    private void guardarObjetivoCultivo() {
        String objetivoSeleccionado = autoCompleteObjetivo.getText().toString().trim();
        String userId = auth.getCurrentUser().getUid();

        // Verificar si el objetivo está vacío
        if (objetivoSeleccionado.isEmpty()) {
            autoCompleteObjetivo.setError("Selecciona un objetivo de cultivo");

            // Mostrar un AlertDialog
            mostrarAlerta("Por favor, completa todos los campos", "Debes seleccionar un objetivo de cultivo.");
            return; // Evita que continúe si el objetivo está vacío
        } else {
            databaseReference.child(userId).child("objetivoCultivo").setValue(objetivoSeleccionado)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(Configuracion.this, "Objetivo guardado", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Configuracion.this, "Error al guardar objetivo", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    // Método para mostrar un AlertDialog
    private void mostrarAlerta(String titulo, String mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Configuracion.this);
        builder.setTitle(titulo);
        builder.setMessage(mensaje);
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        AlertDialog alert = builder.create();
        alert.show();
    }


    private void eliminarCuenta() {
        // Mostrar un diálogo de confirmación
        new AlertDialog.Builder(Configuracion.this)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Estás seguro de que quieres eliminar tu cuenta? Esta acción no se puede deshacer.")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Si el usuario confirma, procedemos a eliminar la cuenta

                        String userId = auth.getCurrentUser().getUid();

                        // Eliminar datos de usuario en Firebase Database (nodo 'users')
                        databaseReference.child(userId).removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("FirebaseDB", "Datos de usuario eliminados correctamente.");
                                        } else {
                                            Log.e("FirebaseDB", "Error al eliminar los datos de usuario: ", task.getException());
                                        }
                                    }
                                });

                        // Eliminar las plantas del usuario en el nodo 'plants'
                        DatabaseReference plantsReference = FirebaseDatabase.getInstance().getReference("plants").child(userId);
                        plantsReference.removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("FirebaseDB", "Plantas del usuario eliminadas correctamente.");
                                        } else {
                                            Log.e("FirebaseDB", "Error al eliminar las plantas del usuario: ", task.getException());
                                        }
                                    }
                                });

                        // Eliminar recordatorios del usuario en el nodo 'recordatorios'
                        DatabaseReference recordatoriosReference = FirebaseDatabase.getInstance().getReference("recordatorios").child(userId);
                        recordatoriosReference.removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("FirebaseDB", "Recordatorios del usuario eliminados correctamente.");
                                        } else {
                                            Log.e("FirebaseDB", "Error al eliminar los recordatorios del usuario: ", task.getException());
                                        }
                                    }
                                });

                        // Eliminar imagen de perfil en Firebase Storage
                        StorageReference imageRef = FirebaseStorage.getInstance().getReference("profile_pics").child(userId + ".jpg");
                        imageRef.delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("FirebaseStorage", "Imagen de perfil eliminada correctamente.");
                                        } else {
                                            Log.e("FirebaseStorage", "Error al eliminar la imagen de perfil: ", task.getException());
                                        }
                                    }
                                });

                        // Eliminar la cuenta del usuario en Firebase Authentication
                        auth.getCurrentUser().delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("FirebaseAuth", "Cuenta eliminada correctamente.");
                                            // Mostrar el diálogo de agradecimiento
                                            new AlertDialog.Builder(Configuracion.this)
                                                    .setTitle("¡Gracias!")
                                                    .setMessage("Gracias por usar nuestra aplicación. Tu cuenta ha sido eliminada.")
                                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            // Redirigir al login o a una actividad final
                                                            Intent intent = new Intent(Configuracion.this, LoginActivity.class);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    })
                                                    .show();
                                        } else {
                                            Log.e("FirebaseAuth", "Error al eliminar la cuenta: ", task.getException());
                                        }
                                    }
                                });
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }


}
