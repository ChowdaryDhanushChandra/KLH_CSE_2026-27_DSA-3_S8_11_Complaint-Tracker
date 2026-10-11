@echo off
title TextHack - Advanced Algorithm Question Bank
cd /d "%~dp0"
echo ===================================================================
echo Starting TextHack (JavaFX 21 Desktop Application)
echo ===================================================================
call .\mvnw.cmd javafx:run
if %ERRORLEVEL% neq 0 (
    echo.
    echo Application exited with error code %ERRORLEVEL%.
    pause
)
