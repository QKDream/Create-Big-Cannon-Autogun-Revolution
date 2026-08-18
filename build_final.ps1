$ErrorActionPreference = "Stop"
$javac = "D:/Users/../.minecraft/runtime/java-runtime-delta/bin/javac.exe"
$jarExe = "D:/.minecraft/runtime/java-runtime-delta/bin/jar.exe"
$ws = "C:/Users/1/Documents/Codex/2026-08-07/zhe/cbc-addon"
$mcLib = "D:/.minecraft/libraries"

$cpParts = @(
  "$mcLib/net/neoforged/neoforge/21.1.233/neoforge-21.1.233-client.jar",
  "$mcLib/net/neoforged/neoforge/21.1.233/neoforge-21.1.233-universal.jar",
  "$mcLib/net/neoforged/mergetool/2.0.3/mergetool-2.0.3-api.jar",
  "$mcLib/net/minecraft/client/1.21.1-20240808.144430/client-1.21.1-20240808.144430-srg.jar",
  "$mcLib/net/minecraft/client/1.21.1-20240808.144430/client-1.21.1-20240808.144430-extra.jar",
  "$mcLib/net/neoforged/fancymodloader/loader/4.0.42/loader-4.0.42.jar",
  "$mcLib/net/neoforged/bus/8.0.5/bus-8.0.5.jar",
  "$mcLib/net/neoforged/coremods/7.0.3/coremods-7.0.3.jar",
  "$mcLib/io/netty/netty-buffer/4.1.97.Final/netty-buffer-4.1.97.Final.jar",
  "$mcLib/io/netty/netty-common/4.1.97.Final/netty-common-4.1.97.Final.jar",
  "$mcLib/com/mojang/datafixerupper/6.0.6/datafixerupper-6.0.6.jar",
  "$mcLib/com/mojang/brigadier/1.3.10/brigadier-1.3.10.jar",
  "$mcLib/org/slf4j/slf4j-api/2.0.17/slf4j-api-2.0.17.jar",
  "$ws/libs/create-1.21.1-6.0.10.jar",
  "$ws/libs/createbigcannons-5.11.7+mc.1.21.1.jar",
  "$ws/libs/cbcmodernwarfare-0.0.6v+mc.1.21.1-neoforge.jar",
  "$ws/libs/ponder-neoforge-1.0.82+mc1.21.1.jar",
  "$ws/libs/Registrate-MC1.21-1.3.0+67.jar",
  "$ws/libs/ritchiesprojectilelib-2.1.2+mc.1.21.1-neoforge.jar",
  "$mcLib/org/spongepowered/mixin/0.8.7/mixin-0.8.7.jar",
  "$mcLib/org/ow2/asm/asm/9.10.1/asm-9.10.1.jar",
  "$mcLib/org/ow2/asm/asm-tree/9.10.1/asm-tree-9.10.1.jar",
  "$mcLib/org/ow2/asm/asm-analysis/9.10.1/asm-analysis-9.10.1.jar",
  "$mcLib/org/ow2/asm/asm-commons/9.10.1/asm-commons-9.10.1.jar"
)
foreach ($p in $cpParts) { if (-not (Test-Path $p)) { Write-Host "MISSING: $p"; exit 1 } }
$classpath = $cpParts -join ";"

$outDir = "$ws/build/classes"
if (Test-Path $outDir) { Remove-Item -Recurse -Force $outDir }
New-Item -ItemType Directory -Force $outDir | Out-Null

$argFile = "$ws/build/javac_argfile.txt"
$lines = New-Object System.Collections.Generic.List[string]
$lines.Add("-d")
$lines.Add($outDir)
$lines.Add("-cp")
$lines.Add($classpath)
$lines.Add("-encoding")
$lines.Add("UTF-8")
$lines.Add("-proc:none")
Get-ChildItem -Path "$ws/src/main/java" -Recurse -Filter "*.java" | ForEach-Object { $lines.Add($_.FullName.Replace('\','/')) }
[System.IO.File]::WriteAllLines($argFile, $lines, (New-Object System.Text.UTF8Encoding($false)))
Write-Host "Compiling $($lines.Count - 7) java files..."

& $javac "@$argFile"
if ($LASTEXITCODE -ne 0) {
    Write-Host "COMPILATION FAILED (exit $LASTEXITCODE)"
    exit 1
}
Write-Host "Compilation OK!"

# Stage and build JAR
$stage = "$ws/build/jar_stage"
if (Test-Path $stage) { Remove-Item -Recurse -Force $stage }
New-Item -ItemType Directory -Force $stage | Out-Null
Copy-Item "$outDir/*" -Destination $stage -Recurse -Force
Copy-Item "$ws/src/main/resources/META-INF" -Destination "$stage/META-INF" -Recurse -Force
Copy-Item "$ws/src/main/resources/assets" -Destination "$stage/assets" -Recurse -Force
Copy-Item "$ws/src/main/resources/data" -Destination "$stage/data" -Recurse -Force
Copy-Item "$ws/src/main/resources/cbcaddon.mixins.json" -Destination "$stage/cbcaddon.mixins.json" -Force
if (Test-Path "$ws/src/main/resources/pack.png") { Copy-Item "$ws/src/main/resources/pack.png" -Destination "$stage/pack.png" -Force }
$packMeta = '{"pack":{"description":"CBC Autocannon Revolution","pack_format":34}}'
[System.IO.File]::WriteAllText("$stage/pack.mcmeta", $packMeta, (New-Object System.Text.UTF8Encoding($false)))

$jarOut = "$ws/Create-Big-Cannon-Autogun-Revolution.jar"
Push-Location $stage
& $jarExe cf $jarOut "."
Pop-Location

if (Test-Path $jarOut) {
    Write-Host "BUILD SUCCESS: $jarOut ($((Get-Item $jarOut).Length) bytes)"
} else {
    Write-Host "JAR creation failed!"
    exit 1
}
Remove-Item -Recurse -Force $stage
