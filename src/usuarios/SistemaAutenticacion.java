package usuarios;

import usuarios.clientes.Cliente;
import usuarios.vendedores.Vendedor;
import persistencia.GestorUsuariosJSON;
import java.util.*;
import java.io.File;

public class SistemaAutenticacion {
    private Map<String, Credenciales> credenciales;
    private Map<String, Usuario> usuarios;
    private Usuario usuarioActual;
    private GestorUsuariosJSON gestorUsuariosJSON;
    private static final String ARCHIVO_USUARIOS = "data/usuarios.json";
    
    // ---------------------- CONSTRUCTORES ----------------------
    public SistemaAutenticacion() {
        this.credenciales = new HashMap<>();
        this.usuarios = new HashMap<>();
        this.usuarioActual = null;
        this.gestorUsuariosJSON = new GestorUsuariosJSON();
        
        // Cargar usuarios desde archivo JSON al inicializar
        cargarUsuariosDesdeArchivo();
    }
    
    // ----------------------REGISTRO ----------------------
    public boolean registrarUsuario(Usuario usuario, String password) {
        if (usuario == null || password == null || password.trim().isEmpty()) {
            System.out.println("❌ Error: Usuario o contraseña inválidos.");
            return false;
        }
        
        String email = usuario.getEmail();
        
        if (credenciales.containsKey(email)) {
            System.out.println("❌ Error: Ya existe un usuario con este email.");
            return false;
        }
        
        if (password.length() < 6) {
            System.out.println("❌ Error: La contraseña debe tener al menos 6 caracteres.");
            return false;
        }
        
        // Crear credenciales y registrar usuario
        Credenciales creds = new Credenciales(email, password);
        credenciales.put(email, creds);
        usuarios.put(email, usuario);
        
        // Guardar usuarios en archivo JSON
        guardarUsuariosEnArchivo();
        
        System.out.println("✅ Usuario registrado exitosamente.");
        return true;
    }
    
    // ---------------------- LOGIN ----------------------
    public boolean login(String email, String password) {
        // Verificar si hay usuarios registrados
        if (usuarios.isEmpty() || credenciales.isEmpty()) {
            System.out.println("❌ Error: No hay usuarios registrados en el sistema.");
            System.out.println("📝 Por favor, regístrese primero antes de iniciar sesión.");
            return false;
        }
        
        if (email == null || password == null) {
            System.out.println("❌ Error: Email o contraseña no pueden ser nulos.");
            return false;
        }
        
        Credenciales creds = credenciales.get(email);
        if (creds == null) {
            System.out.println("❌ Error: Usuario no encontrado.");
            return false;
        }
        
        if (creds.verificarPassword(password)) {
            usuarioActual = usuarios.get(email);
            usuarioActual.actualizarUltimoAcceso();
            System.out.println("✅ Login exitoso. Bienvenido, " + usuarioActual.getNombre() + "!");
            return true;
        } else {
            System.out.println("❌ Error: Contraseña incorrecta.");
            return false;
        }
    }
    
    public void logout() {
        if (usuarioActual != null) {
            System.out.println("👋 Hasta luego, " + usuarioActual.getNombre() + "!");
            usuarioActual = null;
        } else {
            System.out.println("❌ No hay usuario logueado.");
        }
    }
    
    // ---------------------- METODOS  ----------------------

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }
    
    public boolean estaLogueado() {
        return usuarioActual != null;
    }
    
    public List<Usuario> listarUsuarios() {
        return new ArrayList<>(usuarios.values());
    }
    
    public List<Usuario> listarUsuariosPorRol(Rol rol) {
        List<Usuario> usuariosPorRol = new ArrayList<>();
        for (Usuario usuario : usuarios.values()) {
            if (usuario.getRol() == rol) {
                usuariosPorRol.add(usuario);
            }
        }
        return usuariosPorRol;
    }
    
    /**
     * Verifica si hay usuarios registrados en el sistema
     */
    public boolean hayUsuariosRegistrados() {
        return !usuarios.isEmpty() && !credenciales.isEmpty();
    }
    
    // ---------------------- METODOS DE PERSISTENCIA ----------------------
    
    /**
     * Carga usuarios desde el archivo JSON
     */
    private void cargarUsuariosDesdeArchivo() {
        try {
            // Crear directorio si no existe
            File directorio = new File("data");
            if (!directorio.exists()) {
                directorio.mkdirs();
                System.out.println("📁 Directorio de datos creado: data");
            }
            
            // Verificar si existe el archivo
            if (gestorUsuariosJSON.existeArchivoUsuarios()) {
                List<Usuario> usuariosCargados = gestorUsuariosJSON.cargarUsuarios(ARCHIVO_USUARIOS);
                Map<String, Credenciales> credencialesCargadas = gestorUsuariosJSON.cargarUsuariosConCredenciales(ARCHIVO_USUARIOS);
                
                // Cargar usuarios en el sistema
                for (Usuario usuario : usuariosCargados) {
                    usuarios.put(usuario.getEmail(), usuario);
                }
                
                // Cargar credenciales reales
                credenciales.putAll(credencialesCargadas);
            }
        } catch (Exception e) {
            System.out.println("⚠️ Error al cargar usuarios: " + e.getMessage());
            System.out.println("📁 Continuando con usuarios vacíos.");
        }
    }
    
    /**
     * Guarda usuarios en el archivo JSON
     */
    private void guardarUsuariosEnArchivo() {
        try {
            gestorUsuariosJSON.guardarUsuarios(this, ARCHIVO_USUARIOS);
        } catch (Exception e) {
            System.out.println("❌ Error al guardar usuarios: " + e.getMessage());
        }
    }
    
    /**
     * Guarda usuarios manualmente (para uso externo)
     */
    public void guardarUsuarios() {
        guardarUsuariosEnArchivo();
    }
}
