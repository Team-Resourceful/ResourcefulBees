package com.teamresourceful.resourcefulbees.client.screens.beepedia.pages;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaImages;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaLang;
import com.teamresourceful.resourcefulbees.client.screens.beepedia.BeepediaScreen;
import com.teamresourceful.resourcefulbees.client.utils.ClientUtils;
import com.teamresourceful.resourcefulbees.common.registry.custom.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.utils.MathUtils;
import com.teamresourceful.resourcefullib.common.utils.CycleableList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class HomePage extends Screen {

    private final CycleableList<Entity> bees;

    private final BeepediaScreen screen;

    public HomePage(BeepediaScreen screen) {
        super(screen.getTitle());
        this.screen = screen;

        //noinspection ConstantConditions
        this.bees = BeeRegistry.getRegistry()
                .getSetOfBees()
                .stream()
                .map(CustomBeeData::getEntityType)
                .map(type -> type.create(Minecraft.getInstance().level))
                .collect(CycleableList::new, CycleableList::add, CycleableList::addAll);

        this.bees.setSelectedIndex(MathUtils.RANDOM.nextInt(this.bees.size())); //Set random starting bee instead of always the same bee.
    }

    @Override
    public void render(@NotNull PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        ClientUtils.renderEntity(stack, bees.getSelected(), 85, 5, -45, 3);
        Font font = Minecraft.getInstance().font;

        font.draw(stack, BeepediaLang.ITEM_GROUP.withStyle(ChatFormatting.GRAY), 41, 76, -1);
        ClientUtils.bindTexture(BeepediaImages.LOGO);
        Gui.blit(stack, 41,  85, 0, 0, 104, 16, 104, 16);
        font.draw(stack, BeepediaLang.VERSION_NUMBER.withStyle(ChatFormatting.GRAY), 41, 103, -1);

        ClientUtils.drawCenteredString(font, stack, getProgress(), 93, 133, -1, false);

        super.render(stack, mouseX, mouseY, partialTicks);
    }

    private Component getProgress() {
        MutableComponent prefix = BeepediaLang.COLLECTION_PROGRESS.plainCopy();
        //TODO fill in data.
        prefix.append(String.format("%d / %d", 1, BeeRegistry.getRegistry().getBees().size()));
        prefix.withStyle(ChatFormatting.GRAY);
        return prefix;
    }

    @Override
    public void tick() {
        if (screen.ticks % 20 == 0) {
            bees.next();
        }
    }
}
