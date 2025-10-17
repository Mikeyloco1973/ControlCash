package com.example.controlcash;

public class Compra {
    public int id;
    public String nombre;
    public double precio;
    public int cantidad;
    public double subtotal;
    public double descuento;
    public double total;
    public String usuario;

    public Compra(int id, String nombre, double precio, int cantidad, double subtotal, double descuento, double total, String usuarioCompra) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.cantidad = cantidad;
        this.subtotal = subtotal;
        this.descuento = descuento;
        this.total = total;
        this.usuario = usuarioCompra;
    }
}
