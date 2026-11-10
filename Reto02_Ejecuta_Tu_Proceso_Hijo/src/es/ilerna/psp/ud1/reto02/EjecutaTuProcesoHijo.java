// Paquete al que pertenece la clase. Debe coincidir con la ruta de carpetas:
// src/es/ilerna/psp/ud1/reto02/EjecutaTuProcesoHijo.java
package es.ilerna.psp.ud1.reto02;

// BufferedReader: permite leer texto línea a línea con readLine().
import java.io.BufferedReader;
// IOException: error de entrada/salida (por ejemplo, si el comando no existe).
import java.io.IOException;
// InputStream: flujo de BYTES. Así nos entrega Java la salida del proceso hijo.
import java.io.InputStream;
// InputStreamReader: "traductor" que convierte bytes en caracteres (texto).
import java.io.InputStreamReader;
// StandardCharsets: constantes de codificación de caracteres (UTF-8, etc.).
import java.nio.charset.StandardCharsets;
// List: lista ordenada. La usamos para guardar el comando y sus argumentos.
import java.util.List;

/**
 * Reto 02 de UD1: Ejecuta tu proceso hijo.
 *
 * <p>Este programa es el <b>proceso padre</b>. Desde él se crea un
 * <b>proceso hijo</b> (un comando del sistema operativo), se leen sus dos
 * canales de salida (stdout y stderr), se espera a que termine y se muestra
 * su código de salida.</p>
 *
 * <p>Recorrido completo:
 * padre → crea hijo → lee stdout/stderr → waitFor() → código de salida.</p>
 */
public class EjecutaTuProcesoHijo {

    /**
     * Punto de entrada del programa. Al ejecutarlo, la JVM se convierte en el
     * proceso padre.
     *
     * @throws IOException          si no se puede crear el proceso hijo.
     * @throws InterruptedException si alguien interrumpe al padre mientras espera.
     */
    public static void main(String[] args) throws IOException, InterruptedException {

        // ---------------------------------------------------------------
        // 1. PREPARAR: averiguar el sistema y construir el comando del hijo
        // ---------------------------------------------------------------

        // Pedimos a la JVM el nombre del sistema operativo: "Linux",
        // "Windows 11", "Mac OS X"... Lo necesitamos porque cada sistema
        // tiene comandos diferentes.
        String sistema = System.getProperty("os.name");

        // Construimos el comando adecuado para ese sistema (ver el método
        // construirComando más abajo). Es una lista: programa + argumentos.
        List<String> comando = construirComando(sistema);

        // Mostramos información del proceso padre antes de crear el hijo.
        System.out.println("=== Reto 02 · Ejecuta tu proceso hijo ===");
        System.out.println("Sistema detectado: " + sistema);

        // ProcessHandle.current() representa el proceso actual (esta JVM).
        // pid() devuelve su identificador de proceso, asignado por el sistema.
        System.out.println("Proceso padre (JVM): PID " + ProcessHandle.current().pid());

        // String.join une los elementos de la lista con espacios, solo para
        // mostrar el comando por pantalla de forma legible.
        System.out.println("Comando del hijo: " + String.join(" ", comando));

        // Línea en blanco para separar visualmente la salida.
        System.out.println();

        // ---------------------------------------------------------------
        // 2. CREAR EL PROCESO HIJO
        // ---------------------------------------------------------------

        // ProcessBuilder es el "molde" del proceso: guarda qué comando
        // ejecutar (y, si quisiéramos, el directorio de trabajo, variables de
        // entorno, redirecciones...). Todavía NO se ha creado ningún proceso.
        ProcessBuilder configuracion = new ProcessBuilder(comando);

        // start() pide al sistema operativo que cree el proceso hijo.
        // A partir de aquí padre e hijo se ejecutan A LA VEZ (en paralelo).
        // El objeto Process nos permite comunicarnos con el hijo y controlarlo.
        Process hijo = configuracion.start();

        // ---------------------------------------------------------------
        // 3. LEER LAS SALIDAS DEL HIJO (cada una en su propio hilo)
        // ---------------------------------------------------------------

        // Todo proceso tiene dos canales de salida:
        //   - stdout (salida estándar): mensajes normales.
        //   - stderr (salida de error): mensajes de error o diagnóstico.
        //
        // OJO con los nombres: desde el punto de vista del PADRE, lo que el
        // hijo ESCRIBE en su stdout es algo que el padre LEE. Por eso el
        // método se llama getInputStream() (entrada para el padre).
        //
        // Usamos un hilo para cada canal. Si leyéramos solo uno y el hijo
        // escribiera mucho en el otro, el búfer del sistema se llenaría y el
        // hijo se quedaría bloqueado esperando a que alguien lo vacíe.
        Thread salidaEstandar = iniciarLector("stdout", hijo.getInputStream());
        Thread salidaError = iniciarLector("stderr", hijo.getErrorStream());

        // ---------------------------------------------------------------
        // 4. ESPERAR A QUE TODO TERMINE
        // ---------------------------------------------------------------

        // waitFor() bloquea al padre hasta que el hijo termina y devuelve su
        // código de salida (exit code). Mientras tanto, los hilos lectores
        // siguen trabajando.
        int codigoSalida = hijo.waitFor();

        // join() espera a que cada hilo lector termine. Es necesario porque
        // el hijo puede haber acabado pero quizá sus últimas líneas aún no se
        // han impreso. Así garantizamos que toda la salida aparece ANTES del
        // resultado final.
        salidaEstandar.join();
        salidaError.join();

        // ---------------------------------------------------------------
        // 5. INTERPRETAR EL CÓDIGO DE SALIDA
        // ---------------------------------------------------------------

        System.out.println();
        System.out.println("El proceso hijo terminó con código: " + codigoSalida);

        // Por convención, 0 significa "todo fue bien" y cualquier otro valor
        // indica algún tipo de error. Usamos el operador ternario
        // (condición ? siVerdadero : siFalso) para elegir el mensaje.
        System.out.println(codigoSalida == 0
                ? "Resultado: ejecución correcta."
                : "Resultado: revisa stderr y el código de salida.");
    }

