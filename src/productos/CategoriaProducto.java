package productos;

/**
 * Enum que define las categorías de productos de tecnología disponibles en el sistema.
 */
public enum CategoriaProducto {
    LAPTOP("Laptop", "Computadoras portátiles"),
    DESKTOP("Desktop", "Computadoras de escritorio"),
    SMARTPHONE("Smartphone", "Teléfonos inteligentes"),
    TABLET("Tablet", "Tabletas"),
    MONITOR("Monitor", "Monitores y pantallas"),
    TECLADO("Teclado", "Teclados y periféricos de entrada"),
    MOUSE("Mouse", "Ratones y dispositivos señaladores"),
    AUDIFONOS("Audífonos", "Auriculares y audífonos"),
    IMPRESORA("Impresora", "Impresoras y escáneres"),
    ACCESORIO("Accesorio", "Accesorios y componentes"),
    SOFTWARE("Software", "Software y licencias"),
    SERVICIO("Servicio", "Servicios técnicos");
    
    private final String nombre;
    private final String descripcion;
    
    CategoriaProducto(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    @Override
    public String toString() {
        return nombre;
    }
}
