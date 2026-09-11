package com.teamresourceful.resourcefulbees.common.commands;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefulbees.api.ResourcefulBeesAPI;
import com.teamresourceful.resourcefulbees.api.data.honey.HoneyBlockData;
import com.teamresourceful.resourcefulbees.api.data.honey.bottle.HoneyBottleData;
import com.teamresourceful.resourcefulbees.api.data.honey.fluid.HoneyFluidData;
import com.teamresourceful.resourcefulbees.common.data.RecipeBuilder;
import com.teamresourceful.resourcefulbees.common.items.honey.CustomHoneycombItem;
import com.teamresourceful.resourcefulbees.common.recipes.SolidificationRecipe;
import com.teamresourceful.resourcefulbees.common.recipes.breeder.BreederRecipe;
import com.teamresourceful.resourcefulbees.common.registries.custom.HoneyRegistry;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModRecipes;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.nio.file.Path;
import java.util.List;

public class RecipeCommand {
    private RecipeCommand() throws UtilityClassException {
        throw new UtilityClassException();
    }

    // -------------------------------------------------------------------------
    // Honeycomb recipes
    // -------------------------------------------------------------------------
    static int generateHoneycombRecipes(CommandContext<CommandSourceStack> context) {
        Path recipePath = CommandUtil.GENERATED_DATA_PATH.resolve("recipe").resolve("crafting");

        ModItems.HONEYCOMB_ITEMS.getEntries().forEach(entry -> {
            Item item = entry.get();

            if (!(item instanceof CustomHoneycombItem comb)) {
                return;
            }

            if (!comb.hasStorageBlockItem()) {
                return;
            }

            Item storageBlock = comb.getStorageBlockItem();

            if (storageBlock == Items.AIR) {
                return;
            }

            // 4 honeycombs -> honeycomb block
            CommandUtil.writeJsonFile(CommandUtil.createShapedRecipe(new String[]{"CC", "CC"}, 'C', item, storageBlock, 1),
                    recipePath.resolve(CommandUtil.itemPath(storageBlock) + ".json")
            );

            // honeycomb block -> 4 honeycombs
            CommandUtil.writeJsonFile(CommandUtil.createShapelessRecipe(List.of(storageBlock), item, 4),
                    recipePath.resolve(CommandUtil.itemPath(item) + ".json")
            );
        });

        context.getSource().sendSuccess(() -> Component.literal("Generated Resourceful Bees honeycomb recipes."), false);

        return 1;
    }

    // -------------------------------------------------------------------------
    // Honey recipes
    // -------------------------------------------------------------------------
    static int generateHoneyRecipes(CommandContext<CommandSourceStack> context) {
        Path recipePath = CommandUtil.GENERATED_DATA_PATH.resolve("recipe").resolve("crafting");

        HoneyRegistry.getRegistry()
                .getStreamOfHoney()
                .forEach(honey -> {
                    String name = honey.name();

                    HoneyBottleData bottleData = honey.getBottleData();
                    HoneyFluidData fluidData = honey.getFluidData();
                    HoneyBlockData blockData = honey.getBlockData();

                    Item bottle = bottleData.bottle().get();
                    Item bucket = fluidData.fluidBucket().get();
                    Item block = blockData.blockItem().get();

                    /*
                     * 4 honey bottles -> honey block
                     */
                    if (bottle != Items.AIR && block != Items.AIR) {
                        CommandUtil.writeJsonFile(
                                CommandUtil.createShapedRecipe(
                                        new String[]{
                                                "HH",
                                                "HH"
                                        },
                                        'H',
                                        bottle,
                                        block,
                                        1
                                ),
                                recipePath.resolve(name + "_honey_block.json")
                        );
                    }

                    /*
                     * Honey block + 4 glass bottles -> 4 honey bottles
                     */
                    if (block != Items.AIR && bottle != Items.AIR) {
                        CommandUtil.writeJsonFile(
                                CommandUtil.createShapelessRecipe(
                                        List.of(
                                                block,
                                                Items.GLASS_BOTTLE,
                                                Items.GLASS_BOTTLE,
                                                Items.GLASS_BOTTLE,
                                                Items.GLASS_BOTTLE
                                        ),
                                        bottle,
                                        4
                                ),
                                recipePath.resolve(name + "_honey_bottle.json")
                        );
                    }

                    /*
                     * Bucket + 4 honey bottles -> honey bucket
                     */
                    if (bottle != Items.AIR && bucket != Items.AIR) {
                        CommandUtil.writeJsonFile(
                                CommandUtil.createShapelessRecipe(
                                        List.of(
                                                Items.BUCKET,
                                                bottle,
                                                bottle,
                                                bottle,
                                                bottle
                                        ),
                                        bucket,
                                        1
                                ),
                                recipePath.resolve(name + "_bottle_to_bucket.json")
                        );
                    }

                    /*
                     * Honey bucket + 4 glass bottles -> 4 honey bottles
                     */
                    if (bucket != Items.AIR && bottle != Items.AIR) {
                        CommandUtil.writeJsonFile(
                                CommandUtil.createShapelessRecipe(
                                        List.of(
                                                Items.GLASS_BOTTLE,
                                                Items.GLASS_BOTTLE,
                                                Items.GLASS_BOTTLE,
                                                Items.GLASS_BOTTLE,
                                                bucket
                                        ),
                                        bottle,
                                        4
                                ),
                                recipePath.resolve(name + "_bucket_to_bottle.json")
                        );
                    }

                    /*
                     * Honey block + empty bucket -> honey bucket
                     */
                    if (block != Items.AIR && bucket != Items.AIR) {
                        CommandUtil.writeJsonFile(
                                CommandUtil.createShapelessRecipe(
                                        List.of(
                                                block,
                                                Items.BUCKET
                                        ),
                                        bucket,
                                        1
                                ),
                                recipePath.resolve(name + "_block_to_bucket.json")
                        );
                    }

                    /*
                     * Honey bucket -> honey block
                     */
                    if (bucket != Items.AIR && block != Items.AIR) {
                        CommandUtil.writeJsonFile(
                                CommandUtil.createShapelessRecipe(
                                        List.of(bucket),
                                        block,
                                        1
                                ),
                                recipePath.resolve(name + "_bucket_to_block.json")
                        );
                    }
                });

        context.getSource().sendSuccess(() -> Component.literal("Generated Resourceful Bees honey recipes."), false);

        return 1;
    }

