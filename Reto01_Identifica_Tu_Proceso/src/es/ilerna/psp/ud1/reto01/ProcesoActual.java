// Paquete al que pertenece la clase. Debe coincidir con la ruta de carpetas:
// src/es/ilerna/psp/ud1/reto01/ProcesoActual.java
package es.ilerna.psp.ud1.reto01;

// IOException: error de entrada/salida. Lo necesitamos porque System.in.read()
// (leer del teclado) puede lanzar este error.
import java.io.IOException;

/**
 * Reto 01 de UD1: Identifica tu proceso.
 *
 * <p>Muestra la diferencia entre <b>programa</b> y <b>proceso</b>:</p>
 * <ul>
 *   <li><b>Programa:</b> este archivo de código (algo estático, en disco).</li>
 *   <li><b>Proceso:</b> el programa mientras se ejecuta. El sistema operativo
 *       le asigna memoria, recursos y un identificador único: el PID.</li>
 * </ul>
 *
 * <p>Si ejecutas el programa dos veces a la vez, obtendrás dos procesos
 * distintos, cada uno con su propio PID.</p>
 */
public class ProcesoActual {

    /**
     * Punto de entrada del programa. Cuando lo ejecutas, la JVM (la máquina
     * virtual de Java) arranca como un proceso del sistema operativo.
     *
     * @throws IOException si falla la lectura del teclado.
     */
    public static void main(String[] args) throws IOException {

        // ProcessHandle es la clase de Java que representa un proceso del
        // sistema operativo. current() devuelve el proceso ACTUAL, es decir,
        // esta misma ejecución del programa.
        ProcessHandle actual = ProcessHandle.current();

        // Cabecera del reto.
        System.out.println("=== Reto 01 · Identifica tu proceso ===");

        // pid() devuelve el PID (Process IDentifier): el número que el sistema
        // operativo asigna a este proceso para distinguirlo de los demás.
        // Cambia en cada ejecución.
        System.out.println("PID: " + actual.pid());

        // PPID (Parent PID) es el PID del proceso PADRE: el que nos ha lanzado
        // (normalmente la terminal o IntelliJ).
        //
        // parent() no devuelve directamente el padre, sino un Optional: una
        // "caja" que puede contener el padre o estar vacía (por ejemplo, si el
        // padre ya ha terminado o el sistema no permite consultarlo).
        System.out.println("PPID: " + actual.parent()
                // map(...): si la caja tiene un padre, lo transformamos en el
                // texto de su PID. La lambda "procesoPadre -> ..." recibe el
                // padre y devuelve su PID convertido a String.
                .map(procesoPadre -> Long.toString(procesoPadre.pid()))
                // orElse(...): si la caja está vacía, usamos este texto en su
                // lugar. Así nunca obtenemos un error por falta de padre.
                .orElse("No disponible"));

        // System.getProperty consulta propiedades que la JVM conoce del entorno.
        // "os.name" → nombre del sistema operativo (Linux, Windows 11...).
        System.out.println("Sistema: " + System.getProperty("os.name"));

        // "java.version" → versión de Java con la que se está ejecutando.
        System.out.println("Java: " + System.getProperty("java.version"));

        // Línea en blanco para separar visualmente la salida.
        System.out.println();

        // Mensajes que resumen la idea clave del reto.
        System.out.println("Este archivo es un programa.");
        System.out.println("Esta ejecución concreta es un proceso con su propio PID.");
        System.out.println("Pulsa Intro para terminar y poder localizarlo mientras tanto.");

        // System.in es la entrada estándar (el teclado). read() se queda
        // ESPERANDO hasta que se pulsa Intro.
        //
        // Lo usamos para que el proceso NO termine enseguida: mientras espera,
        // puedes buscarlo con el PID mostrado en otra herramienta:
        //   - Linux/macOS: ps -p <PID>   o   top
        //   - Windows:     tasklist /FI "PID eq <PID>"   o el Administrador de tareas
        //
        // Al pulsar Intro, main() termina, el proceso finaliza y el sistema
        // operativo libera su PID.
        System.in.read();
    }
}
