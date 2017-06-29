@echo off

WHERE Java
IF %ERRORLEVEL% NEQ 0 (
    ECHO JAVA wasn't found 
    GOTO Exit
)

pushd ..
SET jarfile=petrolink-mbe-engine.jar
SET MainClass=com.petrolink.mbe.util.PrintVersionString

ECHO Get Version for %jarfile%:
java -cp %jarfile%;. %MainClass%

popd

:Exit
pause