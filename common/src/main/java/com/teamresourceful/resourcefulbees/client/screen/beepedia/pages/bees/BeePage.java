package com.teamresourceful.resourcefulbees.client.screen.beepedia.pages.bees;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.api.data.bee.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.data.trait.Trait;
import com.teamresourceful.resourcefulbees.client.component.SlotButton;
import com.teamresourceful.resourcefulbees.client.screen.base.SubdividedScreen;
import com.teamresourceful.resourcefulbees.client.screen.beepedia.BeepediaTextures;
import com.teamresourceful.resourcefulbees.client.util.ClientRenderUtils;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.platform.common.util.ModUtils;
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
import java.util.function.Consumer;

public class BeePage extends SubdividedScreen {

    public static final ResourceLocation TEXTURE = new ResourceLocation(ModConstants.MOD_ID, "textures/gui/beepedia/bee_splitter.png");

    private final Consumer<Trait> traitOpener;

    private final CustomBeeData data;
    private final Entity bee;

    public BeePage(CustomBeeData data, Consumer<Trait> traitOpener) {
        super(CommonComponents.EMPTY, 186, 163, 0, 51, (screen) -> new InfoPage());
        this.data = data;
        this.traitOpener = traitOpener;
        Level level = Minecraft.getInstance().level;
        this.bee = level != null ? data.entityType().create(level) : null;
    }

    @Override
    protected void init() {
        int x = 50;
        addRenderableWidget(new SlotButton(x, 25, BeepediaTextures.BOOK, () -> false, () -> {})).setTooltipProvider(() -> List.of(Component.literal("Info")));
        if (this.data.getTraitData().hasTraits()) {
            x+=22;
            addRenderableWidget(new SlotButton(x, 25, BeepediaTextures.TRAIT, () -> false,
                    () -> this.setSubScreen(new TraitsPage(this.data.getTraitData(), traitOpener))))
                    .setTooltipProvider(() -> List.of(Component.literal("Traits")));
        }
        var honeycomb = data.getCoreData().getHoneycombData();
        if (honeycomb.isPresent()) {
            x+=22;
            addRenderableWidget(new SlotButton(x, 25, BeepediaTextures.HOMEYCOMB, () -> false,
                    () -> this.setSubScreen(new HoneycombPage(honeycomb.get()))))
                    .setTooltipProvider(() -> List.of(Component.literal("Honeycomb")));
        }

        addRenderableWidget(new SlotButton(160, 25, BeepediaTextures.RECIPE_BOOK, () -> ModUtils.isModLoaded("jei"), () -> {
            if (ModUtils.isModLoaded("jei")) {
                ModUtils.openEntityInJEI(this.data.entityType());
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
                ClientRenderUtils.renderEntity(stack, bee, 14, 12, -45, 2f);
            }
        }

        Font font = Minecraft.getInstance().font;
        font.draw(stack, data.displayName(), 50, 10, 0xFFFFFF);
    }

    public CustomBeeData getData() {
        return data;
    }
}
