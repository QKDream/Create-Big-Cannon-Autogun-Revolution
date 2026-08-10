package com.cbcaddon.addon.init;

import com.cbcaddon.addon.CBCAddon;
import com.cbcaddon.addon.gui.FuzeControllerMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(Registries.MENU, CBCAddon.MOD_ID);

    public static final Supplier<MenuType<FuzeControllerMenu>> FUZE_CONTROLLER =
            MENU_TYPES.register("fuze_controller",
                    () -> IMenuTypeExtension.create(FuzeControllerMenu::new));
}