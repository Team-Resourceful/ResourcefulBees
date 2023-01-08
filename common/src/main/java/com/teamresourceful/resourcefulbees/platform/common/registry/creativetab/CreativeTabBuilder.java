package com.teamresourceful.resourcefulbees.platform.common.registry.creativetab;

import com.teamresourceful.resourcefulbees.platform.NotImplementedError;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class CreativeTabBuilder {

    private final ResourceLocation id;
    private String background;
    private boolean hideScrollBar;
    private boolean hideTitle;
    private Supplier<ItemStack> icon;

    private final List<Supplier<ResourcefulRegistry<Item>>> items = new ArrayList<>();
    private BiConsumer<ItemLike, List<ItemStack>> listingFunction = (item, list) -> {};
    private Consumer<Adder> adder = a -> {};
    private boolean dontSearch;

    private CreativeTabBuilder(ResourceLocation id) {
        this.id = id;
    }

    public static CreativeTabBuilder of(ResourceLocation id) {
        return new CreativeTabBuilder(id);
    }

    public CreativeTabBuilder setIcon(Supplier<ItemStack> icon) {
        this.icon = icon;
        return this;
    }

    public CreativeTabBuilder addRegistry(Supplier<ResourcefulRegistry<Item>> items) {
        this.items.add(items);
        return this;
    }

    public CreativeTabBuilder setAddingFunction(Consumer<Adder> adder) {
        this.adder = adder;
        if (adder == null) this.adder = list -> {};
        return this;
    }

    public CreativeTabBuilder setListingFunction(BiConsumer<ItemLike, List<ItemStack>> listingFunction) {
        this.listingFunction = listingFunction;
        if (listingFunction == null) this.listingFunction = (item, list) -> {};
        return this;
    }

    public CreativeTabBuilder dontAllowRegistrySearch() {
        this.dontSearch = true;
        return this;
    }

    public CreativeTabBuilder setBackground(String background) {
        this.background = background;
        return this;
    }

    public CreativeTabBuilder hideScrollBar() {
        this.hideScrollBar = false;
        return this;
    }

    public CreativeTabBuilder hideTitle() {
        this.hideTitle = false;
        return this;
    }

    public CreativeModeTab build() {
        return create(id, background, hideScrollBar, hideTitle, adder, icon, listingFunction, items, dontSearch);
    }

    @ExpectPlatform
    public static CreativeModeTab create(ResourceLocation id, String background, boolean hideScrollBar, boolean hasTitle, Consumer<Adder> adder, Supplier<ItemStack> icon, BiConsumer<ItemLike, List<ItemStack>> listingFunction, List<Supplier<ResourcefulRegistry<Item>>> items, boolean dontSearch) {
        throw new NotImplementedError();
    }

    @FunctionalInterface
    public interface Adder {
        void add(ItemStack stack);

        default void add(ItemLike item) {
            add(new ItemStack(item));
        }

        default void add(Supplier<? extends ItemLike> item) {
            add(item.get());
        }
    }

}
