package com.teamresourceful.resourcefulbees.datagen.providers.advancements;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModTags;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import com.teamresourceful.resourcefulbees.common.utils.ModUtils;
import com.teamresourceful.resourcefulbees.datagen.bases.BaseAdvancementProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class ModAdvancementProvider extends BaseAdvancementProvider {

    public ModAdvancementProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    public void buildAdvancements() {
        Advancement root = addAdvancement(createRootAdvancement(ModItems.GOLD_FLOWER_ITEM,
                Component.translatable("advancements.resourcefulbees.root.title"),
                Component.translatable("advancements.resourcefulbees.root.description"),
                new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/advancements/backgrounds/resourcefulbees.png"),
                ItemPredicate.Builder.item().of(ModTags.Items.HONEYCOMB).build()
        ));

        addAdvancement(createSimpleAdvancement(ModItems.WAX, "wax", root));
        Advancement scraper = addAdvancement(createSimpleAdvancement(ModItems.SCRAPER, "scraper", root));
        Advancement beeJar = addAdvancement(createSimpleAdvancement(ModItems.BEE_JAR, "bee_jar", root));
        Advancement beepedia = addAdvancement(createSimpleAdvancement(ModItems.BEEPEDIA, "beepedia", root));
        addAdvancement(createSimpleAdvancement(ModItems.ENDER_BEECON_ITEM, "ender_beecon", root));
        addAdvancement(createSimpleAdvancement(ModItems.HONEY_GENERATOR_ITEM, "honey_generator", root));
        Advancement nest = addAdvancement(createAdvancement(ModItems.OAK_BEE_NEST_ITEM, "obtain_bee_nest", root)
                .addCriterion("has_nest", has(ModTags.Items.BEEHIVES))
                .build(new ResourceLocation(ResourcefulBees.MOD_ID, "resourcefulbees/obtain_bee_nest")));

        Advancement t1HiveUpgrade = addAdvancement(createSimpleAdvancement(ModItems.T2_NEST_UPGRADE, "t1_hive_upgrade", nest));
        Advancement t2HiveUpgrade = addAdvancement(createSimpleAdvancement(ModItems.T3_NEST_UPGRADE, "t2_hive_upgrade", t1HiveUpgrade));
        Advancement t3HiveUpgrade = addAdvancement(createSimpleAdvancement(ModItems.T4_NEST_UPGRADE, "t3_hive_upgrade", t2HiveUpgrade));

        Advancement t1ApiaryUpgrade = addAdvancement(createSimpleAdvancement(ModItems.T1_APIARY_ITEM, "t1_apiary", t3HiveUpgrade));
        Advancement t2ApiaryUpgrade = addAdvancement(createSimpleAdvancement(ModItems.T2_APIARY_ITEM, "t2_apiary", t1ApiaryUpgrade));
        Advancement t3ApiaryUpgrade = addAdvancement(createSimpleAdvancement(ModItems.T3_APIARY_ITEM, "t3_apiary", t2ApiaryUpgrade));
        addAdvancement(createSimpleAdvancement(ModItems.T4_APIARY_ITEM, "t4_apiary", t3ApiaryUpgrade));

        Advancement beeBox = addAdvancement(createSimpleAdvancement(ModItems.BEE_BOX, "bee_box", beeJar));
        addAdvancement(createSimpleChallengeAchievement(ModItems.BEE_BOX_TEMP, "crafting_bee_box", beeBox));
        addAdvancement(createSimpleAdvancement(ModItems.HONEY_DIPPER, "honey_dipper", nest));
        addAdvancement(createSimpleAdvancement(ModItems.SMOKER, "smoker", scraper));

        ////TODO fix this!
        //addAdvancement(createSimpleAdvancement(ModItems.APIARY_BREED_TIME_UPGRADE, "apiary_breed_time_upgrade", t4HiveUpgrade));
        //addAdvancement(createSimpleAdvancement(ModItems.APIARY_BREEDER_ITEM, "apiary_breeder", t4HiveUpgrade));
        //addAdvancement(createSimpleAdvancement(ModItems.APIARY_BREEDER_UPGRADE, "apiary_breeder_upgrade", t4HiveUpgrade));

        CompoundTag beepediaNbt = new CompoundTag();
        beepediaNbt.putBoolean("Complete", false);

        Advancement beeUnlockedBeepedia = addAdvancement(createAdvancement(ModItems.BEEPEDIA, "beepedia_unlock_bee", beepedia)
                .addCriterion("has_unlocked_beepedia", InventoryChangeTrigger.TriggerInstance.hasItems(
                        ItemPredicate.Builder.item().of(ModItems.BEEPEDIA.get()).hasNbt(beepediaNbt).build()
                ))
                .build(new ResourceLocation(ResourcefulBees.MOD_ID, "resourcefulbees/beepedia_unlock_bee")));


        CompoundTag completeBeepedia = new CompoundTag();
        completeBeepedia.putBoolean("Complete", true);

        addAdvancement(createAdvancement(ModItems.BEEPEDIA, "beepedia_complete", beeUnlockedBeepedia)
                .addCriterion("has_completed_beepedia", InventoryChangeTrigger.TriggerInstance.hasItems(
                        ItemPredicate.Builder.item().of(ModItems.BEEPEDIA.get()).hasNbt(completeBeepedia).build()
                ))
                .build(new ResourceLocation(ResourcefulBees.MOD_ID, "resourcefulbees/beepedia_complete")));

        //TODO add back when patchouli is updated or if we do our own book
/*        ItemStack fifityShadesOfBeesBook = PatchouliAPI.get().getBookStack(ModConstants.SHADES_OF_BEES);

        addAdvancement(createAdvancement(fifityShadesOfBeesBook, "fifty_shades_of_bees", root)
                .addCriterion("has_guide_book", InventoryChangeTrigger.TriggerInstance.hasItems(
                        ItemPredicate.Builder.item().of(fifityShadesOfBeesBook.getItem()).hasNbt(fifityShadesOfBeesBook.serializeNBT()).build()
                ))
                .build(new ResourceLocation(ResourcefulBees.MOD_ID, "resourcefulbees/fifty_shades_of_bees"))
        );*/

        ItemStack filledJar = createBeejar("minecraft:bee", "#edc343");

        Advancement collectBee = addAdvancement(createAdvancement(filledJar, "collect_bee", beeJar)
                .addCriterion("has_filled_jar", InventoryChangeTrigger.TriggerInstance.hasItems(
                        ItemPredicate.Builder.item().of(filledJar.getItem()).hasNbt(filledJar.serializeNBT()).build()
                ))
                .build(new ResourceLocation(ResourcefulBees.MOD_ID, "resourcefulbees/collect_bee"))
        );

        ItemStack kittenJar = createBeejar("resourcefulbees:kitten_bee", "#EAA939");

        addAdvancement(createChallengeAchievement(kittenJar, "kitten_bee", collectBee)
                .addCriterion("has_kitten_jar", InventoryChangeTrigger.TriggerInstance.hasItems(
                        ItemPredicate.Builder.item().of(kittenJar.getItem()).hasNbt(kittenJar.serializeNBT()).build()
                ))
                .build(new ResourceLocation(ResourcefulBees.MOD_ID, "resourcefulbees/kitten_bee"))
        );

        ItemStack oreoJar = createBeejar("resourcefulbees:oreo_bee", "#442920");

        addAdvancement(createChallengeAchievement(oreoJar, "oreo_bee", collectBee)
                .addCriterion("has_oreo_jar", InventoryChangeTrigger.TriggerInstance.hasItems(
                        ItemPredicate.Builder.item().of(oreoJar.getItem()).hasNbt(oreoJar.serializeNBT()).build()
                ))
                .build(new ResourceLocation(ResourcefulBees.MOD_ID, "resourcefulbees/oreo_bee"))
        );
    }

    private static ItemStack createBeejar(String id, String color) {
        ItemStack stack = ModItems.BEE_JAR.get().getDefaultInstance();
        CompoundTag nbt = new CompoundTag();
        nbt.putString(NBTConstants.NBT_ID, id);
        nbt.putString(NBTConstants.BeeJar.COLOR, color);
        stack.setTag(ModUtils.nbtWithData(NBTConstants.BeeJar.ENTITY, nbt));
        return stack;
    }
}
