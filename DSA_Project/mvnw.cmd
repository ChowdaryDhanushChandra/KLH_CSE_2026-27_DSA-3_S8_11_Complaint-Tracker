@echo off
setlocal
where mvn >nul 2>nul
if %ERRORLEVEL% equ 0 (
    mvn %*
    goto end
)

if exist "C:\Users\Dhanush\AppData\Local\Programs\IntelliJ IDEA 2026.2.2\plugins\maven-plugin\lib\maven3\bin\mvn.cmd" (
    call "C:\Users\Dhanush\AppData\Local\Programs\IntelliJ IDEA 2026.2.2\plugins\maven-plugin\lib\maven3\bin\mvn.cmd" %*
    goto end
)

echo Error: Maven executable not found. Please install Maven or add it to your PATH.
exit /b 1

:end
exit /b %ERRORLEVEL%
