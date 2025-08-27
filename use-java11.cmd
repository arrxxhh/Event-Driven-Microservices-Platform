@echo off
echo ========================================
echo Setting up Java 11 for PySpark
echo ========================================
echo.

:: Set Java 11 environment (adjust path as needed)
set JAVA_HOME=C:\Program Files\Java\jdk-11
set PATH=%JAVA_HOME%\bin;%PATH%

:: Verify Java version
echo Current Java version:
java -version
echo.

echo ========================================
echo Environment ready for PySpark!
echo ========================================
echo.

:: Keep the command prompt open
cmd /k 