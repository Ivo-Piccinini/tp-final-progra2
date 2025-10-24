import productos.CategoriaProducto;
import usuarios.clientes.Cliente;
import usuarios.vendedores.Vendedor;

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
        sistema.inicializarSistema();
        
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
        
        System.out.println("👋 ¡Gracias por usar el Sistema de Comercio de Tecnología!");
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
        System.out.println("1. 🔑 Iniciar Sesión");
        System.out.println("2. 📝 Registrarse");
        System.out.println("3. ❌ Salir");
        System.out.println("═══════════════════════════════════");
    }
    
    private void mostrarMenuLogueado() {
        if (sistema.getUsuarioActual() instanceof usuarios.clientes.Cliente) {
            mostrarMenuCliente();
        } else if (sistema.getUsuarioActual() instanceof usuarios.vendedores.Vendedor) {
            mostrarMenuVendedor();
        }
    }
    
    private void mostrarMenuCliente() {
        usuarios.clientes.Cliente cliente = (usuarios.clientes.Cliente) sistema.getUsuarioActual();
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
        usuarios.vendedores.Vendedor vendedor = (usuarios.vendedores.Vendedor) sistema.getUsuarioActual();
        System.out.println("💼 MENÚ VENDEDOR - " + vendedor.getNombre());
        System.out.println("═══════════════════════════════════");
        System.out.println("1. 👤 Ver Mi Información");
        System.out.println("2. 📦 Ver Stock de Productos");
        System.out.println("3. ➕ Agregar Producto al Stock");
        System.out.println("4. 💰 Vender Productos");
        System.out.println("5. 📊 Ver Mis Ventas");
        System.out.println("6. 🚪 Cerrar Sesión");
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
                    System.out.println("✅ ¡Bienvenido al sistema!");
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
                // Solicitar datos básicos
                String nombre = solicitarDato("Nombre", "texto");
                String apellido = solicitarDato("Apellido", "texto");
                String email = solicitarDato("Email", "email");
                String dni = solicitarDato("DNI (8 dígitos)", "dni");
                
                // Solicitar tipo de usuario
                int tipoUsuario = solicitarTipoUsuario();
                
                // Solicitar contraseña
                String password = solicitarPassword();
                
                usuarios.Usuario usuario = null;
                
                if (tipoUsuario == 1) {
                    // Crear cliente
                    String direccion = solicitarDato("Dirección", "texto");
                    String telefono = solicitarDato("Teléfono", "texto");
                    
                    usuario = new usuarios.clientes.Cliente(nombre, apellido, email, usuarios.Rol.CLIENTE, 1, dni, 0, usuarios.clientes.MetodoPago.EFECTIVO, 0.0, direccion, telefono);
                } else if (tipoUsuario == 2) {
                    // Crear vendedor
                    double salario = solicitarNumero("Salario base: $", "salario");
                    double comision = solicitarNumero("Comisión por venta (%): ", "comision");
                    int metaVentas = (int) solicitarNumero("Meta de ventas mensual: ", "meta");
                    String especializacion = solicitarDato("Especialización", "texto");
                    
                    usuario = new usuarios.vendedores.Vendedor(nombre, apellido, email, usuarios.Rol.VENDEDOR, 1, dni, salario);
                }
                
                if (usuario != null) {
                    if (sistema.registrarUsuario(usuario, password)) {
                        System.out.println("✅ Usuario registrado exitosamente.");
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
        // Limpiar pantalla (funciona en la mayoría de terminales)
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
        usuarios.clientes.Cliente cliente = (usuarios.clientes.Cliente) sistema.getUsuarioActual();
        System.out.println(cliente.toString());
        pausar();
    }
    
    /**
     * Muestra información del vendedor
     */
    private void mostrarInfoVendedor() {
        usuarios.vendedores.Vendedor vendedor = (usuarios.vendedores.Vendedor) sistema.getUsuarioActual();
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
        usuarios.clientes.Cliente cliente = (usuarios.clientes.Cliente) sistema.getUsuarioActual();
        System.out.println("💰 Su saldo actual: $" + String.format("%.2f", cliente.getSaldo()));
        System.out.println("═══════════════════════════════════");
        
        // Mostrar productos disponibles
        sistema.mostrarProductosDisponibles();
        
        System.out.println("\n🛒 Para comprar un producto:");
        System.out.println("1. Ingrese el ID del producto");
        System.out.println("2. Ingrese la cantidad deseada");
        System.out.println("3. Confirme la compra");
        
        try {
            System.out.print("\nID del producto (0 para cancelar): ");
            int productoId = Integer.parseInt(scanner.nextLine());
            
            if (productoId == 0) {
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
                boolean exito = sistema.comprarProducto(productoId, cantidad);
                if (!exito) {
                    System.out.println("❌ No se pudo completar la compra.");
                }
            } else {
                System.out.println("❌ Compra cancelada.");
            }
            
        } catch (NumberFormatException e) {
            System.out.println("❌ Debe ingresar números válidos.");
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
        
        usuarios.clientes.Cliente cliente = (usuarios.clientes.Cliente) sistema.getUsuarioActual();
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
        
        System.out.println("\n📝 Para procesar una venta:");
        System.out.println("1. Seleccione el producto por ID");
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
            System.out.print("ID del producto: ");
            int productoId = Integer.parseInt(scanner.nextLine());
            
            System.out.print("Cantidad: ");
            int cantidad = Integer.parseInt(scanner.nextLine());
            
            // Crear venta simple
            var venta = sistema.crearVentaSimple();
            if (venta != null) {
                boolean exito = sistema.agregarProductoAVenta(venta, productoId, cantidad);
                if (exito) {
                    boolean procesada = sistema.procesarVenta(venta);
                    if (procesada) {
                        System.out.println("✅ Venta procesada exitosamente!");
                    } else {
                        System.out.println("❌ Error al procesar la venta.");
                    }
                } else {
                    System.out.println("❌ Error al agregar el producto a la venta.");
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Debe ingresar números válidos.");
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
            
            if (exito) {
                System.out.println("\n✅ ¡Producto agregado exitosamente al stock!");
            } else {
                System.out.println("\n❌ Error al agregar el producto.");
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
        
        pausar();
    }
}
