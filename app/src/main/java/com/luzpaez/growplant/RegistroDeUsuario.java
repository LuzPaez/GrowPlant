package com.luzpaez.growplant;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistroDeUsuario extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText nombreusuarioInput, emailInput, contrasenaInput, contrasenaInputAgain;
    private CheckBox termsCheckBox;
    private DatabaseReference mDatabase;  // Referencia a la base de datos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_de_usuario);

        // Enlace para ir a la pantalla de inicio de sesión
        TextView linkToLogin = findViewById(R.id.txt_enlace_iniciosesion);
        linkToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegistroDeUsuario.this, LoginActivity.class);
            startActivity(intent);
        });

        // Inicializar Firebase Auth y Firebase Database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();  // Inicializar la referencia a la base de datos

        // Referencias a los campos
        nombreusuarioInput = findViewById(R.id.nombreusuario);
        emailInput = findViewById(R.id.emailInput);
        contrasenaInput = findViewById(R.id.contrasenaInput);
        contrasenaInputAgain = findViewById(R.id.contrasenaInputAgain);
        termsCheckBox = findViewById(R.id.termsCheckBox);

        Button btnRegistrar = findViewById(R.id.btn_button_registrarse);
        btnRegistrar.setOnClickListener(v -> {
            registerUser();
        });
    }

    private void registerUser() {
        String nombreUsuario = nombreusuarioInput.getText().toString();
        String email = emailInput.getText().toString();
        String contrasena = contrasenaInput.getText().toString();
        String confirmarContrasena = contrasenaInputAgain.getText().toString();

        if (TextUtils.isEmpty(nombreUsuario) || TextUtils.isEmpty(email) || TextUtils.isEmpty(contrasena) || TextUtils.isEmpty(confirmarContrasena)) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!contrasena.equals(confirmarContrasena)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!termsCheckBox.isChecked()) {
            Toast.makeText(this, "Debes aceptar los términos y condiciones", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear usuario en Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, contrasena)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // El registro fue exitoso
                        FirebaseUser user = mAuth.getCurrentUser();
                        String userId = user.getUid();

                        // Guardar el nombre de usuario en la base de datos
                        guardarNombreDeUsuario(userId, nombreUsuario);

                        Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                        // Aquí puedes redirigir a otra pantalla si lo deseas
                    } else {
                        Toast.makeText(this, "Error en el registro: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Método para guardar el nombre de usuario en la base de datos
    private void guardarNombreDeUsuario(String userId, String nombreUsuario) {
        mDatabase.child("users").child(userId).child("nombreUsuario").setValue(nombreUsuario)
                .addOnCompleteListener(task -> {  //prueba para ver en en logcat si se esta guardando el usuario en db
                    if (task.isSuccessful()) {
                        Log.d("FirebaseDB", "Nombre de usuario guardado correctamente en la base de datos.");
                    } else {
                        Log.e("FirebaseDB", "Error al guardar en la base de datos: ", task.getException());
                    }
                });

    }


}

