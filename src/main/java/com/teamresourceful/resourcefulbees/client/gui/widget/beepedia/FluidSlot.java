package com.teamresourceful.resourcefulbees.client.gui.widget.beepedia;

import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FluidSlot extends EmptySlot {

    public FluidSlot(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }
}
