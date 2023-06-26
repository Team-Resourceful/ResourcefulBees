package com.teamresourceful.resourcefulbees.client.screen.beepedia.pages.bees;

import com.teamresourceful.resourcefulbees.client.component.selection.BaseListEntry;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.util.MathUtils;
import com.teamresourceful.resourcefullib.client.CloseablePoseStack;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import com.teamresourceful.resourcefullib.client.utils.ScreenUtils;
import com.teamresourceful.resourcefullib.common.collections.CycleableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class HoneycombEntry extends BaseListEntry {

    public static final ResourceLocation ROW_TEXTURE = new ResourceLocation(ModConstants.MOD_ID, "textures/gui/beepedia/honeycomb.png");
    public static final ResourceLocation SLOT_TEXTURE = new ResourceLocation(ModConstants.MOD_ID, "textures/gui/beepedia/list_button.png");


    private final CycleableList<ItemStack> hives;
    private final ItemStack honeycomb;
    private final boolean apiary;

    public HoneycombEntry(CycleableList<ItemStack> hives, ItemStack honeycomb, boolean apiary) {
        this.hives = hives;
        this.honeycomb = honeycomb;
        this.apiary = apiary;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, @NotNull ScissorBoxStack scissorStack, int id, int left, int top, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick, boolean selected) {
        boolean slotHovered = false;
        if (hovered) {
            if (MathUtils.inRangeInclusive(mouseX, left, left + 20)) {
                ScreenUtils.setTooltip(this.hives.getSelected().getHoverName());
                slotHovered = true;
            } else if (MathUtils.inRangeInclusive(mouseX, left + 75, left + 95)) {
                ScreenUtils.setTooltip(this.honeycomb.getHoverName());
                slotHovered = true;
            }
        }


        try (var ignored = new CloseablePoseStack(graphics)) {
            ignored.translate(left, top, 0);

            graphics.blit(ROW_TEXTURE, 24, 1, 0, this.apiary ? 25 : 0, 50, 24, 50, 50);
            graphics.blit(SLOT_TEXTURE, 0, 2, 0, slotHovered ? 20 : 0, 20, 20, 20, 60);
            graphics.renderItem(this.hives.getSelected(), 2, 4);
            graphics.blit(SLOT_TEXTURE, 75, 2, 0, slotHovered ? 20 : 0, 20, 20, 20, 60);
            graphics.renderItem(this.honeycomb, 77, 4);

            if (this.honeycomb.getCount() != 1) {
                ignored.translate(0, 0, 1000);
                String s = String.valueOf(this.honeycomb.getCount());
                Font font = Minecraft.getInstance().font;
                graphics.drawString(font, s, 93 - font.width(s), 21 - font.lineHeight, 0xffffff);
                graphics.drawString(font, s, 93 - font.width(s), 21 - font.lineHeight, 0xffffff, false);
            }
        }
    }

    public void cycle() {
        this.hives.next();
    }
}
