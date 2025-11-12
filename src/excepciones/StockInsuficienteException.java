package excepciones;

/**
 * Excepción lanzada cuando no hay suficiente stock disponible para realizar una operación.
 */
public class StockInsuficienteException extends Exception {
    private int stockDisponible;
    private int stockRequerido;
    
    public StockInsuficienteException(String mensaje, int stockDisponible, int stockRequerido) {
        super(mensaje);
        this.stockDisponible = stockDisponible;
        this.stockRequerido = stockRequerido;
    }
}

