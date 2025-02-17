import java.util.LinkedList;
import java.util.Queue;

public class BuzonReproceso {
    // Cola sin límite
    private final Queue<Product> cola = new LinkedList<>();

    public synchronized void depositar(Product p) {
        cola.add(p);
        notifyAll(); // Avisar a los hilos que puedan estar esperando retirar
        mostrarEstado();
    }

    /**
     * Método para que el productor verifique sin quedar esperando.
     */
    public synchronized Product retirar() {
        if (cola.isEmpty()) {
            return null;
        }
        Product p = cola.remove();
        notifyAll();
        mostrarEstado();
        return p;
    }

    public synchronized boolean estaVacio() {
        return cola.isEmpty();
    }
    
    /**
     * Muestra el estado actual del buzón de reproceso solo si no está vacío.
     */
    private void mostrarEstado() {
        if (!cola.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("BuzonReproceso actual: ");
            for (Product prod : cola) {
                sb.append(prod.toString()).append(" ");
            }
            System.out.println(sb.toString());
        }
    }
}
