package com.teamresourceful.resourcefulbees.client.gui.widget.beepedia;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaImages;
import com.teamresourceful.resourcefulbees.client.gui.widget.TooltipWidget;
import com.teamresourceful.resourcefulbees.common.utils.RenderUtils;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class EmptySlot extends TooltipWidget {

    public EmptySlot(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    @Override
    public void render(@NotNull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        RenderUtils.bindTexture(BeepediaImages.BUTTON_IMAGE);

        // TODO: 24/10/2021 Finish this class
    }
}
