package com.luzpaez.growplant;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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

        // Enlace para ir a la pantalla de política de privacidad
        TextView linkToPolitic = findViewById(R.id.termsTextView);
        linkToPolitic.setOnClickListener(v -> {
            Intent intent = new Intent(RegistroDeUsuario.this, Politica.class);
            startActivity(intent);
        });

        TextView termsTextView = findViewById(R.id.termsTextView);
        termsTextView.setPaintFlags(termsTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

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
            showDialog("Error", "Todos los campos son obligatorios");
            return;
        }

        if (!contrasena.equals(confirmarContrasena)) {
            showDialog("Error", "Las contraseñas no coinciden");
            return;
        }

        if (!termsCheckBox.isChecked()) {
            showDialog("Error", "Debes aceptar los términos y condiciones");
            return;
        }

        if (!isPasswordValid(contrasena)) {
            showDialog("Error", "La contraseña debe tener al menos 8 caracteres, incluyendo una mayúscula, una minúscula, un número y un carácter especial.");
            return;
        }

        // Crear usuario en Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, contrasena)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        String userId = user.getUid();
                        guardarNombreDeUsuario(userId, nombreUsuario);
                        showDialog("Éxito", "Registro exitoso");
                        Intent intent = new Intent(RegistroDeUsuario.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        showDialog("Error", "Error en el registro: " + task.getException().getMessage());
                    }
                });
    }

    private boolean isPasswordValid(String password) {
        String passwordPattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        return password.matches(passwordPattern);
    }

    private void showDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Aceptar", null)
                .show();
    }

    private void guardarNombreDeUsuario(String userId, String nombreUsuario) {
        mDatabase.child("users").child(userId).child("nombreUsuario").setValue(nombreUsuario)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("FirebaseDB", "Nombre de usuario guardado correctamente en la base de datos.");
                    } else {
                        Log.e("FirebaseDB", "Error al guardar en la base de datos: ", task.getException());
                    }
                });
    }
}
