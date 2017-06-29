@echo off

WHERE Java
IF %ERRORLEVEL% NEQ 0 (
    ECHO JAVA wasn't found 
    GOTO Exit
)
ECHO === Java version ===
java -version

pushd ..
SET jarfile=petrolink-mbe-engine.jar
SET MainClass=com.petrolink.mbe.engine.ComplexEventProcessor

ECHO === Executing %jarfile% ===
REM CAN't USE -jar due to context need to be loaded from jar
REM java -DlogPath=log -Dmq.data=amq -DIGNITE_PERFORMANCE_SUGGESTIONS_DISABLED=true -Xmx2G -jar %jarfile%

java -DlogPath=log -Dmq.data=amq -DIGNITE_PERFORMANCE_SUGGESTIONS_DISABLED=true -Xmx4G -XX:+HeapDumpOnOutOfMemoryError -cp %jarfile%;. %MainClass%

popd

:Exit
pause