package com.example.cuidale;

public class Medication {
    private String hora;
    private String nombre;
    private boolean tomada;
    private boolean seleccionado;

    public Medication(String hora, String nombre, boolean tomada) {
        this.hora = hora;
        this.nombre = nombre;
        this.tomada = tomada;
        this.seleccionado = false;
    }

    public String getHora() { return hora; }
    public String getNombre() { return nombre; }
    public boolean isTomada() { return tomada; }
    public void setTomada(boolean tomada) { this.tomada = tomada; }

    public boolean isSeleccionado() { return seleccionado; }
    public void setSeleccionado(boolean seleccionado) { this.seleccionado = seleccionado; }
}
