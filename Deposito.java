import java.util.ArrayList;
import java.util.List;

public class Deposito {
    private final List<Product> productosAprobados = new ArrayList<>();
    private int cantidadAprobados = 0;
    

    /**
     * Agrega el producto y retorna el número total de aprobados.
     */
    public synchronized int depositarConConteo(Product p) {
        productosAprobados.add(p);
        cantidadAprobados++;
        return cantidadAprobados;
    }

    public synchronized int getCantidadAprobados() {
        return cantidadAprobados;
    }

    public synchronized List<Product> getProductosAprobados() {
        return new ArrayList<>(productosAprobados);
    }
}