        package com.fittrack.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import android.widget.SeekBar;
import android.os.Handler;
import android.os.Looper;
import android.view.Window;
import android.view.WindowManager;

import java.io.File;
import java.util.ArrayList;

public class ReproductorFitTrack {

    private final Context contexto;
    private final VideosFitTrack almacenamiento;
    private final LinearLayout contenedor;

    private Runnable seleccionarVideo;

    public ReproductorFitTrack(
            Context contexto,
            LinearLayout contenedor) {

        this.contexto = contexto;
        this.contenedor = contenedor;

        almacenamiento = new VideosFitTrack(contexto);
    }

    public void setSeleccionarVideo(Runnable accion) {
        seleccionarVideo = accion;
    }

    public void mostrarVideos() {

        contenedor.removeAllViews();

        TextView titulo = crearTexto(
                "Mis videos de entrenamiento",
                18,
                true
        );

        titulo.setTextColor(
                contexto.getColor(R.color.fittrack_green)
        );

        contenedor.addView(titulo);

        TextView descripcion = crearTexto(
                "Guarda videos para revisar tu técnica, " +
                        "ejecución y evolución en el gimnasio.",
                14,
                false
        );

        descripcion.setPadding(
                0, dp(12), 0, dp(20)
        );

        contenedor.addView(descripcion);

        Button agregar = crearBoton("+ AGREGAR VIDEO");

        contenedor.addView(agregar);

        agregar.setOnClickListener(v -> {

            if (seleccionarVideo != null) {
                seleccionarVideo.run();
            }
        });

        TextView subtitulo = crearTexto(
                "VIDEOS GUARDADOS",
                15,
                true
        );

        subtitulo.setTextColor(
                contexto.getColor(R.color.fittrack_green)
        );

        subtitulo.setPadding(
                0, dp(25), 0, dp(15)
        );

        contenedor.addView(subtitulo);

        ArrayList<VideosFitTrack.Video> videos =
                almacenamiento.obtenerVideos();

        if (videos.isEmpty()) {

            TextView vacio = crearTexto(
                    "Todavía no tienes videos guardados.",
                    14,
                    false
            );

            contenedor.addView(vacio);
            return;
        }

        for (int i = videos.size() - 1; i >= 0; i--) {

            agregarTarjeta(videos.get(i));
        }
    }

    private void agregarTarjeta(VideosFitTrack.Video video) {

        LinearLayout tarjeta = new LinearLayout(contexto);

        tarjeta.setOrientation(LinearLayout.VERTICAL);

        tarjeta.setPadding(
                dp(10), dp(10), dp(10), dp(10)
        );

        tarjeta.setBackgroundColor(
                contexto.getColor(R.color.fittrack_surface)
        );

        LinearLayout.LayoutParams parametros =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        parametros.bottomMargin = dp(16);

        contenedor.addView(tarjeta, parametros);

        TextView nombre = crearTexto(
                video.nombre,
                16,
                true
        );

        nombre.setTextColor(
                contexto.getColor(R.color.fittrack_green)
        );

        tarjeta.addView(nombre);

        TextView fecha = crearTexto(
                video.fecha,
                12,
                false
        );

        fecha.setPadding(0, dp(8), 0, dp(8));

        tarjeta.addView(fecha);

        TextView descripcion = crearTexto(
                video.descripcion,
                14,
                false
        );

        tarjeta.addView(descripcion);

        Button reproducir = crearBoton("REPRODUCIR");

        tarjeta.addView(reproducir);

        reproducir.setOnClickListener(v ->
                reproducirVideo(video)
        );

        Button editar = crearBoton("EDITAR");

        tarjeta.addView(editar);

        editar.setOnClickListener(v ->
                editarVideo(video)
        );

        Button eliminar = crearBoton("ELIMINAR");

        tarjeta.addView(eliminar);

        eliminar.setOnClickListener(v ->
                eliminarVideo(video)
        );
    }

