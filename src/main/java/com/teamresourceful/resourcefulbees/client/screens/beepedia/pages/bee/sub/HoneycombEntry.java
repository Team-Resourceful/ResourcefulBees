package com.teamresourceful.resourcefulbees.client.screens.beepedia.pages.bee.sub;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.utils.MathUtils;
import com.teamresourceful.resourcefulbees.common.utils.TriState;
import com.teamresourceful.resourcefullib.client.CloseablePoseStack;
import com.teamresourceful.resourcefullib.client.components.selection.ListEntry;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import com.teamresourceful.resourcefullib.client.screens.TooltipProvider;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import com.teamresourceful.resourcefullib.common.utils.CycleableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HoneycombEntry extends ListEntry implements TooltipProvider {

    public static final ResourceLocation ROW_TEXTURE = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/honeycomb.png");
    public static final ResourceLocation SLOT_TEXTURE = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/list_button.png");

    private TriState state = TriState.UNSET;

    private final CycleableList<ItemStack> hives;
    private final ItemStack honeycomb;
    private final boolean apiary;

    public HoneycombEntry(CycleableList<ItemStack> hives, ItemStack honeycomb, boolean apiary) {
        this.hives = hives;
        this.honeycomb = honeycomb;
        this.apiary = apiary;
    }

    @Override
    public void render(@NotNull ScissorBoxStack scissorStack, @NotNull PoseStack stack, int id, int left, int top, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick, boolean selected) {
        this.state = TriState.UNSET;
        if (hovered) {
            if (MathUtils.inRangeInclusive(mouseX, left, left + 20)) {
                this.state = TriState.TRUE;
            } else if (MathUtils.inRangeInclusive(mouseX, left + 75, left + 95)) {
                this.state = TriState.FALSE;
            }
        }


        try (var ignored = new CloseablePoseStack(stack)) {
            ignored.translate(left, top, 0);

            RenderUtils.bindTexture(ROW_TEXTURE);
            Gui.blit(stack, 24, 1, 0, this.apiary ? 25 : 0, 50, 24, 50, 50);

            RenderUtils.bindTexture(SLOT_TEXTURE);
            Gui.blit(stack, 0, 2, 0, this.state.isTrue() ? 20 : 0, 20, 20, 20, 60);
            RenderUtils.renderItem(stack, this.hives.getSelected(), 2, 4);

            RenderUtils.bindTexture(SLOT_TEXTURE);
            Gui.blit(stack, 75, 2, 0, this.state.isFalse() ? 20 : 0, 20, 20, 20, 60);
            RenderUtils.renderItem(stack, this.honeycomb, 77, 4);

            if (this.honeycomb.getCount() != 1) {
                ignored.translate(0, 0, 1000);
                String s = String.valueOf(this.honeycomb.getCount());
                Font font = Minecraft.getInstance().font;
                font.drawShadow(stack, s, 93f - font.width(s), 21f - font.lineHeight, 0xffffff);
                font.draw(stack, s, 93f - font.width(s), 21f - font.lineHeight, 0xffffff);
            }
        }
    }

    public void cycle() {
        this.hives.next();
    }

    @Override
    public @NotNull List<Component> getTooltip(int mouseX, int mouseY) {
        if (this.state.isSet()) {
            if (this.state.isTrue()) {
                return List.of(this.hives.getSelected().getHoverName());
            } else {
                return List.of(this.honeycomb.getHoverName());
            }
        }
        return List.of();
    }
}
