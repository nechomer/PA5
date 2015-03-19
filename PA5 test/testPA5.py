import os
import sys
import time
from glob import glob
from subprocess import *




ProgFileList = glob("input/*.ic")
for Progfile in ProgFileList:
	fname = str(Progfile).split('\\')[-1:][0]
	f1 = open('output/'+fname+'err.txt', 'w+')
	args = ['PA5.jar', Progfile,"-LLibrary/libic.sig"]
	process = Popen(['java', '-jar']+list(args), stdout=f1, stderr=f1)
	#time.sleep(2)
	f1.close
	
#time.sleep(5)	

#ProgFileList = glob("input/*.s")
#for Progfile in ProgFileList:
	#fname = str(Progfile).split('\\')[-1:][0]
	#f1 = open('output/'+fname+'.txt', 'w+')
	#args = ['microLIRFull.jar', Progfile]
	#process = Popen(['compile ']+Progfile, stdout=PIPE, stderr=PIPE)
	#f1.close
	

