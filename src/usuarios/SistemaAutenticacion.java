package usuarios;

import usuarios.clientes.Cliente;
import usuarios.vendedores.Vendedor;
import java.util.*;

public class SistemaAutenticacion {
    private Map<String, Credenciales> credenciales;
    private Map<String, Usuario> usuarios;
    private Usuario usuarioActual;
    
    // ---------------------- CONSTRUCTORES ----------------------
    public SistemaAutenticacion() {
        this.credenciales = new HashMap<>();
        this.usuarios = new HashMap<>();
        this.usuarioActual = null;
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
        
        System.out.println("✅ Usuario registrado exitosamente.");
        return true;
    }
    
    // ---------------------- LOGIN ----------------------
    public boolean login(String email, String password) {
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
    public boolean cambiarPassword(String passwordActual, String passwordNueva) {
        if (usuarioActual == null) {
            System.out.println("❌ Error: No hay usuario logueado.");
            return false;
        }
        
        if (passwordNueva.length() < 6) {
            System.out.println("❌ Error: La nueva contraseña debe tener al menos 6 caracteres.");
            return false;
        }
        
        String email = usuarioActual.getEmail();
        Credenciales creds = credenciales.get(email);
        
        try {
            creds.cambiarPassword(passwordActual, passwordNueva);
            return true;
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }
    
    public boolean estaLogueado() {
        return usuarioActual != null;
    }
    
    public String getTipoUsuario() {
        if (usuarioActual == null) {
            return "Ninguno";
        }
        return usuarioActual.getRol().toString();
    }
    
    public void mostrarInfoUsuarioActual() {
        if (usuarioActual != null) {
            System.out.println(usuarioActual.toString());
        } else {
            System.out.println("❌ No hay usuario logueado.");
        }
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
    
    public void mostrarEstadisticas() {
        System.out.println("📊 ESTADÍSTICAS DEL SISTEMA:");
        System.out.println("  👥 Total usuarios: " + usuarios.size());
        System.out.println("  👤 Clientes: " + listarUsuariosPorRol(Rol.CLIENTE).size());
        System.out.println("  👨‍💼 Vendedores: " + listarUsuariosPorRol(Rol.VENDEDOR).size());
        System.out.println("  🟢 Usuario actual: " + (usuarioActual != null ? usuarioActual.getNombre() : "Ninguno"));
    }
}