    public void agregarVideo(Uri uri) {

        if (uri == null) {
            return;
        }

        LinearLayout formulario = crearFormulario();

        EditText nombre = crearCampo("Nombre del ejercicio");

        EditText descripcion = crearCampo(
                "Descripción del entrenamiento"
        );

        formulario.addView(nombre);
        formulario.addView(descripcion);

        AlertDialog dialogo = new AlertDialog.Builder(
                contexto,
                AlertDialog.THEME_HOLO_LIGHT
        )
                .setTitle("Nuevo video")
                .setView(formulario)
                .setNegativeButton("CANCELAR", null)
                .setPositiveButton("GUARDAR", null)
                .create();

        dialogo.setOnShowListener(d -> {

            dialogo.getButton(
                    AlertDialog.BUTTON_POSITIVE
            ).setOnClickListener(v -> {

                String textoNombre =
                        nombre.getText().toString().trim();

                String textoDescripcion =
                        descripcion.getText().toString().trim();

                if (textoNombre.isEmpty()) {

                    nombre.setError("Escribe un nombre");
                    return;
                }

                boolean guardado = almacenamiento.agregarVideo(
                        uri,
                        textoNombre,
                        textoDescripcion
                );

                if (guardado) {

                    Toast.makeText(
                            contexto,
                            "Video guardado",
                            Toast.LENGTH_SHORT
                    ).show();

                    dialogo.dismiss();
                    mostrarVideos();

                } else {

                    Toast.makeText(
                            contexto,
                            "No se pudo guardar el video",
                            Toast.LENGTH_LONG
                    ).show();
                }
            });
        });

        dialogo.show();
    }

    private void reproducirVideo(VideosFitTrack.Video video) {

        File archivo = new File(video.ruta);
        if (!archivo.exists()) {
            Toast.makeText(contexto, "No se encontró el archivo", Toast.LENGTH_SHORT).show();
            return;
        }

        // El fondo negro se aplica al contenedor, no al VideoView.
        // Aplicarlo directamente al VideoView puede tapar la imagen.
        LinearLayout pantalla = new LinearLayout(contexto);
        pantalla.setOrientation(LinearLayout.VERTICAL);
        pantalla.setBackgroundColor(Color.BLACK);
        pantalla.setPadding(dp(8), dp(12), dp(8), dp(8));

        VideoView reproductor = new VideoView(contexto);
        pantalla.addView(reproductor, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(300)));

        TextView estado = new TextView(contexto);
        estado.setText("Preparando video...");
        estado.setTextColor(Color.WHITE);
        estado.setGravity(Gravity.CENTER);
        estado.setPadding(0, dp(8), 0, dp(4));
        pantalla.addView(estado);

        SeekBar barra = new SeekBar(contexto);
        barra.setMax(1000);
        pantalla.addView(barra);

        LinearLayout controles = new LinearLayout(contexto);
        controles.setGravity(Gravity.CENTER);
        controles.setOrientation(LinearLayout.HORIZONTAL);

        Button botonPlay = new Button(contexto);
        botonPlay.setText("PAUSAR");
        controles.addView(botonPlay);

        TextView tiempo = new TextView(contexto);
        tiempo.setTextColor(Color.WHITE);
        tiempo.setText("00:00 / 00:00");
        tiempo.setPadding(dp(8), 0, 0, 0);
        controles.addView(tiempo);
        pantalla.addView(controles);

        AlertDialog dialogo = new AlertDialog.Builder(
                contexto, AlertDialog.THEME_HOLO_LIGHT)
                .setView(pantalla)
                .setPositiveButton("CERRAR", (d, opcion) -> { })
                .create();

        Handler handler = new Handler(Looper.getMainLooper());
        final boolean[] activo = {true};
        final boolean[] arrastrando = {false};
        final boolean[] preparado = {false};

        Runnable actualizar = new Runnable() {
            @Override
            public void run() {
                if (!activo[0]) return;
                if (preparado[0]) {
                    int duracion = reproductor.getDuration();
                    int posicion = reproductor.getCurrentPosition();
                    if (duracion > 0) {
                        if (!arrastrando[0]) {
                            barra.setProgress((int) (1000L * posicion / duracion));
                        }
                        tiempo.setText(formatearTiempo(posicion) + " / "
                                + formatearTiempo(duracion));
                    }
                }
                handler.postDelayed(this, 400);
            }
        };

