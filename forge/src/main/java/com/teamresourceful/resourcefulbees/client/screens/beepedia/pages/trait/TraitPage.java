package com.teamresourceful.resourcefulbees.client.screens.beepedia.pages.trait;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.teamresourceful.resourcefulbees.api.data.trait.Trait;
import com.teamresourceful.resourcefulbees.client.components.beepedia.ItemSlotWidget;
import com.teamresourceful.resourcefulbees.client.components.beepedia.SlotButton;
import com.teamresourceful.resourcefulbees.client.screens.beepedia.pages.honey.EffectEntry;
import com.teamresourceful.resourcefulbees.common.registry.custom.TraitAbilityRegistry;
import com.teamresourceful.resourcefullib.client.components.selection.ListEntry;
import com.teamresourceful.resourcefullib.client.components.selection.SelectionList;
import com.teamresourceful.resourcefullib.client.screens.HistoryScreen;
import com.teamresourceful.resourcefullib.client.screens.TooltipProvider;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.teamresourceful.resourcefulbees.client.screens.beepedia.pages.bee.BeePage.TEXTURE;

public class TraitPage extends HistoryScreen implements TooltipProvider {

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

        List<Pair<Supplier<Boolean>, Category>> entries = List.of(
            Pair.of(this.data::hasPotionDamageEffects, Category.POTIONS),
            Pair.of(() -> this.data.hasDamageImmunities() || this.data.hasPotionImmunities(), Category.IMMUNITIES),
            Pair.of(this.data::hasDamageTypes, Category.DAMAGE),
            Pair.of(this.data::hasSpecialAbilities, Category.ABILITIES),
            Pair.of(this.data::hasAuras, Category.AURAS)
        );

        for (var entry : entries) {
            if (entry.getFirst().get()) {
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
        Gui.blit(stack, 0, 19, 0, 0, 186, 3, 186, 3);

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
            case ABILITIES -> this.data.specialAbilities().stream().map(TraitAbilityRegistry.getRegistry()::getAbility).filter(Objects::nonNull).map(BasicEntry::of).toList();
            case AURAS -> this.data.auras().stream().map(BasicEntry::of).toList();
        });
    }

    enum Category {
        POTIONS(new ResourceLocation("textures/item/brewing_stand.png")),
        IMMUNITIES(new ResourceLocation("textures/item/barrier.png")),
        DAMAGE(new ResourceLocation("textures/item/iron_axe.png")),
        ABILITIES(new ResourceLocation("textures/item/nether_star.png")),
        AURAS(new ResourceLocation("textures/item/end_crystal.png"));

        public final ResourceLocation texture;

        Category(ResourceLocation texture) {
            this.texture = texture;
        }
    }
}

