package com.resourcefulbees.resourcefulbees.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.IBeepediaData;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.capabilities.BeepediaData;
import com.resourcefulbees.resourcefulbees.lib.ModConstants;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class BeepediaCommand {

    private BeepediaCommand() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    private static final DynamicCommandExceptionType BEE_NOT_FOUND = new DynamicCommandExceptionType(input -> new TranslationTextComponent("argument.resourcefulbees.beepedia.bee_not_found"));

    private static final String PLAYER = "player";
    private static final String BEE = "bee";

    protected static final Set<ResourceLocation> VALID_BEES = BeeRegistry.getRegistry().getBees().values().stream().map(CustomBeeData::getEntityTypeRegistryID).collect(Collectors.toCollection(LinkedHashSet::new));

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("beepedia")
                .requires(cs -> cs.hasPermission(2))
                .then(Commands.argument(PLAYER, EntityArgument.player())
                        .then(DiscoverCommand.register())
                        .then(ForgetCommand.register())
                        .then(ResetCommand.register())
                        .then(CompleteCommand.register())));
    }

    public enum MessageTypes {
        UNLOCK,
        LOCK,
        COMPLETE,
        RESET;
    }

    private static class DiscoverCommand {
        public static ArgumentBuilder<CommandSource, ?> register() {
            return Commands.literal("discover").then(Commands.argument(BEE, BeeArgument.bees()).executes(context -> {
                String arg = getBee(context);
                ServerPlayerEntity playerEntity = EntityArgument.getPlayer(context, PLAYER);
                IBeepediaData data = playerEntity.getCapability(BeepediaData.Provider.BEEPEDIA_DATA).orElse(new BeepediaData());
                data.getBeeList().add(arg);
                BeepediaData.sync(playerEntity, data);
                sendMessageToPlayer(playerEntity, MessageTypes.UNLOCK, arg);
                return 1;
            }));
        }
    }

    private static class ForgetCommand {

        public static ArgumentBuilder<CommandSource, ?> register() {
            return Commands.literal("forget").then(Commands.argument(BEE, BeeArgument.bees()).executes(context -> {
                String arg = getBee(context);
                ServerPlayerEntity playerEntity = EntityArgument.getPlayer(context, PLAYER);
                IBeepediaData data = playerEntity.getCapability(BeepediaData.Provider.BEEPEDIA_DATA).orElse(new BeepediaData());
                data.getBeeList().remove(arg);
                BeepediaData.sync(playerEntity, data);
                sendMessageToPlayer(playerEntity, MessageTypes.LOCK, arg);
                return 1;
            }));
        }
    }

    private static String getBee(CommandContext<CommandSource> context) {
        ResourceLocation bee = BeeArgument.getBee(context, BEE);
        return bee.toString().replaceFirst(String.format("^%s:", ResourcefulBees.MOD_ID), "").replaceAll("_bee$", "");
    }

    private static class ResetCommand {

        public static ArgumentBuilder<CommandSource, ?> register() {
            return Commands.literal("reset").executes(context -> {
                BeepediaCommand.removeAllBees(context);
                return 1;
            });
        }
    }

    private static class CompleteCommand {

        public static ArgumentBuilder<CommandSource, ?> register() {
            return Commands.literal("complete").executes(context -> {
                addAllBees(context);
                return 1;
            });
        }
    }

    private static void addAllBees(CommandContext<CommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity playerEntity = EntityArgument.getPlayer(context, PLAYER);
        IBeepediaData data = playerEntity.getCapability(BeepediaData.Provider.BEEPEDIA_DATA).orElse(new BeepediaData());
        data.getBeeList().addAll(BeeRegistry.getRegistry().getBees().values().stream().map(CustomBeeData::getName).collect(Collectors.toCollection(LinkedHashSet::new)));
        BeepediaData.sync(playerEntity, data);
        sendMessageToPlayer(playerEntity, MessageTypes.COMPLETE, "");
    }

    private static void removeAllBees(CommandContext<CommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity playerEntity = EntityArgument.getPlayer(context, PLAYER);
        IBeepediaData data = playerEntity.getCapability(BeepediaData.Provider.BEEPEDIA_DATA).orElse(new BeepediaData());
        data.getBeeList().clear();
        BeepediaData.sync(playerEntity, data);
        sendMessageToPlayer(playerEntity, MessageTypes.RESET, "");
    }

    private static void sendMessageToPlayer(PlayerEntity playerEntity, BeepediaCommand.MessageTypes messageTypes, String bee) {
        switch (messageTypes) {
            case LOCK:
            case UNLOCK:
                String translation = messageTypes.equals(MessageTypes.UNLOCK) ? "command.resourcefulbees.beepedia.bee_discovered" : "command.resourcefulbees.beepedia.bee_forgotten";
                String beeName = BeeRegistry.getRegistry().getBeeData(bee).getTranslation().getString();
                playerEntity.displayClientMessage(new TranslationTextComponent(translation, beeName), false);
                break;
            case COMPLETE:
                playerEntity.displayClientMessage(new TranslationTextComponent("command.resourcefulbees.beepedia.complete"), false);
                break;
            case RESET:
                playerEntity.displayClientMessage(new TranslationTextComponent("command.resourcefulbees.beepedia.reset"), false);
                break;
            default: //Do Nothing
        }
    }

    public static class BeeArgument implements ArgumentType<ResourceLocation> {

        public static BeeArgument bees() {
            return new BeeArgument();
        }

        public static ResourceLocation getBee(final CommandContext<?> context, String bee) {
            return context.getArgument(bee, ResourceLocation.class);
        }

        @Override
        public ResourceLocation parse(StringReader reader) throws CommandSyntaxException {
            int cursor = reader.getCursor();
            ResourceLocation id = ResourceLocation.read(reader);
            if (!BeepediaCommand.VALID_BEES.contains(id)) {
                reader.setCursor(cursor);
                throw BEE_NOT_FOUND.createWithContext(reader, id.toString());
            }
            return id;
        }

        @Override
        public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
            VALID_BEES.forEach(b -> builder.suggest(b.toString()));
            return builder.buildFuture();
        }

        @Override
        public Collection<String> getExamples() {
            return VALID_BEES.stream().map(ResourceLocation::toString).collect(Collectors.toCollection(LinkedHashSet::new));
        }
    }
}
