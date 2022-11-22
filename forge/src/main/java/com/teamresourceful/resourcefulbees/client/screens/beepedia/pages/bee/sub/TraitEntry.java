package com.teamresourceful.resourcefulbees.client.screens.beepedia.pages.bee.sub;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.data.trait.Trait;
import com.teamresourceful.resourcefullib.client.CloseablePoseStack;
import com.teamresourceful.resourcefullib.client.components.selection.ListEntry;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import com.teamresourceful.resourcefullib.client.screens.TooltipProvider;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TraitEntry extends ListEntry implements TooltipProvider {

    public static final ResourceLocation SLOT_TEXTURE = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/list_button.png");

    private boolean hovered;
    private final Trait trait;

    public TraitEntry(Trait trait) {
        this.trait = trait;
    }

    @Override
    public void render(@NotNull ScissorBoxStack scissorStack, @NotNull PoseStack stack, int id, int left, int top, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick, boolean selected) {
        this.hovered = hovered;

        try (var ignored = new CloseablePoseStack(stack)) {
            ignored.translate(left, top, 0);

            RenderUtils.bindTexture(SLOT_TEXTURE);
            Gui.blit(stack, 0, 2, 0, this.hovered ? 20 : 0, 20, 20, 20, 60);
            RenderUtils.renderItem(stack, this.trait.displayItem().getDefaultInstance(), 2, 4);

            Font font = Minecraft.getInstance().font;
            MutableComponent text = this.trait.getDisplayName().copy();
            if (!this.hovered) {
                text.withStyle(ChatFormatting.GRAY);
            }
            font.drawShadow(stack, text, 24, 8, 0xffffff);
        }
    }

    @Override
    public @NotNull List<Component> getTooltip(int mouseX, int mouseY) {
        if (this.hovered) {
            return List.of(this.trait.getDisplayName());
        }
        return List.of();
    }
}
