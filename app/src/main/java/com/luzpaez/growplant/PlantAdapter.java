package com.luzpaez.growplant;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class PlantAdapter extends RecyclerView.Adapter<PlantAdapter.PlantViewHolder> {
    private Context context;
    private List<Plant> plantList;
    private List<Plant> originalPlantList; // Lista principal para restaurar datos

    public PlantAdapter(Context context, List<Plant> plantList, List<Plant> originalPlantList) {
        this.context = context;
        this.plantList = plantList;
        this.originalPlantList = originalPlantList;
    }


    // Método de filtrado
    public void filter(String text) {
        plantList.clear(); // Limpiar plantList para aplicar el filtro
        if (text.isEmpty()) {
            plantList.addAll(originalPlantList); // Restaurar todos los datos si no hay texto
        } else {
            for (Plant plant : originalPlantList) {
                if (plant.getDescription().toLowerCase().contains(text.toLowerCase())) {
                    plantList.add(plant);
                }
            }
        }

        Log.d("PlantAdapter", "Filtered list size: " + plantList.size());
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_plant, parent, false);
        return new PlantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlantViewHolder holder, int position) {
        Plant plant = plantList.get(position);
        holder.tvFecha.setText(plant.getDate());
        holder.tvNombre.setText(plant.getDescription());
        holder.tvFamilia.setText(plant.getFamily());

        Glide.with(context)
                .load(plant.getImageUrl())
                .into(holder.ivPlanta);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetallesPlanta.class);
            intent.putExtra("plant_key", (Parcelable) plant);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return plantList.size();
    }

    public static class PlantViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPlanta;
        TextView tvFecha, tvNombre, tvFamilia;

        public PlantViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPlanta = itemView.findViewById(R.id.iv_planta);
            tvFecha = itemView.findViewById(R.id.tv_fecha);
            tvNombre = itemView.findViewById(R.id.tv_nombre);
            tvFamilia = itemView.findViewById(R.id.tv_familia);
        }
    }
}
