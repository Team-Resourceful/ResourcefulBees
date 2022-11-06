package com.teamresourceful.resourcefulbees.client.screens.beepedia.pages.bee;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.client.components.beepedia.SlotButton;
import com.teamresourceful.resourcefulbees.client.screens.base.SubdividedScreen;
import com.teamresourceful.resourcefulbees.client.screens.beepedia.BeepediaTextures;
import com.teamresourceful.resourcefulbees.client.screens.beepedia.pages.bee.sub.HoneycombPage;
import com.teamresourceful.resourcefulbees.client.screens.beepedia.pages.bee.sub.InfoPage;
import com.teamresourceful.resourcefulbees.client.screens.beepedia.pages.bee.sub.TraitsPage;
import com.teamresourceful.resourcefulbees.client.utils.ClientUtils;
import com.teamresourceful.resourcefulbees.common.compat.jei.JEICompat;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BeePage extends SubdividedScreen {

    public static final ResourceLocation TEXTURE = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/bee_splitter.png");

    private final CustomBeeData data;
    private final Entity bee;

    public BeePage(CustomBeeData data) {
        super(CommonComponents.EMPTY, 186, 163, 0, 51, (screen) -> new InfoPage());
        this.data = data;
        Level level = Minecraft.getInstance().level;
        this.bee = level != null ? data.getEntityType().create(level) : null;
    }

    @Override
    protected void init() {
        int x = 50;
        addRenderableWidget(new SlotButton(x, 25, BeepediaTextures.BOOK, () -> false, () -> {})).setTooltipProvider(() -> List.of(Component.literal("Info")));
        if (this.data.traitData().hasTraits()) {
            x+=22;
            addRenderableWidget(new SlotButton(x, 25, BeepediaTextures.TRAIT, () -> false,
                    () -> this.setSubScreen(new TraitsPage(this.data.traitData()))))
                    .setTooltipProvider(() -> List.of(Component.literal("Traits")));
        }
        var honeycomb = data.coreData().getHoneycombData();
        if (honeycomb.isPresent()) {
            x+=22;
            addRenderableWidget(new SlotButton(x, 25, BeepediaTextures.HOMEYCOMB, () -> false,
                    () -> this.setSubScreen(new HoneycombPage(honeycomb.get()))))
                    .setTooltipProvider(() -> List.of(Component.literal("Honeycomb")));
        }

        addRenderableWidget(new SlotButton(160, 25, BeepediaTextures.RECIPE_BOOK, () -> ModList.get().isLoaded("jei"), () -> {
            if (ModList.get().isLoaded("jei")) {
                JEICompat.searchEntity(this.data.entityType().get());
            }
        })).setTooltipProvider(() -> List.of(Component.literal("Open JEI")));
    }

    @Override
    public void renderScreen(@NotNull PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        super.renderScreen(stack, mouseX, mouseY, partialTicks);
        RenderUtils.bindTexture(TEXTURE);
        Gui.blit(stack, 0, 46, 0, 0, 186, 3, 186, 3);

        if (bee != null) {
            try (var ignored = RenderUtils.createScissorBox(Minecraft.getInstance(), stack, 0, 0, 49, 49)) {
                ClientUtils.renderEntity(stack, bee, 14, 12, -45, 2f);
            }
        }

        Font font = Minecraft.getInstance().font;
        font.draw(stack, data.displayName(), 50, 10, 0xFFFFFF);
    }

    public CustomBeeData getData() {
        return data;
    }
}
