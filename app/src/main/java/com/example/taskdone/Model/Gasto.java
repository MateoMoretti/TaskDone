package com.example.taskdone.Model;

public class Gasto {

    String fecha;
    String tipo_moneda;
    String cantidad;
    String motivo;
    String ingreso;

    public Gasto( String fecha, String tipo_moneda, String cantidad, String motivo, String ingreso){
        this.fecha = fecha;
        this.tipo_moneda = tipo_moneda;
        this.cantidad =cantidad;
        this.motivo = motivo;
        this.ingreso = ingreso;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getTipo_moneda() {
        return tipo_moneda;
    }

    public void setTipo_moneda(String tipo_moneda) {
        this.tipo_moneda = tipo_moneda;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getIngreso() {
        return ingreso;
    }

    public void setIngreso(String ingreso) {
        this.ingreso = ingreso;
    }
}
