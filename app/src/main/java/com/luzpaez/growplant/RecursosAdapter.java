package com.luzpaez.growplant;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
public class RecursosAdapter extends RecyclerView.Adapter<RecursosAdapter.ViewHolder> {

    private List<String> titles; // Lista de títulos
    private List<String> imageFiles; // Lista de los nombres de los archivos de imagen
    private List<String> fileNames; // Lista de los nombres de los archivos HTML
    private Context context; // Necesitamos el contexto para pasar a la nueva actividad

    // Constructor
    public RecursosAdapter(Context context, List<String> titles, List<String> imageFiles, List<String> fileNames) {
        this.context = context;
        this.titles = titles;
        this.imageFiles = imageFiles;
        this.fileNames = fileNames;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla el layout para cada item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recursos, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String title = titles.get(position);
        holder.titleTextView.setText(title);

        String imageFile = imageFiles.get(position);
        String fileName = fileNames.get(position);

        // Cargar la imagen desde assets
        loadImageFromAssets(holder.imageView, imageFile);

        // Cargar el archivo HTML en WebView
        WebView webView = holder.webView;
        webView.getSettings().setJavaScriptEnabled(true);
        String filePath = "file:///android_asset/recursos/" + fileName;
        webView.loadUrl(filePath);

        // Agregar el clic a la CardView
        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetalleRecursoActivity.class);
            intent.putExtra("fileName", fileName);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    // Método para cargar la imagen desde assets
    private void loadImageFromAssets(ImageView imageView, String imageFile) {
        AssetManager assetManager = context.getAssets();
        try {
            InputStream inputStream = assetManager.open("recursos/" + imageFile);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            imageView.setImageBitmap(bitmap);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ViewHolder para cada item en el RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        ImageView imageView; // ImageView para mostrar la imagen
        WebView webView;
        CardView cardView; // CardView para el clic

        public ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.plantTitle);
            imageView = itemView.findViewById(R.id.plantImage); // Asegúrate de que este ID esté en tu layout
            webView = itemView.findViewById(R.id.plantWebView);
            cardView = itemView.findViewById(R.id.cardViewRecursos);
        }
    }
}
