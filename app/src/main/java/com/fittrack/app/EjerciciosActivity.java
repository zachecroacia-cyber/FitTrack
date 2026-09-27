package com.fittrack.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
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

public class EjerciciosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ejercicios);

        // Elementos de la pantalla
        TextView txtNombreRutina =
                findViewById(R.id.txtNombreRutina);

        Button btnAgregarEjercicio =
                findViewById(R.id.btnAgregarEjercicio);

        RecyclerView recyclerEjercicios =
                findViewById(R.id.recyclerEjercicios);

        // Recibimos la rutina seleccionada
        String nombreRutina =
                getIntent().getStringExtra("NOMBRE_RUTINA");

        if (nombreRutina != null) {
            txtNombreRutina.setText(nombreRutina);
        }

        // Abrimos el almacenamiento de FitTrack
        SharedPreferences preferencias =
                getSharedPreferences(
                        "FitTrackDatos",
                        MODE_PRIVATE
                );

        // Cada rutina tiene sus propios ejercicios
        String claveEjercicios =
                "ejercicios_" + nombreRutina;

        String ejerciciosGuardados =
                preferencias.getString(
                        claveEjercicios,
                        ""
                );

        // Creamos la lista
        ArrayList<String> listaEjercicios =
                new ArrayList<>();

        if (!ejerciciosGuardados.isEmpty()) {

            listaEjercicios.addAll(
                    Arrays.asList(
                            ejerciciosGuardados.split("\\|")
                    )
            );
        }

        // Creamos el Adapter
        EjercicioAdapter adapter =
                new EjercicioAdapter(
                        listaEjercicios,

                        (ejercicio, position) -> {

                            // Ventana de confirmación
                            new AlertDialog.Builder(
                                    EjerciciosActivity.this
                            )
                                    .setTitle("Eliminar ejercicio")
                                    .setMessage(
                                            "¿Deseas eliminar "
                                                    + ejercicio
                                                    + "?"
                                    )

                                    .setNegativeButton(
                                            "Cancelar",
                                            null
                                    )

                                    .setPositiveButton(
                                            "Eliminar",
                                            (dialog, which) -> {

                                                // Lo quitamos de la lista
                                                listaEjercicios.remove(position);


                                                // Convertimos nuevamente la lista a texto
                                                String ejerciciosActualizados =
                                                        String.join(
                                                                "|",
                                                                listaEjercicios
                                                        );

                                                // Actualizamos el almacenamiento
                                                preferencias.edit()
                                                        .putString(
                                                                claveEjercicios,
                                                                ejerciciosActualizados
                                                        )
                                                        .apply();
                                                recreate();

                                                Toast.makeText(
                                                        EjerciciosActivity.this,
                                                        "Ejercicio eliminado",
                                                        Toast.LENGTH_SHORT
                                                ).show();
                                            }
                                    )
                                    .show();
                        }
                );

        // Conectamos RecyclerView
        recyclerEjercicios.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerEjercicios.setAdapter(adapter);

        // Botón agregar ejercicio
        btnAgregarEjercicio.setOnClickListener(v -> {

            Intent intent = new Intent(
                    EjerciciosActivity.this,
                    AgregarEjercicioActivity.class
            );

            intent.putExtra(
                    "NOMBRE_RUTINA",
                    nombreRutina
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