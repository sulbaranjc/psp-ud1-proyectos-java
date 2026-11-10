@echo off
chcp 65001 >nul
cd /d "%~dp0"

where javac >nul 2>nul
if errorlevel 1 (
  echo No se encontró javac. Instala Java 21 LTS y añádelo al PATH.
  exit /b 1
)

if exist out rmdir /s /q out
mkdir out
javac --release 21 -encoding UTF-8 -d out src\es\ilerna\psp\ud1\reto02\EjecutaTuProcesoHijo.java
if errorlevel 1 exit /b 1
java -cp out es.ilerna.psp.ud1.reto02.EjecutaTuProcesoHijo
