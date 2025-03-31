package com.teamresourceful.resourcefulbees.client.screen.beepedia.state;

import com.mojang.datafixers.util.Pair;
import com.teamresourceful.resourcefulbees.client.screen.beepedia.BeepediaScreen;
import com.teamresourceful.resourcefulbees.client.screen.beepedia.BeepediaTextures;
import com.teamresourceful.resourcefullib.client.screens.state.PageState;
import com.teamresourceful.resourcefullib.client.screens.state.ScreenState;
import com.teamresourceful.resourcefullib.common.utils.TriState;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class BeepediaState implements ScreenState {

    private Type type = Type.BEES;
    private PageState<BeepediaScreen> page = null;
    private Pair<Sorting, TriState> sorting = Pair.of(Sorting.ALPHABETICAL, TriState.TRUE);
    private String search = null;

    public void setSorting(Sorting sorting, @Nullable TriState value) {
        this.sorting = Pair.of(sorting, value);
    }

    public TriState getSorting(Sorting sorting) {
        if (sorting.equals(this.sorting.getFirst())) return this.sorting.getSecond();
        return TriState.UNDEFINED;
    }

    @Override
    public Screen createScreen() {
        BeepediaScreen screen = new BeepediaScreen();
        if (page != null) screen.setSubScreen(page.createScreen(screen));
        return screen;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setPage(PageState<BeepediaScreen> page) {
        this.page = page;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
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
