package inventario;

import productos.Producto;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Clase que gestiona el stock de productos en el inventario.
 * Permite agregar, remover, consultar y gestionar el inventario de productos.
 */
public class Stock {
    private Map<Integer, Integer> inventario; // Producto ID -> Cantidad
    private Map<Integer, Producto> productos; // Producto ID -> Producto
    private int stockTotal;
    private LocalDateTime ultimaActualizacion;
    
    // ---------------------- CONSTRUCTORES ----------------------
    public Stock() {
        this.inventario = new HashMap<>();
        this.productos = new HashMap<>();
        this.stockTotal = 0;
        this.ultimaActualizacion = LocalDateTime.now();
    }
    
    // ---------------------- METODOS DE GESTION DE PRODUCTOS ----------------------
    public void agregarProducto(Producto producto, int cantidad) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser null.");
        }
        if (cantidad < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa.");
        }
        
        int productoId = producto.getId();
        
        if (productos.containsKey(productoId)) {
            // el producto ya existe, actualiza la cantidad
            int cantidadActual = inventario.getOrDefault(productoId, 0);
            inventario.put(productoId, cantidadActual + cantidad);
        } else {
            productos.put(productoId, producto);
            inventario.put(productoId, cantidad);
        }
        
        stockTotal += cantidad;
        ultimaActualizacion = LocalDateTime.now();
    }
    
    public boolean eliminarProducto(int productoId, int cantidad) {
        if (cantidad < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa.");
        }

        // si en el inventario no existe el id, el producto no existe
        if (!inventario.containsKey(productoId)) {
            return false;
        }
        
        int cantidadActual = inventario.get(productoId);

        // si la cantidad actual es menor que la cantidad pasada por parametro, no hay stock del producto
        if (cantidadActual < cantidad) {
            return false;
        }
        
        int nuevaCantidad = cantidadActual - cantidad;

        // si la cantidad es 0, se remueve el producto, sino, se agrega
        if (nuevaCantidad == 0) {
            inventario.remove(productoId);
        } else {
            inventario.put(productoId, nuevaCantidad);
        }
        
        stockTotal -= cantidad;
        ultimaActualizacion = LocalDateTime.now();
        return true;
    }

    public int obtenerCantidad(int productoId) {
        return inventario.getOrDefault(productoId, 0);
    }
    
    public boolean hayStock(int productoId, int cantidad) {
        return obtenerCantidad(productoId) >= cantidad;
    }
    
    public Producto obtenerProducto(int productoId) {
        return productos.get(productoId);
    }
    
    // ---------------------- METODOS DE CONSULTA ----------------------
    public List<Producto> obtenerProductosDisponibles() {
        List<Producto> disponibles = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : inventario.entrySet()) {
            if (entry.getValue() > 0) {
                Producto producto = productos.get(entry.getKey());
                if (producto != null && producto.isActivo()) {
                    disponibles.add(producto);
                }
            }
        }
        return disponibles;
    }
    
    // ---------------------- METODOS DE ESTADISTICAS ----------------------
    public int getStockTotal() {
        return stockTotal;
    }
    
    public int getCantidadProductos() {
        return productos.size();
    }
    
    public int getCantidadProductosDisponibles() {
        return obtenerProductosDisponibles().size();
    }
    
    public double getValorTotalInventario() {
        double valorTotal = 0.0;
        for (Map.Entry<Integer, Integer> entry : inventario.entrySet()) {
            Producto producto = productos.get(entry.getKey());
            if (producto != null) {
                valorTotal += producto.getPrecio() * entry.getValue();
            }
        }
        return valorTotal;
    }
    
    public LocalDateTime getUltimaActualizacion() {
        return ultimaActualizacion;
    }
    
    // ---------------------- METODOS PARA PERSISTENCIA ----------------------
    
    /**
     * Obtiene el inventario interno
     */
    public Map<Integer, Integer> getInventario() {
        return new HashMap<>(inventario);
    }
    
    /**
     * Obtiene los productos internos
     */
    public Map<Integer, Producto> getProductos() {
        return new HashMap<>(productos);
    }
    
    /**
     * Actualiza la fecha de última actualización
     */
    public void actualizarFecha() {
        this.ultimaActualizacion = LocalDateTime.now();
    }
    
    // ---------------------- METODOS DE REPORTE ----------------------
    public void mostrarInventario() {
        System.out.println("📦 INVENTARIO DE PRODUCTOS");
        System.out.println("═══════════════════════════════════");
        System.out.println("📊 Total de productos: " + getCantidadProductos());
        System.out.println("📦 Stock total: " + getStockTotal() + " unidades");
        System.out.println("💰 Valor total: $" + String.format("%.2f", getValorTotalInventario()));
        System.out.println("🕒 Última actualización: " + ultimaActualizacion);
        System.out.println("═══════════════════════════════════");
        
        if (inventario.isEmpty()) {
            System.out.println("📭 No hay productos en el inventario.");
        } else {
            for (Map.Entry<Integer, Integer> entry : inventario.entrySet()) {
                Producto producto = productos.get(entry.getKey());
                if (producto != null) {
                    System.out.println(String.format("📱 %s | Stock: %d | Precio: $%.2f",
                        producto.getNombre(), entry.getValue(), producto.getPrecio()));
                }
            }
        }
    }
    
    public void mostrarProductosDisponibles() {
        List<Producto> disponibles = obtenerProductosDisponibles();
        System.out.println("🛍️ PRODUCTOS DISPONIBLES");
        System.out.println("═══════════════════════════════════");
        
        if (disponibles.isEmpty()) {
            System.out.println("📭 No hay productos disponibles.");
        } else {
            for (Producto producto : disponibles) {
                int cantidad = obtenerCantidad(producto.getId());
                System.out.println(String.format("📱 %s | Stock: %d | Precio: $%.2f",
                    producto.getNombre(), cantidad, producto.getPrecio()));
            }
        }
    }
}
