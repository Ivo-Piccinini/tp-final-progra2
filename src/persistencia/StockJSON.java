package persistencia;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import inventario.Stock;
import org.json.JSONTokener;
import productos.Producto;
import productos.CategoriaProducto;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * ELECCION DE COLECCIONES:
 *
 * - Map para inventario y productos: Usamos Map porque necesitamos buscar productos
 *   rápidamente por su ID sin tener que revisar todos los productos uno por uno.
 */
public class StockJSON {
    
    public StockJSON() {}

    /**
     * Guarda el stock en un archivo
     * @param stock stock a guardar en el archivo
     * @param nombreArchivo nombre del archivo donde guardaremos el stock
     */
    public void guardarStock(Stock stock, String nombreArchivo) {
            JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("fechaActualizacion", stock.getUltimaActualizacion().toString());
            jsonObject.put("stockTotal", stock.getStockTotal());
            jsonObject.put("cantidadProductos", stock.getCantidadProductos());
            jsonObject.put("valorTotalInventario", stock.getValorTotalInventario());
            // Guardamos el contador actual para mantener la secuencia de IDs
            jsonObject.put("contadorProductos", Producto.getContador());
            
            // Serializamos los productos
            JSONArray productosArray = new JSONArray();
            Map<Integer, Integer> inventario = stock.getInventario();
            Map<Integer, Producto> productos = stock.getProductos();
            
            for (Map.Entry<Integer, Integer> entry : inventario.entrySet()) {
                int productoId = entry.getKey();
                int cantidad = entry.getValue();
                Producto producto = productos.get(productoId);
                
                if (producto != null) {
                    JSONObject productoJson = serializarProducto(producto, cantidad);
                    productosArray.put(productoJson);
                }
            }
            
            jsonObject.put("productos", productosArray);
            
            OperacionesLectoEscritura.grabar(nombreArchivo, jsonObject);
            
        } catch (Exception e) {
            System.out.println("❌ Error al guardar stock: " + e.getMessage());
        }
    }


    public Stock cargarStock(String nombreArchivo) throws IOException {
        Stock stock = new Stock();
        FileReader fileReader = null;
        
        try {
            // Creamos directorio si no existe
            File directorio = new File("data");
            if (!directorio.exists()) {
                directorio.mkdirs();
            }
            
            // Verificamos si el archivo existe y no está vacío
            File archivo = new File(nombreArchivo);
            if (!archivo.exists() || archivo.length() == 0) {
                return stock;
            }
            
            fileReader = new FileReader(nombreArchivo);
            JSONTokener tokener = new JSONTokener(fileReader);
            JSONObject jsonObject = new JSONObject(tokener);
            
            if (!jsonObject.has("productos")) {
                return stock;
            }
            
            // Primero encontramos el ID máximo para restaurar el contador correctamente
            JSONArray productosArray = jsonObject.getJSONArray("productos");
            int maxId = -1;
            
            // busccamos el ID máximo en el JSON antes de crear productos
            for (int i = 0; i < productosArray.length(); i++) {
                JSONObject productoJson = productosArray.getJSONObject(i);
                int idProducto = productoJson.getInt("id");
                if (idProducto > maxId) {
                    maxId = idProducto;
                }
            }
            
            // Restauramos el contador antes de cargar productos
            if (jsonObject.has("contadorProductos")) {
                int contadorGuardado = jsonObject.getInt("contadorProductos");
                Producto.setContador(contadorGuardado);
            } else if (maxId >= 0) {
                // Si no hay contador guardado, usar el máximo ID + 1
                Producto.setContador(maxId + 1);
            }
            
            // cargamos los productos
            for (int i = 0; i < productosArray.length(); i++) {
                try {
                    JSONObject productoJson = productosArray.getJSONObject(i);
                    int idProducto = productoJson.getInt("id");
                    Producto producto = deserializarProducto(productoJson);
                    int cantidad = productoJson.getInt("cantidad");
                    
                    if (producto != null) {
                        // Verificamos que el ID se haya establecido correctamente
                        if (producto.getId() != idProducto) {
                            producto.setId(idProducto); // Forzar el ID correcto
                        }
                        
                        // Agregamos producto al stock
                        stock.agregarProducto(producto, cantidad);
                    }
                } catch (Exception e) {
                    System.out.println("⚠️ Error al cargar producto en índice " + i + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
        }
         catch (JSONException e) {
            e.printStackTrace();
        } finally {
            // Cerrar el FileReader si está abierto
            if (fileReader != null) {
                fileReader.close();
            }
        }
        
        return stock;
    }

    /**
     * Serializa un producto a JSONObject
     * @param producto producto a serializar
     * @param cantidad cantidad del producto a serializar
     * @throws JSONException si hay un error relacionado con el uso de funciones JSON
     * @return producto ya serializado
     */
    private JSONObject serializarProducto(Producto producto, int cantidad) throws JSONException {
        JSONObject productoJson = new JSONObject();
        
        productoJson.put("id", producto.getId());
        productoJson.put("nombre", producto.getNombre());
        productoJson.put("descripcion", producto.getDescripcion());
        productoJson.put("categoria", producto.getCategoria().toString());
        productoJson.put("precio", producto.getPrecio());
        productoJson.put("marca", producto.getMarca());
        productoJson.put("modelo", producto.getModelo());
        productoJson.put("especificaciones", producto.getEspecificaciones());
        productoJson.put("activo", producto.isActivo());
        productoJson.put("fechaCreacion", producto.getFechaCreacion().toString());
        productoJson.put("cantidad", cantidad);
        
        return productoJson;
    }

    /**
     * Deserializa un JSONObject a un Producto
     * @param productoJson producto serializado
     * @return producto deserializado
     */
    private Producto deserializarProducto(JSONObject productoJson) {
        try {
            int id = productoJson.getInt("id");
            String nombre = productoJson.getString("nombre");
            String descripcion = productoJson.getString("descripcion");
            CategoriaProducto categoria = parsearCategoria(productoJson.getString("categoria"));
            double precio = productoJson.getDouble("precio");
            String marca = productoJson.getString("marca");
            String modelo = productoJson.getString("modelo");
            String especificaciones = productoJson.getString("especificaciones");
            boolean activo = productoJson.getBoolean("activo");
            
            // Creamos un producto usando el constructor vacío para evitar que se incremente el contador
            Producto producto = new Producto();
            producto.setId(id); // Establecemos el ID primero (esto actualiza el contador si es necesario)
            producto.setNombre(nombre);
            producto.setDescripcion(descripcion);
            producto.setCategoria(categoria);
            producto.setPrecio(precio);
            producto.setMarca(marca);
            producto.setModelo(modelo);
            producto.setEspecificaciones(especificaciones);
            producto.setActivo(activo);
            
            return producto;
            
        } catch (Exception e) {
            System.out.println("⚠️ Error al deserializar producto: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Parsea una categoría desde un string, buscando por nombre del enum o por el campo nombre
     * @param categoriaStr categoria a parsear
     * @return categoría parseada
     */
    private CategoriaProducto parsearCategoria(String categoriaStr) {
        if (categoriaStr == null || categoriaStr.isEmpty()) {
            return CategoriaProducto.ACCESORIO; // Valor por defecto
        }
        
        // Primero intentamos con valueOf (nombre del enum en mayúsculas)
        try {
            return CategoriaProducto.valueOf(categoriaStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Si falla, buscamos por el campo nombre
            for (CategoriaProducto categoria : CategoriaProducto.values()) {
                if (categoria.getNombre().equalsIgnoreCase(categoriaStr)) {
                    return categoria;
                }
            }
            // Si no se encuentra, devolvemos ACCESORIO por defecto
            return CategoriaProducto.ACCESORIO;
        }
    }
}
