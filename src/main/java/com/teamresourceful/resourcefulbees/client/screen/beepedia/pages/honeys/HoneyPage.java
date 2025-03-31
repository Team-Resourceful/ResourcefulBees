package com.teamresourceful.resourcefulbees.client.screen.beepedia.pages.honeys;

import com.teamresourceful.resourcefulbees.api.data.honey.CustomHoneyData;
import com.teamresourceful.resourcefulbees.client.component.ItemSlotWidget;
import com.teamresourceful.resourcefulbees.client.screen.beepedia.BeepediaTextures;
import com.teamresourceful.resourcefullib.client.components.selection.ListEntry;
import com.teamresourceful.resourcefullib.client.components.selection.SelectionList;
import com.teamresourceful.resourcefullib.client.screens.HistoryScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.teamresourceful.resourcefulbees.client.screen.beepedia.pages.bees.BeePage.TEXTURE;

public class HoneyPage extends HistoryScreen {

    private final List<Renderable> renderables = new ArrayList<>();

    private final CustomHoneyData data;
    private final ItemStack honeyBottle;

    public HoneyPage(CustomHoneyData data) {
        super(CommonComponents.EMPTY);
        this.data = data;
        this.honeyBottle = new ItemStack(data.getBottleData().bottle().get());
    }

    @Override
    protected void init() {
        addRenderableOnly(new ItemSlotWidget(2, 0, this.honeyBottle));
        SelectionList<ListEntry> list = addRenderableOnly(new SelectionList<>(1, 22, 182, 140, 20, ignored -> {}));
        list.updateEntries(this.data.getBottleData().food().effects().stream().map(EffectEntry::new).toList());
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);

        graphics.blit(TEXTURE, 0, 19, 0, 0, 186, 3, 186, 3);

        Font font = Minecraft.getInstance().font;
        graphics.drawString(font,  this.honeyBottle.getHoverName(), 24, 1, 0xFFFFFF, false);
        int food = Math.min(this.data.getBottleData().food().hunger(), 20);

        float staturation = Math.min(food * this.data.getBottleData().food().saturation() * 2f, 20);
        graphics.blit(BeepediaTextures.HUNGER_BAR, 24, 10, 0, 0, 90, 9, 90, 9);

        float percent = staturation / 20f;
        graphics.blit(BeepediaTextures.SATURATION, 24 + 90 - (int)(percent * 90), 10, 90f - (int)(percent * 90), 0, (int)(percent * 90), 9, 90, 9);

        int amount = food / 2;
        int startX = (10 - amount) * 9;

        if (food % 2 == 1) graphics.blit(BeepediaTextures.HUNGER, 24 + startX - 9, 10, 0, 9, 9, 9, 9, 18);
        for (int i = 0; i < amount; i++) {
            graphics.blit(BeepediaTextures.HUNGER, 24 + startX, 10, 0, 0, 9, 9, 9, 18);
            startX += 9;
        }
    }

    public CustomHoneyData getData() {
        return this.data;
    }

    @Override
    protected <T extends GuiEventListener & Renderable & NarratableEntry> T addRenderableWidget(T widget) {
        this.renderables.add(widget);
        return super.addRenderableWidget(widget);
    }

    @Override
    protected <T extends Renderable> T addRenderableOnly(T widget) {
        this.renderables.add(widget);
        return super.addRenderableOnly(widget);
    }
}
