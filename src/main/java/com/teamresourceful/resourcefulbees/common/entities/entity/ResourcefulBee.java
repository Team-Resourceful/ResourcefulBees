package com.teamresourceful.resourcefulbees.common.entities.entity;

import com.teamresourceful.resourcefulbees.api.data.bee.BeeTraitData;
import com.teamresourceful.resourcefulbees.api.data.bee.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.data.trait.TraitAbility;
import com.teamresourceful.resourcefulbees.api.data.trait.TraitDamageType;
import com.teamresourceful.resourcefulbees.api.data.trait.abilities.CourierAbility;
import com.teamresourceful.resourcefulbees.api.registry.TraitAbilityRegistry;
import com.teamresourceful.resourcefulbees.common.blockentities.ColonyControllerBlockEntity;
import com.teamresourceful.resourcefulbees.common.blockentities.TieredBeehiveBlockEntity;
import com.teamresourceful.resourcefulbees.common.blockentities.base.BeeHolderBlockEntity;
import com.teamresourceful.resourcefulbees.common.config.BeeConfig;
import com.teamresourceful.resourcefulbees.common.entities.ai.AuraHandler;
import com.teamresourceful.resourcefulbees.common.entities.goals.*;
import com.teamresourceful.resourcefulbees.common.entities.pathfinding.BeePathNavigation;
import com.teamresourceful.resourcefulbees.common.lib.constants.DataConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.TraitConstants;
import com.teamresourceful.resourcefulbees.common.lib.util.ModUtils;
import com.teamresourceful.resourcefulbees.common.lib.util.WorldUtils;
import com.teamresourceful.resourcefulbees.common.logistics.CourierResourceType;
import com.teamresourceful.resourcefulbees.common.logistics.CourierRoute;
import com.teamresourceful.resourcefulbees.common.logistics.CourierRouteExecutionMode;
import com.teamresourceful.resourcefulbees.common.logistics.courier.CourierAssignment;
import com.teamresourceful.resourcefulbees.common.logistics.courier.CourierDeathNotifications;
import com.teamresourceful.resourcefulbees.common.logistics.courier.CourierDeliveryController;
import com.teamresourceful.resourcefulbees.common.logistics.courier.CourierState;
import com.teamresourceful.resourcefulbees.mixin.common.BeeEntityAccessor;
import com.teamresourceful.resourcefulbees.mixin.common.BeeGoToHiveGoalInvoker;
import com.teamresourceful.resourcefulbees.mixin.common.BeeInvoker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SculkSpreader;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

public class ResourcefulBee extends CustomBeeEntity {

    private @Nullable AuraHandler auraHandler;

    public int entityFlower = 0;

    private boolean wasColliding;
    private int numberOfMutations;
    private ModBeePollinateGoal pollinateGoal;
    private int explosiveCooldown = 0;

    private final CourierDeliveryController courierDelivery = new CourierDeliveryController();
    private @Nullable BlockPos courierControllerPos;
    private @Nullable BlockPos colonyControllerPos;
    private @Nullable UUID colonyId;
    private int courierReturnGraceTicks;

    private final ItemStacksResourceHandler courierCargo = new ItemStacksResourceHandler(1) {
        @Override
        protected int getCapacity(int index, @NonNull ItemResource resource) {
            return Math.min(16, super.getCapacity(index, resource));
        }
    };


    private final FluidStacksResourceHandler courierFluidCargo = new FluidStacksResourceHandler(1, 1_000_000) {
        @Override
        protected int getCapacity(int index, @NonNull FluidResource resource) {
            CourierAbility ability = getCourierAbility();
            return ability == null ? 0 : ability.fluidTransferLimit();
        }
    };


    private final SimpleEnergyHandler courierEnergyCargo = new SimpleEnergyHandler(1_000_000) {

        @Override
        public int insert(int amount, @NonNull TransactionContext transaction) {
            if (amount <= 0) {
                return 0;
            }

            CourierAbility ability = getCourierAbility();

            if (ability == null || ability.energyTransferLimit() <= 0) {
                return 0;
            }

            int configuredLimit = ability.energyTransferLimit();

            int available = Math.max(0, configuredLimit - getAmountAsInt());

            if (available <= 0) {
                return 0;
            }

            return super.insert(Math.min(amount, available), transaction);
        }
    };


    private final SculkSpreader sculkSpreader = SculkSpreader.createLevelSpreader();

