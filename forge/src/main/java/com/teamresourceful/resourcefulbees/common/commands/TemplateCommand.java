package com.teamresourceful.resourcefulbees.common.commands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.beedata.traits.TraitData;
import com.teamresourceful.resourcefulbees.api.honeycombdata.OutputVariation;
import com.teamresourceful.resourcefulbees.api.honeydata.HoneyData;
import com.teamresourceful.resourcefulbees.api.spawndata.SpawnData;
import com.teamresourceful.resourcefulbees.common.lib.templates.*;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;

public class TemplateCommand implements Command<CommandSourceStack> {

    private static final TemplateCommand CMD = new TemplateCommand();
    private static final Gson PRETTY_GSON = new GsonBuilder().setPrettyPrinting().create();
    //can this be a static final?
    private static final DynamicOps<JsonElement> REGISTRY_OPS = RegistryOps.create(JsonOps.INSTANCE, RegistryAccess.builtinCopy());
    private static final String TEMPLATE_STRING = "template";

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal(TEMPLATE_STRING).requires(commandSourceStack -> commandSourceStack.hasPermission(Commands.LEVEL_GAMEMASTERS)).executes(CMD);
        builder.then(Commands.literal("bee").executes(TemplateCommand::printBeeTemplate))
                .then(Commands.literal("honeycomb").executes(TemplateCommand::printHoneycombTemplate))
                .then(Commands.literal("honey").executes(TemplateCommand::printHoneyTemplate))
                .then(Commands.literal("spawn_data").executes(TemplateCommand::printSpawnDataTemplate))
                .then(Commands.literal("trait").executes(TemplateCommand::printTraitTemplate));
        return builder;
    }

    private static int printBeeTemplate(CommandContext<CommandSourceStack> context) {
        DataResult<JsonElement> beeResult = CustomBeeData.codec(TEMPLATE_STRING).encodeStart(REGISTRY_OPS, DummyBeeData.DUMMY_CUSTOM_BEE_DATA);
        ResourcefulBees.LOGGER.info(PRETTY_GSON.toJson(beeResult.getOrThrow(false, ResourcefulBees.LOGGER::error)));
        context.getSource().sendSuccess(Component.literal("Bee template printed to logs!"), true);
        return 1;
    }

    private static int printHoneycombTemplate(CommandContext<CommandSourceStack> context) {
        DataResult<JsonElement> variationResult = OutputVariation.CODEC.encodeStart(REGISTRY_OPS, DummyHoneycombData.DUMMY_OUTPUT_VARIATION);
        ResourcefulBees.LOGGER.info(PRETTY_GSON.toJson(variationResult.getOrThrow(false, ResourcefulBees.LOGGER::error)));
        context.getSource().sendSuccess(Component.literal("Honeycomb template printed to logs!"), true);
        return 1;
    }

    private static int printHoneyTemplate(CommandContext<CommandSourceStack> context) {
        DataResult<JsonElement> honeyResult = HoneyData.codec(TEMPLATE_STRING).encodeStart(REGISTRY_OPS, DummyHoneyData.DUMMY_HONEY_DATA);
        ResourcefulBees.LOGGER.info(PRETTY_GSON.toJson(honeyResult.getOrThrow(false, ResourcefulBees.LOGGER::error)));
        context.getSource().sendSuccess(Component.literal("Honey template printed to logs!"), true);
        return 1;
    }

    private static int printSpawnDataTemplate(CommandContext<CommandSourceStack> context) {
        DataResult<JsonElement> spawnResult = SpawnData.CODEC.encodeStart(REGISTRY_OPS, DummySpawnData.DUMMY_SPAWN_DATA);
        ResourcefulBees.LOGGER.info(PRETTY_GSON.toJson(spawnResult.getOrThrow(false, ResourcefulBees.LOGGER::error)));
        context.getSource().sendSuccess(Component.literal("Spawn data template printed to logs!"), true);
        return 1;
    }

    private static int printTraitTemplate(CommandContext<CommandSourceStack> context) {
        DataResult<JsonElement> traitResult = TraitData.codec(TEMPLATE_STRING).encodeStart(REGISTRY_OPS, DummyTraitData.DUMMY_TRAIT_DATA);
        ResourcefulBees.LOGGER.info(PRETTY_GSON.toJson(traitResult.getOrThrow(false, ResourcefulBees.LOGGER::error)));
        context.getSource().sendSuccess(Component.literal("Trait template printed to logs!"), true);
        return 1;
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return 1;
    }
}
