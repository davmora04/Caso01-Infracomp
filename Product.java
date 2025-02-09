public class Product {
    private final String id;     // Ej: "P0_1234"
    private final boolean fin;   // true si es FIN

    public Product(String id, boolean fin) {
        this.id = id;
        this.fin = fin;
    }

    public String getId() {
        return id;
    }

    public boolean isFin() {
        return fin;
    }

    @Override
    public String toString() {
        return fin ? "[FIN]" : "[Producto " + id + "]";
    }
}
