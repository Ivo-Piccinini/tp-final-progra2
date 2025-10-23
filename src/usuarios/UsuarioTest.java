package usuarios;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

/**
 * Clase concreta de prueba que extiende de Usuario para permitir la instanciación
 * en los tests, ya que la clase base es abstracta.
 */
class Cliente extends Usuario {
    public Cliente(String nombre, String apellido, String email, Rol rol, int estado, String dni) {
        super(nombre, apellido, email, rol, estado, dni);
    }
    // Constructor vacío
    public Cliente() {
        super();
    }
}

/**
 * Clase de pruebas unitarias para la clase Usuario.
 * Verifica la lógica de constructores, validaciones de datos (Email, DNI)
 * y el cumplimiento del contrato de las interfaces (Comparable, equals, hashCode).
 */
class UsuarioTest {

    private static final String DNI_VALIDO = "12345678";
    private static final String EMAIL_VALIDO = "prueba@valida.com";
    private static int contadorInicial;

    /**
     * Configuración inicial antes de cada método de prueba.
     * Captura el valor actual del contador estático para asegurar que las pruebas
     * de ID se basen en un punto de partida conocido.
     */
    @BeforeEach
    void setUp() {
        contadorInicial = Usuario.getContador();
    }

    // =========================================================================
    // PRUEBAS DE VALIDACIÓN Y CONSTRUCTOR
    // =========================================================================

    /**
     * Verifica que el campo 'id' se asigne correctamente y que el contador estático
     * se incremente por cada nueva instancia de Usuario.
     * "assertEquals" se utiliza para afirmar que el valor esperado (el resultado correcto que debería obtenerse) coincide exactamente con el valor actual (el resultado que produjo el código bajo prueba).
     *
     * Si los dos valores no son iguales, la prueba falla y lanza un AssertionFailedError.
     */
    @Test
    void testIDAutoincremental() {
        // Creamos dos instancias
        Cliente user1 = new Cliente("A", "A", EMAIL_VALIDO, Rol.CLIENTE, 1, "10000000");
        Cliente user2 = new Cliente("B", "B", EMAIL_VALIDO, Rol.VENDEDOR, 1, "20000000");

        // El ID del primer usuario debe coincidir con el contador antes de la creación
        assertEquals(contadorInicial, user1.getId(), "El ID del primer usuario debe ser el contador inicial.");
        // El ID del segundo usuario debe ser el ID del primero más 1
        assertEquals(user1.getId() + 1, user2.getId(), "El ID debe ser autoincremental.");
    }

    /**
     * Prueba la validación del DNI en el constructor.
     * Verifica que se lanza una IllegalArgumentException si el DNI no cumple
     * con el formato de 8 dígitos numéricos.
     * "assertThrows(...)" : Es la aserción clave de JUnit para verificar que se lanza una excepción. ||
     * "IllegalArgumentException.class" : Especifica que la prueba solo pasará si se lanza exactamente una excepción de tipo IllegalArgumentException. ||
     * "() -> { new Cliente(...) }" : Es una expresión lambda que envuelve el código que debe causar la excepción. En este caso, intenta instanciar la clase Cliente con el dniCorto inválido. ||
     */
    @Test
    void testDniInvalidoLanzaExcepcionEnConstructor() {
        String dniCorto = "12345"; // DNI inválido por longitud

        // Se espera que la creación del objeto lance una excepción
        assertThrows(IllegalArgumentException.class, () -> {
            new Cliente("Test", "DNI", EMAIL_VALIDO, Rol.CLIENTE, 1, dniCorto);
        }, "Se esperaba IllegalArgumentException para DNI inválido en el constructor.");
    }

