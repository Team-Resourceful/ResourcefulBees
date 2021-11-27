package com.teamresourceful.resourcefulbees.common.mixin.accessors;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.fonts.FontResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Minecraft.class)
public interface MinecraftAccessor {

    @Accessor("fontManager")
    FontResourceManager getFontManager();
}
