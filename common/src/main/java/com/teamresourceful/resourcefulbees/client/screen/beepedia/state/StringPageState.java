package com.teamresourceful.resourcefulbees.client.screen.beepedia.state;

import com.teamresourceful.resourcefulbees.api.data.bee.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.data.honey.CustomHoneyData;
import com.teamresourceful.resourcefulbees.api.data.trait.Trait;
import com.teamresourceful.resourcefulbees.api.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.api.registry.HoneyRegistry;
import com.teamresourceful.resourcefulbees.api.registry.TraitRegistry;
import com.teamresourceful.resourcefulbees.client.screen.beepedia.BeepediaScreen;
import com.teamresourceful.resourcefulbees.client.screen.beepedia.pages.bees.BeePage;
import com.teamresourceful.resourcefulbees.client.screen.beepedia.pages.honeys.HoneyPage;
import com.teamresourceful.resourcefulbees.client.screen.beepedia.pages.traits.TraitPage;
import com.teamresourceful.resourcefullib.client.screens.state.PageState;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;

public class StringPageState<T, P> implements PageState<P> {

    private final String string;
    private final Function<String, @Nullable T> getter;
    private final Function<@NotNull T, Screen> creator;

    public StringPageState(String string, Function<String, T> getter, Function<T, Screen> creator) {
        this.string = string;
        this.getter = getter;
        this.creator = creator;
    }

    public static StringPageState<CustomHoneyData, BeepediaScreen> createHoneyPageState(String honeyType) {
        return new StringPageState<>(honeyType, HoneyRegistry.get()::getHoneyData, HoneyPage::new);
    }

    public static StringPageState<CustomBeeData, BeepediaScreen> createBeePageState(String beeType, Consumer<Trait> opener) {
        return new StringPageState<>(beeType, BeeRegistry.get()::getBeeData, beeData -> new BeePage(beeData, opener));
    }

    public static StringPageState<Trait, BeepediaScreen> createTraitPageState(String traitType) {
        return new StringPageState<>(traitType, TraitRegistry.get()::getTrait, TraitPage::new);
    }

    @Override
    public Screen createScreen(P data) {
        T t = getter.apply(string);
        return t == null ? null : creator.apply(t);
    }
}
