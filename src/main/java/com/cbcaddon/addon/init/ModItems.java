package com.cbcaddon.addon.init;

import com.cbcaddon.addon.CBCAddon;
import com.cbcaddon.addon.item.APFSDSAutocannonRoundItem;
import com.cbcaddon.addon.item.APHEAutocannonRoundItem;
import com.cbcaddon.addon.item.FragGrenadeRoundItem;
import com.cbcaddon.addon.item.HeavyExplosiveAutocannonRoundItem;
import com.cbcaddon.addon.item.HighVelocityAutocannonCartridgeItem;
import com.cbcaddon.addon.item.MultiPurposeAutocannonRoundItem;
import com.cbcaddon.addon.item.SAPAutocannonRoundItem;
import com.cbcaddon.addon.item.ShrapnelAutocannonRoundItem;
import com.cbcaddon.addon.item.SmokeAutocannonRoundItem;
import com.cbcaddon.addon.item.SoulFireDeviceItem;
import com.cbcaddon.addon.item.ThermiteAutocannonRoundItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, CBCAddon.MOD_ID);

    public static final Supplier<Item> APFSDS_AUTOCANNON_ROUND = ITEMS.register("apfsds_autocannon_round", () -> new APFSDSAutocannonRoundItem(new Item.Properties()));
    public static final Supplier<Item> APHE_AUTOCANNON_ROUND = ITEMS.register("aphe_autocannon_round", () -> new APHEAutocannonRoundItem(new Item.Properties()));
    public static final Supplier<Item> SAP_AUTOCANNON_ROUND = ITEMS.register("sap_autocannon_round", () -> new SAPAutocannonRoundItem(new Item.Properties()));
    public static final Supplier<Item> SHRAPNEL_AUTOCANNON_ROUND = ITEMS.register("shrapnel_autocannon_round", () -> new ShrapnelAutocannonRoundItem(new Item.Properties()));
    public static final Supplier<Item> THERMITE_AUTOCANNON_ROUND = ITEMS.register("thermite_autocannon_round", () -> new ThermiteAutocannonRoundItem(new Item.Properties()));
    public static final Supplier<Item> MULTIPURPOSE_AUTOCANNON_ROUND = ITEMS.register("multipurpose_autocannon_round", () -> new MultiPurposeAutocannonRoundItem(new Item.Properties()));
    public static final Supplier<Item> SOUL_FIRE_DEVICE = ITEMS.register("soul_fire_device", () -> new SoulFireDeviceItem(new Item.Properties().stacksTo(16)));
    public static final Supplier<Item> HIGH_VELOCITY_CARTRIDGE = ITEMS.register("high_velocity_autocannon_cartridge", () -> new HighVelocityAutocannonCartridgeItem(new Item.Properties()));
    public static final Supplier<Item> FRAG_GRENADE_ROUND = ITEMS.register("frag_grenade_autocannon_round", () -> new FragGrenadeRoundItem(new Item.Properties()));
    public static final Supplier<Item> SMOKE_AUTOCANNON_ROUND = ITEMS.register("smoke_autocannon_round", () -> new SmokeAutocannonRoundItem(new Item.Properties().stacksTo(16)));
    public static final Supplier<Item> HEAVY_EXPLOSIVE_AUTOCANNON_ROUND = ITEMS.register("heavy_explosive_autocannon_round", () -> new HeavyExplosiveAutocannonRoundItem(new Item.Properties()));
}