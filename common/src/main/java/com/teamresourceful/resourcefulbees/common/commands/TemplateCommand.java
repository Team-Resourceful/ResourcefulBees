package com.teamresourceful.resourcefulbees.common.commands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefulbees.api.data.honeycomb.OutputVariation;
import com.teamresourceful.resourcefulbees.api.data.trait.Trait;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.ModTranslations;
import com.teamresourceful.resourcefulbees.common.lib.templates.DummyBeeData;
import com.teamresourceful.resourcefulbees.common.lib.templates.DummyHoneyData;
import com.teamresourceful.resourcefulbees.common.lib.templates.DummyHoneycombData;
import com.teamresourceful.resourcefulbees.common.lib.templates.DummyTraitData;
import com.teamresourceful.resourcefulbees.common.registries.custom.BeeDataRegistry;
import com.teamresourceful.resourcefulbees.common.registries.custom.HoneyDataRegistry;
import com.teamresourceful.resourcefullib.common.codecs.maps.DispatchMapCodec;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;

public final class TemplateCommand {

    private static final Gson PRETTY_GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String TEMPLATE_STRING = "template";

    private TemplateCommand() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal(TEMPLATE_STRING);
        builder.then(Commands.literal("bee").requires(stack -> stack.hasPermission(Commands.LEVEL_GAMEMASTERS)).executes(TemplateCommand::printBeeTemplate))
                .then(Commands.literal("honeycomb").requires(stack -> stack.hasPermission(Commands.LEVEL_GAMEMASTERS)).executes(TemplateCommand::printHoneycombTemplate))
                .then(Commands.literal("honey").requires(stack -> stack.hasPermission(Commands.LEVEL_GAMEMASTERS)).executes(TemplateCommand::printHoneyTemplate))
                .then(Commands.literal("trait").requires(stack -> stack.hasPermission(Commands.LEVEL_GAMEMASTERS)).executes(TemplateCommand::printTraitTemplate));
        return builder;
    }

    private static DynamicOps<JsonElement> registryOps(CommandContext<CommandSourceStack> context) {
        return RegistryOps.create(JsonOps.INSTANCE, context.getSource().registryAccess());
    }

    private static int printBeeTemplate(CommandContext<CommandSourceStack> context) {
        DataResult<JsonElement> beeResult = new DispatchMapCodec<>(ResourceLocation.CODEC, BeeDataRegistry.codec(TEMPLATE_STRING))
                .encodeStart(registryOps(context), DummyBeeData.DATA);
        ModConstants.LOGGER.info(PRETTY_GSON.toJson(beeResult.getOrThrow(false, ModConstants.LOGGER::error)));
        context.getSource().sendSuccess(() -> ModTranslations.BEE_TEMPLATE_PRINTED, true);

        //TODO move these into their own template commands and finish the entity predicate
        /*LocationPredicate predicate = new LocationPredicate(
                MinMaxBounds.Doubles.between(10d, 40d),
                MinMaxBounds.Doubles.between(10d, 40d),
                MinMaxBounds.Doubles.between(10d, 40d),
                Biomes.DARK_FOREST,
                Structures.WOODLAND_MANSION.unwrapKey().get(),
                context.getSource().getLevel().dimension(),
                true,
                new LightPredicate.Builder().setComposite(MinMaxBounds.Ints.between(0, 7)).build(),
                new BlockPredicate(BlockTags.BEEHIVES, Set.of(Blocks.COBBLESTONE, Blocks.DIAMOND_BLOCK), StatePropertiesPredicate.Builder.properties().hasProperty(BeehiveBlock.HONEY_LEVEL, 5).build(), new NbtPredicate(new CompoundTag())),
                FluidPredicate.Builder.fluid().of(Fluids.LAVA).build()
        );

        ModConstants.LOGGER.info(predicate.serializeToJson());
        ModConstants.LOGGER.info("Printed Location Predicate");

        Map<MobEffect, MobEffectsPredicate.MobEffectInstancePredicate> effectInstancePredicateMap = new HashMap<>();
        effectInstancePredicateMap.put(MobEffects.REGENERATION, new MobEffectsPredicate.MobEffectInstancePredicate(MinMaxBounds.Ints.between(1, 10), MinMaxBounds.Ints.between(1, 10), true, true));

        MobEffectsPredicate mobEffectsPredicate = new MobEffectsPredicate(effectInstancePredicateMap);
        ModConstants.LOGGER.info(mobEffectsPredicate.serializeToJson());
        ModConstants.LOGGER.info("Printed Mob Effects Predicate");

        EntityFlagsPredicate entityFlagsPredicate = new EntityFlagsPredicate(true, true, true, true, true);
        ModConstants.LOGGER.info(entityFlagsPredicate.serializeToJson());
        ModConstants.LOGGER.info("Printed Entity Flags Predicate");

        EntityPredicate entityPredicate = EntityPredicate.Builder.entity().equipment(EntityEquipmentPredicate.Builder.equipment().mainhand(ItemPredicate.Builder.item().hasEnchantment(new EnchantmentPredicate(Enchantments.BLOCK_FORTUNE, MinMaxBounds.Ints.between(1, 4))).build()).build()).build();
        ModConstants.LOGGER.info(entityPredicate.serializeToJson());
        ModConstants.LOGGER.info("Printed Entity Predicate");*/


        return 1;
    }

    private static int printHoneycombTemplate(CommandContext<CommandSourceStack> context) {
        DataResult<JsonElement> variationResult = OutputVariation.CODEC.encodeStart(registryOps(context), DummyHoneycombData.DUMMY_OUTPUT_VARIATION);
        ModConstants.LOGGER.info(PRETTY_GSON.toJson(variationResult.getOrThrow(false, ModConstants.LOGGER::error)));
        context.getSource().sendSuccess(() -> ModTranslations.HONEYCOMB_TEMPLATE_PRINTED, true);
        return 1;
    }

    private static int printHoneyTemplate(CommandContext<CommandSourceStack> context) {
        DataResult<JsonElement> honeyResult = new DispatchMapCodec<>(ResourceLocation.CODEC, HoneyDataRegistry.codec(TEMPLATE_STRING))
                .encodeStart(registryOps(context), DummyHoneyData.DATA);
        ModConstants.LOGGER.info(PRETTY_GSON.toJson(honeyResult.getOrThrow(false, ModConstants.LOGGER::error)));
        context.getSource().sendSuccess(() -> ModTranslations.HONEY_TEMPLATE_PRINTED, true);
        return 1;
    }

    private static int printTraitTemplate(CommandContext<CommandSourceStack> context) {
        DataResult<JsonElement> traitResult = Trait.getCodec(TEMPLATE_STRING).encodeStart(registryOps(context), DummyTraitData.DUMMY_TRAIT_DATA);
        ModConstants.LOGGER.info(PRETTY_GSON.toJson(traitResult.getOrThrow(false, ModConstants.LOGGER::error)));
        context.getSource().sendSuccess(() -> ModTranslations.TRAIT_TEMPLATE_PRINTED, true);
        return 1;
    }
}
