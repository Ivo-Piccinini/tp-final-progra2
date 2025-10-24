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
    public boolean verificarPassword(String passwordIngresada) {
        
        if (password.equals(passwordIngresada)) {
            return true;
        } else {
            return false;
        }
    }
    
    public void cambiarPassword(String passwordActual, String passwordNueva) {
        if (verificarPassword(passwordActual)) {
            this.password = passwordNueva;
            System.out.println("✅ Contraseña cambiada exitosamente.");
        } else {
            throw new IllegalArgumentException("❌ La contraseña actual es incorrecta.");
        }
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
