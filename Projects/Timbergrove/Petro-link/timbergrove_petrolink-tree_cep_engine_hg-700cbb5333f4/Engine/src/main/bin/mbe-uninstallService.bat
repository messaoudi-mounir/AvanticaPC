@echo off
ECHO 
ECHO === Load Config version ===
SET jarfile=petrolink-mbe-engine.jar
SET MainClass=com.petrolink.mbe.engine.ComplexEventProcessor
SET ServiceName=PetrolinkMBE
SET JRunner=%cd%\procrun\amd64\prunsrv.exe
SET JRunnerMgr=%cd%\procrun\prunmgr.exe
ECHO 
ECHO === UnInstalling %jarfile% ===

%JRunnerMgr% //MQ//%ServiceName%
%JRunner% //DS//%ServiceName%

popd

:Exit
pause