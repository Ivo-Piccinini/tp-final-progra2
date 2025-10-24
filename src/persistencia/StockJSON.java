package persistencia;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import inventario.Stock;
import productos.Producto;
import productos.CategoriaProducto;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Map;

public class StockJSON {
    private static final String ARCHIVO_STOCK = "data/stock.json";
    
    public StockJSON() {}

    /**
     * Guarda el stock en un archivo JSON
     */
    public void guardarStock(Stock stock, String nombreArchivo) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("fechaActualizacion", stock.getUltimaActualizacion().toString());
            jsonObject.put("stockTotal", stock.getStockTotal());
            jsonObject.put("cantidadProductos", stock.getCantidadProductos());
            jsonObject.put("valorTotalInventario", stock.getValorTotalInventario());
            
            // Serializar productos
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
            System.out.println("✅ Stock guardado exitosamente en: " + nombreArchivo);
            
        } catch (Exception e) {
            System.out.println("❌ Error al guardar stock: " + e.getMessage());
        }
    }

    /**
     * Carga el stock desde un archivo JSON
     */
    public Stock cargarStock(String nombreArchivo) {
        Stock stock = new Stock();
        
        try {
            // Verificar si el archivo existe y no está vacío
            java.io.File archivo = new java.io.File(nombreArchivo);
            if (!archivo.exists()) {
                System.out.println("📁 Archivo de stock no encontrado: " + nombreArchivo);
                System.out.println("📦 Creando stock vacío.");
                return stock;
            }
            
            if (archivo.length() == 0) {
                System.out.println("📁 Archivo de stock está vacío: " + nombreArchivo);
                System.out.println("📦 Creando stock vacío.");
                return stock;
            }
            
            FileReader fileReader = new FileReader(nombreArchivo);
            org.json.JSONTokener tokener = new org.json.JSONTokener(fileReader);
            JSONObject jsonObject = new JSONObject(tokener);
            
            JSONArray productosArray = jsonObject.getJSONArray("productos");
            
            for (int i = 0; i < productosArray.length(); i++) {
                JSONObject productoJson = productosArray.getJSONObject(i);
                Producto producto = deserializarProducto(productoJson);
                int cantidad = productoJson.getInt("cantidad");
                
                if (producto != null) {
                    stock.agregarProducto(producto, cantidad);
                    System.out.println("✅ Producto cargado: " + producto.getNombre() + " (ID: " + producto.getId() + ", Cantidad: " + cantidad + ")");
                } else {
                    System.out.println("⚠️ No se pudo cargar un producto del archivo");
                }
            }
            
            System.out.println("✅ Stock cargado exitosamente desde: " + nombreArchivo);
            System.out.println("📦 Productos cargados: " + stock.getCantidadProductos());
            System.out.println("📊 Stock total: " + stock.getStockTotal() + " unidades");
            
        } catch (java.io.FileNotFoundException e) {
            System.out.println("📁 Archivo de stock no encontrado: " + nombreArchivo);
            System.out.println("📦 Creando stock vacío.");
        } catch (org.json.JSONException e) {
            System.out.println("❌ Error de formato JSON en archivo: " + nombreArchivo);
            System.out.println("📁 El archivo puede estar corrupto. Creando stock vacío.");
            // Eliminar archivo corrupto
            try {
                new java.io.File(nombreArchivo).delete();
                System.out.println("🗑️ Archivo corrupto eliminado.");
            } catch (Exception deleteError) {
                System.out.println("⚠️ No se pudo eliminar el archivo corrupto.");
            }
        } catch (Exception e) {
            System.out.println("❌ Error al cargar stock: " + e.getMessage());
            System.out.println("📁 Creando stock vacío.");
        }
        
        return stock;
    }

    /**
     * Serializa un producto a JSONObject
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
     */
    private Producto deserializarProducto(JSONObject productoJson) {
        try {
            int id = productoJson.getInt("id");
            String nombre = productoJson.getString("nombre");
            String descripcion = productoJson.getString("descripcion");
            CategoriaProducto categoria = CategoriaProducto.valueOf(productoJson.getString("categoria"));
            double precio = productoJson.getDouble("precio");
            String marca = productoJson.getString("marca");
            String modelo = productoJson.getString("modelo");
            String especificaciones = productoJson.getString("especificaciones");
            boolean activo = productoJson.getBoolean("activo");
            
            Producto producto = new Producto(nombre, descripcion, categoria, precio, marca, modelo, especificaciones);
            producto.setId(id); // Restaurar el ID original
            producto.setActivo(activo);
            
            return producto;
            
        } catch (Exception e) {
            System.out.println("⚠️ Error al deserializar producto: " + e.getMessage());
            return null;
        }
    }

    /**
     * Verifica si existe el archivo de stock
     */
    public boolean existeArchivoStock() {
        return new java.io.File(ARCHIVO_STOCK).exists();
    }

    /**
     * Elimina el archivo de stock
     */
    public boolean eliminarArchivoStock() {
        try {
            java.io.File archivo = new java.io.File(ARCHIVO_STOCK);
            boolean eliminado = archivo.delete();
            if (eliminado) {
                System.out.println("🗑️ Archivo de stock eliminado.");
            }
            return eliminado;
        } catch (Exception e) {
            System.out.println("❌ Error al eliminar archivo: " + e.getMessage());
            return false;
        }
    }
}
