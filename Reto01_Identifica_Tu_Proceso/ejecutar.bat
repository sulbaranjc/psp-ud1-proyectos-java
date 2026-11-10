@echo off
setlocal

rem Consola en UTF-8 para que se vean bien las tildes y el caracter "·"
chcp 65001 >nul

set "ROOT=%~dp0"
set "OUT=%ROOT%out"
set "SOURCE=%ROOT%src\es\ilerna\psp\ud1\reto01\ProcesoActual.java"

where javac >nul 2>nul
if errorlevel 1 (
    echo No se encuentra javac. Instala el JDK 21 y añádelo al PATH.
    pause
    exit /b 1
)

if exist "%OUT%" rmdir /s /q "%OUT%"
mkdir "%OUT%"

javac --release 21 -encoding UTF-8 -d "%OUT%" "%SOURCE%"
if errorlevel 1 (
    echo Error de compilación.
    pause
    exit /b 1
)

java -cp "%OUT%" es.ilerna.psp.ud1.reto01.ProcesoActual

endlocal
