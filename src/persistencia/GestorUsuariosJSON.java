package persistencia;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import usuarios.Credenciales;
import usuarios.Rol;
import usuarios.SistemaAutenticacion;
import usuarios.Usuario;
import usuarios.clientes.Cliente;
import usuarios.vendedores.Vendedor;
import usuarios.clientes.MetodoPago;
import excepciones.ErrorPersistenciaException;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestorUsuariosJSON {
    private static final String ARCHIVO_USUARIOS = "data/usuarios.json";
    
    public GestorUsuariosJSON() {}

    /**
     * Guarda todos los usuarios del sistema en un archivo JSON
     */
    public void guardarUsuarios(SistemaAutenticacion sistemaAutenticacion, String nombreArchivo) throws ErrorPersistenciaException {
        try {
            ArrayList<Usuario> usuarios = (ArrayList<Usuario>) sistemaAutenticacion.listarUsuarios();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("usuarios", serializarLista(usuarios, sistemaAutenticacion));
            jsonObject.put("fechaActualizacion", java.time.LocalDateTime.now().toString());
            jsonObject.put("totalUsuarios", usuarios.size());
            // Guardar el contador actual para mantener la secuencia de IDs
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
     */
    public ArrayList<Usuario> cargarUsuarios(String nombreArchivo) {
        ArrayList<Usuario> usuarios = new ArrayList<>();
        int maxId = 0;
        try {
            FileReader fileReader = new FileReader(nombreArchivo);
            org.json.JSONTokener tokener = new org.json.JSONTokener(fileReader);
            JSONObject jsonObject = new JSONObject(tokener);
            
            JSONArray usuariosArray = jsonObject.getJSONArray("usuarios");
            
            for (int i = 0; i < usuariosArray.length(); i++) {
                JSONObject usuarioJson = usuariosArray.getJSONObject(i);
                Usuario usuario = deserializarUsuario(usuarioJson);
                if (usuario != null) {
                    usuarios.add(usuario);
                    // Encontrar el ID máximo para actualizar el contador
                    if (usuario.getId() > maxId) {
                        maxId = usuario.getId();
                    }
                }
            }
            
            // Restaurar el contador: usar el valor guardado si existe, sino usar el máximo ID encontrado
            if (jsonObject.has("contadorUsuarios")) {
                int contadorGuardado = jsonObject.getInt("contadorUsuarios");
                Usuario.setContador(contadorGuardado);
            } else {
                // Si no hay contador guardado, usar el máximo ID encontrado
                Usuario.setContador(maxId);
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error al cargar usuarios: " + e.getMessage());
        }
        
        return usuarios;
    }
    
    /**
     * Carga usuarios y credenciales desde un archivo JSON
     */
    public Map<String, Credenciales> cargarUsuariosConCredenciales(String nombreArchivo) {
        Map<String, usuarios.Credenciales> credenciales = new HashMap<>();
        
        try {
            FileReader fileReader = new FileReader(nombreArchivo);
            org.json.JSONTokener tokener = new org.json.JSONTokener(fileReader);
            JSONObject jsonObject = new JSONObject(tokener);
            
            JSONArray usuariosArray = jsonObject.getJSONArray("usuarios");
            
            for (int i = 0; i < usuariosArray.length(); i++) {
                JSONObject usuarioJson = usuariosArray.getJSONObject(i);
                String email = usuarioJson.getString("email");
                String password = usuarioJson.optString("password", "temp123");
                
                usuarios.Credenciales credencial = new usuarios.Credenciales(email, password);
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
     */
    private JSONObject serializarUsuario(Usuario usuario, SistemaAutenticacion sistemaAutenticacion) throws JSONException {
        JSONObject usuarioJson = new JSONObject();
        usuarioJson.put("id", usuario.getId());
        usuarioJson.put("nombre", usuario.getNombre() != null ? usuario.getNombre() : "");
        usuarioJson.put("apellido", usuario.getApellido() != null ? usuario.getApellido() : "");
        usuarioJson.put("email", usuario.getEmail() != null ? usuario.getEmail() : "");
        usuarioJson.put("rol", usuario.getRol() != null ? usuario.getRol().toString() : "");
        usuarioJson.put("estado", usuario.getEstado());
        usuarioJson.put("dni", usuario.getDni() != null ? usuario.getDni() : "");
        usuarioJson.put("fechaRegistro", usuario.getFechaRegistro() != null ? usuario.getFechaRegistro().toString() : "");
        
        // Obtener y guardar la contraseña
        try {
            // Acceder a las credenciales del sistema de autenticación
            Field credencialesField = SistemaAutenticacion.class.getDeclaredField("credenciales");
            credencialesField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, usuarios.Credenciales> credenciales = (Map<String, usuarios.Credenciales>) credencialesField.get(sistemaAutenticacion);
            
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
                
                Cliente cliente = new Cliente(id, nombre, apellido, email, rol, estado, dni,
                        cantProductosComprados, metodoPago, saldo, direccion, telefono);
                
                // Cargar historial de compras
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
                
                Vendedor vendedor = new Vendedor(id, nombre, apellido, email, rol, estado, dni, cantVentas, salario);
                vendedor.setComisionPorVenta(comision);
                
                return vendedor;
            }
            
        } catch (Exception e) {
            System.out.println("⚠️ Error al deserializar usuario: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * Verifica si existe el archivo de usuarios
     */
    public boolean existeArchivoUsuarios() {
        return new java.io.File(ARCHIVO_USUARIOS).exists();
    }
}
