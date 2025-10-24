import usuarios.*;
import usuarios.clientes.Cliente;
import usuarios.clientes.MetodoPago;
import usuarios.vendedores.Vendedor;
import productos.*;
import inventario.Stock;
import ventas.Venta;
import ventas.DetalleVenta;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Scanner;
import java.util.InputMismatchException;

/**
 * Clase central que gestiona todo el sistema de comercio de tecnología.
 */
public class SistemaComercio {
    private SistemaAutenticacion sistemaAutenticacion;
    private Stock stock;
    private List<Venta> ventas;
    private LocalDateTime fechaInicioSistema;
    
    // ---------------------- CONSTRUCTOR ----------------------
    public SistemaComercio() {
        this.sistemaAutenticacion = new SistemaAutenticacion();
        this.stock = new Stock();
        this.ventas = new ArrayList<>();
        this.fechaInicioSistema = LocalDateTime.now();
        
        System.out.println("🚀 SISTEMA DE COMERCIO DE TECNOLOGÍA INICIADO");
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("📅 Fecha de inicio: " + fechaInicioSistema);
        System.out.println("═══════════════════════════════════════════════\n");
    }
    
    // ---------------------- METODOS DE AUTENTICACION ----------------------
    public boolean login(String email, String password) {
        return sistemaAutenticacion.login(email, password);
    }
    
    public void logout() {
        sistemaAutenticacion.logout();
    }
    
    public boolean registrarUsuario(Usuario usuario, String password) {
        return sistemaAutenticacion.registrarUsuario(usuario, password);
    }
    
    public Usuario getUsuarioActual() {
        return sistemaAutenticacion.getUsuarioActual();
    }
    
    public boolean estaLogueado() {
        return sistemaAutenticacion.estaLogueado();
    }

    
    // ---------------------- METODOS ----------------------
    
    public void mostrarInventario() {
        stock.mostrarInventario();
    }
    
    public void mostrarProductosDisponibles() {
        stock.mostrarProductosDisponibles();
    }
    
    // ---------------------- METODOS DE COMPRA PARA CLIENTES ----------------------
    
    /**
     * Permite a un cliente comprar productos directamente
     */
    public boolean comprarProducto(int productoId, int cantidad) {
        if (!estaLogueado()) {
            System.out.println("❌ Error: Debe estar logueado para realizar una compra.");
            return false;
        }
        
        Usuario usuario = getUsuarioActual();
        if (!(usuario instanceof Cliente)) {
            System.out.println("❌ Error: Solo los clientes pueden realizar compras directas.");
            return false;
        }
        
        Cliente cliente = (Cliente) usuario;
        Producto producto = stock.obtenerProducto(productoId);
        
        if (producto == null) {
            System.out.println("❌ Error: Producto no encontrado.");
            return false;
        }
        
        if (!producto.isActivo()) {
            System.out.println("❌ Error: El producto no está disponible.");
            return false;
        }
        
        if (!stock.hayStock(productoId, cantidad)) {
            System.out.println("❌ Error: No hay suficiente stock disponible.");
            return false;
        }
        
        double totalCompra = producto.getPrecio() * cantidad;
        
        if (cliente.getSaldo() < totalCompra) {
            System.out.println("❌ Error: Saldo insuficiente.");
            System.out.println("💰 Su saldo actual: $" + String.format("%.2f", cliente.getSaldo()));
            System.out.println("💵 Total de la compra: $" + String.format("%.2f", totalCompra));
            return false;
        }
        
        // Procesar la compra
        if (stock.eliminarProducto(productoId, cantidad)) {
            // Actualizar saldo del cliente
            cliente.setSaldo(cliente.getSaldo() - totalCompra);
            
            // Registrar la compra
            String descripcionCompra = producto.getNombre() + " x" + cantidad + " = $" + String.format("%.2f", totalCompra);
            cliente.agregarCompra(descripcionCompra);
            
            System.out.println("✅ ¡Compra realizada exitosamente!");
            System.out.println("📱 Producto: " + producto.getNombre());
            System.out.println("📦 Cantidad: " + cantidad);
            System.out.println("💵 Total pagado: $" + String.format("%.2f", totalCompra));
            System.out.println("💰 Saldo restante: $" + String.format("%.2f", cliente.getSaldo()));
            
            return true;
        } else {
            System.out.println("❌ Error al procesar la compra.");
            return false;
        }
    }
    
