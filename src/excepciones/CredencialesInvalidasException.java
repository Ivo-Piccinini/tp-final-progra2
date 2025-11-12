package excepciones;

/**
 * Excepción lanzada cuando las credenciales de autenticación son inválidas.
 */
public class CredencialesInvalidasException extends Exception {
    public CredencialesInvalidasException(String mensaje) {
        super(mensaje);
    }
}

