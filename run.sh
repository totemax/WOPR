#!/bin/bash

echo "Se lanza el sistema inteligente, hora:";
date -u;

java -Dlog4j.configurationFile=log4j2.xml -jar WOPR.jar -file gnm.params > out.log 2>&1

echo "Ejecución finalizada, hora:";
date -u;
