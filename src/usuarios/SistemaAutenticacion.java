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

/**
 * ELECCION DE COLECCIONES:
 *
 * - HashMap para credenciales: Usamos HashMap porque necesitamos buscar credenciales rápidamente
 *   por el email del usuario sin tener que revisar todas las credenciales una por una.
 *
 * - HashMap para usuarios: Usamos HashMap porque necesitamos buscar usuarios rápidamente por su
 *   email sin tener que revisar todos los usuarios uno por uno.
 *
 * - ArrayList para listar usuarios: Usamos ArrayList cuando necesitamos devolver una lista de
 *   todos los usuarios que podemos recorrer en orden.
 */
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
    /**
     * Registra usuarios en el sistema con las validaciónes necesarias
     * @param usuario usuario a registrar
     * @param password contraseña del usuario a registrar
     * @throws UsuarioYaExisteException si el usuario que intentamos registrar ya existe
     * @throws PasswordInvalidaException si la contraseña es inválida
     * @return true/false si se creo el usuario o no
     */
    public boolean registrarUsuario(Usuario usuario, String password) throws UsuarioYaExisteException, PasswordInvalidaException {
        // Validación de que el usuario y la contraseña pasados por parámetros no sean null y que la contraseña no esté vacía
        if (usuario == null || password == null || password.trim().isEmpty()) {
            throw new PasswordInvalidaException("Usuario o contraseña inválidos.");
        }
        
        String email = usuario.getEmail();

        // Verifica si ya existe un usuario con ese email
        if (credenciales.containsKey(email)) {
            throw new UsuarioYaExisteException("Ya existe un usuario con este email: " + email);
        }

        // Verifica que la contraseña tenga más de 6 caracteres
        if (password.length() < 6) {
            throw new PasswordInvalidaException("La contraseña debe tener al menos 6 caracteres.");
        }
        
        // Creamos credenciales y registramos el usuario
        Credenciales creds = new Credenciales(email, password);
        credenciales.put(email, creds);
        usuarios.put(email, usuario);
        
        // Guardamos el usuario en el archivo
        guardarUsuariosEnArchivo();
        
        System.out.println("✅ Usuario registrado exitosamente.");
        return true;
    }
    
    // ---------------------- LOGIN ----------------------
    /**
     *  Inicio de sesión al sistema
     * @param email email del usuario que quiere acceder al sistema
     * @param password contraseña del usuario que quiere acceder al sistema
     * @throws CredencialesInvalidasException si las credenciales no son validas
     * @return true o false si el usuario se pudo loguear o no
     */
    public boolean login(String email, String password) throws CredencialesInvalidasException {
        // Verifica si hay usuarios registrados
        if (usuarios.isEmpty() || credenciales.isEmpty()) {
            throw new CredencialesInvalidasException("No hay usuarios registrados en el sistema. Por favor, regístrese primero antes de iniciar sesión.");
        }

        // Verifica que el email y la contraseña no sean null
        if (email == null || password == null) {
            throw new CredencialesInvalidasException("Email o contraseña no pueden ser nulos.");
        }

        // Verifica que el usuario tenga una cuenta en el sistema
        Credenciales creds = credenciales.get(email);
        if (creds == null) {
            throw new CredencialesInvalidasException("Usuario no encontrado: " + email);
        }

        // Verifica que la contraseña sea correcta
        if (creds.verificarPassword(password)) {
            Usuario usuario = usuarios.get(email);
            
            // Verifica que el usuario esté activo (estado = 1)
            if (usuario.getEstado() == 0) {
                throw new CredencialesInvalidasException("No se puede iniciar sesión. El usuario está inactivo. Contacte al administrador.");
            }
            
            usuarioActual = usuario;
            usuarioActual.actualizarUltimoAcceso();
            System.out.println("✅ Login exitoso. Bienvenido, " + usuarioActual.getNombre() + "!");
            return true;
        } else {
            throw new CredencialesInvalidasException("Contraseña incorrecta para el usuario: " + email);
        }
    }

    /**
     *  Cierre de sesión
     */
    public void logout() {
        if (usuarioActual != null) {
            System.out.println("👋 Hasta luego, " + usuarioActual.getNombre() + "!");
            usuarioActual = null;
        } else {
            System.out.println("❌ No hay usuario logueado.");
        }
    }
    
    // ---------------------- METODOS  ----------------------

    /**
     *  Obtiene el usuario que esta usando el sistema en ese momento
     * @return el usuario actual
     */
    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    /**
     *  Indica con true/false si el usuario está logueado
     */
    public boolean estaLogueado() {
        return usuarioActual != null;
    }

    /**
     *  Lista los usuarios del sistema
     * @return una lista con todos los usuarios del sistema
     */
    public List<Usuario> listarUsuarios() {
        return new ArrayList<>(usuarios.values());
    }

    
    /**
     * Verifica si hay usuarios registrados en el sistema
     * @return true/false si hay usuarios registrados o no
     */
    public boolean hayUsuariosRegistrados() {
        return !usuarios.isEmpty() && !credenciales.isEmpty();
    }
    
    /**
     * Busca un usuario por su email
     * @param email el email del usuario a buscar
     * @throws UsuarioNoEncontradoException si no encuentra el usuario
     * @return el usuario del sisitema que coincida con el email ingresado
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
     * Da de baja lógica a un usuario (estado = 0 = Inactivo)
     * No permite dar de baja al usuario actual
     * @param email Email del usuario a dar de baja
     * @throws UsuarioNoEncontradoException en caso de no encontrar el usuario
     * @return true si el usuario se pudo dar de baja
     */
    public boolean darBajaUsuario(String email) throws UsuarioNoEncontradoException {
        Usuario usuario = buscarUsuarioPorEmail(email);
        
        // No permitir dar de baja al usuario actual
        if (usuarioActual != null && usuarioActual.getEmail().equals(email)) {
            throw new IllegalStateException("No puede dar de baja su propia cuenta.");
        }
        
        usuario.setEstado(0);
        guardarUsuariosEnArchivo();
        System.out.println("✅ Usuario dado de baja exitosamente: " + usuario.getNombre() + " " + usuario.getApellido());
        return true;
    }
    
    /**
     * Reactiva un usuario (estado = 1 = Activo)
     * @param email Email del usuario a reactivar
     * @throws UsuarioNoEncontradoException si el usuario no se encontró
     * @return true si el usuario pudo ser reactivado
     */
    public boolean reactivarUsuario(String email) throws UsuarioNoEncontradoException {
        Usuario usuario = buscarUsuarioPorEmail(email);
        usuario.setEstado(1); // 1 = Activo
        guardarUsuariosEnArchivo();
        System.out.println("✅ Usuario reactivado exitosamente: " + usuario.getNombre() + " " + usuario.getApellido());
        return true;
    }
    
    /**
     * Modifica los datos básicos de un usuario (Los datos que todos los usuarios tienen, sean clientes o vendedores)
     * @param email email del usuario
     * @param nuevoNombre nuevo nombre del usuario
     * @param nuevoApellido nuevo apellido del usuario
     * @param nuevoDni nuevo dni del usuario
     * @throws UsuarioNoEncontradoException si no se encuentra el usuario buscado por email
     * @return true si el usuario fue modificado con éxito
     */
    public boolean modificarUsuario(String email, String nuevoNombre, String nuevoApellido, String nuevoDni) throws UsuarioNoEncontradoException {
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
        return true;
    }
    
    /**
     * Modifica datos específicos de un Cliente
     * @param email email del cliente
     * @param nuevaDireccion nueva dirección del cliente
     * @param nuevoTelefono nuevo teléfono del cliente
     * @throws UsuarioNoEncontradoException si el usuario no existe
     * @return true si el cliente fue modificado con éxito
     */
    public boolean modificarCliente(String email, String nuevaDireccion, String nuevoTelefono) throws UsuarioNoEncontradoException {
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
        
        // No guardar aquí, se guardará al final de todas las modificaciones
        return true;
    }
    
    /**
     * Modifica datos específicos de un Vendedor
     * @param email email del vendedor
     * @param nuevaComision nueva comisión del vendedor
     * @param nuevoSalario nuevo salario del vendedor
     * @throws UsuarioNoEncontradoException si el vendedor no fue encontrado
     * @return true si se pudo modificar el vendedor
     */
    public boolean modificarVendedor(String email, Double nuevoSalario, Double nuevaComision) throws UsuarioNoEncontradoException {
        Usuario usuario = buscarUsuarioPorEmail(email);

        // verifica que el usuario sea un vendedor
        if (!(usuario instanceof Vendedor)) {
            throw new IllegalArgumentException("El usuario no es un Vendedor.");
        }
        
        Vendedor vendedor = (Vendedor) usuario;

        // verifica que el nuevo salario no sea null y sea mayor a 0 (sino re rata el jefe jajaj)
        if (nuevoSalario != null && nuevoSalario >= 0) {
            vendedor.setSalario(nuevoSalario);
        }

        // verifica que la nueva comisión no sea null, sea mayor o igual a 0 y menor o igual a 100
        if (nuevaComision != null && nuevaComision >= 0 && nuevaComision <= 100) {
            vendedor.setComisionPorVenta(nuevaComision);
        }

        return true;
    }
    
    // ---------------------- METODOS DE PERSISTENCIA ----------------------
    
    /**
     * Carga usuarios desde el archivo JSON
     */
    private void cargarUsuariosDesdeArchivo() {
        try {
            // Creamos la carpeta donde se guardaran los json (si no existe)
            File directorio = new File("data");
            if (!directorio.exists()) {
                directorio.mkdirs(); // mkdirs es una función para crear carpetas
            }
            
            // Verificamos si existe el archivo
            if (gestorUsuariosJSON.existeArchivoUsuarios()) {
                List<Usuario> usuariosCargados = gestorUsuariosJSON.cargarUsuarios(ARCHIVO_USUARIOS);
                Map<String, Credenciales> credencialesCargadas = gestorUsuariosJSON.cargarUsuariosConCredenciales(ARCHIVO_USUARIOS);
                
                // Cargamos los usuarios usuarios en el sistema
                for (Usuario usuario : usuariosCargados) {
                    usuarios.put(usuario.getEmail(), usuario);
                }
                
                // Cargamos las credenciales reales
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
