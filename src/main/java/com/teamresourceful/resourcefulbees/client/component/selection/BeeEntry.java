package com.teamresourceful.resourcefulbees.client.component.selection;

import com.teamresourceful.resourcefulbees.api.data.bee.CustomBeeData;
import com.teamresourceful.resourcefulbees.client.util.ClientRenderUtils;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefullib.client.CloseablePoseStack;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.function.BooleanSupplier;

public class BeeEntry extends BaseListEntry {

    public static final ResourceLocation SLOT_TEXTURE = new ResourceLocation(ModConstants.MOD_ID, "textures/gui/beepedia/list_button.png");

    private final BooleanSupplier isFound;
    private final CustomBeeData data;
    private final Entity entity;

    public BeeEntry(CustomBeeData data, BooleanSupplier isFound) {
        this.data = data;
        Level level = Minecraft.getInstance().level;
        this.entity = level == null ? null : data.entityType().create(level);
        this.isFound = isFound;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, @NotNull ScissorBoxStack scissorStack, int id, int left, int top, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick, boolean selected) {
        try (var pose = new CloseablePoseStack(graphics)) {
            pose.translate(left, top, 0);
            Font font = Minecraft.getInstance().font;
            if (this.entity != null) {
                try (var ignored2 = RenderUtils.createScissorBoxStack(scissorStack, Minecraft.getInstance(), pose, 3, 2, 16, 16)) {
                    ClientRenderUtils.renderEntity(graphics, this.entity, 1, 4, 45f, 0.75f);
                }
            }
            graphics.blit(SLOT_TEXTURE, 1, 0, 0, selected ? 40 : hovered ? 20 : 0, 20, 20, 20, 60);

            MutableComponent component = this.isFound.getAsBoolean() ? Component.literal("✦ ").withStyle(ChatFormatting.GREEN) : Component.literal("✧ ");
            component.append(this.data.displayName().copy().withStyle(selected ? ChatFormatting.WHITE : ChatFormatting.GRAY));

            graphics.drawString(font, component, 22, 5, -1);
        }
    }

    public CustomBeeData getData() {
        return this.data;
    }
}
