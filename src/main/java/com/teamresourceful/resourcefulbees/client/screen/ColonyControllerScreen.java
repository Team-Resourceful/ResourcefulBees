package com.teamresourceful.resourcefulbees.client.screen;

import com.teamresourceful.resourcefulbees.common.logistics.ColonyCourierSnapshot;
import com.teamresourceful.resourcefulbees.common.logistics.ColonyRouteSnapshot;
import com.teamresourceful.resourcefulbees.common.logistics.CourierRouteEndpointEdit;
import com.teamresourceful.resourcefulbees.common.logistics.CourierRouteExecutionMode;
import com.teamresourceful.resourcefulbees.common.menus.ColonyControllerMenu;
import com.teamresourceful.resourcefulbees.common.networking.NetworkHandler;
import com.teamresourceful.resourcefulbees.common.networking.packets.client.BeginCourierRouteEditPacket;
import com.teamresourceful.resourcefulbees.common.networking.packets.client.SetColonyControllerTabPacket;
import com.teamresourceful.resourcefulbees.common.networking.packets.client.SetCourierSchedulingPacket;
import com.teamresourceful.resourcefulbees.common.networking.packets.client.UpdateColonyRoutePacket;
import earth.terrarium.olympus.client.components.Widgets;
import earth.terrarium.olympus.client.components.compound.LayoutWidget;
import earth.terrarium.olympus.client.components.compound.radio.RadioState;
import earth.terrarium.olympus.client.components.dropdown.DropdownState;
import earth.terrarium.olympus.client.components.renderers.WidgetRenderers;
import earth.terrarium.olympus.client.layouts.LinearViewLayout;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.TriState;
import net.minecraft.world.entity.player.Inventory;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ColonyControllerScreen extends AbstractContainerScreen<ColonyControllerMenu> {

    private static final int GUI_WIDTH = 256;
    private static final int GUI_HEIGHT = 300;

    private static final int CONTENT_WIDTH = 224;
    private static final int CONTENT_HEIGHT = 220;

    private static final int ROUTE_SELECTOR_HEIGHT = 42;
    private static final int ROUTE_DETAILS_HEIGHT = 174;

    private static final int PANEL_X = 16;
    private static final int PANEL_Y = 50;

    private static final UUID NO_PREFERRED_ROUTE = new UUID(0L, 0L);
    private final RadioState<Tab> selectedTab = RadioState.ofEnum(Tab.OVERVIEW);
    private final DropdownState<UUID> courierSelection = DropdownState.of(null);
    private final DropdownState<UUID> routeSelection = DropdownState.of(null);
    private final DropdownState<UUID> preferredRouteSelection = DropdownState.of(NO_PREFERRED_ROUTE);

    private int lastDataHash = Integer.MIN_VALUE;
    private LayoutWidget<LinearViewLayout> content;
    private LayoutWidget<LinearViewLayout> routeDetails;
    private LayoutWidget<LinearViewLayout> routeSelector;
    private LayoutWidget<LinearViewLayout> courierSelector;
    private LayoutWidget<LinearViewLayout> courierDetails;
    private UUID pendingRouteDeletion;

    private enum Tab {
        OVERVIEW, ROUTES, COURIERS, UPGRADES
    }

    public ColonyControllerScreen(ColonyControllerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, GUI_WIDTH, GUI_HEIGHT);

        this.titleLabelX = 12;
        this.titleLabelY = 8;

        this.inventoryLabelX = 47;
        this.inventoryLabelY = 205;
    }

    @Override
    protected void init() {
        super.init();

        AbstractWidget tabs = createTabs();
        tabs.setPosition(leftPos + PANEL_X, topPos + 25);
        addRenderableWidget(tabs);

        LayoutWidget<LinearViewLayout> mainContent = createContent();
        mainContent.setPosition(leftPos + PANEL_X, topPos + PANEL_Y);
        addRenderableWidget(mainContent);

        LayoutWidget<LinearViewLayout> selector = createRouteSelector();
        selector.setPosition(leftPos + PANEL_X, topPos + PANEL_Y);
        addRenderableWidget(selector);

        LayoutWidget<LinearViewLayout> details = createRouteDetails();
        details.setPosition(leftPos + PANEL_X, topPos + PANEL_Y + ROUTE_SELECTOR_HEIGHT + 4);
        addRenderableWidget(details);

        LayoutWidget<LinearViewLayout> courierSelectorWidget =
                createCourierSelector();

        courierSelectorWidget.setPosition(
                leftPos + PANEL_X,
                topPos + PANEL_Y
        );

        addRenderableWidget(courierSelectorWidget);

        LayoutWidget<LinearViewLayout> courierDetailsWidget =
                createCourierDetails();

        courierDetailsWidget.setPosition(
                leftPos + PANEL_X,
                topPos + PANEL_Y + ROUTE_SELECTOR_HEIGHT + 4
        );

        addRenderableWidget(courierDetailsWidget);

        updatePanelVisibility();
    }

    @Override
    protected void containerTick() {
        super.containerTick();

        int hash = Objects.hash(
                menu.getOperatingRadius(),
                menu.getMaxRoutes(),
                menu.getMaxBees(),
                menu.getRegisteredCourierCount(),
                menu.getRouteCount(),
                menu.getStoredBeeCount(),
                menu.getRouteSnapshots(),
                menu.getCourierSnapshots()
        );

        if (hash != lastDataHash) {
            lastDataHash = hash;
            rebuildContent();
            rebuildRouteSelector();
            rebuildRouteDetails();
            rebuildCourierSelector();
            rebuildCourierDetails();
        }
    }

    private void onTabChanged() {
        Tab tab = selectedTab.get();

        menu.setActiveTab(tab.ordinal());

        NetworkHandler.NETWORK.sendToServer(new SetColonyControllerTabPacket(tab.ordinal()));

        rebuildContent();
        updatePanelVisibility();
    }

    private AbstractWidget createTabs() {
        return Widgets.radio(selectedTab, radio -> radio.withOptions(List.of(Tab.values())).withSize(CONTENT_WIDTH, 20).withGap(2).withRenderer((tab, _) -> WidgetRenderers.text(tabName(tab))).withCallback(_ -> onTabChanged()), _ -> {
        });
    }

    private Component tabName(Tab tab) {
        return switch (tab) {
            case OVERVIEW -> Component.literal("Overview");
            case ROUTES -> Component.literal("Routes");
            case COURIERS -> Component.literal("Couriers");
            case UPGRADES -> Component.literal("Upgrades");
        };
    }

    private LayoutWidget<LinearViewLayout> createContent() {
        content = Widgets.list(widget -> {
            widget.withSize(CONTENT_WIDTH, CONTENT_HEIGHT);
            widget.withScrollableY(TriState.DEFAULT);
            widget.withContentFillWidth();
        });

        rebuildContent();
        return content;
    }

    private void rebuildContent() {
        if (content == null) {
            return;
        }

        content.withContents(layout -> {
            layout.removeChildren();
            layout.withGap(3);

            switch (selectedTab.get()) {
                case OVERVIEW -> buildOverview(layout);
                case ROUTES -> {
                    // Routes use dedicated widgets.
                }
                case COURIERS -> {
                    // Couriers use dedicated selector and details widgets.
                }
                case UPGRADES -> buildUpgrades(layout);
            }
        });
    }

    private void buildOverview(LinearViewLayout layout) {
        layout.withChild(Widgets.text("Operating radius: " + menu.getOperatingRadius() + " blocks"));

        layout.withChild(Widgets.text("Routes: " + menu.getRouteCount() + " / " + menu.getMaxRoutes()));

        layout.withChild(Widgets.text("Registered couriers: " + menu.getRegisteredCourierCount() + " / " + menu.getMaxBees()));

        layout.withChild(Widgets.text("Stored bees: " + menu.getStoredBeeCount()));
    }


    private LayoutWidget<LinearViewLayout> createCourierDetails() {
        courierDetails = Widgets.list(widget -> {
            widget.withSize(CONTENT_WIDTH, ROUTE_DETAILS_HEIGHT);
            widget.withScrollableY(TriState.DEFAULT);
            widget.withContentFillWidth();
        });

        rebuildCourierDetails();

        return courierDetails;
    }


    private void rebuildCourierDetails() {
        if (courierDetails == null) {
            return;
        }

        courierDetails.withContents(layout -> {
            layout.removeChildren();
            layout.withGap(3);

            ColonyCourierSnapshot courier = getSelectedCourier();

            if (courier == null) {
                layout.withChild(Widgets.text("Select a courier."));
                return;
            }

            layout.withChild(Widgets.text(
                    "Courier: " + courier.name()
            ));

            layout.withChild(Widgets.text(
                    "Location: " + formatCourierText(courier.location().name())
            ));

            layout.withChild(Widgets.text(
                    "State: " + formatCourierText(courier.deliveryState())
            ));

            layout.withChild(Widgets.text(
                    "Assigned route: " + getAssignedRouteLabel(courier.routeId())
            ));

            layout.withChild(Widgets.text(
                    "Cargo: " + formatCourierCargo(courier)
            ));

            layout.withChild(Widgets.text(
                    "Scheduling: " + (courier.manualMode()
                            ? "Manual"
                            : "Automatic")
            ));

            layout.withChild(Widgets.button()
                    .withSize(CONTENT_WIDTH - 8, 20)
                    .withRenderer(WidgetRenderers.text(
                            Component.literal(courier.manualMode()
                                    ? "Switch to Automatic"
                                    : "Switch to Manual")
                    ))
                    .withCallback(() -> toggleCourierScheduling(courier.id()))
            );

            if (courier.manualMode()) {
                layout.withChild(Widgets.text("Preferred route:"));
                layout.withChild(createPreferredRouteDropdown(courier));
            }
        });
    }


    private void toggleCourierScheduling(UUID courierId) {
        ColonyCourierSnapshot courier = menu.getCourierSnapshots()
                .stream()
                .filter(snapshot -> snapshot.id().equals(courierId))
                .findFirst()
                .orElse(null);

        if (courier == null) {
            return;
        }

        boolean manual = !courier.manualMode();

        NetworkHandler.NETWORK.sendToServer(
                new SetCourierSchedulingPacket(
                        courier.id(),
                        manual,
                        manual ? courier.preferredRouteId() : null
                )
        );
    }



    private AbstractWidget createPreferredRouteDropdown(ColonyCourierSnapshot courier) {
        UUID preferredId = courier.preferredRouteId();

        preferredRouteSelection.set(
                preferredId == null
                        ? NO_PREFERRED_ROUTE
                        : preferredId
        );

        List<UUID> routeIds = new java.util.ArrayList<>();
        routeIds.add(NO_PREFERRED_ROUTE);

        // Preserve an existing preference even if its route
        // has since been deleted.
        if (preferredId != null
                && menu.getRouteSnapshots().stream()
                .noneMatch(route ->
                        route.id().equals(preferredId)
                                && route.enabled()
                                && route.executionMode()
                                == CourierRouteExecutionMode.COURIER)) {
            routeIds.add(preferredId);
        }

        for (ColonyRouteSnapshot route : menu.getRouteSnapshots()) {
            if (route.enabled() && route.executionMode() == CourierRouteExecutionMode.COURIER) {
                routeIds.add(route.id());
            }
        }

        return Widgets.dropdown(
                preferredRouteSelection,
                routeIds,

                id -> {
                    if (NO_PREFERRED_ROUTE.equals(id)) {
                        return Component.literal("None (Idle)");
                    }

                    for (ColonyRouteSnapshot route : menu.getRouteSnapshots()) {
                        if (route.id().equals(id)) {
                            String suffix = !route.enabled()
                                    ? " [Disabled]"
                                    : route.executionMode()
                                    != CourierRouteExecutionMode.COURIER
                                    ? " [Not Courier]"
                                    : "";

                            return Component.literal(
                                    getAssignedRouteLabel(id) + suffix
                            );
                        }
                    }

                    return Component.literal("Deleted route");
                }
                ,
                button -> button.withSize(CONTENT_WIDTH - 8, 20),
                dropdown -> dropdown
                        .withSize(CONTENT_WIDTH - 8, 100)
                        .withCallback(_ -> {
                            UUID selected = preferredRouteSelection.get();

                            if (selected == null) {
                                return;
                            }

                            UUID routeId = NO_PREFERRED_ROUTE.equals(selected)
                                    ? null
                                    : selected;

                            if (Objects.equals(
                                    routeId,
                                    courier.preferredRouteId()
                            )) {
                                return;
                            }

                            NetworkHandler.NETWORK.sendToServer(
                                    new SetCourierSchedulingPacket(
                                            courier.id(),
                                            true,
                                            routeId
                                    )
                            );
                        })
        );
    }


    private ColonyCourierSnapshot getSelectedCourier() {
        UUID selectedId = courierSelection.get();

        if (selectedId == null) {
            return null;
        }

        return menu.getCourierSnapshots().stream()
                .filter(courier -> courier.id().equals(selectedId))
                .findFirst()
                .orElse(null);
    }

    private String getAssignedRouteLabel(UUID routeId) {
        if (routeId == null) {
            return "Unassigned";
        }

        var routes = menu.getRouteSnapshots();

        for (int i = 0; i < routes.size(); i++) {
            if (routes.get(i).id().equals(routeId)) {
                return "Route " + (i + 1);
            }
        }

        return "Unknown route";
    }

    private static String formatCourierCargo(ColonyCourierSnapshot courier) {
        if (courier.cargoAmount() < 0) {
            return "Unknown";
        }

        if (courier.cargoAmount() == 0) {
            return "Empty";
        }

        return courier.cargoAmount() + " " + courier.cargoName();
    }

    private static String formatCourierText(String value) {
        String[] words = value.toLowerCase(java.util.Locale.ROOT)
                .split("_");

        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (word.isEmpty()) {
                continue;
            }

            if (!result.isEmpty()) {
                result.append(' ');
            }

            result.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1));
        }

        return result.toString();
    }

    private LayoutWidget<LinearViewLayout> createCourierSelector() {
        courierSelector = Widgets.list(widget -> {
            widget.withSize(CONTENT_WIDTH, ROUTE_SELECTOR_HEIGHT);
            widget.withContentFillWidth();
        });

        rebuildCourierSelector();

        return courierSelector;
    }

    private void rebuildCourierSelector() {
        if (courierSelector == null) {
            return;
        }

        validateCourierSelection();

        courierSelector.withContents(layout -> {
            layout.removeChildren();
            layout.withGap(3);

            var couriers = menu.getCourierSnapshots();

            layout.withChild(Widgets.text(
                    "Couriers: " + couriers.size() + " / " + menu.getMaxBees()
            ));

            if (couriers.isEmpty()) {
                layout.withChild(Widgets.text("No couriers registered."));
                return;
            }

            layout.withChild(createCourierDropdown());
        });
    }

    private AbstractWidget createCourierDropdown() {
        var courierIds = menu.getCourierSnapshots().stream()
                .map(ColonyCourierSnapshot::id)
                .toList();

        return Widgets.dropdown(
                courierSelection,
                courierIds,
                this::getCourierLabel,
                button -> button.withSize(CONTENT_WIDTH - 8, 20),
                dropdown -> dropdown
                        .withSize(CONTENT_WIDTH - 8, 100)
                        .withCallback(_ -> rebuildCourierDetails())
        );
    }

    private Component getCourierLabel(UUID courierId) {
        for (ColonyCourierSnapshot courier : menu.getCourierSnapshots()) {
            if (courier.id().equals(courierId)) {
                return Component.literal(
                        courier.name() + " [" + courier.location().name() + "]"
                );
            }
        }

        return Component.literal("Unknown courier");
    }

    private void validateCourierSelection() {
        var couriers = menu.getCourierSnapshots();
        UUID selectedId = courierSelection.get();

        if (couriers.isEmpty()) {
            if (selectedId != null) {
                courierSelection.set(null);
            }
            return;
        }

        boolean valid = couriers.stream()
                .anyMatch(courier -> courier.id().equals(selectedId));

        if (!valid) {
            courierSelection.set(couriers.getFirst().id());
        }
    }


    private AbstractWidget createRouteDropdown() {
        var routes = menu.getRouteSnapshots();

        var routeIds = routes.stream().map(ColonyRouteSnapshot::id).toList();

        return Widgets.dropdown(routeSelection, routeIds, this::getRouteLabel, button -> button.withSize(CONTENT_WIDTH - 8, 20), dropdown -> dropdown.withSize(CONTENT_WIDTH - 8, 100).withCallback(_ -> {
            pendingRouteDeletion = null;
            rebuildRouteDetails();
        }));
    }

    private Component getRouteLabel(UUID routeId) {
        var routes = menu.getRouteSnapshots();

        for (int i = 0; i < routes.size(); i++) {
            var route = routes.get(i);

            if (route.id().equals(routeId)) {
                return Component.literal("Route " + (i + 1) + " - " + route.resourceType() + (route.enabled() ? "" : " [Disabled]"));
            }
        }

        return Component.literal("Unknown route");
    }

    private LayoutWidget<LinearViewLayout> createRouteDetails() {
        routeDetails = Widgets.list(widget -> {
            widget.withSize(CONTENT_WIDTH, ROUTE_DETAILS_HEIGHT);
            widget.withScrollableY(TriState.DEFAULT);
            widget.withContentFillWidth();
        });

        rebuildRouteDetails();

        return routeDetails;
    }

    private void rebuildRouteDetails() {
        if (routeDetails == null) {
            return;
        }

        routeDetails.withContents(layout -> {
            layout.removeChildren();

            ColonyRouteSnapshot selected = getSelectedRoute();

            if (selected == null) {
                layout.withChild(Widgets.text("Select a route."));
                return;
            }

            buildRouteDetails(layout, selected);
        });
    }

    private void buildRouteDetails(LinearViewLayout layout, ColonyRouteSnapshot route) {
        layout.withGap(3);

        layout.withChild(Widgets.text("Selected route"));
        layout.withChild(Widgets.text("Status: " + (route.enabled() ? "Enabled" : "Disabled")));
        layout.withChild(Widgets.text("Resource: " + route.resourceType()));
        layout.withChild(Widgets.text("Priority: " + route.priority()));
        layout.withChild(Widgets.text("Source: " + formatPosition(route.source())));
        layout.withChild(Widgets.text("Destination: " + formatPosition(route.destination())));

        layout.withChild(Widgets.button().withSize(CONTENT_WIDTH - 8, 20)
                .withRenderer(WidgetRenderers.text(Component.literal("Edit Source")))
                .withCallback(() -> beginRouteEndpointEdit(CourierRouteEndpointEdit.EndpointType.SOURCE)));

        layout.withChild(Widgets.button().withSize(CONTENT_WIDTH - 8, 20)
                .withRenderer(WidgetRenderers.text(Component.literal("Edit Destination")))
                .withCallback(() -> beginRouteEndpointEdit(CourierRouteEndpointEdit.EndpointType.DESTINATION)));

        layout.withChild(Widgets.button().withSize(CONTENT_WIDTH - 8, 20)
                .withRenderer(WidgetRenderers.text(Component.literal(route.enabled() ? "Disable Route" : "Enable Route")))
                .withCallback(this::toggleSelectedRoute));

        layout.withChild(Widgets.button().withSize(CONTENT_WIDTH - 8, 18)
                .withRenderer(WidgetRenderers.text(Component.literal("Priority +1")))
                .withCallback(() -> changeSelectedRoutePriority(1)));

        layout.withChild(Widgets.button().withSize(CONTENT_WIDTH - 8, 18)
                .withRenderer(WidgetRenderers.text(Component.literal("Priority -1")))
                .withCallback(() -> changeSelectedRoutePriority(-1)));

        if (route.id().equals(pendingRouteDeletion)) {
            layout.withChild(Widgets.text("Delete this route? This cannot be undone."));

            layout.withChild(Widgets.button().withSize(CONTENT_WIDTH - 8, 20)
                    .withRenderer(WidgetRenderers.text(Component.literal("Confirm Delete")))
                    .withCallback(this::confirmRouteDeletion));

            layout.withChild(Widgets.button().withSize(CONTENT_WIDTH - 8, 20)
                    .withRenderer(WidgetRenderers.text(Component.literal("Cancel")))
                    .withCallback(this::cancelRouteDeletion));
        } else {
            layout.withChild(Widgets.button().withSize(CONTENT_WIDTH - 8, 20)
                    .withRenderer(WidgetRenderers.text(Component.literal("Delete Route")))
                    .withCallback(this::requestRouteDeletion));
        }
    }


    private void buildUpgrades(LinearViewLayout layout) {
        layout.withChild(Widgets.text("Radius: " + menu.getOperatingRadius() + " blocks"));

        layout.withChild(Widgets.text("Routes: " + menu.getMaxRoutes()));

        layout.withChild(Widgets.text("Bees: " + menu.getMaxBees()));
    }

    @Override
    public void extractBackground(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);

        // Temporary background.
        // Replace with a custom sprite when the UI layout is finalized.
        graphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xFF20242C);

        // Upgrade slot backgrounds.
        if (selectedTab.get() == Tab.UPGRADES) {
            for (int x : new int[]{62, 119, 176}) {
                graphics.fill(leftPos + x - 1, topPos + 182, leftPos + x + 17, topPos + 200, 0xFF555B68);
            }
        }
    }

    @Override
    protected void extractLabels(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if (selectedTab.get() == Tab.UPGRADES) {
            super.extractLabels(graphics, mouseX, mouseY);
        }
    }

    private LayoutWidget<LinearViewLayout> createRouteSelector() {
        routeSelector = Widgets.list(widget -> {
            widget.withSize(CONTENT_WIDTH, ROUTE_SELECTOR_HEIGHT);
            widget.withContentFillWidth();
        });

        rebuildRouteSelector();

        return routeSelector;
    }

    private void rebuildRouteSelector() {
        if (routeSelector == null) {
            return;
        }

        validateRouteSelection();

        routeSelector.withContents(layout -> {
            layout.removeChildren();
            layout.withGap(3);

            var routes = menu.getRouteSnapshots();

            layout.withChild(Widgets.text("Routes: " + routes.size() + " / " + menu.getMaxRoutes()));

            if (routes.isEmpty()) {
                layout.withChild(Widgets.text("No routes configured."));
                return;
            }

            layout.withChild(createRouteDropdown());
        });
    }

    private void beginRouteEndpointEdit(CourierRouteEndpointEdit.EndpointType endpointType) {
        ColonyRouteSnapshot route = getSelectedRoute();

        if (route == null) {
            return;
        }

        NetworkHandler.NETWORK.sendToServer(new BeginCourierRouteEditPacket(menu.getEntity().getBlockPos(), route.id(), endpointType));

        onClose();
    }

    private void toggleSelectedRoute() {
        ColonyRouteSnapshot route = getSelectedRoute();

        if (route == null) {
            return;
        }

        NetworkHandler.NETWORK.sendToServer(UpdateColonyRoutePacket.enabled(menu.getEntity().getBlockPos(), route.id(), !route.enabled()));
    }

    private void changeSelectedRoutePriority(int delta) {
        ColonyRouteSnapshot route = getSelectedRoute();

        if (route == null) {
            return;
        }

        int priority = Math.clamp(route.priority() + (long) delta, 0, 100);

        if (priority == route.priority()) {
            return;
        }

        NetworkHandler.NETWORK.sendToServer(UpdateColonyRoutePacket.priority(menu.getEntity().getBlockPos(), route.id(), priority));
    }

    private ColonyRouteSnapshot getSelectedRoute() {
        UUID selectedId = routeSelection.get();

        if (selectedId == null) {
            return null;
        }

        return menu.getRouteSnapshots().stream().filter(route -> route.id().equals(selectedId)).findFirst().orElse(null);
    }

    private void requestRouteDeletion() {
        ColonyRouteSnapshot route = getSelectedRoute();

        if (route == null) {
            return;
        }

        pendingRouteDeletion = route.id();
        rebuildRouteDetails();
    }

    private void cancelRouteDeletion() {
        pendingRouteDeletion = null;
        rebuildRouteDetails();
    }

    private void confirmRouteDeletion() {
        ColonyRouteSnapshot route = getSelectedRoute();

        if (route == null || !route.id().equals(pendingRouteDeletion)) {
            cancelRouteDeletion();
            return;
        }

        NetworkHandler.NETWORK.sendToServer(UpdateColonyRoutePacket.delete(menu.getEntity().getBlockPos(), route.id()));

        pendingRouteDeletion = null;
        rebuildRouteDetails();
    }

    private void validateRouteSelection() {
        var routes = menu.getRouteSnapshots();
        UUID selectedId = routeSelection.get();

        if (routes.isEmpty()) {
            if (selectedId != null) {
                routeSelection.set(null);
            }
            pendingRouteDeletion = null;
            return;
        }

        boolean valid = routes.stream().anyMatch(route -> route.id().equals(selectedId));

        if (!valid) {
            routeSelection.set(routes.getFirst().id());
            pendingRouteDeletion = null;
        }

        if (pendingRouteDeletion != null && routes.stream().noneMatch(route -> route.id().equals(pendingRouteDeletion))) {
            pendingRouteDeletion = null;
        }
    }


    private void updatePanelVisibility() {
        boolean routesTab = selectedTab.get() == Tab.ROUTES;
        boolean couriersTab = selectedTab.get() == Tab.COURIERS;

        content.visible = !routesTab && !couriersTab;
        content.active = !routesTab && !couriersTab;

        routeSelector.visible = routesTab;
        routeSelector.active = routesTab;

        routeDetails.visible = routesTab;
        routeDetails.active = routesTab;

        courierSelector.visible = couriersTab;
        courierSelector.active = couriersTab;

        courierDetails.visible = couriersTab;
        courierDetails.active = couriersTab;
    }


    private static String formatPosition(BlockPos pos) {
        return pos.getX() + ", " + pos.getY() + ", " + pos.getZ();
    }
}