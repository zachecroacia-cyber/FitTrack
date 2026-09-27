package com.fittrack.app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RegistrarEntrenamientoActivity extends AppCompatActivity {

    private LinearLayout contenedorSeries;
    private String nombreEjercicio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registrar_entrenamiento);

        TextView txtNombreEjercicio =
                findViewById(R.id.txtNombreEjercicio);

        contenedorSeries =
                findViewById(R.id.contenedorSeries);

        Button btnAgregarSerie =
                findViewById(R.id.btnAgregarSerie);

        Button btnGuardarEntrenamiento =
                findViewById(R.id.btnGuardarEntrenamiento);

        // Recibimos el ejercicio seleccionado
        nombreEjercicio =
                getIntent().getStringExtra("NOMBRE_EJERCICIO");

        if (nombreEjercicio != null) {
            txtNombreEjercicio.setText(nombreEjercicio);
        }

        // Comenzamos con una serie
        agregarSerie();

        // Agregar nuevas series
        btnAgregarSerie.setOnClickListener(v -> {
            agregarSerie();
        });

        // Guardar entrenamiento
        btnGuardarEntrenamiento.setOnClickListener(v -> {
            guardarEntrenamiento();
        });
    }

    private void agregarSerie() {

        View vistaSerie =
                LayoutInflater.from(this)
                        .inflate(
                                R.layout.item_serie,
                                contenedorSeries,
                                false
                        );

        Button btnEliminarSerie =
                vistaSerie.findViewById(
                        R.id.btnEliminarSerie
                );

        contenedorSeries.addView(vistaSerie);

        renumerarSeries();

        btnEliminarSerie.setOnClickListener(v -> {

            if (contenedorSeries.getChildCount() <= 1) {

                Toast.makeText(
                        RegistrarEntrenamientoActivity.this,
                        "Debe existir al menos una serie",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            contenedorSeries.removeView(vistaSerie);

            renumerarSeries();
        });
    }

    private void renumerarSeries() {

        for (int i = 0;
             i < contenedorSeries.getChildCount();
             i++) {

            View vistaSerie =
                    contenedorSeries.getChildAt(i);

            TextView txtNumeroSerie =
                    vistaSerie.findViewById(
                            R.id.txtNumeroSerie
                    );

            txtNumeroSerie.setText(
                    "Serie " + (i + 1)
            );
        }
    }

    private void guardarEntrenamiento() {

        StringBuilder datosSeries =
                new StringBuilder();

        // Recorremos todas las series visibles
        for (int i = 0;
             i < contenedorSeries.getChildCount();
             i++) {

            View vistaSerie =
                    contenedorSeries.getChildAt(i);

            EditText edtRepeticiones =
                    vistaSerie.findViewById(
                            R.id.edtRepeticiones
                    );

            EditText edtPeso =
                    vistaSerie.findViewById(
                            R.id.edtPeso
                    );

            String repeticiones =
                    edtRepeticiones
                            .getText()
                            .toString()
                            .trim();

            String peso =
                    edtPeso
                            .getText()
                            .toString()
                            .trim();

            // Validación
            if (repeticiones.isEmpty()
                    || peso.isEmpty()) {

                Toast.makeText(
                        RegistrarEntrenamientoActivity.this,
                        "Completa repeticiones y peso en todas las series",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            // Ejemplo: 10,22.5
            datosSeries
                    .append(repeticiones)
                    .append(",")
                    .append(peso);

            // Separador entre series
            if (i <
                    contenedorSeries.getChildCount() - 1) {

                datosSeries.append(";");
            }
        }

        // Fecha y hora del entrenamiento
        String fechaHora =
                new SimpleDateFormat(
                        "dd/MM/yyyy HH:mm",
                        Locale.getDefault()
                ).format(new Date());

        /*
         * Un entrenamiento quedará parecido a:
         *
         * 27/09/2026 20:15#Laterales#10,22.5;8,25;6,27.5
         */
        String entrenamiento =
                fechaHora
                        + "#"
                        + nombreEjercicio
                        + "#"
                        + datosSeries;

        SharedPreferences preferencias =
                getSharedPreferences(
                        "FitTrackDatos",
                        MODE_PRIVATE
                );

        String historial =
                preferencias.getString(
                        "historial_entrenamientos",
                        ""
                );

        if (historial.isEmpty()) {

            historial = entrenamiento;

        } else {

            historial =
                    historial
                            + "|"
                            + entrenamiento;
        }

        preferencias.edit()
                .putString(
                        "historial_entrenamientos",
                        historial
                )
                .apply();

        Toast.makeText(
                RegistrarEntrenamientoActivity.this,
                "Entrenamiento guardado",
                Toast.LENGTH_SHORT
        ).show();

        finish();
    }
}