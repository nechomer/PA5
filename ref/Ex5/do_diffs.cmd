@ECHO off
mkdir pa-4b-myoutput
del /Q pa-4b-myoutput\*.*
FOR %%a in (pa-4b-input/*.ic) do (
   echo ********      TESTING %%a      ********
	java -cp bin;lib\gearley.jar;src Main pa-4b-input/%%a -Lpa-4b-input/libic.sig > pa-4b-myoutput/_%%a.txt
	java -jar lib/3ac-emu.jar -q pa-4b-myoutput/_%%a.txt > pa-4b-myoutput/%%a.out
	echo.
	echo diff -b pa-4b-myoutput/%%a.out pa-4b-output/%%~na.out
	diff -b pa-4b-myoutput/%%a.out pa-4b-output/%%~na.out
   echo =================================================================
)

   echo ********      TESTING Sieve-static.ic      ********
	java -cp bin;lib\gearley.jar;src Main pa-4b-input/_special/Sieve-static.ic -Lpa-4b-input/libic.sig > pa-4b-myoutput/_Sieve-static.ic.txt
        echo # 3ac-emu - 100 > pa-4b-myoutput/Sieve-static.ic.out  
	java -jar lib/3ac-emu.jar -q -a pa-4b-myoutput/_Sieve-static.ic.txt 100 >> pa-4b-myoutput/Sieve-static.ic.out 
	echo.
	echo diff -b pa-4b-myoutput/Sieve-static.ic.out pa-4b-output/Sieve-static.out
	diff -b pa-4b-myoutput/Sieve-static.ic.out pa-4b-output/Sieve-static.out
   echo =================================================================

   echo ********      TESTING Sieve.ic      ********
	java -cp bin;lib\gearley.jar;src Main pa-4b-input/_special/Sieve.ic -Lpa-4b-input/libic.sig > pa-4b-myoutput/_Sieve.ic.txt
        echo # 3ac-emu - 100 > pa-4b-myoutput/Sieve.ic.out  
	java -jar lib/3ac-emu.jar -q -a pa-4b-myoutput/_Sieve.ic.txt 100 >> pa-4b-myoutput/Sieve.ic.out 
	echo.
	echo diff -b pa-4b-myoutput/Sieve.ic.out pa-4b-output/Sieve-static.out
	diff -b pa-4b-myoutput/Sieve.ic.out pa-4b-output/Sieve.out
   echo =================================================================