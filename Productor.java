import java.util.concurrent.atomic.AtomicInteger;

public class Productor extends Thread {

    private static final AtomicInteger ContadorProducto = new AtomicInteger(1);

    private final int idProductor;
    private final BuzonReproceso buzonReproceso;
    private final BuzonRevision buzonRevision;

    public Productor(int idProductor, 
                     BuzonReproceso buzonReproceso,
                     BuzonRevision buzonRevision) {
        this.idProductor = idProductor;
        this.buzonReproceso = buzonReproceso;
        this.buzonRevision = buzonRevision;
    }

    @Override
    public void run() {
        try {
            while (true) {
                // 1) Prioridad: Verificar si hay producto en reproceso (sin bloquear)
                Product prodRepro = buzonReproceso.retirar();

                if (prodRepro != null) {
                    // Hay producto en reproceso
                    if (prodRepro.isFin()) {
                        // Si es FIN, el productor termina
                        System.out.println("Productor " + idProductor
                                + " recibe FIN de BuzonReproceso. Termina.");
                        break;
                    } else {
                        // Reprocesar el producto
                        System.out.println("Productor " + idProductor
                                + " REPROCESANDO " + prodRepro);
                        reprocesar(prodRepro);

                        // Bloquea si el buzón de revisión está lleno
                        buzonRevision.depositar(prodRepro);
                        System.out.println("Productor " + idProductor
                                + " deposita REPROCESADO en BuzonRevision: " + prodRepro);
                    }
                } 
                else {
                    // 2) Si no hay producto para reprocesar, fabricar uno nuevo
                    Product nuevo = generarProductoNuevo();
                    System.out.println("Productor " + idProductor
                            + " FABRICANDO " + nuevo);
                    fabricar(nuevo);

                    // Bloquea si el buzón de revisión está lleno
                    buzonRevision.depositar(nuevo);
                    System.out.println("Productor " + idProductor
                            + " deposita NUEVO en BuzonRevision: " + nuevo);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Productor " + idProductor + " interrumpido!");
        }
    }

    /**
     * Simula el tiempo de fabricación de un producto nuevo.
     */
    private void fabricar(Product p) throws InterruptedException {
        Thread.sleep(150);
    }

    /**
     * Simula el tiempo de reproceso de un producto rechazado.
     */
    private void reprocesar(Product p) throws InterruptedException {
        Thread.sleep(150);
    }

    /**
     * Genera un nuevo producto con un identificador único.
     */
    private Product generarProductoNuevo() {
        int productId = ContadorProducto.getAndIncrement();
        String nombre = "P" + idProductor + "_"+productId;
        return new Product(nombre, false);
    }
}