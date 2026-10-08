package com.teamresourceful.resourcefulbees.client.screen.beepedia.view.honey;

import com.teamresourceful.resourcefulbees.api.data.honey.CustomHoneyData;
import com.teamresourceful.resourcefulbees.api.data.honey.bottle.HoneyBottleEffectData;
import com.teamresourceful.resourcefulbees.api.data.honey.bottle.HoneyFoodData;
import com.teamresourceful.resourcefulbees.client.screen.beepedia.component.honey.HoneyEffectRow;
import com.teamresourceful.resourcefulbees.client.screen.beepedia.component.honey.HoneyFoodWidget;
import com.teamresourceful.resourcefulbees.client.screen.beepedia.component.honey.HoneyItemsWidget;
import com.teamresourceful.resourcefulbees.client.util.BeepediaScreenUtil;
import earth.terrarium.olympus.client.layouts.LinearViewLayout;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class HoneyOverview {

    private HoneyOverview() {
    }

    public static void build(LinearViewLayout layout, CustomHoneyData honey, int contentWidth) {
        layout.withGap(6);

        HoneyHeader.build(layout, honey);
        buildItems(layout, honey, contentWidth);
        buildFood(layout, honey);
        buildEffects(layout, honey, contentWidth);
        buildBlock(layout, honey);
        buildFluid(layout, honey);
    }

    private static void buildItems(LinearViewLayout layout, CustomHoneyData honey, int contentWidth) {
        layout.withChild(
                new HoneyItemsWidget(
                        new ItemStack(honey.getBottleData().bottle().get()),
                        new ItemStack(honey.getFluidData().fluidBucket().get()),
                        new ItemStack(honey.getBlockData().blockItem().get()),
                        contentWidth
                )
        );
    }

    private static void buildFood(LinearViewLayout layout, CustomHoneyData honey) {
        HoneyFoodData food = honey.getBottleData().food();

        layout.withChild(BeepediaScreenUtil.sectionTitle("Food"));
        layout.withChild(new HoneyFoodWidget(food));

        layout.withChild(BeepediaScreenUtil.property(Component.literal("Nutrition"), Integer.toString(food.nutrition())));
        layout.withChild(BeepediaScreenUtil.property(Component.literal("Saturation Modifier"), BeepediaScreenUtil.formatFloat(food.saturation())));
        layout.withChild(BeepediaScreenUtil.property(Component.literal("Always Edible"), food.canAlwaysEat() ? "Yes" : "No"));
        layout.withChild(BeepediaScreenUtil.property(Component.literal("Consume Time"), BeepediaScreenUtil.formatFloat(food.consumeSeconds()) + "s"));
    }

    private static void buildEffects(LinearViewLayout layout, CustomHoneyData honey, int contentWidth) {
        List<HoneyBottleEffectData> effects = honey.getBottleData().food().effects();

        if (effects.isEmpty()) {
            return;
        }

        layout.withChild(BeepediaScreenUtil.sectionTitle("Effects"));

        for (HoneyBottleEffectData effect : effects) {
            layout.withChild(new HoneyEffectRow(effect, contentWidth));
        }
    }

    private static void buildBlock(LinearViewLayout layout, CustomHoneyData honey) {
        var block = honey.getBlockData();

        layout.withChild(BeepediaScreenUtil.sectionTitle("Block"));
        layout.withChild(BeepediaScreenUtil.property(Component.literal("Speed Factor"), BeepediaScreenUtil.formatFloat(block.speedFactor())));
        layout.withChild(BeepediaScreenUtil.property(Component.literal("Jump Factor"), BeepediaScreenUtil.formatFloat(block.jumpFactor())));
    }

    private static void buildFluid(LinearViewLayout layout, CustomHoneyData honey) {
        var fluid = honey.getFluidData();
        var attributes = fluid.fluidAttributesData();

        layout.withChild(BeepediaScreenUtil.sectionTitle("Fluid"));

        layout.withChild(BeepediaScreenUtil.property(
                Component.literal("Density"),
                Integer.toString(attributes.density())
        ));

        layout.withChild(BeepediaScreenUtil.property(
                Component.literal("Viscosity"),
                Integer.toString(attributes.viscosity())
        ));

        layout.withChild(BeepediaScreenUtil.property(
                Component.literal("Temperature"),
                Integer.toString(attributes.temperature())
        ));

        layout.withChild(BeepediaScreenUtil.property(
                Component.literal("Light Level"),
                Integer.toString(attributes.lightLevel())
        ));

        layout.withChild(BeepediaScreenUtil.property(
                Component.literal("Motion Scale"),
                BeepediaScreenUtil.formatDouble(attributes.motionScale())
        ));

        layout.withChild(BeepediaScreenUtil.property(
                Component.literal("Fall Distance Modifier"),
                BeepediaScreenUtil.formatFloat(attributes.fallDistanceModifier())
        ));

        layout.withChild(BeepediaScreenUtil.property(
                Component.literal("Pushes Entities"),
                BeepediaScreenUtil.yesNo(attributes.canPushEntities())
        ));

        layout.withChild(BeepediaScreenUtil.property(
                Component.literal("Swimmable"),
                BeepediaScreenUtil.yesNo(attributes.canSwimIn())
        ));

        layout.withChild(BeepediaScreenUtil.property(
                Component.literal("Can Drown"),
                BeepediaScreenUtil.yesNo(attributes.canDrownIn())
        ));

        layout.withChild(BeepediaScreenUtil.property(
                Component.literal("Extinguishes"),
                BeepediaScreenUtil.yesNo(attributes.canExtinguish())
        ));

        layout.withChild(BeepediaScreenUtil.property(
                Component.literal("Infinite Sources"),
                BeepediaScreenUtil.yesNo(attributes.canConvertToSource())
        ));

        layout.withChild(BeepediaScreenUtil.property(
                Component.literal("Supports Boats"),
                BeepediaScreenUtil.yesNo(attributes.supportsBoating())
        ));

        layout.withChild(BeepediaScreenUtil.property(
                Component.literal("Hydrates Farmland"),
                BeepediaScreenUtil.yesNo(attributes.canHydrate())
        ));
    }

}
