package ventas;

import productos.Producto;

import java.util.Objects;

/**
 * Clase que representa un detalle individual de una venta.
 * Contiene información sobre un producto específico y su cantidad.
 */
public class DetalleVenta {
    private Producto producto;
    private int cantidad;
    private double precioUnitario;
    private double subtotal;
    
    // ---------------------- CONSTRUCTOR ----------------------
    public DetalleVenta(Producto producto, int cantidad) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser null.");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0.");
        }
        
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = producto.getPrecio();
        this.subtotal = precioUnitario * cantidad;
    }
    
    // ---------------------- GETTERS Y SETTERS ----------------------
    public Producto getProducto() {
        return producto;
    }
    public int getCantidad() {
        return cantidad;
    }
    public void setCantidad(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0.");
        }
        this.cantidad = cantidad;
        this.subtotal = precioUnitario * cantidad;
    }
    public double getPrecioUnitario() {
        return precioUnitario;
    }
    public double getSubtotal() {
        return subtotal;
    }

    
    // ---------------------- MÉTODOS SOBREESCRITOS ----------------------
    @Override
    public String toString() {
        return String.format("📱 %s x%d = $%.2f",producto.getNombre(), cantidad, subtotal);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        DetalleVenta that = (DetalleVenta) obj;
        return Objects.equals(producto.getId(), that.producto.getId());
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(producto.getId());
    }
}
