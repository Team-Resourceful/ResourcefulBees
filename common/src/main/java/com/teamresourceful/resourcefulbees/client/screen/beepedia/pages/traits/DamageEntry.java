package com.teamresourceful.resourcefulbees.client.screen.beepedia.pages.traits;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.api.data.trait.DamageType;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefullib.client.CloseablePoseStack;
import com.teamresourceful.resourcefullib.client.components.selection.ListEntry;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import static com.teamresourceful.resourcefulbees.client.component.selection.BeeEntry.SLOT_TEXTURE;

public class DamageEntry extends ListEntry {

    private final ItemStack icon;
    private final String id;
    private final int amplifier;

    public DamageEntry(DamageType type) {
        this(type.type(), type.amplifier());
    }

    public DamageEntry(String id, int amplifier) {
        this.id = id;
        this.amplifier = amplifier;
        this.icon = switch (id) {
            case "explosive" -> new ItemStack(Items.TNT);
            case "setOnFire" -> new ItemStack(Items.BLAZE_POWDER);
            default -> new ItemStack(Items.BARRIER);
        };
    }

    @Override
    protected void render(@NotNull ScissorBoxStack scissorStack, @NotNull PoseStack stack, int id, int left, int top, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick, boolean selected) {
        try (var ignored = new CloseablePoseStack(stack)) {
            ignored.translate(left, top, 0);
            Minecraft instance = Minecraft.getInstance();
            Font font = instance.font;

            RenderUtils.renderItem(stack, this.icon, 3, 2);

            RenderUtils.bindTexture(SLOT_TEXTURE);
            GuiComponent.blit(stack, 1, 0, 0, 0, 20, 20, 20, 60);

            GuiComponent.drawString(stack, font, Component.translatable("damage_type.resourcefulbees."+ this.id), 24, 1, 0x55FF55);
            GuiComponent.drawString(stack, font, Component.translatable(TranslationConstants.Beepedia.Traits.AMPLIFIER, this.amplifier), 24, 11, 0xAAAAAA);
            GuiComponent.drawString(stack, font, Component.translatable("damage_type.resourcefulbees.desc."+ this.id), 24, 21, 0xAAAAAA);
        }
    }
}