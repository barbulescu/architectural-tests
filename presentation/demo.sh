#!/usr/bin/env bash
# Live-demo helper. Run it from the example to demo (examples/shop-maven or examples/shop-gradle).
# Scenarios only ADD files to that example, so "reset" is always safe.
# Kotlin scenario files are stored as *.kt.scenario: Konsist scans every .kt file in the project.
#
#   ../../presentation/demo.sh list         show the scenarios
#   ../../presentation/demo.sh apply 01     copy a scenario's files into ./src/
#   ../../presentation/demo.sh reset        remove every scenario file and restore the clean freeze baseline
set -euo pipefail
EXAMPLE=$PWD
cd "$(dirname "$0")"
SCENARIOS=scenarios

files_of() { (cd "$1" && find src -type f); }
target_of() { printf '%s\n' "$EXAMPLE/${1%.scenario}"; }

case "${1:-}" in
  apply|reset)
    [ -d "$EXAMPLE/src/main" ] && [ -d "$EXAMPLE/archunit_store" ] \
      || { echo "Run this from an example project, e.g. examples/shop-maven" >&2; exit 1; } ;;
esac

case "${1:-}" in
  list)
    for d in "$SCENARIOS"/*/; do printf '%s\n' "$(basename "$d")"; done ;;
  apply)
    dir=$(ls -d "$SCENARIOS/${2:?scenario number, e.g. 01}"* | head -1)
    files_of "$dir" | while read -r f; do t=$(target_of "$f"); mkdir -p "$(dirname "$t")"; cp "$dir/$f" "$t"; echo "+ ${t#"$EXAMPLE"/}"; done
    echo; sed -n '1,200p' "$dir/README.md" ;;
  reset)
    for d in "$SCENARIOS"/*/; do files_of "$d" | while read -r f; do rm -f "$(target_of "$f")"; done; done
    rm -rf "$EXAMPLE/archunit_store" && cp -R archunit_store.clean "$EXAMPLE/archunit_store"
    echo "Back to the clean code base." ;;
  *)
    sed -n '2,9p' "$0"; exit 1 ;;
esac
