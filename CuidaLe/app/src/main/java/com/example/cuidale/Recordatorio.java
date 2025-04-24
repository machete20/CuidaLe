package com.example.cuidale;

public class Recordatorio {
    private String nombre;
    private String hora;

    public Recordatorio(String nombre, String hora) {
        this.nombre = nombre;
        this.hora = hora;
    }

    public String getNombre() {
        return nombre;
    }

    public String getHora() {
        return hora;
    }
    
    public int compareTo(Recordatorio other) {
        return this.hora.compareTo(other.getHora());
    }
}
