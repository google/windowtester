#*******************************************************************************
#  Copyright (c) 2012 Google, Inc.
#  All rights reserved. This program and the accompanying materials
#  are made available under the terms of the Eclipse Public License v1.0
#  which accompanies this distribution, and is available at
#  http://www.eclipse.org/legal/epl-v10.html
#  
#  Contributors:
#  Google, Inc. - initial API and implementation
#*******************************************************************************

# Makefile for SWT libraries on Carbon/Mac

include make_common.mak

SWT_PREFIX=swtext
WS_PREFIX=carbon
SWT_VERSION=$(maj_ver)$(min_ver)
SWT_LIB=lib$(SWT_PREFIX)-$(WS_PREFIX)-$(SWT_VERSION).jnilib

# Uncomment for Native Stats tool
#NATIVE_STATS = -DNATIVE_STATS

#SWT_DEBUG = -g
ARCHS = -arch i386 -arch ppc
CFLAGS = -c $(ARCHS) -DSWT_VERSION=$(SWT_VERSION) $(NATIVE_STATS) $(SWT_DEBUG) -DCARBON -I /System/Library/Frameworks/JavaVM.framework/Headers -I /Code/swt-M20070212-1330-carbon-macosx/src
LFLAGS = -bundle $(ARCHS) -framework JavaVM -framework Carbon 
SWT_OBJECTS = os_ext.o os_structs.o

all: $(SWT_LIB)

.c.o:
	cc $(CFLAGS) $*.c

$(SWT_LIB): $(SWT_OBJECTS)
	cc -o $(SWT_LIB) $(LFLAGS) $(SWT_OBJECTS)

install: all
	cp *.jnilib $(OUTPUT_X86)
	cp *.jnilib $(OUTPUT_PPC)

clean:
	rm -f *.jnilib *.o
