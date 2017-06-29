@echo off

WHERE Java
IF %ERRORLEVEL% NEQ 0 (
    ECHO mycommand wasn't found 
    GOTO Exit
)
ECHO === Java version ===
java -version

pushd ..
SET jarfile=petrolink-mbe-engine.jar
SET MainClass=com.smartnow.engine.settings.EncryptConfigFile

ECHO === Executing %jarfile% ===
java -cp %jarfile%;. %MainClass% connections.xml
ECHO === 
ECHO If above process is a success please make sure you delete connections.xml.orig, when you do not need it anymore

popd

:Exit
pause