#!/bin/sh
#
# Use this script to run your program LOCALLY.
#
# Note: Changing this script WILL NOT affect how CodeCrafters runs your program.
#
# Learn more: https://codecrafters.io/program-interface

set -e # Exit early if any commands fail

PATTERN="$2"
INPUT="${1:-dog}" # default to "dog" if not provided

echo "$INPUT" | /usr/bin/env /usr/lib/jvm/jdk-21.0.5-oracle-x64/bin/java \
  -XX:+ShowCodeDetailsInExceptionMessages \
  -cp /home/theslinkers2011/codecrafters-grep-java/target/classes \
  Main -E "$PATTERN"
