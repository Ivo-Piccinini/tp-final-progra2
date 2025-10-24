package persistencia;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import usuarios.Credenciales;
import usuarios.SistemaAutenticacion;
import usuarios.Usuario;
import usuarios.clientes.Cliente;
import usuarios.vendedores.Vendedor;
import usuarios.clientes.MetodoPago;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GestorUsuariosJSON {
    private static final String ARCHIVO_USUARIOS = "data/usuarios.json";
    
    public GestorUsuariosJSON() {}

    /**
     * Guarda todos los usuarios del sistema en un archivo JSON
     */
    public void guardarUsuarios(SistemaAutenticacion sistemaAutenticacion, String nombreArchivo) {
        try {
            ArrayList<Usuario> usuarios = (ArrayList<Usuario>) sistemaAutenticacion.listarUsuarios();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("usuarios", serializarLista(usuarios, sistemaAutenticacion));
            jsonObject.put("fechaActualizacion", java.time.LocalDateTime.now().toString());
            jsonObject.put("totalUsuarios", usuarios.size());
            
            OperacionesLectoEscritura.grabar(nombreArchivo, jsonObject);
            System.out.println("✅ Usuarios guardados exitosamente en: " + nombreArchivo);
        } catch (Exception e) {
            System.out.println("❌ Error al guardar usuarios: " + e.getMessage());
        }
    }

    /**
     * Carga todos los usuarios desde un archivo JSON
     */
    public ArrayList<Usuario> cargarUsuarios(String nombreArchivo) {
        ArrayList<Usuario> usuarios = new ArrayList<>();
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
                }
            }
            
            System.out.println("✅ Usuarios cargados exitosamente desde: " + nombreArchivo);
            System.out.println("👥 Total de usuarios cargados: " + usuarios.size());
            
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
            
            System.out.println("✅ Credenciales cargadas exitosamente desde: " + nombreArchivo);
            System.out.println("🔐 Total de credenciales cargadas: " + credenciales.size());
            
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
        
        // Datos básicos de Usuario
        usuarioJson.put("id", usuario.getId());
        usuarioJson.put("nombre", usuario.getNombre());
        usuarioJson.put("apellido", usuario.getApellido());
        usuarioJson.put("email", usuario.getEmail());
        usuarioJson.put("rol", usuario.getRol().toString());
        usuarioJson.put("estado", usuario.getEstado());
        usuarioJson.put("dni", usuario.getDni());
        usuarioJson.put("fechaRegistro", usuario.getFechaRegistro().toString());
        
        // Obtener y guardar la contraseña
        try {
            // Acceder a las credenciales del sistema de autenticación
            java.lang.reflect.Field credencialesField = SistemaAutenticacion.class.getDeclaredField("credenciales");
            credencialesField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, usuarios.Credenciales> credenciales = (Map<String, usuarios.Credenciales>) credencialesField.get(sistemaAutenticacion);
            
            usuarios.Credenciales credencial = credenciales.get(usuario.getEmail());
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
            for (String compra : cliente.getHistorialCompras()) {
                historialArray.put(compra);
            }
            usuarioJson.put("historialCompras", historialArray);
            
            // Preferencias
            JSONArray preferenciasArray = new JSONArray();
            for (String preferencia : cliente.getPreferencias()) {
                preferenciasArray.put(preferencia);
            }
            usuarioJson.put("preferencias", preferenciasArray);
            
        } else if (usuario instanceof Vendedor) {
            Vendedor vendedor = (Vendedor) usuario;
            usuarioJson.put("tipoUsuario", "VENDEDOR");
            usuarioJson.put("salario", vendedor.getSalario());
            usuarioJson.put("comision", vendedor.getComisionPorVenta());
            usuarioJson.put("metaVentas", vendedor.getMetaVentasMensual());
            usuarioJson.put("especializacion", vendedor.getEspecializacion() != null ? vendedor.getEspecializacion() : "");
            usuarioJson.put("ventasRealizadas", vendedor.getHistorialVentas());
            usuarioJson.put("totalVentas", vendedor.getCantVentas());
        }
        
        return usuarioJson;
    }

    /**
     * Deserializa un JSONObject a un Usuario
     */
    private Usuario deserializarUsuario(JSONObject usuarioJson) {
        try {
            String tipoUsuario = usuarioJson.getString("tipoUsuario");
            String nombre = usuarioJson.getString("nombre");
            String apellido = usuarioJson.getString("apellido");
            String email = usuarioJson.getString("email");
            usuarios.Rol rol = usuarios.Rol.valueOf(usuarioJson.getString("rol"));
            int estado = usuarioJson.getInt("estado");
            String dni = usuarioJson.getString("dni");
            String password = usuarioJson.optString("password", "temp123"); // Contraseña por defecto si no existe
            
            if ("CLIENTE".equals(tipoUsuario)) {
                int cantProductosComprados = usuarioJson.getInt("cantProductosComprados");
                MetodoPago metodoPago = MetodoPago.valueOf(usuarioJson.getString("metodoPago"));
                double saldo = usuarioJson.getDouble("saldo");
                String direccion = usuarioJson.optString("direccion", "");
                String telefono = usuarioJson.optString("telefono", "");
                
                Cliente cliente = new Cliente(nombre, apellido, email, rol, estado, dni, 
                    cantProductosComprados, metodoPago, saldo, direccion, telefono);
                
                // Cargar historial de compras
                JSONArray historialArray = usuarioJson.optJSONArray("historialCompras");
                if (historialArray != null) {
                    for (int i = 0; i < historialArray.length(); i++) {
                        cliente.agregarCompra(historialArray.getString(i));
                    }
                }
                
                // Cargar preferencias
                JSONArray preferenciasArray = usuarioJson.optJSONArray("preferencias");
                if (preferenciasArray != null) {
                    for (int i = 0; i < preferenciasArray.length(); i++) {
                        cliente.agregarPreferencia(preferenciasArray.getString(i));
                    }
                }
                
                return cliente;
                
            } else if ("VENDEDOR".equals(tipoUsuario)) {
                double salario = usuarioJson.getDouble("salario");
                double comision = usuarioJson.getDouble("comision");
                int metaVentas = usuarioJson.getInt("metaVentas");
                String especializacion = usuarioJson.optString("especializacion", "");
                
                Vendedor vendedor = new Vendedor(nombre, apellido, email, rol, estado, dni, salario);
                vendedor.setComisionPorVenta(comision);
                vendedor.setMetaVentasMensual(metaVentas);
                vendedor.setEspecializacion(especializacion);
                
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

    /**
     * Elimina el archivo de usuarios
     */
    public boolean eliminarArchivoUsuarios() {
        try {
            java.io.File archivo = new java.io.File(ARCHIVO_USUARIOS);
            boolean eliminado = archivo.delete();
            if (eliminado) {
                System.out.println("🗑️ Archivo de usuarios eliminado.");
            }
            return eliminado;
        } catch (Exception e) {
            System.out.println("❌ Error al eliminar archivo: " + e.getMessage());
            return false;
        }
    }
}
