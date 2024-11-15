package com.luzpaez.growplant;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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

        // Enlace para ir a la pantalla de registro
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

        //ANIMACIONES
        // Referencias a los elementos del layout
        ImageView imgLogo = findViewById(R.id.imgLogo);
        TextView textView1 = findViewById(R.id.textView1);
        TextView textView2 = findViewById(R.id.textView2);

        // Animación para el logo (deslizar desde arriba)
        ObjectAnimator logoAnimator = ObjectAnimator.ofFloat(imgLogo, "translationY", -1000f, 0f);
        logoAnimator.setDuration(1000); // Duración de 1 segundo

        // Animación para textView1 (desvanecer)
        ObjectAnimator textView1Animator = ObjectAnimator.ofFloat(textView1, "alpha", 0f, 1f);
        textView1Animator.setDuration(1000); // Duración de 1 segundo

        // Animación para textView2 (deslizar desde abajo)
        ObjectAnimator textView2Animator = ObjectAnimator.ofFloat(textView2, "translationY", 500f, 0f);
        textView2Animator.setDuration(1000); // Duración de 1 segundo

        // Animación para el botón (desvanecer)
        ObjectAnimator btnAnimator = ObjectAnimator.ofFloat(btnLogin, "alpha", 0f, 1f);
        btnAnimator.setDuration(1000); // Duración de 1 segundo

        // Combinamos las animaciones en un AnimatorSet para que se reproduzcan juntas
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(logoAnimator, textView1Animator, textView2Animator, btnAnimator);

        // Iniciamos las animaciones
        animatorSet.start();

    }

    private void loginUser() {
        String email = emailInput.getText().toString();
        String contrasena = contrasenaInput.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(contrasena)) {
            showDialog("Error", "Todos los campos son obligatorios");
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
                        showDialog("Error", "Error al iniciar sesión: " + task.getException().getMessage());
                    }
                });
    }

    private void showDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
