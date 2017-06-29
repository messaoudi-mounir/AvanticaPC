@echo off
echo This will delete %USERPROFILE%\.m2\repository\com\smartnow
echo This reset your jar state and redownload from server.
echo You must exit eclipse first!
rd %USERPROFILE%\.m2\repository\com\smartnow /s
pause