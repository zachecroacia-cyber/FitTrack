package com.fittrack.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class MenuFragment extends Fragment {

    public interface OnMenuSeleccionadoListener {
        void onMenuSeleccionado(String opcion);
    }

    private Button btnPerfil;
    private Button btnFotos;
    private Button btnVideo;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View vista = inflater.inflate(
                R.layout.fragment_menu,
                container,
                false
        );

        // Opciones del fragment derecho
        btnPerfil = vista.findViewById(R.id.btnMenuPerfil);
        btnFotos = vista.findViewById(R.id.btnMenuFotos);
        btnVideo = vista.findViewById(R.id.btnMenuVideo);

        // Opciones de entrenamiento
        Button btnMisRutinas =
                vista.findViewById(R.id.btnMenuMisRutinas);

        Button btnHistorial =
                vista.findViewById(R.id.btnMenuHistorial);

        Button btnProgreso =
                vista.findViewById(R.id.btnMenuProgreso);

        // Navegación entre fragments
        btnPerfil.setOnClickListener(v ->
                seleccionarOpcion("Perfil"));

        btnFotos.setOnClickListener(v ->
                seleccionarOpcion("Fotos"));

        btnVideo.setOnClickListener(v ->
                seleccionarOpcion("Video"));

        // Abrir Mis Rutinas
        btnMisRutinas.setOnClickListener(v -> {

            Intent intent = new Intent(
                    requireContext(),
                    MisRutinasActivity.class
            );

            startActivity(intent);
        });

        // Abrir Historial
        btnHistorial.setOnClickListener(v -> {

            Intent intent = new Intent(
                    requireContext(),
                    HistorialActivity.class
            );

            startActivity(intent);
        });

        // Abrir Progreso
        btnProgreso.setOnClickListener(v -> {

            Intent intent = new Intent(
                    requireContext(),
                    ProgresoActivity.class
            );

            startActivity(intent);
        });

        return vista;
    }

    private void seleccionarOpcion(String opcion) {

        // Actualizar el color del menú
        actualizarSeleccion(opcion);

        // Comunicar la selección a la actividad
        if (getActivity() instanceof OnMenuSeleccionadoListener) {

            OnMenuSeleccionadoListener listener =
                    (OnMenuSeleccionadoListener) getActivity();

            listener.onMenuSeleccionado(opcion);
        }
    }

    private void actualizarSeleccion(String opcion) {

        Button[] botones = {
                btnPerfil,
                btnFotos,
                btnVideo
        };

        // Restaurar colores originales
        for (Button boton : botones) {

            boton.setBackgroundTintList(
                    ContextCompat.getColorStateList(
                            requireContext(),
                            R.color.fittrack_surface
                    )
            );

            boton.setTextColor(
                    ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                    )
            );
        }

        // Identificar el botón seleccionado
        Button seleccionado = null;

        switch (opcion) {

            case "Perfil":
                seleccionado = btnPerfil;
                break;

            case "Fotos":
                seleccionado = btnFotos;
                break;

            case "Video":
                seleccionado = btnVideo;
                break;
        }

        // Aplicar los colores de selección
        if (seleccionado != null) {

            seleccionado.setBackgroundTintList(
                    ContextCompat.getColorStateList(
                            requireContext(),
                            R.color.fittrack_menu_selected
                    )
            );

            seleccionado.setTextColor(
                    ContextCompat.getColor(
                            requireContext(),
                            R.color.fittrack_green
                    )
            );
        }
    }
}