package com.teamresourceful.resourcefulbees.common.commands;

import com.google.gson.JsonElement;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefulbees.common.loot.condition.BeeHasNectarCondition;
import com.teamresourceful.resourcefulbees.common.loot.function.RandomPollenRecipeFunction;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModEntities;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;

import java.nio.file.Path;

public final class LootCommand {

    private LootCommand() {
    }

    public static int generateBeeLoot(CommandContext<CommandSourceStack> context) {
        var source = context.getSource();

        RegistryOps<JsonElement> ops = source.getServer()
                .registryAccess()
                .createSerializationContext(JsonOps.INSTANCE);

        Path output = CommandUtil.GENERATED_DATA_PATH
                .resolve("loot_table");

        int generated = 0;

        for (var entry : ModEntities.getModBees().entrySet()) {
            var entityType = entry.getValue().get();

            var lootTableKey = entityType.getDefaultLootTable();

            if (lootTableKey.isEmpty()) {
                continue;
            }

            var json = LootTable.DIRECT_CODEC
                    .encodeStart(ops, createBeeLootTable())
                    .getOrThrow();

            var id = lootTableKey.get().identifier();

            Path path = CommandUtil.GENERATED_DATA_PATH
                    .resolveSibling(id.getNamespace())
                    .resolve("loot_table")
                    .resolve(id.getPath() + ".json");

            CommandUtil.writeJsonFile(json.getAsJsonObject(), path);
            generated++;
        }

        int count = generated;

        source.sendSuccess(
                () -> Component.literal(
                        "Generated " + count + " Resourceful Bees loot tables."
                ),
                false
        );

        return generated;
    }

    private static LootTable createBeeLootTable() {
        return LootTable.lootTable()
                .withPool(
                        LootPool.lootPool()
                                .setRolls(ContextIntProviders.between(1, 10))
                                .add(
                                        LootItem.lootTableItem(ModItems.POLLEN.get())
                                                .when(BeeHasNectarCondition::new)
                                                .apply(RandomPollenRecipeFunction::new)
                                )
                )
                .build();
    }
}