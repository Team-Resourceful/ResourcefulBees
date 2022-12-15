package com.teamresourceful.resourcefulbees.client.screen.beepedia.pages;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.api.data.bee.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.client.screen.beepedia.BeepediaScreen;
import com.teamresourceful.resourcefulbees.client.util.ClientRenderUtils;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.resources.storage.beepedia.BeepediaData;
import com.teamresourceful.resourcefulbees.common.resources.storage.beepedia.BeepediaSavedData;
import com.teamresourceful.resourcefulbees.common.util.MathUtils;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import com.teamresourceful.resourcefullib.common.utils.CycleableList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
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
    public void render(@NotNull PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        ClientRenderUtils.renderEntity(stack, bees.getSelected(), 85, 5, -45, 3);
        Font font = Minecraft.getInstance().font;

        font.draw(stack, Component.translatable("itemGroup.resourcefulbees.resourcefulbees").withStyle(ChatFormatting.GRAY), 41, 76, -1);
        RenderUtils.bindTexture(LOGO);
        GuiComponent.blit(stack, 41,  85, 0, 0, 104, 16, 104, 16);
        font.draw(stack, TranslationConstants.Beepedia.NAME.withStyle(ChatFormatting.GRAY), 41, 103, -1);

        font.draw(stack, getProgress(), 93 - font.width(getProgress())/2f, 133, -1);

        super.render(stack, mouseX, mouseY, partialTicks);
    }

    private Component getProgress() {
        int amount = Optional.of(Minecraft.getInstance())
                .flatMap(mc -> Optional.ofNullable(mc.player))
                .map(BeepediaSavedData::getBeepediaData)
                .map(BeepediaData::size)
                .orElse(0);
        return Component.translatable(TranslationConstants.Beepedia.PROGRESS, amount, BeeRegistry.get().getBees().size()).withStyle(ChatFormatting.GRAY);
    }

    @Override
    public void tick() {
        if (screen.getTicks() % 20 == 0) {
            bees.next();
        }
    }
}
