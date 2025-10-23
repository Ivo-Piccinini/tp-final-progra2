package usuarios.clientes;

import usuarios.Rol;
import usuarios.Usuario;

public class Cliente extends Usuario {
    private int cantProductosComprados = 0;
    private MetodoPago metodoPago = MetodoPago.EFECTIVO;
    private double saldo = 0;

    // ---------------------- CONSTRUCTORES ----------------------
    public Cliente(String nombre, String apellido, String email, Rol rol, int estado, String dni, int cantProductosComprados, MetodoPago metodoPago, double saldo) {
        super(nombre, apellido, email, rol, estado, dni);
        if(cantProductosComprados < 0) {
            throw new IllegalArgumentException("La cantidad de productos comprados no puede ser negativa.");
        }
        if(saldo < 0) {
            throw new IllegalArgumentException("El saldo no puede ser negativo.");
        }
        this.cantProductosComprados = cantProductosComprados;
        this.metodoPago = metodoPago;
        this.saldo = saldo;
    }
    public Cliente(String nombre, String apellido, String email, Rol rol, int estado, String dni) {
        super(nombre, apellido, email, rol, estado, dni);
    }

    // ---------------------- GETTERS Y SETTERS ----------------------
    public int getCantProductosComprados() {
        return cantProductosComprados;
    }
    public void setCantProductosComprados(int cantProductosComprados) {
        if(cantProductosComprados < 0) {
            throw new IllegalArgumentException("La cantidad de productos comprados no puede ser negativa.");
        }
        this.cantProductosComprados = cantProductosComprados;
    }
    public MetodoPago getMetodoPago() {
        return metodoPago;
    }
    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }
    public double getSaldo() {
        return saldo;
    }
    public void setSaldo(double saldo) {
        if(saldo < 0) {
            throw new IllegalArgumentException("El saldo no puede ser negativo.");
        }
        this.saldo = saldo;
    }

    // ---------------------- MÉTODOS SOBREESCRITOS ----------------------
    @Override
    public String toString() {
        return super.toString() + "\n" +
                "  💰 Saldo: $" + String.format("%.2f", saldo) + "\n" +
                "  🛍️ Cant. Productos Comprados: " + cantProductosComprados + "\n" +
                "  💳 Método de Pago: " + metodoPago + "\n" +
                "══════════════════════════════════";
    }
}
