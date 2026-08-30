package com.teamresourceful.resourcefulbees.common.commands;

import com.google.gson.*;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefulbees.api.ResourcefulBeesAPI;
import com.teamresourceful.resourcefulbees.api.data.BeekeeperTradeData;
import com.teamresourceful.resourcefulbees.api.data.honey.HoneyBlockData;
import com.teamresourceful.resourcefulbees.api.data.honey.bottle.HoneyBottleData;
import com.teamresourceful.resourcefulbees.api.data.honey.fluid.HoneyFluidData;
import com.teamresourceful.resourcefulbees.client.data.LangGenerator;
import com.teamresourceful.resourcefulbees.common.data.RecipeBuilder;
import com.teamresourceful.resourcefulbees.common.items.BeeJarItem;
import com.teamresourceful.resourcefulbees.common.items.base.Tradeable;
import com.teamresourceful.resourcefulbees.common.recipes.breeder.BreederRecipe;
import com.teamresourceful.resourcefulbees.common.registries.custom.HoneyRegistry;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModRecipes;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.trading.TradeCost;
import net.minecraft.world.item.trading.VillagerTrade;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.fml.loading.FMLPaths;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class GenerateCommand {

    private GenerateCommand() throws UtilityClassException {
        throw new UtilityClassException();
    }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final Path GENERATED_DATA_PATH = FMLPaths.CONFIGDIR.get()
            .resolve("resourcefulbees")
            .resolve("resources")
            .resolve("data")
            .resolve("resourcefulbees");

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("generate")
                .then(registerRecipeCommand())
                .then(registerTradeCommand())
                .then(registerLangCommand());
    }

    // -------------------------------------------------------------------------
    // Command registration
    // -------------------------------------------------------------------------

    private static ArgumentBuilder<CommandSourceStack, ?> registerLangCommand() {
        return Commands.literal("lang")
                .requires(stack -> stack.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                .executes(LangGenerator::generateEnglishLang);
    }

    private static ArgumentBuilder<CommandSourceStack, ?> registerRecipeCommand() {
        return  Commands.literal("recipe")
                .then(Commands.literal("breeder")
                        .requires(stack -> stack.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                        .executes(GenerateCommand::generateBreederRecipes)
        );
    }

    private static ArgumentBuilder<CommandSourceStack, ?> registerTradeCommand() {
        return Commands.literal("trade")
                .then(Commands.literal("beekeeper")
                        .requires(stack -> stack.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                        .executes(GenerateCommand::generateBeekeeperTrades)
        );
    }

    // -------------------------------------------------------------------------
    // Breeder recipes
    // -------------------------------------------------------------------------

    private static int generateBreederRecipes(CommandContext<CommandSourceStack> context) {
        Path recipePath = GENERATED_DATA_PATH
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

                                        writeJsonFile(json, recipePath.resolve(fileName));
                            });
                        })
                );

        return 1;
    }

    // -------------------------------------------------------------------------
    // Beekeeper trades
    // -------------------------------------------------------------------------

    private static int generateBeekeeperTrades(CommandContext<CommandSourceStack> context) {
        RegistryOps<JsonElement> registryOps = RegistryOps.create(JsonOps.INSTANCE, context.getSource().registryAccess());

        generateLevelThreeBeekeeperTrades(registryOps);
        generateLevelFiveBeekeeperTrades(registryOps);

        context.getSource().sendSuccess(() -> Component.literal("Generated Resourceful Bees beekeeper trades."), false);

        return 1;
    }

    private static void generateLevelThreeBeekeeperTrades(
            RegistryOps<JsonElement> registryOps
    ) {
        Path tradePath = GENERATED_DATA_PATH
                .resolve("villager_trade")
                .resolve("beekeeper")
                .resolve("3");

        Path tagPath = GENERATED_DATA_PATH
                .resolve("tags")
                .resolve("villager_trade")
                .resolve("beekeeper")
                .resolve("level_3.json");

        JsonArray values = baseValues();


        HoneyRegistry.getRegistry()
                .getStreamOfHoney()
                .forEach(honey -> {
                    String honeyName = honey.name();

                    // Honey bottle
                    HoneyBottleData bottleData = honey.getBottleData();

                    generateLevelThreeTrade(
                            registryOps,
                            tradePath,
                            values,
                            bottleData.bottle().get(),
                            bottleData.tradeData(),
                            honeyName + "_honey_bottle"
                    );

                    // Honey bucket
                    HoneyFluidData fluidData = honey.getFluidData();

                    generateLevelThreeTrade(
                            registryOps,
                            tradePath,
                            values,
                            fluidData.fluidBucket().get(),
                            fluidData.tradeData(),
                            honeyName + "_honey_bucket"
                    );

                    // Honey block
                    HoneyBlockData blockData = honey.getBlockData();

                    generateLevelThreeTrade(
                            registryOps,
                            tradePath,
                            values,
                            blockData.blockItem().get(),
                            blockData.tradeData(),
                            honeyName + "_honey_block"
                    );
                });

        ModItems.HONEYCOMB_ITEMS.getEntries()
                .forEach(entry -> {
                    Item item = entry.get();

                    if (item instanceof Tradeable tradeable && tradeable.isTradable()) {
                        generateLevelThreeTrade(
                                registryOps,
                                tradePath,
                                values,
                                item,
                                tradeable.getTradeData(),
                                BuiltInRegistries.ITEM.getKey(item).getPath()
                        );
                    }
                });

        writeTradeTag(
                values,
                tagPath
        );
    }

    private static @NonNull JsonArray baseValues() {
        JsonArray values = new JsonArray();

        /*
         * These four are static trades shipped with the mod.
         *
         * They preserve the vanilla-product trades from the old Beekeeper:
         *
         * 8-16 Gold Flowers -> Honeycomb
         * 8-16 Gold Flowers -> Honey Bottle
         * 8-16 Gold Flowers + Bucket -> Honey Bucket
         * 8-16 Gold Flowers -> Honey Block
         */
        values.add("resourcefulbees:beekeeper/3/honeycomb");
        values.add("resourcefulbees:beekeeper/3/honey_bottle");
        values.add("resourcefulbees:beekeeper/3/honey_bucket");
        values.add("resourcefulbees:beekeeper/3/honey_block");
        return values;
    }

    private static void generateLevelThreeTrade(RegistryOps<JsonElement> registryOps, Path tradePath, JsonArray values, Item result, BeekeeperTradeData tradeData, String name) {
        if (result == Items.AIR || !tradeData.isTradable()) {
            return;
        }

        VillagerTrade trade = createBeekeeperTrade(tradeData, result.getDefaultInstance(), UniformGenerator.between(8.0F, 16.0F));
        writeVillagerTrade(registryOps, trade, tradePath.resolve(name + ".json"));
        values.add("resourcefulbees:beekeeper/3/" + name);
    }

    private static void generateLevelFiveBeekeeperTrades(RegistryOps<JsonElement> registryOps) {
        Path tradePath = GENERATED_DATA_PATH
                .resolve("villager_trade")
                .resolve("beekeeper")
                .resolve("5");

        Path tagPath = GENERATED_DATA_PATH
                .resolve("tags")
                .resolve("villager_trade")
                .resolve("beekeeper")
                .resolve("level_5.json");

        JsonArray values = new JsonArray();

        // Static trade shipped with Resourceful Bees.
        //values.add("resourcefulbees:beekeeper/5/queen_bee_banner");

        ResourcefulBeesAPI.getRegistry()
                .getBeeRegistry()
                .getStreamOfBees()
                .filter(bee -> bee.getTradeData().isTradable())
                .forEach(bee -> {
                    BeekeeperTradeData tradeData = bee.getTradeData();
                    ItemStack result = BeeJarItem.createFilledJar(bee.entityType(), bee.getRenderData().colorData().jarColor().getOpaqueValue());
                    VillagerTrade trade = createBeekeeperTrade(tradeData, result, UniformGenerator.between(32.0F, 64.0F));
                    String name = bee.id().getPath();
                    writeVillagerTrade(registryOps, trade, tradePath.resolve(name + ".json"));
                    values.add("resourcefulbees:beekeeper/5/" + name);
                });

        writeTradeTag(values, tagPath);
    }

    private static VillagerTrade createBeekeeperTrade(BeekeeperTradeData tradeData, ItemStack result, NumberProvider flowerCost) {
        TradeCost wants = new TradeCost(ModItems.GOLD_FLOWER_ITEM.get(), flowerCost);

        Optional<TradeCost> additionalWants = tradeData.secondaryItem() == Items.AIR
                ? Optional.empty()
                : Optional.of(new TradeCost(tradeData.secondaryItem(), tradeData.secondaryItemCost()));

        result.setCount(1);
        ItemStackTemplate gives = ItemStackTemplate.fromNonEmptyStack(result);
        List<LootItemFunction> givenItemModifiers = List.of(SetItemCountFunction.setCount(tradeData.amount()).build());

        return new VillagerTrade(wants, additionalWants, gives, tradeData.maxTrades(), tradeData.xp(), tradeData.priceMultiplier(), Optional.empty(), givenItemModifiers);
    }

    // -------------------------------------------------------------------------
    // Serialization
    // -------------------------------------------------------------------------

    private static void writeVillagerTrade(RegistryOps<JsonElement> registryOps, VillagerTrade trade, Path path) {
        VillagerTrade.CODEC.encodeStart(registryOps, trade).resultOrPartial(error -> {
            throw new IllegalStateException("Failed to encode villager trade " + path.toAbsolutePath() + ": " + error);
        }).ifPresent(json -> writeJsonFile(json.getAsJsonObject(), path));
    }

    private static void writeTradeTag(JsonArray values, Path path) {
        JsonObject tag = new JsonObject();
        tag.addProperty("replace", false);
        tag.add("values", values);
        writeJsonFile(tag, path);
    }

    private static void writeJsonFile(JsonObject jsonObject, Path path) {
        try {
            Path parent = path.getParent();

            if (parent != null) {
                Files.createDirectories(parent);
            }

            try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                GSON.toJson(jsonObject, writer);
            }
        } catch (IOException exception) {
            throw new RuntimeException("Failed to write JSON file: " + path.toAbsolutePath(), exception);
        }
    }
}