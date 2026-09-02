package com.teamresourceful.resourcefulbees.client.screen.locator;

import com.teamresourceful.resourcefulbees.api.data.bee.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.client.util.ClientRenderUtils;
import com.teamresourceful.resourcefulbees.common.items.locator.DimensionalBeeHolder;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;

import java.util.Comparator;
import java.util.function.Consumer;

public class BeeListWidget
        extends ObjectSelectionList<BeeLocatorEntry> {

    public static final Identifier BACKGROUND = ModIdentifier.of("textures/gui/advancements/backgrounds/resourcefulbees.png");

    private final Consumer<BeeLocatorEntry> selector;

    public BeeListWidget(Consumer<BeeLocatorEntry> selector, Minecraft minecraft, int width, int height, int top, int bottom, int entryHeight) {
        super(minecraft, width, bottom - top, top, entryHeight);
        this.selector = selector;
    }

    public void updateEntries(BeeRegistry registry) {
        var level = minecraft.level;

        if (level == null) {
            return;
        }

        clearEntries();

        var dimensionalBees = DimensionalBeeHolder.getBees(level.dimension());

        registry.getSetOfBees()
                .stream()
                .filter(bee -> dimensionalBees.contains(bee.id()))
                .sorted(Comparator.comparing(CustomBeeData::id))
                .forEach(bee -> {
                    Entity entity = bee.entityType().create(level, EntitySpawnReason.COMMAND);

                    if (entity == null) {
                        return;
                    }

                    ClientRenderUtils.preparePreviewEntity(entity);
                    addEntry(new BeeLocatorEntry(selector, entity, bee.displayName().copy()));
                });
    }
}