package com.teamresourceful.resourcefulbees.common.mixin.accessors;

import net.minecraft.client.gui.fonts.Font;
import net.minecraft.client.gui.fonts.FontResourceManager;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(FontResourceManager.class)
public interface FontResourceManagerAccessor {

    @Accessor("fontSets")
    Map<ResourceLocation, Font> getFontSets();
}
