package com.teamresourceful.resourcefulbees.mixin.client;

import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(Screen.class)
public interface ScreenAccessor {

    @Accessor("renderables")
    List<Widget> getRenderables();
}
