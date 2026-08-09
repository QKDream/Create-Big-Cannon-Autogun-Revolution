@echo off
setlocal enabledelayedexpansion
echo ========================================
echo  CBC Autocannon Revolution - Build Script v10
echo ========================================
echo.

set "JAVA_RUNTIME="
for /d %%d in ("D:\.minecraft\runtime\java-runtime-*") do set "JAVA_RUNTIME=%%d"
if not defined JAVA_RUNTIME ( echo ERROR: No Java runtime! & pause & exit /b 1 )
set "JAVAC=%JAVA_RUNTIME%\bin\javac.exe"
set "JAR=%JAVA_RUNTIME%\bin\jar.exe"
echo Java: %JAVA_RUNTIME%

set "LIBS=D:\.minecraft\libraries"

REM ============ BUILD CLASSPATH (order matters!) ============
set "CP="

REM 1. NeoForge client (patched MC + NeoForge APIs) - MUST BE FIRST
for /f "delims=" %%f in ('dir /s /b "%LIBS%\net\neoforged\neoforge\21.1.228\*client*.jar" 2^>nul') do ( set "CP=!CP!%%f;" & goto :cp1done )
:cp1done

REM 2. Minecraft SRG (vanilla classes) - MUST BE SECOND
for /f "delims=" %%f in ('dir /s /b "%LIBS%\net\minecraft\client\1.21.1-20240808.144430\*srg*.jar" 2^>nul') do ( set "CP=!CP!%%f;" & goto :cp2done )
:cp2done

REM 3. Minecraft extra
for /f "delims=" %%f in ('dir /s /b "%LIBS%\net\minecraft\client\1.21.1-20240808.144430\*extra*.jar" 2^>nul') do ( set "CP=!CP!%%f;" & goto :cp3done )
:cp3done

REM 4. FML Loader
for /f "delims=" %%f in ('dir /s /b "%LIBS%\net\neoforged\fancymodloader\loader\4.0.42\*loader*.jar" 2^>nul') do ( set "CP=!CP!%%f;" & goto :cp4done )
:cp4done

REM 5. Bus
for /f "delims=" %%f in ('dir /s /b "%LIBS%\net\neoforged\bus\8.0.5\bus-8.0.5.jar" 2^>nul') do ( set "CP=!CP!%%f;" )
REM 6. DataFixerUpper
for /f "delims=" %%f in ('dir /s /b "%LIBS%\com\mojang\datafixerupper\6.0.6\datafixerupper-6.0.6.jar" 2^>nul') do ( set "CP=!CP!%%f;" )
REM 7. Brigadier
for /f "delims=" %%f in ('dir /s /b "%LIBS%\com\mojang\brigadier\1.3.10\brigadier-1.3.10.jar" 2^>nul') do ( set "CP=!CP!%%f;" )
REM 8. Slf4j
for /f "delims=" %%f in ('dir /s /b "%LIBS%\org\slf4j\slf4j-api\2.0.17\slf4j-api-2.0.17.jar" 2^>nul') do ( set "CP=!CP!%%f;" )

REM 9. Our libs mod JARs
for /f "delims=" %%f in ('dir /s /b "%~dp0libs\*.jar" 2^>nul') do ( set "CP=!CP!%%f;" )

REM 10. Version mods
set "MC_DIR="
if exist "D:\.minecraft" set "MC_DIR=D:\.minecraft"
set "VERSION_DIR="
for /d %%v in ("%MC_DIR%\versions\*") do ( if exist "%%v\mods" set "VERSION_DIR=%%v" )
echo Version: %VERSION_DIR%
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