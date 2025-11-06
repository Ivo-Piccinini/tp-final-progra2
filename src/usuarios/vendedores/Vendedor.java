package usuarios.vendedores;

import usuarios.Rol;
import usuarios.Usuario;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Vendedor extends Usuario {
    private int cantVentas = 0;
    private double salario;
    private double comisionPorVenta;
    private List<String> historialVentas;
    private double totalComisiones;

    // ---------------------- CONSTRUCTORES ----------------------
    public Vendedor(String nombre, String apellido, String email, Rol rol, int estado, String dni, double salario) {
        super(nombre, apellido, email, rol, estado, dni);
        if (salario < 0) {
            throw new IllegalArgumentException("El salario del vendedor no puede ser negativo.");
        }
        if (comisionPorVenta < 0) {
            throw new IllegalArgumentException("La comisión por venta no puede ser negativa.");
        }
        this.salario = salario;
        this.historialVentas = new ArrayList<>();
        this.totalComisiones = 0.0;
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
        this.comisionPorVenta = 0.0;
        this.historialVentas = new ArrayList<>();
        this.totalComisiones = 0.0;
    }
    public Vendedor(int id, String nombre, String apellido, String email, Rol rol, int estado, String dni, int cantVentas, double salario) {
        super(id, nombre, apellido, email, rol, estado, dni);
        if (salario < 0) {
            throw new IllegalArgumentException("El salario del vendedor no puede ser negativo.");
        }
        if (cantVentas < 0) {
            throw new IllegalArgumentException("La cantidad de ventas no puede ser negativa.");
        }
        this.cantVentas = cantVentas;
        this.salario = salario;
        this.comisionPorVenta = 0.0;
        this.historialVentas = new ArrayList<>();
        this.totalComisiones = 0.0;
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
    public double getComisionPorVenta() {
        return comisionPorVenta;
    }
    public void setComisionPorVenta(double comisionPorVenta) {
        if (comisionPorVenta < 0) {
            throw new IllegalArgumentException("La comisión por venta no puede ser negativa.");
        }
        this.comisionPorVenta = comisionPorVenta;
    }
    public List<String> getHistorialVentas() {
        return new ArrayList<>(historialVentas);
    }
    public double getTotalComisiones() {
        return totalComisiones;
    }
    
    // ---------------------- METODOS ----------------------
    public void realizarVenta(String descripcionVenta, double montoVenta) {
        String venta = LocalDateTime.now().toString() + " - " + descripcionVenta + " - Monto: $" + String.format("%.2f", montoVenta);
        historialVentas.add(venta);
        cantVentas++;
        
        // Calcular comisión
        double comision = montoVenta * (comisionPorVenta / 100.0);
        totalComisiones += comision;
    }
    
    public double calcularSalarioTotal() {
        return salario + totalComisiones;
    }

    // ---------------------- MÉTODOS SOBREESCRITOS ----------------------
    @Override
    public String toString() {
        return super.toString() + "\n" +
                "  💵 Salario Base: $" + String.format("%.2f", salario) + "\n" +
                "  📈 Cant. Ventas Realizadas: " + cantVentas + "\n" +
                "  💰 Comisión por Venta: " + String.format("%.1f", comisionPorVenta) + "%\n" +
                "  💎 Total Comisiones: $" + String.format("%.2f", totalComisiones) + "\n" +
                "  💰 Salario Total: $" + String.format("%.2f", calcularSalarioTotal()) + "\n" +
                "══════════════════════════════════";
    }

}
