package usuarios;

import usuarios.clientes.Cliente;
import usuarios.vendedores.Vendedor;
import persistencia.GestorUsuariosJSON;
import excepciones.UsuarioYaExisteException;
import excepciones.PasswordInvalidaException;
import excepciones.CredencialesInvalidasException;
import excepciones.ErrorPersistenciaException;
import excepciones.UsuarioNoEncontradoException;
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
    public boolean registrarUsuario(Usuario usuario, String password) throws UsuarioYaExisteException, PasswordInvalidaException {
        if (usuario == null || password == null || password.trim().isEmpty()) {
            throw new PasswordInvalidaException("Usuario o contraseña inválidos.");
        }
        
        String email = usuario.getEmail();
        
        if (credenciales.containsKey(email)) {
            throw new UsuarioYaExisteException("Ya existe un usuario con este email: " + email);
        }
        
        if (password.length() < 6) {
            throw new PasswordInvalidaException("La contraseña debe tener al menos 6 caracteres.");
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
    public boolean login(String email, String password) throws CredencialesInvalidasException {
        // Verificar si hay usuarios registrados
        if (usuarios.isEmpty() || credenciales.isEmpty()) {
            throw new CredencialesInvalidasException("No hay usuarios registrados en el sistema. Por favor, regístrese primero antes de iniciar sesión.");
        }
        
        if (email == null || password == null) {
            throw new CredencialesInvalidasException("Email o contraseña no pueden ser nulos.");
        }
        
        Credenciales creds = credenciales.get(email);
        if (creds == null) {
            throw new CredencialesInvalidasException("Usuario no encontrado: " + email);
        }
        
        if (creds.verificarPassword(password)) {
            usuarioActual = usuarios.get(email);
            usuarioActual.actualizarUltimoAcceso();
            System.out.println("✅ Login exitoso. Bienvenido, " + usuarioActual.getNombre() + "!");
            return true;
        } else {
            throw new CredencialesInvalidasException("Contraseña incorrecta para el usuario: " + email);
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
    
    /**
     * Busca un usuario por su email
     */
    public Usuario buscarUsuarioPorEmail(String email) throws UsuarioNoEncontradoException {
        if (email == null || email.trim().isEmpty()) {
            throw new UsuarioNoEncontradoException("El email no puede ser nulo o vacío.");
        }
        
        Usuario usuario = usuarios.get(email);
        if (usuario == null) {
            throw new UsuarioNoEncontradoException("Usuario no encontrado con email: " + email, email);
        }
        
        return usuario;
    }
    
    /**
     * Busca un usuario por su ID
     */
    public Usuario buscarUsuarioPorId(int id) throws UsuarioNoEncontradoException {
        for (Usuario usuario : usuarios.values()) {
            if (usuario.getId() == id) {
                return usuario;
            }
        }
        
        throw new UsuarioNoEncontradoException("Usuario no encontrado con ID: " + id, id);
    }
    
    /**
     * Da de baja lógica a un usuario (estado = 0 = Inactivo)
     * No permite dar de baja al usuario actual
     */
    public boolean darBajaUsuario(String email) throws UsuarioNoEncontradoException {
        Usuario usuario = buscarUsuarioPorEmail(email);
        
        // No permitir dar de baja al usuario actual
        if (usuarioActual != null && usuarioActual.getEmail().equals(email)) {
            throw new IllegalStateException("No puede dar de baja su propia cuenta.");
        }
        
        usuario.setEstado(0); // 0 = Inactivo
        guardarUsuariosEnArchivo();
        System.out.println("✅ Usuario dado de baja exitosamente: " + usuario.getNombre() + " " + usuario.getApellido());
        return true;
    }
    
    /**
     * Reactiva un usuario (estado = 1 = Activo)
     */
    public boolean reactivarUsuario(String email) throws UsuarioNoEncontradoException {
        Usuario usuario = buscarUsuarioPorEmail(email);
        usuario.setEstado(1); // 1 = Activo
        guardarUsuariosEnArchivo();
        System.out.println("✅ Usuario reactivado exitosamente: " + usuario.getNombre() + " " + usuario.getApellido());
        return true;
    }
    
    /**
     * Modifica los datos básicos de un usuario
     */
    public boolean modificarUsuario(String email, String nuevoNombre, String nuevoApellido, String nuevoDni) 
            throws UsuarioNoEncontradoException {
        Usuario usuario = buscarUsuarioPorEmail(email);
        
        if (nuevoNombre != null && !nuevoNombre.trim().isEmpty()) {
            usuario.setNombre(nuevoNombre.trim());
        }
        
        if (nuevoApellido != null && !nuevoApellido.trim().isEmpty()) {
            usuario.setApellido(nuevoApellido.trim());
        }
        
        if (nuevoDni != null && !nuevoDni.trim().isEmpty()) {
            usuario.setDni(nuevoDni.trim());
        }
        
        guardarUsuariosEnArchivo();
        System.out.println("✅ Usuario modificado exitosamente: " + usuario.getNombre() + " " + usuario.getApellido());
        return true;
    }
    
    /**
     * Modifica datos específicos de un Cliente
     */
    public boolean modificarCliente(String email, String nuevaDireccion, String nuevoTelefono) 
            throws UsuarioNoEncontradoException {
        Usuario usuario = buscarUsuarioPorEmail(email);
        
        if (!(usuario instanceof Cliente)) {
            throw new IllegalArgumentException("El usuario no es un Cliente.");
        }
        
        Cliente cliente = (Cliente) usuario;
        
        if (nuevaDireccion != null) {
            cliente.setDireccion(nuevaDireccion.trim());
        }
        
        if (nuevoTelefono != null) {
            cliente.setTelefono(nuevoTelefono.trim());
        }
        
        guardarUsuariosEnArchivo();
        System.out.println("✅ Cliente modificado exitosamente: " + cliente.getNombre() + " " + cliente.getApellido());
        return true;
    }
    
    /**
     * Modifica datos específicos de un Vendedor
     */
    public boolean modificarVendedor(String email, Double nuevoSalario) 
            throws UsuarioNoEncontradoException {
        Usuario usuario = buscarUsuarioPorEmail(email);
        
        if (!(usuario instanceof Vendedor)) {
            throw new IllegalArgumentException("El usuario no es un Vendedor.");
        }
        
        Vendedor vendedor = (Vendedor) usuario;
        
        if (nuevoSalario != null && nuevoSalario >= 0) {
            vendedor.setSalario(nuevoSalario);
        }
        
        guardarUsuariosEnArchivo();
        System.out.println("✅ Vendedor modificado exitosamente: " + vendedor.getNombre() + " " + vendedor.getApellido());
        return true;
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
        } catch (ErrorPersistenciaException e) {
            System.out.println("❌ Error al guardar usuarios: " + e.getMessage());
        }
    }
    
    /**
     * Guarda usuarios manualmente (para uso externo)
     */
    public void guardarUsuarios() throws ErrorPersistenciaException {
        gestorUsuariosJSON.guardarUsuarios(this, ARCHIVO_USUARIOS);
    }
}
