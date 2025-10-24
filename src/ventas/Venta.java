package ventas;

import inventario.Stock;
import productos.Producto;
import usuarios.clientes.Cliente;
import usuarios.vendedores.Vendedor;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Clase que representa una venta en el sistema.
 * Incluye información del cliente, vendedor, productos y totales.
 */
public class Venta {
    private int id;
    private static int contador = 0;
    private Cliente cliente;
    private Vendedor vendedor;
    private List<DetalleVenta> detalles;
    private double subtotal;
    private double descuento;
    private double total;
    private LocalDateTime fechaVenta;
    private String estado; // PENDIENTE, COMPLETADA, CANCELADA
    
    // ---------------------- CONSTRUCTOR ----------------------
    public Venta(Cliente cliente, Vendedor vendedor) {
        this.id = contador;
        contador++;
        this.cliente = cliente;
        this.vendedor = vendedor;
        this.detalles = new ArrayList<>();
        this.subtotal = 0.0;
        this.descuento = 0.0;
        this.total = 0.0;
        this.fechaVenta = LocalDateTime.now();
        this.estado = "PENDIENTE";
    }
    
    // ---------------------- MÉTODOS DE GESTIÓN ----------------------
    public boolean agregarProducto(Producto producto, int cantidad, Stock stock) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser null.");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0.");
        }
        if (!producto.isActivo()) {
            throw new IllegalArgumentException("El producto no está activo.");
        }
        if (!stock.hayStock(producto.getId(), cantidad)) {
            return false; // No hay suficiente stock
        }
        
        // Verificar si el producto ya está en la venta
        for (DetalleVenta detalle : detalles) {
            if (detalle.getProducto().getId() == producto.getId()) {
                // Producto ya existe, actualizar cantidad
                detalle.setCantidad(detalle.getCantidad() + cantidad);
                actualizarTotales();
                return true;
            }
        }
        
        // Producto nuevo en la venta
        DetalleVenta nuevoDetalle = new DetalleVenta(producto, cantidad);
        detalles.add(nuevoDetalle);
        actualizarTotales();
        return true;
    }
    
    public boolean removerProducto(int productoId) {
        for (Iterator<DetalleVenta> iterator = detalles.iterator(); iterator.hasNext();) {
            DetalleVenta detalle = iterator.next();
            if (detalle.getProducto().getId() == productoId) {
                iterator.remove();
                actualizarTotales();
                return true;
            }
        }
        return false;
    }
    
    public boolean actualizarCantidad(int productoId, int nuevaCantidad) {
        if (nuevaCantidad <= 0) {
            return removerProducto(productoId);
        }
        
        for (DetalleVenta detalle : detalles) {
            if (detalle.getProducto().getId() == productoId) {
                detalle.setCantidad(nuevaCantidad);
                actualizarTotales();
                return true;
            }
        }
        return false;
    }
    
    public boolean procesarVenta(Stock stock) {
        if (detalles.isEmpty()) {
            return false; // No hay productos en la venta
        }
        
        // Verificar stock de todos los productos
        for (DetalleVenta detalle : detalles) {
            if (!stock.hayStock(detalle.getProducto().getId(), detalle.getCantidad())) {
                return false; // No hay suficiente stock
            }
        }
        
        // Remover productos del stock
        for (DetalleVenta detalle : detalles) {
            stock.eliminarProducto(detalle.getProducto().getId(), detalle.getCantidad());
        }
        
        // Actualizar estadísticas del cliente y vendedor
        cliente.agregarCompra("Venta #" + id + " - " + detalles.size() + " productos");
        vendedor.realizarVenta("Venta #" + id, total);
        
        this.estado = "COMPLETADA";
        return true;
    }
    
    public void cancelarVenta() {
        this.estado = "CANCELADA";
    }
    
    public void aplicarDescuento(double porcentajeDescuento) {
        if (porcentajeDescuento < 0 || porcentajeDescuento > 100) {
            throw new IllegalArgumentException("El porcentaje de descuento debe estar entre 0 y 100.");
        }
        this.descuento = subtotal * (porcentajeDescuento / 100.0);
        this.total = subtotal - descuento;
    }
    
    private void actualizarTotales() {
        subtotal = 0.0;
        for (DetalleVenta detalle : detalles) {
            subtotal += detalle.getSubtotal();
        }
        total = subtotal - descuento;
    }
    
    // ---------------------- GETTERS Y SETTERS ----------------------
    public int getId() {
        return id;
    }
    
    public static int getContador() {
        return contador;
    }
    
    public Cliente getCliente() {
        return cliente;
    }
    
    public Vendedor getVendedor() {
        return vendedor;
    }
    
    public List<DetalleVenta> getDetalles() {
        return new ArrayList<>(detalles);
    }
    
    public double getSubtotal() {
        return subtotal;
    }
    
    public double getDescuento() {
        return descuento;
    }
    
    public double getTotal() {
        return total;
    }
    
    public LocalDateTime getFechaVenta() {
        return fechaVenta;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public int getCantidadProductos() {
        return detalles.size();
    }
    
    public int getCantidadTotalItems() {
        int total = 0;
        for (DetalleVenta detalle : detalles) {
            total += detalle.getCantidad();
        }
        return total;
    }
    
    // ---------------------- MÉTODOS DE CONSULTA ----------------------
    public void mostrarDetallesVenta() {
        System.out.println("🧾 DETALLE DE VENTA #" + id);
        System.out.println("═══════════════════════════════════");
        System.out.println("👤 Cliente: " + cliente.getNombre() + " " + cliente.getApellido());
        System.out.println("💼 Vendedor: " + vendedor.getNombre() + " " + vendedor.getApellido());
        System.out.println("📅 Fecha: " + fechaVenta);
        System.out.println("📊 Estado: " + estado);
        System.out.println("═══════════════════════════════════");
        
        if (detalles.isEmpty()) {
            System.out.println("📭 No hay productos en la venta.");
        } else {
            System.out.println("🛍️ PRODUCTOS:");
            for (DetalleVenta detalle : detalles) {
                System.out.println(String.format("  📱 %s x%d = $%.2f",
                    detalle.getProducto().getNombre(), 
                    detalle.getCantidad(), 
                    detalle.getSubtotal()));
            }
            
            System.out.println("═══════════════════════════════════");
            System.out.println(String.format("💰 Subtotal: $%.2f", subtotal));
            if (descuento > 0) {
                System.out.println(String.format("🎯 Descuento: -$%.2f", descuento));
            }
            System.out.println(String.format("💵 Total: $%.2f", total));
            System.out.println("═══════════════════════════════════");
        }
    }
    
    // ---------------------- MÉTODOS SOBREESCRITOS ----------------------
    @Override
    public String toString() {
        return String.format("🧾 Venta #%d | Cliente: %s | Total: $%.2f | Estado: %s",
                           id, cliente.getNombre(), total, estado);
    }
}
