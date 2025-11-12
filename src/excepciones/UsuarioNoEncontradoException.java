package excepciones;

/**
 * Excepción lanzada cuando un usuario no se encuentra en el sistema.
 */
public class UsuarioNoEncontradoException extends Exception {
    private String email;
    private int id;
    
    public UsuarioNoEncontradoException(String mensaje) {
        super(mensaje);
    }
    
    public UsuarioNoEncontradoException(String mensaje, String email) {
        super(mensaje);
        this.email = email;
    }
    
    public UsuarioNoEncontradoException(String mensaje, int id) {
        super(mensaje);
        this.id = id;
    }
    
    public int getId() {
        return id;
    }
}

