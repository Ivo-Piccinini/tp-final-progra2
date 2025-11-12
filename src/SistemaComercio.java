import usuarios.*;
import usuarios.clientes.Cliente;
import usuarios.clientes.MetodoPago;
import usuarios.vendedores.Vendedor;
import productos.*;
import inventario.Stock;
import ventas.Venta;
import ventas.DetalleVenta;
import descuentos.DescuentoMetodoPago;
import persistencia.StockJSON;
import excepciones.ProductoNoEncontradoException;
import excepciones.StockInsuficienteException;
import excepciones.SaldoInsuficienteException;
import excepciones.UsuarioYaExisteException;
import excepciones.PasswordInvalidaException;
import excepciones.CredencialesInvalidasException;
import excepciones.ErrorPersistenciaException;
import excepciones.UsuarioNoEncontradoException;
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
    private StockJSON stockJSON;
    private static final String ARCHIVO_STOCK = "data/stock.json";
    
    // ---------------------- CONSTRUCTOR ----------------------
    public SistemaComercio() {
        this.sistemaAutenticacion = new SistemaAutenticacion();
        this.stockJSON = new StockJSON();
        this.ventas = new ArrayList<>();
        this.fechaInicioSistema = LocalDateTime.now();
        
        // Cargar stock desde archivo JSON
        cargarStockDesdeArchivo();
        
        System.out.println("🚀 SISTEMA DE COMERCIO DE TECNOLOGÍA INICIADO");
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("📅 Fecha de inicio: " + fechaInicioSistema);
        System.out.println("═══════════════════════════════════════════════\n");
    }
    
    // ---------------------- METODOS DE AUTENTICACION ----------------------
    public boolean login(String email, String password) {
        try {
            return sistemaAutenticacion.login(email, password);
        } catch (CredencialesInvalidasException e) {
            System.out.println("❌ Error: " + e.getMessage());
            return false;
        }
    }
    
    public void logout() {
        sistemaAutenticacion.logout();
    }
    
    public boolean registrarUsuario(Usuario usuario, String password) {
        try {
            return sistemaAutenticacion.registrarUsuario(usuario, password);
        } catch (UsuarioYaExisteException e) {
            System.out.println("❌ Error: " + e.getMessage());
            return false;
        } catch (PasswordInvalidaException e) {
            System.out.println("❌ Error: " + e.getMessage());
            return false;
        }
    }
    
    public Usuario getUsuarioActual() {
        return sistemaAutenticacion.getUsuarioActual();
    }
    
    public boolean estaLogueado() {
        return sistemaAutenticacion.estaLogueado();
    }
    
    public boolean hayUsuariosRegistrados() {
        return sistemaAutenticacion.hayUsuariosRegistrados();
    }

    // ---------------------- METODOS DE PERSISTENCIA ----------------------
    
    /**
     * Carga el stock desde el archivo JSON
     */
    private void cargarStockDesdeArchivo() {
        try {
            this.stock = stockJSON.cargarStock(ARCHIVO_STOCK);
        } catch (Exception e){
            this.stock = new Stock();
        }
    }
    
    /**
     * Guarda el stock actual en el archivo JSON
     */
    public boolean guardarStockEnArchivo() {
        try {
            stock.actualizarFecha();
            stockJSON.guardarStock(stock, ARCHIVO_STOCK);
            return true;
        } catch (Exception e) {
            System.out.println("❌ Error al guardar stock: " + e.getMessage());
            return false;
        }
    }
    
    // ---------------------- METODOS ----------------------
    
    public void mostrarInventario() {
        stock.mostrarInventario();
    }
    
    public void mostrarProductosDisponibles() {
        stock.mostrarProductosDisponibles();
    }
    
    /**
     * Verifica si hay productos disponibles en el stock
     */
    public boolean hayProductosDisponibles() {
        return stock.getCantidadProductosDisponibles() > 0;
    }
    
    // ---------------------- METODOS DE COMPRA PARA CLIENTES ----------------------
    
    /**
     * Permite a un cliente comprar productos directamente por nombre
     */
    public boolean comprarProductoPorNombre(String nombreProducto, int cantidad) throws ProductoNoEncontradoException, StockInsuficienteException, SaldoInsuficienteException {
        if (!estaLogueado()) {
            throw new IllegalStateException("Debe estar logueado para realizar una compra.");
        }
        
        Usuario usuario = getUsuarioActual();
        if (!(usuario instanceof Cliente)) {
            throw new IllegalStateException("Solo los clientes pueden realizar compras directas.");
        }
        
        Cliente cliente = (Cliente) usuario;
        Producto producto = stock.buscarProductoPorNombre(nombreProducto);
        
        if (producto == null) {
            throw new ProductoNoEncontradoException("Producto no encontrado: " + nombreProducto, -1);
        }
        
        int productoId = producto.getId();
        
        if (!producto.isActivo()) {
            throw new IllegalStateException("El producto no está disponible.");
        }
        
        if (!stock.hayStock(productoId, cantidad)) {
            int stockDisponible = stock.obtenerCantidad(productoId);
            throw new StockInsuficienteException(
                "No hay suficiente stock disponible. Disponible: " + stockDisponible + ", Requerido: " + cantidad,
                stockDisponible,
                cantidad
            );
        }
        
        double subtotal = producto.getPrecio() * cantidad;
        
        // Mostrar descuentos disponibles
        DescuentoMetodoPago.mostrarDescuentosDisponibles();
        
        // Seleccionar método de pago
        MetodoPago metodoPagoSeleccionado = seleccionarMetodoPago(cliente);
        if (metodoPagoSeleccionado == null) {
            throw new IllegalStateException("Compra cancelada por el usuario.");
        }
        
        // Calcular descuento y total final
        double descuento = DescuentoMetodoPago.calcularDescuento(subtotal, metodoPagoSeleccionado);
        double totalCompra = DescuentoMetodoPago.calcularMontoFinal(subtotal, metodoPagoSeleccionado);
        
        // Mostrar resumen de la compra
        System.out.println("\n🧾 RESUMEN DE COMPRA");
        System.out.println("═══════════════════════════════════");
        System.out.println("📱 Producto: " + producto.getNombre());
        System.out.println("📦 Cantidad: " + cantidad);
        System.out.println("💰 Subtotal: $" + String.format("%.2f", subtotal));
        if (descuento > 0) {
            System.out.println("🎯 Descuento (" + metodoPagoSeleccionado + "): -$" + String.format("%.2f", descuento));
        }
        System.out.println("💵 Total a pagar: $" + String.format("%.2f", totalCompra));
        System.out.println("💳 Método de pago: " + metodoPagoSeleccionado);
        System.out.println("═══════════════════════════════════");
        
        if (cliente.getSaldo() < totalCompra) {
            throw new SaldoInsuficienteException(
                "Saldo insuficiente. Saldo actual: $" + String.format("%.2f", cliente.getSaldo()) + 
                ", Total de la compra: $" + String.format("%.2f", totalCompra),
                cliente.getSaldo(),
                totalCompra
            );
        }
        
        // Procesar la compra
        try {
            stock.eliminarProducto(productoId, cantidad);
        } catch (ProductoNoEncontradoException | StockInsuficienteException e) {
            // Estas excepciones ya fueron validadas antes, pero por si acaso
            throw e;
        }
        
        // Actualizar saldo del cliente
        cliente.setSaldo(cliente.getSaldo() - totalCompra);
        
        // Registrar la compra con descuento
        String descripcionCompra = producto.getNombre() + " x" + cantidad + " = $" + String.format("%.2f", totalCompra);
        if (descuento > 0) {
            descripcionCompra += " (Descuento: $" + String.format("%.2f", descuento) + ")";
        }
        cliente.agregarCompra(descripcionCompra);
        
        // Guardar cambios en archivo JSON
        guardarStockEnArchivo();
        try {
            sistemaAutenticacion.guardarUsuarios();
        } catch (ErrorPersistenciaException e) {
            System.out.println("⚠️ Advertencia: " + e.getMessage());
        }
        
        System.out.println("✅ ¡Compra realizada exitosamente!");
        System.out.println("📱 Producto: " + producto.getNombre());
        System.out.println("📦 Cantidad: " + cantidad);
        System.out.println("💵 Total pagado: $" + String.format("%.2f", totalCompra));
        if (descuento > 0) {
            System.out.println("🎯 Descuento aplicado: $" + String.format("%.2f", descuento));
        }
        System.out.println("💰 Saldo restante: $" + String.format("%.2f", cliente.getSaldo()));
        
        return true;
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
        
        // Guardar cambios en archivo JSON
        try {
            sistemaAutenticacion.guardarUsuarios();
        } catch (ErrorPersistenciaException e) {
            System.out.println("⚠️ Advertencia: " + e.getMessage());
        }
        
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
    
    /**
     * Permite al cliente seleccionar el método de pago
     */
    private MetodoPago seleccionarMetodoPago(Cliente cliente) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("\n💳 SELECCIONAR MÉTODO DE PAGO");
        System.out.println("═══════════════════════════════════");
        System.out.println("Método por defecto: " + cliente.getMetodoPago());
        System.out.println("═══════════════════════════════════");
        System.out.println("1. 📱 Pago QR (5% descuento)");
        System.out.println("2. 💳 Tarjeta de Débito (3% descuento)");
        System.out.println("3. 📲 Billetera Virtual (4% descuento)");
        System.out.println("4. 💳 Tarjeta de Crédito (Sin descuento)");
        System.out.println("5. 💵 Efectivo (Sin descuento)");
        System.out.println("6. 🔄 Usar método por defecto");
        System.out.println("0. ❌ Cancelar compra");
        System.out.println("═══════════════════════════════════");
        
        while (true) {
            try {
                System.out.print("Seleccione una opción: ");
                int opcion = Integer.parseInt(scanner.nextLine());
                
                switch (opcion) {
                    case 1:
                        cliente.cambiarMetodoPagoPorDefecto(MetodoPago.QR);
                        return MetodoPago.QR;
                    case 2:
                        cliente.cambiarMetodoPagoPorDefecto(MetodoPago.DEBITO);
                        return MetodoPago.DEBITO;
                    case 3:
                        cliente.cambiarMetodoPagoPorDefecto(MetodoPago.BILLETERA_VIRTUAL);
                        return MetodoPago.BILLETERA_VIRTUAL;
                    case 4:
                        cliente.cambiarMetodoPagoPorDefecto(MetodoPago.TARJETA_CREDITO);
                        return MetodoPago.TARJETA_CREDITO;
                    case 5:
                        cliente.cambiarMetodoPagoPorDefecto(MetodoPago.EFECTIVO);
                        return MetodoPago.EFECTIVO;
                    case 6:
                        System.out.println("✅ Usando método por defecto: " + cliente.getMetodoPago());
                        return cliente.getMetodoPago();
                    case 0:
                        return null;
                    default:
                        System.out.println("❌ Opción no válida. Intente nuevamente.");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Debe ingresar un número válido.");
            }
        }
    }
    
    // ---------------------- METODOS DE GESTION DE VENTAS ----------------------
    public Venta crearVenta() {
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
        
        // Seleccionar método de pago
        MetodoPago metodoPago = seleccionarMetodoPago(clienteSeleccionado);
        if (metodoPago == null) {
            System.out.println("❌ Error: No se pudo seleccionar un método de pago.");
            return null;
        }
        
        Venta venta = new Venta(clienteSeleccionado, vendedor, metodoPago);
        ventas.add(venta);
        System.out.println("✅ Venta creada para cliente: " + clienteSeleccionado.getNombre() + " " + clienteSeleccionado.getApellido());
        System.out.println("💳 Método de pago: " + metodoPago);
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
            System.out.println((i + 1) + ". " + cliente.getNombre() + " " + cliente.getApellido() + " (" + cliente.getEmail() + ")");
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
                } else if (opcion >= 1 && opcion <= clientes.size()) {
                    Cliente clienteSeleccionado = clientes.get(opcion - 1);
                    System.out.println("✅ Cliente seleccionado: " + clienteSeleccionado.getNombre() + " " + clienteSeleccionado.getApellido());
                    return clienteSeleccionado;
                } else {
                    System.out.println("❌ Opción no válida. Intente nuevamente.");
                }
            } catch (InputMismatchException e) {
                System.out.println("❌ Debe ingresar un número válido.");
                scanner.nextLine();
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
        
        try {
            boolean resultado = venta.agregarProducto(producto, cantidad, stock);
            if (resultado) {
                System.out.println("✅ Producto agregado a la venta: " + producto.getNombre() + " x" + cantidad);
            }
            return resultado;
        } catch (StockInsuficienteException e) {
            System.out.println("❌ Error: " + e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Error: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Permite a un vendedor agregar un nuevo producto al stock
     */
    public boolean agregarProductoAlStock(String nombre, String descripcion, CategoriaProducto categoria, double precio, String marca, String modelo, String especificaciones, int cantidad) {
        if (!(getUsuarioActual() instanceof Vendedor)) {
            System.out.println("❌ Solo los vendedores pueden agregar productos al stock.");
            return false;
        }
        
        try {
            // Crear el nuevo producto
            Producto nuevoProducto = new Producto(nombre, descripcion, categoria, precio, marca, modelo, especificaciones);
            
            // Agregar al stock
            stock.agregarProducto(nuevoProducto, cantidad);
            
            // Guardar en archivo JSON
            guardarStockEnArchivo();
            
            System.out.println("✅ Producto agregado exitosamente al stock:");
            System.out.println("📱 " + nombre + " | Cantidad: " + cantidad + " | Precio: $" + String.format("%.2f", precio));
            
            return true;
        } catch (Exception e) {
            System.out.println("❌ Error al agregar producto: " + e.getMessage());
            return false;
        }
    }
    
    public boolean procesarVenta(Venta venta) {
        if (venta == null) {
            System.out.println("❌ Error: La venta no puede ser null.");
            return false;
        }
        
        try {
            boolean resultado = venta.procesarVenta(stock);
            if (resultado) {
                // Guardar cambios en archivo JSON
                guardarStockEnArchivo();
                try {
                    sistemaAutenticacion.guardarUsuarios();
                } catch (ErrorPersistenciaException e) {
                    System.out.println("⚠️ Advertencia: " + e.getMessage());
                }
                System.out.println("✅ Venta procesada exitosamente.");
                venta.mostrarDetallesVenta();
            }
            return resultado;
        } catch (StockInsuficienteException e) {
            System.out.println("❌ Error: " + e.getMessage());
            return false;
        } catch (SaldoInsuficienteException e) {
            System.out.println("❌ Error: " + e.getMessage());
            return false;
        } catch (ProductoNoEncontradoException e) {
            System.out.println("❌ Error: " + e.getMessage());
            return false;
        }
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
    
    // ---------------------- METODOS DE GESTION DE USUARIOS (VENDEDOR) ----------------------
    
    /**
     * Lista todos los usuarios del sistema
     */
    public void listarTodosLosUsuarios() {
        List<Usuario> usuarios = sistemaAutenticacion.listarUsuarios();
        
        System.out.println("👥 LISTA DE USUARIOS");
        System.out.println("═══════════════════════════════════");
        
        if (usuarios.isEmpty()) {
            System.out.println("📭 No hay usuarios registrados.");
        } else {
            for (Usuario usuario : usuarios) {
                System.out.println("ID: " + usuario.getId() + " | " + 
                                 usuario.getNombre() + " " + usuario.getApellido() + 
                                 " | Email: " + usuario.getEmail() + 
                                 " | Rol: " + usuario.getRol() + 
                                 " | Estado: " + (usuario.getEstado() == 1 ? "Activo" : "Inactivo"));
            }
        }
        System.out.println("═══════════════════════════════════");
    }
    
    /**
     * Busca un usuario por email
     */
    public Usuario buscarUsuarioPorEmail(String email) throws UsuarioNoEncontradoException {
        return sistemaAutenticacion.buscarUsuarioPorEmail(email);
    }
    
    /**
     * Da de baja lógica a un usuario (estado = 0)
     */
    public boolean darBajaUsuario(String email) throws UsuarioNoEncontradoException {
        try {
            return sistemaAutenticacion.darBajaUsuario(email);
        } catch (IllegalStateException e) {
            System.out.println("❌ Error: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Reactiva un usuario (estado = 1)
     */
    public boolean reactivarUsuario(String email) throws UsuarioNoEncontradoException {
        return sistemaAutenticacion.reactivarUsuario(email);
    }
    
    /**
     * Modifica los datos básicos de un usuario
     */
    public boolean modificarUsuario(String email, String nuevoNombre, String nuevoApellido, String nuevoDni) 
            throws UsuarioNoEncontradoException {
        return sistemaAutenticacion.modificarUsuario(email, nuevoNombre, nuevoApellido, nuevoDni);
    }
    
    /**
     * Modifica datos específicos de un Cliente
     */
    public boolean modificarCliente(String email, String nuevaDireccion, String nuevoTelefono) 
            throws UsuarioNoEncontradoException {
        try {
            return sistemaAutenticacion.modificarCliente(email, nuevaDireccion, nuevoTelefono);
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Error: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Modifica datos específicos de un Vendedor
     */
    public boolean modificarVendedor(String email, Double nuevoSalario) 
            throws UsuarioNoEncontradoException {
        try {
            return sistemaAutenticacion.modificarVendedor(email, nuevoSalario);
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Error: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Guarda los usuarios en el archivo JSON
     */
    public void guardarUsuarios() throws excepciones.ErrorPersistenciaException {
        sistemaAutenticacion.guardarUsuarios();
    }
}
