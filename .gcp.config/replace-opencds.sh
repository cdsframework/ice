#!/bin/bash

PROJECT_DEST_DIR="opencds"

EXCLUDE_DIRS=(
  "opencds-parent/opencds-fhir-evaluation"
  "opencds-parent/opencds-fhir-operations"
  ".idea"
  ".git"
  "opencds-parent/opencds-vmr-1_0/opencds-vmr-1_0-schema/src/main/java"
  "opencds-parent/opencds-config/opencds-config-schema/src/main/java"
)

EXCLUDE_FILES=(
  "opencds-parent/opencds-config/opencds-config-cli/src/test/resources/my-output.psv"
  "opencds-parent/opencds-config/opencds-config-cli/src/test/resources/cdm.xml"
  "opencds-parent/opencds-config/opencds-config-cli/src/test/resources/cdm_pqrs.psv"
)

SOURCE_DIR=${1:-$OPENCDS_SOURCE_DIR}

if [ -z "$SOURCE_DIR" ]; then
    echo "Error: Source directory must be specified."
    echo "Usage: $0 /path/to/source"
    exit 1
fi

if [ ! -d "$SOURCE_DIR" ]; then
    echo "Error: Source directory '$SOURCE_DIR' does not exist or is not a directory."
    exit 1
fi

if [ -z "$(ls -A "$SOURCE_DIR")" ]; then
    echo "Error: Source directory '$SOURCE_DIR' is empty."
    exit 1
fi

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PARENT_DIR="$(dirname "$SCRIPT_DIR")"
DEST_DIR="$PARENT_DIR/${PROJECT_DEST_DIR:?}"

read -rp "This script will delete the folder '$DEST_DIR' before copying files from '$SOURCE_DIR'. Proceed? (y/n): " confirm
confirm=$(echo "$confirm" | tr '[:upper:]' '[:lower:]')
if [[ "$confirm" != "y" ]]; then
    echo "Operation cancelled."
    exit 0
fi

rm -rf "$DEST_DIR"
echo "Removed dir: $DEST_DIR"

mkdir "$DEST_DIR"
echo "Created dir: $DEST_DIR"

cp -a "$SOURCE_DIR/." "$DEST_DIR/"
echo "Copied files from '$SOURCE_DIR' to '$DEST_DIR'."

for dir in "${EXCLUDE_DIRS[@]}"; do
    if [[ "$dir" =~ ^[[:space:]]*$ ]]; then
        echo "Warning: Found blank entry in EXCLUDE_DIRS"
    else
        REM_DIR="${DEST_DIR:?}/$dir"
        if [[ -e "$REM_DIR" ]]; then
            rm -rf "$REM_DIR"
            echo "Removed dir: $REM_DIR"
        fi
    fi
done

for file in "${EXCLUDE_FILES[@]}"; do
    if [[ "$file" =~ ^[[:space:]]*$ ]]; then
        echo "Warning: Found blank entry in EXCLUDE_FILES"
    else
        REM_FILE="${DEST_DIR:?}/$file"
        if [[ -e "$REM_FILE" ]]; then
            rm "$REM_FILE"
            echo "Removed file: $REM_FILE"
        else
            echo "Warning: $REM_FILE does not exist."
        fi
    fi
done

echo "Completed."