    /**
     * Prueba la validación del Email en el setter.
     * Verifica que el setter lanza una IllegalArgumentException ante un email inválido
     * y que el valor del email original no se modifica.
     */
    @Test
    void testEmailInvalidoLanzaExcepcionEnSetter() {
        Cliente usuario = new Cliente("Test", "Email", EMAIL_VALIDO, Rol.CLIENTE, 1, DNI_VALIDO);
        String emailInvalido = "invalido@.c"; // Email inválido por formato

        // Se espera que el setter lance una excepción
        assertThrows(IllegalArgumentException.class, () -> {
            usuario.setEmail(emailInvalido);
        }, "Se esperaba IllegalArgumentException para email inválido en el setter.");

        // Se verifica que, al fallar el setter, el email original se mantiene
        assertEquals(EMAIL_VALIDO, usuario.getEmail(), "El email no debe cambiar si la validación falla.");
    }

    // =========================================================================
    // PRUEBAS DE CONTRATO (equals, hashCode, Comparable)
    // =========================================================================

    /**
     * Verifica que el método equals y hashCode cumplen el contrato
     * basándose en la combinación de 'id' y 'dni' (según la implementación actual).
     * También verifica el comportamiento en un HashSet.
     */
    @Test
    void testEqualsYHashCode() {
        // Usuario A (original)
        Cliente userA = new Cliente("A", "A", "a@test.com", Rol.CLIENTE, 1, "11111111");

        // Usuario B: Diferente a A
        Cliente userB = new Cliente("B", "B", "b@test.com", Rol.CLIENTE, 1, "22222222");

        // Prueba de objetos diferentes
        /*
        * El método assertFalse se utiliza para afirmar que una condición booleana es falsa.
        * Es ideal para verificar estados o resultados lógicos dentro de tus pruebas.
        * */
        assertFalse(userA.equals(userB), "Usuarios con IDs y DNIs diferentes no deben ser iguales.");

        // Prueba de identidad
        /*
         * El método assertTrue se utiliza para afirmar que una condición booleana es verdadera.
         * Es ideal para verificar estados o resultados lógicos dentro de tus pruebas.
         * */
        assertTrue(userA.equals(userA), "El objeto debe ser igual a sí mismo.");

        // Prueba en HashSet (asegura que los objetos distintos se almacenan)
        Set<Usuario> conjunto = new HashSet<>();
        conjunto.add(userA);
        conjunto.add(userB);
        assertEquals(2, conjunto.size(), "El HashSet debe contener 2 elementos distintos según equals/hashCode.");
    }

    /**
     * Verifica que la implementación de Comparable ordena correctamente
     * basándose en el campo 'id' (orden natural).
     */
    @Test
    void testComparableOrdenaPorID() {
        // IDs asignados secuencialmente: user1 < user2
        Cliente user1 = new Cliente("A", "U1", EMAIL_VALIDO, Rol.CLIENTE, 1, "10000001");
        Cliente user2 = new Cliente("B", "U2", EMAIL_VALIDO, Rol.VENDEDOR, 1, "10000002");

        List<Usuario> lista = new ArrayList<>();
        lista.add(user2); // Agregado fuera de orden
        lista.add(user1);

        // Ordenamos la lista usando Comparable
        Collections.sort(lista);

        // user1 (ID más bajo) debe estar en la posición 0
        assertEquals(user1, lista.get(0), "El ordenamiento debe colocar el ID más bajo (user1) primero.");

        // Verificación directa de compareTo
        assertTrue(user1.compareTo(user2) < 0, "user1.compareTo(user2) debe ser negativo (user1 es menor).");
    }

    /**
     * Verifica que el método toString() genera una cadena formateada
     * y contiene toda la información clave de manera legible.
     */
    @Test
    void testToStringFormato() {
        Cliente user = new Cliente("Juan", "Perez", "jp@tienda.com", Rol.VENDEDOR, 1, "99999999");
        String output = user.toString();

        // Verificaciones de contenido y formato legible
        assertTrue(output.contains("DATOS DEL USUARIO (ID:"), "Falta el encabezado del toString.");
        assertTrue(output.contains("Nombre Completo: Juan Perez"), "Falta el nombre completo.");
        assertTrue(output.contains("Email: jp@tienda.com"), "Falta el email.");
        assertTrue(output.contains("Estado: Activo"), "El estado 1 debe mostrarse como 'Activo'.");
    }
}