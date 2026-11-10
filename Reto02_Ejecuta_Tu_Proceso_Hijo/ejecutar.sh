#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")"

if ! command -v javac >/dev/null 2>&1; then
  echo "No se encontró javac. Instala Java 21 LTS y añádelo al PATH." >&2
  exit 1
fi

rm -rf out
mkdir -p out
javac --release 21 -encoding UTF-8 -d out src/es/ilerna/psp/ud1/reto02/EjecutaTuProcesoHijo.java
java -cp out es.ilerna.psp.ud1.reto02.EjecutaTuProcesoHijo
