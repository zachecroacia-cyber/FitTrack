package com.fittrack.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class GraficaProgresoView extends View {

    private final Paint pintura = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final List<Float> valores = new ArrayList<>();
    private final List<String> etiquetas = new ArrayList<>();

    private int colorBarras = Color.rgb(168, 230, 61);

    public GraficaProgresoView(Context context) {
        super(context);
    }

    public GraficaProgresoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GraficaProgresoView(
            Context context,
            AttributeSet attrs,
            int defStyleAttr) {

        super(context, attrs, defStyleAttr);
    }

    public void establecerDatos(
            List<Float> nuevosValores,
            List<String> nuevasEtiquetas,
            int nuevoColor) {

        valores.clear();
        etiquetas.clear();

        if (nuevosValores != null) {
            valores.addAll(nuevosValores);
        }

        if (nuevasEtiquetas != null) {
            etiquetas.addAll(nuevasEtiquetas);
        }

        colorBarras = nuevoColor;

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float ancho = getWidth();
        float alto = getHeight();

        if (valores.isEmpty()) {

            pintura.setColor(Color.LTGRAY);
            pintura.setTextSize(32f);
            pintura.setTextAlign(Paint.Align.CENTER);

            canvas.drawText(
                    "Sin datos",
                    ancho / 2f,
                    alto / 2f,
                    pintura
            );

            return;
        }

        float margenIzquierdo = 48f;
        float margenDerecho = 20f;
        float margenSuperior = 35f;
        float margenInferior = 65f;

        float anchoDisponible =
                ancho - margenIzquierdo - margenDerecho;

        float altoDisponible =
                alto - margenSuperior - margenInferior;

        if (anchoDisponible <= 0 || altoDisponible <= 0) {
            return;
        }

        // Encontrar el valor máximo
        float valorMaximo = 0f;

        for (float valor : valores) {
            if (valor > valorMaximo) {
                valorMaximo = valor;
            }
        }

        if (valorMaximo <= 0f) {
            valorMaximo = 1f;
        }

        // Dibujar línea horizontal inferior
        pintura.setColor(Color.GRAY);
        pintura.setStrokeWidth(2f);

        float lineaBase = margenSuperior + altoDisponible;

        canvas.drawLine(
                margenIzquierdo,
                lineaBase,
                ancho - margenDerecho,
                lineaBase,
                pintura
        );

        float espacioPorBarra =
                anchoDisponible / valores.size();

        float anchoBarra = espacioPorBarra * 0.55f;

        // Dibujar barras
        for (int i = 0; i < valores.size(); i++) {

            float valor = Math.max(0f, valores.get(i));

            float alturaBarra =
                    (valor / valorMaximo) * altoDisponible;

            float centroX =
                    margenIzquierdo
                            + espacioPorBarra * i
                            + espacioPorBarra / 2f;

            float izquierda = centroX - anchoBarra / 2f;
            float derecha = centroX + anchoBarra / 2f;

            float arriba = lineaBase - alturaBarra;

            pintura.setColor(colorBarras);
            pintura.setStyle(Paint.Style.FILL);

            RectF rectangulo = new RectF(
                    izquierda,
                    arriba,
                    derecha,
                    lineaBase
            );

            canvas.drawRoundRect(
                    rectangulo,
                    8f,
                    8f,
                    pintura
            );

            // Mostrar valor encima de cada barra
            pintura.setColor(Color.WHITE);
            pintura.setTextSize(25f);
            pintura.setTextAlign(Paint.Align.CENTER);

            canvas.drawText(
                    String.valueOf(valor),
                    centroX,
                    Math.max(25f, arriba - 10f),
                    pintura
            );

            // Mostrar etiqueta inferior
            if (i < etiquetas.size()) {

                pintura.setColor(Color.LTGRAY);
                pintura.setTextSize(22f);

                canvas.drawText(
                        etiquetas.get(i),
                        centroX,
                        lineaBase + 32f,
                        pintura
                );
            }
        }
    }
}