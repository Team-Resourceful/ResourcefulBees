package com.teamresourceful.resourcefulbees.client.screen.beepedia.view.bee;

import com.teamresourceful.resourcefulbees.api.data.bee.CustomBeeData;
import com.teamresourceful.resourcefulbees.client.screen.beepedia.component.bee.BeePreviewWidget;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.BeepediaTranslations;
import com.teamresourceful.resourcefulbees.common.modcompat.jei.JEICompat;
import com.teamresourceful.resourcefullib.common.color.Color;
import com.teamresourceful.resourcefullib.common.utils.modinfo.ModInfoUtils;
import earth.terrarium.olympus.client.components.Widgets;
import earth.terrarium.olympus.client.components.string.TextWidget;
import earth.terrarium.olympus.client.layouts.LinearViewLayout;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;

public final class BeeHeader {

    private static final int PREVIEW_SIZE = 40;
    private static final int JEI_BUTTON_SIZE = 20;

    private BeeHeader() {
    }

    public static void build(LinearViewLayout layout, CustomBeeData bee, int width) {
        layout.withChild(Widgets.frame(frame ->
                        frame.withSize(width, PREVIEW_SIZE).withContents(contents -> {
                            LinearLayout header = LinearLayout.horizontal().spacing(6);

                            header.addChild(new BeePreviewWidget(bee, PREVIEW_SIZE, PREVIEW_SIZE));

                            LinearLayout identity = LinearLayout.vertical().spacing(1);

                            identity.addChild(Widgets.text(bee.displayName(), text -> text
                                                    .withColor(Color.DEFAULT)
                                                    .withShadow()
                                                    .withLeftAlignment()
                            ));

                            identity.addChild(Widgets.text(Component.literal(bee.id().toString()), TextWidget::withLeftAlignment));
                            header.addChild(identity, LayoutSettings::alignVerticallyMiddle);
                            contents.addChild(header, LayoutSettings::alignHorizontallyLeft);

                            if (ModInfoUtils.isModLoaded("jei")) {
                                contents.addChild(createJeiButton(bee), settings -> settings
                                                .alignHorizontallyRight()
                                                .alignVerticallyMiddle()
                                );
                            }
                        }).withWidthCallback((widget, contents) -> contents.setMinWidth(widget.getViewWidth())))
        );
    }

    private static AbstractWidget createJeiButton(CustomBeeData bee) {
        return Widgets.button().withSize(JEI_BUTTON_SIZE, JEI_BUTTON_SIZE)
                .withTexture(null)
                .withRenderer((graphics, widget, _) -> graphics.fakeItem(Items.KNOWLEDGE_BOOK.getDefaultInstance(), widget.getX(), widget.getY()))
                .withCallback(() -> JEICompat.searchEntity(bee.entityType()))
                .withTooltip(BeepediaTranslations.OPEN_JEI);
    }
}