package com.fittrack.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EjercicioAdapter extends RecyclerView.Adapter<EjercicioAdapter.EjercicioViewHolder> {

    private List<String> listaEjercicios;
    private OnEjercicioLongClickListener listener;

    // Interfaz para avisar qué ejercicio fue mantenido pulsado
    public interface OnEjercicioLongClickListener {
        void onEjercicioLongClick(String ejercicio, int position);
    }

    // Constructor
    public EjercicioAdapter(
            List<String> listaEjercicios,
            OnEjercicioLongClickListener listener) {

        this.listaEjercicios = listaEjercicios;
        this.listener = listener;
    }

    public static class EjercicioViewHolder extends RecyclerView.ViewHolder {

        TextView txtItemEjercicio;

        public EjercicioViewHolder(@NonNull View itemView) {
            super(itemView);

            txtItemEjercicio =
                    itemView.findViewById(R.id.txtItemEjercicio);
        }
    }

    @NonNull
    @Override
    public EjercicioViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(
                        R.layout.item_ejercicio,
                        parent,
                        false
                );

        return new EjercicioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull EjercicioViewHolder holder,
            int position) {

        String nombreEjercicio =
                listaEjercicios.get(position);

        holder.txtItemEjercicio.setText(nombreEjercicio);
        // Clic normal: abrir registro de entrenamiento
        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(
                    v.getContext(),
                    RegistrarEntrenamientoActivity.class
            );

            intent.putExtra(
                    "NOMBRE_EJERCICIO",
                    nombreEjercicio
            );

            v.getContext().startActivity(intent);
        });

        // Detectar pulsación larga
        holder.itemView.setOnLongClickListener(v -> {

            int posicionActual =
                    holder.getAdapterPosition();

            if (posicionActual != RecyclerView.NO_POSITION) {

                listener.onEjercicioLongClick(
                        listaEjercicios.get(posicionActual),
                        posicionActual
                );
            }

            return true;
        });
    }

    @Override
    public int getItemCount() {
        return listaEjercicios.size();
    }
}