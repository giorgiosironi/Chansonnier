#!/bin/sh

java -cp lib/activation.jar:lib/logging.jar:lib/jaxb-api.jar:lib/jaxb-impl.jar:lib/jmxclient.jar:lib/log4j.jar:lib/stax-api.jar:lib/beanutils.jar:. org.eclipse.smila.management.jmx.client.Main $*

read -n 1 -p "Press any key to continue..."
