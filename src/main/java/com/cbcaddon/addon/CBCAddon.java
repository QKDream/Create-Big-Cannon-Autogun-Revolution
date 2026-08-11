package com.cbcaddon.addon;

import com.cbcaddon.addon.init.ModBlockEntities;
import com.cbcaddon.addon.init.ModBlocks;
import com.cbcaddon.addon.init.ModDataComponents;
import com.cbcaddon.addon.init.ModEntities;
import com.cbcaddon.addon.init.ModItems;
import com.cbcaddon.addon.init.ModRecipeSerializers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonCartridgeItem;
import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonProjectileRenderer;

import java.util.List;
import java.util.function.Supplier;

@Mod(CBCAddon.MOD_ID)
public class CBCAddon {
    public static final String MOD_ID = "cbcaddon";

    private static final List<Supplier<Item>> ALL_ROUNDS = List.of(
            ModItems.APFSDS_AUTOCANNON_ROUND,
            ModItems.APHE_AUTOCANNON_ROUND,
            ModItems.SAP_AUTOCANNON_ROUND,
            ModItems.SHRAPNEL_AUTOCANNON_ROUND,
            ModItems.THERMITE_AUTOCANNON_ROUND,
            ModItems.MULTIPURPOSE_AUTOCANNON_ROUND,
            ModItems.FRAG_GRENADE_ROUND,
            ModItems.SMOKE_AUTOCANNON_ROUND
    );

    private static final List<Supplier<Item>> HV_ROUNDS = List.of(
            ModItems.APFSDS_AUTOCANNON_ROUND,
            ModItems.APHE_AUTOCANNON_ROUND,
            ModItems.SAP_AUTOCANNON_ROUND,
            ModItems.SHRAPNEL_AUTOCANNON_ROUND,
            ModItems.THERMITE_AUTOCANNON_ROUND,
            ModItems.MULTIPURPOSE_AUTOCANNON_ROUND,
            ModItems.SMOKE_AUTOCANNON_ROUND
    );

    private static Item getCBCItem(String name) {
        return BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("createbigcannons", name));
    }

    private static void addCartridgeWithRound(CreativeModeTab.Output output, Item cartridgeItem, Item roundItem) {
        ItemStack cartridge = new ItemStack(cartridgeItem);
        ItemStack round = new ItemStack(roundItem);
        AutocannonCartridgeItem.writeProjectile(round, cartridge);
        output.accept(cartridge);
    }

    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    public static final Supplier<CreativeModeTab> MAIN_TAB = TABS.register("tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.cbcaddon"))
                    .icon(() -> new ItemStack(ModItems.APFSDS_AUTOCANNON_ROUND.get()))
                    .displayItems((params, output) -> {
                        for (Supplier<Item> round : ALL_ROUNDS) {
                            output.accept(round.get());
                        }
                        output.accept(ModItems.SOUL_FIRE_DEVICE.get());
                        output.accept(ModItems.HIGH_VELOCITY_CARTRIDGE.get());

                        Item cbcCartridge = getCBCItem("autocannon_cartridge");
                        if (cbcCartridge != null) {
                            for (Supplier<Item> round : ALL_ROUNDS) {
                                addCartridgeWithRound(output, cbcCartridge, round.get());
                            }
                        }

                        Item hvCartridge = ModItems.HIGH_VELOCITY_CARTRIDGE.get();
                        for (Supplier<Item> round : HV_ROUNDS) {
                            addCartridgeWithRound(output, hvCartridge, round.get());
                        }
                    })
                    .build());

    public CBCAddon(IEventBus modEventBus) {
        ModEntities.ENTITY_TYPES.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModDataComponents.DATA_COMPONENTS.register(modEventBus);
        ModRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);
        TABS.register(modEventBus);
        modEventBus.addListener(this::onCommonSetup);
        modEventBus.addListener(this::registerRenderers);
        modEventBus.addListener(this::onClientSetup);
    }

    private void onCommonSetup(FMLCommonSetupEvent event) { ModEntities.registerProjectileHandlers(); }

    private void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(ModItems.HIGH_VELOCITY_CARTRIDGE.get(),
                    ResourceLocation.fromNamespaceAndPath(MOD_ID, "has_projectile"),
                    (stack, level, entity, seed) -> AutocannonCartridgeItem.hasProjectile(stack) ? 1.0f : 0.0f);
        });
    }

    private void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.APFSDS_AUTOCANNON.get(), AutocannonProjectileRenderer::new);
        event.registerEntityRenderer(ModEntities.APHE_AUTOCANNON.get(), AutocannonProjectileRenderer::new);
        event.registerEntityRenderer(ModEntities.SAP_AUTOCANNON.get(), AutocannonProjectileRenderer::new);
        event.registerEntityRenderer(ModEntities.SHRAPNEL_AUTOCANNON.get(), AutocannonProjectileRenderer::new);
        event.registerEntityRenderer(ModEntities.THERMITE_AUTOCANNON.get(), AutocannonProjectileRenderer::new);
        event.registerEntityRenderer(ModEntities.MULTIPURPOSE_AUTOCANNON.get(), AutocannonProjectileRenderer::new);
        event.registerEntityRenderer(ModEntities.FRAG_GRENADE.get(), AutocannonProjectileRenderer::new);
        event.registerEntityRenderer(ModEntities.FRAG_SUB.get(), AutocannonProjectileRenderer::new);
        event.registerEntityRenderer(ModEntities.SMOKE_AUTOCANNON.get(), AutocannonProjectileRenderer::new);
    }
}