    public ResourcefulBee(EntityType<? extends Bee> type, Level level, Identifier beeType) {
        super(type, level, beeType);
        //THIS NEEDS TO BE ONLY LOADED ON THE SERVER AS IT WOULD NOT MATCH HOW VANILLA LOADS GOALS OTHERWISE.
        if (level instanceof ServerLevel) registerConditionalGoals();
        updateAuraHandler(getBeeData());
    }

    @Override
    protected void onBeeDataChanged(@NonNull CustomBeeData previous, @NonNull CustomBeeData current) {
        updateAuraHandler(current);
    }

    private void updateAuraHandler(CustomBeeData data) {
        auraHandler = data.getTraitData().hasAuras() ? new AuraHandler(this, data) : null;
    }

    @Override
    protected void registerGoals() {
        BeeEntityAccessor accessor = (BeeEntityAccessor) this;
        this.goalSelector.addGoal(1, new ModBeeEnterHiveGoal(this));
        this.goalSelector.addGoal(3, new CourierDeliveryGoal(this));
        this.goalSelector.addGoal(4, new CourierReturnToControllerGoal(this));
        this.pollinateGoal = new ModBeePollinateGoal(this);
        this.goalSelector.addGoal(4, this.pollinateGoal);
        accessor.setPollinateGoal(new FakePollinateGoal());

        this.goalSelector.addGoal(6, new Bee.BeeLocateHiveGoal() {
            @Override
            public boolean canBeeUse() {
                return !BeeConfig.manualMode && super.canBeeUse();
            }
        });

        accessor.setGoToHiveGoal(new GoToHiveGoal());
        this.goalSelector.addGoal(6, accessor.getGoToHiveGoal());

        accessor.setGoToKnownFlowerGoal(new BeeGoToKnownFlowerGoal());
        this.goalSelector.addGoal(7, accessor.getGoToKnownFlowerGoal());
        this.goalSelector.addGoal(8, new ClingyFollowGoal(this));
        this.goalSelector.addGoal(9, !BeeConfig.manualMode ? new BeeWanderGoal(this) : new WanderWorkerGoal(this));
        this.goalSelector.addGoal(10, new FloatGoal(this));
    }

    /**
     * This method is used to register goals that require data passed into the constructor, and should only be used for that.
     * This is required because vanilla calls registerGoals() in the mob constructor so we can't get any of the data we pass in through the constructor.
     * <br>
     * <br>
     * Vanilla falls into this problem itself with the fox and rabbit too as they have a similar method.
     * The only difference is they call theirs on finalize spawn because they choose a type on spawn, but we choose it before in construction and pass it in.
     * But the premise still apply that they wait because they need data that would not be available in the normal registerGoals method.
     * <br>
     * See {@link net.minecraft.world.entity.animal.fox.Fox#setTargetGoals()}
     */
    @SuppressWarnings("JavadocReference")
    protected void registerConditionalGoals() {

        if (!getCombatData().isPassive()) {
            this.goalSelector.addGoal(0, new Bee.BeeAttackGoal(this, 1.4, true));
            this.targetSelector.addGoal(1, (new BeeAngerGoal(this)).setAlertOthers());
            this.targetSelector.addGoal(2, new Bee.BeeBecomeAngryTargetGoal(this));
        }
        if (getBreedData().hasParents()) {
            this.goalSelector.addGoal(2, new BeeBreedGoal(this, 1.0D, getBeeType()));
            this.goalSelector.addGoal(3, new BeeTemptGoal(this, 1.25D));
            this.goalSelector.addGoal(6, new FollowParentGoal(this, 1.25D));
        }
        if (getMutationData().hasMutation(level())) {
            //this.goalSelector.addGoal(5, new BeeFakeFlowerGoal(this));
            this.goalSelector.addGoal(8, new BeeMutateGoal(this));
        }
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level level) {
        return new BeePathNavigation(this, level, () -> !pollinateGoal.isPollinating());
    }

    @Override
    public boolean isHiveValid() {
        if (this.hasHive()) {
            BlockPos pos = this.getHivePos();
            if (pos != null) {
                BlockEntity blockEntity = this.level().getBlockEntity(pos);
                return blockEntity instanceof TieredBeehiveBlockEntity hive && hive.isAllowedBee() || blockEntity instanceof BeeHolderBlockEntity beeHolderBlock && beeHolderBlock.isAllowedBee(this)
                        || blockEntity instanceof BeehiveBlockEntity;
            }
        }
        return false;
    }

