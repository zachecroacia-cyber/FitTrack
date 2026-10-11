package com.fittrack.app;

import android.content.Context;
import android.content.SharedPreferences;
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

public class VideosFitTrack {

    private static final String PREFERENCIAS = "FitTrackVideos";
    private static final String CLAVE_VIDEOS = "videos";

    private final Context contexto;

    public VideosFitTrack(Context contexto) {
        this.contexto = contexto.getApplicationContext();
    }

    // Representa un video guardado.
    public static class Video {

        public String id;
        public String nombre;
        public String descripcion;
        public String fecha;
        public String ruta;

        public Video(
                String id,
                String nombre,
                String descripcion,
                String fecha,
                String ruta) {

            this.id = id;
            this.nombre = nombre;
            this.descripcion = descripcion;
            this.fecha = fecha;
            this.ruta = ruta;
        }
    }

    // Obtener los videos guardados.
    public ArrayList<Video> obtenerVideos() {

        ArrayList<Video> videos = new ArrayList<>();

        SharedPreferences preferencias =
                contexto.getSharedPreferences(
                        PREFERENCIAS,
                        Context.MODE_PRIVATE
                );

        String datos = preferencias.getString(
                CLAVE_VIDEOS,
                "[]"
        );

        try {

            JSONArray arreglo = new JSONArray(datos);

            for (int i = 0; i < arreglo.length(); i++) {

                JSONObject objeto = arreglo.getJSONObject(i);

                Video video = new Video(
                        objeto.optString("id"),
                        objeto.optString("nombre"),
                        objeto.optString("descripcion"),
                        objeto.optString("fecha"),
                        objeto.optString("ruta")
                );

                if (new File(video.ruta).exists()) {
                    videos.add(video);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return videos;
    }

    // Guardar un nuevo video.
    public boolean agregarVideo(
            Uri uri,
            String nombre,
            String descripcion) {

        if (uri == null || nombre == null ||
                nombre.trim().isEmpty()) {
            return false;
        }

        String id = UUID.randomUUID().toString();

        File carpeta = new File(
                contexto.getFilesDir(),
                "videos_entrenamiento"
        );

        if (!carpeta.exists() && !carpeta.mkdirs()) {
            return false;
        }

        File archivo = new File(
                carpeta,
                id + ".mp4"
        );

        try {

            try (InputStream entrada =
                         contexto.getContentResolver()
                                 .openInputStream(uri);

                 FileOutputStream salida =
                         new FileOutputStream(archivo)) {

                if (entrada == null) {
                    return false;
                }

                byte[] buffer = new byte[8192];
                int cantidad;

                while ((cantidad = entrada.read(buffer)) != -1) {

                    salida.write(
                            buffer,
                            0,
                            cantidad
                    );
                }
            }

            String fecha = new SimpleDateFormat(
                    "dd/MM/yyyy HH:mm",
                    Locale.getDefault()
            ).format(new Date());

            ArrayList<Video> videos = obtenerVideos();

            videos.add(new Video(
                    id,
                    nombre.trim(),
                    descripcion == null
                            ? ""
                            : descripcion.trim(),
                    fecha,
                    archivo.getAbsolutePath()
            ));

            boolean guardado = guardarLista(videos);

            if (!guardado) {
                archivo.delete();
            }

            return guardado;

        } catch (Exception e) {

            e.printStackTrace();
            archivo.delete();

            return false;
        }
    }

    // Editar nombre y descripción.
    public boolean actualizarVideo(
            String id,
            String nombre,
            String descripcion) {

        ArrayList<Video> videos = obtenerVideos();

        for (Video video : videos) {

            if (video.id.equals(id)) {

                video.nombre = nombre;
                video.descripcion = descripcion;

                return guardarLista(videos);
            }
        }

        return false;
    }

    // Eliminar un video.
    public boolean eliminarVideo(String id) {

        ArrayList<Video> videos = obtenerVideos();

        Video seleccionado = null;

        for (Video video : videos) {

            if (video.id.equals(id)) {
                seleccionado = video;
                break;
            }
        }

        if (seleccionado == null) {
            return false;
        }

        videos.remove(seleccionado);

        if (!guardarLista(videos)) {
            return false;
        }

        File archivo = new File(seleccionado.ruta);

        if (archivo.exists()) {
            archivo.delete();
        }

        return true;
    }

    // Guardar la información de los videos.
    private boolean guardarLista(ArrayList<Video> videos) {

        JSONArray arreglo = new JSONArray();

        try {

            for (Video video : videos) {

                JSONObject objeto = new JSONObject();

                objeto.put("id", video.id);
                objeto.put("nombre", video.nombre);
                objeto.put(
                        "descripcion",
                        video.descripcion
                );
                objeto.put("fecha", video.fecha);
                objeto.put("ruta", video.ruta);

                arreglo.put(objeto);
            }

            SharedPreferences preferencias =
                    contexto.getSharedPreferences(
                            PREFERENCIAS,
                            Context.MODE_PRIVATE
                    );

            return preferencias.edit()
                    .putString(
                            CLAVE_VIDEOS,
                            arreglo.toString()
                    )
                    .commit();

        } catch (JSONException e) {

            e.printStackTrace();
            return false;
        }
    }
}