package com.cbcaddon.addon.init;

import com.cbcaddon.addon.CBCAddon;
import com.cbcaddon.addon.entity.APFSDSAutocannonProjectile;
import com.cbcaddon.addon.entity.APHEAutocannonProjectile;
import com.cbcaddon.addon.entity.SAPAutocannonProjectile;
import com.cbcaddon.addon.entity.ShrapnelAutocannonProjectile;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredRegister;
import rbasamoyai.createbigcannons.index.CBCMunitionPropertiesHandlers;
import rbasamoyai.createbigcannons.munitions.config.MunitionPropertiesHandler;

import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, CBCAddon.MOD_ID);

    public static final Supplier<EntityType<APFSDSAutocannonProjectile>> APFSDS_AUTOCANNON =
            ENTITY_TYPES.register("apfsds_autocannon",
                    () -> EntityType.Builder.<APFSDSAutocannonProjectile>of(APFSDSAutocannonProjectile::new, MobCategory.MISC)
                            .sized(0.25f, 0.25f)
                            .clientTrackingRange(4)
                            .updateInterval(1)
                            .build("apfsds_autocannon"));

    public static final Supplier<EntityType<APHEAutocannonProjectile>> APHE_AUTOCANNON =
            ENTITY_TYPES.register("aphe_autocannon",
                    () -> EntityType.Builder.<APHEAutocannonProjectile>of(APHEAutocannonProjectile::new, MobCategory.MISC)
                            .sized(0.25f, 0.25f)
                            .clientTrackingRange(4)
                            .updateInterval(1)
                            .build("aphe_autocannon"));

    public static final Supplier<EntityType<SAPAutocannonProjectile>> SAP_AUTOCANNON =
            ENTITY_TYPES.register("sap_autocannon",
                    () -> EntityType.Builder.<SAPAutocannonProjectile>of(SAPAutocannonProjectile::new, MobCategory.MISC)
                            .sized(0.25f, 0.25f)
                            .clientTrackingRange(4)
                            .updateInterval(1)
                            .build("sap_autocannon"));

    public static final Supplier<EntityType<ShrapnelAutocannonProjectile>> SHRAPNEL_AUTOCANNON =
            ENTITY_TYPES.register("shrapnel_autocannon",
                    () -> EntityType.Builder.<ShrapnelAutocannonProjectile>of(ShrapnelAutocannonProjectile::new, MobCategory.MISC)
                            .sized(0.25f, 0.25f)
                            .clientTrackingRange(4)
                            .updateInterval(1)
                            .build("shrapnel_autocannon"));

    public static void registerProjectileHandlers() {
        MunitionPropertiesHandler.registerProjectileHandler(
                APFSDS_AUTOCANNON.get(), CBCMunitionPropertiesHandlers.INERT_AUTOCANNON_PROJECTILE);
        MunitionPropertiesHandler.registerProjectileHandler(
                APHE_AUTOCANNON.get(), CBCMunitionPropertiesHandlers.FLAK_AUTOCANNON);
        MunitionPropertiesHandler.registerProjectileHandler(
                SAP_AUTOCANNON.get(), CBCMunitionPropertiesHandlers.FLAK_AUTOCANNON);
        MunitionPropertiesHandler.registerProjectileHandler(
                SHRAPNEL_AUTOCANNON.get(), CBCMunitionPropertiesHandlers.FLAK_AUTOCANNON);
    }
}