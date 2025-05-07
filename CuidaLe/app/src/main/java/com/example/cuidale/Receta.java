package com.example.cuidale;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Receta implements Comparable<Receta> {
    private String nombre;
    private String fecha; // formato "dd/MM/yyyy"

    public Receta(String nombre, String fecha) {
        this.nombre = nombre;
        this.fecha = fecha;
    }

    public String getNombre() {
        return nombre;
    }

    public String getFecha() {
        return fecha;
    }

    @Override
    public int compareTo(Receta otra) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date f1 = sdf.parse(this.fecha);
            Date f2 = sdf.parse(otra.fecha);
            return f1.compareTo(f2); // orden ascendente
        } catch (ParseException e) {
            return 0; // en caso de error, no cambia el orden
        }
    }
}
