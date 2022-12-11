package com.teamresourceful.resourcefulbees.common.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.teamresourceful.resourcefulbees.api.ResourcefulBeesAPI;
import com.teamresourceful.resourcefulbees.api.data.bee.CustomBeeData;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class BeeArgument implements ArgumentType<String> {

    private static final DynamicCommandExceptionType BEE_NOT_FOUND = new DynamicCommandExceptionType(input -> TranslationConstants.Beepedia.COMMAND_NONE_FOUND);

    public static final Set<String> BEES = ResourcefulBeesAPI.getRegistry().getBeeRegistry()
            .getStreamOfBees()
            .map(CustomBeeData::name)
            .collect(Collectors.toUnmodifiableSet());

    public static RequiredArgumentBuilder<CommandSourceStack, String> argument() {
        return Commands.argument("bee", new BeeArgument());
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        int cursor = reader.getCursor();
        String id = reader.readString();
        if (!BEES.contains(id)) {
            reader.setCursor(cursor);
            throw BEE_NOT_FOUND.createWithContext(reader, id);
        }
        return id;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(BEES.stream(), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return BEES;
    }

    public static String get(final CommandContext<?> context) throws CommandSyntaxException {
        String value = context.getArgument("bee", String.class);
        if (BEES.contains(value)) {
            return value;
        }
        throw BEE_NOT_FOUND.create(value);
    }
}
