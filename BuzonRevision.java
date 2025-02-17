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
     * Deposita un producto
     * Si el buzón está lleno, el hilo hace wait() hasta que haya espacio.
     */
    public synchronized void depositar(Product p, ControlGlobal cg) throws InterruptedException {
        while (cola.size() == capacidad  && !cg.isFin()) {
            wait();
        }
        if (cg.isFin()) {
            // Salir sin depositar
            return;
        }
        cola.add(p);
        // Notificar a posibles hilos que estén esperando retirar
        notifyAll();
        // Mostrar el estado actual del buzón
        mostrarEstado();
    }

    /**
     * Retira un producto.
     * Si no hay productos disponibles, no retorna nada.
     */
    public synchronized Product retirar() {
        if (cola.isEmpty()) {
            return null;
        }
        Product p = cola.remove();
        notifyAll();
        // Mostrar el estado actual del buzón después de retirar
        mostrarEstado();
        return p;
    }

    public synchronized boolean estaVacio() {
        return cola.isEmpty();
    }

    /**
     * Imprime el estado actual del buzón (los productos contenidos en él).
     */
    private void mostrarEstado() {
        if (!cola.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("BuzonRevision actual: ");
            for (Product prod : cola) {
                sb.append(prod.toString()).append(" ");
            }
            System.out.println(sb.toString());
        }
    }
    
}
