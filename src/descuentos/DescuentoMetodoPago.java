package descuentos;

import usuarios.clientes.MetodoPago;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase que maneja los descuentos por método de pago.
 * Cada método de pago tiene un porcentaje de descuento asociado.
 */
public class DescuentoMetodoPago {
    private static final Map<MetodoPago, Double> DESCUENTOS_POR_METODO;
    
    static {
        DESCUENTOS_POR_METODO = new HashMap<>();
        DESCUENTOS_POR_METODO.put(MetodoPago.QR, 5.0);           // 5% descuento
        DESCUENTOS_POR_METODO.put(MetodoPago.DEBITO, 3.0);       // 3% descuento
        DESCUENTOS_POR_METODO.put(MetodoPago.BILLETERA_VIRTUAL, 4.0); // 4% descuento
        DESCUENTOS_POR_METODO.put(MetodoPago.TARJETA_CREDITO, 0.0); // Sin descuento
        DESCUENTOS_POR_METODO.put(MetodoPago.EFECTIVO, 0.0);      // Sin descuento
    }
    
    /**
     * Obtiene el porcentaje de descuento para un método de pago
     * @param metodoPago metodo de pago del cual queremos conocer el descuento
     * @return descuento del metodo de pago especificado
     */
    public static double obtenerDescuento(MetodoPago metodoPago) {
        return DESCUENTOS_POR_METODO.getOrDefault(metodoPago, 0.0);
    }
    
    /**
     * Calcula el descuento en pesos para un monto dado
     * @param monto
     * @param metodoPago
     * @return descuento para el monto y el metodo de pago especificados
     */
    public static double calcularDescuento(double monto, MetodoPago metodoPago) {
        double porcentaje = obtenerDescuento(metodoPago);
        return monto * (porcentaje / 100.0);
    }
    
    /**
     * Calcula el monto final después del descuento
     * @param monto
     * @param metodoPago
     * @return monto final luego de ser aplicado el descuento
     */
    public static double calcularMontoFinal(double monto, MetodoPago metodoPago) {
        double descuento = calcularDescuento(monto, metodoPago);
        return monto - descuento;
    }
    
    /**
     * Muestra información sobre los descuentos disponibles
     */
    public static void mostrarDescuentosDisponibles() {
        System.out.println("💳 DESCUENTOS POR MÉTODO DE PAGO");
        System.out.println("═══════════════════════════════════");
        
        for (Map.Entry<MetodoPago, Double> entry : DESCUENTOS_POR_METODO.entrySet()) {
            MetodoPago metodo = entry.getKey();
            double descuento = entry.getValue();
            
            String emoji = obtenerEmojiMetodo(metodo);
            String descripcion = obtenerDescripcionMetodo(metodo);
            
            if (descuento > 0) {
                System.out.printf("%s %s: %.1f%% de descuento%n",
                    emoji, descripcion, descuento);
            } else {
                System.out.printf("%s %s: Sin descuento%n",
                    emoji, descripcion);
            }
        }
        System.out.println("═══════════════════════════════════");
    }
    
    /**
     * Obtiene el emoji correspondiente al método de pago
     * @param metodo metodo de pago cuyo emogi queremos obtener
     * @return el emoji correspondiente al método de pago
     */
    private static String obtenerEmojiMetodo(MetodoPago metodo) {
        switch (metodo) {
            case QR: return "📱";
            case DEBITO: return "💳";
            case TARJETA_CREDITO: return "💳";
            case BILLETERA_VIRTUAL: return "📲";
            case EFECTIVO: return "💵";
            default: return "💰";
        }
    }
    
    /**
     * Obtiene la descripción del método de pago
     * @param metodo metodo de pago cuya descripcion queremos obtener
     * @return descripcion del metodo de pago especificado
     */
    private static String obtenerDescripcionMetodo(MetodoPago metodo) {
        switch (metodo) {
            case QR: return "Pago QR";
            case DEBITO: return "Tarjeta de Débito";
            case TARJETA_CREDITO: return "Tarjeta de Crédito";
            case BILLETERA_VIRTUAL: return "Billetera Virtual";
            case EFECTIVO: return "Efectivo";
            default: return "Método no reconocido";
        }
    }
}
