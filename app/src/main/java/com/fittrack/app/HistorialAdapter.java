package com.fittrack.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.HistorialViewHolder> {

    private List<String> listaEntrenamientos;
    private OnEliminarClickListener listener;

    public interface OnEliminarClickListener {
        void onEliminarClick(int position);
    }

    public HistorialAdapter(
            List<String> listaEntrenamientos,
            OnEliminarClickListener listener) {

        this.listaEntrenamientos = listaEntrenamientos;
        this.listener = listener;
    }

    public static class HistorialViewHolder
            extends RecyclerView.ViewHolder {

        TextView txtFechaHistorial;
        TextView txtEjercicioHistorial;
        TextView txtSeriesHistorial;
        Button btnEliminarHistorial;

        public HistorialViewHolder(
                @NonNull View itemView) {

            super(itemView);

            txtFechaHistorial =
                    itemView.findViewById(
                            R.id.txtFechaHistorial
                    );

            txtEjercicioHistorial =
                    itemView.findViewById(
                            R.id.txtEjercicioHistorial
                    );

            txtSeriesHistorial =
                    itemView.findViewById(
                            R.id.txtSeriesHistorial
                    );

            btnEliminarHistorial =
                    itemView.findViewById(
                            R.id.btnEliminarHistorial
                    );
        }
    }

    @NonNull
    @Override
    public HistorialViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view =
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(
                                R.layout.item_historial,
                                parent,
                                false
                        );

        return new HistorialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull HistorialViewHolder holder,
            int position) {

        String entrenamiento =
                listaEntrenamientos.get(position);

        /*
         * Formato guardado:
         *
         * fecha#ejercicio#series
         *
         * Ejemplo:
         * 27/09/2026 20:15#Press banca#10,60;8,70
         */

        String[] partes =
                entrenamiento.split("#");

        if (partes.length >= 3) {

            String fecha = partes[0];
            String ejercicio = partes[1];
            String series = partes[2];

            holder.txtFechaHistorial.setText(fecha);
            holder.txtEjercicioHistorial.setText(ejercicio);

            // Convertimos las series a texto legible
            String[] listaSeries =
                    series.split(";");

            StringBuilder textoSeries =
                    new StringBuilder();

            for (int i = 0;
                 i < listaSeries.length;
                 i++) {

                String[] datos =
                        listaSeries[i].split(",");

                if (datos.length == 2) {

                    textoSeries
                            .append("Serie ")
                            .append(i + 1)
                            .append(": ")
                            .append(datos[0])
                            .append(" reps × ")
                            .append(datos[1])
                            .append(" kg");

                    if (i < listaSeries.length - 1) {
                        textoSeries.append("\n");
                    }
                }
            }

            holder.txtSeriesHistorial.setText(
                    textoSeries.toString()
            );
        }

        // Botón eliminar
        holder.btnEliminarHistorial
                .setOnClickListener(v -> {

                    int posicionActual =
                            holder.getAdapterPosition();

                    if (posicionActual
                            != RecyclerView.NO_POSITION) {

                        listener.onEliminarClick(
                                posicionActual
                        );
                    }
                });
    }

    @Override
    public int getItemCount() {
        return listaEntrenamientos.size();
    }
}