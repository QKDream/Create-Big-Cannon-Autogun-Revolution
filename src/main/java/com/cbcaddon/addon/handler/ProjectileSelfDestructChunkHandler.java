package com.cbcaddon.addon.handler;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.event.level.ChunkEvent;
import rbasamoyai.createbigcannons.munitions.AbstractCannonProjectile;

/**
 * When a chunk unloads, airborne cbcaddon projectiles inside it would freeze
 * forever (entities in unloaded chunks are not ticked). Discard them so they
 * neither linger nor reappear when the chunk loads again.
 */
public final class ProjectileSelfDestructChunkHandler {

    private ProjectileSelfDestructChunkHandler() {
    }

    public static void onChunkUnload(ChunkEvent.Unload event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;
        ChunkAccess chunk = event.getChunk();
        if (chunk == null) return;
        AABB box = new AABB(
                chunk.getPos().getMinBlockX(), serverLevel.getMinBuildHeight(),
                chunk.getPos().getMinBlockZ(),
                chunk.getPos().getMaxBlockX() + 1, serverLevel.getMaxBuildHeight(),
                chunk.getPos().getMaxBlockZ() + 1);
        List<Entity> toDiscard = new ArrayList<>();
        for (Entity entity : serverLevel.getEntities((Entity) null, box, e -> e instanceof AbstractCannonProjectile proj
                && !proj.isInGround()
                && proj.getType().toShortString().startsWith("cbcaddon:"))) {
            toDiscard.add(entity);
        }
        for (Entity entity : toDiscard) {
            entity.discard();
        }
    }
}