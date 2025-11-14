package usuarios;

import java.util.Objects;

public class Credenciales {
    private String email;
    private String password;
    
    // ---------------------- CONSTRUCTORES ----------------------
    public Credenciales(String email, String password) {
        this.email = email;
        this.password = password;
    }
    
    // ---------------------- GETTERS Y SETTERS ----------------------
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    
    // ---------------------- MÉTODOS DE AUTENTICACIÓN ----------------------
    /**
     * Verifica que la contraseña ingresada en el login sea la misma que la contraseña del usuario
     * @return Verdadero si la contraseña es la misma, Falso si no
     */
    public boolean verificarPassword(String passwordIngresada) {

        return password.equals(passwordIngresada);
    }
    
    // ---------------------- MÉTODOS SOBREESCRITOS ----------------------
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Credenciales that = (Credenciales) o;
        return Objects.equals(email, that.email);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
    
    @Override
    public String toString() {
        return "Credenciales{" +
                "email='" + email + '\'' +
                '}';
    }
}
