@echo off
WHERE Java
IF %ERRORLEVEL% NEQ 0 (
    ECHO JAVA wasn't found 
	exit /b %errorlevel%
)
ECHO === Java version ===
java -version

ECHO 
ECHO === Load Config version ===
SET jarfile=petrolink-mbe-engine.jar
SET MainClass=com.petrolink.mbe.engine.ComplexEventProcessor
SET ServiceName=PetrolinkMBE
SET JRunner=%cd%\procrun\amd64\prunsrv.exe
SET JRunnerMgr=%cd%\procrun\prunmgr.exe
ECHO 
ECHO === JRunner is %JRunner% ===

pushd ..

ECHO === Installing %jarfile% ===

REM java -DlogPath=log -Dmq.data=amq -DIGNITE_PERFORMANCE_SUGGESTIONS_DISABLED=true -Xmx2G -cp %jarfile%;. %MainClass%
%JRunner% //DS//%ServiceName%
%JRunner% //IS//%ServiceName% --DisplayName="Petrolink MBE Service" --StartPath=%cd% --LogPath=%cd%\log --Jvm=auto --StartMode=jvm --StopMode=jvm --StopTimeout=15 ++JvmOptions=-DlogPath=log ++JvmOptions=-Dmq.data=amq ++JvmOptions=-DIGNITE_PERFORMANCE_SUGGESTIONS_DISABLED=true --JvmMx=4000 ++JvmOptions=-XX:+HeapDumpOnOutOfMemoryError --Classpath=%jarfile%;. --StartClass=%MainClass%
start %JRunnerMgr% //MS//%ServiceName%

popd
 
:Exit
pause