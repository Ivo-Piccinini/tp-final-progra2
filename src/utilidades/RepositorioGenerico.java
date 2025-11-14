package utilidades;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Clase utilitaria con métodos genéricos para operaciones comunes
 * de repositorio (búsqueda, filtrado) sobre colecciones.
 */
public class RepositorioGenerico {

    
    /**
     * Busca una entidad en una lista por su nombre (búsqueda case-insensitive).
     * 
     * @param <T> El tipo de entidad
     * @param lista La lista donde buscar
     * @param nombre El nombre a buscar
     * @param obtenerNombre Función que extrae el nombre de la entidad
     * @return La primera entidad encontrada con ese nombre, o null si no existe
     */
    public static <T> T buscarPorNombre(List<T> lista, String nombre, Function<T, String> obtenerNombre) {
        if (lista == null || nombre == null || nombre.trim().isEmpty() || obtenerNombre == null) {
            return null;
        }
        
        String nombreBusqueda = nombre.trim().toLowerCase();
        for (T entidad : lista) {
            if (entidad != null) {
                String nombreEntidad = obtenerNombre.apply(entidad);
                if (nombreEntidad != null && nombreEntidad.toLowerCase().equals(nombreBusqueda)) {
                    return entidad;
                }
            }
        }
        return null;
    }
}

