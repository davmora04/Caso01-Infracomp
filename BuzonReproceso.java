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
     * Retira un producto de forma bloqueante.
     * Si la cola está vacía, el hilo hace wait() hasta que alguien deposite.
     */
    public synchronized Product retirar() throws InterruptedException {
        while (cola.isEmpty()) {
            wait();
        }
        return cola.remove();
    }

    /**
     * Método no bloqueante para que el productor verifique sin quedar esperando
     * (Por si deseas mantener la 'prioridad' de reproceso de forma rápida).
     */
    public synchronized Product retirarNoBloqueante() {
        if (cola.isEmpty()) {
            return null;
        }
        return cola.remove();
    }

    public synchronized boolean estaVacio() {
        return cola.isEmpty();
    }
}