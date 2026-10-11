package com.fittrack.app;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Locale;

public class ProgresoActivity extends AppCompatActivity {

    private Spinner spinnerEjercicios;

    private TextView txtEntrenamientosRegistrados;
    private TextView txtPesoMaximo;
    private TextView txtRepeticionesMaximas;
    private TextView txtUltimoEntrenamiento;
    private TextView txtComparacion;

    private GraficaProgresoView graficaPeso;
    private GraficaProgresoView graficaRepeticiones;
    private GraficaProgresoView graficaVolumen;

    private final ArrayList<String> listaEntrenamientos =
            new ArrayList<>();

    private final ArrayList<String> listaEjercicios =
            new ArrayList<>();

    // Datos individuales de cada entrenamiento
    private static class DatosSesion {

        String fecha;
        double pesoMaximo;
        int repeticionesMaximas;
        double volumenTotal;
        String detalleSeries;

        DatosSesion(String fecha) {

            this.fecha = fecha;
            this.pesoMaximo = 0;
            this.repeticionesMaximas = 0;
            this.volumenTotal = 0;
            this.detalleSeries = "";
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_progreso);

        spinnerEjercicios =
                findViewById(R.id.spinnerEjercicios);

        txtEntrenamientosRegistrados =
                findViewById(R.id.txtEntrenamientosRegistrados);

        txtPesoMaximo =
                findViewById(R.id.txtPesoMaximo);

        txtRepeticionesMaximas =
                findViewById(R.id.txtRepeticionesMaximas);

        txtUltimoEntrenamiento =
                findViewById(R.id.txtUltimoEntrenamiento);

        txtComparacion =
                findViewById(R.id.txtComparacion);

        graficaPeso =
                findViewById(R.id.graficaPeso);

        graficaRepeticiones =
                findViewById(R.id.graficaRepeticiones);

        graficaVolumen =
                findViewById(R.id.graficaVolumen);

        cargarDatos();

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {

                    Insets systemBars = insets.getInsets(
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

    private void cargarDatos() {

        SharedPreferences preferencias =
                getSharedPreferences(
                        "FitTrackDatos",
                        MODE_PRIVATE
                );

        String historial = preferencias.getString(
                "historial_entrenamientos",
                ""
        );

        listaEntrenamientos.clear();
        listaEjercicios.clear();

        if (historial == null || historial.isEmpty()) {

            listaEjercicios.add("Sin entrenamientos");

            configurarSpinner();

            return;
        }

        String[] entrenamientos = historial.split("\\|");

        for (String entrenamiento : entrenamientos) {

            String[] partes = entrenamiento.split("#");

            if (partes.length < 3) {
                continue;
            }

            listaEntrenamientos.add(entrenamiento);

            String ejercicio = partes[1].trim();

            boolean existe = false;

            for (String guardado : listaEjercicios) {

                if (guardado.equalsIgnoreCase(ejercicio)) {

                    existe = true;

                    break;
                }
            }

            if (!existe) {

                listaEjercicios.add(ejercicio);
            }
        }

        if (listaEjercicios.isEmpty()) {

            listaEjercicios.add("Sin entrenamientos");
        }

        configurarSpinner();
    }

    private void configurarSpinner() {

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        listaEjercicios
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerEjercicios.setAdapter(adapter);

        spinnerEjercicios.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {

                        String ejercicio =
                                listaEjercicios.get(position);

                        if (ejercicio.equals("Sin entrenamientos")) {

                            mostrarSinDatos();

                        } else {

                            calcularProgreso(ejercicio);
                        }
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent) {
                    }
                }
        );
    }

