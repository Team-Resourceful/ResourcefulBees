package com.teamresourceful.resourcefulbees.client.screens.beepedia.state;

import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.beedata.traits.BeeTrait;
import com.teamresourceful.resourcefulbees.api.honeydata.HoneyData;
import com.teamresourceful.resourcefulbees.client.screens.beepedia.BeepediaScreen;
import com.teamresourceful.resourcefulbees.client.screens.beepedia.pages.bee.BeePage;
import com.teamresourceful.resourcefulbees.client.screens.beepedia.pages.honey.HoneyPage;
import com.teamresourceful.resourcefulbees.client.screens.beepedia.pages.trait.TraitPage;
import com.teamresourceful.resourcefulbees.common.registry.custom.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.registry.custom.HoneyRegistry;
import com.teamresourceful.resourcefulbees.common.registry.custom.TraitRegistry;
import com.teamresourceful.resourcefullib.client.screens.state.PageState;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    public static StringPageState<HoneyData, BeepediaScreen> createHoneyPageState(String honeyType) {
        return new StringPageState<>(honeyType, HoneyRegistry.getRegistry()::getHoneyData, HoneyPage::new);
    }

    public static StringPageState<CustomBeeData, BeepediaScreen> createBeePageState(String beeType) {
        return new StringPageState<>(beeType, BeeRegistry.getRegistry()::getBeeData, BeePage::new);
    }

    public static StringPageState<BeeTrait, BeepediaScreen> createTraitPageState(String traitType) {
        return new StringPageState<>(traitType, TraitRegistry.getRegistry()::getTrait, TraitPage::new);
    }

    @Override
    public Screen createScreen(P data) {
        T t = getter.apply(string);
        return t == null ? null : creator.apply(t);
    }
}
