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

public class CrearRutinaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_crear_rutina);

        EditText edtNombreRutina =
                findViewById(R.id.edtNombreRutina);

        Button btnGuardarRutina =
                findViewById(R.id.btnGuardarRutina);

        btnGuardarRutina.setOnClickListener(v -> {

            String nombreRutina =
                    edtNombreRutina.getText().toString().trim();

            // Evitar nombres vacíos
            if (nombreRutina.isEmpty()) {

                Toast.makeText(
                        CrearRutinaActivity.this,
                        "Escribe el nombre de la rutina",
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

            // Recuperamos las rutinas existentes
            String rutinasGuardadas =
                    preferencias.getString(
                            "rutinas",
                            ""
                    );

            // Comprobamos si la rutina ya existe
            if (!rutinasGuardadas.isEmpty()) {

                String[] rutinasExistentes =
                        rutinasGuardadas.split("\\|");

                for (String rutina : rutinasExistentes) {

                    if (rutina.equalsIgnoreCase(nombreRutina)) {

                        Toast.makeText(
                                CrearRutinaActivity.this,
                                "Esta rutina ya existe",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }
                }
            }

            // La rutina no está duplicada:
            // la enviamos a MisRutinasActivity
            Toast.makeText(
                    CrearRutinaActivity.this,
                    "Rutina " + nombreRutina + " guardada",
                    Toast.LENGTH_SHORT
            ).show();

            Intent intent = new Intent(
                    CrearRutinaActivity.this,
                    MisRutinasActivity.class
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