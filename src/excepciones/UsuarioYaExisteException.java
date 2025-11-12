package excepciones;

/**
 * Excepción lanzada cuando se intenta registrar un usuario que ya existe en el sistema.
 */
public class UsuarioYaExisteException extends Exception {
    public UsuarioYaExisteException(String mensaje) {
        super(mensaje);
    }
}

