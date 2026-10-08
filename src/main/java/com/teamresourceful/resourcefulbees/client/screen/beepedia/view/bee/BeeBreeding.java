package com.teamresourceful.resourcefulbees.client.screen.beepedia.view.bee;

import com.teamresourceful.resourcefulbees.api.data.bee.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.data.bee.breeding.BeeBreedData;
import com.teamresourceful.resourcefulbees.api.data.bee.breeding.FamilyUnit;
import com.teamresourceful.resourcefulbees.client.component.CyclingItemStackDisplayWidget;
import com.teamresourceful.resourcefulbees.client.component.ItemStackDisplayWidget;
import com.teamresourceful.resourcefulbees.client.screen.beepedia.component.bee.BeeBreedRow;
import com.teamresourceful.resourcefulbees.client.util.BeepediaScreenUtil;
import com.teamresourceful.resourcefullib.common.color.Color;
import earth.terrarium.olympus.client.components.Widgets;
import earth.terrarium.olympus.client.components.string.TextWidget;
import earth.terrarium.olympus.client.layouts.LinearViewLayout;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Set;
import java.util.stream.StreamSupport;

public final class BeeBreeding {

    private BeeBreeding() {
    }

    public static void build(LinearViewLayout layout, CustomBeeData bee, int contentWidth) {
        var breeding = bee.getBreedData();

        layout.withGap(5);

        if (breeding.hasParents()) {
            layout.withChild(BeepediaScreenUtil.sectionTitle("Families"));
            buildBreedingContent(layout, breeding.families(), contentWidth);
        } else {
            layout.withChild(Widgets.text(Component.literal("Bee has no parents.")));
        }

        layout.withChild(BeepediaScreenUtil.sectionTitle("Feeding"));
        buildFeedingContent(layout, breeding);

        layout.withChild(BeepediaScreenUtil.sectionTitle("Timing"));
        buildTimingContent(layout, breeding);
    }

    private static void buildBreedingContent(LinearViewLayout layout, Set<FamilyUnit> families, int contentWidth) {
        boolean found = false;

        for (FamilyUnit family : families) {
            if (!family.validUnit()) {
                continue;
            }

            layout.withChild(new BeeBreedRow(family, contentWidth));

            found = true;
        }

        if (!found) {
            layout.withChild(Widgets.text(Component.literal("No valid breeding recipes.")));
        }
    }

    private static void buildTimingContent(LinearViewLayout layout, BeeBreedData breeding) {
        layout.withChild(property(
                Component.literal("Breed Delay").withColor(TextColor.GRAY),
                Component.literal(Integer.toString(breeding.breedDelay()))
        ));

        layout.withChild(property(
                Component.literal("Child Growth Delay").withColor(TextColor.GRAY),
                Component.literal(Integer.toString(breeding.childGrowthDelay()))
        ));
    }

    private static void buildFeedingContent(LinearViewLayout layout, BeeBreedData breeding) {
        List<ItemStack> feedItems = getFeedItems(breeding);

        if (!feedItems.isEmpty()) {
            layout.withChild(itemProperty(
                            Component.literal("Food").withColor(TextColor.GRAY),
                            new CyclingItemStackDisplayWidget(feedItems)
            ));
        }

        layout.withChild(property(
                        Component.literal("Feed Amount").withColor(TextColor.GRAY),
                        Component.literal(Integer.toString(breeding.feedAmount()))
        ));

        breeding.feedReturnItem().ifPresent(returnItem ->
                layout.withChild(itemProperty(
                                Component.literal("Returns").withColor(TextColor.GRAY),
                                new ItemStackDisplayWidget(returnItem.create())
                ))
        );
    }

    private static List<ItemStack> getFeedItems(BeeBreedData breeding) {
        return breeding.feedItems()
                .unwrap()
                .map(tag -> StreamSupport.stream(BuiltInRegistries.ITEM.getTagOrEmpty(tag).spliterator(), false)
                                .filter(Holder::isBound)
                                .map(Holder::value)
                                .map(ItemStack::new)
                                .toList(),

                        holders -> holders.stream()
                                .filter(Holder::isBound)
                                .map(Holder::value)
                                .map(ItemStack::new)
                                .toList()
                );
    }

    private static AbstractWidget itemProperty(Component label, AbstractWidget item) {
        return Widgets.labelled(Minecraft.getInstance().font, label, item);
    }

    private static AbstractWidget property(Component label, Component value) {
        return Widgets.labelled(Minecraft.getInstance().font, label, Widgets.text(value, TextWidget::withRightAlignment).withColor(Color.parse("green")));
    }
}