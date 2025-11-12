package excepciones;

/**
 * Excepción lanzada cuando ocurre un error al guardar o cargar datos desde archivos.
 */
public class ErrorPersistenciaException extends Exception {
    private String archivo;
    
    public ErrorPersistenciaException(String mensaje, String archivo, Throwable causa) {
        super(mensaje, causa);
        this.archivo = archivo;
    }

}

