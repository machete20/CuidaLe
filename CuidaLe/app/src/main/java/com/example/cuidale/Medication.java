package com.example.cuidale;

public class Medication {
    private String hora;
    private String nombre;
    private boolean tomado;

    public Medication(String hora, String nombre, boolean tomado) {
        this.hora = hora;
        this.nombre = nombre;
        this.tomado = tomado;
    }

    public String getHora() {
        return hora;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean isTomado() {
        return tomado;
    }

    public void setTomado(boolean tomado) {
        this.tomado = tomado;
    }
}
