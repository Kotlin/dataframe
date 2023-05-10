#!/bin/bash

set -e
/opt/builder/bin/idea.sh helpbuilderinspect -source-dir . -product $PRODUCT --frontend-url https://resources.jetbrains.com/storage/writerside/v5.14.0/ --runner github -output-dir artifacts/ || true
echo "Test existing of $ARTIFACT artifact"
test -e artifacts/$ARTIFACT
