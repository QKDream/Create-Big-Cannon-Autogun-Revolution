# Create Big Cannons: Autocannon Revolution

机械动力火炮：机炮弹药革新 - 为 [Create Big Cannons](https://github.com/Cannoneers-of-Create/CreateBigCannons) 添加多种新型机炮弹药。

## Items / 物品

| 物品 | 类型 | 描述 |
|------|------|------|
| APFSDS Projectile | 弹头 | 动能穿甲弹，穿深60 |
| APHE Projectile | 弹头 | 穿甲高爆弹，穿深14 |
| SAP Projectile | 弹头 | 半穿甲弹，穿深8 |
| Shrapnel Projectile | 弹头 | 榴霰弹，40枚破片 |
| Thermite Projectile | 弹头 | 铝热剂弹头，方块挖掘+引燃 |
| Multi-Purpose Grenade | 弹头 | 多用途榴弹，小型破甲/爆炸 |
| Smoke/Potion Shell | 弹头 | 烟雾/药水弹，无穿甲，可装备滞留药水 |
| Frag Grenade (LV) | 弹头 | 低速破片榴弹，无穿甲，24枚子弹药二次引爆 |
| Soul Fire Device | 工具 | 与弹药合成，附加3×3×3火焰爆炸 + 25%HP伤害 |
| High-Velocity Cartridge | 弹药 | 2倍初速机炮弹药 |
| ~~Smart Fuze~~ | ~~引信~~ | ~~智能引信（v1.09已删除）~~ |
| ~~Fuze Controller~~ | ~~方块~~ | ~~引信控制器（v1.09已删除）~~ |

## Features / 特性

### 烟雾/药水弹 (v2.1)
- 无穿甲能力，无法装配灵魂火装置和Incendiary Tip
- 无药水时：小型烟雾效果（类似Smoke Shell缩小版）
- 装备滞留药水后：小型药水云效果（类似Fluid Shell缩小版）
- 物品说明显示当前附加的药水效果

### 低速破片榴弹 (v2.1)
- 无穿甲能力，弹速较低，不可用于高速机炮弹药
- 引爆后产生24枚子弹药
- 子弹药撞击实体或10tick后二次引爆，造成巨量生物伤害

### ~~智能引信系统 (v1.07-v1.08, v1.09已删除)~~
- ~~智能引信可绑定到引信控制器方块~~
- ~~引信控制器GUI可视化切换碰炸/定时/近炸模式~~
- ~~近炸模式可调触发距离（0.5-32格）~~
- ~~定时模式可调引爆时间（10-600 ticks）~~

### 高速机炮弹药
- 初速为普通弹药2倍
- 支持所有弹头类型（APFSDS/APHE/SAP/Shrapnel/Thermite/MultiPurpose/Smoke）
- 低速破片榴弹不可用于高速弹药

### 灵魂火装置
- 可附加到机炮弹药上（除高速机炮弹药外）
- 爆炸时产生3×3×3火焰 + 对周围生物造成25%最大生命伤害
- 与 Incendiary Tip 互斥

## Dependencies / 依赖
- Minecraft 1.21.1
- NeoForge 21.1+
- Create Big Cannons 5.11+
- Create 6.0+
- Ritchie's Projectile Library 2.1+

## Links / 链接
- GitHub: https://github.com/QKDream/Create-Big-Cannon-Autogun-Revolution
- Pages: https://qkdream.github.io/Create-Big-Cannon-Autogun-Revolution/