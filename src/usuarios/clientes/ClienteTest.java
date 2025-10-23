package usuarios.clientes;

import org.junit.jupiter.api.Test;
import usuarios.Rol;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de pruebas unitarias para la clase Cliente.
 * Verifica la inicialización de campos, las validaciones de saldo y cantidad
 * de productos, y la sobrescritura del método toString().
 */
class ClienteTest {

    // Datos base para constructores
    private static final String NOMBRE = "Laura";
    private static final String APELLIDO = "Santos";
    private static final String EMAIL = "laura@ejemplo.com";
    private static final Rol ROL = Rol.CLIENTE;
    private static final int ESTADO = 1; // Activo
    private static final String DNI = "12345678";
    private static final int CANT_COMPRADOS_VALIDO = 10;
    private static final double SALDO_VALIDO = 500.50;

    // Usamos uno de los nuevos métodos de pago para pruebas de set/constructor completo
    private static final MetodoPago METODO_PAGO_VALIDO = MetodoPago.CREDITO;

    // =========================================================================
    // PRUEBAS DE CONSTRUCTOR Y GETTERS
    // =========================================================================

    /**
     * Verifica que el constructor completo inicializa todos los campos correctamente
     * y aplica la herencia de Usuario.
     */
    @Test
    void testConstructorCompletoInicializacionCorrecta() {
        Cliente cliente = new Cliente(
                NOMBRE, APELLIDO, EMAIL, ROL, ESTADO, DNI,
                CANT_COMPRADOS_VALIDO, METODO_PAGO_VALIDO, SALDO_VALIDO
        );

        // Verificación de campos propios de Cliente
        assertEquals(CANT_COMPRADOS_VALIDO, cliente.getCantProductosComprados(), "La cantidad de productos comprados debe coincidir.");
        assertEquals(METODO_PAGO_VALIDO, cliente.getMetodoPago(), "El método de pago debe coincidir con el valor pasado (CREDITO).");
        assertEquals(SALDO_VALIDO, cliente.getSaldo(), 0.001, "El saldo debe coincidir.");

        // Verificación de un campo heredado de Usuario (para asegurar la llamada a super)
        assertEquals(DNI, cliente.getDni(), "El DNI heredado debe ser correcto.");
    }

    /**
     * Verifica que el constructor parcial inicializa los campos por defecto.
     * El valor por defecto para MetodoPago es EFECTIVO (según la clase Cliente).
     */
    @Test
    void testConstructorParcialInicializacionPorDefecto() {
        Cliente cliente = new Cliente(NOMBRE, APELLIDO, EMAIL, ROL, ESTADO, DNI);

        // Verificación de valores por defecto
        assertEquals(0, cliente.getCantProductosComprados(), "La cantidad de productos debe ser 0 por defecto.");
        assertEquals(0.0, cliente.getSaldo(), 0.001, "El saldo debe ser 0.0 por defecto.");
        assertEquals(MetodoPago.EFECTIVO, cliente.getMetodoPago(), "El método de pago debe ser EFECTIVO por defecto.");
    }

    // =========================================================================
    // PRUEBAS DE VALIDACIÓN Y SETTERS
    // =========================================================================

    /**
     * Verifica que el constructor lanza una excepción cuando la cantidad de productos
     * comprados es negativa.
     */
    @Test
    void testConstructorLanzaExcepcionCantProductosCompradosNegativa() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Cliente(NOMBRE, APELLIDO, EMAIL, ROL, ESTADO, DNI, -5, METODO_PAGO_VALIDO, SALDO_VALIDO);
        }, "Se esperaba IllegalArgumentException para cantidad de productos negativa en el constructor.");
    }

    /**
     * Verifica que el setter lanza una excepción cuando la cantidad de productos
     * comprados es negativa y que el valor no se actualiza.
     */
    @Test
    void testSetterLanzaExcepcionCantProductosCompradosNegativa() {
        Cliente cliente = new Cliente(NOMBRE, APELLIDO, EMAIL, ROL, ESTADO, DNI);
        cliente.setCantProductosComprados(10); // Valor inicial

        assertThrows(IllegalArgumentException.class, () -> {
            cliente.setCantProductosComprados(-1);
        }, "Se esperaba IllegalArgumentException para cantidad de productos negativa en el setter.");

        // El valor original debe mantenerse
        assertEquals(10, cliente.getCantProductosComprados(), "La cantidad no debe actualizarse tras la excepción.");
    }

    /**
     * Verifica que el constructor lanza una excepción cuando el saldo inicial es negativo.
     */
    @Test
    void testConstructorLanzaExcepcionSaldoNegativo() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Cliente(NOMBRE, APELLIDO, EMAIL, ROL, ESTADO, DNI, CANT_COMPRADOS_VALIDO, METODO_PAGO_VALIDO, -10.0);
        }, "Se esperaba IllegalArgumentException para saldo negativo en el constructor.");
    }

    /**
     * Verifica que el setter lanza una excepción cuando el saldo es negativo y
     * que el valor no se actualiza.
     */
    @Test
    void testSetterLanzaExcepcionSaldoNegativo() {
        Cliente cliente = new Cliente(NOMBRE, APELLIDO, EMAIL, ROL, ESTADO, DNI);
        cliente.setSaldo(100.0); // Valor inicial

        assertThrows(IllegalArgumentException.class, () -> {
            cliente.setSaldo(-0.01);
        }, "Se esperaba IllegalArgumentException para saldo negativo en el setter.");

        // El valor original debe mantenerse
        assertEquals(100.0, cliente.getSaldo(), 0.001, "El saldo no debe actualizarse tras la excepción.");
    }

    /**
     * Verifica que el setter de MetodoPago funciona correctamente, usando un nuevo valor (QR).
     */
    @Test
    void testSetMetodoPago() {
        Cliente cliente = new Cliente(NOMBRE, APELLIDO, EMAIL, ROL, ESTADO, DNI);

        cliente.setMetodoPago(MetodoPago.QR);
        assertEquals(MetodoPago.QR, cliente.getMetodoPago(), "El método de pago debe actualizarse a QR.");
    }

    // =========================================================================
    // PRUEBAS DE MÉTODOS SOBREESCRITOS
    // =========================================================================

    /**
     * Verifica que el método toString() sobrescrito incluye la información de Usuario
     * (llamada a super.toString()) y los campos específicos de Cliente.
     */
    @Test
    void testToStringIncluyeDatosCliente() {
        Cliente cliente = new Cliente(
                "Juan", "García", "jg@test.com", Rol.CLIENTE, 1, "99887766",
                5, MetodoPago.BILLETERA_VIRTUAL, 1250.75
        );
        String output = cliente.toString();

        // 1. Verifica la información de Usuario (por ejemplo, el nombre)
        assertTrue(output.contains("Nombre Completo: Juan García"), "Debe incluir el resultado de super.toString().");

        // 2. Verifica la información específica de Cliente
        assertTrue(output.contains("Saldo: $1250.75"), "Debe incluir el saldo formateado con punto decimal.");
        assertTrue(output.contains("Cant. Productos Comprados: 5"), "Debe incluir la cantidad de productos.");
        assertTrue(output.contains("Método de Pago: BILLETERA_VIRTUAL"), "Debe incluir el método de pago BILLETERA_VIRTUAL.");
    }
}