package com.luzpaez.growplant;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.luzpaez.growplant.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class AgregarPlanta extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private ImageView plantImageView;
    private TextView plantNameTextView;
    private TextView plantDescriptionTextView;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_planta);

        plantImageView = findViewById(R.id.plantImageView);
        plantNameTextView = findViewById(R.id.plantNameTextView);
        plantDescriptionTextView = findViewById(R.id.plantDescriptionTextView);
        Button selectImageButton = findViewById(R.id.selectImageButton);
        Button analyzeButton = findViewById(R.id.analyzeButton);
        Button saveButton = findViewById(R.id.saveButton);
        Button backButton = findViewById(R.id.backButton);

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        analyzeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri != null) {
                    analyzeImage();
                } else {
                    Toast.makeText(AgregarPlanta.this, "Por favor selecciona una imagen primero.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implementar lógica para guardar la planta en Firebase
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Regresar a la actividad anterior
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            plantImageView.setImageURI(imageUri);
        }
    }

    private void analyzeImage() {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();

            //Aqui se agrega la api key

            String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();
            String apiUrl = "https://my-api.plantnet.org/v2/identify/all?api-key=############";

            new Thread(() -> {
                try {
                    URL url = new URL(apiUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                    conn.setDoOutput(true);

                    DataOutputStream request = new DataOutputStream(conn.getOutputStream());
                    request.writeBytes("--" + boundary + "\r\n");
                    request.writeBytes("Content-Disposition: form-data; name=\"images\"; filename=\"image.jpg\"\r\n");
                    request.writeBytes("Content-Type: image/jpeg\r\n\r\n");
                    request.write(byteArray);
                    request.writeBytes("\r\n--" + boundary + "--\r\n");
                    request.flush();
                    request.close();

                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // Leer la respuesta
                        InputStream responseStream = new BufferedInputStream(conn.getInputStream());
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(responseStream));
                        String response = bufferedReader.lines().collect(Collectors.joining("\n"));

                        // Parsear JSON
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray results = jsonResponse.getJSONArray("results");

                        if (results.length() > 0) {
                            JSONObject plantInfo = results.getJSONObject(0);
                            String plantName = plantInfo.getJSONObject("species").getString("scientificNameWithoutAuthor");
                            String plantDescription = plantInfo.getJSONObject("species").getJSONArray("commonNames").getString(0);

                            // Actualizar UI en el hilo principal
                            runOnUiThread(() -> {
                                plantNameTextView.setText(plantName);
                                plantDescriptionTextView.setText(plantDescription);
                            });
                        } else {
                            runOnUiThread(() -> {
                                Toast.makeText(this, "No se encontró información de la planta.", Toast.LENGTH_SHORT).show();
                            });
                        }
                    } else {
                        Log.e("API_ERROR", "Error en la llamada a la API: " + responseCode);
                    }
                } catch (Exception e) {
                    Log.e("API_ERROR", "Excepción: " + e.getMessage());
                }
            }).start();
        } catch (Exception e) {
            Toast.makeText(this, "Error al seleccionar la imagen.", Toast.LENGTH_SHORT).show();
        }
    }






}