    // -------------------------------------------------------------------------
    // Breeder recipes
    // -------------------------------------------------------------------------
    static int generateBreederRecipes(CommandContext<CommandSourceStack> context) {
        Path recipePath = CommandUtil.GENERATED_DATA_PATH
                .resolve("recipe")
                .resolve("breeder");

        RegistryOps<JsonElement> registryOps = RegistryOps.create(JsonOps.INSTANCE, context.getSource().registryAccess());

        ResourcefulBeesAPI.getRegistry()
                .getBeeRegistry()
                .getFamilyTree()
                .values()
                .forEach(collection ->
                        collection.forEach(familyUnit -> {
                            Recipe<BreederRecipe.Input> recipe = RecipeBuilder.makeBreedingRecipe(collection);

                            BreederRecipe.MAP_CODEC.codec().encodeStart(registryOps, (BreederRecipe) recipe).result().ifPresent(jsonElement -> {
                                JsonObject json = jsonElement.getAsJsonObject();

                                json.addProperty("type", ModRecipes.BREEDER_RECIPE_TYPE.getId().toString());

                                String fileName = familyUnit.getParents().getParent1().getPath()
                                        + "_"
                                        + familyUnit.getParents().getParent2().getPath()
                                        + "_"
                                        + familyUnit.getChild().getPath()
                                        + ".json";

                                        CommandUtil.writeJsonFile(json, recipePath.resolve(fileName));
                            });
                        })
                );

        return 1;
    }

    // -------------------------------------------------------------------------
    // Chamber recipes
    // -------------------------------------------------------------------------
    static int generateChamberRecipes(CommandContext<CommandSourceStack> context) {
        Path recipePath = CommandUtil.GENERATED_DATA_PATH
                .resolve("recipe")
                .resolve("solidification");

        RegistryOps<JsonElement> registryOps = RegistryOps.create(JsonOps.INSTANCE, context.getSource().registryAccess());

        HoneyRegistry.getRegistry()
                .getStreamOfHoney()
                .forEach(honey -> {
                    HoneyFluidData fluidData = honey.getFluidData();
                    HoneyBlockData blockData = honey.getBlockData();

                    var fluid = fluidData.stillFluid().get();
                    Item result = blockData.blockItem().get();

                    if (result == Items.AIR) {
                        return;
                    }

                    SolidificationRecipe recipe = new SolidificationRecipe(SizedFluidIngredient.of(fluid, 1000), ItemStackTemplate.fromNonEmptyStack(result.getDefaultInstance()), 200);

                    SolidificationRecipe.CODEC.codec()
                            .encodeStart(registryOps, recipe)
                            .resultOrPartial(error -> {
                                throw new IllegalStateException(
                                        "Failed to encode solidification recipe for "
                                                + honey.name()
                                                + ": "
                                                + error
                                );
                            })
                            .ifPresent(jsonElement -> {
                                JsonObject json = jsonElement.getAsJsonObject();

                                json.addProperty(
                                        "type",
                                        ModRecipes.SOLIDIFICATION_RECIPE_TYPE
                                                .getId()
                                                .toString()
                                );

                                CommandUtil.writeJsonFile(json, recipePath.resolve(honey.name() + "_honey_block.json"));
                            });
                });

        context.getSource().sendSuccess(
                () -> Component.literal(
                        "Generated Resourceful Bees solidification chamber recipes."
                ),
                false
        );

        return 1;
    }
}
