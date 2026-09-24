@echo off
cd /d "%~dp0"
echo ====================================================
echo Starting Samadhan - Product & Package Complaint Center
echo ====================================================
echo.
mvn javafx:run
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Application failed to start or exited with an error.
    pause
)
