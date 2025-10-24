package usuarios.clientes;

import usuarios.Rol;
import usuarios.Usuario;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Cliente extends Usuario {
    private int cantProductosComprados = 0;
    private MetodoPago metodoPago = MetodoPago.EFECTIVO;
    private double saldo = 0;
    private String direccion;
    private String telefono;
    private List<String> historialCompras;
    private List<String> preferencias;

    // ---------------------- CONSTRUCTORES ----------------------
    public Cliente(String nombre, String apellido, String email, Rol rol, int estado, String dni, int cantProductosComprados, MetodoPago metodoPago, double saldo, String direccion, String telefono) {
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
        this.direccion = direccion;
        this.telefono = telefono;
        this.historialCompras = new ArrayList<>();
        this.preferencias = new ArrayList<>();
    }
    public Cliente(String nombre, String apellido, String email, Rol rol, int estado, String dni) {
        super(nombre, apellido, email, rol, estado, dni);
        this.historialCompras = new ArrayList<>();
        this.preferencias = new ArrayList<>();
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
    public String getDireccion() {
        return direccion;
    }
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
    public String getTelefono() {
        return telefono;
    }
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    public List<String> getHistorialCompras() {
        return new ArrayList<>(historialCompras);
    }
    public List<String> getPreferencias() {
        return new ArrayList<>(preferencias);
    }
    
    // ---------------------- METODOS  ----------------------
    public void agregarCompra(String descripcionCompra) {
        String compra = LocalDateTime.now().toString() + " - " + descripcionCompra;
        historialCompras.add(compra);
        cantProductosComprados++;
    }
    
    public void agregarPreferencia(String preferencia) {
        if (!preferencias.contains(preferencia)) {
            preferencias.add(preferencia);
        }
    }
    
    public void removerPreferencia(String preferencia) {
        preferencias.remove(preferencia);
    }
    
    /**
     * Cambia el método de pago por defecto del cliente
     */
    public void cambiarMetodoPagoPorDefecto(MetodoPago nuevoMetodo) {
        if (nuevoMetodo == null) {
            throw new IllegalArgumentException("El método de pago no puede ser null.");
        }
        this.metodoPago = nuevoMetodo;
        System.out.println("✅ Método de pago por defecto actualizado a: " + nuevoMetodo);
    }
    
    public void mostrarHistorialCompras() {
        System.out.println("🛍️ HISTORIAL DE COMPRAS:");
        if (historialCompras.isEmpty()) {
            System.out.println("  No hay compras registradas.");
        } else {
            for (String compra : historialCompras) {
                System.out.println("  • " + compra);
            }
        }
    }
    
    public void mostrarPreferencias() {
        System.out.println("⭐ PREFERENCIAS DEL CLIENTE:");
        if (preferencias.isEmpty()) {
            System.out.println("  No hay preferencias registradas.");
        } else {
            for (String preferencia : preferencias) {
                System.out.println("  • " + preferencia);
            }
        }
    }

    // ---------------------- MÉTODOS SOBREESCRITOS ----------------------
    @Override
    public String toString() {
        return super.toString() + "\n" +
                "  💰 Saldo: $" + String.format("%.2f", saldo) + "\n" +
                "  🛍️ Cant. Productos Comprados: " + cantProductosComprados + "\n" +
                "  💳 Método de Pago: " + metodoPago + "\n" +
                "  📍 Dirección: " + (direccion != null ? direccion : "No registrada") + "\n" +
                "  📞 Teléfono: " + (telefono != null ? telefono : "No registrado") + "\n" +
                "══════════════════════════════════";
    }
}
