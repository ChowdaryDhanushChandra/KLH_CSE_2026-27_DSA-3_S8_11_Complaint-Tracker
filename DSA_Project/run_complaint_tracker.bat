@echo off
title Complaint Tracker - Smart Complaint Management Using Advanced DSA
cd /d "%~dp0"
echo ===================================================================
echo Starting Complaint Tracker (DSA-3 JavaFX Desktop Application)
echo ===================================================================
echo Default Credentials:
echo   - Administrator: admin / admin123  (or click 'Demo Login as Administrator')
echo   - Citizen/Customer: customer / cust123 (or click 'Demo Login as Customer')
echo ===================================================================
call .\mvnw.cmd javafx:run
if %ERRORLEVEL% neq 0 (
    echo.
    echo Application exited with error code %ERRORLEVEL%.
    pause
)
