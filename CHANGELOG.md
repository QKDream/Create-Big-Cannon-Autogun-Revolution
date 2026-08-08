# 更新日志

## v1.04 — 2026-08-08
- 新增高速机炮弹药（High-Velocity Autocannon Cartridge），初速2倍
- 弹射物实体新增高初速模式，首tick速度翻倍
- 高速弹药空壳合成：filled_autocannon_cartridge + 2×火药
- 高速弹药直接装填：任意弹头 + 空高速弹壳
- 高速弹药升级：任意已装填弹壳 + 火药
- 高速弹药支持装引信（继承 AutocannonCartridgeItem）
- 高速弹药模型适配 Autocannon Cartridge

## v1.03 — 2026-08-08
- 修复 APFSDS 穿深丢失：重写所有 RoundItem 的 getAutocannonProjectile()
- 根因：父类硬编码 CBC 实体类型，自定义实体从未生成

## v1.02 — 2026-08-07
- 禁用弹头单独塞入弹药箱
- 修复机炮弹药命名：机炮弹药 [弹射物：弹头名称]

## v1.01 — 2026-08-07
- 修复 APHE/SAP 出膛即引爆问题
- 修复引信无法合成
- 修复引信组装后数据不显示
- 修复机炮无法激发

## v1.00beta — 2026-08-07
- 初始版本
- APFSDS / APHE / SAP / 榴霰弹 四种机炮弹头
- 包图案（pack.png）