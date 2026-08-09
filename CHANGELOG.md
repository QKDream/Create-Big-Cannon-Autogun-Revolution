# 更新日志

## v1.05-hotfix — 2026-08-10
- 修复新增机炮弹道动画异常：所有实体类型调用 AbstractCannonProjectile.build() 确保正确初始化
- 灵魂火装置改为普通火焰 + 小爆炸 + 对撞击点1格内生物造成15%最大生命伤害
- 削弱铝热剂弹头：仅判定撞击点方块挖掘
- 所有弹射物实体统一灵魂火行为（APHE/SAP/榴霰弹/铝热剂）

## v1.05 — 2026-08-09
- 新增铝热剂弹头（Thermite Autocannon Round）：爆炸 + 方块挖掘 + 引燃
- 新增灵魂火装置（Soul Fire Device）：可为弹药附加火焰爆炸效果
- 修复灵魂火装置无法正常工作
- 修复 SoulFireApplicationRecipe：对已组装弹药施加灵魂火时，同时更新内部弹射物数据
- HighVelocityAutocannonCartridgeItem 新增防御性灵魂火检查（弹药箱级 CUSTOM_DATA 回退）

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