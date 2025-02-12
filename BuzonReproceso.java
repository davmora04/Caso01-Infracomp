import java.util.LinkedList;
import java.util.Queue;

public class BuzonReproceso {
    // Cola sin límite
    private final Queue<Product> cola = new LinkedList<>();

    public synchronized void depositar(Product p) {
        cola.add(p);
        notifyAll(); // Avisar a los hilos que puedan estar esperando retirar
    }

    /**
     * Método no bloqueante para que el productor verifique sin quedar esperando
     * (por si deseas mantener la 'prioridad' de reproceso de forma rápida).
     */
    public synchronized Product retirar() {
        if (cola.isEmpty()) {
            return null;
        }
        return cola.remove();
    }

    public synchronized boolean estaVacio() {
        return cola.isEmpty();
    }
}
