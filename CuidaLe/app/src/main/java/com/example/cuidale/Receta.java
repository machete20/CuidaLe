package com.example.cuidale;

public class Receta {
    private String nombre;
    private String fecha;

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
}
