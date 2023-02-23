package com.teamresourceful.resourcefulbees.client.screen.beepedia.pages.traits;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.api.data.trait.Aura;
import com.teamresourceful.resourcefulbees.api.data.trait.TraitAbility;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.BeepediaTranslations;
import com.teamresourceful.resourcefulbees.common.util.MathUtils;
import com.teamresourceful.resourcefullib.client.CloseablePoseStack;
import com.teamresourceful.resourcefullib.client.components.selection.ListEntry;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import com.teamresourceful.resourcefullib.client.screens.TooltipProvider;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.teamresourceful.resourcefulbees.client.component.selection.BeeEntry.SLOT_TEXTURE;

public class BasicEntry extends ListEntry implements TooltipProvider {

    private boolean hovered;

    private final ItemStack icon;
    private final Component title;
    private final Component description;
    private final int color;

    private final Component tooltip;


    public BasicEntry(ItemStack icon, Component title, Component description, int color, Component tooltip) {
        this.icon = icon;
        this.title = title;
        this.description = description;
        this.color = color;
        this.tooltip = tooltip;
    }

    public BasicEntry(ItemStack icon, Component title, Component description, int color) {
        this(icon, title, description, color, null);
    }

    public static BasicEntry of(TraitAbility ability) {
        return new BasicEntry(ability.displayedItem(), ability.getTitle(), ability.getDescription(), 0x55FF55, BeepediaTranslations.HOVER_FOR_DESC);
    }

    public static BasicEntry of(String immunity) {
        return new BasicEntry(new ItemStack(Items.SHIELD), BeepediaTranslations.DAMAGE_IMMUNITY, Component.literal(immunity), 0x55FF55);
    }

    public static BasicEntry of(Aura aura) {
        return switch (aura.type()) {
            case POTION -> new BasicEntry(Items.POTION.getDefaultInstance(), BeepediaTranslations.POTION_AURA, aura.potionEffect().effect().getDisplayName().copy().append(" " + MathUtils.createRomanNumeral(aura.potionEffect().strength())), auraColor(aura));
            case BURNING -> new BasicEntry(Items.BLAZE_POWDER.getDefaultInstance(), BeepediaTranslations.BURN_AURA, BeepediaTranslations.BURN_AURA_DESC, auraColor(aura));
            case HEALING -> new BasicEntry(Items.GLISTERING_MELON_SLICE.getDefaultInstance(), BeepediaTranslations.HEAL_AURA, Component.translatable(BeepediaTranslations.HEAL_AURA_DESC, aura.modifier()), auraColor(aura));
            case DAMAGING -> new BasicEntry(Items.IRON_SWORD.getDefaultInstance(), BeepediaTranslations.DAMAGE_AURA, aura.damageEffect().getDisplayName(), auraColor(aura));
            case EXPERIENCE -> new BasicEntry(Items.ENCHANTING_TABLE.getDefaultInstance(), BeepediaTranslations.XP_AURA, Component.translatable(BeepediaTranslations.XP_AURA_DESC, aura.modifier()), auraColor(aura));
            case EXPERIENCE_DRAIN -> new BasicEntry(Items.GRINDSTONE.getDefaultInstance(), BeepediaTranslations.XP_DRAIN_AURA, Component.translatable(BeepediaTranslations.XP_DRAIN_AURA_DESC, aura.modifier()), auraColor(aura));
        };
    }

    private static int auraColor(Aura aura) {
        return aura.isBeneficial() ? 0x5555FF : 0xFF5555;
    }

    @Override
    protected void render(@NotNull ScissorBoxStack scissorStack, @NotNull PoseStack stack, int id, int left, int top, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick, boolean selected) {
        this.hovered = hovered;
        try (var ignored = new CloseablePoseStack(stack)) {
            ignored.translate(left, top, 0);
            Minecraft instance = Minecraft.getInstance();
            Font font = instance.font;

            RenderUtils.renderItem(stack, this.icon, 3, 2);

            RenderUtils.bindTexture(SLOT_TEXTURE);
            GuiComponent.blit(stack, 1, 0, 0, 0, 20, 20, 20, 60);

            GuiComponent.drawString(stack, font, this.title, 24, 1, this.color);
            GuiComponent.drawString(stack, font, this.tooltip != null ? this.tooltip : this.description, 24, 11, 0xAAAAAA);
        }
    }

    @Override
    public @NotNull List<Component> getTooltip(int mouseX, int mouseY) {
        if (!this.hovered || this.tooltip == null) {
            return List.of();
        }
        return List.of(this.title, this.description);
    }
}