package com.teamresourceful.resourcefulbees.client.gui.widget.beepedia;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EntitySlot extends EmptySlot {

    public EntitySlot(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    public static void renderEntity(PoseStack matrix, RotatingEntitySlot rotatingEntitySlot, Entity entity) {
        // TODO: 24/10/2021 finish this class
    }
}
