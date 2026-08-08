package com.cbcaddon.addon;

import com.cbcaddon.addon.init.ModEntities;
import com.cbcaddon.addon.init.ModItems;
import com.cbcaddon.addon.init.ModRecipeSerializers;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonProjectileRenderer;

import java.util.function.Supplier;

@Mod(CBCAddon.MOD_ID)
public class CBCAddon {
    public static final String MOD_ID = "cbcaddon";

    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    public static final Supplier<CreativeModeTab> MAIN_TAB = TABS.register("tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.cbcaddon"))
                    .icon(() -> new ItemStack(ModItems.APFSDS_AUTOCANNON_ROUND.get()))
                    .displayItems((params, output) -> {
                        output.accept(ModItems.APFSDS_AUTOCANNON_ROUND.get());
                        output.accept(ModItems.APHE_AUTOCANNON_ROUND.get());
                        output.accept(ModItems.SAP_AUTOCANNON_ROUND.get());
                        output.accept(ModItems.SHRAPNEL_AUTOCANNON_ROUND.get());
                        output.accept(ModItems.HIGH_VELOCITY_CARTRIDGE.get());
                    })
                    .build());

    public CBCAddon(IEventBus modEventBus) {
        ModEntities.ENTITY_TYPES.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);
        TABS.register(modEventBus);
        modEventBus.addListener(this::onCommonSetup);
        modEventBus.addListener(this::registerRenderers);
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        ModEntities.registerProjectileHandlers();
    }

    private void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.APFSDS_AUTOCANNON.get(),
                AutocannonProjectileRenderer::new);
        event.registerEntityRenderer(ModEntities.APHE_AUTOCANNON.get(),
                AutocannonProjectileRenderer::new);
        event.registerEntityRenderer(ModEntities.SAP_AUTOCANNON.get(),
                AutocannonProjectileRenderer::new);
        event.registerEntityRenderer(ModEntities.SHRAPNEL_AUTOCANNON.get(),
                AutocannonProjectileRenderer::new);
    }
}