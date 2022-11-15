package com.teamresourceful.resourcefulbees.client.screens.beepedia;

import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public final class BeepediaSearchHandler {

    private static final String OR_SEPARATOR = " | ";
    private static final String AND_SEPARATOR = " & ";
    private static final String ID_SYMBOL = "@";
    private static final String NOT_SYMBOL = "!";

    private BeepediaSearchHandler() {
        throw new UtilityClassError();
    }

    public static Predicate<CustomBeeData> search(@Nullable String search) {
        if (search == null) return predicateTrue();
        String trimmed = search.trim();
        if (search.contains(OR_SEPARATOR)) {
            return orSearch(search.split(Pattern.quote(OR_SEPARATOR)));
        } else if (search.contains(AND_SEPARATOR)) {
            return andSearch(search.split(Pattern.quote(AND_SEPARATOR)));
        } else if (trimmed.startsWith(ID_SYMBOL)) {
            return searchId(trimmed.substring(1));
        } else if (trimmed.startsWith(NOT_SYMBOL)) {
            return Predicate.not(search(trimmed.substring(1)));
        }
        return searchName(search);
    }

    @NotNull
    private static Predicate<CustomBeeData> orSearch(String[] search) {
        Predicate<CustomBeeData> predicate = predicateFalse();
        for (String s : search) predicate = predicate.or(search(s));
        return predicate;
    }

    @NotNull
    private static Predicate<CustomBeeData> andSearch(String[] search) {
        Predicate<CustomBeeData> predicate = predicateTrue();
        for (String s : search) predicate = predicate.and(search(s));
        return predicate;
    }

    @NotNull
    private static Predicate<CustomBeeData> searchName(String search) {
        return bee -> bee.displayName().getString().toLowerCase().contains(search.toLowerCase());
    }

    @NotNull
    private static Predicate<CustomBeeData> searchId(String search) {
        return bee -> bee.coreData().name().toLowerCase().contains(search.toLowerCase());
    }

    private static <T> Predicate<T> predicateTrue() {
        return any -> true;
    }

    private static <T> Predicate<T> predicateFalse() {
        return any -> false;
    }
}
