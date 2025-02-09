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
                // Retira un producto del buzón de revisión (bloquea si está vacío)
                Product p = buzonRevision.retirar();
                // (Por diseño, aquí nunca debería aparecer un FIN, 
                //  porque FIN se deposita en BuzonReproceso. 
                //  Si apareciera por error, podríamos decidir terminar).
                if (p.isFin()) {
                    // Caso no contemplado en la especificación,
                    // pero podemos simplemente terminar:
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
                        // Generar “FIN” en el buzón de reproceso
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

                // Simula tiempo de revisión
                Thread.sleep(2000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Inspector " + idInspector + " interrumpido!");
        }
    }

    /**
     * Lógica de decisión de aprobación:
     * - Cada inspector puede fallar hasta maxFallos veces.
     * - Si aún no hemos alcanzado maxFallos, un número aleatorio 1..100 
     *   que sea múltiplo de 7 => rechazo.
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