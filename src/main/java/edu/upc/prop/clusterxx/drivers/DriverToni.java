package edu.upc.prop.clusterxx.drivers;

import edu.upc.prop.clusterxx.*;
import edu.upc.prop.clusterxx.controladors.CtrDomini;
import edu.upc.prop.clusterxx.exceptions.ExcepcioCasellaBuida;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class DriverToni {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        CtrDomini ctrlDomini = CtrDomini.getInstance();
        inicialitzarPartida(sc, ctrlDomini);
        jugarTorns(sc, ctrlDomini);
    }
    private static void inicialitzarPartida(Scanner sc, CtrDomini ctrDomini) {
        System.out.println("\n======================================");
        System.out.println("         CONFIGURACIÓ DE PARTIDA       ");
        System.out.println("======================================\n");

        // Mida del taulell
        int midaTaulell = 0;
        boolean midaValida = false;
        while (!midaValida) {
            System.out.print("Introdueix la mida del taulell: ");
            midaTaulell = sc.nextInt();

            try {
                // Intentar crear el Taulell
                Taulell taulell = new Taulell(midaTaulell);
                midaValida = true;  // Si no lanza excepción, se considera válida
            } catch (IllegalArgumentException e) {
                // Atrapar la excepción y mostrar un mensaje al usuario
                System.out.println(e.getMessage());
                // El bucle continuará pidiendo la medida del tablero
            }
        }

        // Mida del faristol
        System.out.print("Introdueix la mida del faristol: ");
        int midaFaristol = sc.nextInt();
        while (midaFaristol < 1) {
            System.out.print("La mida del faristol ha de ser positiva. Torna-ho a intentar: ");
            midaFaristol = sc.nextInt();
        }

        // Idioma
        System.out.println("Idiomes/Temàtiques disponibles: ");
        System.out.println("castellano");
        System.out.println("catalan");
        System.out.println("english");
        System.out.print("Introdueix l'idioma/temàtica: ");
        sc.nextLine(); // Consumir salt de línia
        String idioma = sc.nextLine();

        while (!idioma.equals("castellano") && !idioma.equals("catalan") && !idioma.equals("english")) {
            System.out.print("Idioma no vàlid. Torna-ho a intentar: ");
            idioma = sc.nextLine();
        }

        // Bots
        System.out.print("Introdueix el nombre de bots: ");
        int numBots = sc.nextInt();
        while (numBots < 0) {
            System.out.print("El nombre de bots ha de ser positiu. Torna-ho a intentar: ");
            numBots = sc.nextInt();
        }

        int[] dificultatsBots = new int[numBots];
        for (int i = 0; i < numBots; i++) {
            System.out.print("   Dificultat del Bot " + (i + 1) + " (1-Fàcil, 2-Normal, 3-Difícil): ");
            dificultatsBots[i] = sc.nextInt();
            while(dificultatsBots[i] < 1 || dificultatsBots[i] > 3) {
                System.out.print("Dificultat no vàlida. Torna-ho a intentar: ");
                dificultatsBots[i] = sc.nextInt();
            }
        }

        // Jugadors humans
        System.out.print("Introdueix el nombre de jugadors (persones): ");
        int numJugadors = sc.nextInt();
        while (numJugadors < 0) {
            System.out.print("El nombre de jugadors ha de ser positiu. Torna-ho a intentar: ");
            numJugadors = sc.nextInt();
        }

        sc.nextLine(); // Consumir salt de línia
        String[] nomsJugadors = new String[numJugadors];
        for (int i = 0; i < numJugadors; i++) {
            System.out.println("\n--- Jugador " + (i + 1) + " ---");
            System.out.print("Nom: ");
            nomsJugadors[i] = sc.nextLine();
        }

        // Mostrar configuració
        System.out.println("\n======================================");
        System.out.println("        CONFIGURACIÓ COMPLETADA        ");
        System.out.println("======================================");
        System.out.println("Taulell: " + midaTaulell);
        System.out.println("Faristol: " + midaFaristol);
        System.out.println("Idioma: " + idioma);
        System.out.println("Jugadors totals: " + (numBots + numJugadors));

        // Mostrar bots
        for (int i = 0; i < numBots; i++) {
            System.out.println("  - Bot \"Bot" + (i + 1) + "\" amb dificultat " + dificultatsBots[i]);
        }

        // Mostrar jugadors humans
        for (String nomsJugador : nomsJugadors) {
            System.out.println("  - Persona \"" + nomsJugador + "\"");
        }

        System.out.println("======================================\n");

        // Confirmació
        System.out.print("Vols jugar amb aquesta configuració? (true/false): ");
        String confirmacio = sc.nextLine();
        while (!confirmacio.equals("true") && !confirmacio.equals("false")) {
            System.out.print("Resposta no vàlida. Torna-ho a intentar (true/false): ");
            confirmacio = sc.nextLine();
        }
        boolean jugar;
        jugar = confirmacio.equals("true");

        if (!jugar) {
            System.out.println("\nTornant a la configuració inicial...\n");
            inicialitzarPartida(sc, ctrDomini);
            return;
        }

        System.out.print("Inicialitzan partida...\n");

        // Inicialitzar partida (només amb noms de jugadors humans i dificultats dels bots)
        ctrDomini.inicialitzarPartida(midaTaulell, midaFaristol, idioma, nomsJugadors, dificultatsBots); //Aqui el ctrDomini creará el Taulell lo que disparará la excepción
    }




    private static void jugarTorns(Scanner sc, CtrDomini ctrlDomini) {
        boolean first = true; // Variable per controlar si és el primer torn
        while (!ctrlDomini.esFinalDePartida()) {
            imprimirSeparador();
            boolean passatorn = false;
            int torn = ctrlDomini.obtenirTorn();

            while (torn == ctrlDomini.obtenirTorn()) {
                imprimirPrincipiTorn(ctrlDomini);
                Jugador jugadorActual = ctrlDomini.obtenirJugadorActual();
                if (jugadorActual.esBot()) {
                    Jugada jugadabot = ctrlDomini.jugadaBot();
                    imprimirJugada(jugadabot);
                    imprimirFaristol(ctrlDomini.obtenirJugadorActual().obtenirFaristol());
                    passatorn = true;
                    if (first) first = false; // Si el bot juga, ja no és el primer torn
                }
                else {
                    int opcio;
                    if (!first) {
                        System.out.print("Que vols fer? (1 = jugar, 2 = passar torn, 3 = canviar fitxes, 4 = undo, 5 = guadar i sortir, 6 = sortir sense guardar): ");
                        opcio = sc.nextInt();
                    }
                    else {
                        System.out.print("Que vols fer?. (1 = jugar, 5 = guadar i sortir, 6 = sortir sense guardar): ");
                        opcio = sc.nextInt();
                        first = false;
                    }


                    switch (opcio) {
                        case 1 -> passatorn = jugarParaula(sc, ctrlDomini);
                        case 2 -> {
                            ctrlDomini.passarTorn();
                            passatorn = true;
                        }
                        case 3 -> {
                            canviarFitxes(sc, ctrlDomini);
                            passatorn = true;
                        }
                        case 4 -> {
                            if (ctrlDomini.esPotFerUndo()) {
                                ctrlDomini.ferUndo();
                                passatorn = true;
                            }
                            else {
                                System.out.println("No es pot fer undo en aquest moment.");
                            }
                        }
                        case 5 -> {
                            System.out.println("Guardant partida...");
                            ctrlDomini.guardarPartida();
                            System.out.println("Partida guardada.");
                            return;
                        }
                        case 6 -> {
                            System.out.println("Sortint sense guardar...");
                            return;
                        }
                        default -> System.out.println("Opció no vàlida. Torna a intentar-ho.");
                    }
                }
            }
            imprimirSeparador();
        }
        if (ctrlDomini.esFinalDePartida()) {
            System.out.println(Colors.YELLOW_BACKGROUND + Colors.BLACK_TEXT + "======== PARTIDA ACABADA ========" + Colors.RESET);
            imprimirTaulell(ctrlDomini.obtenirTaulell());
        }
    }

    private static void imprimirPrincipiTorn(CtrDomini ctrlDomini) {
        imprimirSeparador();
        imprimirSeparador();
        System.out.println(Colors.YELLOW_BACKGROUND + Colors.BLACK_TEXT + "======== INICI DEL TORN"+ ctrlDomini.obtenirTorn() +"========" + Colors.RESET);
        System.out.println(Colors.YELLOW_TEXT + "Jugador: " + Colors.RESET + ctrlDomini.obtenirJugadorActual().obtenirNom());
        System.out.println(Colors.CYAN_TEXT + "Punts: " + Colors.RESET + ctrlDomini.obtenirJugadorActual().obtenirPunts());
        if (!ctrlDomini.obtenirJugadorActual().esBot()) {
            System.out.println(Colors.YELLOW_TEXT + "Faristol: " + Colors.RESET);
            imprimirFaristol(ctrlDomini.obtenirJugadorActual().obtenirFaristol());
            imprimirTaulell(ctrlDomini.obtenirTaulell());
        }
        imprimirSeparador();
        imprimirSeparador();

    }

    private static boolean jugarParaula(Scanner sc, CtrDomini ctrlDomini) {
        boolean commit = false;
        int torn = ctrlDomini.obtenirTorn();

        while (torn == ctrlDomini.obtenirTorn()) {

            System.out.print("Vols col·locar o retirar una fitxa? (1 = col·locar, 2 = retirar, 3 = reiniciar torn): ");
            int opcio = sc.nextInt();
            sc.nextLine(); // Netejar buffer

            switch (opcio) {
                case 1 -> {
                    imprimirSeparador();
                    boolean fitxacolocada = gestionarColocacio(sc, ctrlDomini);
                    if (fitxacolocada) {
                        ImprimirInfo(ctrlDomini);
                    }
                    else imprimirFaristol(ctrlDomini.obtenirJugadorActual().obtenirFaristol());
                }
                case 2 -> {
                    retirarFitxa(sc, ctrlDomini);
                }
                case 3 -> {
                    resetTorn(ctrlDomini);
                }
                default -> System.out.println("Opció no vàlida. Torna-ho a intentar.");
            }

        }

        return true;
    }

    private static void resetTorn(CtrDomini ctrlDomini) {
        try {
            ctrlDomini.resetTorn();
            imprimirPrincipiTorn(ctrlDomini);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("No es pot reiniciar el torn. Torna-ho a intentar.");
        }
    }

    private static void ImprimirInfo(CtrDomini ctrlDomini) {
        imprimirSeparador();
        imprimirTaulell(ctrlDomini.obtenirTaulell());
        System.out.println(Colors.YELLOW_TEXT + "Faristol: " + Colors.RESET);
        imprimirFaristol(ctrlDomini.obtenirJugadorActual().obtenirFaristol());
        imprimirSeparador();
    }

    private static void retirarFitxa(Scanner sc, CtrDomini ctrlDomini) {
        System.out.print("Introdueix la fila de la fitxa que vols retirar: ");
        int fila = sc.nextInt();
        System.out.print("Introdueix la columna de la fitxa que vols retirar: ");
        int columna = sc.nextInt();
        sc.nextLine(); // Netejar buffer
        try {
            ctrlDomini.retirarFitxa(fila, columna);
            imprimirTaulell(ctrlDomini.obtenirTaulell());
            imprimirFaristol(ctrlDomini.obtenirJugadorActual().obtenirFaristol());
        }
        catch (ExcepcioCasellaBuida e){
            System.out.print ("Error: " + e.getMessage() + "\n");
        }

    }

    private static boolean gestionarColocacio(Scanner sc, CtrDomini ctrlDomini) {
        System.out.print("Escriu la lletra de la fitxa que vols jugar: ");
        String lletra = sc.nextLine();

        if (lletra.equals("#")) {
            lletra = gestionarComodi(sc, ctrlDomini);
            if (lletra == null) return false; // No hi havia comodí
        }

        System.out.print("Introdueix la fila on vols col·locar la fitxa: ");
        int fila = sc.nextInt();
        System.out.print("Introdueix la columna on vols col·locar la fitxa: ");
        int columna = sc.nextInt();
        sc.nextLine(); // Netejar buffer

        try {
            Jugada jugada = ctrlDomini.colocarFitxa(lletra, fila, columna);

            if (jugada.getJugadaValida()) {
                System.out.print("Vols confirmar la jugada? (true/false): ");
                boolean confirmacio = sc.nextBoolean();
                sc.nextLine();

                if (confirmacio) {
                    imprimirJugada(jugada);
                    ctrlDomini.commitParaula();
                }

            }
        }
        catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
            System.out.print("Intenta-ho de nou. Tria que vols fer: \n");
            return false;
        }

        return true;
    }

    private static String gestionarComodi(Scanner sc, CtrDomini ctrlDomini) {
        System.out.print("Per quina lletra vols substituir el comodí? ");
        Fitxa fitxa = null;
        try {
            fitxa = ctrlDomini.obtenirJugadorActual().obtenirFitxa("#");
        } catch (IllegalArgumentException e) {
            System.out.print("No tens cap comodí disponible. ");
            return null;
        }


        while (true) {
            String lletra = sc.nextLine().toUpperCase();
            if (ctrlDomini.setLletraComodi(fitxa, lletra)) {
                return lletra;
            } else {
                System.out.println("No hi ha cap paraula amb la lletra '" + lletra + "' al diccionari.");
                System.out.print("Introdueix una altra lletra: ");
            }
        }
    }




    private static void canviarFitxes(Scanner sc, CtrDomini ctrlDomini) {
        System.out.print("Tria quantes fitxes vols canviar: ");
        int numFitxes = sc.nextInt();
        while (numFitxes < 1 || numFitxes > ctrlDomini.obtenirJugadorActual().obtenirFaristol().obtenirNumFitxes()) {
            System.out.print("El nombre de fitxes ha de ser entre 1 i " + ctrlDomini.obtenirJugadorActual().obtenirFaristol().obtenirNumFitxes() + ". Torna-ho a intentar: ");
            numFitxes = sc.nextInt();
        }
        sc.nextLine();
        boolean canviFet = false;

        while (!canviFet) {
            String[] fitxes = new String[numFitxes];
            for (int i = 0; i < numFitxes; ++i) {
                System.out.print("Tria la fitxa que vols canviar: ");
                fitxes[i] = sc.nextLine();
            }
            Arrays.sort(fitxes);
            try {
                ctrlDomini.canviarFitxes(fitxes);
                canviFet = true;
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
                System.out.print("Intenta-ho de nou. Tria les fitxes que vols canviar: ");

            }
        }
    }

    private static void imprimirJugada(Jugada jugada) {
        System.out.println("\n🎯 Jugada realitzada:");
        System.out.println(" - Paraula formada: " + jugada.getParaulaFormada());
        System.out.println(" - Puntuació: " + jugada.getPuntuacio());
        System.out.println(" - És vàlida? " + (jugada.getJugadaValida() ? "Sí" : "No"));
        System.out.println(" - Caselles jugades:");

        for (Casella casella : jugada.getCasellesJugades()) {
            System.out.println("     · " + casella);
        }
    }

    private static void imprimirTorn(CtrDomini ctrlDomini) {
        Jugador jugadorActual = ctrlDomini.obtenirJugadorActual();
        System.out.println(Colors.YELLOW_BACKGROUND + Colors.BLACK_TEXT + "======== TORN DE " + jugadorActual.obtenirNom() + " ========" + Colors.RESET);
        System.out.println(Colors.CYAN_TEXT + "Punts: " + Colors.RESET + jugadorActual.obtenirPunts());
    }

    private static void imprimirTaulell(Taulell taulell) {
        System.out.println("🎨 Llegenda del Taulell:");
        System.out.println("\033[41m   \033[0m → Multiplicador de PARAULA x3");
        System.out.println("\033[45m   \033[0m → Multiplicador de PARAULA x2");
        System.out.println("\033[44m   \033[0m → Multiplicador de LLETRA x3");
        System.out.println("\033[46m   \033[0m → Multiplicador de LLETRA x2");
        System.out.println("\033[107m   \033[0m → Casella normal");

        int size = taulell.getSize();

        // Imprimir índices de columna con padding
        System.out.print("     "); // Espacio para alinear con los números de fila
        for (int i = 0; i < size; i++) {
            System.out.printf(" %2d  ", i);
        }
        System.out.println();

        // Línea superior
        System.out.print("    ");
        for (int i = 0; i < size; i++) System.out.print("+----");
        System.out.println("+");

        // Filas del taulell
        for (int i = 0; i < size; ++i) {
            System.out.printf("%2d  ", i); // Índice de fila
            for (int j = 0; j < size; ++j) {
                Casella casella = taulell.getTaulell()[i][j];
                String colorFons = obtenirColorFons(casella);
                System.out.print("|" + colorFons + Colors.BLACK_TEXT + " " + casella + " " + Colors.RESET);
            }
            System.out.println("|");

            // Línea divisoria
            System.out.print("    ");
            for (int j = 0; j < size; j++) System.out.print("+----");
            System.out.println("+");
        }
    }
    private static String obtenirColorFons(Casella casella) {
        if (casella.obtenirEstrategia() instanceof EstrategiaPuntuacio.EstrategiaMultiplicadorParaula estrategia) {
            return estrategia.obtenirMultiplicador() == 3 ? Colors.RED_BACKGROUND : Colors.PURPLE_BACKGROUND;
        } else if (casella.obtenirEstrategia() instanceof EstrategiaPuntuacio.EstrategiaMultiplicadorLletra estrategia) {
            return estrategia.obtenirMultiplicador() == 3 ? Colors.BLUE_BACKGROUND : Colors.CYAN_BACKGROUND;
        } else {
            return "\033[107m";
        }
    }

    public static void imprimirFaristol(Faristol faristol) {
        List<Fitxa> fitxes = faristol.obtenirFitxes();
        System.out.print("[");
        for (int i = 0; i < fitxes.size(); i++) {
            Fitxa fitxa = fitxes.get(i);
            System.out.print("[" + fitxa + " " + Colors.YELLOW_TEXT + fitxa.obtenirPunts() + Colors.RESET + "]");
            if (i < fitxes.size() - 1) System.out.print(" ");
        }
        System.out.println("]");
    }

    private static void imprimirSeparador() {
        System.out.println("================================================");
    }
}
