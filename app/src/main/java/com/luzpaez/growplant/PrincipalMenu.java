package com.luzpaez.growplant;

import static com.luzpaez.growplant.R.id.IrRegistroPlantasHome;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class PrincipalMenu extends AppCompatActivity {

    private ImageView fotoperfil;
    private TextView nombreHuertero;
    private ImageButton btnSalir;

    private FirebaseAuth auth;
    private DatabaseReference userRef;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal_menu);

        // Inicializar Firebase
        auth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("users").child(auth.getCurrentUser().getUid());
        storageRef = FirebaseStorage.getInstance().getReference();

        // Inicializar vistas
        fotoperfil = findViewById(R.id.fotoperfil);
        nombreHuertero = findViewById(R.id.nombreHuertero);
        btnSalir = findViewById(R.id.SalirApp);

        // Actualizar imagen y nombre de usuario
        cargarDatosUsuario();

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

        // Botón para redirigir a la actividad de Recordatorio_(Cambiar IrRecursos cuando se cree, IrRecordatorios)
        CardView cardRecordatorio = findViewById(R.id.IrRecursos);
        cardRecordatorio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrincipalMenu.this, RecordatoriosActivity.class);
                startActivity(intent);
            }
        });
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