    private void calcularProgreso(String ejercicioSeleccionado) {

        ArrayList<DatosSesion> sesiones =
                new ArrayList<>();

        double pesoMaximoGeneral = 0;

        int repeticionesMaximasGeneral = 0;

        for (String entrenamiento : listaEntrenamientos) {

            String[] partes = entrenamiento.split("#");

            if (partes.length < 3) {
                continue;
            }

            String fecha = partes[0];

            String ejercicio = partes[1];

            String series = partes[2];

            if (!ejercicio.equalsIgnoreCase(
                    ejercicioSeleccionado)) {

                continue;
            }

            DatosSesion sesion = new DatosSesion(fecha);

            String[] listaSeries = series.split(";");

            StringBuilder detalle = new StringBuilder();

            detalle.append(fecha).append("\n");

            for (int i = 0; i < listaSeries.length; i++) {

                String[] datosSerie = listaSeries[i].split(",");

                if (datosSerie.length != 2) {
                    continue;
                }

                try {

                    int repeticiones = Integer.parseInt(
                            datosSerie[0].trim()
                    );

                    double peso = Double.parseDouble(
                            datosSerie[1].trim()
                                    .replace(",", ".")
                    );

                    // Peso máximo de esta sesión
                    sesion.pesoMaximo = Math.max(
                            sesion.pesoMaximo,
                            peso
                    );

                    // Repeticiones máximas de esta sesión
                    sesion.repeticionesMaximas = Math.max(
                            sesion.repeticionesMaximas,
                            repeticiones
                    );

                    // Volumen total: peso × repeticiones
                    sesion.volumenTotal +=
                            peso * repeticiones;

                    detalle.append("Serie ")
                            .append(i + 1)
                            .append(": ")
                            .append(repeticiones)
                            .append(" reps × ")
                            .append(mostrarNumero(peso))
                            .append(" kg\n");

                } catch (NumberFormatException e) {

                    // Ignorar series con datos inválidos.
                }
            }

            sesion.detalleSeries = detalle.toString();

            pesoMaximoGeneral = Math.max(
                    pesoMaximoGeneral,
                    sesion.pesoMaximo
            );

            repeticionesMaximasGeneral = Math.max(
                    repeticionesMaximasGeneral,
                    sesion.repeticionesMaximas
            );

            sesiones.add(sesion);
        }

        if (sesiones.isEmpty()) {

            mostrarSinDatos();

            return;
        }

        // Mostrar estadísticas generales
        txtEntrenamientosRegistrados.setText(
                "Entrenamientos registrados: "
                        + sesiones.size()
        );

        txtPesoMaximo.setText(
                "Peso máximo: "
                        + mostrarNumero(pesoMaximoGeneral)
                        + " kg"
        );

        txtRepeticionesMaximas.setText(
                "Máximo de repeticiones: "
                        + repeticionesMaximasGeneral
        );

        // Mostrar el último entrenamiento
        DatosSesion ultima =
                sesiones.get(sesiones.size() - 1);

        txtUltimoEntrenamiento.setText(
                "Último entrenamiento:\n"
                        + ultima.detalleSeries
        );

        // Actualizar gráficas
        actualizarGraficas(sesiones);

        // Comparar los dos últimos entrenamientos
        actualizarComparacion(sesiones);
    }

    private void actualizarGraficas(
            ArrayList<DatosSesion> sesiones) {

        ArrayList<Float> pesos =
                new ArrayList<>();

        ArrayList<Float> repeticiones =
                new ArrayList<>();

        ArrayList<Float> volumenes =
                new ArrayList<>();

        ArrayList<String> fechas =
                new ArrayList<>();

        for (DatosSesion sesion : sesiones) {

            // Guardar peso máximo de la sesión
            pesos.add(
                    (float) sesion.pesoMaximo
            );

            // Guardar repeticiones máximas
            repeticiones.add(
                    (float) sesion.repeticionesMaximas
            );

            // Guardar volumen total
            volumenes.add(
                    (float) sesion.volumenTotal
            );

            /*
             * CORRECCIÓN:
             *
             * Antes se mostraba la fecha completa:
             *
             * 27/09/2026 20:26
             *
             * Ahora se muestra:
             *
             * 27/09
             *
             * Esto evita que las fechas se
             * superpongan debajo de las barras.
             */

            String fechaCorta = sesion.fecha.trim();

            if (fechaCorta.length() >= 5) {

                fechaCorta =
                        fechaCorta.substring(0, 5);
            }

            fechas.add(fechaCorta);
        }

        // GRÁFICA VERDE: PESO MÁXIMO
        graficaPeso.establecerDatos(
                pesos,
                fechas,
                Color.rgb(168, 230, 61)
        );

        // GRÁFICA AZUL: REPETICIONES
        graficaRepeticiones.establecerDatos(
                repeticiones,
                fechas,
                Color.rgb(64, 156, 255)
        );

        // GRÁFICA MORADA: VOLUMEN
        graficaVolumen.establecerDatos(
                volumenes,
                fechas,
                Color.rgb(180, 110, 255)
        );
    }

