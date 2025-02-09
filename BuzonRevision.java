import java.util.LinkedList;
import java.util.Queue;

public class BuzonRevision {
    private final int capacidad;
    private final Queue<Product> cola;

    public BuzonRevision(int capacidad) {
        this.capacidad = capacidad;
        this.cola = new LinkedList<>();
    }

    /**
     * Deposita un producto de forma bloqueante.
     * - Si el buzón está lleno, el hilo hace wait() hasta que haya espacio.
     * - No se maneja FIN aquí (suponemos que FIN no entra nunca al buzón de revisión).
     */
    public synchronized void depositar(Product p) throws InterruptedException {
        while (cola.size() == capacidad) {
            wait();
        }
        cola.add(p);
        // Notificar a posibles hilos que estén esperando retirar
        notifyAll();
    }

    /**
     * Retira un producto de manera bloqueante.
     * Si la cola está vacía, el hilo hace wait() hasta que alguien deposite.
     */
    public synchronized Product retirar() throws InterruptedException {
        while (cola.isEmpty()) {
            wait();
        }
        Product p = cola.remove();
        // Notificar a posibles hilos que estén esperando depositar
        notifyAll();
        return p;
    }

    public synchronized boolean estaVacio() {
        return cola.isEmpty();
    }
}