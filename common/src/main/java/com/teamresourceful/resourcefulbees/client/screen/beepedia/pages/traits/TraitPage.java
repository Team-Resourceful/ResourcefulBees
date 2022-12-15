package com.teamresourceful.resourcefulbees.client.screen.beepedia.pages.traits;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.teamresourceful.resourcefulbees.api.data.trait.Trait;
import com.teamresourceful.resourcefulbees.api.registry.TraitAbilityRegistry;
import com.teamresourceful.resourcefulbees.client.component.ItemSlotWidget;
import com.teamresourceful.resourcefulbees.client.component.SlotButton;
import com.teamresourceful.resourcefulbees.client.screen.beepedia.pages.honeys.EffectEntry;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefullib.client.components.selection.ListEntry;
import com.teamresourceful.resourcefullib.client.components.selection.SelectionList;
import com.teamresourceful.resourcefullib.client.screens.HistoryScreen;
import com.teamresourceful.resourcefullib.client.screens.TooltipProvider;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.stream.Stream;

import static com.teamresourceful.resourcefulbees.client.screen.beepedia.pages.bees.BeePage.TEXTURE;

public class TraitPage extends HistoryScreen implements TooltipProvider {

    private final List<Widget> renderables = new ArrayList<>();

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
    public void render(@NotNull PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        super.render(stack, mouseX, mouseY, partialTicks);

        RenderUtils.bindTexture(TEXTURE);
        GuiComponent.blit(stack, 0, 19, 0, 0, 186, 3, 186, 3);

        Font font = Minecraft.getInstance().font;
        font.draw(stack, this.data.getDisplayName(), 24, 1, 0xFFFFFF);
        font.draw(stack, this.data.name(), 24, 10, 0x555555);

    }

    @Override
    public @NotNull List<Component> getTooltip(int mouseX, int mouseY) {
        return TooltipProvider.getTooltips(this.renderables, mouseX, mouseY);
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

    @Override
    protected <T extends GuiEventListener & Widget & NarratableEntry> T addRenderableWidget(T widget) {
        this.renderables.add(widget);
        return super.addRenderableWidget(widget);
    }

    @Override
    protected <T extends Widget> T addRenderableOnly(T widget) {
        this.renderables.add(widget);
        return super.addRenderableOnly(widget);
    }

    enum Category {
        POTIONS(new ResourceLocation("textures/item/brewing_stand.png"), TranslationConstants.Beepedia.Traits.POTION_DAMAGE_EFFECTS),
        IMMUNITIES(new ResourceLocation("textures/item/barrier.png"), TranslationConstants.Beepedia.Traits.IMMUNITIES),
        DAMAGE(new ResourceLocation("textures/item/iron_axe.png"), TranslationConstants.Beepedia.Traits.DAMAGE_TYPES),
        ABILITIES(new ResourceLocation("textures/item/nether_star.png"), TranslationConstants.Beepedia.Traits.ABILITIES),
        AURAS(new ResourceLocation("textures/item/end_crystal.png"), TranslationConstants.Beepedia.Traits.AURAS);

        public final ResourceLocation texture;
        public final MutableComponent component;

        Category(ResourceLocation texture, MutableComponent component) {
            this.texture = texture;
            this.component = component;
        }
    }
}

