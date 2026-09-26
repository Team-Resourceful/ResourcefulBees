package com.teamresourceful.resourcefulbees.datagen.providers.base;

import com.teamresourceful.resourcefulbees.common.components.predicates.JarBeePredicate;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModDataComponentPredicates;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.predicates.DataComponentMatchers;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.advancements.triggers.InventoryChangeTrigger;
import net.minecraft.core.ClientAsset;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.ItemLike;

import java.util.Optional;
import java.util.function.Supplier;

public abstract class BaseAdvancementProvider extends AdvancementSubProvider {

    public static final String TRANSLATIONS_PREFIX = "advancements.resourcefulbees.";
    public static final String TITLE_SUFFIX = ".title";
    public static final String DESCRIPTION_SUFFIX = ".description";

    protected BaseAdvancementProvider(BootstrapContext<Advancement> output) {
        super(output);
    }

    protected AdvancementHolder register(AdvancementHolder advancement) {
        output.register(ResourceKey.create(Registries.ADVANCEMENT, advancement.id()), advancement.value());
        return advancement;
    }

    protected ItemPredicate itemPredicate(ItemLike item) {
        HolderGetter<Item> items = output.lookup(Registries.ITEM);

        return ItemPredicate.Builder.item()
                .of(items, item)
                .build();
    }

    protected static AdvancementHolder createRootAdvancement(Supplier<Item> item, Component title, Component description, Identifier background, ItemPredicate predicate) {
        return Advancement.Builder.advancement()
                .display(new DisplayInfo(
                        new ItemStackTemplate(item.get()),
                        title,
                        description,
                        Optional.of(new ClientAsset.ResourceTexture(background)),
                        AdvancementType.TASK,
                        false,
                        false,
                        false
                ))
                .addCriterion("inventory_changed", inventoryTrigger(predicate))
                .build(advancementId("root"));
    }

    protected static Advancement.Builder createAdvancement(ItemStackTemplate item, String id, AdvancementHolder parent
    ) {
        return Advancement.Builder.advancement()
                .display(item, Component.translatable(TRANSLATIONS_PREFIX + id + TITLE_SUFFIX), Component.translatable(TRANSLATIONS_PREFIX + id + DESCRIPTION_SUFFIX), AdvancementType.TASK, true, true, false)
                .parent(parent);
    }

    protected static Advancement.Builder createAdvancement(ItemLike item, String id, AdvancementHolder parent) {
        return Advancement.Builder.advancement()
                .display(item.asItem(), Component.translatable(TRANSLATIONS_PREFIX + id + TITLE_SUFFIX), Component.translatable(TRANSLATIONS_PREFIX + id + DESCRIPTION_SUFFIX), AdvancementType.TASK, true, true, false)
                .parent(parent);
    }

    protected static Advancement.Builder createAdvancement(
            Supplier<Item> item,
            String id,
            AdvancementHolder parent
    ) {
        return Advancement.Builder.advancement()
                .display(item.get(), Component.translatable(TRANSLATIONS_PREFIX + id + TITLE_SUFFIX), Component.translatable(TRANSLATIONS_PREFIX + id + DESCRIPTION_SUFFIX), AdvancementType.TASK, true, true, false)
                .parent(parent);
    }

    protected static AdvancementHolder createSimpleAdvancement(Supplier<Item> item, String id, AdvancementHolder parent) {
        return createAdvancement(item, id, parent)
                .addCriterion("has_" + id, has(item.get()))
                .build(advancementId(id));
    }

    protected static Advancement.Builder createChallengeAchievement(Supplier<Item> item, String id, AdvancementHolder parent) {
        return Advancement.Builder.advancement()
                .display(item.get(), Component.translatable(TRANSLATIONS_PREFIX + id + TITLE_SUFFIX), Component.translatable(TRANSLATIONS_PREFIX + id + DESCRIPTION_SUFFIX), AdvancementType.CHALLENGE, true, true, true)
                .parent(parent);
    }

    protected static AdvancementHolder createSimpleChallengeAchievement(Supplier<Item> item, String id, AdvancementHolder parent) {
        return createChallengeAchievement(item, id, parent)
                .addCriterion("has_" + id, has(item.get()))
                .build(advancementId(id));
    }

    protected static Advancement.Builder createChallengeAchievement(ItemStackTemplate item, String id, AdvancementHolder parent) {
        return Advancement.Builder.advancement()
                .display(item, Component.translatable(TRANSLATIONS_PREFIX + id + TITLE_SUFFIX), Component.translatable(TRANSLATIONS_PREFIX + id + DESCRIPTION_SUFFIX), AdvancementType.CHALLENGE, true, true, true)
                .parent(parent);
    }

    protected static Criterion<InventoryChangeTrigger.TriggerInstance> has(ItemLike item) {
        return InventoryChangeTrigger.TriggerInstance.hasItems(item);
    }

    protected Criterion<InventoryChangeTrigger.TriggerInstance> has(TagKey<Item> tag) {
        HolderGetter<Item> items = output.lookup(Registries.ITEM);
        ItemPredicate predicate = ItemPredicate.Builder.item().of(items, tag).build();
        return inventoryTrigger(predicate);
    }

    protected Criterion<InventoryChangeTrigger.TriggerInstance> hasJarBee(EntityType<?> entityType) {
        HolderGetter<Item> items = output.lookup(Registries.ITEM);

        ItemPredicate predicate = ItemPredicate.Builder.item()
                .of(items, ModItems.BEE_JAR.get())
                .withComponents(DataComponentMatchers.Builder.components()
                                .partial(ModDataComponentPredicates.JAR_BEE.get(), new JarBeePredicate(Optional.of(entityType)))
                                .build()
                )
                .build();

        return InventoryChangeTrigger.TriggerInstance.hasItems(predicate);
    }

    protected Criterion<InventoryChangeTrigger.TriggerInstance> hasAnyJarBee() {
        HolderGetter<Item> items = output.lookup(Registries.ITEM);

        ItemPredicate predicate = ItemPredicate.Builder.item()
                .of(items, ModItems.BEE_JAR.get())
                .withComponents(
                        DataComponentMatchers.Builder.components()
                                .partial(ModDataComponentPredicates.JAR_BEE.get(), JarBeePredicate.any())
                                .build()
                )
                .build();

        return InventoryChangeTrigger.TriggerInstance.hasItems(predicate);
    }

    protected static Criterion<InventoryChangeTrigger.TriggerInstance> inventoryTrigger(ItemPredicate... predicates) {
        return InventoryChangeTrigger.TriggerInstance.hasItems(predicates);
    }

    protected static Identifier advancementId(String path) {
        return ModIdentifier.of("resourcefulbees/" + path);
    }

//    public interface AdvancementGenerator extends AdvancementSubProvider {
//
//        @Override
//        default void generate(BootstrapContext<Advancement> context, @NotNull Consumer<AdvancementHolder> writer) {
//            generate(registries, value -> {
//                writer.accept(value);
//                return value;
//            });
//        }
//
//        void generate(BootstrapContext<Advancement> context, UnaryOperator<AdvancementHolder> writer);
//    }
}