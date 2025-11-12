package productos;

import org.json.JSONObject;

import java.time.LocalDateTime;

/**
 * Clase que representa un producto de tecnología en el sistema.
 * Incluye información básica, especificaciones técnicas y estado del producto.
 */
public class Producto {
    private int id;
    private static int contador = 0;
    private String nombre;
    private String descripcion;
    private CategoriaProducto categoria;
    private double precio;
    private String marca;
    private String modelo;
    private String especificaciones;
    private boolean activo;
    private LocalDateTime fechaCreacion;
    
    // ---------------------- CONSTRUCTORES ----------------------
    public Producto(String nombre, String  descripcion, CategoriaProducto categoria,  double precio, String marca, String modelo, String especificaciones) {
        this.id = contador;
        contador++;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.precio = precio;
        this.marca = marca;
        this.modelo = modelo;
        this.especificaciones = especificaciones;
        this.activo = true;
        this.fechaCreacion = LocalDateTime.now();
    }
    
    public Producto() {
        this.id = contador;
        contador++;
        this.activo = true;
        this.fechaCreacion = LocalDateTime.now();
    }
    
    // ---------------------- GETTERS Y SETTERS ----------------------
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
        // Actualizar el contador si el ID es mayor o igual al contador actual
        // Esto asegura que el contador siempre esté por encima del ID más alto
        if (id >= contador) {
            contador = id + 1;
        }
    }
    
    public static void setContador(int nuevoContador) {
        contador = nuevoContador;
    }
    public static int getContador() {
        return contador;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto no puede estar vacío.");
        }
        this.nombre = nombre;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public CategoriaProducto getCategoria() {
        return categoria;
    }
    public void setCategoria(CategoriaProducto categoria) {
        this.categoria = categoria;
    }
    public double getPrecio() {
        return precio;
    }
    public void setPrecio(double precio) {
        if (precio < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo.");
        }
        this.precio = precio;
    }
    public String getMarca() {
        return marca;
    }
    public void setMarca(String marca) {
        this.marca = marca;
    }
    public String getModelo() {
        return modelo;
    }
    public void setModelo(String modelo) {
        this.modelo = modelo;
    }
    public String getEspecificaciones() {
        return especificaciones;
    }
    public void setEspecificaciones(String especificaciones) {
        this.especificaciones = especificaciones;
    }
    public boolean isActivo() {
        return activo;
    }
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    
    // ---------------------- METODOS PROPIOS ----------------------
    public void desactivar() {
        this.activo = false;
    }
    
    public void activar() {
        this.activo = true;
    }
    
    public double calcularPrecioConDescuento(double porcentajeDescuento) {
        if (porcentajeDescuento < 0 || porcentajeDescuento > 100) {
            throw new IllegalArgumentException("El porcentaje de descuento debe estar entre 0 y 100.");
        }
        return precio * (1 - porcentajeDescuento / 100.0);
    }
    
    // ---------------------- METODOS SOBREESCRITOS ----------------------
    @Override
    public String toString() {
        return String.format("📱 %s - %s %s | 💰 $%.2f | 📂 %s | %s", nombre, marca, modelo, precio, categoria, activo ? "🟢 Activo" : "🔴 Inactivo");
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Producto producto = (Producto) obj;
        return id == producto.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
