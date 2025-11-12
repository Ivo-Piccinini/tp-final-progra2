package entidades;

/**
 * Interfaz que define el contrato para entidades del sistema
 * que tienen un identificador único (ID).
 * 
 * @param <T> El tipo del identificador (generalmente Integer)
 */
public interface IEntidad<T> {
    
    /**
     * Obtiene el identificador único de la entidad
     * 
     * @return El ID de la entidad
     */
    T getId();
    
    /**
     * Obtiene el nombre de la entidad (para búsquedas y visualización)
     * 
     * @return El nombre de la entidad
     */
    String getNombre();
}

