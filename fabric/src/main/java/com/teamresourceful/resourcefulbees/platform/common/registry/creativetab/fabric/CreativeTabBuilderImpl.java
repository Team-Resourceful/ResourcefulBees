package com.teamresourceful.resourcefulbees.platform.common.registry.creativetab.fabric;

import com.teamresourceful.resourcefulbees.platform.common.registry.api.RegistryEntry;
import com.teamresourceful.resourcefulbees.platform.common.registry.api.ResourcefulRegistry;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class CreativeTabBuilderImpl {
    public static CreativeModeTab create(ResourceLocation id, String background, boolean hideScrollBar, boolean hideTitle, Supplier<ItemStack> icon, BiConsumer<ItemLike, List<ItemStack>> listingFunction, List<Supplier<ResourcefulRegistry<Item>>> registryItems, boolean dontSearch) {
        // We do this to get the array expanded fabric doesnt let use edit the
        // display items so we just let fabric expand the array size and then replace it after.
        FabricItemGroupBuilder.create(id).build();


        CreativeModeTab tab = new CreativeModeTab(CreativeModeTab.TABS.length - 1, String.format("%s.%s", id.getNamespace(), id.getPath())) {
            @Override
            public @NotNull ItemStack makeIcon() {
                return icon.get();
            }

            @Override
            public void fillItemList(@NotNull NonNullList<ItemStack> items) {
                if (!dontSearch) {
                    for (Item item : Registry.ITEM) {
                        item.fillItemCategory(this, items);
                        listingFunction.accept(item, items);
                    }
                }

                registryItems.stream()
                    .map(Supplier::get)
                    .map(ResourcefulRegistry::getEntries)
                    .flatMap(Collection::stream)
                    .map(RegistryEntry::get)
                    .map(ItemStack::new)
                    .forEach(item -> {
                        items.add(item);
                        listingFunction.accept(item.getItem(), items);
                    });
            }
        };
        if (hideScrollBar) tab.hideScroll();
        if (hideTitle) tab.hideTitle();
        if (background != null) tab.setBackgroundSuffix(background);
        return tab;
    }
}
