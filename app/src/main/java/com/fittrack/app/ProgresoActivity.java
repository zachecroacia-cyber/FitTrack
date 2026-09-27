package com.fittrack.app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class ProgresoActivity extends AppCompatActivity {

    private Spinner spinnerEjercicios;

    private TextView txtEntrenamientosRegistrados;
    private TextView txtPesoMaximo;
    private TextView txtRepeticionesMaximas;
    private TextView txtUltimoEntrenamiento;

    private ArrayList<String> listaEntrenamientos;
    private ArrayList<String> listaEjercicios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_progreso);

        spinnerEjercicios =
                findViewById(R.id.spinnerEjercicios);

        txtEntrenamientosRegistrados =
                findViewById(
                        R.id.txtEntrenamientosRegistrados
                );

        txtPesoMaximo =
                findViewById(
                        R.id.txtPesoMaximo
                );

        txtRepeticionesMaximas =
                findViewById(
                        R.id.txtRepeticionesMaximas
                );

        txtUltimoEntrenamiento =
                findViewById(
                        R.id.txtUltimoEntrenamiento
                );

        listaEntrenamientos =
                new ArrayList<>();

        listaEjercicios =
                new ArrayList<>();

        cargarDatos();

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {

                    Insets systemBars =
                            insets.getInsets(
                                    WindowInsetsCompat.Type.systemBars()
                            );

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                }
        );
    }

    private void cargarDatos() {

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

            listaEjercicios.add(
                    "Sin entrenamientos"
            );

            configurarSpinner();

            return;
        }

        String[] entrenamientos =
                historial.split("\\|");

        for (String entrenamiento : entrenamientos) {

            listaEntrenamientos.add(
                    entrenamiento
            );

            String[] partes =
                    entrenamiento.split("#");

            if (partes.length >= 3) {

                String ejercicio =
                        partes[1];

                boolean yaExiste = false;

                for (String ejercicioGuardado
                        : listaEjercicios) {

                    if (ejercicioGuardado
                            .equalsIgnoreCase(
                                    ejercicio
                            )) {

                        yaExiste = true;
                        break;
                    }
                }

                if (!yaExiste) {
                    listaEjercicios.add(
                            ejercicio
                    );
                }
            }
        }

        configurarSpinner();
    }

    private void configurarSpinner() {

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout
                                .simple_spinner_item,
                        listaEjercicios
                );

        adapter.setDropDownViewResource(
                android.R.layout
                        .simple_spinner_dropdown_item
        );

        spinnerEjercicios.setAdapter(adapter);

        spinnerEjercicios
                .setOnItemSelectedListener(
                        new AdapterView
                                .OnItemSelectedListener() {

                            @Override
                            public void onItemSelected(
                                    AdapterView<?> parent,
                                    View view,
                                    int position,
                                    long id) {

                                String ejercicioSeleccionado =
                                        listaEjercicios.get(
                                                position
                                        );

                                if (ejercicioSeleccionado
                                        .equals(
                                                "Sin entrenamientos"
                                        )) {

                                    mostrarSinDatos();

                                } else {

                                    calcularProgreso(
                                            ejercicioSeleccionado
                                    );
                                }
                            }

                            @Override
                            public void onNothingSelected(
                                    AdapterView<?> parent) {
                            }
                        });
    }

    private void calcularProgreso(
            String ejercicioSeleccionado) {

        int cantidadEntrenamientos = 0;

        int maxRepeticiones = 0;

        double maxPeso = 0;

        String ultimoEntrenamiento =
                "Sin datos";

        /*
         * Recorremos todos los entrenamientos
         * guardados.
         */
        for (String entrenamiento
                : listaEntrenamientos) {

            String[] partes =
                    entrenamiento.split("#");

            if (partes.length < 3) {
                continue;
            }

            String fecha =
                    partes[0];

            String ejercicio =
                    partes[1];

            String series =
                    partes[2];

            if (!ejercicio.equalsIgnoreCase(
                    ejercicioSeleccionado)) {

                continue;
            }

            cantidadEntrenamientos++;

            String[] listaSeries =
                    series.split(";");

            StringBuilder textoUltimo =
                    new StringBuilder();

            textoUltimo
                    .append(fecha)
                    .append("\n");

            for (int i = 0;
                 i < listaSeries.length;
                 i++) {

                String[] datosSerie =
                        listaSeries[i]
                                .split(",");

                if (datosSerie.length != 2) {
                    continue;
                }

                try {

                    int repeticiones =
                            Integer.parseInt(
                                    datosSerie[0]
                            );

                    double peso =
                            Double.parseDouble(
                                    datosSerie[1]
                                            .replace(
                                                    ",",
                                                    "."
                                            )
                            );

                    if (repeticiones
                            > maxRepeticiones) {

                        maxRepeticiones =
                                repeticiones;
                    }

                    if (peso > maxPeso) {

                        maxPeso = peso;
                    }

                    textoUltimo
                            .append("Serie ")
                            .append(i + 1)
                            .append(": ")
                            .append(repeticiones)
                            .append(" reps × ")
                            .append(
                                    mostrarPeso(peso)
                            )
                            .append(" kg");

                    if (i
                            < listaSeries.length - 1) {

                        textoUltimo
                                .append("\n");
                    }

                } catch (
                        NumberFormatException e) {

                    // Si algún dato está dañado,
                    // continuamos con las demás series.
                }
            }

            /*
             * Como los entrenamientos se guardan
             * en orden, el último encontrado
             * será el entrenamiento más reciente.
             */
            ultimoEntrenamiento =
                    textoUltimo.toString();
        }

        txtEntrenamientosRegistrados
                .setText(
                        "Entrenamientos registrados: "
                                + cantidadEntrenamientos
                );

        txtPesoMaximo
                .setText(
                        "Peso máximo: "
                                + mostrarPeso(maxPeso)
                                + " kg"
                );

        txtRepeticionesMaximas
                .setText(
                        "Máximo de repeticiones: "
                                + maxRepeticiones
                );

        txtUltimoEntrenamiento
                .setText(
                        "Último entrenamiento:\n"
                                + ultimoEntrenamiento
                );
    }

    private String mostrarPeso(
            double peso) {

        if (peso == (long) peso) {

            return String.valueOf(
                    (long) peso
            );
        }

        return String.valueOf(peso);
    }

    private void mostrarSinDatos() {

        txtEntrenamientosRegistrados
                .setText(
                        "Entrenamientos registrados: 0"
                );

        txtPesoMaximo
                .setText(
                        "Peso máximo: 0 kg"
                );

        txtRepeticionesMaximas
                .setText(
                        "Máximo de repeticiones: 0"
                );

        txtUltimoEntrenamiento
                .setText(
                        "Último entrenamiento: sin datos"
                );
    }
}