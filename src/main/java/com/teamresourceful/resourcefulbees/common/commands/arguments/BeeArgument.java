package com.teamresourceful.resourcefulbees.common.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.BeepediaTranslations;
import com.teamresourceful.resourcefulbees.common.registries.custom.BeeRegistry;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.resources.Identifier;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class BeeArgument implements ArgumentType<Identifier> {

    private static final DynamicCommandExceptionType BEE_NOT_FOUND = new DynamicCommandExceptionType(_ -> BeepediaTranslations.COMMAND_NONE_FOUND);

    private static Stream<Identifier> bees() {
        return BeeRegistry.getRegistry().getBeeTypes().stream();
    }

    public static RequiredArgumentBuilder<CommandSourceStack, Identifier> argument() {
        return Commands.argument(
                "bee",
                new BeeArgument()
        );
    }

    @Override
    public Identifier parse(StringReader reader) throws CommandSyntaxException {
        int cursor = reader.getCursor();

        Identifier id = Identifier.readNonEmpty(reader);

        if (bees().noneMatch(id::equals)) {
            reader.setCursor(cursor);

            throw BEE_NOT_FOUND.createWithContext(
                    reader,
                    id
            );
        }

        return id;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(bees().map(Identifier::toString), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return bees()
                .map(Identifier::toString)
                .toList();
    }

    public static Identifier get(CommandContext<?> context) {
        return context.getArgument(
                "bee",
                Identifier.class
        );
    }
}