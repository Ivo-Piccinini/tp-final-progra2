package inventario;

import productos.Producto;
import excepciones.StockInsuficienteException;
import excepciones.ProductoNoEncontradoException;
import utilidades.RepositorioGenerico;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Clase que gestiona el stock de productos en el inventario.
 * Permite agregar, remover, consultar y gestionar el inventario de productos.
 *
 */

/*
 *  * ELECCION DE COLECCIONES:
 *  *
 *  * - HashMap para inventario y productos: Usamos HashMap porque necesitamos buscar productos
 *  *   rápidamente por su ID sin tener que revisar todos los productos uno por uno.
 *  *
 *  * - ArrayList para listas de productos disponibles: Usamos ArrayList cuando necesitamos devolver
 *  *   una lista de productos que podemos recorrer en orden.
 * */
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
    /**
     * Agrega productos al stock
     * @param producto Producto a ingresar al stock
     * @param cantidad cantidad a ingresar al stock de dicho producto
     */
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

    /**
     * Eliminar productos del stock
     * @param productoId id del producto a eliminar del stock
     * @param cantidad cantidad a eliminar del stock de dicho producto
     * @throws ProductoNoEncontradoException si el producto que queremos eliminar no fue encontrado
     * @throws StockInsuficienteException si el stock del producto que queremos eliminar no es suficiente
     * @return true si el producto fue eliminado
     */
    public boolean eliminarProducto(int productoId, int cantidad) throws ProductoNoEncontradoException, StockInsuficienteException {
        if (cantidad < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa.");
        }

        // si en el inventario no existe el id, el producto no existe
        if (!inventario.containsKey(productoId)) {
            throw new ProductoNoEncontradoException("El producto con ID " + productoId + " no existe en el inventario.", productoId);
        }
        
        int cantidadActual = inventario.get(productoId);

        // si la cantidad actual es menor que la cantidad pasada por parametro, no hay stock del producto
        if (cantidadActual < cantidad) {
            throw new StockInsuficienteException(
                "Stock insuficiente para el producto ID " + productoId + ". Disponible: " + cantidadActual + ", Requerido: " + cantidad,
                cantidadActual,
                cantidad
            );
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

    /**
     * Retorna la cantidad disponible de un producto
     * @param productoId id del producto del cual queremos obtener la cantidad en stock
     * @return la cantidad disponible del producto
     */
    public int obtenerCantidad(int productoId) {
        return inventario.getOrDefault(productoId, 0);
    }

    /**
     * Indica si hay stock de un producto
     * @return Verdadero si hay stock del producto
     * @return Falso si no hay stock del producto
     * @param productoId id del producto a consultar
     * @param cantidad cantidad del producto a consultar
     */
    public boolean hayStock(int productoId, int cantidad) {
        return obtenerCantidad(productoId) >= cantidad;
    }

    
    /**
     * Busca un producto por nombre
     * Si hay múltiples productos con el mismo nombre, retorna el primero disponible
     * @param nombre Nombre del producto a buscar
     * @return el prodicto encontrado
     */
    public Producto buscarProductoPorNombre(String nombre) {
        List<Producto> disponibles = obtenerProductosDisponibles();
        return RepositorioGenerico.buscarPorNombre(disponibles, nombre, Producto::getNombre);
    }
    
    // ---------------------- METODOS DE CONSULTA ----------------------
    /**
     * Obtiene los productos disponibles (activos)
     * @return la lista de productos disponibles
     */
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

    /**
     * Obtiene la cantidad de productos total en stock
     * @return La cantidad de stock total
     */
    public int getStockTotal() {
        return stockTotal;
    }

    /**
     * Obtiene la cantidad de productos diferentes en stock (sin sumar la cantidad de cada uno)
     * @return La cantidad de productos
     */
    public int getCantidadProductos() {
        return productos.size();
    }


    /**
     * Obtiene la cantidad de productos diferentes disponibles en stock (sin sumar la cantidad de cada uno)
     * @return La cantidad de productos disponibles
     */
    public int getCantidadProductosDisponibles() {
        return obtenerProductosDisponibles().size();
    }

    /**
     * Obtiene el valor total del inventario y lo retorna
     * @return el valor total del inventario
     */
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

    /**
     * @return  la ultima actualización del inventario
     */
    public LocalDateTime getUltimaActualizacion() {
        return ultimaActualizacion;
    }
    
    // ---------------------- METODOS PARA PERSISTENCIA ----------------------
    
    /**
     * Obtiene el inventario interno
     * @return un map que contiene el inventario del comercio
     */
    public Map<Integer, Integer> getInventario() {
        return new HashMap<>(inventario);
    }
    
    /**
     * Obtiene los productos internos
     * @return un map que contiene los productos del comercio
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
