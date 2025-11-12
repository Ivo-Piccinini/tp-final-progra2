package excepciones;

/**
 * Excepción lanzada cuando la contraseña no cumple con los requisitos de validación.
 */
public class PasswordInvalidaException extends Exception {
    public PasswordInvalidaException(String mensaje) {
        super(mensaje);
    }
    
    public PasswordInvalidaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}