    /**
     * Permite a un cliente agregar saldo a su cuenta
     */
    public boolean agregarSaldo(double monto) {
        if (!estaLogueado()) {
            System.out.println("❌ Error: Debe estar logueado para agregar saldo.");
            return false;
        }
        
        Usuario usuario = getUsuarioActual();
        if (!(usuario instanceof Cliente)) {
            System.out.println("❌ Error: Solo los clientes pueden agregar saldo.");
            return false;
        }
        
        if (monto <= 0) {
            System.out.println("❌ Error: El monto debe ser mayor a 0.");
            return false;
        }
        
        Cliente cliente = (Cliente) usuario;
        cliente.setSaldo(cliente.getSaldo() + monto);
        
        System.out.println("✅ Saldo agregado exitosamente!");
        System.out.println("💰 Saldo anterior: $" + String.format("%.2f", cliente.getSaldo() - monto));
        System.out.println("💰 Saldo actual: $" + String.format("%.2f", cliente.getSaldo()));
        
        return true;
    }
    
    /**
     * Muestra el historial de compras del cliente actual
     */
    public void mostrarHistorialCompras() {
        if (!estaLogueado()) {
            System.out.println("❌ Error: Debe estar logueado para ver el historial.");
            return;
        }
        
        Usuario usuario = getUsuarioActual();
        if (!(usuario instanceof Cliente)) {
            System.out.println("❌ Error: Solo los clientes pueden ver su historial de compras.");
            return;
        }
        
        Cliente cliente = (Cliente) usuario;
        cliente.mostrarHistorialCompras();
    }
    
    // ---------------------- METODOS DE GESTION DE VENTAS ----------------------
    public Venta crearVentaSimple() {
        if (!estaLogueado()) {
            System.out.println("❌ Error: Debe estar logueado para crear una venta.");
            return null;
        }
        
        Usuario usuario = getUsuarioActual();
        if (!(usuario instanceof Vendedor)) {
            System.out.println("❌ Error: Solo los vendedores pueden procesar ventas.");
            return null;
        }
        
        // Seleccionar cliente existente
        Cliente clienteSeleccionado = seleccionarCliente();
        if (clienteSeleccionado == null) {
            System.out.println("❌ Error: No se pudo seleccionar un cliente.");
            return null;
        }
        
        Vendedor vendedor = (Vendedor) usuario;
        
        Venta venta = new Venta(clienteSeleccionado, vendedor);
        ventas.add(venta);
        System.out.println("✅ Venta creada para cliente: " + clienteSeleccionado.getNombre() + " " + clienteSeleccionado.getApellido());
        return venta;
    }
    
    /**
     * Permite seleccionar un cliente existente de la lista de usuarios registrados
     */
    private Cliente seleccionarCliente() {
        Scanner scanner = new Scanner(System.in);
        
        // Obtener todos los clientes registrados
        List<Cliente> clientes = new ArrayList<>();
        for (Usuario usuario : sistemaAutenticacion.listarUsuarios()) {
            if (usuario instanceof Cliente) {
                clientes.add((Cliente) usuario);
            }
        }
        
        if (clientes.isEmpty()) {
            System.out.println("❌ No hay clientes registrados en el sistema.");
            return null;
        }
        
        // Mostrar lista de clientes
        System.out.println("👥 SELECCIONAR CLIENTE");
        System.out.println("═══════════════════════════════════");
        for (int i = 0; i < clientes.size(); i++) {
            Cliente cliente = clientes.get(i);
            System.out.println((i + 1) + ". " + cliente.getNombre() + " " + cliente.getApellido() + 
                             " (" + cliente.getEmail() + ")");
            System.out.println("💰 Saldo: $" + String.format("%.2f", cliente.getSaldo()) +" | 🛍️ Compras: " + cliente.getCantProductosComprados());
        }
        System.out.println("═══════════════════════════════════");
        
        // Solicitar selección
        while (true) {
            try {
                System.out.print("Seleccione el cliente (número) o 0 para ver detalles: ");
                int opcion = scanner.nextInt();
                scanner.nextLine(); // Limpiar buffer
                
                if (opcion == 0) {
                    // Mostrar detalles de todos los clientes
                    mostrarDetallesClientes(clientes);
                    continue;
                } else if (opcion >= 1 && opcion <= clientes.size()) {
                    Cliente clienteSeleccionado = clientes.get(opcion - 1);
                    System.out.println("✅ Cliente seleccionado: " + clienteSeleccionado.getNombre() + " " + clienteSeleccionado.getApellido());
                    return clienteSeleccionado;
                } else {
                    System.out.println("❌ Opción no válida. Intente nuevamente.");
                }
            } catch (InputMismatchException e) {
                System.out.println("❌ Debe ingresar un número válido.");
                scanner.nextLine(); // Limpiar buffer
            }
        }
    }
    
