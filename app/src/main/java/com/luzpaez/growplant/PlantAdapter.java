package com.luzpaez.growplant;

// PlantAdapter.java
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

public class PlantAdapter extends RecyclerView.Adapter<PlantAdapter.PlantViewHolder> {

    private Context context;
    private List<Plant> plantList;

    public PlantAdapter(Context context, List<Plant> plantList) {
        this.context = context;
        this.plantList = plantList;
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

        // Cargar la imagen desde la URL con Glide
        Glide.with(context)
                .load(plant.getImageUrl())
                .into(holder.ivPlanta);
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
