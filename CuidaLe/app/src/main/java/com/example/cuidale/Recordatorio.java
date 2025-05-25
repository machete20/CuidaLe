package com.example.cuidale;

public class Recordatorio implements Comparable<Recordatorio> {
    private String id;
    private String nombre;
    private String hora;

    // Constructor vacío requerido por Firebase
    public Recordatorio() {
    }

    // Constructor principal
    public Recordatorio(String nombre, String hora) {
        this.nombre = nombre;
        this.hora = hora;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    // Para ordenar por hora
    @Override
    public int compareTo(Recordatorio other) {
        return this.hora.compareTo(other.getHora());
    }
}

