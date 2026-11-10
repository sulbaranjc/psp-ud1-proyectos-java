package es.ilerna.psp.ud1.reto02;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Reto 02 de UD1.
 * Crea un proceso hijo portable, consume stdout y stderr por separado,
 * espera su terminación y muestra el código de salida.
 */
public class EjecutaTuProcesoHijo {

    public static void main(String[] args) throws IOException, InterruptedException {
        String sistema = System.getProperty("os.name");
        List<String> comando = construirComando(sistema);

        System.out.println("=== Reto 02 · Ejecuta tu proceso hijo ===");
        System.out.println("Sistema detectado: " + sistema);
        System.out.println("Proceso padre (JVM): PID " + ProcessHandle.current().pid());
        System.out.println("Comando del hijo: " + String.join(" ", comando));
        System.out.println();

        ProcessBuilder configuracion = new ProcessBuilder(comando);
        Process hijo = configuracion.start();

        Thread salidaEstandar = iniciarLector("stdout", hijo.getInputStream());
        Thread salidaError = iniciarLector("stderr", hijo.getErrorStream());

        int codigoSalida = hijo.waitFor();
        salidaEstandar.join();
        salidaError.join();

        System.out.println();
        System.out.println("El proceso hijo terminó con código: " + codigoSalida);
        System.out.println(codigoSalida == 0
                ? "Resultado: ejecución correcta."
                : "Resultado: revisa stderr y el código de salida.");
    }

    private static List<String> construirComando(String sistema) {
        if (sistema.toLowerCase().contains("win")) {
            return List.of(
                    "cmd", "/c",
                    "echo Diagnóstico desde el proceso hijo. & "
                            + "echo Mensaje de diagnóstico por stderr. 1>&2 & exit /b 0"
            );
        }

        return List.of(
                "sh", "-c",
                "printf 'Diagnóstico desde el proceso hijo.\\n'; "
                        + "printf 'Mensaje de diagnóstico por stderr.\\n' >&2; exit 0"
        );
    }

    private static Thread iniciarLector(String canal, InputStream flujo) {
        Thread lector = Thread.ofVirtual().name("lector-" + canal).start(() -> {
            try (BufferedReader entrada = new BufferedReader(
                    new InputStreamReader(flujo, StandardCharsets.UTF_8))) {
                String linea;
                while ((linea = entrada.readLine()) != null) {
                    System.out.println("[" + canal + "] " + linea);
                }
            } catch (IOException error) {
                System.err.println("No se pudo leer " + canal + ": " + error.getMessage());
            }
        });
        return lector;
    }
}
