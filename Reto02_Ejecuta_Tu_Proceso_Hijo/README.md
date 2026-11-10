# Reto 02 · Ejecuta tu proceso hijo

Proyecto Java de consola para la **UD1: Programación concurrente, multiproceso y paralelismo** de **Programación de Servicios y Procesos**.

## Idea que demuestra

Un proceso Java puede crear y controlar otro proceso mediante `ProcessBuilder`. El programa principal actúa como **proceso padre** y ejecuta un comando del sistema como **proceso hijo**. Después recoge sus dos canales de salida, espera a que termine y comprueba su código de salida.

El aula trabaja con **Java 21 LTS**.

## Objetivos

1. Detectar si el equipo usa Windows o un sistema tipo Unix.
2. Construir un comando adecuado para ese sistema.
3. Crear el proceso hijo con `ProcessBuilder`.
4. Leer `stdout` y `stderr` por separado sin bloquear el proceso hijo.
5. Esperar con `waitFor()` e interpretar el código de salida.

## Ejecutar

### Linux / macOS

Desde esta carpeta:

```bash
./ejecutar.sh
```

### Windows

Desde esta carpeta, en **Símbolo del sistema (cmd)**:

```bat
ejecutar.bat
```

En **PowerShell**:

```powershell
.\ejecutar.bat
```

Los scripts comprueban `javac`, compilan con Java 21 en `out/` y ejecutan el programa.

Si prefieres hacerlo a mano:

```bash
javac --release 21 -encoding UTF-8 -d out src/es/ilerna/psp/ud1/reto02/EjecutaTuProcesoHijo.java
java -cp out es.ilerna.psp.ud1.reto02.EjecutaTuProcesoHijo
```

## Salida esperada

Los PID y el texto exacto del sistema variarán, pero deben aparecer estos bloques:

```text
=== Reto 02 · Ejecuta tu proceso hijo ===
Sistema detectado: Linux
Proceso padre (JVM): PID ...
Comando del hijo: ...

[stdout] Diagnóstico desde el proceso hijo.
[stderr] Mensaje de diagnóstico por stderr.

El proceso hijo terminó con código: 0
```

`stdout` y `stderr` llegan en orden independiente porque se consumen en dos hilos distintos. Eso es normal: la garantía del reto es que ambos canales se leen y que el código de salida se comprueba.

## Qué observar

| Elemento | Qué significa |
|---|---|
| `ProcessBuilder` | Configura el comando sin concatenar una orden completa en un único texto. |
| `start()` | Crea el proceso hijo. |
| `getInputStream()` | El padre lee por aquí el `stdout` del hijo. |
| `getErrorStream()` | El padre lee por aquí el `stderr` del hijo. |
| `waitFor()` | El padre espera la terminación del hijo. |
| Código `0` | Ejecución correcta por convención. |

## Reto de ampliación

1. Añade el argumento `--fallo` para que el hijo termine con código distinto de cero.
2. Registra en un archivo el comando, ambos flujos y el código de salida.
3. Sustituye el comando de diagnóstico por uno que reciba datos por la entrada estándar.
4. Explica por qué leer solo `stdout` puede bloquear un hijo que escriba mucho en `stderr`.

## Evidencias de aprendizaje

- Código fuente legible y comentado.
- Captura o registro de una ejecución correcta.
- Explicación breve del recorrido `padre → hijo → stdout/stderr → waitFor() → exit code`.

## Estructura deliberadamente simple

- Un único archivo Java: `EjecutaTuProcesoHijo.java`.
- Sin dependencias externas, Maven ni Gradle.
- Dos hilos de lectura, necesarios para que los flujos se consuman de forma independiente.
- El comando usa solo texto fijo del propio ejemplo; en una aplicación real, nunca se deben concatenar datos no confiables en órdenes del sistema.

## Abrir en IntelliJ IDEA

Abre la carpeta padre `Proyectos_Java_Ejecutables` como proyecto y ejecuta el método `main` de `EjecutaTuProcesoHijo`.
