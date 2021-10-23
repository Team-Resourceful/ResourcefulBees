package com.teamresourceful.resourcefulbees.client.gui.widget.beepedia;

import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FluidSlot extends EmptySlot {

    public FluidSlot(int x, int y, int width, int height, ITextComponent message) {
        super(x, y, width, height, message);
    }
}
