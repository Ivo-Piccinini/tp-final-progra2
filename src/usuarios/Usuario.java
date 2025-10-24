package usuarios;

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

    /**
     * Regex (EMAIL_REGEX): Esta expresión regular verifica que la cadena contenga:
     * - Uno o más caracteres válidos ([a-zA-Z0-9._%+-]+).
     * - El símbolo @.
     * - El dominio y subdominios ([a-zA-Z0-9.-]+).
     * - Un punto (\\.).
     * - Una extensión de 2 a 6 letras ([a-zA-Z]{2,6}$).
     */
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
    /**
     * Pattern es una clase del paquete java.util.regex, la cual compila la expresión regular, Y Matcher realiza la comparación real contra el email de entrada
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    private static final String DNI_REGEX = "^\\d{8}$";
    private static final Pattern DNI_PATTERN = Pattern.compile(DNI_REGEX);


    // ---------------------- MÉTODOS DE VALIDACIÓN ----------------------
    private static boolean validaEmail(String email){
        if(email == null){
            return false;
        }
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }

    private static boolean validaDNI(String dni){
        if(dni == null){
            return false;
        }
        Matcher matcher = DNI_PATTERN.matcher(dni);
        return matcher.matches();
    }

    // ---------------------- CONSTRUCTORES ----------------------
    public Usuario(String nombre, String apellido, String email, Rol rol, int estado, String dni) {
        this.id = contador;
        contador++;
        this.nombre = nombre;
        this.apellido = apellido;
        if(!validaEmail(email)){
            throw new IllegalArgumentException("Error: El formato del email es inválido.");
        }
        this.email = email;
        this.rol = rol;
        this.estado = estado;
        if(!validaDNI(dni)){
            throw new IllegalArgumentException("Error: El DNI debe contener exactamente 8 dígitos numéricos.");
        }
        this.dni = dni;
        this.fechaRegistro = LocalDateTime.now();
        this.ultimoAcceso = null;
    }

    public Usuario() {
        this.id = contador;
        contador++;
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
    /**
     * @param email En el caso de que el email sea inválido (no cumpla con el regex establecido) el programa arrojará una IllegalArgumentException, la cual es estándar de Java para indicar que un argumento pasado a un método es inapropiado.
     */
    public void setEmail(String email) {
        if(!validaEmail(email)){
            throw new IllegalArgumentException("Error: El formato del nuevo email es inválido. No se ha realizado la actualización.");
        }
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
    /**
     * @param dni Verifica que el DNI contenga solo 8 dígitos numéricos. Si es inválido, arroja una IllegalArgumentException.
     */
    public void setDni(String dni) {
        if(!validaDNI(dni)){
            throw new IllegalArgumentException("Error: El DNI debe contener exactamente 8 dígitos numéricos. No se ha realizado la actualización.");
        }
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
