package com.teamresourceful.resourcefulbees.common.menus;

import com.teamresourceful.resourcefulbees.common.blockentities.ColonyControllerBlockEntity;
import com.teamresourceful.resourcefulbees.common.components.Upgrade;
import com.teamresourceful.resourcefulbees.common.lib.constants.ColonyConstants;
import com.teamresourceful.resourcefulbees.common.logistics.ColonyCourierSnapshot;
import com.teamresourceful.resourcefulbees.common.logistics.ColonyRouteSnapshot;
import com.teamresourceful.resourcefulbees.common.logistics.CourierRoute;
import com.teamresourceful.resourcefulbees.common.menus.base.AbstractModContainerMenu;
import com.teamresourceful.resourcefulbees.common.menus.content.ColonyControllerData;
import com.teamresourceful.resourcefulbees.common.menus.content.PositionContent;
import com.teamresourceful.resourcefulbees.common.networking.NetworkHandler;
import com.teamresourceful.resourcefulbees.common.networking.packets.server.SyncColonyCouriersPacket;
import com.teamresourceful.resourcefulbees.common.networking.packets.server.SyncColonyRoutesPacket;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModDataComponents;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ColonyControllerMenu extends AbstractModContainerMenu<ColonyControllerBlockEntity> {

    private final ContainerData controllerData;
    private List<ColonyRouteSnapshot> routeSnapshots = List.of();
    private long lastSentRouteRevision = Long.MIN_VALUE;
    private int activeTab = 0;

    private List<ColonyCourierSnapshot> courierSnapshots = List.of();
    private List<ColonyCourierSnapshot> lastSentCourierSnapshots = null;


    private static final int UPGRADES_TAB = 3;


    public ColonyControllerMenu(int id, Inventory inventory, Optional<PositionContent> content) {
        this(id, inventory, PositionContent.getOrNull(content, inventory.player.level(), ColonyControllerBlockEntity.class), new SimpleContainerData(ColonyControllerData.SIZE));
    }

    public ColonyControllerMenu(int id, Inventory inventory, ColonyControllerBlockEntity entity) {
        this(id, inventory, entity, new ColonyControllerData(entity));
    }

    private ColonyControllerMenu(int id, Inventory inventory, ColonyControllerBlockEntity entity, ContainerData data) {
        super(ModMenuTypes.COLONY_CONTROLLER.get(), id, inventory, entity);

        this.controllerData = data;
        this.addDataSlots(data);
    }

    @Override
    protected void addMenuSlots() {
        addUpgradeSlot(ColonyConstants.RADIUS_SLOT, 62, 183, Upgrade.Type.COLONY_RADIUS);
        addUpgradeSlot(ColonyConstants.ROUTES_SLOT, 119, 183, Upgrade.Type.COLONY_ROUTES);
        addUpgradeSlot(ColonyConstants.BEES_SLOT, 176, 183, Upgrade.Type.COLONY_BEES);
    }

    private void addUpgradeSlot(int index, int x, int y, Upgrade.Type type) {
        this.addSlot(new ResourceHandlerSlot(
                getEntity().getUpgradeInventory(),
                getEntity().getUpgradeInventory()::set,
                index,
                x,
                y
        ) {
            @Override
            public boolean mayPlace(@NonNull ItemStack stack) {
                Upgrade upgrade = stack.get(ModDataComponents.UPGRADE);
                return upgrade != null && upgrade.isType(type);
            }

            @Override
            public boolean isActive() {
                return ColonyControllerMenu.this.areUpgradeSlotsActive();
            }
        });
    }

    @Override
    protected int getContainerInputEnd() {
        return ColonyConstants.SLOT_COUNT;
    }

    @Override
    protected int getInventoryStart() {
        return ColonyConstants.SLOT_COUNT;
    }

    @Override
    public int getPlayerInvXOffset() {
        return 47;
    }

    @Override
    public int getPlayerInvYOffset() {
        return 216;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        if (getEntity().isRemoved() || player.level() != getEntity().getLevel()) {
            return false;
        }
        BlockPos pos = getEntity().getBlockPos();
        return player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64.0;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        if (index < 0 || index >= this.slots.size()) {
            return ItemStack.EMPTY;
        }

        if (!areUpgradeSlotsActive()) {
            return ItemStack.EMPTY;
        }

        return super.quickMoveStack(player, index);
    }


    @Override
    protected void addPlayerInvSlots() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(inv, j + i * 9 + 9, getPlayerInvXOffset() + j * 18, getPlayerInvYOffset() + i * 18) {
                    @Override
                    public boolean isActive() {
                        return ColonyControllerMenu.this.isInventoryVisible();
                    }
                });
            }
        }

        for (int k = 0; k < 9; k++) {
            this.addSlot(new Slot(inv, k, getPlayerInvXOffset() + k * 18, getPlayerInvYOffset() + 58) {
                @Override
                public boolean isActive() {
                    return ColonyControllerMenu.this.isInventoryVisible();
                }
            });
        }
    }


    public void setActiveTab(int tab) {
        if (tab >= 0 && tab <= UPGRADES_TAB) {
            this.activeTab = tab;
        }
    }

    public boolean areUpgradeSlotsActive() {
        return activeTab == UPGRADES_TAB;
    }

    public boolean isInventoryVisible() {
        return activeTab == UPGRADES_TAB;
    }

    public List<ColonyRouteSnapshot> getRouteSnapshots() {
        return routeSnapshots;
    }

    public void setRouteSnapshots(List<ColonyRouteSnapshot> snapshots) {
        this.routeSnapshots = List.copyOf(snapshots);
    }

    public int getOperatingRadius() {
        return controllerData.get(ColonyControllerData.RADIUS);
    }

    public int getMaxRoutes() {
        return controllerData.get(ColonyControllerData.MAX_ROUTES);
    }

    public int getMaxBees() {
        return controllerData.get(ColonyControllerData.MAX_BEES);
    }

    public int getRegisteredCourierCount() {
        return controllerData.get(ColonyControllerData.REGISTERED_COURIERS);
    }

    public int getRouteCount() {
        return controllerData.get(ColonyControllerData.ROUTE_COUNT);
    }

    public int getStoredBeeCount() {
        return controllerData.get(ColonyControllerData.STORED_BEES);
    }


    public List<ColonyCourierSnapshot> getCourierSnapshots() {
        return courierSnapshots;
    }



    @Override
    public void broadcastChanges() {
        super.broadcastChanges();

        if (level.isClientSide()
                || !(player instanceof net.minecraft.server.level.ServerPlayer serverPlayer)
                || entity == null) {
            return;
        }

        long revision = entity.getRouteRevision();

        if (revision != lastSentRouteRevision) {
            NetworkHandler.NETWORK.sendToPlayer(
                    new SyncColonyRoutesPacket(
                            entity.getBlockPos(),
                            containerId,
                            createRouteData()
                    ),
                    serverPlayer
            );

            lastSentRouteRevision = revision;
        }

        if (level.getGameTime() % 20 == 0) {
            List<ColonyCourierSnapshot> snapshots =
                    entity.getCourierSnapshots();

            if (!snapshots.equals(lastSentCourierSnapshots)) {
                NetworkHandler.NETWORK.sendToPlayer(
                        new SyncColonyCouriersPacket(
                                entity.getBlockPos(),
                                containerId,
                                createCourierData(snapshots)
                        ),
                        serverPlayer
                );

                lastSentCourierSnapshots = List.copyOf(snapshots);
            }
        }
    }



    private CompoundTag createCourierData(
            List<ColonyCourierSnapshot> snapshots
    ) {
        CompoundTag data = new CompoundTag();

        var result = ColonyCourierSnapshot.CODEC.listOf().encodeStart(
                NbtOps.INSTANCE,
                snapshots
        );

        result.result().ifPresent(tag -> data.put("Couriers", tag));

        return data;
    }



    public void receiveCourierData(CompoundTag data) {
        var couriersTag = data.get("Couriers");

        if (couriersTag == null) {
            courierSnapshots = List.of();
            return;
        }

        var result = ColonyCourierSnapshot.CODEC.listOf().parse(
                NbtOps.INSTANCE,
                couriersTag
        );

        result.result().ifPresent(snapshots ->
                courierSnapshots = List.copyOf(snapshots)
        );
    }


    private CompoundTag createRouteData() {
        CompoundTag data = new CompoundTag();
        var result = CourierRoute.CODEC.listOf().encodeStart(NbtOps.INSTANCE, new ArrayList<>(entity.getRoutes().values()));
        result.result().ifPresent(tag -> data.put("Routes", tag));
        return data;
    }

    public void receiveRouteData(CompoundTag data) {
        var routesTag = data.get("Routes");

        if (routesTag == null) {
            this.routeSnapshots = List.of();
            return;
        }

        var result = CourierRoute.CODEC.listOf().parse(
                net.minecraft.nbt.NbtOps.INSTANCE,
                routesTag
        );

        result.result().ifPresent(routes ->
                this.routeSnapshots = routes.stream()
                        .map(ColonyRouteSnapshot::from)
                        .toList()
        );
    }
}