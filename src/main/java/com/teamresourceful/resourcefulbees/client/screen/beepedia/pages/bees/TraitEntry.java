package com.teamresourceful.resourcefulbees.client.screen.beepedia.pages.bees;

import com.teamresourceful.resourcefulbees.api.data.trait.Trait;
import com.teamresourceful.resourcefulbees.client.component.selection.BaseListEntry;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.util.MathUtils;
import com.teamresourceful.resourcefullib.client.CloseablePoseStack;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import com.teamresourceful.resourcefullib.client.utils.ScreenUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class TraitEntry extends BaseListEntry {

    public static final ResourceLocation SLOT_TEXTURE = new ResourceLocation(ModConstants.MOD_ID, "textures/gui/beepedia/list_button.png");

    private final Trait trait;

    public TraitEntry(Trait trait) {
        this.trait = trait;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, @NotNull ScissorBoxStack scissorStack, int id, int left, int top, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick, boolean selected) {
        try (var pose = new CloseablePoseStack(graphics)) {
            pose.translate(left, top, 0);

            graphics.blit(SLOT_TEXTURE, 0, 2, 0, hovered ? 20 : 0, 20, 20, 20, 60);
            graphics.renderItem(this.trait.displayItem().getDefaultInstance(), 2, 4);

            Font font = Minecraft.getInstance().font;
            MutableComponent text = this.trait.getDisplayName().copy();
            if (!hovered) {
                text.withStyle(ChatFormatting.GRAY);
            }
            graphics.drawString(font, text, 24, 8, 0xffffff);
        }

        if (hovered && MathUtils.inRangeInclusive(mouseX, left, left + width) && MathUtils.inRangeInclusive(mouseY, top, top + height)) {
            ScreenUtils.setTooltip(this.trait.getDisplayName());
        }
    }

    public Trait getTrait() {
        return this.trait;
    }
}
