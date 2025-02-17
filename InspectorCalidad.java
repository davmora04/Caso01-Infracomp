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

    // Banda global de finalización
    private final ControlGlobal controlGlobal;

    public InspectorCalidad(
        int idInspector, 
        BuzonRevision buzonRevision,
        BuzonReproceso buzonReproceso,
        Deposito deposito,
        int totalEsperado,
        int numProductores,
        ControlGlobal controlGlobal
    ) {
        this.idInspector = idInspector;
        this.buzonRevision = buzonRevision;
        this.buzonReproceso = buzonReproceso;
        this.deposito = deposito;
        this.totalEsperado = totalEsperado;
        this.maxFallos = (int) Math.floor(totalEsperado * 0.1);
        this.controlGlobal = controlGlobal;
    }

    @Override
    public void run() {
        try {
            // Mientras no se active la bandera de finalización global.
            while (!controlGlobal.isFin()) {
                // Intentar retirar un producto del buzón de revisión.
                Product p = buzonRevision.retirar();
                if (p == null) {
                    // Si no hay productos: se cede el turno.
                    Thread.yield();
                    continue;
                }

                // Por si acaso llega un FIN en el buzón de revisión (Que no debería pasar).
                if (p.isFin()) {
                    System.out.println("Inspector " + idInspector 
                        + " detectó FIN en BuzonRevision. Termina.");
                    break;
                }

                System.out.println("Inspector " + idInspector + " INSPECCIONA " + p);
                boolean aprobado = decideAprobacion();
                if (aprobado) {
                    int aprobados = deposito.depositarConConteo(p);
                    System.out.println("Inspector " + idInspector + " APRUEBA " 
                        + p + " (Total aprobados: " + aprobados + ")");
                    
                    // Si al depositar se alcanza o supera la meta, entonces:
                    if (aprobados >= totalEsperado) {
                        // Se usa un bloque sincronizado para asegurarse que solo un inspector realice el depósito del FIN.
                        synchronized (controlGlobal) {
                            if (!controlGlobal.isFin()) {
                                controlGlobal.setFin(true);
                                // Se deposita un único FIN para notificar a los productores.
                                Product finProduct = new Product("FIN", true);
                                buzonReproceso.depositar(finProduct);
                                System.out.println("Inspector " + idInspector 
                                    + " alcanza la meta y deposita FIN para productores. Termina.");
                            }
                        }
                        break; // Termina el ciclo del inspector.
                    }
                } else {
                    // Producto rechazado se envía a reproceso.
                    buzonReproceso.depositar(p);
                    System.out.println("Inspector " + idInspector + " RECHAZA " 
                        + p + " (lo envía a reproceso)");
                }

                // Simulación del tiempo de revisión.
                Thread.sleep(150);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Inspector " + idInspector + " interrumpido!");
        }
        System.out.println("Inspector " + idInspector + " finaliza.");
    }

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
