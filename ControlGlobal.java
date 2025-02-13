public class ControlGlobal {
    private volatile boolean fin = false;

    public boolean isFin() {
        return fin;
    }

    public void setFin(boolean fin) {
        this.fin = fin;
    }
}
