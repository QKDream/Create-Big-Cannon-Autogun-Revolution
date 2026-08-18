package com.cbcaddon.addon.mixin;

import net.neoforged.fml.ModList;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

/**
 * Gates the SABLE compatibility mixins on SABLE actually being installed, so
 * that SABLE stays an optional dependency. Vanilla-targeting mixins always apply.
 *
 * NOTE: mixin plugin callbacks run during config PREPARATION, which happens
 * before FML mod containers are constructed. ModList.isLoaded() can therefore
 * report false for mods that load after cbcaddon. The primary check here is a
 * plain class probe against SABLE's core class (mod jars are on the classpath
 * by that point); ModList is only a fallback. The previous version of this
 * plugin probed SABLE mixin-INJECTED methods here, which never exist at
 * prepare time, so every SABLE mixin was silently skipped.
 */
public class CBCAddonMixinPlugin implements IMixinConfigPlugin {

    private static boolean isSableAvailable() {
        try {
            Class.forName("dev.ryanhcode.sable.Sable", false, Thread.currentThread().getContextClassLoader());
            return true;
        } catch (Throwable t) {
            try {
                return ModList.get().isLoaded("sable");
            } catch (Throwable t2) {
                return false;
            }
        }
    }

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.contains(".sable.")) {
            return isSableAvailable();
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
