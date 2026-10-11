package com.fittrack.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class ContenidoFragment extends Fragment {

    private TextView txtTituloContenido;
    private LinearLayout contenedorDinamico;
    private ImageView imagenPerfil;

    private String opcionActual = "Perfil";

    private static final String PREFERENCIAS = "FitTrackPerfil";
    private static final String CLAVE_NOMBRE = "nombre";
    private static final String CLAVE_DESCRIPCION = "acerca_de_mi";
    private static final String ARCHIVO_FOTO = "foto_perfil.jpg";

    private GaleriaFotos galeriaFotos;
    private ReproductorFitTrack reproductorFitTrack;

    private ActivityResultLauncher<String> selectorVideo;
    private ActivityResultLauncher<String> selectorFotoPerfil;
    private ActivityResultLauncher<String> selectorFotoGaleria;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        galeriaFotos = new GaleriaFotos(requireContext());

        selectorFotoPerfil = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {

                    if (uri == null || !isAdded()) {
                        return;
                    }

                    guardarFotografiaPerfil(uri);
                    cargarFotografiaPerfil();
                }
        );

        selectorFotoGaleria = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {

                    if (uri == null || !isAdded()) {
                        return;
                    }

                    mostrarDialogoNuevaFoto(uri);
                }
        );

        selectorVideo = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri == null || !isAdded()) {
                        return;
                    }
                    if ("Video".equals(opcionActual) || "Videos".equals(opcionActual)) {
                        if (reproductorFitTrack != null) {
                            reproductorFitTrack.agregarVideo(uri);
                        }
                    }
                }
        );
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View vista = inflater.inflate(
                R.layout.fragment_contenido,
                container,
                false
        );

        txtTituloContenido =
                vista.findViewById(R.id.txtTituloContenido);

        contenedorDinamico =
                vista.findViewById(R.id.contenedorDinamico);

        mostrarContenido(opcionActual);

        return vista;
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();

        txtTituloContenido = null;
        contenedorDinamico = null;
        imagenPerfil = null;
    }

    // ==================================================
    // NAVEGACIÓN
    // ==================================================

    public void mostrarContenido(String opcion) {

        opcionActual = opcion;

        if (txtTituloContenido == null ||
                contenedorDinamico == null) {
            return;
        }

        txtTituloContenido.setText(opcion);

        contenedorDinamico.removeAllViews();

        imagenPerfil = null;

        switch (opcion) {

            case "Perfil":
                mostrarPerfil();
                break;

            case "Fotos":
                mostrarGaleria();
                break;

            case "Video":
            case "Videos":
                reproductorFitTrack = new ReproductorFitTrack(
                        requireContext(),
                        contenedorDinamico
                );
                reproductorFitTrack.setSeleccionarVideo(
                        () -> selectorVideo.launch("video/*")
                );
                reproductorFitTrack.mostrarVideos();
                break;

            case "Botones":
                mostrarMensaje(
                        "Botones interactivos\n\n" +
                                "Aquí implementaremos botones " +
                                "con diferentes funcionalidades."
                );
                break;

            default:
                mostrarMensaje(
                        "Selecciona una opción del menú."
                );
                break;
        }
    }

    // ==================================================
    // PERFIL
    // ==================================================

    private void mostrarPerfil() {

        SharedPreferences preferencias =
                requireContext().getSharedPreferences(
                        PREFERENCIAS,
                        Context.MODE_PRIVATE
                );

        String nombreGuardado =
                preferencias.getString(CLAVE_NOMBRE, "");

        String descripcionGuardada =
                preferencias.getString(CLAVE_DESCRIPCION, "");

        imagenPerfil = new ImageView(requireContext());

        imagenPerfil.setScaleType(
                ImageView.ScaleType.CENTER_CROP
        );

        imagenPerfil.setImageResource(
                android.R.drawable.ic_menu_camera
        );

        LinearLayout.LayoutParams parametrosImagen =
                new LinearLayout.LayoutParams(
                        dp(130),
                        dp(130)
                );

        parametrosImagen.gravity = Gravity.CENTER_HORIZONTAL;
        parametrosImagen.bottomMargin = dp(12);

        contenedorDinamico.addView(
                imagenPerfil,
                parametrosImagen
        );

        cargarFotografiaPerfil();

        Button botonFoto = crearBoton(
                "CAMBIAR FOTOGRAFÍA"
        );

        contenedorDinamico.addView(botonFoto);

        botonFoto.setOnClickListener(v ->
                selectorFotoPerfil.launch("image/*")
        );

        TextView encabezado = crearTexto(
                "Personaliza tu perfil",
                18,
                true
        );

        encabezado.setPadding(
                0, dp(24), 0, dp(12)
        );

        contenedorDinamico.addView(encabezado);

        TextView etiquetaNombre = crearTexto(
                "NOMBRE",
                14,
                true
        );

        contenedorDinamico.addView(etiquetaNombre);

        EditText campoNombre = crearCampoTexto(
                "Escribe tu nombre"
        );

        campoNombre.setSingleLine(true);
        campoNombre.setText(nombreGuardado);

        contenedorDinamico.addView(campoNombre);

        TextView etiquetaDescripcion = crearTexto(
                "ACERCA DE MÍ",
                14,
                true
        );

        etiquetaDescripcion.setPadding(
                0, dp(20), 0, 0
        );

        contenedorDinamico.addView(etiquetaDescripcion);

        EditText campoDescripcion = crearCampoTexto(
                "Cuéntanos algo sobre ti, " +
                        "tus objetivos y tu experiencia..."
        );

        campoDescripcion.setInputType(
                InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_FLAG_MULTI_LINE |
                        InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
        );

        campoDescripcion.setGravity(Gravity.TOP | Gravity.START);
        campoDescripcion.setMinLines(4);
        campoDescripcion.setMaxLines(8);
        campoDescripcion.setText(descripcionGuardada);

        contenedorDinamico.addView(campoDescripcion);

        Button botonGuardar = crearBoton(
                "GUARDAR PERFIL"
        );

        LinearLayout.LayoutParams parametrosGuardar =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        parametrosGuardar.topMargin = dp(24);

        contenedorDinamico.addView(
                botonGuardar,
                parametrosGuardar
        );

        botonGuardar.setOnClickListener(v -> {

            String nombre =
                    campoNombre.getText().toString().trim();

            String descripcion =
                    campoDescripcion.getText().toString().trim();

            if (nombre.isEmpty()) {

                campoNombre.setError("Escribe tu nombre");
                campoNombre.requestFocus();

                return;
            }

            preferencias.edit()
                    .putString(CLAVE_NOMBRE, nombre)
                    .putString(CLAVE_DESCRIPCION, descripcion)
                    .apply();

            Toast.makeText(
                    requireContext(),
                    "Perfil guardado correctamente",
                    Toast.LENGTH_SHORT
            ).show();
        });
    }

    private void guardarFotografiaPerfil(Uri uri) {

        try {

            Context contexto = requireContext();

            File archivo = new File(
                    contexto.getFilesDir(),
                    ARCHIVO_FOTO
            );

            BitmapFactory.Options opciones =
                    new BitmapFactory.Options();

            opciones.inJustDecodeBounds = true;

            try (InputStream entrada =
                         contexto.getContentResolver()
                                 .openInputStream(uri)) {

                BitmapFactory.decodeStream(
                        entrada,
                        null,
                        opciones
                );
            }

            int ancho = opciones.outWidth;
            int alto = opciones.outHeight;

            int escala = 1;

            while (ancho / escala > 1024 ||
                    alto / escala > 1024) {

                escala *= 2;
            }

            opciones.inJustDecodeBounds = false;
            opciones.inSampleSize = escala;

            Bitmap imagen;

            try (InputStream entrada =
                         contexto.getContentResolver()
                                 .openInputStream(uri)) {

                imagen = BitmapFactory.decodeStream(
                        entrada,
                        null,
                        opciones
                );
            }

            if (imagen == null) {
                throw new Exception("No se pudo leer la imagen");
            }

            try (FileOutputStream salida =
                         new FileOutputStream(archivo)) {

                boolean guardada = imagen.compress(
                        Bitmap.CompressFormat.JPEG,
                        90,
                        salida
                );

                if (!guardada) {
                    throw new Exception("No se pudo guardar la imagen");
                }
            }

            imagen.recycle();

            Toast.makeText(
                    contexto,
                    "Fotografía actualizada",
                    Toast.LENGTH_SHORT
            ).show();

        } catch (Exception e) {

            Toast.makeText(
                    requireContext(),
                    "No se pudo guardar la fotografía",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    private void cargarFotografiaPerfil() {

        if (imagenPerfil == null) {
            return;
        }

        File archivo = new File(
                requireContext().getFilesDir(),
                ARCHIVO_FOTO
        );

        if (archivo.exists()) {

            Bitmap imagen = BitmapFactory.decodeFile(
                    archivo.getAbsolutePath()
            );

            if (imagen != null) {
                imagenPerfil.setImageBitmap(imagen);
            }
        }
    }

    // ==================================================
    // GALERÍA DE FOTOGRAFÍAS
    // ==================================================

    private void mostrarGaleria() {

        TextView encabezado = crearTexto(
                "Mi progreso físico",
                18,
                true
        );

        encabezado.setTextColor(
                obtenerColor(R.color.fittrack_green)
        );

        contenedorDinamico.addView(encabezado);

        TextView explicacion = crearTexto(
                "Guarda fotografías de tu evolución " +
                        "corporal y escribe una descripción " +
                        "para registrar tus avances.",
                14,
                false
        );

        explicacion.setPadding(
                0, dp(12), 0, dp(20)
        );

        contenedorDinamico.addView(explicacion);

        Button botonAgregar = crearBoton(
                "+ AGREGAR FOTO"
        );

        contenedorDinamico.addView(botonAgregar);

        botonAgregar.setOnClickListener(v ->
                selectorFotoGaleria.launch("image/*")
        );

        TextView tituloGaleria = crearTexto(
                "FOTOGRAFÍAS GUARDADAS",
                15,
                true
        );

        tituloGaleria.setTextColor(
                obtenerColor(R.color.fittrack_green)
        );

        tituloGaleria.setPadding(
                0, dp(28), 0, dp(16)
        );

        contenedorDinamico.addView(tituloGaleria);

        ArrayList<GaleriaFotos.Foto> fotos =
                galeriaFotos.obtenerFotos();

        if (fotos.isEmpty()) {

            mostrarMensaje(
                    "Todavía no tienes fotografías.\n\n" +
                            "Presiona AGREGAR FOTO para comenzar."
            );

            return;
        }

        for (int i = fotos.size() - 1; i >= 0; i--) {

            agregarTarjetaFoto(fotos.get(i));
        }
    }

    // ==================================================
    // DIÁLOGO PARA AGREGAR UNA FOTOGRAFÍA
    // ==================================================

    private void mostrarDialogoNuevaFoto(Uri uri) {

        if (!isAdded()) {
            return;
        }

        Context contexto = requireContext();

        // Contenedor del formulario.
        LinearLayout contenedor = new LinearLayout(contexto);

        contenedor.setOrientation(LinearLayout.VERTICAL);

        contenedor.setBackgroundColor(Color.WHITE);

        contenedor.setPadding(
                dp(20),
                dp(12),
                dp(20),
                dp(12)
        );

        // Título visible en negro.
        TextView titulo = crearTextoDialogo(
                "Nueva fotografía",
                20,
                true
        );

        titulo.setPadding(
                0, 0, 0, dp(12)
        );

        contenedor.addView(titulo);

        // Instrucciones.
        TextView instrucciones = crearTextoDialogo(
                "Escribe una descripción de tu progreso.",
                15,
                false
        );

        instrucciones.setPadding(
                0, 0, 0, dp(12)
        );

        contenedor.addView(instrucciones);

        // Campo de descripción.
        EditText campoDescripcion =
                crearCampoDialogo(
                        "Ejemplo: progreso de espalda, primer mes..."
                );

        contenedor.addView(campoDescripcion);

        // Crear el diálogo con tema claro.
        AlertDialog dialogo = new AlertDialog.Builder(
                contexto,
                AlertDialog.THEME_HOLO_LIGHT
        )
                .setView(contenedor)
                .setNegativeButton(
                        "CANCELAR",
                        (d, opcion) -> {
                        }
                )
                .setPositiveButton(
                        "GUARDAR",
                        null
                )
                .create();

        dialogo.setOnShowListener(d -> {

            Button botonGuardar =
                    dialogo.getButton(
                            AlertDialog.BUTTON_POSITIVE
                    );

            botonGuardar.setOnClickListener(v -> {

                String descripcion =
                        campoDescripcion
                                .getText()
                                .toString()
                                .trim();

                if (descripcion.isEmpty()) {

                    descripcion = "Sin descripción";
                }

                boolean guardada = galeriaFotos.agregarFoto(
                        uri,
                        descripcion
                );

                if (guardada) {

                    Toast.makeText(
                            requireContext(),
                            "Fotografía guardada",
                            Toast.LENGTH_SHORT
                    ).show();

                    dialogo.dismiss();

                    if ("Fotos".equals(opcionActual)) {
                        mostrarContenido("Fotos");
                    }

                } else {

                    Toast.makeText(
                            requireContext(),
                            "No se pudo guardar la fotografía",
                            Toast.LENGTH_LONG
                    ).show();
                }
            });
        });

        dialogo.show();
    }

    // ==================================================
    // TARJETAS DE LA GALERÍA
    // ==================================================

    private void agregarTarjetaFoto(
            GaleriaFotos.Foto foto) {

        LinearLayout tarjeta =
                new LinearLayout(requireContext());

        tarjeta.setOrientation(LinearLayout.VERTICAL);

        tarjeta.setBackgroundColor(
                obtenerColor(R.color.fittrack_surface)
        );

        tarjeta.setPadding(
                dp(10),
                dp(10),
                dp(10),
                dp(12)
        );

        LinearLayout.LayoutParams parametrosTarjeta =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        parametrosTarjeta.bottomMargin = dp(18);

        contenedorDinamico.addView(
                tarjeta,
                parametrosTarjeta
        );

        ImageView imagen = new ImageView(requireContext());

        imagen.setScaleType(
                ImageView.ScaleType.FIT_CENTER
        );

        BitmapFactory.Options opciones =
                new BitmapFactory.Options();

        opciones.inSampleSize = 2;

        Bitmap bitmap = BitmapFactory.decodeFile(
                foto.ruta,
                opciones
        );

        if (bitmap != null) {
            imagen.setImageBitmap(bitmap);
        }

        LinearLayout.LayoutParams parametrosImagen =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(180)
                );

        tarjeta.addView(
                imagen,
                parametrosImagen
        );

        TextView fecha = crearTexto(
                foto.fecha,
                12,
                false
        );

        fecha.setTextColor(
                obtenerColor(R.color.fittrack_green)
        );

        fecha.setPadding(
                0, dp(12), 0, dp(6)
        );

        tarjeta.addView(fecha);

        TextView descripcion = crearTexto(
                foto.descripcion,
                14,
                false
        );

        tarjeta.addView(descripcion);

        imagen.setOnClickListener(v ->
                mostrarFotoAmpliada(foto)
        );

        Button botonVer = crearBoton(
                "VER FOTOGRAFÍA"
        );

        tarjeta.addView(botonVer);

        botonVer.setOnClickListener(v ->
                mostrarFotoAmpliada(foto)
        );

        Button botonEditar = crearBoton(
                "EDITAR DESCRIPCIÓN"
        );

        tarjeta.addView(botonEditar);

        botonEditar.setOnClickListener(v ->
                mostrarDialogoEditarFoto(foto)
        );

        Button botonEliminar = crearBoton(
                "ELIMINAR"
        );

        tarjeta.addView(botonEliminar);

        botonEliminar.setOnClickListener(v ->
                confirmarEliminarFoto(foto)
        );
    }

    // ==================================================
    // AMPLIAR FOTOGRAFÍA
    // ==================================================

    private void mostrarFotoAmpliada(
            GaleriaFotos.Foto foto) {

        BitmapFactory.Options opciones =
                new BitmapFactory.Options();

        opciones.inSampleSize = 2;

        Bitmap bitmap = BitmapFactory.decodeFile(
                foto.ruta,
                opciones
        );

        if (bitmap == null) {

            Toast.makeText(
                    requireContext(),
                    "No se pudo abrir la fotografía",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        LinearLayout contenedor =
                new LinearLayout(requireContext());

        contenedor.setOrientation(
                LinearLayout.VERTICAL
        );

        contenedor.setPadding(
                dp(12),
                dp(12),
                dp(12),
                dp(12)
        );

        contenedor.setBackgroundColor(Color.WHITE);

        ImageView imagen =
                new ImageView(requireContext());

        imagen.setScaleType(
                ImageView.ScaleType.FIT_CENTER
        );

        imagen.setImageBitmap(bitmap);

        contenedor.addView(
                imagen,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(350)
                )
        );

        TextView descripcion = crearTextoDialogo(
                foto.fecha + "\n\n" + foto.descripcion,
                14,
                false
        );

        descripcion.setPadding(
                0, dp(12), 0, 0
        );

        contenedor.addView(descripcion);

        ScrollView desplazamiento =
                new ScrollView(requireContext());

        desplazamiento.addView(contenedor);

        new AlertDialog.Builder(
                requireContext(),
                AlertDialog.THEME_HOLO_LIGHT
        )
                .setView(desplazamiento)
                .setPositiveButton(
                        "CERRAR",
                        (dialogo, opcion) -> {
                        }
                )
                .show();
    }

    // ==================================================
    // EDITAR DESCRIPCIÓN
    // ==================================================

    private void mostrarDialogoEditarFoto(
            GaleriaFotos.Foto foto) {

        LinearLayout contenedor =
                new LinearLayout(requireContext());

        contenedor.setOrientation(
                LinearLayout.VERTICAL
        );

        contenedor.setBackgroundColor(Color.WHITE);

        contenedor.setPadding(
                dp(20),
                dp(12),
                dp(20),
                dp(12)
        );

        TextView titulo = crearTextoDialogo(
                "Editar descripción",
                20,
                true
        );

        titulo.setPadding(
                0, 0, 0, dp(12)
        );

        contenedor.addView(titulo);

        EditText campoDescripcion =
                crearCampoDialogo("Descripción");

        campoDescripcion.setText(
                foto.descripcion
        );

        contenedor.addView(campoDescripcion);

        AlertDialog dialogo = new AlertDialog.Builder(
                requireContext(),
                AlertDialog.THEME_HOLO_LIGHT
        )
                .setView(contenedor)
                .setNegativeButton(
                        "CANCELAR",
                        (d, opcion) -> {
                        }
                )
                .setPositiveButton(
                        "GUARDAR",
                        null
                )
                .create();

        dialogo.setOnShowListener(d -> {

            Button botonGuardar =
                    dialogo.getButton(
                            AlertDialog.BUTTON_POSITIVE
                    );

            botonGuardar.setOnClickListener(v -> {

                String nuevaDescripcion =
                        campoDescripcion
                                .getText()
                                .toString()
                                .trim();

                if (nuevaDescripcion.isEmpty()) {
                    nuevaDescripcion = "Sin descripción";
                }

                boolean actualizada =
                        galeriaFotos.actualizarDescripcion(
                                foto.id,
                                nuevaDescripcion
                        );

                if (actualizada) {

                    Toast.makeText(
                            requireContext(),
                            "Descripción actualizada",
                            Toast.LENGTH_SHORT
                    ).show();

                    dialogo.dismiss();

                    if ("Fotos".equals(opcionActual)) {
                        mostrarContenido("Fotos");
                    }

                } else {

                    Toast.makeText(
                            requireContext(),
                            "No se pudo actualizar",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            });
        });

        dialogo.show();
    }

    // ==================================================
    // ELIMINAR FOTOGRAFÍA
    // ==================================================

    private void confirmarEliminarFoto(
            GaleriaFotos.Foto foto) {

        LinearLayout contenedor =
                new LinearLayout(requireContext());

        contenedor.setOrientation(LinearLayout.VERTICAL);
        contenedor.setBackgroundColor(Color.WHITE);

        contenedor.setPadding(
                dp(20),
                dp(16),
                dp(20),
                dp(12)
        );

        TextView titulo = crearTextoDialogo(
                "Eliminar fotografía",
                20,
                true
        );

        contenedor.addView(titulo);

        TextView mensaje = crearTextoDialogo(
                "¿Seguro que deseas eliminar esta fotografía?\n\n" +
                        "Esta acción no se puede deshacer.",
                15,
                false
        );

        mensaje.setPadding(
                0, dp(12), 0, 0
        );

        contenedor.addView(mensaje);

        new AlertDialog.Builder(
                requireContext(),
                AlertDialog.THEME_HOLO_LIGHT
        )
                .setView(contenedor)
                .setNegativeButton(
                        "CANCELAR",
                        (dialogo, opcion) -> {
                        }
                )
                .setPositiveButton(
                        "ELIMINAR",
                        (dialogo, opcion) -> {

                            boolean eliminada =
                                    galeriaFotos.eliminarFoto(
                                            foto.id
                                    );

                            if (eliminada) {

                                Toast.makeText(
                                        requireContext(),
                                        "Fotografía eliminada",
                                        Toast.LENGTH_SHORT
                                ).show();

                                if ("Fotos".equals(opcionActual)) {
                                    mostrarContenido("Fotos");
                                }

                            } else {

                                Toast.makeText(
                                        requireContext(),
                                        "No se pudo eliminar",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                )
                .show();
    }

    // ==================================================
    // COMPONENTES PARA DIÁLOGOS CLAROS
    // ==================================================

    private TextView crearTextoDialogo(
            String texto,
            int tamano,
            boolean negrita) {

        TextView textView =
                new TextView(requireContext());

        textView.setText(texto);
        textView.setTextSize(tamano);
        textView.setTextColor(Color.BLACK);

        if (negrita) {

            textView.setTypeface(
                    null,
                    Typeface.BOLD
            );
        }

        return textView;
    }

    private EditText crearCampoDialogo(String sugerencia) {

        EditText campo =
                new EditText(requireContext());

        campo.setHint(sugerencia);

        campo.setTextSize(15);

        campo.setTextColor(Color.BLACK);

        campo.setHintTextColor(Color.DKGRAY);

        campo.setInputType(
                InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_FLAG_MULTI_LINE |
                        InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
        );

        campo.setMinLines(2);
        campo.setMaxLines(4);

        campo.setGravity(
                Gravity.TOP | Gravity.START
        );

        campo.setBackgroundTintList(
                ColorStateList.valueOf(
                        obtenerColor(R.color.fittrack_green)
                )
        );

        return campo;
    }

    // ==================================================
    // COMPONENTES GENERALES
    // ==================================================

    private Button crearBoton(String texto) {

        Button boton =
                new Button(requireContext());

        boton.setText(texto);
        boton.setTextSize(13);

        boton.setTextColor(
                obtenerColor(R.color.fittrack_background)
        );

        boton.setBackgroundTintList(
                ColorStateList.valueOf(
                        obtenerColor(R.color.fittrack_green)
                )
        );

        return boton;
    }

    private EditText crearCampoTexto(String sugerencia) {

        EditText campo =
                new EditText(requireContext());

        campo.setHint(sugerencia);
        campo.setTextSize(15);

        campo.setTextColor(
                obtenerColor(R.color.fittrack_text_primary)
        );

        campo.setHintTextColor(
                obtenerColor(R.color.fittrack_text_secondary)
        );

        campo.setBackgroundTintList(
                ColorStateList.valueOf(
                        obtenerColor(R.color.fittrack_green)
                )
        );

        return campo;
    }

    private TextView crearTexto(
            String texto,
            int tamano,
            boolean negrita) {

        TextView textView =
                new TextView(requireContext());

        textView.setText(texto);
        textView.setTextSize(tamano);

        textView.setTextColor(
                obtenerColor(R.color.fittrack_text_primary)
        );

        if (negrita) {

            textView.setTypeface(
                    null,
                    Typeface.BOLD
            );
        }

        return textView;
    }

    private void mostrarMensaje(String texto) {

        TextView mensaje = crearTexto(
                texto,
                16,
                false
        );

        mensaje.setPadding(
                dp(8),
                dp(16),
                dp(8),
                dp(16)
        );

        contenedorDinamico.addView(mensaje);
    }

    private int obtenerColor(int color) {

        return getResources().getColor(
                color,
                requireContext().getTheme()
        );
    }

    private int dp(int valor) {

        return Math.round(
                valor *
                        getResources()
                                .getDisplayMetrics()
                                .density
        );
    }
}