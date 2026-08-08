# Create Big Cannons: Autocannon Revolution

[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1-brightgreen)](https://www.minecraft.net)
[![NeoForge](https://img.shields.io/badge/NeoForge-21.1.228-orange)](https://neoforged.net)
[![License](https://img.shields.io/badge/License-MIT-blue)](LICENSE)

为 [Create: Big Cannons](https://www.curseforge.com/minecraft/mc-mods/create-big-cannons) 添加四种新型自动炮弹药，大幅扩展火炮玩法。

## 新增弹药

| 弹药 | 类型 | 描述 |
|------|------|------|
| **APFSDS** | 尾翼稳定脱壳穿甲弹 | 高穿深、高初速，擅长击穿厚重装甲 |
| **APHE** | 穿甲高爆弹 | 穿透后爆炸，对内部结构造成毁灭性伤害 |
| **SAP** | 半穿甲弹 | 平衡穿深与装药，通用型弹药 |
| **Shrapnel（榴霰弹）** | 榴霰弹 | 空爆释放弹丸，覆盖大范围软目标 |

## 依赖

- **Minecraft** 1.21.1
- **NeoForge** 21.1.228+
- **[Create](https://www.curseforge.com/minecraft/mc-mods/create)** 6.0+（CBC 依赖）
- **[Create: Big Cannons](https://www.curseforge.com/minecraft/mc-mods/create-big-cannons)** 5.11+

## 安装

1. 下载 [最新版本](https://github.com/QKDream/Create-Big-Cannon-Autogun-Revolution/releases) 的 .jar 文件
2. 放入 Minecraft 的 mods 文件夹
3. 确保已安装上述所有依赖模组
4. 启动游戏

## 更新日志

### [1.03] - 2026-08-08

- 修复 Cartridge 发射空包弹
- Cartridge 物品显示格式：机炮弹药 [弹射物：弹头名称]

### [1.02] - 2026-08-08

- 弹头不再可直接装入弹药箱，需合成为完整机炮弹药
- 新增 4 种 Cartridge 物品，可正常装填机炮

### [1.01] - 2026-08-08

- 修复 
eoforge.mods.toml UTF-8 BOM 导致模组加载崩溃
- 更新包图标及版本号

### [1.00] - 2026-08-08

- 新增 APFSDS 尾翼稳定脱壳穿甲弹
- 新增 APHE 穿甲高爆弹
- 新增 SAP 半穿甲弹
- 新增 Shrapnel 榴霰弹
- 四种弹药的自定义纹理材质
- 中英文语言支持（zh_cn / en_us）
- 合成配方与弹药标签

## 构建

`ash
# 使用 Gradle
./gradlew build

# 或使用 Windows 批处理脚本
build.bat
`

输出 JAR 位于 uild/libs/ 或桌面（使用 uild.bat 时）。

## 许可

MIT License - 详见 [LICENSE](LICENSE)