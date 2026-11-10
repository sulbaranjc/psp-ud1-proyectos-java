package es.ilerna.psp.ud1.reto01;

import java.io.IOException;

/**
 * Reto 01 de UD1.
 * Muestra que un programa, al ejecutarse, se convierte en un proceso.
 */
public class ProcesoActual {

    public static void main(String[] args) throws IOException {
        ProcessHandle actual = ProcessHandle.current();

        System.out.println("=== Reto 01 · Identifica tu proceso ===");
        System.out.println("PID: " + actual.pid());
        System.out.println("PPID: " + actual.parent()
                .map(procesoPadre -> Long.toString(procesoPadre.pid()))
                .orElse("No disponible"));
        System.out.println("Sistema: " + System.getProperty("os.name"));
        System.out.println("Java: " + System.getProperty("java.version"));
        System.out.println();
        System.out.println("Este archivo es un programa.");
        System.out.println("Esta ejecución concreta es un proceso con su propio PID.");
        System.out.println("Pulsa Intro para terminar y poder localizarlo mientras tanto.");

        System.in.read();
    }
}
