package usuarios;

import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Usuario implements Comparable<Usuario> {
    private int id;
    private static int contador = 0;
    private String nombre;
    private String apellido;
    private String email;
    private Rol rol;
    private int estado = 0;
    private String dni;
    private LocalDateTime fechaRegistro;
    private LocalDateTime ultimoAcceso;

    // ---------------------- CONSTRUCTORES ----------------------
    public Usuario(String nombre, String apellido, String email, Rol rol, int estado, String dni) {
        this.id = ++contador;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.rol = rol;
        this.estado = estado;
        this.dni = dni;
        this.fechaRegistro = LocalDateTime.now();
        this.ultimoAcceso = null;
    }

    // Este es para que reciba el id (solo JSON)
    public Usuario(int id, String nombre, String apellido, String email, Rol rol, int estado, String dni) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.rol = rol;
        this.estado = estado;
        this.dni = dni;
        this.fechaRegistro = LocalDateTime.now();
        this.ultimoAcceso = null;
    }

    public Usuario() {
        this.id = ++contador;
        this.fechaRegistro = LocalDateTime.now();
        this.ultimoAcceso = null;
    }

    // ---------------------- GETTERS Y SETTERS ----------------------
    public int getId() {
        return id;
    }
    public static int getContador() {
        return contador;
    }
    public static void setContador(int nuevoContador) {
        contador = nuevoContador;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getApellido() {
        return apellido;
    }
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public Rol getRol() {
        return rol;
    }
    public void setRol(Rol rol) {
        this.rol = rol;
    }
    public int getEstado() {
        return estado;
    }
    public void setEstado(int estado) {
        this.estado = estado;
    }
    public String getDni() {
        return dni;
    }
    public void setDni(String dni) {
        this.dni = dni;
    }
    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }
    public LocalDateTime getUltimoAcceso() {
        return ultimoAcceso;
    }
    public void actualizarUltimoAcceso() {
        this.ultimoAcceso = LocalDateTime.now();
    }


    // ---------------------- MÉTODOS SOBREESCRITOS ----------------------
    @Override
    public String toString() {
        return "══════════════════════════════════\n" +
                "  DATOS DEL USUARIO (ID: " + id + ")\n" +
                "══════════════════════════════════\n" +
                "  👤 Nombre Completo: " + nombre + " " + apellido + "\n" +
                "  📧 Email: " + email + "\n" +
                "  🔢 DNI: " + dni + "\n" +
                "  💼 Rol: " + (rol != null ? rol : "No Asignado") + "\n" +
                "  🟢 Estado: " + (estado == 1 ? "Activo" : "Inactivo") + "\n" +
                "  📅 Fecha Registro: " + (fechaRegistro != null ? fechaRegistro.toString() : "No disponible") + "\n" +
                "  🕒 Último Acceso: " + (ultimoAcceso != null ? ultimoAcceso.toString() : "Nunca") + "\n" +
                "══════════════════════════════════";
    }
    @Override
    public int compareTo(Usuario o) {
        return Integer.compare(this.id, o.id);
    }
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return id == usuario.id && Objects.equals(dni, usuario.dni);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id, dni);
    }
}
