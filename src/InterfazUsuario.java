import productos.CategoriaProducto;
import usuarios.Usuario;
import usuarios.clientes.Cliente;
import usuarios.vendedores.Vendedor;
import excepciones.ProductoNoEncontradoException;
import excepciones.StockInsuficienteException;
import excepciones.SaldoInsuficienteException;
import excepciones.UsuarioNoEncontradoException;

import java.util.Scanner;

/**
 * Clase que maneja la interfaz de usuario del sistema de comercio.
 * Proporciona una interfaz amigable para interactuar con todas las funcionalidades.
 */
public class InterfazUsuario {
    private SistemaComercio sistema;
    private Scanner scanner;
    private boolean sistemaActivo;
    
    // ---------------------- CONSTRUCTOR ----------------------
    public InterfazUsuario() {
        this.sistema = new SistemaComercio();
        this.scanner = new Scanner(System.in);
        this.sistemaActivo = true;
    }
    
    // ---------------------- METODO PRINCIPAL ----------------------
    public void ejecutar() {
        while (sistemaActivo) {
            try {
                mostrarMenuPrincipal();
                int opcion = leerOpcion();
                procesarOpcion(opcion);
            } catch (Exception e) {
                System.out.println("❌ Error: " + e.getMessage());
                System.out.println("Presione Enter para continuar...");
                scanner.nextLine();
            }
        }
        
        System.out.println("👋 ¡Gracias por usar nuestro sistema!");
    }
    
    // ---------------------- METODOS DE MENU  ----------------------
    private void mostrarMenuPrincipal() {
        limpiarPantalla();
        System.out.println("🏪 SISTEMA DE COMERCIO DE TECNOLOGÍA");
        System.out.println("═══════════════════════════════════════");
        
        if (!sistema.estaLogueado()) {
            mostrarMenuNoLogueado();
        } else {
            mostrarMenuLogueado();
        }
    }
    
    private void mostrarMenuNoLogueado() {
        System.out.println("🔐 MENÚ PRINCIPAL");
        System.out.println("═══════════════════════════════════");
        
        if (sistema.hayUsuariosRegistrados()) {
            System.out.println("1. 🔑 Iniciar Sesión");
            System.out.println("2. 📝 Registrarse");
            System.out.println("3. ❌ Salir");
        } else {
            System.out.println("⚠️  No hay usuarios registrados en el sistema.");
            System.out.println("📝 Por favor, regístrese primero.");
            System.out.println("═══════════════════════════════════");
            System.out.println("1. 📝 Registrarse");
            System.out.println("2. ❌ Salir");
        }
        
        System.out.println("═══════════════════════════════════");
    }
    
    private void mostrarMenuLogueado() {
        if (sistema.getUsuarioActual() instanceof Cliente) {
            mostrarMenuCliente();
        } else if (sistema.getUsuarioActual() instanceof Vendedor) {
            mostrarMenuVendedor();
        }
    }
    
    private void mostrarMenuCliente() {
        Cliente cliente = (Cliente) sistema.getUsuarioActual();
        System.out.println("🛍️ MENÚ CLIENTE - " + cliente.getNombre());
        System.out.println("═══════════════════════════════════");
        System.out.println("1. 👤 Ver Mi Información");
        System.out.println("2. 🛍️ Ver Productos Disponibles");
        System.out.println("3. 🛒 Comprar Productos");
        System.out.println("4. 💰 Agregar Saldo");
        System.out.println("5. 📋 Ver Historial de Compras");
        System.out.println("6. 💳 Cambiar Método de Pago");
        System.out.println("7. 🚪 Cerrar Sesión");
        System.out.println("═══════════════════════════════════");
    }
    
    private void mostrarMenuVendedor() {
        Vendedor vendedor = (Vendedor) sistema.getUsuarioActual();
        System.out.println("💼 MENÚ VENDEDOR - " + vendedor.getNombre());
        System.out.println("═══════════════════════════════════");
        System.out.println("1. 👤 Ver Mi Información");
        System.out.println("2. 📦 Ver Stock de Productos");
        System.out.println("3. ➕ Agregar Producto al Stock");
        System.out.println("4. 💰 Vender Productos");
        System.out.println("5. 📊 Ver Mis Ventas");
        System.out.println("6. 👥 Gestionar Usuarios");
        System.out.println("7. 🚪 Cerrar Sesión");
        System.out.println("═══════════════════════════════════");
    }
    
