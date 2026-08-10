package com.cbcaddon.addon;

import com.cbcaddon.addon.gui.FuzeControllerScreen;
import com.cbcaddon.addon.init.ModBlockEntities;
import com.cbcaddon.addon.init.ModBlocks;
import com.cbcaddon.addon.init.ModDataComponents;
import com.cbcaddon.addon.init.ModEntities;
import com.cbcaddon.addon.init.ModItems;
import com.cbcaddon.addon.init.ModMenuTypes;
import com.cbcaddon.addon.init.ModRecipeSerializers;
import com.cbcaddon.addon.network.FuzeUpdatePacket;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
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
                        output.accept(ModItems.THERMITE_AUTOCANNON_ROUND.get());
                        output.accept(ModItems.MULTIPURPOSE_AUTOCANNON_ROUND.get());
                        output.accept(ModItems.SOUL_FIRE_DEVICE.get());
                        output.accept(ModItems.HIGH_VELOCITY_CARTRIDGE.get());
                        output.accept(ModItems.SMART_FUZE.get());
                        output.accept(ModItems.FUZE_CONTROLLER.get());
                    })
                    .build());

    public CBCAddon(IEventBus modEventBus) {
        ModEntities.ENTITY_TYPES.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModDataComponents.DATA_COMPONENTS.register(modEventBus);
        ModRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);
        ModMenuTypes.MENU_TYPES.register(modEventBus);
        TABS.register(modEventBus);
        modEventBus.addListener(this::onCommonSetup);
        modEventBus.addListener(this::registerRenderers);
        modEventBus.addListener(this::registerScreens);
        modEventBus.addListener(this::registerPayloads);
    }

    private void onCommonSetup(FMLCommonSetupEvent event) { ModEntities.registerProjectileHandlers(); }

    private void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.APFSDS_AUTOCANNON.get(), AutocannonProjectileRenderer::new);
        event.registerEntityRenderer(ModEntities.APHE_AUTOCANNON.get(), AutocannonProjectileRenderer::new);
        event.registerEntityRenderer(ModEntities.SAP_AUTOCANNON.get(), AutocannonProjectileRenderer::new);
        event.registerEntityRenderer(ModEntities.SHRAPNEL_AUTOCANNON.get(), AutocannonProjectileRenderer::new);
        event.registerEntityRenderer(ModEntities.THERMITE_AUTOCANNON.get(), AutocannonProjectileRenderer::new);
        event.registerEntityRenderer(ModEntities.MULTIPURPOSE_AUTOCANNON.get(), AutocannonProjectileRenderer::new);
    }

    private void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.FUZE_CONTROLLER.get(), FuzeControllerScreen::new);
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(FuzeUpdatePacket.TYPE, FuzeUpdatePacket.STREAM_CODEC, FuzeUpdatePacket::handle);
    }
}