package com.teamresourceful.resourcefulbees.client.screen.beepedia.pages.traits;

import com.mojang.datafixers.util.Pair;
import com.teamresourceful.resourcefulbees.api.data.trait.Trait;
import com.teamresourceful.resourcefulbees.api.registry.TraitAbilityRegistry;
import com.teamresourceful.resourcefulbees.client.component.ItemSlotWidget;
import com.teamresourceful.resourcefulbees.client.component.SlotButton;
import com.teamresourceful.resourcefulbees.client.screen.beepedia.pages.honeys.EffectEntry;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.BeepediaTranslations;
import com.teamresourceful.resourcefullib.client.components.selection.ListEntry;
import com.teamresourceful.resourcefullib.client.components.selection.SelectionList;
import com.teamresourceful.resourcefullib.client.screens.HistoryScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.stream.Stream;

import static com.teamresourceful.resourcefulbees.client.screen.beepedia.pages.bees.BeePage.TEXTURE;

public class TraitPage extends HistoryScreen {

    private Category category;
    private SelectionList<ListEntry> list;

    private final Trait data;

    public TraitPage(Trait data) {
        super(CommonComponents.EMPTY);
        this.data = data;
    }

    @Override
    protected void init() {
        addRenderableOnly(new ItemSlotWidget(2, 0, new ItemStack(this.data.displayItem()), false));

        this.list = addRenderableOnly(new SelectionList<>(1, 22, 182, 141, 30, ignored -> {}));
        int x = 184;

        List<Pair<BooleanSupplier, Category>> entries = List.of(
            Pair.of(this.data::hasPotionDamageEffects, Category.POTIONS),
            Pair.of(() -> this.data.hasDamageImmunities() || this.data.hasPotionImmunities(), Category.IMMUNITIES),
            Pair.of(this.data::hasDamageTypes, Category.DAMAGE),
            Pair.of(this.data::hasSpecialAbilities, Category.ABILITIES),
            Pair.of(this.data::hasAuras, Category.AURAS)
        );

        for (var entry : entries) {
            if (entry.getFirst().getAsBoolean()) {
                x-=22;
                addRenderableWidget(createButton(x, 0, entry.getSecond()));
                this.category = entry.getSecond();
            }
        }

        updateSelections();
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);

        graphics.blit(TEXTURE, 0, 19, 0, 0, 186, 3, 186, 3);

        Font font = Minecraft.getInstance().font;
        graphics.drawString(font, this.data.getDisplayName(), 24, 1, 0xFFFFFF, false);
        graphics.drawString(font, this.data.name(), 24, 10, 0x555555, false);

    }

    public Trait getData() {
        return this.data;
    }

    private SlotButton createButton(int x, int y, Category category) {
        SlotButton button = new SlotButton(x + 2, y, category.texture, () -> this.category == category, () -> {
            this.category = category;
            updateSelections();
        });
        button.setTooltipProvider(() -> List.of(Component.literal(category.name())));
        return button;
    }

    private void updateSelections() {
        this.list.updateEntries(switch (this.category) {
            case POTIONS -> this.data.potionDamageEffects().stream().map(EffectEntry::new).toList();
            case IMMUNITIES -> Stream.concat(this.data.potionImmunities().stream().map(EffectEntry::new), this.data.damageImmunities().stream().map(BasicEntry::of)).toList();
            case DAMAGE -> this.data.damageTypes().stream().map(DamageEntry::new).toList();
            case ABILITIES -> this.data.specialAbilities().stream().map(TraitAbilityRegistry.get()::getAbility).filter(Objects::nonNull).map(BasicEntry::of).toList();
            case AURAS -> this.data.auras().stream().map(BasicEntry::of).toList();
        });
    }

    enum Category {
        POTIONS(new ResourceLocation("textures/item/brewing_stand.png"), BeepediaTranslations.Traits.POTION_DAMAGE_EFFECTS),
        IMMUNITIES(new ResourceLocation("textures/item/barrier.png"), BeepediaTranslations.Traits.IMMUNITIES),
        DAMAGE(new ResourceLocation("textures/item/iron_axe.png"), BeepediaTranslations.Traits.DAMAGE_TYPES),
        ABILITIES(new ResourceLocation("textures/item/nether_star.png"), BeepediaTranslations.Traits.ABILITIES),
        AURAS(new ResourceLocation("textures/item/end_crystal.png"), BeepediaTranslations.Traits.AURAS);

        public final ResourceLocation texture;
        public final MutableComponent component;

        Category(ResourceLocation texture, MutableComponent component) {
            this.texture = texture;
            this.component = component;
        }
    }
}

