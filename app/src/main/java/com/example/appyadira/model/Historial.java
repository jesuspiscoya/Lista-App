package com.example.appyadira.model;

public class Historial {
    int cod;
    String titulo;
    int cantidad;
    double total;
    int estado;

    public Historial(String titulo, int cantidad, double total) {
        this.titulo = titulo;
        this.cantidad = cantidad;
        this.total = total;
    }

    public Historial(int cod, String titulo, int cantidad, double total, int estado) {
        this.cod = cod;
        this.titulo = titulo;
        this.cantidad = cantidad;
        this.total = total;
        this.estado = estado;
    }

    public int getCod() {
        return cod;
    }

    public void setCod(int cod) {
        this.cod = cod;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
}
