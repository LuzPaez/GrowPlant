package com.luzpaez.growplant;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput, contrasenaInput;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Enlace para ir a la pantalla de inicio de sesión
        TextView linkToLogin = findViewById(R.id.txt_enlace_registro);
        linkToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistroDeUsuario.class);
            startActivity(intent);
        });

        mAuth = FirebaseAuth.getInstance(); // Inicializar FirebaseAuth

        emailInput = findViewById(R.id.emailInput);
        contrasenaInput = findViewById(R.id.contrasenaInputLogin);
        Button btnLogin = findViewById(R.id.btn_buttonLogin);
        btnLogin.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {
        String email = emailInput.getText().toString();
        String contrasena = contrasenaInput.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(contrasena)) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

// Iniciar sesión en Firebase
        mAuth.signInWithEmailAndPassword(email, contrasena)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Redirigir a la actividad principal
                            Intent intent = new Intent(LoginActivity.this, PrincipalMenu.class);
                            startActivity(intent); // Inicia la actividad
                            finish(); // Cierra la actividad de login
                        }
                    } else {
                        Toast.makeText(this, "Error al iniciar sesión: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
}
