package com.luzpaez.growplant;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;


public class AgregarPlanta extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private ImageView plantImageView;
    private TextView plantNameTextView;
    private TextView plantDescriptionTextView;
    private TextView plantFamilyTextView;
    private Uri imageUri;
    private EditText plantDateEditText;
    private EditText plantQuantityEditText;

    // Firebase variables
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_planta);

        plantImageView = findViewById(R.id.plantImageView);
        plantNameTextView = findViewById(R.id.plantNameTextView);
        plantDescriptionTextView = findViewById(R.id.plantDescriptionTextView);
        plantFamilyTextView = findViewById(R.id.plantFamilyTextView);

        plantDateEditText = findViewById(R.id.plantDateEditText);
        plantQuantityEditText = findViewById(R.id.plantQuantityEditText);

        Button selectImageButton = findViewById(R.id.selectImageButton);
        Button analyzeButton = findViewById(R.id.analyzeButton);
        Button saveButton = findViewById(R.id.saveButton);
        Button backButton = findViewById(R.id.backButton);

        // Inicializar Firebase
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("plants");
        storageReference = FirebaseStorage.getInstance().getReference("plant_images");

        // Obtiene la fecha actual y la establece en el EditText
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());
        plantDateEditText.setText(currentDate);

        selectImageButton.setOnClickListener(v -> openGallery());

        analyzeButton.setOnClickListener(v -> {
            if (imageUri != null) {
                analyzeImage();
            } else {
                Toast.makeText(AgregarPlanta.this, "Por favor selecciona una imagen primero.", Toast.LENGTH_SHORT).show();
            }
        });

        saveButton.setOnClickListener(v -> savePlantData()); // Llamar al método para guardar datos

        backButton.setOnClickListener(v -> finish()); // Regresar a la actividad anterior
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

            // Aquí se agrega la api key

            String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();
            String apiUrl = "https://my-api.plantnet.org/v2/identify/all?include-related-images=false&no-reject=false&nb-results=10&lang=es&api-key=################";

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
                            String plantName = plantInfo.getJSONObject("species").getString("scientificNameWithoutAuthor"); // nombre cientifico
                            String plantDescription = plantInfo.getJSONObject("species").getJSONArray("commonNames").getString(0); // nombre común
                            String plantFamily = plantInfo.getJSONObject("species").getJSONObject("family").getString("scientificNameWithoutAuthor"); // Obtener el nombre de la familia

                            // Actualizar UI en el hilo principal
                            runOnUiThread(() -> {
                                plantNameTextView.setText(plantName);
                                plantDescriptionTextView.setText(plantDescription);
                                plantFamilyTextView.setText(plantFamily);
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

    private void savePlantData() {
        String plantName = plantNameTextView.getText().toString();
        String plantDescription = plantDescriptionTextView.getText().toString();
        String plantFamily = plantFamilyTextView.getText().toString();
        String plantDate = plantDateEditText.getText().toString();
        String plantQuantity = plantQuantityEditText.getText().toString();

        if (plantName.isEmpty() || plantDescription.isEmpty() || plantFamily.isEmpty() || plantDate.isEmpty() || plantQuantity.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener el ID del usuario autenticado
        String userId = auth.getCurrentUser().getUid();
        String plantId = databaseReference.push().getKey();

        // Guardar imagen en Firebase Storage
        StorageReference plantImageRef = storageReference.child(userId).child(plantId + ".jpg");
        plantImageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            // Obtener la URL de descarga de la imagen
            plantImageRef.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
                // Después de guardar la imagen, guardar los datos en la base de datos
                Map<String, Object> plantData = new HashMap<>();
                plantData.put("name", plantName);
                plantData.put("description", plantDescription);
                plantData.put("family", plantFamily);
                plantData.put("date", plantDate);
                plantData.put("quantity", plantQuantity);
                plantData.put("image_url", downloadUrl.toString());

                // Guardar datos en la base de datos
                databaseReference.child(userId).child(plantId).setValue(plantData).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(AgregarPlanta.this, "Planta guardada exitosamente.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AgregarPlanta.this, "Error al guardar la planta.", Toast.LENGTH_SHORT).show();
                    }
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(AgregarPlanta.this, "Error al obtener la URL de la imagen.", Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(AgregarPlanta.this, "Error al subir la imagen.", Toast.LENGTH_SHORT).show();
        });
    }
}