        barra.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                arrastrando[0] = true;
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (preparado[0] && reproductor.getDuration() > 0) {
                    reproductor.seekTo((int) (reproductor.getDuration()
                            * (seekBar.getProgress() / 1000f)));
                }
                arrastrando[0] = false;
            }
        });

        botonPlay.setOnClickListener(v -> {
            if (!preparado[0]) return;
            if (reproductor.isPlaying()) {
                reproductor.pause();
                botonPlay.setText("REPRODUCIR");
            } else {
                reproductor.start();
                botonPlay.setText("PAUSAR");
            }
        });

        reproductor.setOnPreparedListener(mp -> {
            preparado[0] = true;
            estado.setText("");
            mp.setOnVideoSizeChangedListener((player, width, height) -> {
                // VideoView conserva la proporción del video automáticamente.
            });
            reproductor.start();
            botonPlay.setText("PAUSAR");
        });

        reproductor.setOnCompletionListener(mp -> {
            botonPlay.setText("REPRODUCIR");
            barra.setProgress(1000);
        });

        reproductor.setOnErrorListener((mp, what, extra) -> {
            estado.setText("No se pudo reproducir este archivo. "
                    + "Prueba un MP4 con video H.264.");
            botonPlay.setEnabled(false);
            return true;
        });

        dialogo.setOnDismissListener(d -> {
            activo[0] = false;
            handler.removeCallbacks(actualizar);
            reproductor.stopPlayback();
        });

        dialogo.show();
        Window ventana = dialogo.getWindow();
        if (ventana != null) {
            ventana.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
        }
        handler.post(actualizar);
        reproductor.setVideoURI(Uri.fromFile(archivo));
    }

    private String formatearTiempo(int milisegundos) {
        int segundos = Math.max(0, milisegundos / 1000);
        return String.format(java.util.Locale.getDefault(), "%02d:%02d",
                segundos / 60, segundos % 60);
    }

    private void editarVideo(VideosFitTrack.Video video) {

        LinearLayout formulario = crearFormulario();

        EditText nombre = crearCampo("Nombre");

        nombre.setText(video.nombre);

        EditText descripcion = crearCampo("Descripción");

        descripcion.setText(video.descripcion);

        formulario.addView(nombre);
        formulario.addView(descripcion);

        AlertDialog dialogo = new AlertDialog.Builder(
                contexto,
                AlertDialog.THEME_HOLO_LIGHT
        )
                .setTitle("Editar video")
                .setView(formulario)
                .setNegativeButton("CANCELAR", null)
                .setPositiveButton("GUARDAR", null)
                .create();

        dialogo.setOnShowListener(d -> {

            dialogo.getButton(
                    AlertDialog.BUTTON_POSITIVE
            ).setOnClickListener(v -> {

                String nuevoNombre =
                        nombre.getText().toString().trim();

                if (nuevoNombre.isEmpty()) {

                    nombre.setError("Escribe un nombre");
                    return;
                }

                boolean actualizado =
                        almacenamiento.actualizarVideo(
                                video.id,
                                nuevoNombre,
                                descripcion.getText()
                                        .toString().trim()
                        );

                if (actualizado) {

                    dialogo.dismiss();
                    mostrarVideos();

                    Toast.makeText(
                            contexto,
                            "Video actualizado",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            });
        });

        dialogo.show();
    }

    private void eliminarVideo(VideosFitTrack.Video video) {

        new AlertDialog.Builder(
                contexto,
                AlertDialog.THEME_HOLO_LIGHT
        )
                .setTitle("Eliminar video")
                .setMessage(
                        "¿Deseas eliminar este video definitivamente?"
                )
                .setNegativeButton("CANCELAR", null)
                .setPositiveButton(
                        "ELIMINAR",
                        (dialogo, opcion) -> {

                            if (almacenamiento.eliminarVideo(
                                    video.id)) {

                                mostrarVideos();

                                Toast.makeText(
                                        contexto,
                                        "Video eliminado",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                )
                .show();
    }

    private LinearLayout crearFormulario() {

        LinearLayout formulario =
                new LinearLayout(contexto);

        formulario.setOrientation(
                LinearLayout.VERTICAL
        );

        formulario.setPadding(
                dp(20), dp(15), dp(20), dp(10)
        );

        formulario.setBackgroundColor(Color.WHITE);

        return formulario;
    }

    private EditText crearCampo(String sugerencia) {

        EditText campo = new EditText(contexto);

        campo.setHint(sugerencia);

        campo.setTextColor(Color.BLACK);

        campo.setHintTextColor(Color.DKGRAY);

        campo.setTextSize(15);

        campo.setSingleLine(false);

        campo.setBackgroundTintList(
                ColorStateList.valueOf(
                        contexto.getColor(R.color.fittrack_green)
                )
        );

        return campo;
    }

    private Button crearBoton(String texto) {

        Button boton = new Button(contexto);

        boton.setText(texto);

        boton.setTextSize(12);

        boton.setTextColor(
                contexto.getColor(R.color.fittrack_background)
        );

        boton.setBackgroundTintList(
                ColorStateList.valueOf(
                        contexto.getColor(R.color.fittrack_green)
                )
        );

        return boton;
    }

    private TextView crearTexto(
            String texto,
            int tamano,
            boolean negrita) {

        TextView vista = new TextView(contexto);

        vista.setText(texto);

        vista.setTextSize(tamano);

        vista.setTextColor(Color.WHITE);

        if (negrita) {
            vista.setTypeface(
                    null,
                    android.graphics.Typeface.BOLD
            );
        }

        return vista;
    }

    private int dp(int valor) {

        return Math.round(
                valor *
                        contexto.getResources()
                                .getDisplayMetrics().density
        );
    }
}