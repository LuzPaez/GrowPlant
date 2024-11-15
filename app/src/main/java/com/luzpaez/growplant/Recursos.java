package com.luzpaez.growplant;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import java.util.ArrayList;
import java.util.List;

public class Recursos extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecursosAdapter recursosAdapter;
    private List<String> titles;
    private List<String> fileNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recursos);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        ImageButton btnRegresarPrincipal = findViewById(R.id.regresar);

        // Listener para el botón de regresar a la página principal
        btnRegresarPrincipal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Recursos.this, PrincipalMenu.class);
                startActivity(intent);
            }
        });

        List<String> titles = new ArrayList<>();
        List<String> imageFiles = new ArrayList<>();
        List<String> fileNames = new ArrayList<>();


        // Inicializar listas de títulos y nombres de archivos HTML
        titles = new ArrayList<>();
        imageFiles.add("img/im1.jpeg");
        fileNames = new ArrayList<>();

        // Agregar los datos: títulos, imagenes y nombres de archivos HTML
        titles.add("Capítulo I: Aspectos Generales de la Agricultura Urbana");
        imageFiles.add("img/im3.jpeg");
        fileNames.add("capitulo1_argriculturaurbana.html");

        titles.add("Capítulo II: Propagación de Plantas");
        imageFiles.add("img/im5.jpeg");
        fileNames.add("capitulo2_Propagación_de_Plantas.html");

        titles.add("El Suelo y su Fertilidad");
        imageFiles.add("img/im7.jpeg");
        fileNames.add("capitulo3_Suelo_y_su _Fertilidad.html");

        titles.add("Fitosanidad de la Huerta");
        imageFiles.add("img/im9.jpeg");
        fileNames.add("capitulo4_fitosanidad_huerta.html");

        titles.add("Capítulo V: Cosecha y Poscosecha");
        imageFiles.add("img/im11.jpeg");
        fileNames.add("capitulo5_cosecha_y_poscosecha.html");


        // Configurar el RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recursosAdapter = new RecursosAdapter(this, titles, imageFiles, fileNames);
        recyclerView.setAdapter(recursosAdapter);

        // Establecer un ItemDecoration para agregar espacio entre los ítems
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);

                // Establecer márgenes: espacio de 10dp entre cada ítem
                outRect.top = 10;  // Espacio superior
                outRect.bottom = 10;  // Espacio inferior
                outRect.left = 20;  // Espacio izquierdo
                outRect.right = 20;  // Espacio derecho
            }
        });

    }
}