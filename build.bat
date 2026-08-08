@echo off
setlocal enabledelayedexpansion
echo ========================================
echo  CBC Autocannon Revolution - Build Script v9
echo ========================================
echo.

set "JAVA_RUNTIME="
for /d %%d in ("D:\.minecraft\runtime\java-runtime-*") do set "JAVA_RUNTIME=%%d"
if not defined JAVA_RUNTIME ( echo ERROR: No Java runtime! & pause & exit /b 1 )
set "JAVAC=%JAVA_RUNTIME%\bin\javac.exe"
set "JAR=%JAVA_RUNTIME%\bin\jar.exe"
echo Java: %JAVA_RUNTIME%

set "MC_DIR="
if exist "D:\.minecraft" set "MC_DIR=D:\.minecraft"

set "VERSION_DIR="
for /d %%v in ("%MC_DIR%\versions\*") do ( if exist "%%v\mods" set "VERSION_DIR=%%v" )
echo Version: %VERSION_DIR%

set "LIBS=%MC_DIR%\libraries"

REM ============ BUILD CLASSPATH ============
set "CP="

REM 1. NeoForge client (patches)
for /f "delims=" %%f in ('dir /s /b "%LIBS%\net\neoforged\neoforge\*client*.jar" 2^>nul ^| findstr /r 21\.1\. ^| sort /r') do ( set "CP=!CP!%%f;" & goto :cp1done )
:cp1done

REM 2. Minecraft SRG (ALL vanilla classes)
for /f "delims=" %%f in ('dir /s /b "%LIBS%\net\minecraft\client\1.21.1-20240808.144430\*srg*.jar" 2^>nul') do ( set "CP=!CP!%%f;" & goto :cp2done )
:cp2done

REM 3. NeoForge universal
for /f "delims=" %%f in ('dir /s /b "%LIBS%\net\neoforged\neoforge\*universal*.jar" 2^>nul ^| findstr /r 21\.1\. ^| sort /r') do ( set "CP=!CP!%%f;" & goto :cp3done )
:cp3done

REM 4. FML Loader
for /f "delims=" %%f in ('dir /s /b "%LIBS%\net\neoforged\fancymodloader\loader\*loader*.jar" 2^>nul ^| sort /r') do ( set "CP=!CP!%%f;" & goto :cp4done )
:cp4done

REM 5. Bus
for /f "delims=" %%f in ('dir /s /b "%LIBS%\net\neoforged\bus\*bus*.jar" 2^>nul ^| sort /r') do ( set "CP=!CP!%%f;" & goto :cp5done )
:cp5done

REM 6. Logging
for /f "delims=" %%f in ('dir /s /b "%LIBS%\org\apache\logging\log4j\log4j-api\*2.8.1*api*.jar" 2^>nul') do ( set "CP=!CP!%%f;" & goto :cp6done )
:cp6done
for /f "delims=" %%f in ('dir /s /b "%LIBS%\org\apache\logging\log4j\log4j-core\*2.8.1*core*.jar" 2^>nul') do ( set "CP=!CP!%%f;" & goto :cp7done )
:cp7done
for /f "delims=" %%f in ('dir /s /b "%LIBS%\org\slf4j\slf4j-api\2.0.17\slf4j-api-2.0.17.jar" 2^>nul') do ( set "CP=!CP!%%f;" )
for /f "delims=" %%f in ('dir /s /b "%LIBS%\com\mojang\logging\1.2.7\logging-1.2.7.jar" 2^>nul') do ( set "CP=!CP!%%f;" )

REM 7. DataFixerUpper (for Keyable, Codec etc.)
for /f "delims=" %%f in ('dir /s /b "%LIBS%\com\mojang\datafixerupper\*datafixerupper*.jar" 2^>nul') do ( set "CP=!CP!%%f;" )

REM 7b. Brigadier (for Component/Message)
for /f "delims=" %%f in ('dir /s /b "%LIBS%\com\mojang\brigadier\1.3.10\brigadier-1.3.10.jar" 2^>nul') do ( set "CP=!CP!%%f;" )

REM 8. ALL mod JARs
echo Adding mods to classpath...
for /f "delims=" %%f in ('dir /s /b "%VERSION_DIR%\mods\*.jar" 2^>nul') do ( set "CP=!CP!%%f;" )

if "!CP:~-1!"==";" set "CP=!CP:~0,-1!"

echo.
echo ========================================
echo  Compiling...
echo ========================================

set "SRC_DIR=%~dp0src\main\java"
set "OUT_DIR=%~dp0build\classes"
set "RESOURCES=%~dp0src\main\resources"
set "JAR_OUT=%USERPROFILE%\Desktop\Create-Big-Cannon-Autogun-Revolution.jar"

if exist "%OUT_DIR%" rmdir /s /q "%OUT_DIR%"
mkdir "%OUT_DIR%"

set "JAVA_FILES="
for /r "%SRC_DIR%" %%f in (*.java) do set "JAVA_FILES=!JAVA_FILES! "%%f""

"%JAVAC%" -d "%OUT_DIR%" -cp "%CP%" -encoding UTF-8 -proc:none !JAVA_FILES! 2>&1
if errorlevel 1 (
    echo.
    echo ========================================
    echo  COMPILATION FAILED
    echo ========================================
    pause
    exit /b 1
)

echo.
echo Compilation OK! Creating JAR...

set "JAR_BUILD=%~dp0build\jar_tmp"
if exist "%JAR_BUILD%" rmdir /s /q "%JAR_BUILD%"
mkdir "%JAR_BUILD%"

xcopy /E /I /Y "%OUT_DIR%" "%JAR_BUILD%" >nul
xcopy /E /I /Y "%RESOURCES%\META-INF" "%JAR_BUILD%\META-INF" >nul
xcopy /E /I /Y "%RESOURCES%\assets" "%JAR_BUILD%\assets" >nul
xcopy /E /I /Y "%RESOURCES%\data" "%JAR_BUILD%\data" >nul
if exist "%RESOURCES%\pack.png" copy "%RESOURCES%\pack.png" "%JAR_BUILD%\pack.png" >nul
echo {"pack":{"description":"CBC Autocannon Revolution","pack_format":34}} > "%JAR_BUILD%\pack.mcmeta"

pushd "%JAR_BUILD%"
"%JAR%" cf "%JAR_OUT%" *
popd

if exist "%JAR_OUT%" (
    echo.
    echo ========================================
    echo  BUILD SUCCESS!
    echo  Output: %USERPROFILE%\Desktop\Create-Big-Cannon-Autogun-Revolution.jar
    echo ========================================
) else ( echo ERROR: JAR creation failed! )

if exist "%JAR_BUILD%" rmdir /s /q "%JAR_BUILD%"
echo.
pause
