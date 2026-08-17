# Changelog / 更新日志

## v2.5 — 2026-08-17

**English**
- 🆕 New: Universal Proximity Fuze — stackable; right-click opens CBC's fuze GUI to set the detonation distance; hold Shift to view the current distance
- 🎯 Proximity detonation against SABLE structures (sublevel bounds detection)
- 🚀 Proximity detonation against in-flight missiles — vestalihy PTUR / TOW / PTUR-Jet / Malytka, plus any entity whose type name contains "missile" or "rocket" (e.g. mianbao missiles)
- 🔧 Fixed: crash when opening the fuze distance GUI
- 🌐 Removed in-game en_us / ru_ru translations (Chinese only in-game); multilingual text is kept for the changelog

**中文**
- 🆕 新增：通用近炸引信 — 可堆叠，右键打开 CBC 同款调距界面设置近炸距离，按住 Shift 查看当前距离
- 🎯 对 SABLE 结构近炸（检测副等级结构边界盒）
- 🚀 对飞行中导弹近炸 — vestalihy 的 PTUR / TOW / PTUR-Jet / Malytka，以及类型名包含 missile / rocket 的实体（如面包学的导弹）
- 🔧 修复：打开引信调距界面闪退
- 🌐 移除游戏内 en_us / ru_ru 语言文件（游戏内仅中文），多语言仅用于日志

**Русский**
- 🆕 Новое: универсальный неконтактный взрыватель — стакается; ПКМ открывает меню радиуса подрыва (как у CBC), Shift показывает текущий радиус
- 🎯 Подрыв у SABLE-структур (по границам суб-уровней)
- 🚀 Подрыв у летящих ракет — vestalihy PTUR / TOW / PTUR-Jet / Malytka, а также сущности с "missile" / "rocket" в имени типа (например, ракеты mianbao)
- 🔧 Исправлено: вылет при открытии меню радиуса взрывателя
- 🌐 Убраны игровые переводы en_us / ru_ru (в игре только китайский); мультиязычность — только в журнале изменений

## v2.4 — 2026-08-16
- 🎨 美术2.0：全部弹头/装置贴图更新为 32×32 像素画 — APFSDS、APHE、SAP、榴霰弹、铝热剂弹头、多用途榴弹、低速破片榴弹、烟雾药水弹、重爆弹、灵魂火装置

## v2.3 — 2026-08-13
- ⚖️ 平衡：全系穿甲弹穿深进一步下调 — APFSDS 240/120 → 120/60；APHE 16/8 → 10/5；SAP 11/6 → 8/4；重爆弹 10/6 → 7/3（仍明显强于 CBCMW 3/1 基线），物品提示显示值同步更新
- 🆕 重爆弹：穿深随飞行距离增长 — 每飞行 4 格 +1 穿深（上限 +10），韧度每 8 格 +1（上限 +5），飞行 40 格达到峰值，与离膛加速机制同款封顶
- ⚖️ 平衡：APHE/SAP/重爆弹爆炸威力下调 — 对实体 6.0 → 4.5，对方块 4.0 → 3.0
- ⚖️ 平衡：铝热剂弹头方块挖掘概率 100% → 33%
- 🔧 修复：装药弹引爆后弹体立即移除，避免二次爆炸

## v2.2hotfix1 — 2026-08-13
- 🔧 修复：重爆弹命中后不爆炸（终端弹道学会判定为穿透导致打穿目标；现改为命中即引爆，无需引信）
- 🆕 重爆弹新增自带尾迹（火焰+烟雾粒子）
## v2.2 — 2026-08-13
- 🆕 新增重爆弹 — 离膛后持续加速至最高速度（5.0格/tick），命中后引发APHE式大威力爆炸，穿深18，支持高速机炮弹药
- 🆕 多功能榴弹重做为HEAT行为 — 继承CBC现代战争的HEAP破甲弹（小规模金属射流+锥形破片，威力/规模/穿甲较原版更小）
- 🆕 新增前置依赖：CBC Modern Warfare 0.0.6+
- 🔧 修复多语言文件JSON缺失逗号问题
## v2.1hotfix3 — 2026-08-12
- 🔧 修复：恢复所有弹头属性硬编码覆写(getProjectileMass/getBallisticProperties/getDamageProperties)，确保终端弹道学正确读取弹头质量、穿深、韧性数据
- 🔧 修复：APFSDS钢针穿深500/韧性250/质量100，APHE穿深28/韧性14/质量25，SAP穿深20/韧性10/质量30
- 🔧 所有弹头类型（APFSDS/APHE/SAP/榴霰弹/铝热剂/多用途/破片榴弹/烟雾药水弹/子弹药）均已添加属性覆写

## v2.1 — 2026-08-11
- 🆕 新增低速破片榴弹 — 8枚子弹药，碰撞5tick引爆，全向抛射，对生物巨量伤害，不可用于高速弹药
- 🆕 新增烟雾/药水弹 — 无药水时产生烟雾云团，合成滞留药水后产生药水云团，不可装灵魂火
- 🔧 修复：烟雾弹和破片榴弹被排除在灵魂火装置合成之外
- 🔧 修复：高速机炮弹药装配禁用破片榴弹
- 🔧 修复：创造物品栏mod消失问题（JAR缺少class文件）
- 🔧 修复：1.02版本中文内容乱码

## v2.0 — 2026-08-10
- ⚖️ 平衡：钢针(APFSDS)穿深 300→60，韧性 80→40，伤害 40→30
- ⚖️ 增强：灵魂火装置伤害 15%→25%，火焰范围 1格→3×3×3
- ~~引信控制器~~ — v1.09已删除
- ~~智能引信~~ — v1.09已删除

## v1.06 — 2026-08-10
- 🆕 新增多用途榴弹

## v1.05 — 2026-08-09
- 🆕 新增铝热剂弹头、灵魂火装置

## v1.04 — 2026-08-08
- 🆕 新增高速机炮弹药

## v1.03 — 2026-08-08
- 🔧 修复 APFSDS 穿深丢失

## v1.02 — 2026-08-08
- 🔧 修复：SAP和APHE出膛即爆、引信组装问题

## v1.01 — 2026-08-08
- 🔧 修复：弹射物丢失问题
- 机炮弹药命名格式：[弹射物：弹头名称]

## v1.00beta — 2026-08-07
- 🎉 初始版本
