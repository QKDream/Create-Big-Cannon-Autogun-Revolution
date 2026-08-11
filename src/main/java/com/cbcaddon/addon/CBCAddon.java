package com.cbcaddon.addon;

import com.cbcaddon.addon.init.ModBlockEntities;
import com.cbcaddon.addon.init.ModBlocks;
import com.cbcaddon.addon.init.ModDataComponents;
import com.cbcaddon.addon.init.ModEntities;
import com.cbcaddon.addon.init.ModItems;
import com.cbcaddon.addon.init.ModRecipeSerializers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonCartridgeItem;
import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonProjectileRenderer;

import java.util.function.Supplier;

@Mod(CBCAddon.MOD_ID)
public class CBCAddon {
    public static final String MOD_ID = "cbcaddon";

    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    private static final ResourceLocation FILLED_CARTRIDGE_ID =
            ResourceLocation.fromNamespaceAndPath("createbigcannons", "filled_autocannon_cartridge");

    public static final Supplier<CreativeModeTab> MAIN_TAB = TABS.register("tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.cbcaddon"))
                    .icon(() -> new ItemStack(ModItems.APFSDS_AUTOCANNON_ROUND.get()))
                    .displayItems((params, output) -> {
                        // All rounds
                        output.accept(ModItems.APFSDS_AUTOCANNON_ROUND.get());
                        output.accept(ModItems.APHE_AUTOCANNON_ROUND.get());
                        output.accept(ModItems.SAP_AUTOCANNON_ROUND.get());
                        output.accept(ModItems.SHRAPNEL_AUTOCANNON_ROUND.get());
                        output.accept(ModItems.THERMITE_AUTOCANNON_ROUND.get());
                        output.accept(ModItems.MULTIPURPOSE_AUTOCANNON_ROUND.get());
                        output.accept(ModItems.SMOKE_AUTOCANNON_ROUND.get());
                        output.accept(ModItems.FRAG_GRENADE_AUTOCANNON_ROUND.get());
                        // Tools
                        output.accept(ModItems.SOUL_FIRE_DEVICE.get());
                        output.accept(ModItems.HIGH_VELOCITY_CARTRIDGE.get());
                        // Standard Filled Cartridge + round combos
                        Item filledCartridge = BuiltInRegistries.ITEM.get(FILLED_CARTRIDGE_ID);
                        if (filledCartridge != null) {
                            addCartridgeCombo(output, filledCartridge, ModItems.APFSDS_AUTOCANNON_ROUND.get());
                            addCartridgeCombo(output, filledCartridge, ModItems.APHE_AUTOCANNON_ROUND.get());
                            addCartridgeCombo(output, filledCartridge, ModItems.SAP_AUTOCANNON_ROUND.get());
                            addCartridgeCombo(output, filledCartridge, ModItems.SHRAPNEL_AUTOCANNON_ROUND.get());
                            addCartridgeCombo(output, filledCartridge, ModItems.THERMITE_AUTOCANNON_ROUND.get());
                            addCartridgeCombo(output, filledCartridge, ModItems.MULTIPURPOSE_AUTOCANNON_ROUND.get());
                            addCartridgeCombo(output, filledCartridge, ModItems.SMOKE_AUTOCANNON_ROUND.get());
                            addCartridgeCombo(output, filledCartridge, ModItems.FRAG_GRENADE_AUTOCANNON_ROUND.get());
                        }
                        // HV Cartridge + round combos (no frag grenade)
                        Item hvCartridge = ModItems.HIGH_VELOCITY_CARTRIDGE.get();
                        addCartridgeCombo(output, hvCartridge, ModItems.APFSDS_AUTOCANNON_ROUND.get());
                        addCartridgeCombo(output, hvCartridge, ModItems.APHE_AUTOCANNON_ROUND.get());
                        addCartridgeCombo(output, hvCartridge, ModItems.SAP_AUTOCANNON_ROUND.get());
                        addCartridgeCombo(output, hvCartridge, ModItems.SHRAPNEL_AUTOCANNON_ROUND.get());
                        addCartridgeCombo(output, hvCartridge, ModItems.THERMITE_AUTOCANNON_ROUND.get());
                        addCartridgeCombo(output, hvCartridge, ModItems.MULTIPURPOSE_AUTOCANNON_ROUND.get());
                        addCartridgeCombo(output, hvCartridge, ModItems.SMOKE_AUTOCANNON_ROUND.get());
                    })
                    .build());

    private static void addCartridgeCombo(CreativeModeTab.Output output, Item cartridgeItem, Item round) {
        ItemStack cartridge = new ItemStack(cartridgeItem);
        ItemStack projectile = new ItemStack(round);
        AutocannonCartridgeItem.writeProjectile(projectile, cartridge);
        output.accept(cartridge);
    }

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
    }

    private void onCommonSetup(FMLCommonSetupEvent event) { ModEntities.registerProjectileHandlers(); }

    private void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.APFSDS_AUTOCANNON.get(), AutocannonProjectileRenderer::new);
        event.registerEntityRenderer(ModEntities.APHE_AUTOCANNON.get(), AutocannonProjectileRenderer::new);
        event.registerEntityRenderer(ModEntities.SAP_AUTOCANNON.get(), AutocannonProjectileRenderer::new);
        event.registerEntityRenderer(ModEntities.SHRAPNEL_AUTOCANNON.get(), AutocannonProjectileRenderer::new);
        event.registerEntityRenderer(ModEntities.THERMITE_AUTOCANNON.get(), AutocannonProjectileRenderer::new);
        event.registerEntityRenderer(ModEntities.MULTIPURPOSE_AUTOCANNON.get(), AutocannonProjectileRenderer::new);
        event.registerEntityRenderer(ModEntities.SMOKE_AUTOCANNON.get(), AutocannonProjectileRenderer::new);
        event.registerEntityRenderer(ModEntities.FRAG_GRENADE_AUTOCANNON.get(), AutocannonProjectileRenderer::new);
    }
}