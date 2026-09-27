package com.fittrack.app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
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

public class HistorialActivity extends AppCompatActivity {

    private ArrayList<String> listaEntrenamientos;
    private SharedPreferences preferencias;
    private RecyclerView recyclerHistorial;
    private TextView txtSinEntrenamientos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_historial);

        recyclerHistorial =
                findViewById(R.id.recyclerHistorial);

        txtSinEntrenamientos =
                findViewById(R.id.txtSinEntrenamientos);

        preferencias =
                getSharedPreferences(
                        "FitTrackDatos",
                        MODE_PRIVATE
                );

        cargarHistorial();

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

    private void cargarHistorial() {

        String historialGuardado =
                preferencias.getString(
                        "historial_entrenamientos",
                        ""
                );

        listaEntrenamientos =
                new ArrayList<>();

        if (!historialGuardado.isEmpty()) {

            listaEntrenamientos.addAll(
                    Arrays.asList(
                            historialGuardado.split("\\|")
                    )
            );
        }

        if (listaEntrenamientos.isEmpty()) {

            txtSinEntrenamientos.setVisibility(
                    View.VISIBLE
            );

            recyclerHistorial.setVisibility(
                    View.GONE
            );

        } else {

            txtSinEntrenamientos.setVisibility(
                    View.GONE
            );

            recyclerHistorial.setVisibility(
                    View.VISIBLE
            );
        }

        HistorialAdapter adapter =
                new HistorialAdapter(
                        listaEntrenamientos,
                        position -> {

                            new AlertDialog.Builder(
                                    HistorialActivity.this
                            )
                                    .setTitle(
                                            "Eliminar entrenamiento"
                                    )
                                    .setMessage(
                                            "¿Deseas eliminar este entrenamiento del historial?"
                                    )
                                    .setNegativeButton(
                                            "Cancelar",
                                            null
                                    )
                                    .setPositiveButton(
                                            "Eliminar",
                                            (dialog, which) -> {

                                                eliminarEntrenamiento(
                                                        position
                                                );
                                            }
                                    )
                                    .show();
                        }
                );

        recyclerHistorial.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerHistorial.setAdapter(adapter);
    }

    private void eliminarEntrenamiento(
            int position) {

        if (position < 0
                || position >= listaEntrenamientos.size()) {
            return;
        }

        listaEntrenamientos.remove(position);

        String historialActualizado =
                String.join(
                        "|",
                        listaEntrenamientos
                );

        preferencias.edit()
                .putString(
                        "historial_entrenamientos",
                        historialActualizado
                )
                .apply();

        Toast.makeText(
                HistorialActivity.this,
                "Entrenamiento eliminado",
                Toast.LENGTH_SHORT
        ).show();

        cargarHistorial();
    }
}