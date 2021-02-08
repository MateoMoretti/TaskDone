package mis.finanzas.diarias.Model;

public class Gasto {

    String fecha;
    String nombre_moneda;
    Float total_gasto;
    String motivo;
    String ingreso;

    public Gasto( String fecha, String nombre_moneda, Float total_gasto, String motivo, String ingreso){
        this.fecha = fecha;
        this.nombre_moneda = nombre_moneda;
        this.total_gasto =total_gasto;
        this.motivo = motivo;
        this.ingreso = ingreso;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getNombre_moneda() {
        return nombre_moneda;
    }
}
