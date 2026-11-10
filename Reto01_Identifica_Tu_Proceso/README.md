# Reto 01 · Identifica tu proceso

Proyecto Java de consola para la **UD1: Programación concurrente, multiproceso y paralelismo** de Programación de Servicios y Procesos.

## Idea que demuestra

Un **programa** es el archivo `ProcesoActual.java`. Cada vez que se ejecuta, el sistema crea un **proceso** distinto, con un PID propio y, normalmente, un proceso padre identificado por PPID.

El código usa `ProcessHandle`, disponible desde Java 9. El aula trabaja con Java 21 LTS.

## Ejecutar

El programa queda esperando a que se pulse Intro. **No lo cierres**: mientras está activo, anota su PID y localízalo desde otra terminal (ver la sección siguiente).

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

En **PowerShell** hay que indicar la carpeta actual:

```powershell
.\ejecutar.bat
```

También funciona con doble clic sobre `ejecutar.bat` en el Explorador de archivos.

El script pone la consola en UTF-8 (`chcp 65001`) para que las tildes se vean bien, comprueba que `javac` está en el PATH, compila en `out\` y lanza el programa.

Si prefieres hacerlo a mano:

```bat
chcp 65001
javac --release 21 -encoding UTF-8 -d out src\es\ilerna\psp\ud1\reto01\ProcesoActual.java
java -cp out es.ilerna.psp.ud1.reto01.ProcesoActual
```

### Salida esperada (en cualquier sistema)

```
=== Reto 01 · Identifica tu proceso ===
PID: 8124
PPID: 6340
Sistema: Windows 11
Java: 21.0.x

Este archivo es un programa.
Esta ejecución concreta es un proceso con su propio PID.
Pulsa Intro para terminar y poder localizarlo mientras tanto.
```

Los números de PID y PPID serán distintos en cada equipo y en cada ejecución. En Linux, `Sistema` mostrará `Linux`.

## Localizar el proceso en el sistema operativo

Abre **otra** terminal sin cerrar la primera y sustituye `PID_ANOTADO` por el PID que mostró tu programa.

### Linux / macOS

```bash
ps -p PID_ANOTADO -o pid,ppid,stat,comm
```

Salida de ejemplo:

```
    PID    PPID STAT COMMAND
 549702  541332 Sl   java
```

| Columna | Significado |
|---|---|
| **PID** | Identificador que el sistema asignó a esta ejecución. Coincide con el `PID:` que mostró el programa. |
| **PPID** | PID del proceso padre, el que lo lanzó: el `bash` de `ejecutar.sh` o el IDE si lo ejecutaste desde IntelliJ. |
| **STAT** | Estado del proceso. `S` = *sleeping* (en espera, bloqueado en `System.in.read()` hasta que pulses Intro). `l` = *multithreaded* (tiene varios hilos). |
| **COMMAND** | Ejecutable que corre: `java`, es decir, la JVM. Tu clase `ProcesoActual` es el código que esa JVM carga y ejecuta. |

Comandos para ver más detalle:

```bash
# Línea de comandos completa (aquí sí aparece ProcesoActual)
ps -p PID_ANOTADO -o pid,ppid,stat,args

# Número de hilos del proceso (la JVM crea muchos además de main)
ls /proc/PID_ANOTADO/task | wc -l

# Quién es el padre
ps -p PPID_ANOTADO -o pid,ppid,comm

# Cadena completa de antepasados hasta systemd (PID 1)
pstree -p -s PID_ANOTADO
```

### Windows

Windows no tiene `ps`. Hay tres formas de localizar el proceso.

#### 1. `tasklist` (cmd o PowerShell)

```bat
tasklist /FI "PID eq PID_ANOTADO"
```

Salida de ejemplo:

```
Nombre de imagen               PID Nombre de sesión Núm. de ses Uso de memor
========================= ======== ================ =========== ============
java.exe                      8124 Console                    1     42.316 KB
```

Confirma que el proceso existe y que su imagen es `java.exe` (la JVM), pero **no muestra el PPID**.

#### 2. PowerShell con `Get-CimInstance` (recomendado, equivale a `ps`)

```powershell
Get-CimInstance Win32_Process -Filter "ProcessId=PID_ANOTADO" |
    Select-Object ProcessId, ParentProcessId, Name, ThreadCount, CommandLine |
    Format-List
```

Salida de ejemplo:

```
ProcessId       : 8124
ParentProcessId : 6340
Name            : java.exe
ThreadCount     : 19
CommandLine     : java -cp out es.ilerna.psp.ud1.reto01.ProcesoActual
```

| Campo | Equivalente en Linux | Significado |
|---|---|---|
| **ProcessId** | PID | Coincide con el `PID:` que mostró el programa. |
| **ParentProcessId** | PPID | Proceso que lo lanzó: el `cmd.exe` que ejecuta `ejecutar.bat` (también desde PowerShell), `powershell.exe` si lo lanzaste a mano, o `idea64.exe` (IntelliJ). |
| **Name** | COMMAND | `java.exe`: la JVM. |
| **ThreadCount** | `l` en STAT | Número de hilos. Aunque tu `main` solo usa uno, la JVM crea muchos más (recolector de basura, compilador JIT…). |
| **CommandLine** | `args` | Línea de comandos completa, donde aparece `ProcesoActual`. |

Para saber quién es el padre:

```powershell
Get-CimInstance Win32_Process -Filter "ProcessId=PPID_ANOTADO" |
    Select-Object ProcessId, ParentProcessId, Name
```

Repitiendo con cada `ParentProcessId` se sube por la cadena de antepasados. A diferencia de Linux, en Windows no hay un antepasado común como `systemd`, y es posible que el padre ya no exista.

Para ver el **estado** de los hilos (en Windows el estado es de cada hilo, no del proceso):

```powershell
(Get-Process -Id PID_ANOTADO).Threads |
    Group-Object ThreadState | Select-Object Count, Name
```

Casi todos aparecerán como `Wait`: el equivalente al `S` de Linux, porque el programa está bloqueado esperando a que pulses Intro.

#### 3. Administrador de tareas (interfaz gráfica)

`Ctrl` + `Mayús` + `Esc` → pestaña **Detalles** → busca `java.exe` en la columna **PID**. Con clic derecho sobre los encabezados → **Seleccionar columnas** puedes añadir **Subprocesos** (hilos) y **Línea de comandos**.

## Segunda ejecución

Ejecuta el programa una segunda vez en otra terminal. Debe mostrar un PID diferente: es la evidencia de que son dos procesos distintos creados desde el mismo programa. Compruébalo con los comandos anteriores: verás dos procesos `java` / `java.exe` a la vez.

## Estructura deliberadamente simple

- Un solo archivo Java: `ProcesoActual.java`.
- Sin pruebas unitarias, frameworks ni dependencias externas.
- Sin Gradle ni Maven: la atención queda en la relación programa → proceso → PID/PPID.
- La comprobación es observable directamente en la consola y en el sistema operativo.

## Abrir en IntelliJ IDEA

Abre la carpeta padre `Proyectos_Java_Ejecutables` como proyecto y ejecuta el método `main` de `ProcesoActual`. En ese caso, el PPID será el proceso del IDE (`idea64.exe` en Windows, `java` o `idea` en Linux).
