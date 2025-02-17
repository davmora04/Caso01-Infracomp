import java.util.concurrent.atomic.AtomicInteger;

public class Productor extends Thread {

    private static final AtomicInteger ContadorProducto = new AtomicInteger(1);

    private final int idProductor;
    private final BuzonReproceso buzonReproceso;
    private final BuzonRevision buzonRevision;
    private final ControlGlobal controlGlobal;  

    public Productor(int idProductor, 
                     BuzonReproceso buzonReproceso,
                     BuzonRevision buzonRevision,
                     ControlGlobal controlGlobal) {
        this.idProductor = idProductor;
        this.buzonReproceso = buzonReproceso;
        this.buzonRevision = buzonRevision;
        this.controlGlobal = controlGlobal;  
    }

    @Override
    public void run() {
        try {
            // Mientras no haya finalización global
            while (!controlGlobal.isFin()) {
                
                // 1) Prioridad: Verificar si hay producto en reproceso
                Product prodRepro = buzonReproceso.retirar();
                if (prodRepro != null) {
                    // Si es FIN, entonces salir
                    if (prodRepro.isFin()) {
                        System.out.println("Productor " + idProductor
                                + " recibe FIN de BuzonReproceso. Termina.");
                        break;
                    } else {
                        // Reprocesar el producto
                        System.out.println("Productor " + idProductor
                                + " REPROCESANDO " + prodRepro);
                        reprocesar(prodRepro);

                        // Antes de depositar, se reverifica la bandera:
                        if (controlGlobal.isFin()) {
                            break; 
                        }

                        // Bloquea si el buzón de revisión está lleno
                        buzonRevision.depositar(prodRepro, controlGlobal);
                        System.out.println("Productor " + idProductor
                                + " deposita REPROCESADO en BuzonRevision: " + prodRepro);
                    }
                } else {
                    // 2) No hay producto para reprocesar => fabricar uno nuevo
                    Product nuevo = generarProductoNuevo();
                    System.out.println("Productor " + idProductor
                            + " FABRICANDO " + nuevo);
                    fabricar(nuevo);

                    // Reverificar bandera:
                    if (controlGlobal.isFin()) {
                        break;
                    }

                    // Bloquea si el buzón está lleno
                    buzonRevision.depositar(nuevo, controlGlobal);
                    System.out.println("Productor " + idProductor
                            + " deposita NUEVO en BuzonRevision: " + nuevo);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Productor " + idProductor + " interrumpido!");
        }
        System.out.println("Productor " + idProductor + " finaliza.");
    }

    private void fabricar(Product p) throws InterruptedException {
        Thread.sleep(150);
    }

    private void reprocesar(Product p) throws InterruptedException {
        Thread.sleep(150);
    }

    private Product generarProductoNuevo() {
        int productId = ContadorProducto.getAndIncrement();
        String nombre = "P" + idProductor + "_"+productId;
        return new Product(nombre, false);
    }
}
