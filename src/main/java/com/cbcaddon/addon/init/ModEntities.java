package com.cbcaddon.addon.init;

import com.cbcaddon.addon.CBCAddon;
import com.cbcaddon.addon.entity.APFSDSAutocannonProjectile;
import com.cbcaddon.addon.entity.APHEAutocannonProjectile;
import com.cbcaddon.addon.entity.FragGrenadeProjectile;
import com.cbcaddon.addon.entity.FragSubProjectile;
import com.cbcaddon.addon.entity.MultiPurposeAutocannonProjectile;
import com.cbcaddon.addon.entity.SAPAutocannonProjectile;
import com.cbcaddon.addon.entity.ShrapnelAutocannonProjectile;
import com.cbcaddon.addon.entity.SmokeAutocannonProjectile;
import com.cbcaddon.addon.entity.ThermiteAutocannonProjectile;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredRegister;
import rbasamoyai.createbigcannons.index.CBCMunitionPropertiesHandlers;
import rbasamoyai.createbigcannons.munitions.AbstractCannonProjectile;
import rbasamoyai.createbigcannons.munitions.config.MunitionPropertiesHandler;

import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, CBCAddon.MOD_ID);

    public static final Supplier<EntityType<APFSDSAutocannonProjectile>> APFSDS_AUTOCANNON =
            ENTITY_TYPES.register("apfsds_autocannon",
                    () -> {
                        EntityType.Builder<APFSDSAutocannonProjectile> builder = EntityType.Builder
                                .<APFSDSAutocannonProjectile>of(APFSDSAutocannonProjectile::new, MobCategory.MISC)
                                .sized(0.25f, 0.25f).clientTrackingRange(8).updateInterval(1)
                                .setShouldReceiveVelocityUpdates(true);
                        AbstractCannonProjectile.build(builder);
                        return builder.build("apfsds_autocannon");
                    });

    public static final Supplier<EntityType<APHEAutocannonProjectile>> APHE_AUTOCANNON =
            ENTITY_TYPES.register("aphe_autocannon",
                    () -> {
                        EntityType.Builder<APHEAutocannonProjectile> builder = EntityType.Builder
                                .<APHEAutocannonProjectile>of(APHEAutocannonProjectile::new, MobCategory.MISC)
                                .sized(0.25f, 0.25f).clientTrackingRange(8).updateInterval(1)
                                .setShouldReceiveVelocityUpdates(true);
                        AbstractCannonProjectile.build(builder);
                        return builder.build("aphe_autocannon");
                    });

    public static final Supplier<EntityType<SAPAutocannonProjectile>> SAP_AUTOCANNON =
            ENTITY_TYPES.register("sap_autocannon",
                    () -> {
                        EntityType.Builder<SAPAutocannonProjectile> builder = EntityType.Builder
                                .<SAPAutocannonProjectile>of(SAPAutocannonProjectile::new, MobCategory.MISC)
                                .sized(0.25f, 0.25f).clientTrackingRange(8).updateInterval(1)
                                .setShouldReceiveVelocityUpdates(true);
                        AbstractCannonProjectile.build(builder);
                        return builder.build("sap_autocannon");
                    });

    public static final Supplier<EntityType<ShrapnelAutocannonProjectile>> SHRAPNEL_AUTOCANNON =
            ENTITY_TYPES.register("shrapnel_autocannon",
                    () -> {
                        EntityType.Builder<ShrapnelAutocannonProjectile> builder = EntityType.Builder
                                .<ShrapnelAutocannonProjectile>of(ShrapnelAutocannonProjectile::new, MobCategory.MISC)
                                .sized(0.25f, 0.25f).clientTrackingRange(8).updateInterval(1)
                                .setShouldReceiveVelocityUpdates(true);
                        AbstractCannonProjectile.build(builder);
                        return builder.build("shrapnel_autocannon");
                    });

    public static final Supplier<EntityType<ThermiteAutocannonProjectile>> THERMITE_AUTOCANNON =
            ENTITY_TYPES.register("thermite_autocannon",
                    () -> {
                        EntityType.Builder<ThermiteAutocannonProjectile> builder = EntityType.Builder
                                .<ThermiteAutocannonProjectile>of(ThermiteAutocannonProjectile::new, MobCategory.MISC)
                                .sized(0.25f, 0.25f).clientTrackingRange(8).updateInterval(1)
                                .setShouldReceiveVelocityUpdates(true);
                        AbstractCannonProjectile.build(builder);
                        return builder.build("thermite_autocannon");
                    });

    public static final Supplier<EntityType<MultiPurposeAutocannonProjectile>> MULTIPURPOSE_AUTOCANNON =
            ENTITY_TYPES.register("multipurpose_autocannon",
                    () -> {
                        EntityType.Builder<MultiPurposeAutocannonProjectile> builder = EntityType.Builder
                                .<MultiPurposeAutocannonProjectile>of(MultiPurposeAutocannonProjectile::new, MobCategory.MISC)
                                .sized(0.25f, 0.25f).clientTrackingRange(8).updateInterval(1)
                                .setShouldReceiveVelocityUpdates(true);
                        AbstractCannonProjectile.build(builder);
                        return builder.build("multipurpose_autocannon");
                    });

    public static final Supplier<EntityType<FragGrenadeProjectile>> FRAG_GRENADE =
            ENTITY_TYPES.register("frag_grenade",
                    () -> {
                        EntityType.Builder<FragGrenadeProjectile> builder = EntityType.Builder
                                .<FragGrenadeProjectile>of(FragGrenadeProjectile::new, MobCategory.MISC)
                                .sized(0.25f, 0.25f).clientTrackingRange(8).updateInterval(1)
                                .setShouldReceiveVelocityUpdates(true);
                        AbstractCannonProjectile.build(builder);
                        return builder.build("frag_grenade");
                    });

    public static final Supplier<EntityType<FragSubProjectile>> FRAG_SUB =
            ENTITY_TYPES.register("frag_sub",
                    () -> {
                        EntityType.Builder<FragSubProjectile> builder = EntityType.Builder
                                .<FragSubProjectile>of(FragSubProjectile::new, MobCategory.MISC)
                                .sized(0.15f, 0.15f).clientTrackingRange(8).updateInterval(1)
                                .setShouldReceiveVelocityUpdates(true);
                        AbstractCannonProjectile.build(builder);
                        return builder.build("frag_sub");
                    });

    public static final Supplier<EntityType<SmokeAutocannonProjectile>> SMOKE_AUTOCANNON =
            ENTITY_TYPES.register("smoke_autocannon",
                    () -> {
                        EntityType.Builder<SmokeAutocannonProjectile> builder = EntityType.Builder
                                .<SmokeAutocannonProjectile>of(SmokeAutocannonProjectile::new, MobCategory.MISC)
                                .sized(0.25f, 0.25f).clientTrackingRange(8).updateInterval(1)
                                .setShouldReceiveVelocityUpdates(true);
                        AbstractCannonProjectile.build(builder);
                        return builder.build("smoke_autocannon");
                    });

    public static void registerProjectileHandlers() {
        MunitionPropertiesHandler.registerProjectileHandler(APFSDS_AUTOCANNON.get(), CBCMunitionPropertiesHandlers.INERT_AUTOCANNON_PROJECTILE);
        MunitionPropertiesHandler.registerProjectileHandler(APHE_AUTOCANNON.get(), CBCMunitionPropertiesHandlers.FLAK_AUTOCANNON);
        MunitionPropertiesHandler.registerProjectileHandler(SAP_AUTOCANNON.get(), CBCMunitionPropertiesHandlers.FLAK_AUTOCANNON);
        MunitionPropertiesHandler.registerProjectileHandler(SHRAPNEL_AUTOCANNON.get(), CBCMunitionPropertiesHandlers.FLAK_AUTOCANNON);
        MunitionPropertiesHandler.registerProjectileHandler(THERMITE_AUTOCANNON.get(), CBCMunitionPropertiesHandlers.FLAK_AUTOCANNON);
        MunitionPropertiesHandler.registerProjectileHandler(MULTIPURPOSE_AUTOCANNON.get(), CBCMunitionPropertiesHandlers.FLAK_AUTOCANNON);
        MunitionPropertiesHandler.registerProjectileHandler(FRAG_GRENADE.get(), CBCMunitionPropertiesHandlers.FLAK_AUTOCANNON);
        MunitionPropertiesHandler.registerProjectileHandler(SMOKE_AUTOCANNON.get(), CBCMunitionPropertiesHandlers.FLAK_AUTOCANNON);
        MunitionPropertiesHandler.registerProjectileHandler(FRAG_SUB.get(), CBCMunitionPropertiesHandlers.FLAK_AUTOCANNON);
    }
}