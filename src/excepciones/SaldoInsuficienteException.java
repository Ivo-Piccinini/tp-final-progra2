package excepciones;

/**
 * Excepción lanzada cuando el saldo del cliente es insuficiente para realizar una operación.
 */
public class SaldoInsuficienteException extends Exception {
    private double saldoDisponible;
    private double saldoRequerido;
    
    public SaldoInsuficienteException(String mensaje, double saldoDisponible, double saldoRequerido) {
        super(mensaje);
        this.saldoDisponible = saldoDisponible;
        this.saldoRequerido = saldoRequerido;
    }
}