    /**
     * Construye el comando que ejecutará el proceso hijo según el sistema.
     *
     * <p>El comando se pasa como LISTA (programa, argumento 1, argumento 2...)
     * y no como un único texto. Así no hay dudas sobre dónde empieza y
     * termina cada argumento.</p>
     *
     * @param sistema nombre del sistema operativo (valor de "os.name").
     * @return lista con el programa y sus argumentos.
     */
    private static List<String> construirComando(String sistema) {

        // Pasamos el nombre a minúsculas y buscamos "win" para saber si es
        // Windows (por ejemplo, "Windows 11" → "windows 11" contiene "win").
        if (sistema.toLowerCase().contains("win")) {

            // List.of crea una lista inmutable (no se puede modificar).
            return List.of(
                    // "cmd" es el intérprete de comandos de Windows.
                    "cmd",
                    // "/c" le dice a cmd: ejecuta la orden siguiente y termina.
                    "/c",
                    // La orden completa. En cmd:
                    //   echo ...        → escribe texto en stdout.
                    //   &               → separa una orden de la siguiente.
                    //   1>&2            → redirige stdout (1) hacia stderr (2),
                    //                     así ese echo sale por el canal de error.
                    //   exit /b 0       → termina con código de salida 0.
                    "echo Diagnóstico desde el proceso hijo. & "
                            + "echo Mensaje de diagnóstico por stderr. 1>&2 & exit /b 0"
            );
        }

        // Si no es Windows, suponemos un sistema tipo Unix (Linux, macOS).
        return List.of(
                // "sh" es el intérprete de comandos (shell) estándar de Unix.
                "sh",
                // "-c" le dice a sh: ejecuta el texto siguiente como una orden.
                "-c",
                // La orden completa. En sh:
                //   printf '...\n'  → escribe texto en stdout (\n = salto de línea;
                //                     en Java se escribe \\n para que llegue "\n").
                //   ;               → separa una orden de la siguiente.
                //   >&2             → redirige esa salida hacia stderr.
                //   exit 0          → termina con código de salida 0.
                "printf 'Diagnóstico desde el proceso hijo.\\n'; "
                        + "printf 'Mensaje de diagnóstico por stderr.\\n' >&2; exit 0"
        );
    }

    /**
     * Crea y arranca un hilo que lee un flujo del hijo línea a línea y lo
     * muestra por pantalla con una etiqueta delante ([stdout] o [stderr]).
     *
     * @param canal nombre del canal, solo para la etiqueta ("stdout"/"stderr").
     * @param flujo flujo de bytes del proceso hijo que hay que leer.
     * @return el hilo ya arrancado, para poder esperarlo luego con join().
     */
    private static Thread iniciarLector(String canal, InputStream flujo) {

        // Thread.ofVirtual() crea un HILO VIRTUAL (novedad de Java 21): un hilo
        // muy ligero gestionado por la JVM, ideal para tareas que pasan mucho
        // tiempo esperando datos, como esta.
        //   .name(...)  → le pone un nombre (útil al depurar).
        //   .start(...) → lo arranca inmediatamente con la tarea indicada.
        // La tarea se escribe como una lambda: () -> { ...código... }.
        Thread lector = Thread.ofVirtual().name("lector-" + canal).start(() -> {

            // try-with-resources: lo que se abre entre paréntesis se cierra
            // automáticamente al terminar, incluso si hay un error.
            // Encadenamos: bytes (flujo) → texto UTF-8 (InputStreamReader)
            //              → lectura por líneas (BufferedReader).
            try (BufferedReader entrada = new BufferedReader(
                    new InputStreamReader(flujo, StandardCharsets.UTF_8))) {

                // Variable donde guardamos cada línea leída.
                String linea;

                // readLine() devuelve la siguiente línea, esperando si el hijo
                // aún no la ha escrito. Devuelve null cuando el hijo cierra el
                // canal (normalmente, al terminar). Ahí acaba el bucle.
                while ((linea = entrada.readLine()) != null) {
                    // Mostramos la línea con su etiqueta para saber de qué
                    // canal viene.
                    System.out.println("[" + canal + "] " + linea);
                }

            } catch (IOException error) {
                // Si falla la lectura, lo avisamos por el stderr del PADRE
                // en lugar de detener el programa.
                System.err.println("No se pudo leer " + canal + ": " + error.getMessage());
            }
        });

        // Devolvemos el hilo para que main() pueda esperar a que acabe.
        return lector;
    }
}
