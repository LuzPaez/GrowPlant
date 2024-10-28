package com.luzpaez.growplant;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
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

        // Establecer un OnClickListener en el CardView
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un Intent para la actividad DetallesPlanta
                Intent intent = new Intent(context, DetallesPlanta.class);
                // Pasar el objeto Plant al DetallesPlanta
                intent.putExtra("plant_key", (Parcelable) plant); // Asegúrate de que Plant implemente Parcelable o Serializable
                context.startActivity(intent);
            }
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
