package com.luzpaez.growplant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class PlantAdapter2 extends RecyclerView.Adapter<PlantAdapter2.PlantViewHolder> {

    private final Context context;
    private final List<Plant> plantList;
    private final OnPlantClickListener onPlantClickListener; // Interfaz para manejar clics

    public interface OnPlantClickListener {
        void onPlantClick(Plant plant);
    }

    public PlantAdapter2(Context context, List<Plant> plantList, OnPlantClickListener listener) {
        this.context = context;
        this.plantList = plantList;
        this.onPlantClickListener = listener; // Asignar el listener
    }

    @NonNull
    @Override
    public PlantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_plant2, parent, false);
        return new PlantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlantViewHolder holder, int position) {
        Plant plant = plantList.get(position);
        holder.tvPlantName.setText(plant.getDescription()); //getDescription(nombreComun), getName(NombreCientifico)

        // Cargar la imagen desde la URL con Glide
        Glide.with(context)
                .load(plant.getImageUrl())
                .into(holder.ivPlantImage);

        // Configuración el clic en el elemento de la planta
        holder.itemView.setOnClickListener(v -> {
            onPlantClickListener.onPlantClick(plant); // Llama al método del listener
        });
    }

    @Override
    public int getItemCount() {
        return plantList.size();
    }

    public static class PlantViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPlantImage;
        TextView tvPlantName;

        public PlantViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPlantImage = itemView.findViewById(R.id.imageViewPlant);
            tvPlantName = itemView.findViewById(R.id.textViewPlantName);
        }
    }
}
