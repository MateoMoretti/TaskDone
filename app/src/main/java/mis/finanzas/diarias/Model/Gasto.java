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

    public void setNombre_moneda(String nombre_moneda) {
        this.nombre_moneda = nombre_moneda;
    }

    public Float getTotal_gasto() {
        return total_gasto;
    }

    public void setTotal_gasto(Float total_gasto) {
        this.total_gasto = total_gasto;
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