    // ---------------------- MÉTODOS DE PROCESAMIENTO ----------------------
    private void procesarOpcion(int opcion) {
        if (!sistema.estaLogueado()) {
            procesarOpcionNoLogueado(opcion);
        } else {
            procesarOpcionLogueado(opcion);
        }
    }
    
    private void procesarOpcionNoLogueado(int opcion) {
        if (sistema.hayUsuariosRegistrados()) {
            // Menú con opción de iniciar sesión
            switch (opcion) {
                case 1:
                    iniciarSesion();
                    break;
                case 2:
                    registrarse();
                    break;
                case 3:
                    salir();
                    break;
                default:
                    System.out.println("❌ Opción no válida.");
                    pausar();
            }
        } else {
            // Menú sin opción de iniciar sesión
            switch (opcion) {
                case 1:
                    registrarse();
                    break;
                case 2:
                    salir();
                    break;
                default:
                    System.out.println("❌ Opción no válida.");
                    pausar();
            }
        }
    }
    
    private void procesarOpcionLogueado(int opcion) {
        if (sistema.getUsuarioActual() instanceof Cliente) {
            procesarOpcionCliente(opcion);
        } else if (sistema.getUsuarioActual() instanceof Vendedor) {
            procesarOpcionVendedor(opcion);
        }
    }
    
    private void procesarOpcionCliente(int opcion) {
        switch (opcion) {
            case 1:
                mostrarInfoCliente();
                break;
            case 2:
                sistema.mostrarProductosDisponibles();
                pausar();
                break;
            case 3:
                comprarProductos();
                break;
            case 4:
                agregarSaldo();
                break;
            case 5:
                sistema.mostrarHistorialCompras();
                pausar();
                break;
            case 6:
                cambiarMetodoPago();
                break;
            case 7:
                sistema.logout();
                break;
            default:
                System.out.println("❌ Opción no válida.");
                pausar();
        }
    }
    
    private void procesarOpcionVendedor(int opcion) {
        switch (opcion) {
            case 1:
                mostrarInfoVendedor();
                break;
            case 2:
                sistema.mostrarInventario();
                pausar();
                break;
            case 3:
                agregarProductoAlStock();
                break;
            case 4:
                venderProductos();
                break;
            case 5:
                sistema.mostrarVentas();
                pausar();
                break;
            case 6:
                gestionarUsuarios();
                break;
            case 7:
                sistema.logout();
                break;
            default:
                System.out.println("❌ Opción no válida.");
                pausar();
        }
    }
    
    // ---------------------- METODOS DE  AUTENTICACION ----------------------
    private void iniciarSesion() {
        System.out.println("🔑 INICIAR SESIÓN");
        System.out.println("═══════════════════════════════════");
        
        boolean loginExitoso = false;
        
        while (!loginExitoso) {
            try {
                System.out.print("Email: ");
                String email = scanner.nextLine();
                
                System.out.print("Contraseña: ");
                String password = scanner.nextLine();
                
                if (sistema.login(email, password)) {
                    loginExitoso = true;
                }
            } catch (Exception e) {
                System.out.println("❌ Error: " + e.getMessage());
            }
        }
        
        pausar();
    }
    
