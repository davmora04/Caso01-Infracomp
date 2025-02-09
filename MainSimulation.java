import java.util.Scanner;

public class MainSimulation {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.print("Ingrese el número de productores: ");
        int numProductores = sc.nextInt();

        System.out.print("Ingrese el número de inspectores (equipo de calidad): ");
        int numInspectores = sc.nextInt();

        System.out.print("Ingrese la cantidad total de productos que deben llegar al depósito: ");
        int totalEsperado = sc.nextInt();

        System.out.print("Ingrese la capacidad (límite) del buzón de revisión: ");
        int capacidadRevision = sc.nextInt();

        sc.close();

        System.out.println("\n--- Iniciando Simulación ---");
        System.out.println("Productores: " + numProductores
                + ", Inspectores: " + numInspectores
                + ", TotalEsperado: " + totalEsperado
                + ", CapacidadBuzonRevision: " + capacidadRevision);

        // 1) Instanciar buzones y depósito
        BuzonReproceso buzRepro = new BuzonReproceso();
        BuzonRevision buzRev = new BuzonRevision(capacidadRevision);
        Deposito deposito = new Deposito();

        // 2) Crear y arrancar productores
        Productor[] productores = new Productor[numProductores];
        for (int i = 0; i < numProductores; i++) {
            productores[i] = new Productor(i, buzRepro, buzRev);
            productores[i].start();
        }

        // 3) Crear y arrancar inspectores
        InspectorCalidad[] inspectores = new InspectorCalidad[numInspectores];
        for (int j = 0; j < numInspectores; j++) {
            inspectores[j] = new InspectorCalidad(j, buzRev, buzRepro, deposito, totalEsperado);
            inspectores[j].start();
        }

        // 4) Esperar a que todos los inspectores terminen
        for (InspectorCalidad ic : inspectores) {
            try {
                ic.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // 5) Una vez no haya más inspectores, los productores deberían terminar 
        //    en cuanto vean el FIN en el BuzonReproceso (o se queden bloqueados 
        //    si no hay FIN). Esperamos a que terminen.
        for (Productor p : productores) {
            try {
                p.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // 6) Reporte final
        System.out.println("\n--- Simulación finalizada ---");
        int aprobados = deposito.getCantidadAprobados();
        System.out.println("Total productos en el depósito: " + aprobados + " (meta era " + totalEsperado + ")");
        System.out.println("Productos aprobados:");
        deposito.getProductosAprobados().forEach(pr -> System.out.println("   " + pr));
    }
}