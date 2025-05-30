package com.example.cuidale;

public class Medication {
    private String hora;
    private String nombre;
    private boolean tomada;
    private boolean seleccionado;
    private String horaTomada; // NUEVO

    public Medication(String hora, String nombre, boolean tomada) {
        this.hora = hora;
        this.nombre = nombre;
        this.tomada = tomada;
        this.seleccionado = false;
        this.horaTomada = null;
    }

    public String getHora() { return hora; }
    public String getNombre() { return nombre; }
    public boolean isTomada() { return tomada; }
    public void setTomada(boolean tomada) { this.tomada = tomada; }

    public boolean isSeleccionado() { return seleccionado; }
    public void setSeleccionado(boolean seleccionado) { this.seleccionado = seleccionado; }

    public String getHoraTomada() { return horaTomada; }
    public void setHoraTomada(String horaTomada) { this.horaTomada = horaTomada; }
}
