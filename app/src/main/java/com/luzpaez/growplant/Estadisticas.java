package com.luzpaez.growplant;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Estadisticas extends AppCompatActivity {

    private PieChart pieChart;
    private TextView tvSelectedFamily;
    private DatabaseReference plantsReference;
    private BarChart barChart; // Declarar el BarChart
    private Map<String, Integer> familyCount; // Mapa para contar por familia
    private Map<String, Integer> dateCount; // Mapa para contar por fecha
    private List<String> dateLabels; // Lista para almacenar las etiquetas de las fechas

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas);

        ImageButton btnRegresarPrincipal = findViewById(R.id.regresar);

        // Listener para el botón de regresar a la página principal
        btnRegresarPrincipal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Estadisticas.this, PrincipalMenu.class);
                startActivity(intent);
            }
        });


        // Inicializa los elementos de la interfaz
        pieChart = findViewById(R.id.chart1);
        tvSelectedFamily = findViewById(R.id.tvSelectedFamily);
        plantsReference = FirebaseDatabase.getInstance().getReference("plants");
        barChart = findViewById(R.id.barChart); // Inicialización del gráfico de barras

        // Cargar los datos de las familias de plantas
        cargarDatosFamilias();
    }

    private void cargarDatosFamilias() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            tvSelectedFamily.setText("Usuario no autenticado");
            return;
        }

        String userId = currentUser.getUid();
        DatabaseReference userPlantsReference = plantsReference.child(userId);

        userPlantsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                familyCount = new HashMap<>(); // Inicializa el mapa para contar familias
                dateCount = new HashMap<>(); // Mapa para contar por fecha

                for (DataSnapshot plantSnapshot : dataSnapshot.getChildren()) {
                    String family = plantSnapshot.child("family").getValue(String.class);
                    String date = plantSnapshot.child("date").getValue(String.class);
                    Integer quantity = plantSnapshot.child("quantity").getValue(Integer.class);

                    // Contar por familia
                    if (family != null && quantity != null) {
                        familyCount.put(family, familyCount.getOrDefault(family, 0) + quantity);
                    }

                    // Contar por fecha
                    if (date != null && quantity != null) {
                        dateCount.put(date, dateCount.getOrDefault(date, 0) + quantity);
                    }
                }

                // Crear datos para el gráfico de pastel
                List<PieEntry> pieEntries = new ArrayList<>();

                for (Map.Entry<String, Integer> entry : familyCount.entrySet()) {
                    if (entry.getValue() > 0) {
                        pieEntries.add(new PieEntry(entry.getValue(), entry.getKey()));
                    }
                }

                // Gráfico de pastel
                if (!pieEntries.isEmpty()) {
                    PieDataSet dataSet = new PieDataSet(pieEntries, "Familias de Plantas");
                    dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                    dataSet.setValueTextColor(Color.WHITE);

                    // Configuración del ValueFormatter para mostrar valores reales
                    dataSet.setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getFormattedValue(float value) {
                            return String.valueOf((int) value); // Convertir a entero
                        }
                    });

                    PieData data = new PieData(dataSet);
                    pieChart.setUsePercentValues(false); // Cambiar a false para mostrar valores reales
                    pieChart.getDescription().setEnabled(false);
                    pieChart.setData(data);
                    pieChart.invalidate(); // Actualiza el gráfico

                    // Listener para la selección de valores
                    pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                        @Override
                        public void onValueSelected(Entry e, Highlight h) {
                            PieEntry pieEntry = (PieEntry) e;
                            String selectedFamily = pieEntry.getLabel();
                            int quantity = (int) pieEntry.getValue();
                            tvSelectedFamily.setText("Familia: " + selectedFamily + ", Cantidad: " + quantity);
                        }

                        @Override
                        public void onNothingSelected() {
                            tvSelectedFamily.setText("Familia Seleccionada");
                        }
                    });
                } else {
                    tvSelectedFamily.setText("No hay datos de familias");
                    pieChart.clear();
                }

                // Gráfico de barras
                if (!dateCount.isEmpty()) {
                    List<BarEntry> barEntries = new ArrayList<>();
                    List<String> dates = new ArrayList<>(); // Lista para almacenar las fechas
                    int index = 0;

                    for (Map.Entry<String, Integer> entry : dateCount.entrySet()) {
                        barEntries.add(new BarEntry(index, entry.getValue())); // Usar el índice para el gráfico de barras
                        dates.add(entry.getKey()); // Almacenar la fecha
                        index++;
                    }

                    BarDataSet barDataSet = new BarDataSet(barEntries, "Cantidad de Plantas por Fecha de Registro");
                    barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                    BarData barData = new BarData(barDataSet);
                    barChart.setData(barData);
                    barChart.getDescription().setText("Cantidad de Plantas por Fecha");
                    barChart.getDescription().setTextColor(Color.BLACK);
                    barChart.invalidate(); // Actualizar gráfico de barras

                    // Configurar etiquetas del eje X
                    XAxis xAxis = barChart.getXAxis();
                    xAxis.setValueFormatter(new IndexAxisValueFormatter(dates)); // Usar las fechas como etiquetas
                    xAxis.setGranularity(1f); // Espaciado uniforme
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // Posición de las etiquetas
                } else {
                    Toast.makeText(Estadisticas.this, "No hay datos para el gráfico de barras", Toast.LENGTH_SHORT).show();
                    barChart.clear();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                tvSelectedFamily.setText("Error al cargar datos: " + databaseError.getMessage());
            }
        });
    }

    private int getColorForIndex(int index) {
        // Devuelve un color diferente para cada índice
        switch (index) {
            case 0: return Color.RED;
            case 1: return Color.GREEN;
            case 2: return Color.BLUE;
            case 3: return Color.YELLOW;
            case 4: return Color.CYAN;
            case 5: return Color.MAGENTA;
            default: return Color.LTGRAY;
        }
    }
}
