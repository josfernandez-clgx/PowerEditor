#!/bin/sh

# ---------------------------------------
# MS-DOC script to run PowerEditor Tools
# ---------------------------------------

# Make sure this directory contains jar files
LIBDIR=.

java -classpath $LIBDIR/powereditor-tools.jar:$LIBDIR/log4j-1.2.8.jar -Xms4m -Xmx128m com.mindbox.pe.tools.PowerEditorTool
