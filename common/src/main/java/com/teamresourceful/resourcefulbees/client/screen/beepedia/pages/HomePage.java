package com.teamresourceful.resourcefulbees.client.screen.beepedia.pages;

import com.teamresourceful.resourcefulbees.api.data.bee.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.client.screen.beepedia.BeepediaScreen;
import com.teamresourceful.resourcefulbees.client.util.ClientRenderUtils;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.BeepediaTranslations;
import com.teamresourceful.resourcefulbees.common.resources.storage.beepedia.BeepediaData;
import com.teamresourceful.resourcefulbees.common.resources.storage.beepedia.BeepediaSavedData;
import com.teamresourceful.resourcefulbees.common.util.MathUtils;
import com.teamresourceful.resourcefullib.common.collections.CycleableList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class HomePage extends Screen {

    public static final ResourceLocation LOGO = new ResourceLocation(ModConstants.MOD_ID, "textures/gui/beepedia/logo.png");

    private final CycleableList<Entity> bees;

    private final BeepediaScreen screen;

    public HomePage(BeepediaScreen screen) {
        super(screen.getTitle());
        this.screen = screen;

        this.bees = BeeRegistry.get()
            .getStreamOfBees()
            .map(CustomBeeData::entityType)
            .map(type -> type.create(Minecraft.getInstance().level))
            .collect(CycleableList::new, CycleableList::add, CycleableList::addAll);

        this.bees.setSelectedIndex(MathUtils.RANDOM.nextInt(this.bees.size())); //Set random starting bee instead of always the same bee.
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        ClientRenderUtils.renderEntity(graphics, bees.getSelected(), 85, 5, -45, 3);
        Font font = Minecraft.getInstance().font;

        graphics.drawString(font, Component.translatable("itemGroup.resourcefulbees.resourcefulbees").withStyle(ChatFormatting.GRAY), 41, 76, -1, false);
        graphics.blit(LOGO, 41,  85, 0, 0, 104, 16, 104, 16);
        graphics.drawString(font, BeepediaTranslations.NAME.withStyle(ChatFormatting.GRAY), 41, 103, -1, false);

        graphics.drawString(font, getProgress(), 93 - font.width(getProgress())/2, 133, -1, false);

        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    private Component getProgress() {
        int amount = Optional.of(Minecraft.getInstance())
                .flatMap(mc -> Optional.ofNullable(mc.player))
                .map(BeepediaSavedData::getBeepediaData)
                .map(BeepediaData::size)
                .orElse(0);
        return Component.translatable(BeepediaTranslations.PROGRESS, amount, BeeRegistry.get().getBees().size()).withStyle(ChatFormatting.GRAY);
    }

    @Override
    public void tick() {
        if (screen.getTicks() % 20 == 0) {
            bees.next();
        }
    }
}
