package com.teamresourceful.resourcefulbees.datagen.bases;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseAdvancementProvider implements IDataProvider {
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
    private static final String TRANSLATIONS_PREFIX = "advancements.resourcefulbees.";
    private static final String TITLE_SUFFIX = ".title";
    private static final String DESCRIPTION_SUFFIX = ".description";

    private final String modDisplayName;
    private final DataGenerator generator;
    private final Map<ResourceLocation, Advancement> advancements = new HashMap<>();

    protected BaseAdvancementProvider(String modDisplayName, DataGenerator pGenerator) {
        this.modDisplayName = modDisplayName;
        this.generator = pGenerator;
    }

    public abstract void buildAdvancements();

    protected static Advancement createRootAdvancement(RegistryObject<Item> item, ITextComponent title, ITextComponent desc, ResourceLocation background, ItemPredicate predicate) {
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
                .build(new ResourceLocation(ResourcefulBees.MOD_ID, "resourcefulbees/root"));
    }

    protected static Advancement.Builder createAdvancement(ItemStack item, String id, Advancement parent) {
        return Advancement.Builder.advancement()
                .display(item, new TranslationTextComponent(TRANSLATIONS_PREFIX+id+TITLE_SUFFIX), new TranslationTextComponent(TRANSLATIONS_PREFIX+id+DESCRIPTION_SUFFIX), null, FrameType.TASK, true, true, false)
                .parent(parent);
    }

    protected static Advancement.Builder createAdvancement(RegistryObject<Item> item, String id, Advancement parent) {
        return Advancement.Builder.advancement()
                .display(item.get().getDefaultInstance(), new TranslationTextComponent(TRANSLATIONS_PREFIX+id+TITLE_SUFFIX), new TranslationTextComponent(TRANSLATIONS_PREFIX+id+DESCRIPTION_SUFFIX), null, FrameType.TASK, true, true, false)
                .parent(parent);
    }

    protected static Advancement createSimpleAdvancement(RegistryObject<Item> item, String id, Advancement parent) {
        return createAdvancement(item, id, parent)
                .addCriterion("has_"+id, has(item.get()))
                .build(new ResourceLocation(ResourcefulBees.MOD_ID, "resourcefulbees/"+id));
    }

    protected static Advancement.Builder createChallengeAchivement(ItemStack item, String id, Advancement parent) {
        return Advancement.Builder.advancement()
                .display(item, new TranslationTextComponent(TRANSLATIONS_PREFIX+id+TITLE_SUFFIX), new TranslationTextComponent(TRANSLATIONS_PREFIX+id+DESCRIPTION_SUFFIX), null, FrameType.CHALLENGE, true, true, true)
                .parent(parent);
    }

    protected static Advancement.Builder createChallengeAchivement(RegistryObject<Item> item, String id, Advancement parent) {
        return Advancement.Builder.advancement()
                .display(item.get().getDefaultInstance(), new TranslationTextComponent(TRANSLATIONS_PREFIX+id+TITLE_SUFFIX), new TranslationTextComponent(TRANSLATIONS_PREFIX+id+DESCRIPTION_SUFFIX), null, FrameType.CHALLENGE, true, true, true)
                .parent(parent);
    }

    protected static Advancement createSimpleChallengeAchivement(RegistryObject<Item> item, String id, Advancement parent) {
        return createChallengeAchivement(item, id, parent)
                .addCriterion("has_"+id, has(item.get()))
                .build(new ResourceLocation(ResourcefulBees.MOD_ID, "resourcefulbees/"+id));
    }

    protected static InventoryChangeTrigger.Instance has(IItemProvider pItemLike) {
        return inventoryTrigger(ItemPredicate.Builder.item().of(pItemLike).build());
    }

    protected static InventoryChangeTrigger.Instance has(ITag<Item> pTag) {
        return inventoryTrigger(ItemPredicate.Builder.item().of(pTag).build());
    }

    protected static InventoryChangeTrigger.Instance inventoryTrigger(ItemPredicate... pPredicate) {
        return new InventoryChangeTrigger.Instance(EntityPredicate.AndPredicate.ANY, MinMaxBounds.IntBound.ANY, MinMaxBounds.IntBound.ANY, MinMaxBounds.IntBound.ANY, pPredicate);
    }

    public Advancement addAdvancement(Advancement advancement) {
        if (this.advancements.containsKey(advancement.getId()))
            throw new IllegalStateException("Duplicate advancement " + advancement.getId());
        this.advancements.put(advancement.getId(), advancement);
        return advancement;
    }

    @Override
    public void run(@NotNull DirectoryCache cache) throws IOException {
        Path path = this.generator.getOutputFolder();

        buildAdvancements();

        for (Advancement advancement : this.advancements.values()) {
            Path path1 = createPath(path, advancement);
            IDataProvider.save(GSON, cache, advancement.deconstruct().serializeToJson(), path1);
        }
    }

    private static Path createPath(Path path, Advancement advancement) {
        return path.resolve("data/" + advancement.getId().getNamespace() + "/advancements/" + advancement.getId().getPath() + ".json");
    }

    @Override
    public @NotNull String getName() {
        return modDisplayName + " Advancement Provider";
    }
}