    private void notifyColonyOfCourierDeath() {
        if (!(level() instanceof ServerLevel serverLevel)
                || colonyControllerPos == null
                || colonyId == null) {
            return;
        }

        CourierDeathNotifications.get(serverLevel).recordDeath(getUUID(), colonyId, colonyControllerPos);

        // Immediately process the notification when possible.
        if (serverLevel.hasChunkAt(colonyControllerPos)
                && serverLevel.getBlockEntity(colonyControllerPos) instanceof ColonyControllerBlockEntity controller
                && colonyId.equals(controller.getColonyId())) {

            CourierDeathNotifications.get(serverLevel).process(colonyId, colonyControllerPos, controller::courierDied);
        }
    }

    @Override
    public void die(@NonNull DamageSource damageSource) {
        notifyColonyOfCourierDeath();
        super.die(damageSource);
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide()) {
            if (courierReturnGraceTicks > 0) {
                courierReturnGraceTicks--;
            }

            if (level().getGameTime() % 20 == 0) {
                reconcileCourierAssignment();
                reconcileColonyMembership();
                reconcilePendingRouteUpdate();
            }
        }

        if (getTarget() != null && getTraitData().hasDamageTypes() && getTraitData().damageTypes().contains(TraitDamageType.EXPLOSIVE)) {
            this.explosiveCooldown = 60;
        }

        if (explosiveCooldown > 0) {
            explosiveCooldown--;
        }
    }


    @Override
    public void dropOffNectar() {
        super.dropOffNectar();
        this.resetCropCounter();
    }

    @Override
    public boolean wantsToEnterHive() {
        if (isColonyMember() && getCourierAbility() != null) {
            return false;
        }

        if (((BeeEntityAccessor) this).getStayOutOfHiveCountdown() <= 0 && !this.pollinateGoal.isPollinating() && !this.hasStung() && this.getTarget() == null) {
            boolean flag = ((BeeInvoker) this).callIsTiredOfLookingForNectar() || this.hasNectar();
            return flag && !((BeeInvoker) this).callIsHiveNearFire();
        }
        return false;
    }

    private void resetCropCounter() {
        this.numberOfMutations = 0;
    }


    @Override
    public void incrementNumCropsGrownSincePollination() {
        this.numberOfMutations++;
    }

    public int getNumberOfMutations() {
        return this.numberOfMutations;
    }

