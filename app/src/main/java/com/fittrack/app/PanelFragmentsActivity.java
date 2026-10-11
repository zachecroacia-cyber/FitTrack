package com.fittrack.app;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class PanelFragmentsActivity extends AppCompatActivity
        implements MenuFragment.OnMenuSeleccionadoListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_panel_fragments);
    }

    @Override
    public void onMenuSeleccionado(String opcion) {

        Fragment fragment = getSupportFragmentManager()
                .findFragmentById(R.id.contenedorPrincipal);

        if (fragment instanceof ContenidoFragment) {

            ContenidoFragment contenidoFragment =
                    (ContenidoFragment) fragment;

            contenidoFragment.mostrarContenido(opcion);
        }
    }
}