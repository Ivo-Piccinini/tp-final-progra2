package persistencia;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import org.json.JSONTokener;
import usuarios.Credenciales;
import usuarios.Rol;
import usuarios.SistemaAutenticacion;
import usuarios.Usuario;
import usuarios.clientes.Cliente;
import usuarios.vendedores.Vendedor;
import usuarios.clientes.MetodoPago;
import excepciones.ErrorPersistenciaException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ELECCION DE COLECCIONES:
 *
 * - ArrayList para listas de usuarios: Usamos ArrayList cuando necesitamos guardar y recorrer
 *   usuarios en orden.
 *
 * - HashMap para credenciales: Usamos HashMap porque necesitamos buscar credenciales rápidamente
 *   por el email del usuario sin tener que revisar todas las credenciales una por una.
 *
 * - ArrayList para historiales (compras y ventas): Usamos ArrayList para mantener listas de
 *   compras o ventas en orden cronológico.
 */
public class GestorUsuariosJSON {
    private static final String ARCHIVO_USUARIOS = "data/usuarios.json";
    
    public GestorUsuariosJSON() {}

    /**
     * Guarda todos los usuarios del sistema en un archivo JSON
     * @param sistemaAutenticacion instancia de "SistemaAutenticación" donde están los usuarios autenticados
     * @param nombreArchivo nombre del archivo donde se van a guardar los usuarios
     * @throws ErrorPersistenciaException si no se pueden guardar los usuarios en el archivo
     */
    public void guardarUsuarios(SistemaAutenticacion sistemaAutenticacion, String nombreArchivo) throws ErrorPersistenciaException {
        try {
            ArrayList<Usuario> usuarios = (ArrayList<Usuario>) sistemaAutenticacion.listarUsuarios();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("usuarios", serializarLista(usuarios, sistemaAutenticacion));
            jsonObject.put("fechaActualizacion", LocalDateTime.now().toString());
            jsonObject.put("totalUsuarios", usuarios.size());
            // Guardamos el contador actual para mantener la secuencia de IDs
            jsonObject.put("contadorUsuarios", Usuario.getContador());
            
            OperacionesLectoEscritura.grabar(nombreArchivo, jsonObject);
        } catch (IOException e) {
            throw new ErrorPersistenciaException("Error de E/S al guardar usuarios en el archivo: " + nombreArchivo + ". Detalle: " + e.getMessage(), nombreArchivo, e);
        } catch (JSONException e) {
            throw new ErrorPersistenciaException("Error al serializar usuarios a JSON. Detalle: " + e.getMessage(), nombreArchivo, e);
        } catch (Exception e) {
            throw new ErrorPersistenciaException("Error inesperado al guardar usuarios en el archivo: " + nombreArchivo + ". Detalle: " + e.getMessage(), nombreArchivo, e);
        }
    }

