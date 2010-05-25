#!/bin/sh
# ***********************************************************************************************************************
#  * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
#  * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
#  * and is available at http://www.eclipse.org/legal/epl-v10.html
#  *
#  * Contributors: brox IT Solutions GmbH)- initial creator
#  **********************************************************************************************************************/
#
# executes a normative (same as bamboo) build of the whole project and all active tests 
# see: http://wiki.eclipse.org/SMILA/Development_Guidelines/Howto_build_a_SMILA-Distribution

# set this to the location of your working copy of the SMILA source code (default is ok, usually
SMILA_HOME=$PWD/..

# set this to the location of a Eclipse Classic 3.5 SDK + matching delta pack
ECLIPSE_HOME=$PWD/../../eclipse_standard

# make sure that the arch setting matches the used JDK
ARCH="-Dos=linux -Dws=gtk -Darch=x86"
JAVA_HOME=/usr/lib/jvm/java-6-sun/

ANT_HOME=$PWD/../../ant
#BUILDLIB_DIR=/data07/SMILA/3rd_party_sw/lib
# comment out the following line, if your lib directory is in trunk
#libDir="-Dlib.dir=$BUILDLIB_DIR"

# standard values for Eclipse 3.5 Classic SDK + Delta Pack 3.5
buildOpts="-Declipse.running=true -DpdeBuildPluginVersion=3.5.2.R35x_20100114 -DequinoxLauncherPluginVersion=1.0.201.R35x_v20090715"

# usually there is no need to change the following lines
buildDir=-DbuildDirectory=${SMILA_HOME}/eclipse.build
builder=-Dbuilder=${SMILA_HOME}/SMILA.builder 
eclipseHome=-Declipse.home="${ECLIPSE_HOME}"
testJava=-Dtest.java.home="${JAVA_HOME}"
configHome="-Dorg.eclipse.smila.utils.config.root=../SMILA.application/configuration"

antTarget=$1
logfile=log.${antTarget}.txt

date | tee ${logfile}

PATH=$JAVA_HOME/bin:$ANT_HOME/bin:$PATH

export JAVA_HOME ANT_HOME PATH

exec ant -f build.xml ${buildDir} ${eclipseHome} ${builder} ${ARCH} ${testJava} ${buildOpts} ${configHome} ${libDir} $* | tee -a ${logfile}
