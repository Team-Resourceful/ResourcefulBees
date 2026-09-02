package com.teamresourceful.resourcefulbees.datagen.providers.advancements;

import com.teamresourceful.resourcefulbees.api.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.components.JarOccupant;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.lib.defaults.DefaultHiveTypes;
import com.teamresourceful.resourcefulbees.common.lib.tags.ModItemTags;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModDataComponents;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems;
import com.teamresourceful.resourcefulbees.datagen.providers.base.BaseAdvancementProvider;
import com.teamresourceful.resourcefullib.common.color.Color;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.UnaryOperator;

public class RBeesAdvancementProvider extends BaseAdvancementProvider {

    public RBeesAdvancementProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider, List.of(new Generator()));
    }

    private static class Generator implements AdvancementGenerator {

        @Override
        public void generate(HolderLookup.Provider registries, UnaryOperator<AdvancementHolder> writer) {
            AdvancementHolder root = writer.apply(
                    createRootAdvancement(
                            ModItems.GOLD_FLOWER_ITEM,
                            Component.translatable("advancements.resourcefulbees.root.title"),
                            Component.translatable("advancements.resourcefulbees.root.description"),
                            ModIdentifier.of("gui/advancements/backgrounds/resourcefulbees"),
                            itemPredicate(registries, ModItems.GOLD_FLOWER_ITEM.get())
                    )
            );

            writer.apply(createSimpleAdvancement(ModItems.WAX, "wax", root));

            AdvancementHolder honeycomb = writer.apply(createAdvancement(Items.HONEYCOMB, "honeycomb", root)
                    .addCriterion("has_honeycomb", has(registries, ModItemTags.HONEYCOMBS))
                    .build(advancementId("honeycomb"))
            );
            AdvancementHolder scraper = writer.apply(createSimpleAdvancement(ModItems.SCRAPER, "scraper", root));
            AdvancementHolder beeJar = writer.apply(createSimpleAdvancement(ModItems.BEE_JAR, "bee_jar", root));

            writer.apply(
                    createSimpleAdvancement(
                            ModItems.ENDER_BEECON_ITEM,
                            "ender_beecon",
                            root
                    )
            );

            writer.apply(
                    createSimpleAdvancement(
                            ModItems.HONEY_GENERATOR_ITEM,
                            "honey_generator",
                            root
                    )
            );

            AdvancementHolder nest = writer.apply(
                    createAdvancement(
                            DefaultHiveTypes.OAK.tierOneNest().asItem(),
                            "obtain_bee_nest",
                            root
                    )
                            .addCriterion(
                                    "has_nest",
                                    has(
                                            registries,
                                            ModItemTags.BEEHIVES
                                    )
                            )
                            .build(
                                    advancementId("obtain_bee_nest")
                            )
            );

            AdvancementHolder t2HiveUpgrade = writer.apply(
                    createSimpleAdvancement(
                            ModItems.T2_NEST_UPGRADE,
                            "t2_hive_upgrade",
                            nest
                    )
            );

            AdvancementHolder t3HiveUpgrade = writer.apply(
                    createSimpleAdvancement(
                            ModItems.T3_NEST_UPGRADE,
                            "t3_hive_upgrade",
                            t2HiveUpgrade
                    )
            );

            AdvancementHolder t4HiveUpgrade = writer.apply(
                    createSimpleAdvancement(
                            ModItems.T4_NEST_UPGRADE,
                            "t4_hive_upgrade",
                            t3HiveUpgrade
                    )
            );

            AdvancementHolder t1ApiaryUpgrade = writer.apply(
                    createSimpleAdvancement(
                            ModItems.T1_APIARY_ITEM,
                            "t1_apiary",
                            t4HiveUpgrade
                    )
            );

            AdvancementHolder t2ApiaryUpgrade = writer.apply(
                    createSimpleAdvancement(
                            ModItems.T2_APIARY_ITEM,
                            "t2_apiary",
                            t1ApiaryUpgrade
                    )
            );

            AdvancementHolder t3ApiaryUpgrade = writer.apply(
                    createSimpleAdvancement(
                            ModItems.T3_APIARY_ITEM,
                            "t3_apiary",
                            t2ApiaryUpgrade
                    )
            );

            writer.apply(
                    createSimpleAdvancement(
                            ModItems.T4_APIARY_ITEM,
                            "t4_apiary",
                            t3ApiaryUpgrade
                    )
            );

            AdvancementHolder beeBox = writer.apply(
                    createSimpleAdvancement(
                            ModItems.BEE_BOX,
                            "bee_box",
                            beeJar
                    )
            );

            writer.apply(
                    createSimpleChallengeAchievement(
                            ModItems.BEE_BOX_TEMP,
                            "crafting_bee_box",
                            beeBox
                    )
            );

            writer.apply(
                    createSimpleAdvancement(
                            ModItems.HONEY_DIPPER,
                            "honey_dipper",
                            nest
                    )
            );

            writer.apply(
                    createSimpleAdvancement(
                            ModItems.SMOKER,
                            "smoker",
                            scraper
                    )
            );

            /*
             * Beepedia advancements intentionally omitted for now.
             *
             * They should be re-added once Beepedia has been rewritten
             * around its new 26.2 data model.
             */


            /*
             * Collect any bee.
             *
             * Display icon uses a vanilla bee in a jar, but the criterion
             * only requires that the JAR_BEE component exists.
             */
            ItemStackTemplate filledJar = createJarTemplate(EntityTypes.BEE, Color.parseColor("#FFedc343"));

            AdvancementHolder collectBee = writer.apply(createAdvancement(filledJar, "collect_bee", beeJar)
                    .addCriterion("has_filled_jar", hasAnyJarBee(registries))
                    .build(advancementId("collect_bee")));


            /*
             * Kitten Bee challenge.
             */
            EntityType<?> kittenBeeType = BeeRegistry.get()
                    .getBeeData(Identifier.parse("resourcefulbees:kitten_bee"))
                    .entityType();

            ItemStackTemplate kittenJar = createJarTemplate(kittenBeeType, Color.parseColor("#FFEAA939"));

            writer.apply(
                    createChallengeAchievement(
                            kittenJar,
                            "kitten_bee",
                            collectBee
                    )
                            .addCriterion(
                                    "has_kitten_jar",
                                    hasJarBee(
                                            registries,
                                            kittenBeeType
                                    )
                            )
                            .build(
                                    advancementId("kitten_bee")
                            )
            );


            /*
             * Oreo Bee challenge.
             */
            EntityType<?> oreoBeeType = BeeRegistry.get().getBeeData(Identifier.parse("resourcefulbees:oreo_bee")).entityType();

            ItemStackTemplate oreoJar = createJarTemplate(oreoBeeType, Color.parseColor("#FF442920"));

            writer.apply(
                    createChallengeAchievement(
                            oreoJar,
                            "oreo_bee",
                            collectBee
                    )
                            .addCriterion(
                                    "has_oreo_jar",
                                    hasJarBee(
                                            registries,
                                            oreoBeeType
                                    )
                            )
                            .build(
                                    advancementId("oreo_bee")
                            )
            );
        }

        private static ItemStackTemplate createJarTemplate(EntityType<?> entityType, int color) {
            DataComponentPatch components = DataComponentPatch.builder()
                    .set(ModDataComponents.JAR_BEE.get(), JarOccupant.from(entityType, color))
                    .build();

            return new ItemStackTemplate(ModItems.BEE_JAR.holder(), 1, components);
        }
    }
}