    /**
     * Muestra detalles completos de todos los clientes
     */
    private void mostrarDetallesClientes(List<Cliente> clientes) {
        System.out.println("📋 DETALLES COMPLETOS DE CLIENTES");
        System.out.println("═══════════════════════════════════════════════════════════════");
        
        for (int i = 0; i < clientes.size(); i++) {
            Cliente cliente = clientes.get(i);
            System.out.println((i + 1) + ". " + cliente.getNombre() + " " + cliente.getApellido());
            System.out.println("   📧 Email: " + cliente.getEmail());
            System.out.println("   📍 Dirección: " + (cliente.getDireccion() != null ? cliente.getDireccion() : "No registrada"));
            System.out.println("   📞 Teléfono: " + (cliente.getTelefono() != null ? cliente.getTelefono() : "No registrado"));
            System.out.println("   💰 Saldo: $" + String.format("%.2f", cliente.getSaldo()));
            System.out.println("   🛍️ Compras realizadas: " + cliente.getCantProductosComprados());
            System.out.println("   💳 Método de pago: " + cliente.getMetodoPago());
            System.out.println("   ⭐ Preferencias: " + cliente.getPreferencias().size() + " registradas");
            System.out.println("   📅 Miembro desde: " + cliente.getFechaRegistro());
            System.out.println("   ───────────────────────────────────────────────────────────");
        }
        
        System.out.println("═══════════════════════════════════════════════════════════════");
    }
    
    public boolean agregarProductoAVenta(Venta venta, int productoId, int cantidad) {
        if (venta == null) {
            System.out.println("❌ Error: La venta no puede ser null.");
            return false;
        }
        
        Producto producto = stock.obtenerProducto(productoId);
        if (producto == null) {
            System.out.println("❌ Error: Producto no encontrado.");
            return false;
        }
        
        return venta.agregarProducto(producto, cantidad, stock);
    }
    
    public boolean procesarVenta(Venta venta) {
        if (venta == null) {
            System.out.println("❌ Error: La venta no puede ser null.");
            return false;
        }
        
        boolean resultado = venta.procesarVenta(stock);
        if (resultado) {
            System.out.println("✅ Venta procesada exitosamente.");
            venta.mostrarDetallesVenta();
        } else {
            System.out.println("❌ Error al procesar la venta. Verifique el stock.");
        }
        
        return resultado;
    }
    
    public void mostrarVentas() {
        System.out.println("🧾 HISTORIAL DE VENTAS");
        System.out.println("═══════════════════════════════════");
        
        if (ventas.isEmpty()) {
            System.out.println("📭 No hay ventas registradas.");
        } else {
            for (Venta venta : ventas) {
                System.out.println(venta.toString());
            }
        }
    }


    // ---------------------- MÉTODOS DE INICIALIZACIÓN ----------------------
    public void inicializarSistema() {
        crearProductosEjemplo();
    }
    
    private void crearProductosEjemplo() {
        // Crear productos de tecnología de ejemplo
        Producto laptop1 = new Producto("Dell Inspiron 15", "Laptop para trabajo y estudio", 
            CategoriaProducto.LAPTOP, 150000.0, "Dell", "Inspiron 15 3000", 
            "Intel i5, 8GB RAM, 256GB SSD, Windows 11");
        
        Producto laptop2 = new Producto("MacBook Air M2", "Laptop ultraportátil de Apple", 
            CategoriaProducto.LAPTOP, 250000.0, "Apple", "MacBook Air M2", 
            "Chip M2, 8GB RAM, 256GB SSD, macOS");
        
        Producto smartphone1 = new Producto("iPhone 14", "Smartphone premium de Apple", 
            CategoriaProducto.SMARTPHONE, 180000.0, "Apple", "iPhone 14", 
            "A15 Bionic, 128GB, iOS 16, Cámara dual 12MP");
        
        Producto smartphone2 = new Producto("Samsung Galaxy S23", "Smartphone Android premium", 
            CategoriaProducto.SMARTPHONE, 200000.0, "Samsung", "Galaxy S23", 
            "Snapdragon 8 Gen 2, 128GB, Android 13, Cámara 50MP");
        
        Producto mouse1 = new Producto("Logitech MX Master 3", "Mouse inalámbrico profesional", 
            CategoriaProducto.MOUSE, 25000.0, "Logitech", "MX Master 3", 
            "Inalámbrico, 4000 DPI, batería 70 días");
        
        Producto teclado1 = new Producto("Corsair K95 RGB", "Teclado mecánico gaming", 
            CategoriaProducto.TECLADO, 35000.0, "Corsair", "K95 RGB", 
            "Switches Cherry MX, RGB, 6 teclas macro");
        
        // Agregar productos al stock
        stock.agregarProducto(laptop1, 10);
        stock.agregarProducto(laptop2, 5);
        stock.agregarProducto(smartphone1, 15);
        stock.agregarProducto(smartphone2, 12);
        stock.agregarProducto(mouse1, 25);
        stock.agregarProducto(teclado1, 8);
    }

}
