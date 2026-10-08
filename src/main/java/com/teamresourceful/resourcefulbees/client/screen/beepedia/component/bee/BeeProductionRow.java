package com.teamresourceful.resourcefulbees.client.screen.beepedia.component.bee;

import com.teamresourceful.resourcefulbees.api.tiers.ApiaryTier;
import com.teamresourceful.resourcefulbees.api.tiers.BeehiveTier;
import com.teamresourceful.resourcefulbees.client.component.CyclingItemStackDisplayWidget;
import com.teamresourceful.resourcefulbees.client.util.BeepediaScreenUtil;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.List;

public final class BeeProductionRow extends AbstractWidget implements BeepediaTickable {

    private static final Identifier HIVE = ModIdentifier.of("beepedia/hive");
    private static final Identifier APIARY = ModIdentifier.of("beepedia/apiary");

    private static final int HEIGHT = 24;

    private final CyclingItemStackDisplayWidget input;
    private final ItemStack output;
    private final boolean apiary;

    private BeeProductionRow(List<ItemStack> inputs, ItemStack output, boolean apiary, int width) {
        super(0, 0, width, HEIGHT, Component.empty());

        this.input = new CyclingItemStackDisplayWidget(inputs);
        this.output = output;
        this.apiary = apiary;
    }

    public static BeeProductionRow hive(BeehiveTier tier, ItemStack output, int width) {
        List<ItemStack> inputs = tier.getDisplayItems()
                .stream()
                .map(ItemStack::new)
                .toList();

        return new BeeProductionRow(inputs, output, false, width);
    }

    public static BeeProductionRow apiary(ApiaryTier tier, ItemStack output, int width) {
        return new BeeProductionRow(List.of(new ItemStack(tier.getItem())), output, true, width);
    }


    @Override
    protected void extractWidgetRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        ItemStack inputStack = input.getStack();

        if (inputStack.isEmpty()) {
            return;
        }

        int left = getX() + 8;
        int top = getY();

        int inputX = left + 2;
        int itemY = top + 4;

        int outputX = left + 77;

        input.setPosition(inputX, itemY);
        input.extractRenderState(graphics, mouseX, mouseY, partialTick);

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, apiary ? APIARY : HIVE, left + 24, top, 50, 24);

        graphics.item(output, outputX, itemY);
        graphics.itemDecorations(Minecraft.getInstance().font, output, outputX, itemY);

        if (BeepediaScreenUtil.isInside(mouseX, mouseY, outputX, itemY, 16, 16)) {
            graphics.setTooltipForNextFrame(Minecraft.getInstance().font, output, mouseX, mouseY);
        }
    }

    @Override
    protected void updateWidgetNarration(@NonNull NarrationElementOutput narration) {
        ItemStack inputStack = input.getStack();

        if (inputStack.isEmpty()) {
            return;
        }

        narration.add(NarratedElementType.TITLE, Component.translatable("narrator.resourcefulbees.beepedia.production", inputStack.getHoverName(), output.getHoverName()));
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        return false;
    }

    @Override
    public void tick() {
        input.tick();
    }
}