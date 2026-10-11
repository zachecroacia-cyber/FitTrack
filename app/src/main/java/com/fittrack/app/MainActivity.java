package com.fittrack.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Botones del menú principal
        Button btnMisRutinas =
                findViewById(R.id.btnMisRutinas);

        Button btnHistorial =
                findViewById(R.id.btnHistorial);

        Button btnProgreso =
                findViewById(R.id.btnProgreso);

        Button btnPanelInteractivo =
                findViewById(R.id.btnPanelInteractivo);

        // Abrir Mis Rutinas
        btnMisRutinas.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    MisRutinasActivity.class
            );

            startActivity(intent);
        });

        // Abrir Historial
        btnHistorial.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    HistorialActivity.class
            );

            startActivity(intent);
        });

        // Abrir Progreso
        btnProgreso.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    ProgresoActivity.class
            );

            startActivity(intent);
        });

        // Abrir Panel Interactivo
        btnPanelInteractivo.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    PanelFragmentsActivity.class
            );

            startActivity(intent);
        });

        // Ajustar pantalla a las barras del sistema
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