    /**
     * Carga todos los usuarios desde un archivo JSON
     * @param nombreArchivo nombre del archivo de donde se cargarán los usuarios
     * @return una lista con los usuarios que contiene el archivo
     */
    public ArrayList<Usuario> cargarUsuarios(String nombreArchivo) {
        ArrayList<Usuario> usuarios = new ArrayList<>();
        int maxId = 0;
        try {
            FileReader fileReader = new FileReader(nombreArchivo);
            JSONTokener tokener = new JSONTokener(fileReader);
            JSONObject jsonObject = new JSONObject(tokener);
            
            JSONArray usuariosArray = jsonObject.getJSONArray("usuarios");
            
            for (int i = 0; i < usuariosArray.length(); i++) {
                JSONObject usuarioJson = usuariosArray.getJSONObject(i);
                Usuario usuario = deserializarUsuario(usuarioJson);
                if (usuario != null) {
                    usuarios.add(usuario);
                    // Encontramos el ID máximo para actualizar el contador
                    if (usuario.getId() > maxId) {
                        maxId = usuario.getId();
                    }
                }
            }
            
            // Restauramos el contador: usar el valor guardado si existe, sino usar el máximo ID encontrado
            if (jsonObject.has("contadorUsuarios")) {
                int contadorGuardado = jsonObject.getInt("contadorUsuarios");
                Usuario.setContador(contadorGuardado);
            } else {
                // Si no hay contador guardado, usamos el máximo ID encontrado
                Usuario.setContador(maxId);
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error al cargar usuarios: " + e.getMessage());
        }
        
        return usuarios;
    }
    
    /**
     * Carga usuarios y credenciales desde un archivo JSON
     * @param nombreArchivo nombre del archivo del cual cargaremos los usuarios y credenciales al sistema
     * @return un map que contiene los usuarios y credenciales
     */
    public Map<String, Credenciales> cargarUsuariosConCredenciales(String nombreArchivo) {
        Map<String, Credenciales> credenciales = new HashMap<>();
        
        try {
            FileReader fileReader = new FileReader(nombreArchivo);
            JSONTokener tokener = new JSONTokener(fileReader);
            JSONObject jsonObject = new JSONObject(tokener);
            
            JSONArray usuariosArray = jsonObject.getJSONArray("usuarios");
            
            for (int i = 0; i < usuariosArray.length(); i++) {
                JSONObject usuarioJson = usuariosArray.getJSONObject(i);
                String email = usuarioJson.getString("email");
                String password = usuarioJson.optString("password", "temp123");
                
                Credenciales credencial = new Credenciales(email, password);
                credenciales.put(email, credencial);
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error al cargar credenciales: " + e.getMessage());
            System.out.println("📁 Continuando con credenciales vacías.");
        }
        
        return credenciales;
    }

    /**
     * Serializa una lista de usuarios a JSONArray
     * @param usuarios usuarios a serializar
     * @param sistemaAutenticacion credenciales de los usuarios a serializar
     * @return un JSONArray con los usuarios serializados
     */
    public JSONArray serializarLista(ArrayList<Usuario> usuarios, SistemaAutenticacion sistemaAutenticacion) {
        JSONArray jsonArray = new JSONArray();
        
        try {
            for (Usuario usuario : usuarios) {
                JSONObject usuarioJson = serializarUsuario(usuario, sistemaAutenticacion);
                jsonArray.put(usuarioJson);
            }
        } catch (JSONException e) {
            System.out.println("❌ Error al serializar usuarios: " + e.getMessage());
        }
        
        return jsonArray;
    }

    /**
     * Serializa un usuario individual a JSONObject
     * @param usuario usuario a serializar
     * @param sistemaAutenticacion credenciales del usuario a serializar
     * @throws JSONException si hay algun error relacionado al uso de JSON
     * @return un JSONObject con el usuario serializado
     */
    private JSONObject serializarUsuario(Usuario usuario, SistemaAutenticacion sistemaAutenticacion) throws JSONException {
        JSONObject usuarioJson = new JSONObject();
        usuarioJson.put("id", usuario.getId());
        usuarioJson.put("nombre", usuario.getNombre() != null ? usuario.getNombre() : ""); // Para cada campo comprobamos que el dato no sea null, en el caso de que lo sea guarda como String vacío
        usuarioJson.put("apellido", usuario.getApellido() != null ? usuario.getApellido() : "");
        usuarioJson.put("email", usuario.getEmail() != null ? usuario.getEmail() : "");
        usuarioJson.put("rol", usuario.getRol() != null ? usuario.getRol().toString() : "");
        usuarioJson.put("estado", usuario.getEstado());
        usuarioJson.put("dni", usuario.getDni() != null ? usuario.getDni() : "");
        usuarioJson.put("fechaRegistro", usuario.getFechaRegistro() != null ? usuario.getFechaRegistro().toString() : "");
        
        // Obtenemos y guardamos la contraseña
        try {
            // Accedemos a las credenciales del sistema de autenticación
            Field credencialesField = SistemaAutenticacion.class.getDeclaredField("credenciales");
            credencialesField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, Credenciales> credenciales = (Map<String, Credenciales>) credencialesField.get(sistemaAutenticacion);
            
            Credenciales credencial = credenciales.get(usuario.getEmail());
            if (credencial != null) {
                usuarioJson.put("password", credencial.getPassword());
            } else {
                usuarioJson.put("password", "temp123"); // Contraseña por defecto
            }
        } catch (Exception e) {
            System.out.println("⚠️ No se pudo obtener la contraseña para " + usuario.getEmail() + ": " + e.getMessage());
            usuarioJson.put("password", "temp123"); // Contraseña por defecto
        }
        
        // Datos específicos según el tipo de usuario
        if (usuario instanceof Cliente) {
            Cliente cliente = (Cliente) usuario;
            usuarioJson.put("tipoUsuario", "CLIENTE");
            usuarioJson.put("cantProductosComprados", cliente.getCantProductosComprados());
            usuarioJson.put("metodoPago", cliente.getMetodoPago().toString());
            usuarioJson.put("saldo", cliente.getSaldo());
            usuarioJson.put("direccion", cliente.getDireccion() != null ? cliente.getDireccion() : "");
            usuarioJson.put("telefono", cliente.getTelefono() != null ? cliente.getTelefono() : "");
            
            // Historial de compras
            JSONArray historialArray = new JSONArray();
            List<String> historialCompras = cliente.getHistorialCompras();
            if (historialCompras != null) {
                for (String compra : historialCompras) {
                    historialArray.put(compra);
                }
            }
            usuarioJson.put("historialCompras", historialArray);
            
        } else if (usuario instanceof Vendedor) {
            Vendedor vendedor = (Vendedor) usuario;
            usuarioJson.put("tipoUsuario", "VENDEDOR");
            usuarioJson.put("salario", vendedor.getSalario());
            usuarioJson.put("comision", vendedor.getComisionPorVenta());
            usuarioJson.put("totalComisiones", vendedor.getTotalComisiones());
            
            // Historial de ventas
            JSONArray ventasArray = new JSONArray();
            List<String> historialVentas = vendedor.getHistorialVentas();
            if (historialVentas != null) {
                for (String venta : historialVentas) {
                    ventasArray.put(venta);
                }
            }
            usuarioJson.put("ventasRealizadas", ventasArray);
            usuarioJson.put("totalVentas", vendedor.getCantVentas());
        }
        
        return usuarioJson;
    }

    /**
     * Deserializa un JSONObject a un Usuario
     * @param usuarioJson usuario serializado que vamos a deserializar
     * @return usuario ya deserializado
     */
    private Usuario deserializarUsuario(JSONObject usuarioJson) {
        try {
            int id = usuarioJson.getInt("id");
            String tipoUsuario = usuarioJson.getString("tipoUsuario");
            String nombre = usuarioJson.getString("nombre");
            String apellido = usuarioJson.getString("apellido");
            String email = usuarioJson.getString("email");
            Rol rol = Rol.valueOf(usuarioJson.getString("rol"));
            int estado = usuarioJson.getInt("estado");
            String dni = usuarioJson.getString("dni");
            
            if ("CLIENTE".equals(tipoUsuario)) {
                int cantProductosComprados = usuarioJson.getInt("cantProductosComprados");
                MetodoPago metodoPago = MetodoPago.valueOf(usuarioJson.getString("metodoPago"));
                double saldo = usuarioJson.getDouble("saldo");
                String direccion = usuarioJson.optString("direccion", "");
                String telefono = usuarioJson.optString("telefono", "");
                
                Cliente cliente = new Cliente(id, nombre, apellido, email, rol, estado, dni,cantProductosComprados, metodoPago, saldo, direccion, telefono);
                
                // Cargamos el historial de compras
                JSONArray historialArray = usuarioJson.optJSONArray("historialCompras");
                if (historialArray != null) {
                    for (int i = 0; i < historialArray.length(); i++) {
                        cliente.agregarCompra(historialArray.getString(i));
                    }
                }
                
                return cliente;
                
            } else if ("VENDEDOR".equals(tipoUsuario)) {
                double salario = usuarioJson.getDouble("salario");
                double comision = usuarioJson.getDouble("comision");
                int cantVentas = usuarioJson.optInt("totalVentas", 0);
                double totalComisiones = usuarioJson.optDouble("totalComisiones", 0.0);
                
                Vendedor vendedor = new Vendedor(id, nombre, apellido, email, rol, estado, dni, cantVentas, salario);
                vendedor.setComisionPorVenta(comision);
                vendedor.setTotalComisiones(totalComisiones);
                
                // Restauramos el historial de ventas
                if (usuarioJson.has("ventasRealizadas")) {
                    JSONArray ventasArray = usuarioJson.getJSONArray("ventasRealizadas");
                    List<String> historialVentas = new ArrayList<>();
                    for (int i = 0; i < ventasArray.length(); i++) {
                        historialVentas.add(ventasArray.getString(i));
                    }
                    vendedor.setHistorialVentas(historialVentas);
                }
                
                return vendedor;
            }
            
        } catch (Exception e) {
            System.out.println("⚠️ Error al deserializar usuario: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * Verifica si existe el archivo de usuarios
     * @return true si existe el archivo de usuarios, false si no
     */
    public boolean existeArchivoUsuarios() {
        return new File(ARCHIVO_USUARIOS).exists();
    }
}
