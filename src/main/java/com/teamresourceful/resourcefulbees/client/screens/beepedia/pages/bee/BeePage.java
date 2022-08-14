package com.teamresourceful.resourcefulbees.client.screens.beepedia.pages.bee;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.client.components.beepedia.SlotButton;
import com.teamresourceful.resourcefulbees.client.screens.base.SubdividedScreen;
import com.teamresourceful.resourcefulbees.client.screens.beepedia.BeepediaTextures;
import com.teamresourceful.resourcefulbees.client.utils.ClientUtils;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BeePage extends SubdividedScreen {

    public static final ResourceLocation TEXTURE = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/bee_splitter.png");

    private final CustomBeeData data;
    private final Entity bee;

    public BeePage(CustomBeeData data) {
        super(CommonComponents.EMPTY, 186, 163, 0, 51);
        this.data = data;
        Level level = Minecraft.getInstance().level;
        this.bee = level != null ? data.getEntityType().create(level) : null;
    }

    @Override
    protected void init() {
        addRenderableWidget(new SlotButton(50, 25, BeepediaTextures.BOOK, () -> false, () -> {})).setTooltipProvider(() -> List.of(Component.literal("Info")));
        addRenderableWidget(new SlotButton(72, 25, BeepediaTextures.TRAIT, () -> false, () -> {})).setTooltipProvider(() -> List.of(Component.literal("Traits")));
        addRenderableWidget(new SlotButton(94, 25, BeepediaTextures.HOMEYCOMB, () -> false, () -> {})).setTooltipProvider(() -> List.of(Component.literal("Honeycombs")));
        addRenderableWidget(new SlotButton(138, 25, BeepediaTextures.COMPASS, () -> false, () -> {})).setTooltipProvider(() -> List.of(Component.literal("Open Bee Locator")));
        addRenderableWidget(new SlotButton(160, 25, BeepediaTextures.RECIPE_BOOK, () -> false, () -> {})).setTooltipProvider(() -> List.of(Component.literal("Open JEI")));
    }

    @Override
    public void renderScreen(@NotNull PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        super.renderScreen(stack, mouseX, mouseY, partialTicks);
        RenderUtils.bindTexture(TEXTURE);
        Gui.blit(stack, 0, 46, 0, 0, 186, 3, 186, 3);

        if (bee != null) {
            try (var ignored = RenderUtils.createScissorBox(Minecraft.getInstance(), stack, 0, 0, 49, 49)) {
                ClientUtils.renderEntity(stack, bee, 14, 0, -45, 2f);
            }
        }

        Font font = Minecraft.getInstance().font;
        font.draw(stack, data.displayName(), 50, 10, 0xFFFFFF);
    }

    public CustomBeeData getData() {
        return data;
    }
}