//    public boolean isFakeFlowerValid() {
//        return this.fakeFlower.hasData() && WorldUtils.checkBlock(this.level(), this.fakeFlower.get(), state -> state.is(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks.FAKE_FLOWER.get()));
//    }

    public boolean isFlowerValid(@NotNull BlockPos pos) {
        if (getCoreData().hasEntityFlower()) {
            return this.level().getEntity(this.entityFlower) != null;
        }
        return WorldUtils.checkBlock(this.level(), pos, getCoreData()::isBlockFlower);
    }

    @Override
    public void makeStuckInBlock(BlockState state, @NonNull Vec3 vector) {
        if (state.is(Blocks.SWEET_BERRY_BUSH)) {
            return;
        }

        TraitAbilityRegistry registry = TraitAbilityRegistry.get();

        boolean ignoresSlowdown = getTraitData().specialAbilities().stream().map(registry::getAbility).filter(Objects::nonNull).anyMatch(ability -> ability.ignoresBlockSlowdown(this, state));

        if (!ignoresSlowdown) {
            super.makeStuckInBlock(state, vector);
        }
    }


    @Override
    protected void customServerAiStep(@NonNull ServerLevel serverLevel) {
        BeeTraitData info = getTraitData();

        if (auraHandler != null && ((this.tickCount + getId()) % BeeConfig.auraFrequency * 20) == 0) {
            auraHandler.tick();
        }

        if (!info.hasTraits() || !info.hasSpecialAbilities()) {
            super.customServerAiStep(serverLevel);
            return;
        }

        TraitAbilityRegistry registry = TraitAbilityRegistry.get();

        info.specialAbilities().stream().map(registry::getAbility).filter(Objects::nonNull).filter(TraitAbility::canRun).forEach(ability -> ability.run(this));

        super.customServerAiStep(serverLevel);
    }

    public void pollinationComplete(BlockPos flowerPos) {
        TraitAbilityRegistry registry = TraitAbilityRegistry.get();

        getTraitData().specialAbilities().stream().map(registry::getAbility).filter(Objects::nonNull).forEach(ability -> ability.onPollinationComplete(this, flowerPos));
    }

    public void addSculkCharge(BlockPos flowerPos, int charge) {
        sculkSpreader.addCursors(flowerPos.above(), charge);
    }

    public void tickSculkAbility() {
        if (level() instanceof ServerLevel serverLevel && !sculkSpreader.getCursors().isEmpty()) {
            sculkSpreader.updateCursors(serverLevel, blockPosition(), serverLevel.getRandom(), true);
        }
    }

    public void setColliding() {
        wasColliding = true;
    }

    public boolean wasColliding() {
        return wasColliding;
    }

    public boolean isPollinating() {
        return this.pollinateGoal.isPollinating();
    }

    @Override
    public boolean doHurtTarget(@NonNull ServerLevel serverLevel, @NotNull Entity entity) {
        float damage = (float) getAttributeValue(Attributes.ATTACK_DAMAGE);
        DamageSource damageSource = damageSources().sting(this);

        if (entity.hurtServer(serverLevel, damageSource, damage)) {
            EnchantmentHelper.doPostAttackEffects(serverLevel, entity, damageSource);
            if (entity instanceof LivingEntity target) {
                target.setStingerCount(target.getStingerCount() + 1);
                applyTraitEffectsAndDamage(target, getDifficultyModifier());
            }

            this.stopBeingAngry();
            ((BeeInvoker) this).callSetFlag(4, BeeConfig.beesDieFromSting && this.getCombatData().removeStingerOnAttack());
            this.playSound(SoundEvents.BEE_STING, 1.0F, 1.0F);
            return true;
        }

        return false;
    }

    private void applyTraitEffectsAndDamage(@NotNull LivingEntity target, int diffMod) {
        BeeTraitData info = getTraitData();
        if (info.hasTraits() && diffMod > 0) {
            if (info.hasDamageTypes()) {
                info.damageTypes().forEach(damageType -> {
                    if (damageType.equals(TraitDamageType.SET_ON_FIRE))
                        target.igniteForSeconds(diffMod * (float) damageType.amplifier());
                    if (damageType.equals(TraitDamageType.EXPLOSIVE))
                        this.explode(diffMod / damageType.amplifier());
                });
            }
            int duration = diffMod * 20;
            if (info.hasPotionDamageEffects()) {
                info.potionDamageEffects().forEach(effect -> target.addEffect(effect.createInstance(duration)));
            }
            if (canPoison(info)) {
                target.addEffect(new MobEffectInstance(MobEffects.POISON, duration, 0));
            }
        }
    }

    private int getDifficultyModifier() {
        return switch (this.level().getDifficulty()) {
            case EASY -> 5;
            case NORMAL -> 10;
            case HARD -> 18;
            case PEACEFUL -> 0;
        };
    }

    private boolean canPoison(BeeTraitData info) {
        return BeeConfig.beesInflictPoison && this.getCombatData().inflictsPoison() && info.canPoison();
    }

    private void explode(int radius) {
        if (level() instanceof ServerLevel serverLevel) {
            Level.ExplosionInteraction mode = ModUtils.getExplosionInteraction(serverLevel, this);

            notifyColonyOfCourierDeath();

            this.dead = true;

            serverLevel.explode(this, this.getX(), this.getY(), this.getZ(), random.nextFloat() * radius, explosiveCooldown > 0 ? Level.ExplosionInteraction.NONE : mode);

            dropCourierCargo(serverLevel);
            this.discard();

            this.spawnLingeringCloud(this.getActiveEffects().stream().map(MobEffectInstance::new).toList());
        }
    }

    private void spawnLingeringCloud(Collection<MobEffectInstance> effects) {
        if (!effects.isEmpty()) {
            AreaEffectCloud cloud = new AreaEffectCloud(this.level(), this.getX(), this.getY(), this.getZ());
            cloud.setRadius(2.5F);
            cloud.setRadiusOnUse(-0.5F);
            cloud.setWaitTime(10);
            cloud.setDuration(cloud.getDuration() / 2);
            cloud.setRadiusPerTick(-cloud.getRadius() / cloud.getDuration());
            effects.forEach(cloud::addEffect);

            this.level().addFreshEntity(cloud);
        }

    }

    @Override
    public void readAdditionalSaveData(@NonNull ValueInput input) {
        super.readAdditionalSaveData(input);
        ModConstants.LOGGER.debug(beeType.toShortString());
        ModConstants.LOGGER.debug(getStringUUID());
        colonyId = input.read("ColonyId", UUIDUtil.CODEC).orElse(null);
        colonyControllerPos = input.read("ColonyControllerPos", BlockPos.CODEC).orElse(null);
        this.numberOfMutations = input.getIntOr(DataConstants.MUTATION_COUNT, 0);
        this.sculkSpreader.load(input);
        courierDelivery.clearAssignment();
        courierControllerPos = null;

        var savedAssignment = input.read("CourierAssignment", CourierAssignment.CODEC);
        var savedControllerPos = input.read("CourierControllerPos", BlockPos.CODEC);

        if (savedAssignment.isPresent() && savedControllerPos.isPresent()) {
            courierDelivery.assign(savedAssignment.get());
            courierControllerPos = savedControllerPos.get();
        }
        courierCargo.set(0, ItemResource.EMPTY, 0);
        input.readChild("CourierCargo", courierCargo);
        courierFluidCargo.set(0, FluidResource.EMPTY, 0);
        input.readChild("CourierFluidCargo", courierFluidCargo);
        try (Transaction transaction = Transaction.openRoot()) {
            courierEnergyCargo.extract(courierEnergyCargo.getAmountAsInt(), transaction);
            transaction.commit();
        }
        input.readChild("CourierEnergyCargo", courierEnergyCargo);
        CourierState savedState;

        try {
            savedState = CourierState.valueOf(input.getStringOr("CourierState", CourierState.IDLE.name()));
        } catch (IllegalArgumentException _) {
            savedState = CourierState.IDLE;
        }

        courierDelivery.restore(courierDelivery.getAssignment(), savedState, hasCourierCargo());
    }

    @Override
    public void addAdditionalSaveData(@NotNull ValueOutput output) {
        super.addAdditionalSaveData(output);
        if (isColonyMember()) {
            output.store("ColonyId", UUIDUtil.CODEC, colonyId);
            output.store("ColonyControllerPos", BlockPos.CODEC, colonyControllerPos);
        }
        output.putInt(DataConstants.MUTATION_COUNT, getNumberOfMutations());
        this.sculkSpreader.save(output);
        CourierAssignment assignment = courierDelivery.getAssignment();

        if (assignment != null && courierControllerPos != null) {
            output.store("CourierAssignment", CourierAssignment.CODEC, assignment);
            output.store("CourierControllerPos", BlockPos.CODEC, courierControllerPos);
        }
        output.putChild("CourierCargo", courierCargo);
        output.putChild("CourierFluidCargo", courierFluidCargo);
        output.putChild("CourierEnergyCargo", courierEnergyCargo);
        output.putString("CourierState", courierDelivery.getState().name());
    }

    public void dropOffMutations() {
        this.numberOfMutations = getMutationData().count();
//        if (isFakeFlowerValid()) {
//            level().getBlockEntity(fakeFlower.get(), ModBlockEntityTypes.FAKE_FLOWER_ENTITY.get())
//                .ifPresent(entity -> entity.createPollen(this));
//        }
    }

    public class GoToHiveGoal extends BeeGoToHiveGoal {

        public GoToHiveGoal() {
            super();
        }

        @Override
        public boolean canBeeUse() {
            return getHivePos() != null && !hasHome() && wantsToEnterHive() && !((BeeGoToHiveGoalInvoker) this).callHasReachedTarget(getHivePos()) && isHiveValid();
        }

        @Override
        public void dropAndBlacklistHive() {
            if (!BeeConfig.manualMode) {
                ResourcefulBee.this.dropHive();
            }  // double check blacklist as it may need to be cleared - epic
        }
    }

    public class FakePollinateGoal extends Bee.BeePollinateGoal {

        public FakePollinateGoal() {
            super();
        }

        @Override
        public boolean isPollinating() {
            return pollinateGoal.isPollinating();
        }

        @Override
        public void stopPollinating() {
            pollinateGoal.stopPollinating();
        }
    }


    public CourierDeliveryController getCourierDelivery() {
        return courierDelivery;
    }

    public CourierState getCourierState() {
        return courierDelivery.getState();
    }

    public boolean assignCourierRoute(CourierAssignment assignment, BlockPos controllerPos) {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return false;
        }

        if (courierDelivery.isAssigned()) {
            return false;
        }

        if (getCourierAbility() == null) {
            return false;
        }

        if (!serverLevel.hasChunkAt(controllerPos)) {
            return false;
        }

        if (!(serverLevel.getBlockEntity(controllerPos) instanceof ColonyControllerBlockEntity controller)) {
            return false;
        }

        if (!controller.getColonyId().equals(assignment.colonyId())) {
            return false;
        }

        if (!belongsToColony(assignment.colonyId())) {
            return false;
        }

        if (controller.hasPendingRouteUpdate(assignment.routeId())) {
            return false;
        }

        if (!controllerPos.equals(colonyControllerPos)) {
            return false;
        }

        if (!controller.isCourierRegistered(getUUID())) {
            return false;
        }

        CourierRoute route = controller.getRoutes().get(assignment.routeId());

        if (route == null || !route.enabled() || (route.resourceType() != CourierResourceType.ITEM && route.resourceType() != CourierResourceType.FLUID && route.resourceType() != CourierResourceType.ENERGY) || route.executionMode() != CourierRouteExecutionMode.COURIER || !controller.isRouteWithinRange(route)) {
            return false;
        }

        if (hasCourierCargoOtherThan(route.resourceType())) return false;

        if (!courierDelivery.assign(assignment)) {
            return false;
        }

        courierControllerPos = controllerPos.immutable();

        if (hasCourierCargo(route.resourceType())) {
            courierDelivery.resumeDeliveryWithCargo();
        }

        if (!controller.trackCourierAssignment(getUUID(), route.id())) {
            courierDelivery.clearAssignment();
            courierControllerPos = null;
            return false;
        }

        return true;
    }

    public @Nullable CourierRoute getAssignedCourierRoute() {
        if (!(level() instanceof ServerLevel serverLevel) || courierControllerPos == null || !serverLevel.hasChunkAt(courierControllerPos)) {
            return null;
        }

        if (!(serverLevel.getBlockEntity(courierControllerPos) instanceof ColonyControllerBlockEntity controller)) {
            return null;
        }

        CourierAssignment assignment = courierDelivery.getAssignment();

        if (assignment == null || !controller.getColonyId().equals(assignment.colonyId())) {
            return null;
        }

        CourierRoute route = controller.getRoutes().get(assignment.routeId());

        return route != null
                && route.enabled()
                && (route.resourceType() == CourierResourceType.ITEM
                || route.resourceType() == CourierResourceType.FLUID
                || route.resourceType() == CourierResourceType.ENERGY)
                && route.executionMode() == CourierRouteExecutionMode.COURIER
                && controller.isRouteWithinRange(route) ? route : null;
    }


    public boolean hasInvalidCourierAssignment() {
        if (!courierDelivery.isAssigned()) {
            return false;
        }

        if (!(level() instanceof ServerLevel serverLevel)) {
            return false;
        }

        // An unloaded controller is temporarily unavailable,
        // not proof that the assignment is invalid.
        if (courierControllerPos == null) {
            return true;
        }

        if (!serverLevel.hasChunkAt(courierControllerPos)) {
            return false;
        }

        if (!(serverLevel.getBlockEntity(courierControllerPos) instanceof ColonyControllerBlockEntity controller)) {
            return true;
        }

        CourierAssignment assignment = courierDelivery.getAssignment();

        if (assignment == null || !controller.getColonyId().equals(assignment.colonyId()) || !belongsToColony(assignment.colonyId()) || !courierControllerPos.equals(colonyControllerPos) || !controller.isCourierRegistered(getUUID())) {
            return true;
        }

        CourierRoute route = controller.getRoutes().get(assignment.routeId());

        return route == null || !route.enabled() || route.executionMode() != CourierRouteExecutionMode.COURIER || !controller.isRouteWithinRange(route);
    }


    public void clearCourierAssignment() {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }

        CourierAssignment assignment = courierDelivery.getAssignment();
        BlockPos controllerPos = courierControllerPos;

        if (assignment != null
                && controllerPos != null
                && serverLevel.hasChunkAt(controllerPos)
                && serverLevel.getBlockEntity(controllerPos)
                instanceof ColonyControllerBlockEntity controller
                && controller.getColonyId().equals(assignment.colonyId())) {

            controller.untrackCourierAssignment(getUUID());
        }

        courierDelivery.clearAssignment();
        courierControllerPos = null;
    }


    public boolean completeCourierReturn() {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return false;
        }

        if (courierDelivery.getState() != CourierState.RETURNING) {
            return false;
        }

        CourierAssignment assignment = courierDelivery.getAssignment();

        if (assignment == null || courierControllerPos == null) {
            return false;
        }

        if (!serverLevel.hasChunkAt(courierControllerPos)) {
            return false;
        }

        if (!(serverLevel.getBlockEntity(courierControllerPos)
                instanceof ColonyControllerBlockEntity controller)) {
            return false;
        }

        if (!controller.getColonyId().equals(assignment.colonyId())) {
            return false;
        }

        if (hasCourierCargo()) {
            return false;
        }

        if (controller.hasPendingRouteUpdate(assignment.routeId())) {
            return false;
        }

        if (controller.shouldReleaseCourierAssignment(
                getUUID(),
                assignment.routeId()
        )) {
            if (!controller.untrackCourierAssignment(getUUID())) {
                return false;
            }

            courierDelivery.clearAssignment();
            courierControllerPos = null;
            getNavigation().stop();

            return true;
        }

        return courierDelivery.returnCompleted();
    }


    public void reconcileCourierAssignment() {
        if (!(level() instanceof ServerLevel) || !courierDelivery.isAssigned() || !hasInvalidCourierAssignment()) {
            return;
        }

        // Clear the invalid assignment, but preserve existing cargo.
        clearCourierAssignment();
        getNavigation().stop();
    }

    private void reconcileColonyMembership() {
        if (!(level() instanceof ServerLevel serverLevel) || colonyId == null || colonyControllerPos == null) {
            return;
        }

        // An unloaded chunk does not mean the colony was destroyed.
        if (!serverLevel.hasChunkAt(colonyControllerPos)) {
            return;
        }

        if (!(serverLevel.getBlockEntity(colonyControllerPos) instanceof ColonyControllerBlockEntity controller) || !colonyId.equals(controller.getColonyId())) {

            // Abandon the old colony, including any active assignment.
            leaveColony(colonyId);
        }
    }


    public ResourceHandler<ItemResource> getCourierCargo() {
        return courierCargo;
    }


    @Override
    protected void dropCustomDeathLoot(@NonNull ServerLevel level, @NonNull DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
        dropCourierCargo(level);
    }

    private void dropCourierCargo(ServerLevel level) {
        for (int slot = 0; slot < courierCargo.size(); slot++) {
            ItemResource resource = courierCargo.getResource(slot);
            int amount = courierCargo.getAmountAsInt(slot);

            if (amount <= 0 || resource.isEmpty()) {
                continue;
            }

            ItemStack stack = resource.toStack(amount);

            try (Transaction transaction = Transaction.openRoot()) {
                int extracted = courierCargo.extract(slot, resource, amount, transaction);

                if (extracted != amount) {
                    continue;
                }

                transaction.commit();
            }

            spawnAtLocation(level, stack);
        }
    }

    public boolean hasCourierAbility() {
        return getTraitData().specialAbilities().contains(TraitConstants.COURIER);
    }

    public @Nullable CourierAbility getCourierAbility() {
        if (!hasCourierAbility()) {
            return null;
        }

        TraitAbility ability = TraitAbilityRegistry.get().getAbility(TraitConstants.COURIER);

        return ability instanceof CourierAbility courier ? courier : null;
    }

    public boolean isColonyMember() {
        return colonyId != null && colonyControllerPos != null;
    }

    public boolean belongsToColony(UUID id) {
        return isColonyMember() && colonyId.equals(id);
    }

    public @Nullable UUID getColonyId() {
        return colonyId;
    }

    public @Nullable BlockPos getColonyControllerPos() {
        return colonyControllerPos;
    }

    public boolean transferColony(UUID expectedOldColonyId, BlockPos expectedOldControllerPos, UUID newColonyId, BlockPos newControllerPos) {
        if (!(level() instanceof ServerLevel serverLevel) || !isAlive() || getCourierAbility() == null || !isColonyMember() || !belongsToColony(expectedOldColonyId) || !expectedOldControllerPos.equals(colonyControllerPos) || !serverLevel.hasChunkAt(newControllerPos)) {
            return false;
        }

        if (!(serverLevel.getBlockEntity(newControllerPos) instanceof ColonyControllerBlockEntity destination)) {
            return false;
        }

        if (!destination.getColonyId().equals(newColonyId) || !destination.isWithinRange(blockPosition())) {
            return false;
        }

        // Do not clear cargo: only invalidate the old route.
        clearCourierAssignment();
        getNavigation().stop();

        colonyId = newColonyId;
        colonyControllerPos = newControllerPos.immutable();

        return true;
    }


    public boolean joinColony(UUID id, BlockPos controllerPos) {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return false;
        }

        if (getCourierAbility() == null) {
            return false;
        }

        if (!serverLevel.hasChunkAt(controllerPos)) {
            return false;
        }

        if (!(serverLevel.getBlockEntity(controllerPos) instanceof ColonyControllerBlockEntity controller)) {
            return false;
        }

        if (!controller.getColonyId().equals(id)) {
            return false;
        }

        if (!controller.isWithinRange(blockPosition())) {
            return false;
        }

        if (isColonyMember()) {
            return colonyId.equals(id) && colonyControllerPos.equals(controllerPos);
        }

        colonyId = id;
        colonyControllerPos = controllerPos.immutable();

        return true;
    }

    public boolean leaveColony(UUID id) {
        if (!(level() instanceof ServerLevel)) {
            return false;
        }

        if (!belongsToColony(id)) {
            return false;
        }

        if (courierDelivery.isAssigned()) {
            clearCourierAssignment();
        }

        colonyId = null;
        colonyControllerPos = null;

        return true;
    }

    public ResourceHandler<FluidResource> getCourierFluidCargo() {
        return courierFluidCargo;
    }

    public boolean hasItemCourierCargo() {
        for (int i = 0; i < courierCargo.size(); i++) {
            if (courierCargo.getAmountAsInt(i) > 0) return true;
        }
        return false;
    }

    public boolean hasFluidCourierCargo() {
        for (int i = 0; i < courierFluidCargo.size(); i++) {
            if (courierFluidCargo.getAmountAsInt(i) > 0) return true;
        }
        return false;
    }

    public EnergyHandler getCourierEnergyCargo() {
        return courierEnergyCargo;
    }

    public boolean hasEnergyCourierCargo() {
        return courierEnergyCargo.getAmountAsInt() > 0;
    }

    public boolean hasCourierCargoOtherThan(CourierResourceType type) {
        return switch (type) {
            case ITEM -> hasFluidCourierCargo() || hasEnergyCourierCargo();
            case FLUID -> hasItemCourierCargo() || hasEnergyCourierCargo();
            case ENERGY -> hasItemCourierCargo() || hasFluidCourierCargo();
        };
    }

    public boolean hasCourierCargo(CourierResourceType type) {
        return switch (type) {
            case ITEM -> hasItemCourierCargo();
            case FLUID -> hasFluidCourierCargo();
            case ENERGY -> courierEnergyCargo.getAmountAsInt() > 0;
        };
    }

    public boolean hasCourierCargo() {
        return hasItemCourierCargo() || hasFluidCourierCargo() || hasEnergyCourierCargo();
    }

    public void delayCourierReturn(int ticks) {
        courierReturnGraceTicks = Math.max(courierReturnGraceTicks, Math.max(0, ticks));
    }

    public boolean canBeginCourierReturn() {
        return courierReturnGraceTicks <= 0;
    }

    private void reconcilePendingRouteUpdate() {
        if (!(level() instanceof ServerLevel serverLevel)
                || !courierDelivery.isAssigned()
                || courierDelivery.getState()
                != CourierState.WAITING_FOR_ROUTE_UPDATE
                || hasCourierCargo()) {
            return;
        }

        CourierAssignment assignment = courierDelivery.getAssignment();

        if (assignment == null
                || courierControllerPos == null
                || !serverLevel.hasChunkAt(courierControllerPos)) {
            return;
        }

        if (!(serverLevel.getBlockEntity(courierControllerPos)
                instanceof ColonyControllerBlockEntity controller)) {
            return;
        }

        if (!controller.getColonyId().equals(assignment.colonyId())
                || !controller.isCourierRegistered(getUUID())) {
            return;
        }

        if (controller.hasPendingRouteUpdate(assignment.routeId())) {
            controller.acknowledgeRouteUpdate(
                    assignment.routeId(),
                    getUUID()
            );
        } else {
            courierDelivery.resumeAfterRouteUpdate();
        }
    }
}