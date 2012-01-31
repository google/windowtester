#!/bin/sh
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

cd `dirname $0`

OUTPUT_X86=../os/macosx/x86
OUTPUT_PPC=../os/macosx/ppc

export OUTPUT_X86
export OUTPUT_PPC

make -f make_macosx.mak $1 $2 $3 $4 $5 $6 $7 $8 $9 install
make -f make_macosx.mak $1 $2 $3 $4 $5 $6 $7 $8 $9 clean
exit