    private void registrarse() {
        System.out.println("📝 REGISTRARSE");
        System.out.println("═══════════════════════════════════");
        
        boolean registroExitoso = false;
        
        while (!registroExitoso) {
            try {
                String nombre = solicitarDato("Nombre", "texto");
                String apellido = solicitarDato("Apellido", "texto");
                String email = solicitarDato("Email", "email");
                String dni = solicitarDato("DNI (8 dígitos)", "dni");
                int tipoUsuario = solicitarTipoUsuario();
                String password = solicitarPassword();
                Usuario usuario = null;
                
                if (tipoUsuario == 1) {
                    // Crear cliente
                    String direccion = solicitarDato("Dirección", "texto");
                    String telefono = solicitarDato("Teléfono", "texto");
                    
                    usuario = new Cliente(nombre, apellido, email, usuarios.Rol.CLIENTE, 1, dni, 0, usuarios.clientes.MetodoPago.EFECTIVO, 0.0, direccion, telefono);
                } else if (tipoUsuario == 2) {
                    // Crear vendedor
                    double salario = solicitarNumero("Salario base: $", "salario");
                    
                    usuario = new Vendedor(nombre, apellido, email, usuarios.Rol.VENDEDOR, 1, dni, salario);
                }
                
                if (usuario != null) {
                    if (sistema.registrarUsuario(usuario, password)) {
                        registroExitoso = true;
                    }
                }
            } catch (Exception e) {
                System.out.println("❌ Error: " + e.getMessage());
            }
        }
        
        pausar();
    }
    
