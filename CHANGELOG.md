# Changelog

## [1.03] - 2026-08-08

### Fixed
- 修复 APHE/SAP 出膛即炸：移除强制 onClip 引爆，改由引信系统控制
- 修复 Cartridge 发射空包弹：覆写 getAutocannonProjectile() 直接创建弹射物
- 修复弹射物实体缺少渲染器导致 NullPointerException 崩溃

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
eoforge.mods.toml 包含 UTF-8 BOM 导致模组加载失败的崩溃

## [1.0.0] - 2026-08-08

### Added
- APFSDS / APHE / SAP / Shrapnel 四种自动炮弹药
- 自定义纹理材质、中英文语言支持、合成配方

---

格式基于 [Keep a Changelog](https://keepachangelog.com/)