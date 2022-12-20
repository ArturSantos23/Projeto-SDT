@echo OFF
SETLOCAL ENABLEDELAYEDEXPANSION
SET "destdir=Storage\src\savedFiles"
SET "filename1=%destdir%\%1"
SET "outfile=%destdir%\outfile_%1"
SET /a count=0
FOR /f "delims=" %%a IN (%filename1%) DO (
SET /a count+=1
SET "line[!count!]=%%a"
)
(
FOR /L %%a IN (%count%,-1,1) DO ECHO(!line[%%a]!
)>"%outfile%"

GOTO :EOF
