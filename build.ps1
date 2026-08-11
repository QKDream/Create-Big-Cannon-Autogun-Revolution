$ErrorActionPreference = "Stop"
$javac = "D:\.minecraft\runtime\java-runtime-delta\bin\javac.exe"
$jar = "D:\.minecraft\runtime\java-runtime-delta\bin\jar.exe"
$libs = "D:\.minecraft\libraries"
$ws = "C:\Users\1\Documents\Codex\2026-08-07\zhe\cbc-addon"

# Classpath construction
$cp = @()

# 1. NeoForge client
$cp += (Get-ChildItem "$libs\net\neoforged\neoforge\21.1.233\*client*.jar" | Select-Object -First 1).FullName

# 2. Minecraft client jars
$cp += (Get-ChildItem "$libs\net\minecraft\client\1.21.1-20240808.144430\*srg*.jar" | Select-Object -First 1).FullName
$cp += (Get-ChildItem "$libs\net\minecraft\client\1.21.1-20240808.144430\*extra*.jar" | Select-Object -First 1).FullName

# 3. FML Loader
$cp += (Get-ChildItem "$libs\net\neoforged\fancymodloader\loader\4.0.42\*.jar" | Select-Object -First 1).FullName

# 4. Other needed libs
$extraLibs = @(
    "$libs\com\mojang\datafixerupper\6.0.6\datafixerupper-6.0.6.jar",
    "$libs\com\mojang\brigadier\1.3.10\brigadier-1.3.10.jar",
    "$libs\org\slf4j\slf4j-api\2.0.17\slf4j-api-2.0.17.jar"
)
foreach ($l in $extraLibs) { if (Test-Path $l) { $cp += $l } }

# 5. Our libs
Get-ChildItem "$ws\libs\*.jar" | ForEach-Object { $cp += $_.FullName }

# 6. Version mods (for CBC and dependencies)
Get-ChildItem "D:\.minecraft\versions\航空学\mods\*.jar" | ForEach-Object { $cp += $_.FullName }

$classpath = ($cp | Select-Object -Unique) -join ";"
Write-Host "Classpath has $($cp.Count) entries"

# Compile
$srcDir = "$ws\src\main\java"
$outDir = "$ws\build\classes"
if (Test-Path $outDir) { Remove-Item -Recurse -Force $outDir }
New-Item -ItemType Directory -Force $outDir | Out-Null

$javaFiles = Get-ChildItem -Path $srcDir -Recurse -Filter "*.java" | ForEach-Object { "`"$($_.FullName)`"" }
$javaFilesStr = $javaFiles -join " "

Write-Host "Compiling $($javaFiles.Count) Java files..."
$compileCmd = "`"$javac`" -d `"$outDir`" -cp `"$classpath`" -encoding UTF-8 -proc:none $javaFilesStr"
Write-Host "Command: $compileCmd"
Invoke-Expression $compileCmd
if ($LASTEXITCODE -ne 0) {
    Write-Host "COMPILATION FAILED!"
    exit 1
}
Write-Host "Compilation OK!"

# Build JAR
$jarTmp = "$ws\build\jar_tmp"
if (Test-Path $jarTmp) { Remove-Item -Recurse -Force $jarTmp }
New-Item -ItemType Directory -Force $jarTmp | Out-Null

Copy-Item "$outDir\*" -Destination $jarTmp -Recurse -Force
Copy-Item "$ws\src\main\resources\META-INF" -Destination "$jarTmp\META-INF" -Recurse -Force
Copy-Item "$ws\src\main\resources\assets" -Destination "$jarTmp\assets" -Recurse -Force
Copy-Item "$ws\src\main\resources\data" -Destination "$jarTmp\data" -Recurse -Force
if (Test-Path "$ws\src\main\resources\pack.png") { Copy-Item "$ws\src\main\resources\pack.png" -Destination "$jarTmp\pack.png" }
'{"pack":{"description":"CBC Autocannon Revolution","pack_format":34}}' | Out-File -FilePath "$jarTmp\pack.mcmeta" -Encoding utf8

$jarOut = "$ws\Create-Big-Cannon-Autogun-Revolution.jar"
Push-Location $jarTmp
& $jar cf $jarOut *
Pop-Location

if (Test-Path $jarOut) {
    Write-Host "BUILD SUCCESS: $jarOut ($((Get-Item $jarOut).Length) bytes)"
} else {
    Write-Host "JAR creation failed!"
}
Remove-Item -Recurse -Force $jarTmp
