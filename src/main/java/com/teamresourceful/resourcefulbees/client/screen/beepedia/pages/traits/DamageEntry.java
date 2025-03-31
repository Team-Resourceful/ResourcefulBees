package com.teamresourceful.resourcefulbees.client.screen.beepedia.pages.traits;

import com.teamresourceful.resourcefulbees.api.data.trait.TraitDamageType;
import com.teamresourceful.resourcefulbees.client.component.selection.BaseListEntry;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.BeepediaTranslations;
import com.teamresourceful.resourcefullib.client.CloseablePoseStack;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import static com.teamresourceful.resourcefulbees.client.component.selection.BeeEntry.SLOT_TEXTURE;

public class DamageEntry extends BaseListEntry {

    private final ItemStack icon;
    private final String id;
    private final int amplifier;

    public DamageEntry(TraitDamageType type) {
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
    protected void render(@NotNull GuiGraphics graphics, @NotNull ScissorBoxStack scissorStack, int id, int left, int top, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick, boolean selected) {
        try (var pose = new CloseablePoseStack(graphics)) {
            pose.translate(left, top, 0);
            Minecraft instance = Minecraft.getInstance();
            Font font = instance.font;

            graphics.renderItem(this.icon, 3, 2);

            graphics.blit(SLOT_TEXTURE, 1, 0, 0, 0, 20, 20, 20, 60);

            graphics.drawString(font, Component.translatable("damage_type.resourcefulbees."+ this.id), 24, 1, 0x55FF55);
            graphics.drawString(font, Component.translatable(BeepediaTranslations.Traits.AMPLIFIER, this.amplifier), 24, 11, 0xAAAAAA);
            graphics.drawString(font, Component.translatable("damage_type.resourcefulbees.desc."+ this.id), 24, 21, 0xAAAAAA);
        }
    }
}