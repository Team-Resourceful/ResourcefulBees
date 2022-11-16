package com.teamresourceful.resourcefulbees.client.screens.beepedia.state;

import com.mojang.datafixers.util.Pair;
import com.teamresourceful.resourcefulbees.client.screens.beepedia.BeepediaScreen;
import com.teamresourceful.resourcefulbees.client.screens.beepedia.BeepediaTextures;
import com.teamresourceful.resourcefulbees.common.util.TriState;
import com.teamresourceful.resourcefullib.client.screens.state.PageState;
import com.teamresourceful.resourcefullib.client.screens.state.ScreenState;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class BeepediaState implements ScreenState {

    public Type type = Type.BEES;
    public PageState<BeepediaScreen> page = null;


    private Pair<Sorting, TriState> sorting = Pair.of(Sorting.ALPHABETICAL, TriState.TRUE);
    public String search = null;

    public void setSorting(Sorting sorting, @Nullable TriState value) {
        this.sorting = Pair.of(sorting, value);
    }

    public TriState getSorting(Sorting sorting) {
        if (sorting.equals(this.sorting.getFirst())) return this.sorting.getSecond();
        return TriState.UNSET;
    }

    @Override
    public Screen createScreen() {
        BeepediaScreen screen = new BeepediaScreen();
        if (page != null) screen.setSubScreen(page.createScreen(screen));
        return screen;
    }

    public enum Type {
        BEES(BeepediaTextures.BEE),
        TRAITS(BeepediaTextures.TRAIT),
        HONEY(BeepediaTextures.HONEY);

        public final ResourceLocation texture;

        Type(ResourceLocation texture) {
            this.texture = texture;
        }
    }

    public enum Sorting {
        FOUND(0),
        ALPHABETICAL(52),
        TRAITS(26),
        MUTATION(78);

        public final int u;

        Sorting(int u) {
            this.u = u;
        }
    }
}
