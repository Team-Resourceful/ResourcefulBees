package com.resourcefulbees.resourcefulbees.entity.passive;

import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.api.beedata.MutationData;
import com.resourcefulbees.resourcefulbees.api.beedata.TraitData;
import com.resourcefulbees.resourcefulbees.api.beedata.mutation.outputs.BlockOutput;
import com.resourcefulbees.resourcefulbees.api.beedata.mutation.outputs.EntityOutput;
import com.resourcefulbees.resourcefulbees.api.beedata.mutation.outputs.ItemOutput;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.entity.goals.BeeAngerGoal;
import com.resourcefulbees.resourcefulbees.entity.goals.BeeBreedGoal;
import com.resourcefulbees.resourcefulbees.entity.goals.BeeTemptGoal;
import com.resourcefulbees.resourcefulbees.entity.goals.PollinateGoal;
import com.resourcefulbees.resourcefulbees.lib.NBTConstants;
import com.resourcefulbees.resourcefulbees.lib.TraitConstants;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.registry.ModEffects;
import com.resourcefulbees.resourcefulbees.tileentity.TieredBeehiveTileEntity;
import com.resourcefulbees.resourcefulbees.tileentity.multiblocks.apiary.ApiaryTileEntity;
import com.resourcefulbees.resourcefulbees.utils.RandomCollection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ResourcefulBee extends CustomBeeEntity {

    private boolean wasColliding;
    private int numberOfMutations;
    private PollinateGoal pollinateGoal;
    private int explosiveCooldown = 0;

    public ResourcefulBee(EntityType<? extends Bee> type, Level world, CustomBeeData beeData) {
        super(type, world, beeData);
    }


    @Override
    protected void registerGoals() {

        String namespaceID = this.getEncodeId();
        assert namespaceID != null;
        String beeType = namespaceID.substring(namespaceID.lastIndexOf(":") + 1, namespaceID.length() - 4);
        CustomBeeData customBeeData = BeeRegistry.getRegistry().getBeeData(beeType);

        if (!customBeeData.getCombatData().isPassive()) {
            this.goalSelector.addGoal(0, new Bee.BeeAttackGoal(this, 1.4, true));
            this.targetSelector.addGoal(1, (new BeeAngerGoal(this)).setAlertOthers());
            this.targetSelector.addGoal(2, new Bee.BeeBecomeAngryTargetGoal(this));
        }
        this.goalSelector.addGoal(1, new EnterBeehiveGoal2());

        if (customBeeData.getBreedData().isBreedable()) {
            this.goalSelector.addGoal(2, new BeeBreedGoal(this, 1.0D));
            this.goalSelector.addGoal(3, new BeeTemptGoal(this, 1.25D));
            this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.25D));
        }
        this.pollinateGoal = new PollinateGoal(this);
        this.goalSelector.addGoal(4, this.pollinateGoal);

        this.beePollinateGoal = new FakePollinateGoal();

        this.goalSelector.addGoal(5, new ResourcefulBee.UpdateBeehiveGoal2());

        this.goToHiveGoal = new ResourcefulBee.FindBeehiveGoal2();
        this.goalSelector.addGoal(5, this.goToHiveGoal);

        this.goToKnownFlowerGoal = new Bee.BeeGoToKnownFlowerGoal();
        this.goalSelector.addGoal(6, this.goToKnownFlowerGoal);

        if (customBeeData.getMutationData().hasMutation()) {
            this.goalSelector.addGoal(7, new ResourcefulBee.FindPollinationTargetGoal2());
        }
        if (!Config.MANUAL_MODE.get()) this.goalSelector.addGoal(8, new BeeWanderGoal());
        this.goalSelector.addGoal(9, new FloatGoal(this));
    }

    @Override
    public boolean isHiveValid() {
        if (!this.hasHive()) {
            return false;
        } else {
            BlockPos pos = this.hivePos;
            if (pos != null) {
                BlockEntity blockEntity = this.level.getBlockEntity(this.hivePos);
                return blockEntity instanceof TieredBeehiveTileEntity && ((TieredBeehiveTileEntity) blockEntity).isAllowedBee()
                        || blockEntity instanceof ApiaryTileEntity && ((ApiaryTileEntity) blockEntity).isAllowedBee()
                        || blockEntity instanceof BeehiveBlockEntity;
            } else
                return false;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (explosiveCooldown > 0) explosiveCooldown--;
    }

    public void setExplosiveCooldown(int cooldown) {
        this.explosiveCooldown = cooldown;
    }

    @Override
    public void dropOffNectar() {
        super.dropOffNectar();
        this.resetCropCounter();
    }

    @Override
    public boolean wantsToEnterHive() {
        if (this.stayOutOfHiveCountdown <= 0 && !this.pollinateGoal.isPollinating() && !this.hasStung() && this.getTarget() == null) {
            boolean flag = this.isTiredOfLookingForNectar() || this.hasNectar();
            return flag && !this.isHiveNearFire();
        } else {
            return false;
        }
    }

    private void resetCropCounter() {
        this.numberOfMutations = 0;
    }

    @Override
    public void incrementNumCropsGrownSincePollination() {
        ++this.numberOfMutations;
    }

    public void applyPollinationEffect() {
        MutationData mutationData = getBeeData().getMutationData();
        if (mutationData.hasMutation()) {
            if (mutationData.hasEntityMutations() && mutateEntity()) return;

            for (int i = 1; i <= 2; ++i) {
                BlockPos beePosDown = this.blockPosition().below(i);
                BlockState state = level.getBlockState(beePosDown);

                if (mutateBlock(mutationData, state, beePosDown)) {
                    break;
                }
            }
        }
    }

    public boolean mutateEntity() {
        AABB box = this.getMutationBoundingBox();
        List<Entity> entityList = this.level.getEntities(this, box, entity ->
                getBeeData().getMutationData().getEntityMutations().get(entity.getType()) != null);
        if (!entityList.isEmpty()) {
            Map<EntityType<?>, Pair<Double, RandomCollection<EntityOutput>>> entityMutations = getBeeData().getMutationData().getEntityMutations();
            Pair<Double, RandomCollection<EntityOutput>> output = entityMutations.get(entityList.get(0).getType());

            if (output != null) {
                if (output.getLeft() >= level.random.nextFloat()) {
                    EntityOutput entityOutput = output.getRight().next();
                    CompoundTag nbt = new CompoundTag();
                    nbt.put("EntityTag", entityOutput.getCompoundNBT());
                    entityOutput.getEntityType().spawn((ServerLevel) level, nbt, null, null, entityList.get(0).blockPosition(), MobSpawnType.NATURAL, false, false);
                    entityList.get(0).remove();
                    level.levelEvent(2005, this.blockPosition().below(1), 0);
                }
                incrementNumCropsGrownSincePollination();
                return true;
            }
        }
        return false;
    }

    public boolean mutateBlock(MutationData mutationData, BlockState state, BlockPos blockPos) {
        Block block = state.getBlock();
        if (mutationData.getBlockMutations().containsKey(block)) {
            mutateBlock(mutationData.getBlockMutations().get(block), blockPos);
            incrementNumCropsGrownSincePollination();
            return true;
        }

        if (mutationData.getItemMutations().containsKey(block)) {
            mutateItem(mutationData.getItemMutations().get(block), blockPos);
            incrementNumCropsGrownSincePollination();
            return true;
        }
        return false;
    }

    private void mutateBlock(Pair<Double, RandomCollection<BlockOutput>> output, BlockPos blockPos) {
        if (output.getLeft() >= level.random.nextFloat()) {
            BlockOutput blockOutput = output.getRight().next();
            level.setBlockAndUpdate(blockPos, blockOutput.getBlock().defaultBlockState());
            if (!blockOutput.getCompoundNBT().isEmpty()) {
                BlockEntity tile = level.getBlockEntity(blockPos);
                CompoundTag nbt = blockOutput.getCompoundNBT();
                if (tile != null) {
                    nbt.putInt("x", blockPos.getX());
                    nbt.putInt("y", blockPos.getY());
                    nbt.putInt("z", blockPos.getZ());
                    tile.load(blockOutput.getBlock().defaultBlockState(), nbt);
                }
            }
        }
    }

    private void mutateItem(Pair<Double, RandomCollection<ItemOutput>> output, BlockPos blockPos) {
        if (output.getLeft() >= level.random.nextFloat()) {
            ItemOutput itemOutput = output.getRight().next();
            level.levelEvent(2005, blockPos, 0);
            level.removeBlock(blockPos, false);
            ItemStack stack = new ItemStack(itemOutput.getItem());
            stack.setTag(itemOutput.getCompoundNBT());
            level.addFreshEntity(new ItemEntity(level, blockPos.getX(), blockPos.getY(), blockPos.getZ(), stack));
        }
    }

    private AABB getMutationBoundingBox() {
        return getBoundingBox().expandTowards(new Vec3(0, -2, 0));
    }

    @Override
    public boolean isFlowerValid(@NotNull BlockPos pos) {
        if (getBeeData().hasEntityFlower()) {
            return this.level.getEntity(this.getFlowerEntityID()) != null;
        } else if (getBeeData().hasBlockFlowers()) {
            return this.level.isLoaded(pos) && getBeeData().getBlockFlowers().contains(this.level.getBlockState(pos).getBlock());
        }
        return false;
    }

    @Override
    public void makeStuckInBlock(BlockState state, @NotNull Vec3 vector) {
        TraitData info = getBeeData().getTraitData();
        boolean isSpider = info.hasSpecialAbilities() && info.getSpecialAbilities().contains(TraitConstants.SPIDER);
        if (state.is(Blocks.COBWEB) && isSpider) return;
        super.makeStuckInBlock(state, vector);
    }

    @Override
    protected void customServerAiStep() {
        TraitData info = getBeeData().getTraitData();

        if (info.hasTraits() && info.hasSpecialAbilities()) {
            info.getSpecialAbilities().forEach(ability -> {
                switch (ability) {
                    case TraitConstants.TELEPORT:
                        doTeleportEffect();
                        break;
                    case TraitConstants.FLAMMABLE:
                        doFlameEffect();
                        break;
                    case TraitConstants.SLIMY:
                        doSlimeEffect();
                        break;
                    case TraitConstants.ANGRY:
                        doAngryEffect();
                        break;
                    default:
                        //do nothing
                }
            });
        }
        super.customServerAiStep();
    }

    private void doAngryEffect() {
        if (getEffect(ModEffects.CALMING.get()) == null) {
            Entity player = level.getNearestPlayer(getEntity(), 20);
            setPersistentAngerTarget(player != null ? player.getUUID() : null);
            setRemainingPersistentAngerTime(1000);
        }
    }

    private void doTeleportEffect() {
        if (canTeleport() && !hasHiveInRange() && !getDisruptorInRange()) {
            this.teleportRandomly();
        }
    }

    private void doFlameEffect() {
        if (this.tickCount % 150 == 0) this.setSecondsOnFire(3);
    }

    private void doSlimeEffect() {
        if (!checkSpawnObstruction(level) && !wasColliding) {
            for (int j = 0; j < 8; ++j) {
                float f = this.random.nextFloat() * ((float) Math.PI * 2F);
                float f1 = this.random.nextFloat() * 0.5F + 0.5F;
                float f2 = Mth.sin(f) * 1 * 0.5F * f1;
                float f3 = Mth.cos(f) * 1 * 0.5F * f1;
                this.level.addParticle(ParticleTypes.ITEM_SLIME, this.getX() + (double) f2, this.getY(), this.getZ() + (double) f3, 0.0D, 0.0D, 0.0D);
            }

            this.playSound(SoundEvents.SLIME_SQUISH, this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) / 0.8F);
            wasColliding = true;
        }
    }

    private boolean canTeleport() {
        return !hasCustomName() && this.tickCount % 150 == 0 && this.level.isDay() && !this.pollinateGoal.isPollinating();
    }

    protected void teleportRandomly() {
        if (!this.level.isClientSide() && this.isAlive()) {
            double d0 = this.getX() + (this.random.nextDouble() - 0.5D) * 4.0D;
            double d1 = this.getY() + (double) (this.random.nextInt(4) - 2);
            double d2 = this.getZ() + (this.random.nextDouble() - 0.5D) * 4.0D;
            this.teleportToPos(d0, d1, d2);
        }
    }

    private void teleportToPos(double x, double y, double z) {
        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos(x, y, z);

        while (blockPos.getY() > 0 && !this.level.getBlockState(blockPos).getMaterial().blocksMotion()) {
            blockPos.move(Direction.DOWN);
        }

        BlockState blockstate = this.level.getBlockState(blockPos);
        boolean canMove = blockstate.getMaterial().blocksMotion();
        boolean water = blockstate.getFluidState().is(FluidTags.WATER);
        if (canMove && !water) {
            EnderTeleportEvent event = new EnderTeleportEvent(this, x, y, z, 0);
            if (MinecraftForge.EVENT_BUS.post(event)) return;
            boolean teleported = this.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true);
            if (teleported) {
                this.level.playSound(null, this.xo, this.yo, this.zo, SoundEvents.ENDERMAN_TELEPORT, this.getSoundSource(), 1.0F, 1.0F);
                this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
            }
        }
    }

    @Override
    public boolean doHurtTarget(@NotNull Entity entityIn) {
        float damage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        TraitData info = this.getBeeData().getTraitData();
        boolean flag = entityIn.hurt(DamageSource.sting(this), damage);
        if (flag && this.getBeeData().getCombatData().removeStingerOnAttack()) {
            this.doEnchantDamageEffects(this, entityIn);
            if (entityIn instanceof LivingEntity) {
                ((LivingEntity) entityIn).setStingerCount(((LivingEntity) entityIn).getStingerCount() + 1);
            }
        }
        if (entityIn instanceof LivingEntity) {
            int i = getDifficultyModifier();
            if (info.hasTraits() && info.hasDamageTypes()) {
                info.getDamageTypes().forEach(stringIntegerPair -> {
                    if (stringIntegerPair.getLeft().equals(TraitConstants.SET_ON_FIRE))
                        entityIn.setSecondsOnFire(i * stringIntegerPair.getRight());
                    if (stringIntegerPair.getLeft().equals(TraitConstants.EXPLOSIVE))
                        this.explode(i / stringIntegerPair.getRight());
                });
            }
            if (i > 0 && info.hasTraits() && info.hasDamagePotionEffects()) {
                info.getPotionDamageEffects().forEach(effectIntegerPair -> ((LivingEntity) entityIn).addEffect(new MobEffectInstance(effectIntegerPair.getLeft(), i * 20, effectIntegerPair.getRight())));
            }
            if (canPoison(info))
                ((LivingEntity) entityIn).addEffect(new MobEffectInstance(MobEffects.POISON, i * 20, 0));
        }

        this.setTarget(null);

        this.setHasStung(Config.BEE_DIES_FROM_STING.get() && this.getBeeData().getCombatData().removeStingerOnAttack());
        this.playSound(SoundEvents.BEE_STING, 1.0F, 1.0F);

        return flag;
    }

    private int getDifficultyModifier() {
        switch (this.level.getDifficulty()) {
            case EASY:
                return 5;
            case NORMAL:
                return 10;
            case HARD:
                return 18;
            default:
                return 0;
        }
    }

    private boolean canPoison(TraitData info) {
        return (Config.BEES_INFLICT_POISON.get() && this.getBeeData().getCombatData().inflictsPoison()) && info.hasTraits() && !info.hasDamagePotionEffects() && !info.hasDamageTypes();
    }

    private void explode(int radius) {
        if (!this.level.isClientSide) {
            Explosion.BlockInteraction mode = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level, this) ? Explosion.BlockInteraction.BREAK : Explosion.BlockInteraction.NONE;
            this.dead = true;
            this.level.explode(this, this.getX(), this.getY(), this.getZ(), random.nextFloat() * radius, explosiveCooldown > 0 ? Explosion.BlockInteraction.NONE : mode);
            this.remove();
            this.spawnLingeringCloud();
        }
    }

    private void spawnLingeringCloud() {
        Collection<MobEffectInstance> collection = this.getActiveEffects();
        if (!collection.isEmpty()) {
            AreaEffectCloud areaeffectcloudentity = new AreaEffectCloud(this.level, this.getX(), this.getY(), this.getZ());
            areaeffectcloudentity.setRadius(2.5F);
            areaeffectcloudentity.setRadiusOnUse(-0.5F);
            areaeffectcloudentity.setWaitTime(10);
            areaeffectcloudentity.setDuration(areaeffectcloudentity.getDuration() / 2);
            areaeffectcloudentity.setRadiusPerTick(-areaeffectcloudentity.getRadius() / (float) areaeffectcloudentity.getDuration());

            for (MobEffectInstance effectinstance : collection) {
                areaeffectcloudentity.addEffect(new MobEffectInstance(effectinstance));
            }

            this.level.addFreshEntity(areaeffectcloudentity);
        }

    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.numberOfMutations = compound.getInt(NBTConstants.NBT_MUTATION_COUNT);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt(NBTConstants.NBT_MUTATION_COUNT, this.numberOfMutations);
    }

    public class EnterBeehiveGoal2 extends Bee.BeeEnterHiveGoal {
        public EnterBeehiveGoal2() {
            //constructor
        }

        @Override
        public boolean canBeeUse() {
            if (ResourcefulBee.this.hasHive() && ResourcefulBee.this.wantsToEnterHive() && ResourcefulBee.this.hivePos != null && ResourcefulBee.this.hivePos.closerThan(ResourcefulBee.this.position(), 2.0D)) {
                BlockEntity blockEntity = ResourcefulBee.this.level.getBlockEntity(ResourcefulBee.this.hivePos);
                if (blockEntity instanceof BeehiveBlockEntity) {
                    BeehiveBlockEntity beehiveBlockEntity = (BeehiveBlockEntity) blockEntity;
                    if (!beehiveBlockEntity.isFull()) {
                        return true;
                    }

                    ResourcefulBee.this.hivePos = null;
                } else if (blockEntity instanceof ApiaryTileEntity) {
                    ApiaryTileEntity apiaryTileEntity = (ApiaryTileEntity) blockEntity;
                    if (apiaryTileEntity.hasSpace()) {
                        return true;
                    }

                    ResourcefulBee.this.hivePos = null;
                }
            }

            return false;
        }

        @Override
        public boolean canBeeContinueToUse() {
            return false;
        }

        @Override
        public void start() {
            if (ResourcefulBee.this.hivePos != null) {
                BlockEntity blockEntity = ResourcefulBee.this.level.getBlockEntity(ResourcefulBee.this.hivePos);
                if (blockEntity != null) {
                    if (blockEntity instanceof BeehiveBlockEntity) {
                        BeehiveBlockEntity beehiveBlockEntity = (BeehiveBlockEntity) blockEntity;
                        beehiveBlockEntity.addOccupant(ResourcefulBee.this, ResourcefulBee.this.hasNectar());
                    } else if (blockEntity instanceof ApiaryTileEntity) {
                        ApiaryTileEntity apiaryTileEntity = (ApiaryTileEntity) blockEntity;
                        apiaryTileEntity.tryEnterHive(ResourcefulBee.this, ResourcefulBee.this.hasNectar(), false);
                    }
                }
            }
        }
    }

    @Override
    public ItemStack getPickedResult(HitResult target) {
        return new ItemStack(beeData.getSpawnEggItemRegistryObject().get());
    }

    protected class FindPollinationTargetGoal2 extends Bee.BeeGrowCropGoal {

        public FindPollinationTargetGoal2() {
            super();
        }

        @Override
        public boolean canBeeUse() {
            if (ResourcefulBee.this.numberOfMutations >= getBeeData().getMutationData().getMutationCount()) {
                return false;
            } else if (ResourcefulBee.this.random.nextFloat() < 0.3F) {
                return false;
            } else {
                return ResourcefulBee.this.hasNectar();
            }
        }

        @Override
        public void tick() {
            if (ResourcefulBee.this.tickCount % 5 == 0) {
                MutationData mutationData = getBeeData().getMutationData();
                if (mutationData.hasMutation() && (mutationData.hasBlockMutations() || mutationData.hasItemMutations() || mutationData.hasEntityMutations())) {
                    applyPollinationEffect();
                }
            }
        }
    }

    public class FindBeehiveGoal2 extends Bee.BeeGoToHiveGoal {

        public FindBeehiveGoal2() {
            super();
        }

        @Override
        public boolean canBeeUse() {
            return hivePos != null && !hasRestriction() && wantsToEnterHive() && !this.hasReachedTarget(hivePos) && isHiveValid();
        }

        @Override
        public void dropHive() {
            if (!Config.MANUAL_MODE.get()) {
                super.dropHive();
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

    public class UpdateBeehiveGoal2 extends Bee.BeeLocateHiveGoal {
        public UpdateBeehiveGoal2() {
            super();
        }

        @Override
        public boolean canBeeUse() {
            if (!Config.MANUAL_MODE.get()) {
                return super.canBeeUse();
            }
            return false;
        }
    }
}
