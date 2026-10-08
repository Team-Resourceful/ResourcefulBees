package com.teamresourceful.resourcefulbees.client.screen.beepedia.view.bee;

import com.teamresourceful.resourcefulbees.api.data.bee.CustomBeeData;
import com.teamresourceful.resourcefulbees.client.util.BeepediaScreenUtil;
import com.teamresourceful.resourcefulbees.common.resources.storage.beepedia.DiscoveredBee;
import earth.terrarium.olympus.client.components.Widgets;
import earth.terrarium.olympus.client.layouts.LinearViewLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public final class BeeOverview {

    private BeeOverview() {
    }

    public static void build(LinearViewLayout layout, CustomBeeData bee, DiscoveredBee discovered, int contentWidth) {
        var core = bee.getCoreData();
        var combat = bee.getCombatData();

        layout.withGap(5);

        for (Component lore : core.lore()) {
            layout.withChild(Widgets.textarea(lore, contentWidth));
        }

        Instant instant = Instant.ofEpochSecond(discovered.discoveredAt());

        String discoveredAt = DateTimeFormatter
                .ofLocalizedDateTime(FormatStyle.MEDIUM)
                .withZone(ZoneId.systemDefault())
                .format(instant);

        LinearLayout discovery = LinearLayout.vertical();

        discovery.addChild(BeepediaScreenUtil.property(
                Component.literal("Discovered"),
                Component.literal(discoveredAt)), settings -> settings.paddingTop(4).paddingBottom(4)
        );

        layout.withChild(discovery);

        layout.withChild(BeepediaScreenUtil.sectionTitle("General"));

        layout.withChild(BeepediaScreenUtil.property(
                Component.literal("Max Time In Hive").withColor(TextColor.GRAY),
                Component.literal(Integer.toString(core.maxTimeInHive()))
        ));

        layout.withChild(BeepediaScreenUtil.property(
                Component.literal("Passive").withColor(TextColor.GRAY),
                BeepediaScreenUtil.yesNo(combat.isPassive())
        ));

        layout.withChild(BeepediaScreenUtil.property(
                Component.literal("Removes Stinger").withColor(TextColor.GRAY),
                BeepediaScreenUtil.yesNo(combat.removeStingerOnAttack())
        ));

        layout.withChild(BeepediaScreenUtil.property(
                Component.literal("Poison").withColor(TextColor.GRAY),
                BeepediaScreenUtil.yesNo(combat.inflictsPoison())
        ));

        layout.withChild(BeepediaScreenUtil.property(
                Component.literal("Invulnerable").withColor(TextColor.GRAY),
                BeepediaScreenUtil.yesNo(combat.isInvulnerable())
        ));
    }

}