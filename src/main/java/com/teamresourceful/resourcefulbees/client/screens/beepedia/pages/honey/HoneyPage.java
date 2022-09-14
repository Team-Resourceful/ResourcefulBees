package com.teamresourceful.resourcefulbees.client.screens.beepedia.pages.honey;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.api.honeydata.HoneyData;
import com.teamresourceful.resourcefulbees.client.components.beepedia.ItemSlotWidget;
import com.teamresourceful.resourcefulbees.client.screens.beepedia.BeepediaTextures;
import com.teamresourceful.resourcefulbees.client.utils.ClientUtils;
import com.teamresourceful.resourcefullib.client.components.selection.ListEntry;
import com.teamresourceful.resourcefullib.client.components.selection.SelectionList;
import com.teamresourceful.resourcefullib.client.screens.HistoryScreen;
import com.teamresourceful.resourcefullib.client.screens.TooltipProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.roguelogix.phosphophyllite.repack.org.joml.Math;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.teamresourceful.resourcefulbees.client.screens.beepedia.pages.bee.BeePage.TEXTURE;

public class HoneyPage extends HistoryScreen implements TooltipProvider {

    private final HoneyData data;
    private final ItemStack honeyBottle;

    public HoneyPage(HoneyData data) {
        super(CommonComponents.EMPTY);
        this.data = data;
        this.honeyBottle = new ItemStack(data.bottleData().honeyBottle().get());
    }

    @Override
    protected void init() {
        addRenderableOnly(new ItemSlotWidget(2, 0, this.honeyBottle));
        SelectionList<ListEntry> list = addRenderableOnly(new SelectionList<>(1, 22, 182, 140, 20, ignored -> {}));
        list.updateEntries(this.data.bottleData().effects().stream().map(EffectEntry::new).toList());
    }

    @Override
    public void render(@NotNull PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        super.render(stack, mouseX, mouseY, partialTicks);

        ClientUtils.bindTexture(TEXTURE);
        Gui.blit(stack, 0, 19, 0, 0, 186, 3, 186, 3);

        Font font = Minecraft.getInstance().font;
        font.draw(stack,  this.honeyBottle.getHoverName(), 24, 1, 0xFFFFFF);
        int food = Math.min(this.data.bottleData().hunger(), 20);
        float staturation = Math.min(food * this.data.bottleData().saturation() * 2f, 20);
        ClientUtils.bindTexture(BeepediaTextures.HUNGER_BAR);
        Gui.blit(stack, 24, 10, 0, 0, 90, 9, 90, 9);

        ClientUtils.bindTexture(BeepediaTextures.SATURATION);
        float percent = staturation / 20f;
        Gui.blit(stack, 24 + 90 - (int)(percent * 90), 10, 90 - (int)(percent * 90), 0, (int)(percent * 90), 9, 90, 9);

        int amount = food / 2;
        int startX = (10 - amount) * 9;

        ClientUtils.bindTexture(BeepediaTextures.HUNGER);
        if (food % 2 == 1) Gui.blit(stack, 24 + startX - 9, 10, 0, 9, 9, 9, 9, 18);
        for (int i = 0; i < amount; i++) {
            Gui.blit(stack, 24 + startX, 10, 0, 0, 9, 9, 9, 18);
            startX += 9;
        }
    }

    @Override
    public @NotNull List<Component> getTooltip(int mouseX, int mouseY) {
        return TooltipProvider.getTooltips(this.renderables, mouseX, mouseY);
    }

    public HoneyData getData() {
        return this.data;
    }
}
