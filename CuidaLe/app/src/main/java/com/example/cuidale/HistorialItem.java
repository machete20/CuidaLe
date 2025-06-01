package com.example.cuidale;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HistorialItem {
    private String nombre;
    private String horaProgramada;
    private String horaTomada;
    private boolean fueTomada;

    public HistorialItem(String nombre, String horaProgramada, boolean fueTomada, String horaTomada) {
        this.nombre = nombre;
        this.horaProgramada = horaProgramada;
        this.fueTomada = fueTomada;
        this.horaTomada = horaTomada;
    }

    public String getNombre() { return nombre; }
    public String getHoraProgramada() { return horaProgramada; }
    public boolean isFueTomada() { return fueTomada; }
    public String getHoraTomada() { return horaTomada; }

    public String getDiferenciaTiempo() {
        if (horaTomada == null) return "No tomada";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date horaProg = sdf.parse(horaProgramada);
            Date horaReal = sdf.parse(horaTomada);

            long diff = (horaReal.getTime() - horaProg.getTime()) / (60 * 1000);
            return diff + " min";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error";
        }
    }
}
