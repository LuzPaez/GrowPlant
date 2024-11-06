package com.luzpaez.growplant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private final Context context;
    private final ArrayList<Task> tasks;
    private final OnTaskCheckChangeListener onTaskCheckChangeListener;

    public interface OnTaskCheckChangeListener {
        void onTaskCheckChange(Task task, boolean isChecked);
    }

    public TaskAdapter(Context context, ArrayList<Task> tasks, OnTaskCheckChangeListener listener) {
        this.context = context;
        this.tasks = tasks;
        this.onTaskCheckChangeListener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recordatorio, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.bind(task);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvDescripcionTarea;
        private final TextView tvFechaFinalizacion;
        private final ImageView imgRecordatorio;
        private final CheckBox checkTareaCompletada;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDescripcionTarea = itemView.findViewById(R.id.tvDescripcionTarea);
            tvFechaFinalizacion = itemView.findViewById(R.id.tvFechaFinalizacion);
            imgRecordatorio = itemView.findViewById(R.id.imgRecordatorio);
            checkTareaCompletada = itemView.findViewById(R.id.checkTareaCompletada);

            checkTareaCompletada.setOnCheckedChangeListener((buttonView, isChecked) -> {
                Task task = tasks.get(getAdapterPosition());
                onTaskCheckChangeListener.onTaskCheckChange(task, isChecked);
            });
        }

        public void bind(Task task) {
            tvDescripcionTarea.setText(task.getTaskDescription());
            tvFechaFinalizacion.setText(task.getDueDate());
            checkTareaCompletada.setChecked(task.isCompleted());

            Glide.with(context)
                    .load(task.getImageUrl())
                    .placeholder(R.drawable.fotoperfilpordefecto)
                    .into(imgRecordatorio);
        }
    }
}
