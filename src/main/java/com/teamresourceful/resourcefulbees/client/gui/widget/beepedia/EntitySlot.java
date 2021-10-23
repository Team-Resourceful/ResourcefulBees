package com.teamresourceful.resourcefulbees.client.gui.widget.beepedia;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EntitySlot extends EmptySlot {

    public EntitySlot(int x, int y, int width, int height, ITextComponent message) {
        super(x, y, width, height, message);
    }

    public static void renderEntity(MatrixStack matrix, RotatingEntitySlot rotatingEntitySlot, Entity entity) {
        // TODO: 24/10/2021 finish this class
    }
}
