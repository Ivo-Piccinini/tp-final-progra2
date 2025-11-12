package excepciones;

/**
 * Excepción lanzada cuando un producto no se encuentra en el sistema.
 */
public class ProductoNoEncontradoException extends Exception {
    private int productoId;
    
    public ProductoNoEncontradoException(String mensaje, int productoId) {
        super(mensaje);
        this.productoId = productoId;
    }
}

