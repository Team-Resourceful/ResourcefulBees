package com.teamresourceful.resourcefulbees.client.screen.beepedia.view.bee;

import com.teamresourceful.resourcefulbees.api.data.bee.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.data.honeycomb.OutputVariation;
import com.teamresourceful.resourcefulbees.api.tiers.ApiaryTier;
import com.teamresourceful.resourcefulbees.api.tiers.BeehiveTier;
import com.teamresourceful.resourcefulbees.client.screen.beepedia.component.bee.BeeProductionRow;
import com.teamresourceful.resourcefulbees.client.util.BeepediaScreenUtil;
import earth.terrarium.olympus.client.components.Widgets;
import earth.terrarium.olympus.client.layouts.LinearViewLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;

public final class BeeProduction {

    private BeeProduction() {
    }

    public static void build(LinearViewLayout layout, CustomBeeData bee, int contentWidth) {
        layout.withGap(5);

        bee.getCoreData().getHoneycombData().ifPresentOrElse(output -> buildProduction(layout, output, contentWidth), () -> layout.withChild(Widgets.text(Component.literal("No production data."))));
    }

    private static void buildProduction(LinearViewLayout layout, OutputVariation output, int contentWidth) {
        int gap = 8;
        int columnWidth = (contentWidth - gap) / 2;

        LinearLayout columns = LinearLayout.horizontal().spacing(gap);
        LinearLayout hives = LinearLayout.vertical().spacing(5);
        LinearLayout apiaries = LinearLayout.vertical().spacing(5);

        hives.addChild(BeepediaScreenUtil.sectionTitle("Beehives"));

        for (BeehiveTier tier : BeehiveTier.values()) {
            hives.addChild(BeeProductionRow.hive(tier, output.getHiveOutput(tier), columnWidth));
        }

        apiaries.addChild(BeepediaScreenUtil.sectionTitle("Apiaries"));

        for (ApiaryTier tier : ApiaryTier.values()) {
            apiaries.addChild(BeeProductionRow.apiary(tier, output.getApiaryOutput(tier), columnWidth));
        }

        columns.addChild(hives);
        columns.addChild(apiaries);

        columns.arrangeElements();

        layout.withChild(columns);
    }

}