    // ---------------------- METODOS DE UTILIDAD ----------------------
    private int leerOpcion() {
        System.out.print("Seleccione una opción: ");
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    private void pausar() {
        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }
    
    private void limpiarPantalla() {
        // Limpiar pantalla
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
    
    private void salir() {
        System.out.println("¿Está seguro que desea salir? (s/n)");
        String respuesta = scanner.nextLine().toLowerCase();
        if (respuesta.equals("s") || respuesta.equals("si") || respuesta.equals("sí")) {
            sistemaActivo = false;
        }
    }
    
    // ---------------------- METODOS DE VALIDACION CON REINTENTOS ----------------------
    
    /**
     * Solicita un dato con validación y reintentos
     */
    private String solicitarDato(String etiqueta, String tipo) {
        boolean datoValido = false;
        String dato = "";
        
        while (!datoValido) {
            try {
                System.out.print(etiqueta + ": ");
                dato = scanner.nextLine().trim();
                
                if (dato.isEmpty()) {
                    System.out.println("❌ El campo no puede estar vacío. Intente nuevamente.");
                    continue;
                }
                
                switch (tipo) {
                    case "email":
                        if (!validarEmail(dato)) {
                            System.out.println("❌ Formato de email inválido. Use el formato: usuario@dominio.com");
                            continue;
                        }
                        break;
                    case "dni":
                        if (!validarDNI(dato)) {
                            System.out.println("❌ DNI inválido. Debe contener exactamente 8 dígitos numéricos.");
                            continue;
                        }
                        break;
                    case "texto":
                        if (dato.length() < 2) {
                            System.out.println("❌ El texto debe tener al menos 2 caracteres.");
                            continue;
                        }
                        break;
                }
                
                datoValido = true;
            } catch (Exception e) {
                System.out.println("❌ Error al procesar el dato: " + e.getMessage());
            }
        }
        
        return dato;
    }
    
    /**
     * Solicita una contraseña con validación
     */
    private String solicitarPassword() {
        boolean passwordValida = false;
        String password = "";
        
        while (!passwordValida) {
            System.out.print("Contraseña (mínimo 6 caracteres): ");
            password = scanner.nextLine();
            
            if (password.length() < 6) {
                System.out.println("❌ La contraseña debe tener al menos 6 caracteres. Intente nuevamente.");
                continue;
            }
            
            // Confirmar contraseña
            System.out.print("Confirmar contraseña: ");
            String confirmacion = scanner.nextLine();
            
            if (!password.equals(confirmacion)) {
                System.out.println("❌ Las contraseñas no coinciden. Intente nuevamente.");
                continue;
            }
            
            passwordValida = true;
        }
        
        return password;
    }
    
    /**
     * Solicita un número con validación
     */
    private double solicitarNumero(String etiqueta, String tipo) {
        boolean numeroValido = false;
        double numero = 0;
        
        while (!numeroValido) {
            try {
                System.out.print(etiqueta);
                String entrada = scanner.nextLine().trim();
                numero = Double.parseDouble(entrada);
                
                switch (tipo) {
                    case "salario":
                        if (numero < 0) {
                            System.out.println("❌ El salario no puede ser negativo. Intente nuevamente.");
                            continue;
                        }
                        break;
                    case "comision":
                        if (numero < 0 || numero > 100) {
                            System.out.println("❌ La comisión debe estar entre 0 y 100. Intente nuevamente.");
                            continue;
                        }
                        break;
                    case "meta":
                        if (numero < 0) {
                            System.out.println("❌ La meta de ventas no puede ser negativa. Intente nuevamente.");
                            continue;
                        }
                        break;
                }
                
                numeroValido = true;
            } catch (NumberFormatException e) {
                System.out.println("❌ Debe ingresar un número válido. Intente nuevamente.");
            }
        }
        
        return numero;
    }
    
    /**
     * Solicita el tipo de usuario con validación
     */
    private int solicitarTipoUsuario() {
        boolean tipoValido = false;
        int tipo = 0;
        
        while (!tipoValido) {
            System.out.println("Tipo de usuario:");
            System.out.println("1. Cliente");
            System.out.println("2. Vendedor");
            System.out.print("Seleccione: ");
            
            try {
                String entrada = scanner.nextLine().trim();
                tipo = Integer.parseInt(entrada);
                
                if (tipo != 1 && tipo != 2) {
                    System.out.println("❌ Opción inválida. Seleccione 1 o 2.");
                    continue;
                }
                
                tipoValido = true;
            } catch (NumberFormatException e) {
                System.out.println("❌ Debe ingresar un número válido (1 o 2). Intente nuevamente.");
            }
        }
        
        return tipo;
    }
    
    /**
     * Valida formato de email
     */
    private boolean validarEmail(String email) {
        String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        return email.matches(regex);
    }
    
    /**
     * Valida formato de DNI
     */
    private boolean validarDNI(String dni) {
        return dni.matches("^\\d{8}$");
    }
    
    // ---------------------- METODOS ----------------------
    
    /**
     * Muestra información del cliente
     */
    private void mostrarInfoCliente() {
        Cliente cliente = (Cliente) sistema.getUsuarioActual();
        System.out.println(cliente.toString());
        pausar();
    }
    
    /**
     * Muestra información del vendedor
     */
    private void mostrarInfoVendedor() {
        Vendedor vendedor = (Vendedor) sistema.getUsuarioActual();
        System.out.println(vendedor.toString());
        pausar();
    }
    
    /**
     * Proceso de compra para clientes
     */
    private void comprarProductos() {
        System.out.println("🛒 COMPRAR PRODUCTOS");
        System.out.println("═══════════════════════════════════");
        
        // Mostrar saldo actual
        Cliente cliente = (Cliente) sistema.getUsuarioActual();
        System.out.println("💰 Su saldo actual: $" + String.format("%.2f", cliente.getSaldo()));
        System.out.println("═══════════════════════════════════");
        
        // Mostrar productos disponibles
        sistema.mostrarProductosDisponibles();
        
        // Verificar si hay productos disponibles antes de permitir comprar
        if (!sistema.hayProductosDisponibles()) {
            System.out.println("\n❌ No hay productos disponibles en el stock.");
            System.out.println("No se puede realizar una compra sin productos disponibles.");
            pausar();
            return;
        }
        
        System.out.println("\n🛒 Para comprar un producto:");
        System.out.println("1. Ingrese el nombre del producto");
        System.out.println("2. Ingrese la cantidad deseada");
        System.out.println("3. Confirme la compra");
        
        try {
            System.out.print("\nNombre del producto (Enter para cancelar): ");
            String nombreProducto = scanner.nextLine().trim();
            
            if (nombreProducto.isEmpty()) {
                System.out.println("❌ Compra cancelada.");
                return;
            }
            
            System.out.print("Cantidad: ");
            int cantidad = Integer.parseInt(scanner.nextLine());
            
            if (cantidad <= 0) {
                System.out.println("❌ La cantidad debe ser mayor a 0.");
                pausar();
                return;
            }
            
            // Confirmar compra
            System.out.println("\n¿Confirma la compra? (s/n)");
            String confirmacion = scanner.nextLine().toLowerCase();
            
            if (confirmacion.equals("s") || confirmacion.equals("si") || confirmacion.equals("sí")) {
                try {
                    boolean exito = sistema.comprarProductoPorNombre(nombreProducto, cantidad);
                    if (exito) {
                        System.out.println("✅ Compra realizada exitosamente.");
                    }
                } catch (ProductoNoEncontradoException e) {
                    System.out.println("❌ Error: " + e.getMessage());
                } catch (StockInsuficienteException e) {
                    System.out.println("❌ Error: " + e.getMessage());
                } catch (SaldoInsuficienteException e) {
                    System.out.println("❌ Error: " + e.getMessage());
                } catch (IllegalStateException e) {
                    System.out.println("❌ Error: " + e.getMessage());
                }
            } else {
                System.out.println("❌ Compra cancelada.");
            }
            
        } catch (NumberFormatException e) {
            System.out.println("❌ Debe ingresar un número válido para la cantidad.");
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
        
        pausar();
    }
    
    /**
     * Permite al cliente agregar saldo a su cuenta
     */
    private void agregarSaldo() {
        System.out.println("💰 AGREGAR SALDO");
        System.out.println("═══════════════════════════════════");
        
        Cliente cliente = (Cliente) sistema.getUsuarioActual();
        System.out.println("💰 Saldo actual: $" + String.format("%.2f", cliente.getSaldo()));
        
        try {
            System.out.print("Monto a agregar: $");
            double monto = Double.parseDouble(scanner.nextLine());
            
            if (monto <= 0) {
                System.out.println("❌ El monto debe ser mayor a 0.");
                pausar();
                return;
            }
            
            boolean exito = sistema.agregarSaldo(monto);
            if (!exito) {
                System.out.println("❌ No se pudo agregar el saldo.");
            }
            
        } catch (NumberFormatException e) {
            System.out.println("❌ Debe ingresar un número válido.");
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
        
        pausar();
    }
    
    /**
     * Permite al cliente cambiar su método de pago por defecto
     */
    private void cambiarMetodoPago() {
        System.out.println("💳 CAMBIAR MÉTODO DE PAGO");
        System.out.println("═══════════════════════════════════");
        
        usuarios.clientes.Cliente cliente = (usuarios.clientes.Cliente) sistema.getUsuarioActual();
        System.out.println("Método actual: " + cliente.getMetodoPago());
        System.out.println("═══════════════════════════════════");
        System.out.println("1. 📱 Pago QR (5% descuento)");
        System.out.println("2. 💳 Tarjeta de Débito (3% descuento)");
        System.out.println("3. 📲 Billetera Virtual (4% descuento)");
        System.out.println("4. 💳 Tarjeta de Crédito (Sin descuento)");
        System.out.println("5. 💵 Efectivo (Sin descuento)");
        System.out.println("0. ❌ Cancelar");
        System.out.println("═══════════════════════════════════");
        
        try {
            System.out.print("Seleccione una opción: ");
            int opcion = Integer.parseInt(scanner.nextLine());
            
            switch (opcion) {
                case 1:
                    cliente.cambiarMetodoPagoPorDefecto(usuarios.clientes.MetodoPago.QR);
                    break;
                case 2:
                    cliente.cambiarMetodoPagoPorDefecto(usuarios.clientes.MetodoPago.DEBITO);
                    break;
                case 3:
                    cliente.cambiarMetodoPagoPorDefecto(usuarios.clientes.MetodoPago.BILLETERA_VIRTUAL);
                    break;
                case 4:
                    cliente.cambiarMetodoPagoPorDefecto(usuarios.clientes.MetodoPago.TARJETA_CREDITO);
                    break;
                case 5:
                    cliente.cambiarMetodoPagoPorDefecto(usuarios.clientes.MetodoPago.EFECTIVO);
                    break;
                case 0:
                    System.out.println("❌ Operación cancelada.");
                    break;
                default:
                    System.out.println("❌ Opción no válida.");
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Debe ingresar un número válido.");
            e.printStackTrace();
        }
        
        pausar();
    }
    
    /**
     * Proceso de venta para vendedores
     */
    private void venderProductos() {
        System.out.println("💰 VENDER PRODUCTOS");
        System.out.println("═══════════════════════════════════");
        
        // Mostrar productos disponibles
        sistema.mostrarProductosDisponibles();
        
        // Verificar si hay productos disponibles antes de permitir procesar una venta
        if (!sistema.hayProductosDisponibles()) {
            System.out.println("\n❌ No hay productos disponibles en el stock.");
            System.out.println("No se puede procesar una venta sin productos disponibles.");
            pausar();
            return;
        }
        
        System.out.println("\n📝 Para procesar una venta:");
        System.out.println("1. Ingrese el nombre del producto");
        System.out.println("2. Ingrese la cantidad");
        System.out.println("3. Confirme la venta");
        
        System.out.print("\n¿Desea procesar una venta? (s/n): ");
        String respuesta = scanner.nextLine().toLowerCase();
        
        if (respuesta.equals("s") || respuesta.equals("si")) {
            procesarVenta();
        }
    }
    
    /**
     * Proceso de venta
     */
    private void procesarVenta() {
        try {
            System.out.print("Nombre del producto (Enter para cancelar): ");
            String nombreProducto = scanner.nextLine().trim();
            
            if (nombreProducto.isEmpty()) {
                System.out.println("❌ Venta cancelada.");
                return;
            }
            
            System.out.print("Cantidad: ");
            int cantidad = Integer.parseInt(scanner.nextLine());
            
            if (cantidad <= 0) {
                System.out.println("❌ La cantidad debe ser mayor a 0.");
                pausar();
                return;
            }
            
            // Crear venta simple
            var venta = sistema.crearVenta();
            if (venta != null) {
                boolean exito = sistema.agregarProductoAVentaPorNombre(venta, nombreProducto, cantidad);
                if (exito) {
                    boolean procesada = sistema.procesarVenta(venta);
                    if (procesada) {
                        System.out.println("✅ Venta procesada exitosamente!");
                    } else {
                        System.out.println("❌ Error al procesar la venta.");
                    }
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Debe ingresar un número válido para la cantidad.");
        }
        
        pausar();
    }
    
    private void agregarProductoAlStock() {
        System.out.println("➕ AGREGAR PRODUCTO AL STOCK");
        System.out.println("═══════════════════════════════════");
        
        try {
            // Solicitar datos del producto
            String nombre = solicitarDato("Nombre del producto", "texto");
            String descripcion = solicitarDato("Descripción", "texto");
            
            // Mostrar categorías disponibles
            System.out.println("\n📋 CATEGORÍAS DISPONIBLES:");
            System.out.println("1. LAPTOP");
            System.out.println("2. SMARTPHONE");
            System.out.println("3. TABLET");
            System.out.println("4. AURICULARES");
            System.out.println("5. SMARTWATCH");
            System.out.println("6. MOUSE");
            System.out.println("7. TECLADO");
            System.out.println("8. MONITOR");
            
            int categoriaOpcion = (int) solicitarNumero("Seleccione categoría (1-8): ", "categoria");
            productos.CategoriaProducto categoria = null;
            
            switch (categoriaOpcion) {
                case 1: categoria = productos.CategoriaProducto.LAPTOP; break;
                case 2: categoria = productos.CategoriaProducto.SMARTPHONE; break;
                case 3: categoria = productos.CategoriaProducto.TABLET; break;
                case 4: categoria = CategoriaProducto.AUDIFONOS; break;
                case 5: categoria = CategoriaProducto.SOFTWARE; break;
                case 6: categoria = productos.CategoriaProducto.MOUSE; break;
                case 7: categoria = productos.CategoriaProducto.TECLADO; break;
                case 8: categoria = productos.CategoriaProducto.MONITOR; break;
                default:
                    System.out.println("❌ Categoría no válida.");
                    return;
            }
            
            double precio = solicitarNumero("Precio: $", "precio");
            String marca = solicitarDato("Marca", "texto");
            String modelo = solicitarDato("Modelo", "texto");
            String especificaciones = solicitarDato("Especificaciones técnicas", "texto");
            int cantidad = (int) solicitarNumero("Cantidad a agregar: ", "cantidad");
            
            // Validar datos
            if (precio <= 0) {
                System.out.println("❌ El precio debe ser mayor a 0.");
                return;
            }
            
            if (cantidad <= 0) {
                System.out.println("❌ La cantidad debe ser mayor a 0.");
                return;
            }
            
            // Agregar producto al stock
            boolean exito = sistema.agregarProductoAlStock(nombre, descripcion, categoria, precio, marca, modelo, especificaciones, cantidad);
            
            if (!exito) {
                System.out.println("\n❌ Error al agregar el producto.");
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
        
        pausar();
    }
    
    // ---------------------- METODOS DE GESTION DE USUARIOS (VENDEDOR) ----------------------
    
    /**
     * Menú principal para gestionar usuarios
     */
    private void gestionarUsuarios() {
        limpiarPantalla();
        System.out.println("👥 GESTIÓN DE USUARIOS");
        System.out.println("═══════════════════════════════════");
        System.out.println("1. 📋 Listar Todos los Usuarios");
        System.out.println("2. 🔍 Buscar Usuario por Email");
        System.out.println("3. ❌ Dar de Baja Usuario");
        System.out.println("4. ✅ Reactivar Usuario");
        System.out.println("5. ✏️ Modificar Usuario");
        System.out.println("0. 🔙 Volver al Menú Principal");
        System.out.println("═══════════════════════════════════");
        
        try {
            System.out.print("Seleccione una opción: ");
            int opcion = Integer.parseInt(scanner.nextLine());
            
            switch (opcion) {
                case 1:
                    listarUsuarios();
                    break;
                case 2:
                    buscarUsuario();
                    break;
                case 3:
                    darBajaUsuario();
                    break;
                case 4:
                    reactivarUsuario();
                    break;
                case 5:
                    modificarUsuario();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("❌ Opción no válida.");
                    pausar();
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Debe ingresar un número válido.");
            pausar();
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
            pausar();
        }
    }
    
    /**
     * Lista todos los usuarios del sistema
     */
    private void listarUsuarios() {
        limpiarPantalla();
        sistema.listarTodosLosUsuarios();
        pausar();
    }
    
    /**
     * Busca un usuario por email y muestra su información
     */
    private void buscarUsuario() {
        limpiarPantalla();
        System.out.println("🔍 BUSCAR USUARIO");
        System.out.println("═══════════════════════════════════");
        
        try {
            System.out.print("Ingrese el email del usuario: ");
            String email = scanner.nextLine().trim();
            
            if (email.isEmpty()) {
                System.out.println("❌ El email no puede estar vacío.");
                pausar();
                return;
            }
            
            Usuario usuario = sistema.buscarUsuarioPorEmail(email);
            System.out.println("\n" + usuario.toString());
            
        } catch (UsuarioNoEncontradoException e) {
            System.out.println("❌ Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
        
        pausar();
    }
    
    /**
     * Da de baja lógica a un usuario
     */
    private void darBajaUsuario() {
        limpiarPantalla();
        System.out.println("❌ DAR DE BAJA USUARIO");
        System.out.println("═══════════════════════════════════");
        
        try {
            sistema.listarTodosLosUsuarios();
            
            System.out.print("\nIngrese el email del usuario a dar de baja: ");
            String email = scanner.nextLine().trim();
            
            if (email.isEmpty()) {
                System.out.println("❌ El email no puede estar vacío.");
                pausar();
                return;
            }

            System.out.print("¿Está seguro de dar de baja a este usuario? (s/n): ");
            String confirmacion = scanner.nextLine().toLowerCase();
            
            if (confirmacion.equals("s") || confirmacion.equals("si") || confirmacion.equals("sí")) {
                sistema.darBajaUsuario(email);
            } else {
                System.out.println("❌ Operación cancelada.");
            }
            
        } catch (UsuarioNoEncontradoException e) {
            System.out.println("❌ Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
        
        pausar();
    }
    
    /**
     * Reactiva un usuario
     */
    private void reactivarUsuario() {
        limpiarPantalla();
        System.out.println("✅ REACTIVAR USUARIO");
        System.out.println("═══════════════════════════════════");
        
        try {
            sistema.listarTodosLosUsuarios();
            
            System.out.print("\nIngrese el email del usuario a reactivar: ");
            String email = scanner.nextLine().trim();
            
            if (email.isEmpty()) {
                System.out.println("❌ El email no puede estar vacío.");
                pausar();
                return;
            }
            
            sistema.reactivarUsuario(email);
        } catch (UsuarioNoEncontradoException e) {
            System.out.println("❌ Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
        
        pausar();
    }
    
    /**
     * Modifica los datos de un usuario
     */
    private void modificarUsuario() {
        limpiarPantalla();
        System.out.println("✏️ MODIFICAR USUARIO");
        System.out.println("═══════════════════════════════════");
        
        try {
            sistema.listarTodosLosUsuarios();
            
            System.out.print("\nIngrese el email del usuario a modificar: ");
            String email = scanner.nextLine().trim();
            
            if (email.isEmpty()) {
                System.out.println("❌ El email no puede estar vacío.");
                pausar();
                return;
            }
            
            // Buscar usuario para verificar su tipo
            Usuario usuario = sistema.buscarUsuarioPorEmail(email);
            
            System.out.println("\n📝 Datos actuales del usuario:");
            System.out.println(usuario.toString());
            System.out.println("\n═══════════════════════════════════");
            System.out.println("Ingrese los nuevos datos (presione Enter para mantener el valor actual):");
            
            // Modificar datos básicos
            System.out.print("Nuevo nombre [" + usuario.getNombre() + "]: ");
            String nuevoNombre = scanner.nextLine().trim();
            if (nuevoNombre.isEmpty()) nuevoNombre = null;
            
            System.out.print("Nuevo apellido [" + usuario.getApellido() + "]: ");
            String nuevoApellido = scanner.nextLine().trim();
            if (nuevoApellido.isEmpty()) nuevoApellido = null;
            
            System.out.print("Nuevo DNI [" + usuario.getDni() + "]: ");
            String nuevoDni = scanner.nextLine().trim();
            if (nuevoDni.isEmpty()) nuevoDni = null;
            
            // Modificar datos básicos
            sistema.modificarUsuario(email, nuevoNombre, nuevoApellido, nuevoDni);
            
            // Modificar datos específicos según el tipo de usuario
            if (usuario instanceof Cliente) {
                Cliente cliente = (Cliente) usuario;
                System.out.print("Nueva dirección [" + (cliente.getDireccion() != null ? cliente.getDireccion() : "N/A") + "]: ");
                String nuevaDireccion = scanner.nextLine().trim();
                if (nuevaDireccion.isEmpty()) nuevaDireccion = null;
                
                System.out.print("Nuevo teléfono [" + (cliente.getTelefono() != null ? cliente.getTelefono() : "N/A") + "]: ");
                String nuevoTelefono = scanner.nextLine().trim();
                if (nuevoTelefono.isEmpty()) nuevoTelefono = null;
                
                sistema.modificarCliente(email, nuevaDireccion, nuevoTelefono);
                
            } else if (usuario instanceof Vendedor) {
                Vendedor vendedor = (Vendedor) usuario;
                System.out.print("Nuevo salario [" + String.format("%.2f", vendedor.getSalario()) + "]: ");
                String salarioStr = scanner.nextLine().trim();
                Double nuevoSalario = null;
                if (!salarioStr.isEmpty()) {
                    try {
                        nuevoSalario = Double.parseDouble(salarioStr);
                    } catch (NumberFormatException e) {
                        System.out.println("⚠️ Salario inválido, se mantendrá el valor actual.");
                    }
                }
                
                sistema.modificarVendedor(email, nuevoSalario);
            }
            
            // Guardar todos los cambios al final
            try {
                sistema.guardarUsuarios();
            } catch (excepciones.ErrorPersistenciaException e) {
                System.out.println("❌ Error al guardar usuarios: " + e.getMessage());
                if (e.getCause() != null) {
                    System.out.println("   Causa: " + e.getCause().getMessage());
                }
                return;
            }
            
            // Obtener el usuario actualizado para mostrar el nombre en el mensaje final
            Usuario usuarioActualizado = sistema.buscarUsuarioPorEmail(email);
            System.out.println("\n✅ Usuario modificado exitosamente: " + usuarioActualizado.getNombre() + " " + usuarioActualizado.getApellido());
            
        } catch (UsuarioNoEncontradoException e) {
            System.out.println("❌ Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
        
        pausar();
    }
}
