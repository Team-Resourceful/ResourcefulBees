package com.teamresourceful.resourcefulbees.datagen.bases;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public abstract class BaseAdvancementProvider extends AdvancementProvider {

    public static final String TRANSLATIONS_PREFIX = "advancements.resourcefulbees.";
    public static final String TITLE_SUFFIX = ".title";
    public static final String DESCRIPTION_SUFFIX = ".description";

    protected BaseAdvancementProvider(DataGenerator generator, CompletableFuture<HolderLookup.Provider> completableFuture, List<AdvancementSubProvider> list) {
        super(generator.getPackOutput(), completableFuture, list);
    }

    protected static Advancement createRootAdvancement(Supplier<Item> item, Component title, Component desc, ResourceLocation background, ItemPredicate predicate) {
        return Advancement.Builder.advancement()
                .display(item.get().getDefaultInstance(),
                        title,
                        desc,
                        background,
                        FrameType.TASK,
                        false,
                        false,
                        false)
                .addCriterion("inventory_changed", inventoryTrigger(predicate))
                .build(new ResourceLocation(ModConstants.MOD_ID, "resourcefulbees/root"));
    }

    protected static Advancement.Builder createAdvancement(ItemStack item, String id, Advancement parent) {
        return Advancement.Builder.advancement()
                .display(item, Component.translatable(TRANSLATIONS_PREFIX+id+TITLE_SUFFIX), Component.translatable(TRANSLATIONS_PREFIX+id+DESCRIPTION_SUFFIX), null, FrameType.TASK, true, true, false)
                .parent(parent);
    }

    protected static Advancement.Builder createAdvancement(Supplier<Item> item, String id, Advancement parent) {
        return Advancement.Builder.advancement()
                .display(item.get().getDefaultInstance(), Component.translatable(TRANSLATIONS_PREFIX+id+TITLE_SUFFIX), Component.translatable(TRANSLATIONS_PREFIX+id+DESCRIPTION_SUFFIX), null, FrameType.TASK, true, true, false)
                .parent(parent);
    }

    protected static Advancement createSimpleAdvancement(Supplier<Item> item, String id, Advancement parent) {
        return createAdvancement(item, id, parent)
                .addCriterion("has_"+id, has(item.get()))
                .build(new ResourceLocation(ModConstants.MOD_ID, "resourcefulbees/"+id));
    }

    protected static Advancement.Builder createChallengeAchievement(ItemStack item, String id, Advancement parent) {
        return Advancement.Builder.advancement()
                .display(item, Component.translatable(TRANSLATIONS_PREFIX+id+TITLE_SUFFIX), Component.translatable(TRANSLATIONS_PREFIX+id+DESCRIPTION_SUFFIX), null, FrameType.CHALLENGE, true, true, true)
                .parent(parent);
    }

    protected static Advancement.Builder createChallengeAchievement(Supplier<Item> item, String id, Advancement parent) {
        return Advancement.Builder.advancement()
                .display(item.get().getDefaultInstance(), Component.translatable(TRANSLATIONS_PREFIX+id+TITLE_SUFFIX), Component.translatable(TRANSLATIONS_PREFIX+id+DESCRIPTION_SUFFIX), null, FrameType.CHALLENGE, true, true, true)
                .parent(parent);
    }

    protected static Advancement createSimpleChallengeAchievement(Supplier<Item> item, String id, Advancement parent) {
        return createChallengeAchievement(item, id, parent)
                .addCriterion("has_"+id, has(item.get()))
                .build(new ResourceLocation(ModConstants.MOD_ID, "resourcefulbees/"+id));
    }

    protected static InventoryChangeTrigger.TriggerInstance has(ItemLike pItemLike) {
        return inventoryTrigger(ItemPredicate.Builder.item().of(pItemLike).build());
    }

    protected static InventoryChangeTrigger.TriggerInstance has(TagKey<Item> pTag) {
        return inventoryTrigger(ItemPredicate.Builder.item().of(pTag).build());
    }

    protected static InventoryChangeTrigger.TriggerInstance inventoryTrigger(ItemPredicate... pPredicate) {
        return new InventoryChangeTrigger.TriggerInstance(ContextAwarePredicate.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, pPredicate);
    }

    private static Path createPath(Path path, Advancement advancement) {
        return path.resolve("data/" + advancement.getId().getNamespace() + "/advancements/" + advancement.getId().getPath() + ".json");
    }

    public interface AdvancementGenerator extends AdvancementSubProvider {
        @Override
        default void generate(HolderLookup.@NotNull Provider registries, @NotNull Consumer<Advancement> writer) {
            generate(registries, value -> {
                writer.accept(value);
                return value;
            });
        }

        void generate(HolderLookup.Provider registries, UnaryOperator<Advancement> writer);
    }
}
