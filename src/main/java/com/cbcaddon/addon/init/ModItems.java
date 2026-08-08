package com.cbcaddon.addon.init;

import com.cbcaddon.addon.CBCAddon;
import com.cbcaddon.addon.item.APFSDSAutocannonCartridgeItem;
import com.cbcaddon.addon.item.APFSDSAutocannonRoundItem;
import com.cbcaddon.addon.item.APHEAutocannonCartridgeItem;
import com.cbcaddon.addon.item.APHEAutocannonRoundItem;
import com.cbcaddon.addon.item.SAPAutocannonCartridgeItem;
import com.cbcaddon.addon.item.SAPAutocannonRoundItem;
import com.cbcaddon.addon.item.ShrapnelAutocannonCartridgeItem;
import com.cbcaddon.addon.item.ShrapnelAutocannonRoundItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(Registries.ITEM, CBCAddon.MOD_ID);

    public static final Supplier<Item> APFSDS_AUTOCANNON_ROUND =
            ITEMS.register("apfsds_autocannon_round",
                    () -> new APFSDSAutocannonRoundItem(new Item.Properties()));

    public static final Supplier<Item> APHE_AUTOCANNON_ROUND =
            ITEMS.register("aphe_autocannon_round",
                    () -> new APHEAutocannonRoundItem(new Item.Properties()));

    public static final Supplier<Item> SAP_AUTOCANNON_ROUND =
            ITEMS.register("sap_autocannon_round",
                    () -> new SAPAutocannonRoundItem(new Item.Properties()));

    public static final Supplier<Item> SHRAPNEL_AUTOCANNON_ROUND =
            ITEMS.register("shrapnel_autocannon_round",
                    () -> new ShrapnelAutocannonRoundItem(new Item.Properties()));

    public static final Supplier<Item> APFSDS_AUTOCANNON_CARTRIDGE =
            ITEMS.register("apfsds_autocannon_cartridge",
                    () -> new APFSDSAutocannonCartridgeItem(new Item.Properties()));

    public static final Supplier<Item> APHE_AUTOCANNON_CARTRIDGE =
            ITEMS.register("aphe_autocannon_cartridge",
                    () -> new APHEAutocannonCartridgeItem(new Item.Properties()));

    public static final Supplier<Item> SAP_AUTOCANNON_CARTRIDGE =
            ITEMS.register("sap_autocannon_cartridge",
                    () -> new SAPAutocannonCartridgeItem(new Item.Properties()));

    public static final Supplier<Item> SHRAPNEL_AUTOCANNON_CARTRIDGE =
            ITEMS.register("shrapnel_autocannon_cartridge",
                    () -> new ShrapnelAutocannonCartridgeItem(new Item.Properties()));
}