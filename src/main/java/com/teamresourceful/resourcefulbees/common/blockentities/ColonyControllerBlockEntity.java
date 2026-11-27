package com.teamresourceful.resourcefulbees.common.blockentities;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.compat.BeeCompat;
import com.teamresourceful.resourcefulbees.common.blockentities.base.BeeHolderBlockEntity;
import com.teamresourceful.resourcefulbees.common.blockentities.base.IOResourceHandler;
import com.teamresourceful.resourcefulbees.common.blocks.ColonyControllerBlock;
import com.teamresourceful.resourcefulbees.common.blocks.base.InstanceBlockEntityTicker;
import com.teamresourceful.resourcefulbees.common.components.ApiaryOccupant;
import com.teamresourceful.resourcefulbees.common.components.Upgrade;
import com.teamresourceful.resourcefulbees.common.entities.entity.ResourcefulBee;
import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.ColonyConstants;
import com.teamresourceful.resourcefulbees.common.logistics.*;
import com.teamresourceful.resourcefulbees.common.logistics.courier.CourierAssignment;
import com.teamresourceful.resourcefulbees.common.logistics.courier.CourierDeathNotifications;
import com.teamresourceful.resourcefulbees.common.logistics.courier.CourierWorkProbe;
import com.teamresourceful.resourcefulbees.common.menus.ColonyControllerMenu;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlockEntityTypes;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ColonyControllerBlockEntity extends BeeHolderBlockEntity implements InstanceBlockEntityTicker {


    private static final int COURIER_RETURN_GRACE_TICKS = 100;
    public static final int DEFAULT_RECRUITMENT_INTERVAL = 100;
    private static final int COURIER_WAKE_COOLDOWN = 40;

    private final UpgradeInventory upgradeInventory = new UpgradeInventory();

    private static final Codec<List<CourierRoute>> ROUTES_CODEC = CourierRoute.CODEC.listOf();
    private static final Codec<List<UUID>> COURIERS_CODEC = UUIDUtil.CODEC.listOf();
    private static final Codec<List<StoredPendingRouteCouriers>> PENDING_ROUTE_COURIERS_CODEC = StoredPendingRouteCouriers.CODEC.listOf();
    private static final Codec<List<StoredCourierAssignment>> ACTIVE_COURIER_ASSIGNMENTS_CODEC = StoredCourierAssignment.CODEC.listOf();
    private static final Codec<List<UUID>> MANUAL_COURIERS_CODEC = UUIDUtil.CODEC.listOf();

    private static final Codec<List<StoredManualCourierRoute>> MANUAL_COURIER_ROUTES_CODEC = StoredManualCourierRoute.CODEC.listOf();

    private long nextCourierWakeTick;

    @NonNull
    private UUID colonyId = UUID.randomUUID();
    private int recruitmentInterval = DEFAULT_RECRUITMENT_INTERVAL;
    private int recruitmentTicks;
    private int fluidTransferLimit = CourierFluidTransferEngine.DEFAULT_TRANSFER_LIMIT;
    private int energyTransferLimit = CourierEnergyTransferEngine.DEFAULT_TRANSFER_LIMIT;

    private final Map<UUID, CourierRoute> routes = new LinkedHashMap<>();
    private final Map<UUID, CourierRoute> pendingRouteUpdates = new LinkedHashMap<>();
    private final Map<UUID, Set<UUID>> pendingRouteCouriers = new LinkedHashMap<>();
    private final Map<UUID, UUID> activeCourierAssignments = new LinkedHashMap<>();
    private final Set<UUID> manualCouriers = new HashSet<>();
    private final Map<UUID, UUID> manualCourierRoutes = new HashMap<>();
    private final Set<UUID> pendingCourierReassignments = new HashSet<>();

    private final Set<UUID> registeredCouriers = new HashSet<>();
    private long routeRevision;


    private final CourierRouteScheduler courierScheduler = new CourierRouteScheduler();

    public ColonyControllerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.COLONY_CONTROLLER.get(), pos, state);
    }

    public long getRouteRevision() {
        return routeRevision;
    }

    private void routesChanged() {
        routeRevision++;
        setChanged();
    }


    public boolean isCourierManual(UUID courierId) {
        return manualCouriers.contains(courierId);
    }

    public @Nullable UUID getManualCourierRoute(UUID courierId) {
        return manualCourierRoutes.get(courierId);
    }


    public boolean setCourierScheduling(UUID courierId, boolean manual, @Nullable UUID preferredRouteId) {
        if (!(level instanceof ServerLevel) || !registeredCouriers.contains(courierId)) {
            return false;
        }

        if (manual && preferredRouteId != null && !routes.containsKey(preferredRouteId)) {
            return false;
        }

        boolean wasManual = manualCouriers.contains(courierId);
        UUID previousRoute = manualCourierRoutes.get(courierId);

        boolean changed = wasManual != manual
                || (manual && !Objects.equals(
                previousRoute, preferredRouteId));

        if (!changed) {
            return true;
        }

        if (manual) {
            manualCouriers.add(courierId);

            if (preferredRouteId == null) {
                manualCourierRoutes.remove(courierId);
            } else {
                manualCourierRoutes.put(courierId, preferredRouteId);
            }
        } else {
            manualCouriers.remove(courierId);
            manualCourierRoutes.remove(courierId);
        }

        if (activeCourierAssignments.containsKey(courierId)) {
            pendingCourierReassignments.add(courierId);
        }

        setChanged();
        return true;
    }



    public Set<UUID> getRegisteredCouriers() {
        return Collections.unmodifiableSet(registeredCouriers);
    }

    public boolean isCourierRegistered(UUID beeId) {
        return registeredCouriers.contains(beeId);
    }

    public boolean registerCourier(ResourcefulBee bee) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return false;
        }

        if (bee.level() != serverLevel || !bee.isAlive()) {
            return false;
        }

        if (!isWithinRange(bee.blockPosition())) {
            return false;
        }

        UUID beeId = bee.getUUID();

        // Already registered: verify that both sides agree.
        if (registeredCouriers.contains(beeId)) {
            return bee.belongsToColony(colonyId) && worldPosition.equals(bee.getColonyControllerPos());
        }

        if (registeredCouriers.size() >= getMaxBees()) {
            return false;
        }

        // Reject membership belonging to a different colony/controller.
        if (bee.isColonyMember()) {
            return false;
        }

        // Bee-side registration validates the courier ability,
        // controller identity, and operating radius.
        if (!bee.joinColony(colonyId, worldPosition)) {
            return false;
        }

        registeredCouriers.add(beeId);
        setChanged();

        return true;
    }

    public boolean unregisterCourier(ResourcefulBee bee) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return false;
        }

        if (bee.level() != serverLevel) {
            return false;
        }

        UUID beeId = bee.getUUID();
        UUID assignedRouteId = activeCourierAssignments.get(beeId);

        if (assignedRouteId != null && pendingRouteUpdates.containsKey(assignedRouteId)) {
            return false;
        }

        if (!registeredCouriers.contains(beeId)) {
            return false;
        }

        // Never remove a bee's membership from another colony.
        if (!bee.belongsToColony(colonyId) || !worldPosition.equals(bee.getColonyControllerPos())) {
            return false;
        }

        // Clears the active assignment, but preserves cargo.
        if (!bee.leaveColony(colonyId)) {
            return false;
        }

        registeredCouriers.remove(beeId);
        manualCouriers.remove(beeId);
        manualCourierRoutes.remove(beeId);
        pendingCourierReassignments.remove(beeId);
        setChanged();

        return true;
    }


    public boolean recruitOrTransferCourier(ResourcefulBee bee) {
        if (!(level instanceof ServerLevel serverLevel) || bee.level() != serverLevel || !bee.isAlive() || bee.getCourierAbility() == null || !isWithinRange(bee.blockPosition())) {
            return false;
        }

        UUID beeId = bee.getUUID();

        if (!registeredCouriers.contains(beeId) && registeredCouriers.size() >= getMaxBees()) {
            return false;
        }

        if (!bee.isColonyMember()) {
            return registerCourier(bee);
        }

        if (bee.belongsToColony(colonyId) && worldPosition.equals(bee.getColonyControllerPos())) {
            return registeredCouriers.contains(beeId);
        }

        UUID previousId = bee.getColonyId();
        BlockPos previousPos = bee.getColonyControllerPos();

        if (previousId == null || previousPos == null || !serverLevel.hasChunkAt(previousPos) || registeredCouriers.contains(beeId)) {
            return false;
        }

        if (previousPos.equals(worldPosition)) {
            if (previousId.equals(colonyId)) {
                return false;
            }

            if (!bee.transferColony(previousId, previousPos, colonyId, worldPosition)) {
                return false;
            }

            registeredCouriers.add(beeId);
            setChanged();
            return true;
        }

        if (!(serverLevel.getBlockEntity(previousPos) instanceof ColonyControllerBlockEntity previous)) {
            return false;
        }

        if (previous == this || !previous.getColonyId().equals(previousId) || !previous.isCourierRegistered(beeId)) {
            return false;
        }

        UUID previousRouteId = previous.activeCourierAssignments.get(beeId);

        if (previousRouteId != null && previous.pendingRouteUpdates.containsKey(previousRouteId)) {
            return false;
        }


        if (!bee.transferColony(previousId, previousPos, colonyId, worldPosition)) {
            return false;
        }

        previous.registeredCouriers.remove(beeId);
        previous.manualCouriers.remove(beeId);
        previous.manualCourierRoutes.remove(beeId);
        previous.pendingCourierReassignments.remove(beeId);
        registeredCouriers.add(beeId);
        manualCouriers.remove(beeId);
        manualCourierRoutes.remove(beeId);
        pendingCourierReassignments.remove(beeId);

        previous.setChanged();
        setChanged();

        return true;
    }


    private void recruitCouriers(ServerLevel level) {
        double radius = getOperatingRadius();

        AABB searchArea = new AABB(worldPosition.getX() - radius, worldPosition.getY() - radius, worldPosition.getZ() - radius, worldPosition.getX() + radius + 1, worldPosition.getY() + radius + 1, worldPosition.getZ() + radius + 1);

        List<ResourcefulBee> candidates = level.getEntitiesOfClass(ResourcefulBee.class, searchArea, bee -> bee.isAlive() && (!bee.isColonyMember() || worldPosition.equals(bee.getColonyControllerPos())) && bee.getCourierAbility() != null && isWithinRange(bee.blockPosition()));

        candidates.sort(Comparator.comparingDouble(bee -> ((ResourcefulBee) bee).blockPosition().distSqr(worldPosition)).thenComparing(bee -> ((ResourcefulBee) bee).getUUID()));

        for (ResourcefulBee bee : candidates) {
            if (!bee.isColonyMember() || worldPosition.equals(bee.getColonyControllerPos())) {
                recruitOrTransferCourier(bee);
            }
        }
    }


    public @NonNull UUID getColonyId() {
        return colonyId;
    }

    @Override
    public Side getSide() {
        return Side.SERVER;
    }


    @Override
    public void serverTick(ServerLevel level, BlockPos pos, BlockState state) {
        BeeHolderBlockEntity.serverTick(level, pos, state, this);
        processPendingCourierDeaths(level);
        applyReadyRouteUpdates();

        Map<UUID, CourierRoute> validRoutes = new LinkedHashMap<>();

        for (CourierRoute route : routes.values()) {
            if (isRouteWithinRange(route) && !hasPendingRouteUpdate(route.id())) {
                validRoutes.put(route.id(), route);
            }
        }


        if (!bees.isEmpty()) {
            boolean needsCourier = validRoutes.values().stream().anyMatch(route -> CourierWorkProbe.hasPendingWork(level, route) && !hasAssignableCourier(level, route));

            if (needsCourier) {
                wakeOneStoredCourier(level, state);
            }
        }

        courierScheduler.tick(validRoutes, level.getGameTime(), route -> switch (route.executionMode()) {
            case INSTANT -> true;
            case COURIER -> hasAssignableCourier(level, route);
        }, route -> {
            if (route.executionMode() == CourierRouteExecutionMode.COURIER) {
                assignOneCourier(level, route);
                return;
            }

            switch (route.resourceType()) {
                case ITEM -> CourierTransferEngine.transfer(level, route, courierScheduler.getTransferLimit());
                case FLUID -> CourierFluidTransferEngine.transfer(level, route, fluidTransferLimit);
                case ENERGY -> CourierEnergyTransferEngine.transfer(level, route, energyTransferLimit);
            }
        });

        if (++recruitmentTicks >= recruitmentInterval) {
            recruitmentTicks = 0;
            recruitCouriers(level);
        }
    }

    public Map<UUID, CourierRoute> getRoutes() {
        return Collections.unmodifiableMap(routes);
    }

    public List<ColonyRouteSnapshot> getRouteSnapshots() {
        return routes.values().stream().map(ColonyRouteSnapshot::from).toList();
    }

    public boolean addRoute(CourierRoute route) {
        if (!isRouteWithinRange(route)) {
            return false;
        }

        if (routes.containsKey(route.id())) {
            return false;
        }

        if (routes.size() >= getMaxRoutes()) {
            return false;
        }

        routes.put(route.id(), route);
        routesChanged();
        return true;
    }



    public boolean removeRoute(UUID routeId) {
        if (!routes.containsKey(routeId) || hasActiveCourierAssignments(routeId) || pendingRouteUpdates.containsKey(routeId)) {
            return false;
        }

        routes.remove(routeId);
        pendingRouteCouriers.remove(routeId);

        routesChanged();
        return true;
    }



    public boolean updateRoute(CourierRoute updated) {
        CourierRoute previous = routes.get(updated.id());

        if (previous == null || pendingRouteUpdates.containsKey(updated.id()) || !isRouteWithinRange(updated)) {
            return false;
        }

        // Endpoint changes must use queueRouteEndpointUpdate().
        if (!previous.source().equals(updated.source()) || !previous.destination().equals(updated.destination())) {
            return false;
        }

        if (previous.equals(updated)) {
            return true;
        }

        if (hasActiveCourierAssignments(updated.id())
                && ((previous.enabled() && !updated.enabled())
                        || (previous.executionMode() == CourierRouteExecutionMode.COURIER
                        && updated.executionMode() != CourierRouteExecutionMode.COURIER)
        )) {
            return false;
        }

        routes.put(updated.id(), updated);
        routesChanged();

        return true;
    }

    public boolean acknowledgeRouteUpdate(UUID routeId, UUID courierId) {
        Set<UUID> outstanding = pendingRouteCouriers.get(routeId);

        if (outstanding == null || !routeId.equals(activeCourierAssignments.get(courierId)) || !outstanding.remove(courierId)) {
            return false;
        }

        setChanged();
        return true;
    }

    public boolean queueRouteEndpointUpdate(UUID routeId, CourierEndpoint replacement, CourierRouteEndpointEdit.EndpointType endpointType) {
        CourierRoute current = routes.get(routeId);

        if (current == null || pendingRouteUpdates.containsKey(routeId) || !isWithinRange(replacement.pos())) {
            return false;
        }

        CourierEndpoint source = endpointType == CourierRouteEndpointEdit.EndpointType.SOURCE ? replacement : current.source();
        CourierEndpoint destination = endpointType == CourierRouteEndpointEdit.EndpointType.DESTINATION ? replacement : current.destination();

        if (source.equals(destination)) {
            return false;
        }

        CourierRoute updated = current.withEndpoints(source, destination);

        pendingRouteUpdates.put(routeId, updated);
        pendingRouteCouriers.put(routeId, new HashSet<>(getAssignedCouriers(routeId)));
        setChanged();

        return true;
    }

    private void applyReadyRouteUpdates() {
        if (pendingRouteUpdates.isEmpty()) {
            return;
        }

        boolean changed = false;

        Iterator<Map.Entry<UUID, CourierRoute>> iterator = pendingRouteUpdates.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<UUID, CourierRoute> entry = iterator.next();

            UUID routeId = entry.getKey();
            CourierRoute updated = entry.getValue();

            Set<UUID> outstanding = pendingRouteCouriers.get(routeId);

            if (outstanding == null || !outstanding.isEmpty()) {
                continue;
            }

            CourierRoute current = routes.get(routeId);

            if (current == null || !isRouteWithinRange(updated)) {
                continue;
            }

            routes.put(routeId, updated);

            iterator.remove();
            pendingRouteCouriers.remove(routeId);

            changed = true;
        }

        if (changed) {
            routesChanged();
        }
    }

    public boolean setRouteEnabled(UUID routeId, boolean enabled) {
        CourierRoute route = routes.get(routeId);

        return route != null && updateRoute(route.withEnabled(enabled));
    }

    public boolean setRoutePriority(UUID routeId, int priority) {
        CourierRoute route = routes.get(routeId);

        return route != null && updateRoute(route.withPriority(priority));
    }

    public boolean setRouteExecutionMode(UUID routeId, CourierRouteExecutionMode mode) {
        CourierRoute route = routes.get(routeId);

        return route != null && updateRoute(route.withExecutionMode(mode));
    }


    public boolean isRouteWithinRange(CourierRoute route) {
        return isWithinRange(route.source().pos()) && isWithinRange(route.destination().pos());
    }

    public boolean isWithinRange(BlockPos pos) {
        double radiusSquared = (double) getOperatingRadius() * getOperatingRadius();

        return worldPosition.distSqr(pos) <= radiusSquared;
    }


    private boolean canStoreCourier(ResourcefulBee bee) {
        return level instanceof ServerLevel && bee.level() == level && bee.isAlive() && bee.getCourierAbility() != null && registeredCouriers.contains(bee.getUUID()) && bee.belongsToColony(colonyId) && worldPosition.equals(bee.getColonyControllerPos()) && !bee.getCourierDelivery().isAssigned() && !bee.hasCourierCargo() && isWithinRange(bee.blockPosition()) && hasSpace();
    }

    public boolean canAcceptReturningCourier(ResourcefulBee bee) {
        return canStoreCourier(bee);
    }


    @Override
    public void tryEnterHive(@NotNull Entity entity) {
        if (!(entity instanceof ResourcefulBee bee) || !canStoreCourier(bee)) {
            return;
        }

        super.tryEnterHive(bee);
    }


    @Override
    protected void onBeeReleased(Entity entity) {
        if (!(entity instanceof ResourcefulBee bee) || !bee.belongsToColony(colonyId) || !worldPosition.equals(bee.getColonyControllerPos()) || !registeredCouriers.contains(bee.getUUID())) {
            return;
        }

        // Keep the courier available long enough for the
        // route scheduler to assign it before it returns.
        bee.delayCourierReturn(Math.max(COURIER_RETURN_GRACE_TICKS, courierScheduler.getInterval() + 20));
    }


    @Override
    protected void deliverNectar(boolean hasNectar, Entity bee) {
        //colony bees don't deliver nectar
        if (bee instanceof BeeCompat compat) {
            compat.resourcefulBees$nectarDroppedOff();
        }
    }

    @Override
    public int getMaxTimeInHive(@NotNull BeeCompat bee, boolean hasNectar) {
        return (int) ((hasNectar ? BeeConstants.MIN_HIVE_TIME : bee.resourcefulBees$getMaxTimeInHive()) * 0.25);
    }

    @Override
    public boolean hasSpace() {
        return this.bees.size() < getMaxBees();
    }

    @Override
    public boolean isAllowedBee(@Nullable ResourcefulBee bee) {
        return getBlockState().getBlock() instanceof ColonyControllerBlock && bee != null && bee.hasCourierAbility();
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);

        colonyId = input.read("ColonyId", UUIDUtil.CODEC).orElse(colonyId);
        upgradeInventory.deserialize(input);

        int interval = input.getIntOr("TransferInterval", CourierRouteScheduler.DEFAULT_INTERVAL);
        int limit = input.getIntOr("TransferLimit", CourierRouteScheduler.DEFAULT_TRANSFER_LIMIT);

        fluidTransferLimit = Math.max(1, input.getIntOr("FluidTransferLimit", CourierFluidTransferEngine.DEFAULT_TRANSFER_LIMIT));
        energyTransferLimit = Math.max(1, input.getIntOr("EnergyTransferLimit", CourierEnergyTransferEngine.DEFAULT_TRANSFER_LIMIT));
        courierScheduler.setInterval(interval > 0 ? interval : CourierRouteScheduler.DEFAULT_INTERVAL);
        courierScheduler.setTransferLimit(limit > 0 ? limit : CourierRouteScheduler.DEFAULT_TRANSFER_LIMIT);
        courierScheduler.reset();

        routes.clear();
        List<CourierRoute> savedRoutes = input.read("Routes", ROUTES_CODEC).orElse(List.of());

        for (CourierRoute route : savedRoutes) {
            if (isRouteWithinRange(route)) {
                routes.putIfAbsent(route.id(), route);
            }
        }

        pendingRouteUpdates.clear();
        List<CourierRoute> savedPendingUpdates = input.read("PendingRouteUpdates", ROUTES_CODEC).orElse(List.of());

        for (CourierRoute pending : savedPendingUpdates) {
            CourierRoute current = routes.get(pending.id());

            if (current != null && isRouteWithinRange(pending) && current.resourceType() == pending.resourceType() && current.executionMode() == pending.executionMode() && current.enabled() == pending.enabled() && current.priority() == pending.priority() && current.courierFilter().equals(pending.courierFilter())) {
                pendingRouteUpdates.put(pending.id(), pending);
            }
        }

        pendingRouteCouriers.clear();

        List<StoredPendingRouteCouriers> savedPendingCouriers = input.read("PendingRouteCouriers", PENDING_ROUTE_COURIERS_CODEC).orElse(List.of());

        for (StoredPendingRouteCouriers stored : savedPendingCouriers) {
            if (pendingRouteUpdates.containsKey(stored.routeId())) {
                pendingRouteCouriers.putIfAbsent(stored.routeId(), new HashSet<>(stored.courierIds()));
            }
        }

        registeredCouriers.clear();

        List<UUID> savedCouriers = input.read("RegisteredCouriers", COURIERS_CODEC).orElse(List.of());
        registeredCouriers.addAll(savedCouriers);


        manualCouriers.clear();
        manualCourierRoutes.clear();

        for (UUID courierId : input.read("ManualCouriers", MANUAL_COURIERS_CODEC).orElse(List.of())) {
            if (registeredCouriers.contains(courierId)) {
                manualCouriers.add(courierId);
            }
        }

        for (StoredManualCourierRoute saved : input.read("ManualCourierRoutes", MANUAL_COURIER_ROUTES_CODEC).orElse(List.of())) {
            if (manualCouriers.contains(saved.courierId())) {
                manualCourierRoutes.put(saved.courierId(), saved.routeId());
            }
        }


        activeCourierAssignments.clear();

        List<StoredCourierAssignment> savedAssignments = input.read("ActiveCourierAssignments", ACTIVE_COURIER_ASSIGNMENTS_CODEC).orElse(List.of());

        for (StoredCourierAssignment stored : savedAssignments) {
            UUID courierId = stored.courierId();
            CourierAssignment assignment = stored.assignment();

            if (colonyId.equals(assignment.colonyId()) && registeredCouriers.contains(courierId) && routes.containsKey(assignment.routeId())) {
                activeCourierAssignments.putIfAbsent(courierId, assignment.routeId());
            }
        }

        pendingCourierReassignments.clear();

        for (UUID courierId : input.read("PendingCourierReassignments", COURIERS_CODEC).orElse(List.of())) {
            if (registeredCouriers.contains(courierId) && activeCourierAssignments.containsKey(courierId)) {
                pendingCourierReassignments.add(courierId);
            }
        }
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);
        output.store("ColonyId", UUIDUtil.CODEC, colonyId);
        output.store("Routes", ROUTES_CODEC, new ArrayList<>(routes.values()));
        output.store("PendingRouteUpdates", ROUTES_CODEC, new ArrayList<>(pendingRouteUpdates.values()));
        List<StoredPendingRouteCouriers> savedPendingCouriers =
                pendingRouteCouriers.entrySet().stream()
                        .map(entry -> new StoredPendingRouteCouriers(
                                entry.getKey(),
                                entry.getValue()
                        ))
                        .toList();

        output.store("PendingRouteCouriers", PENDING_ROUTE_COURIERS_CODEC, savedPendingCouriers);
        List<StoredCourierAssignment> savedAssignments =
                activeCourierAssignments.entrySet().stream()
                        .map(entry -> new StoredCourierAssignment(
                                entry.getKey(),
                                new CourierAssignment(colonyId, entry.getValue())
                        ))
                        .toList();


        output.store("ManualCouriers", MANUAL_COURIERS_CODEC, new ArrayList<>(manualCouriers));

        List<StoredManualCourierRoute> savedManualRoutes =
                manualCourierRoutes.entrySet().stream()
                        .map(entry -> new StoredManualCourierRoute(
                                entry.getKey(),
                                entry.getValue()
                        ))
                        .toList();

        output.store("ManualCourierRoutes", MANUAL_COURIER_ROUTES_CODEC, savedManualRoutes);
        output.store("PendingCourierReassignments", COURIERS_CODEC, new ArrayList<>(pendingCourierReassignments));

        output.store("ActiveCourierAssignments", ACTIVE_COURIER_ASSIGNMENTS_CODEC, savedAssignments);
        output.store("RegisteredCouriers", COURIERS_CODEC, new ArrayList<>(registeredCouriers));
        output.putInt("TransferInterval", courierScheduler.getInterval());
        output.putInt("TransferLimit", courierScheduler.getTransferLimit());
        output.putInt("FluidTransferLimit", fluidTransferLimit);
        output.putInt("EnergyTransferLimit", energyTransferLimit);
        upgradeInventory.serialize(output);
    }

    @VisibleForDebug
    public int getTransferInterval() {
        return courierScheduler.getInterval();
    }

    public void setTransferInterval(int interval) {
        if (interval < 1) {
            throw new IllegalArgumentException("Transfer interval must be at least 1 tick");
        }

        if (courierScheduler.getInterval() != interval) {
            courierScheduler.setInterval(interval);
            setChanged();
        }
    }

    public int getTransferLimit() {
        return courierScheduler.getTransferLimit();
    }

    public void setTransferLimit(int limit) {
        if (limit < 1) {
            throw new IllegalArgumentException("Transfer limit must be at least 1 item");
        }

        if (courierScheduler.getTransferLimit() != limit) {
            courierScheduler.setTransferLimit(limit);
            setChanged();
        }
    }

    public int getFluidTransferLimit() {
        return fluidTransferLimit;
    }

    public void setFluidTransferLimit(int limit) {
        if (limit < 1) {
            throw new IllegalArgumentException("Fluid transfer limit must be at least 1");
        }

        if (fluidTransferLimit != limit) {
            fluidTransferLimit = limit;
            setChanged();
        }
    }

    public int getEnergyTransferLimit() {
        return energyTransferLimit;
    }

    public void setEnergyTransferLimit(int limit) {
        if (limit < 1) {
            throw new IllegalArgumentException("Energy transfer limit must be at least 1");
        }
        if (energyTransferLimit != limit) {
            energyTransferLimit = limit;
            setChanged();
        }
    }

    private boolean isCourierEligibleForRoute(
            UUID courierId,
            UUID routeId
    ) {
        if (!manualCouriers.contains(courierId)) {
            return true;
        }

        return routeId.equals(manualCourierRoutes.get(courierId));
    }

    private Map<UUID, ApiaryOccupant> getStoredCourierOccupants() {
        Map<UUID, ApiaryOccupant> stored = new HashMap<>();

        for (ApiaryOccupant.Mutable mutable : bees) {
            ApiaryOccupant occupant = mutable.immutable();
            CompoundTag entityTag = occupant.entityData().copyTagWithoutId();
            UUID beeId = entityTag.read("UUID", UUIDUtil.CODEC).orElse(null);

            if (beeId != null && registeredCouriers.contains(beeId)) {
                stored.put(beeId, occupant);
            }
        }

        return stored;
    }



    public List<ColonyCourierSnapshot> getCourierSnapshots() {
        if (!(level instanceof ServerLevel serverLevel)) {
            return List.of();
        }

        Map<UUID, ResourcefulBee> loadedCouriers =
                serverLevel.getEntitiesOfClass(
                        ResourcefulBee.class,
                        new AABB(worldPosition).inflate(getOperatingRadius()),
                        bee -> registeredCouriers.contains(bee.getUUID())
                                && bee.isAlive()
                                && bee.belongsToColony(colonyId)
                                && worldPosition.equals(bee.getColonyControllerPos())
                ).stream().collect(Collectors.toMap(
                        ResourcefulBee::getUUID,
                        Function.identity()
                ));

        Map<UUID, ApiaryOccupant> storedCouriers =
                getStoredCourierOccupants();

        return registeredCouriers.stream()
                .sorted()
                .map(id -> {
                    ResourcefulBee bee = loadedCouriers.get(id);

                    if (bee != null) {
                        ColonyCourierSnapshot.CargoSummary cargo =
                                ColonyCourierSnapshot.cargoFrom(bee);

                        return new ColonyCourierSnapshot(
                                id,
                                bee.getDisplayName().getString(),
                                ColonyCourierSnapshot.Location.ACTIVE,
                                activeCourierAssignments.get(id),
                                bee.getCourierState().name(),
                                cargo.name(),
                                cargo.amount(),
                                isCourierManual(id),
                                getManualCourierRoute(id)
                        );
                    }

                    ApiaryOccupant occupant = storedCouriers.get(id);

                    if (occupant != null) {
                        return new ColonyCourierSnapshot(
                                id,
                                occupant.displayName().getString(),
                                ColonyCourierSnapshot.Location.STORED,
                                activeCourierAssignments.get(id),
                                "IDLE",
                                "Empty",
                                0,
                                isCourierManual(id),
                                getManualCourierRoute(id)
                        );
                    }

                    return new ColonyCourierSnapshot(
                            id,
                            "Unknown Courier",
                            ColonyCourierSnapshot.Location.UNLOADED,
                            activeCourierAssignments.get(id),
                            "UNKNOWN",
                            "Unknown",
                            -1,
                            isCourierManual(id),
                            getManualCourierRoute(id)
                    );
                })
                .toList();
    }



    public boolean shouldReleaseCourierAssignment(UUID courierId, UUID activeRouteId) {
        if (!registeredCouriers.contains(courierId)) {
            return false;
        }

        if (hasPendingRouteUpdate(activeRouteId)) {
            return false;
        }

        if (pendingCourierReassignments.contains(courierId)) {
            return true;
        }

        if (!manualCouriers.contains(courierId)) {
            return false;
        }

        return !activeRouteId.equals(
                manualCourierRoutes.get(courierId)
        );
    }



    public boolean trackCourierAssignment(UUID courierId, UUID routeId) {
        if (!registeredCouriers.contains(courierId) || !routes.containsKey(routeId) || pendingRouteUpdates.containsKey(routeId) || activeCourierAssignments.containsKey(courierId)) {
            return false;
        }

        activeCourierAssignments.put(courierId, routeId);
        setChanged();
        return true;
    }

    public boolean untrackCourierAssignment(UUID courierId) {
        UUID routeId = activeCourierAssignments.get(courierId);

        if (routeId == null) {
            return false;
        }

        if (pendingRouteUpdates.containsKey(routeId)) {
            return false;
        }

        activeCourierAssignments.remove(courierId);
        pendingCourierReassignments.remove(courierId);
        setChanged();
        return true;
    }


    private boolean hasActiveCourierAssignments(UUID routeId) {
        return activeCourierAssignments.containsValue(routeId);
    }


    public Set<UUID> getAssignedCouriers(UUID routeId) {
        Set<UUID> result = new HashSet<>();

        activeCourierAssignments.forEach((courierId, assignedRouteId) -> {
            if (routeId.equals(assignedRouteId)) {
                result.add(courierId);
            }
        });

        return Collections.unmodifiableSet(result);
    }

    private boolean isRouteCompatibleWithCargo(ResourcefulBee bee, CourierRoute route) {
        CourierResourceType type = route.resourceType();

        if (bee.hasCourierCargoOtherThan(type)) {
            return false;
        }

        CourierFilter filter = route.courierFilter();

        return switch (type) {
            case ITEM -> {
                var cargo = bee.getCourierCargo();
                boolean compatible = true;

                for (int slot = 0; slot < cargo.size(); slot++) {
                    if (cargo.getAmountAsInt(slot) <= 0) {
                        continue;
                    }

                    if ((filter.mode() != CourierFilterMode.WHITELIST) || !filter.allows(cargo.getResource(slot).getItem())) {
                        compatible = false;
                        break;
                    }
                }

                yield compatible;
            }

            case FLUID -> {
                var cargo = bee.getCourierFluidCargo();
                boolean compatible = true;

                for (int slot = 0; slot < cargo.size(); slot++) {
                    if (cargo.getAmountAsInt(slot) <= 0) {
                        continue;
                    }

                    if ((filter.mode() != CourierFilterMode.WHITELIST) || !filter.allows(cargo.getResource(slot).getFluid())) {
                        compatible = false;
                        break;
                    }
                }

                yield compatible;
            }

            case ENERGY -> true;
        };
    }

    private boolean hasAssignableCourier(ServerLevel level, CourierRoute route) {
        return !hasPendingRouteUpdate(route.id()) && couriers(level, route).findAny().isPresent();
    }

    private void assignOneCourier(ServerLevel level, CourierRoute route) {
        if (hasPendingRouteUpdate(route.id())) {
            return;
        }

        couriers(level, route).min(Comparator.comparing(ResourcefulBee::getUUID)).ifPresent(bee -> bee.assignCourierRoute(new CourierAssignment(colonyId, route.id()), worldPosition));
    }


    private Stream<ResourcefulBee> couriers(ServerLevel level, CourierRoute route) {
        return level.getEntitiesOfClass(
                ResourcefulBee.class,
                new AABB(worldPosition).inflate(getOperatingRadius()),
                bee -> bee.isAlive()
                        && registeredCouriers.contains(bee.getUUID())
                        && bee.belongsToColony(colonyId)
                        && worldPosition.equals(bee.getColonyControllerPos())
                        && bee.getCourierAbility() != null
                        && !bee.getCourierDelivery().isAssigned()
                        && isWithinRange(bee.blockPosition())
                        && isCourierEligibleForRoute(
                        bee.getUUID(),
                        route.id()
                )
                        && canAssignCourierToRoute(level, bee, route)
        ).stream();
    }


    @Override
    public @NonNull Component getDisplayName() {
        return Component.literal("Colony Logistics Controller");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, @NonNull Inventory inventory, @NonNull Player player) {
        return new ColonyControllerMenu(containerId, inventory, this);
    }

    public boolean hasPendingRouteUpdate(UUID routeId) {
        return pendingRouteUpdates.containsKey(routeId);
    }

    public Optional<CourierRoute> getPendingRouteUpdate(UUID routeId) {
        return Optional.ofNullable(pendingRouteUpdates.get(routeId));
    }

    private int getUpgradeCount(int slot) {
        return Math.clamp(upgradeInventory.getAmountAsInt(slot), 0, ColonyConstants.MAX_UPGRADE_COUNT);
    }

    public int getOperatingRadius() {
        return ColonyConstants.RADIUS_BY_TIER[getUpgradeCount(ColonyConstants.RADIUS_SLOT)];
    }

    public int getMaxRoutes() {
        return ColonyConstants.ROUTES_BY_TIER[getUpgradeCount(ColonyConstants.ROUTES_SLOT)];
    }

    public int getMaxBees() {
        return ColonyConstants.BEES_BY_TIER[getUpgradeCount(ColonyConstants.BEES_SLOT)];
    }

    public UpgradeInventory getUpgradeInventory() {
        return upgradeInventory;
    }

    private boolean wakeOneStoredCourier(ServerLevel level, BlockState state) {
        if (bees.isEmpty() || level.getGameTime() < nextCourierWakeTick) {
            return false;
        }

        for (int index = 0; index < bees.size(); index++) {
            ApiaryOccupant.Mutable occupant = bees.get(index);

            if (occupant.isLocked()) {
                continue;
            }

            if (!releaseBee(state, occupant.immutable())) {
                return false;
            }

            bees.remove(index);
            nextCourierWakeTick = level.getGameTime() + COURIER_WAKE_COOLDOWN;

            setChanged();
            return true;
        }

        return false;
    }

    private boolean canAssignCourierToRoute(ServerLevel level, ResourcefulBee bee, CourierRoute route) {
        if (!isRouteCompatibleWithCargo(bee, route)) {
            return false;
        }

        if (bee.hasCourierCargo()) {
            return true;
        }

        return CourierWorkProbe.hasPendingWork(level, route);
    }

    private void processPendingCourierDeaths(ServerLevel level) {
        CourierDeathNotifications.get(level).process(colonyId, worldPosition, this::courierDied);
    }

    public void courierDied(UUID courierId) {
        boolean changed = registeredCouriers.remove(courierId);

        UUID routeId = activeCourierAssignments.remove(courierId);

        if (routeId != null) {
            changed = true;

            Set<UUID> outstanding = pendingRouteCouriers.get(routeId);

            if (outstanding != null) {
                outstanding.remove(courierId);
            }
        }

        if (manualCouriers.remove(courierId)) {
            changed = true;
        }

        if (manualCourierRoutes.remove(courierId) != null) {
            changed = true;
        }

        if (pendingCourierReassignments.remove(courierId)) {
            changed = true;
        }

        if (changed) {
            setChanged();
        }
    }

    public int getRegisteredCourierCount() {
        return registeredCouriers.size();
    }

    public int getRouteCount() {
        return routes.size();
    }

    public class UpgradeInventory extends IOResourceHandler {

        public UpgradeInventory() {
            super(ColonyConstants.SLOT_COUNT, ColonyConstants.SLOT_COUNT);
        }


        @Override
        public int insert(int index, @NonNull ItemResource resource, int amount, @NonNull TransactionContext transaction) {
            if (!isValidUpgrade(index, resource) || amount <= 0) {
                return 0;
            }

            int current = getAmountAsInt(index);
            int allowed = ColonyConstants.MAX_UPGRADE_COUNT - current;

            if (allowed <= 0) {
                return 0;
            }

            return super.insert(index, resource, Math.min(amount, allowed), transaction);
        }

        @Override
        public int extract(int index, @NonNull ItemResource resource, int amount, @NonNull TransactionContext transaction) {
            if (index < 0 || index >= ColonyConstants.SLOT_COUNT || amount <= 0) {
                return 0;
            }

            int current = getAmountAsInt(index);

            if (current == 0) {
                return 0;
            }

            int allowed = 0;

            for (int removed = 1; removed <= Math.min(amount, current); removed++) {
                if (!canUseUpgradeCount(index, current - removed)) {
                    break;
                }
                allowed = removed;
            }

            if (allowed == 0) {
                return 0;
            }

            return super.extract(index, resource, allowed, transaction);
        }

        @Override
        protected void onContentsChanged(int index, @NonNull ItemStack previousContents) {
            ColonyControllerBlockEntity.this.setChanged();
        }

        private boolean isValidUpgrade(int slot, ItemResource resource) {
            ItemStack stack = resource.toStack();

            Upgrade upgrade = stack.get(ModDataComponents.UPGRADE);

            if (upgrade == null) {
                return false;
            }

            return switch (slot) {
                case ColonyConstants.RADIUS_SLOT -> upgrade.isType(Upgrade.Type.COLONY_RADIUS);
                case ColonyConstants.ROUTES_SLOT -> upgrade.isType(Upgrade.Type.COLONY_ROUTES);
                case ColonyConstants.BEES_SLOT -> upgrade.isType(Upgrade.Type.COLONY_BEES);
                default -> false;
            };
        }
        private boolean canUseUpgradeCount(int slot, int count) {
            if (count < 0 || count > ColonyConstants.MAX_UPGRADE_COUNT) {
                return false;
            }

            return switch (slot) {
                case ColonyConstants.RADIUS_SLOT -> {
                    int radius = ColonyConstants.RADIUS_BY_TIER[count];
                    long radiusSquared = (long) radius * radius;

                    yield routes.values().stream().allMatch(route -> worldPosition.distSqr(route.source().pos()) <= radiusSquared && worldPosition.distSqr(route.destination().pos()) <= radiusSquared);
                }

                case ColonyConstants.ROUTES_SLOT -> routes.size() <= ColonyConstants.ROUTES_BY_TIER[count];

                case ColonyConstants.BEES_SLOT -> {
                    int capacity = ColonyConstants.BEES_BY_TIER[count];

                    yield registeredCouriers.size() <= capacity && bees.size() <= capacity;
                }

                default -> false;
            };
        }

    }

    private record StoredCourierAssignment(
            UUID courierId,
            CourierAssignment assignment
    ) {
        private static final Codec<StoredCourierAssignment> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                UUIDUtil.CODEC.fieldOf("courier_id").forGetter(StoredCourierAssignment::courierId),
                CourierAssignment.CODEC.fieldOf("assignment").forGetter(StoredCourierAssignment::assignment)
        ).apply(instance, StoredCourierAssignment::new));
    }

    private record StoredPendingRouteCouriers(
            UUID routeId,
            Set<UUID> courierIds
    ) {
        private static final Codec<StoredPendingRouteCouriers> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                UUIDUtil.CODEC.fieldOf("route_id").forGetter(StoredPendingRouteCouriers::routeId),
                UUIDUtil.CODEC_SET.fieldOf("courier_ids").forGetter(StoredPendingRouteCouriers::courierIds)
        ).apply(instance, StoredPendingRouteCouriers::new));
    }
}