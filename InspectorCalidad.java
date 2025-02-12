import java.util.Random;

public class InspectorCalidad extends Thread {
    private final int idInspector;
    private final BuzonRevision buzonRevision;
    private final BuzonReproceso buzonReproceso;
    private final Deposito deposito;
    private final int totalEsperado; 
    private final int maxFallos;

    private int fallosRealizados = 0;
    private final Random random = new Random();

    public InspectorCalidad(int idInspector, 
                            BuzonRevision buzonRevision,
                            BuzonReproceso buzonReproceso,
                            Deposito deposito,
                            int totalEsperado) {
        this.idInspector = idInspector;
        this.buzonRevision = buzonRevision;
        this.buzonReproceso = buzonReproceso;
        this.deposito = deposito;
        this.totalEsperado = totalEsperado;
        // Se toma el piso del 10% de totalEsperado.
        this.maxFallos = (int) Math.floor(totalEsperado * 0.1);
    }

    @Override
    public void run() {
        try {
            while (true) {
                // Intentar retirar un producto sin bloquear (semi-activo)
                Product p = buzonRevision.retirar();
                if (p == null) {
                    // No hay productos en el buzón de revisión: ceder el turno a otros hilos.
                    Thread.yield();
                    continue;
                }

                // Si, por error, se obtiene FIN desde aquí (lo normal es que FIN vaya al buzón de reproceso)
                if (p.isFin()) {
                    System.out.println("Inspector " + idInspector 
                                       + " detectó FIN en BuzonRevision (inusual). Termina.");
                    break;
                }

                System.out.println("Inspector " + idInspector + " INSPECCIONA " + p);
                boolean aprobado = decideAprobacion();
                if (aprobado) {
                    int aprobados = deposito.depositarConConteo(p);
                    System.out.println("Inspector " + idInspector + " APRUEBA " 
                                       + p + " (Total aprobados: " + aprobados + ")");
                    // Si al depositar se alcanza o supera la meta, se genera FIN y se termina.
                    if (aprobados >= totalEsperado) {
                        Product finProduct = new Product("FIN", true);
                        buzonReproceso.depositar(finProduct);
                        System.out.println("Inspector " + idInspector 
                                           + " alcanza la meta y deposita FIN en BuzonReproceso. Termina.");
                        break; // Este inspector deja de inspeccionar
                    }
                } else {
                    // Producto rechazado se envía a reproceso
                    buzonReproceso.depositar(p);
                    System.out.println("Inspector " + idInspector + " RECHAZA " 
                                       + p + " (lo envía a reproceso)");
                }

                // Simula el tiempo de revisión (sin afectar la espera semi-activa)
                Thread.sleep(150);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Inspector " + idInspector + " interrumpido!");
        }
    }

    /**
     * Lógica de decisión de aprobación:
     * - Cada inspector puede fallar máximo el 10% del total de productos a producir.
     * - Si aún no se ha alcanzado el máximo, un número aleatorio entre 1 y 100 que sea múltiplo de 7 => rechazo.
     */
    private boolean decideAprobacion() {
        if (fallosRealizados >= maxFallos) {
            return true;
        }
        int n = 1 + random.nextInt(100);
        if (n % 7 == 0) {
            fallosRealizados++;
            return false;
        }
        return true;
    }
}
