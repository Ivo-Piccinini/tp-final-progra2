package usuarios.vendedores;

import usuarios.Rol;
import usuarios.Usuario;

public class Vendedor extends Usuario {
    private int cantVentas = 0;
    private double salario;

    // ---------------------- CONSTRUCTORES ----------------------
    public Vendedor(String nombre, String apellido, String email, Rol rol, int estado, String dni, double salario) {
        super(nombre, apellido, email, rol, estado, dni);
        if (salario < 0) {
            throw new IllegalArgumentException("El salario del vendedor no puede ser negativo.");
        }
        this.salario = salario;
    }

    public Vendedor(String nombre, String apellido, String email, Rol rol, int estado, String dni, int cantVentas, double salario) {
        super(nombre, apellido, email, rol, estado, dni);
        if (salario < 0) {
            throw new IllegalArgumentException("El salario del vendedor no puede ser negativo.");
        }
        if (cantVentas < 0) {
            throw new IllegalArgumentException("La cantidad de ventas no puede ser negativa.");
        }
        this.cantVentas = cantVentas;
        this.salario = salario;
    }

    // ---------------------- GETTERS Y SETTERS ----------------------
    public int getCantVentas() {
        return cantVentas;
    }
    public void setCantVentas(int cantVentas) {
        if (cantVentas < 0) {
            throw new IllegalArgumentException("La cantidad de ventas no puede ser negativa.");
        }
        this.cantVentas = cantVentas;
    }
    public double getSalario() {
        return salario;
    }
    public void setSalario(double salario) {
        if (salario < 0) {
            throw new IllegalArgumentException("El salario del vendedor no puede ser negativo.");
        }
        this.salario = salario;
    }

    // ---------------------- MÉTODOS SOBREESCRITOS ----------------------
    @Override
    public String toString() {
        return super.toString() + "\n" +
                "  💵 Salario: $" + String.format("%.2f", salario) + "\n" +
                "  📈 Cant. Ventas Realizadas: " + cantVentas + "\n" +
                "══════════════════════════════════";
    }

}
