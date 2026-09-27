package com.fittrack.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;

public class MisRutinasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mis_rutinas);

        // Elementos de la pantalla
        RecyclerView recyclerRutinas =
                findViewById(R.id.recyclerRutinas);

        Button btnCrearRutina =
                findViewById(R.id.btnCrearRutina);

        // Almacenamiento de FitTrack
        SharedPreferences preferencias =
                getSharedPreferences(
                        "FitTrackDatos",
                        MODE_PRIVATE
                );

        // Recuperamos las rutinas guardadas
        String rutinasGuardadas =
                preferencias.getString(
                        "rutinas",
                        ""
                );

        ArrayList<String> listaRutinas =
                new ArrayList<>();

        if (!rutinasGuardadas.isEmpty()) {

            listaRutinas.addAll(
                    Arrays.asList(
                            rutinasGuardadas.split("\\|")
                    )
            );
        }

        // Recibimos una nueva rutina
        String nombreRutina =
                getIntent().getStringExtra(
                        "NOMBRE_RUTINA"
                );

        if (nombreRutina != null
                && !nombreRutina.trim().isEmpty()) {

            nombreRutina = nombreRutina.trim();

            listaRutinas.add(nombreRutina);

            String todasLasRutinas =
                    String.join(
                            "|",
                            listaRutinas
                    );

            preferencias.edit()
                    .putString(
                            "rutinas",
                            todasLasRutinas
                    )
                    .apply();
        }

        // Adapter + pulsación larga
        RutinaAdapter adapter =
                new RutinaAdapter(
                        listaRutinas,

                        (rutina, position) -> {

                            new AlertDialog.Builder(
                                    MisRutinasActivity.this
                            )
                                    .setTitle(
                                            "Eliminar rutina"
                                    )

                                    .setMessage(
                                            "¿Deseas eliminar "
                                                    + rutina
                                                    + "?"
                                    )

                                    .setNegativeButton(
                                            "Cancelar",
                                            null
                                    )

                                    .setPositiveButton(
                                            "Eliminar",
                                            (dialog, which) -> {

                                                // Eliminamos la rutina seleccionada
                                                listaRutinas.remove(position);

                                                // Guardamos nuevamente la lista
                                                String rutinasActualizadas =
                                                        String.join(
                                                                "|",
                                                                listaRutinas
                                                        );

                                                preferencias.edit()
                                                        .putString(
                                                                "rutinas",
                                                                rutinasActualizadas
                                                        )
                                                        .apply();

                                                /*
                                                 * Comprobamos si todavía existe
                                                 * otra rutina con el mismo nombre.
                                                 */
                                                boolean existeOtraIgual = false;

                                                for (String otraRutina : listaRutinas) {

                                                    if (otraRutina.equalsIgnoreCase(rutina)) {
                                                        existeOtraIgual = true;
                                                        break;
                                                    }
                                                }

                                                /*
                                                 * Si ya no queda ninguna rutina
                                                 * con ese nombre, también borramos
                                                 * sus ejercicios.
                                                 */
                                                if (!existeOtraIgual) {

                                                    preferencias.edit()
                                                            .remove(
                                                                    "ejercicios_" + rutina
                                                            )
                                                            .apply();
                                                }

                                                Toast.makeText(
                                                        MisRutinasActivity.this,
                                                        "Rutina eliminada",
                                                        Toast.LENGTH_SHORT
                                                ).show();

                                                // Recargamos la pantalla
                                                recreate();
                                            }
                                    )
                                    .show();
                        }
                );

        recyclerRutinas.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerRutinas.setAdapter(adapter);

        // Crear nueva rutina
        btnCrearRutina.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MisRutinasActivity.this,
                    CrearRutinaActivity.class
            );

            startActivity(intent);
        });

        // Ajuste de barras del sistema
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
}