    private void actualizarComparacion(
            ArrayList<DatosSesion> sesiones) {

        if (sesiones.size() < 2) {

            txtComparacion.setText(
                    "Necesitas al menos dos entrenamientos "
                            + "para comparar tu progreso."
            );

            return;
        }

        DatosSesion anterior =
                sesiones.get(sesiones.size() - 2);

        DatosSesion actual =
                sesiones.get(sesiones.size() - 1);

        StringBuilder comparacion =
                new StringBuilder();

        comparacion.append("Anterior: ")
                .append(anterior.fecha)
                .append("\n");

        comparacion.append("Actual: ")
                .append(actual.fecha)
                .append("\n\n");

        // Comparación del peso máximo
        comparacion.append("PESO MÁXIMO\n")
                .append(compararValores(
                        anterior.pesoMaximo,
                        actual.pesoMaximo,
                        "kg"
                ))
                .append("\n\n");

        // Comparación de repeticiones
        comparacion.append("REPETICIONES MÁXIMAS\n")
                .append(compararValores(
                        anterior.repeticionesMaximas,
                        actual.repeticionesMaximas,
                        "reps"
                ))
                .append("\n\n");

        // Comparación del volumen
        comparacion.append("VOLUMEN TOTAL\n")
                .append(compararValores(
                        anterior.volumenTotal,
                        actual.volumenTotal,
                        "kg"
                ));

        txtComparacion.setText(
                comparacion.toString()
        );
    }

    private String compararValores(
            double anterior,
            double actual,
            String unidad) {

        String estado;

        if (actual > anterior) {

            estado = "↑ Aumentó";

        } else if (actual < anterior) {

            estado = "↓ Disminuyó";

        } else {

            estado = "= Sin cambios";
        }

        String resultado =
                mostrarNumero(anterior)
                        + " " + unidad
                        + " → "
                        + mostrarNumero(actual)
                        + " " + unidad
                        + "\n"
                        + estado;

        if (anterior > 0) {

            double porcentaje =
                    ((actual - anterior) / anterior) * 100;

            resultado += " ("
                    + String.format(
                    Locale.getDefault(),
                    "%+.1f%%",
                    porcentaje
            )
                    + ")";
        }

        return resultado;
    }

    private String mostrarNumero(double numero) {

        if (numero == (long) numero) {

            return String.valueOf(
                    (long) numero
            );
        }

        return String.format(
                Locale.getDefault(),
                "%.1f",
                numero
        );
    }

    private void mostrarSinDatos() {

        txtEntrenamientosRegistrados.setText(
                "Entrenamientos registrados: 0"
        );

        txtPesoMaximo.setText(
                "Peso máximo: 0 kg"
        );

        txtRepeticionesMaximas.setText(
                "Máximo de repeticiones: 0"
        );

        txtUltimoEntrenamiento.setText(
                "Último entrenamiento: sin datos"
        );

        txtComparacion.setText(
                "Necesitas al menos dos entrenamientos "
                        + "para comparar tu progreso."
        );

        ArrayList<Float> vacios =
                new ArrayList<>();

        ArrayList<String> etiquetas =
                new ArrayList<>();

        graficaPeso.establecerDatos(
                vacios,
                etiquetas,
                Color.rgb(168, 230, 61)
        );

        graficaRepeticiones.establecerDatos(
                vacios,
                etiquetas,
                Color.rgb(64, 156, 255)
        );

        graficaVolumen.establecerDatos(
                vacios,
                etiquetas,
                Color.rgb(180, 110, 255)
        );
    }
}