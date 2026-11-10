#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
OUT="$ROOT/out"
SOURCE="$ROOT/src/es/ilerna/psp/ud1/reto01/ProcesoActual.java"

rm -rf "$OUT"
mkdir -p "$OUT"
javac --release 21 -encoding UTF-8 -d "$OUT" "$SOURCE"
java -cp "$OUT" es.ilerna.psp.ud1.reto01.ProcesoActual
