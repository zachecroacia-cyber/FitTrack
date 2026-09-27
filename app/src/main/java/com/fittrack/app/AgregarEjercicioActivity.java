package com.fittrack.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AgregarEjercicioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_agregar_ejercicio);

        EditText edtNombreEjercicio =
                findViewById(R.id.edtNombreEjercicio);

        Button btnGuardarEjercicio =
                findViewById(R.id.btnGuardarEjercicio);

        // Recibimos la rutina a la que pertenece el ejercicio
        String nombreRutina =
                getIntent().getStringExtra("NOMBRE_RUTINA");

        btnGuardarEjercicio.setOnClickListener(v -> {

            String nombreEjercicio =
                    edtNombreEjercicio.getText().toString().trim();

            // Evitamos guardar un ejercicio vacío
            if (nombreEjercicio.isEmpty()) {

                Toast.makeText(
                        AgregarEjercicioActivity.this,
                        "Escribe el nombre del ejercicio",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            // Abrimos el almacenamiento de FitTrack
            SharedPreferences preferencias =
                    getSharedPreferences(
                            "FitTrackDatos",
                            MODE_PRIVATE
                    );

            // Cada rutina tendrá su propia clave
            String claveEjercicios =
                    "ejercicios_" + nombreRutina;

            // Recuperamos los ejercicios que ya tenía esa rutina
            String ejerciciosGuardados =
                    preferencias.getString(
                            claveEjercicios,
                            ""
                    );
            if (!ejerciciosGuardados.isEmpty()) {

                String[] ejerciciosExistentes =
                        ejerciciosGuardados.split("\\|");

                for (String ejercicio : ejerciciosExistentes) {

                    if (ejercicio.equalsIgnoreCase(nombreEjercicio)) {

                        Toast.makeText(
                                AgregarEjercicioActivity.this,
                                "Este ejercicio ya existe en la rutina",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }
                }
            }

            // Agregamos el nuevo ejercicio
            String nuevosEjercicios;

            if (ejerciciosGuardados.isEmpty()) {

                nuevosEjercicios = nombreEjercicio;

            } else {

                nuevosEjercicios =
                        ejerciciosGuardados
                                + "|"
                                + nombreEjercicio;
            }

            // Guardamos la nueva lista
            preferencias.edit()
                    .putString(
                            claveEjercicios,
                            nuevosEjercicios
                    )
                    .apply();

            Toast.makeText(
                    AgregarEjercicioActivity.this,
                    "Ejercicio guardado",
                    Toast.LENGTH_SHORT
            ).show();

            // Regresamos a los ejercicios de la misma rutina
            Intent intent = new Intent(
                    AgregarEjercicioActivity.this,
                    EjerciciosActivity.class
            );

            intent.putExtra(
                    "NOMBRE_RUTINA",
                    nombreRutina
            );

            startActivity(intent);
            finish();
        });

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