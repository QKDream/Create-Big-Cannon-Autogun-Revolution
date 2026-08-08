# Changelog

## [1.03] - 2026-08-08

### Fixed
- 修复 Cartridge 发射空包弹问题：覆写 getAutocannonProjectile() 直接创建弹射物

### Changed
- Cartridge 物品命名格式：机炮弹药 [弹射物：弹头名称]

## [1.02] - 2026-08-08

### Changed
- 弹头（Round）不再可直接装入弹药箱，需先合成为完整弹药（Cartridge）
- 新增 4 种 Cartridge 物品，继承 AutocannonCartridgeItem，可正常装填机炮

### Added
- APFSDS / APHE / SAP / Shrapnel 机炮弹药（Cartridge）物品注册
- Cartridge 物品中英文名称

## [1.01] - 2026-08-08

### Fixed
- 修复 
eoforge.mods.toml 包含 UTF-8 BOM 导致模组加载失败的崩溃（ParsingException: Invalid bare key）

## [1.0.0] - 2026-08-08

### Added
- APFSDS 尾翼稳定脱壳穿甲弹：高穿深、高初速自动炮弹药
- APHE 穿甲高爆弹：穿透后爆炸，对内部结构造成伤害
- SAP 半穿甲弹：平衡穿深与装药量的通用弹药
- Shrapnel 榴霰弹：空爆释放弹丸，覆盖大范围软目标
- 四种弹药的自定义纹理材质
- 包图标（pack.png）
- 中英文语言支持（zh_cn / en_us）
- 合成配方与弹药标签
- NeoForge 1.21.1 / Create Big Cannons 5.11+ 兼容

### Dependencies
- Minecraft 1.21.1
- NeoForge 21.1.228+
- Create 6.0+
- Create: Big Cannons 5.11+

---

格式基于 [Keep a Changelog](https://keepachangelog.com/)