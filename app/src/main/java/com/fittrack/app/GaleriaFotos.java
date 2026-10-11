package com.fittrack.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class GaleriaFotos {

    private static final String PREFERENCIAS = "FitTrackGaleria";
    private static final String CLAVE_FOTOS = "fotografias";

    private final Context contexto;

    public GaleriaFotos(Context contexto) {
        this.contexto = contexto.getApplicationContext();
    }

    // Representa una fotografía guardada.
    public static class Foto {

        public String id;
        public String ruta;
        public String fecha;
        public String descripcion;

        public Foto(
                String id,
                String ruta,
                String fecha,
                String descripcion) {

            this.id = id;
            this.ruta = ruta;
            this.fecha = fecha;
            this.descripcion = descripcion;
        }
    }

    // Recuperar todas las fotografías.
    public ArrayList<Foto> obtenerFotos() {

        ArrayList<Foto> fotos = new ArrayList<>();

        SharedPreferences preferencias =
                contexto.getSharedPreferences(
                        PREFERENCIAS,
                        Context.MODE_PRIVATE
                );

        String datos = preferencias.getString(
                CLAVE_FOTOS,
                "[]"
        );

        try {

            JSONArray arreglo = new JSONArray(datos);

            for (int i = 0; i < arreglo.length(); i++) {

                JSONObject objeto = arreglo.getJSONObject(i);

                Foto foto = new Foto(
                        objeto.optString("id"),
                        objeto.optString("ruta"),
                        objeto.optString("fecha"),
                        objeto.optString("descripcion")
                );

                // Mostrar solamente archivos existentes.
                if (new File(foto.ruta).exists()) {
                    fotos.add(foto);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return fotos;
    }

    // Agregar una fotografía desde la galería.
    public boolean agregarFoto(
            Uri uri,
            String descripcion) {

        String id = UUID.randomUUID().toString();

        File carpeta = new File(
                contexto.getFilesDir(),
                "fotos_progreso"
        );

        if (!carpeta.exists() && !carpeta.mkdirs()) {
            return false;
        }

        File archivo = new File(
                carpeta,
                id + ".jpg"
        );

        Bitmap imagen = null;

        try {

            // Consultar dimensiones sin cargar toda la imagen.
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

            int escala = 1;

            while (opciones.outWidth / escala > 1600 ||
                    opciones.outHeight / escala > 1600) {

                escala *= 2;
            }

            opciones.inJustDecodeBounds = false;
            opciones.inSampleSize = escala;

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
                return false;
            }

            try (FileOutputStream salida =
                         new FileOutputStream(archivo)) {

                if (!imagen.compress(
                        Bitmap.CompressFormat.JPEG,
                        90,
                        salida)) {

                    return false;
                }
            }

            String fecha =
                    new SimpleDateFormat(
                            "dd/MM/yyyy HH:mm",
                            Locale.getDefault()
                    ).format(new Date());

            ArrayList<Foto> fotos = obtenerFotos();

            fotos.add(new Foto(
                    id,
                    archivo.getAbsolutePath(),
                    fecha,
                    descripcion
            ));

            boolean guardado = guardarLista(fotos);

            if (!guardado) {
                archivo.delete();
            }

            return guardado;

        } catch (Exception e) {

            e.printStackTrace();
            archivo.delete();

            return false;

        } finally {

            if (imagen != null && !imagen.isRecycled()) {
                imagen.recycle();
            }
        }
    }

    // Actualizar la descripción de una fotografía.
    public boolean actualizarDescripcion(
            String id,
            String nuevaDescripcion) {

        ArrayList<Foto> fotos = obtenerFotos();

        boolean encontrada = false;

        for (Foto foto : fotos) {

            if (foto.id.equals(id)) {

                foto.descripcion = nuevaDescripcion;
                encontrada = true;
                break;
            }
        }

        return encontrada && guardarLista(fotos);
    }

    // Eliminar una fotografía.
    public boolean eliminarFoto(String id) {

        ArrayList<Foto> fotos = obtenerFotos();

        Foto seleccionada = null;

        for (Foto foto : fotos) {

            if (foto.id.equals(id)) {
                seleccionada = foto;
                break;
            }
        }

        if (seleccionada == null) {
            return false;
        }

        fotos.remove(seleccionada);

        // Primero actualizar el registro.
        if (!guardarLista(fotos)) {
            return false;
        }

        // Después eliminar el archivo físico.
        File archivo = new File(seleccionada.ruta);

        if (archivo.exists()) {
            archivo.delete();
        }

        return true;
    }

    // Guardar el listado de fotografías.
    private boolean guardarLista(ArrayList<Foto> fotos) {

        JSONArray arreglo = new JSONArray();

        try {

            for (Foto foto : fotos) {

                JSONObject objeto = new JSONObject();

                objeto.put("id", foto.id);
                objeto.put("ruta", foto.ruta);
                objeto.put("fecha", foto.fecha);
                objeto.put(
                        "descripcion",
                        foto.descripcion
                );

                arreglo.put(objeto);
            }

            SharedPreferences preferencias =
                    contexto.getSharedPreferences(
                            PREFERENCIAS,
                            Context.MODE_PRIVATE
                    );

            return preferencias.edit()
                    .putString(
                            CLAVE_FOTOS,
                            arreglo.toString()
                    )
                    .commit();

        } catch (JSONException e) {

            e.printStackTrace();
            return false;
        }
    }
}