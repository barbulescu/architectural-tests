#!/usr/bin/env bash
# Live-demo helper. Scenarios only ADD files, so "reset" is always safe.
# Kotlin scenario files are stored as *.kt.scenario: Konsist scans every .kt file in the project.
#
#   ./demo.sh list            show the scenarios
#   ./demo.sh apply 01        copy a scenario's files into src/
#   ./demo.sh reset           remove every scenario file and restore the clean freeze baseline
set -euo pipefail
cd "$(dirname "$0")"
SCENARIOS=demo/scenarios

files_of() { (cd "$1" && find src -type f); }
target_of() { printf '%s\n' "${1%.scenario}"; }

case "${1:-}" in
  list)
    for d in "$SCENARIOS"/*/; do printf '%s\n' "$(basename "$d")"; done ;;
  apply)
    dir=$(ls -d "$SCENARIOS/${2:?scenario number, e.g. 01}"* | head -1)
    files_of "$dir" | while read -r f; do t=$(target_of "$f"); mkdir -p "$(dirname "$t")"; cp "$dir/$f" "$t"; echo "+ $t"; done
    echo; sed -n '1,200p' "$dir/README.md" ;;
  reset)
    for d in "$SCENARIOS"/*/; do files_of "$d" | while read -r f; do rm -f "$(target_of "$f")"; done; done
    rm -rf archunit_store && cp -R demo/archunit_store.clean archunit_store
    echo "Back to the clean code base." ;;
  *)
    sed -n '2,8p' "$0"; exit 1 ;;
esac
