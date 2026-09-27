package com.fittrack.app;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RutinaAdapter extends RecyclerView.Adapter<RutinaAdapter.RutinaViewHolder> {

    private List<String> listaRutinas;
    private OnRutinaLongClickListener listener;

    // Interfaz para avisar qué rutina se mantuvo pulsada
    public interface OnRutinaLongClickListener {
        void onRutinaLongClick(String rutina, int position);
    }

    // Constructor
    public RutinaAdapter(
            List<String> listaRutinas,
            OnRutinaLongClickListener listener) {

        this.listaRutinas = listaRutinas;
        this.listener = listener;
    }

    public static class RutinaViewHolder extends RecyclerView.ViewHolder {

        TextView txtItemRutina;

        public RutinaViewHolder(@NonNull View itemView) {
            super(itemView);

            txtItemRutina =
                    itemView.findViewById(R.id.txtItemRutina);
        }
    }

    @NonNull
    @Override
    public RutinaViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(
                        R.layout.item_rutina,
                        parent,
                        false
                );

        return new RutinaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull RutinaViewHolder holder,
            int position) {

        String nombreRutina =
                listaRutinas.get(position);

        holder.txtItemRutina.setText(nombreRutina);

        // CLIC NORMAL:
        // abre los ejercicios de la rutina
        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(
                    v.getContext(),
                    EjerciciosActivity.class
            );

            intent.putExtra(
                    "NOMBRE_RUTINA",
                    nombreRutina
            );

            v.getContext().startActivity(intent);
        });

        // PULSACIÓN LARGA:
        // avisará a MisRutinasActivity para eliminar
        holder.itemView.setOnLongClickListener(v -> {

            int posicionActual =
                    holder.getAdapterPosition();

            if (posicionActual != RecyclerView.NO_POSITION) {

                listener.onRutinaLongClick(
                        listaRutinas.get(posicionActual),
                        posicionActual
                );
            }

            return true;
        });
    }

    @Override
    public int getItemCount() {
        return listaRutinas.size();
    }
}