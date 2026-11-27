package com.teamresourceful.resourcefulbees.client.screen;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.logistics.CourierEndpoint;
import com.teamresourceful.resourcefulbees.common.logistics.CourierResourceType;
import com.teamresourceful.resourcefulbees.common.networking.NetworkHandler;
import com.teamresourceful.resourcefulbees.common.networking.packets.client.ConfirmCourierRoutePacket;
import earth.terrarium.olympus.client.components.Widgets;
import earth.terrarium.olympus.client.components.renderers.WidgetRenderers;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import org.jspecify.annotations.NonNull;

import java.util.EnumSet;
import java.util.Set;

public final class HoneyDipperRouteTypeScreen extends Screen {

    private static final int PANEL_WIDTH = 220;
    private static final int PANEL_HEIGHT = 130;

    private static final Identifier BACKGROUND = ModIdentifier.of("beepedia/background");

    private final CourierEndpoint destination;
    private final Set<CourierResourceType> available;

    private int panelX;
    private int panelY;

    private final InteractionHand hand;

    public HoneyDipperRouteTypeScreen(CourierEndpoint destination, EnumSet<CourierResourceType> available, InteractionHand hand) {
        super(Component.translatable("items.resourcefulbees.honey_dipper.select_route_type"));

        this.destination = destination;
        this.available = available.clone();
        this.hand = hand;
    }

    @Override
    protected void init() {
        super.init();

        panelX = (width - PANEL_WIDTH) / 2;
        panelY = (height - PANEL_HEIGHT) / 2;

        LinearLayout layout = LinearLayout.vertical().spacing(5);

        layout.addChild(Widgets.text(Component.translatable("items.resourcefulbees.honey_dipper.select_route_type")));

        for (CourierResourceType type : CourierResourceType.values()) {
            if (!available.contains(type)) {
                continue;
            }

            layout.addChild(Widgets.button()
                    .withSize(PANEL_WIDTH - 24, 20)
                    .withRenderer(WidgetRenderers.text(Component.literal(type.name())))
                    .withCallback(() -> select(type))
            );
        }

        layout.arrangeElements();
        layout.setPosition(panelX + 12, panelY + 12);
        layout.visitWidgets(this::addRenderableWidget);
    }

    private void select(CourierResourceType type) {
        NetworkHandler.NETWORK.sendToServer(new ConfirmCourierRoutePacket(destination, type, hand));

        onClose();
    }

    @Override
    public void extractBackground(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND, panelX, panelY, PANEL_WIDTH, PANEL_HEIGHT);
    }
}
