package com.resourcefulbees.resourcefulbees.entity.passive;

import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.api.beedata.MutationData;
import com.resourcefulbees.resourcefulbees.api.beedata.TraitData;
import com.resourcefulbees.resourcefulbees.api.beedata.mutation.outputs.BlockOutput;
import com.resourcefulbees.resourcefulbees.api.beedata.mutation.outputs.EntityOutput;
import com.resourcefulbees.resourcefulbees.api.beedata.mutation.outputs.ItemOutput;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.registry.ModEffects;
import com.resourcefulbees.resourcefulbees.entity.goals.*;
import com.resourcefulbees.resourcefulbees.lib.NBTConstants;
import com.resourcefulbees.resourcefulbees.lib.TraitConstants;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.registry.ModPOIs;
import com.resourcefulbees.resourcefulbees.tileentity.TieredBeehiveTileEntity;
import com.resourcefulbees.resourcefulbees.tileentity.multiblocks.apiary.ApiaryTileEntity;
import com.resourcefulbees.resourcefulbees.utils.RandomCollection;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.village.PointOfInterest;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ResourcefulBee extends CustomBeeEntity {

    private boolean wasColliding;
    private int numberOfMutations;
    private BeePollinateGoal beePollinateGoal;

    public ResourcefulBee(EntityType<? extends BeeEntity> type, World world, CustomBeeData beeData) {
        super(type, world, beeData);
    }

    @Override
    protected void registerGoals() {

        String namespaceID = this.getEntityString();
        assert namespaceID != null;
        String beeType = namespaceID.substring(namespaceID.lastIndexOf(":") + 1, namespaceID.length() - 4);
        CustomBeeData customBeeData = BeeRegistry.getRegistry().getBeeData(beeType);

        if (!customBeeData.getCombatData().isPassive()) {
            this.goalSelector.addGoal(0, new BeeEntity.StingGoal(this, 1.4, true));
            this.targetSelector.addGoal(1, (new BeeAngerGoal(this)).setCallsForHelp());
            this.targetSelector.addGoal(2, new BeeEntity.AttackPlayerGoal(this));
        }
        this.goalSelector.addGoal(1, new EnterBeehiveGoal2());

        if (customBeeData.getBreedData().isBreedable()) {
            this.goalSelector.addGoal(2, new BeeBreedGoal(this, 1.0D));
            this.goalSelector.addGoal(3, new BeeTemptGoal(this, 1.25D, false));
            this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.25D));
        }
        this.beePollinateGoal = new BeePollinateGoal(this);
        this.goalSelector.addGoal(4, this.beePollinateGoal);

        this.pollinateGoal = new FakePollinateGoal();

        this.goalSelector.addGoal(5, new ResourcefulBee.UpdateBeehiveGoal2());

        this.moveToHiveGoal = new ResourcefulBee.FindBeehiveGoal2();
        this.goalSelector.addGoal(5, this.moveToHiveGoal);

        this.moveToFlowerGoal = new BeeEntity.FindFlowerGoal();
        this.goalSelector.addGoal(6, this.moveToFlowerGoal);

        if (customBeeData.getMutationData().hasMutation()) {
            this.goalSelector.addGoal(7, new ResourcefulBee.FindPollinationTargetGoal2());
        }
        this.goalSelector.addGoal(8, new BeeWanderGoal(this));
        this.goalSelector.addGoal(9, new SwimGoal(this));
    }

    @Override
    public boolean isHiveValid() {
        if (!this.hasHive()) {
            return false;
        } else {
            BlockPos pos = this.hivePos;
            if (pos != null) {
                TileEntity blockEntity = this.world.getTileEntity(this.hivePos);
                return blockEntity instanceof TieredBeehiveTileEntity && ((TieredBeehiveTileEntity) blockEntity).isAllowedBee()
                        || blockEntity instanceof ApiaryTileEntity && ((ApiaryTileEntity) blockEntity).isAllowedBee()
                        || blockEntity instanceof BeehiveTileEntity;
            } else
                return false;
        }
    }

    public boolean doesHiveHaveSpace(BlockPos pos) {
        TileEntity blockEntity = this.world.getTileEntity(pos);
        return (blockEntity instanceof TieredBeehiveTileEntity && !((TieredBeehiveTileEntity) blockEntity).isFullOfBees())
                || (blockEntity instanceof ApiaryTileEntity && !((ApiaryTileEntity) blockEntity).isFullOfBees())
                || (blockEntity instanceof BeehiveTileEntity && !((BeehiveTileEntity) blockEntity).isFullOfBees());
    }

    @Override
    public void onHoneyDelivered() {
        super.onHoneyDelivered();
        this.resetCropCounter();
    }

    @Override
    public boolean canEnterHive() {
        if (this.cannotEnterHiveTicks <= 0 && !this.beePollinateGoal.isRunning() && !this.hasStung() && this.getAttackTarget() == null) {
            boolean flag = this.failedPollinatingTooLong() || this.hasNectar();
            return flag && !this.isHiveNearFire();
        } else {
            return false;
        }
    }

    private void resetCropCounter() {
        this.numberOfMutations = 0;
    }

    @Override
    public void addCropCounter() {
        ++this.numberOfMutations;
    }

    public void applyPollinationEffect() {
        MutationData mutationData = getBeeData().getMutationData();
        if (mutationData.hasMutation()) {
            if (mutationData.hasEntityMutations() && mutateEntity()) return;

            for (int i = 1; i <= 2; ++i) {
                BlockPos beePosDown = this.getBlockPos().down(i);
                BlockState state = world.getBlockState(beePosDown);

                if (mutateBlock(mutationData, state, beePosDown)) {
                    break;
                }
            }
        }
    }

    public boolean mutateEntity() {
        AxisAlignedBB box = this.getMutationBoundingBox();
        List<Entity> entityList = this.world.getEntitiesInAABBexcluding(this, box, entity ->
                getBeeData().getMutationData().getEntityMutations().get(entity.getType()) != null);
        if (!entityList.isEmpty()) {
            Map<EntityType<?>, Pair<Double, RandomCollection<EntityOutput>>> entityMutations = getBeeData().getMutationData().getEntityMutations();
            Pair<Double, RandomCollection<EntityOutput>> output = entityMutations.get(entityList.get(0).getType());

            if (output != null) {
                if (output.getLeft() >= world.rand.nextFloat()) {
                    EntityOutput entityOutput = output.getRight().next();
                    CompoundNBT nbt = new CompoundNBT();
                    nbt.put("EntityTag", entityOutput.getCompoundNBT());
                    entityOutput.getEntityType().spawn((ServerWorld) world, nbt, null, null, entityList.get(0).getBlockPos(), SpawnReason.NATURAL, false, false);
                    entityList.get(0).remove();
                    world.playEvent(2005, this.getBlockPos().down(1), 0);
                }
                addCropCounter();
                return true;
            }
        }
        return false;
    }

    public boolean mutateBlock(MutationData mutationData, BlockState state, BlockPos blockPos) {
        Block block = state.getBlock();
        if (mutationData.getBlockMutations().containsKey(block)) {
            mutateBlock(mutationData.getBlockMutations().get(block), blockPos);
            addCropCounter();
            return true;
        }

        if (mutationData.getItemMutations().containsKey(block)) {
            mutateItem(mutationData.getItemMutations().get(block), blockPos);
            addCropCounter();
            return true;
        }
        return false;
    }

    private void mutateBlock(Pair<Double, RandomCollection<BlockOutput>> output, BlockPos blockPos) {
        if (output.getLeft() >= world.rand.nextFloat()) {
            BlockOutput blockOutput = output.getRight().next();
            world.setBlockState(blockPos, blockOutput.getBlock().getDefaultState());
            if (!blockOutput.getCompoundNBT().isEmpty()) {
                TileEntity tile = world.getTileEntity(blockPos);
                CompoundNBT nbt = blockOutput.getCompoundNBT();
                if (tile != null) {
                    nbt.putInt("x", blockPos.getX());
                    nbt.putInt("y", blockPos.getY());
                    nbt.putInt("z", blockPos.getZ());
                    tile.fromTag(blockOutput.getBlock().getDefaultState(), nbt);
                }
            }
        }
    }

    private void mutateItem(Pair<Double, RandomCollection<ItemOutput>> output, BlockPos blockPos) {
        if (output.getLeft() >= world.rand.nextFloat()) {
            ItemOutput itemOutput = output.getRight().next();
            world.playEvent(2005, blockPos, 0);
            world.removeBlock(blockPos, false);
            ItemStack stack = new ItemStack(itemOutput.getItem());
            stack.setTag(itemOutput.getCompoundNBT());
            world.addEntity(new ItemEntity(world, blockPos.getX(), blockPos.getY(), blockPos.getZ(), stack));
        }
    }

    private AxisAlignedBB getMutationBoundingBox() {
        return getBoundingBox().expand(new Vector3d(0, -2, 0));
    }

    @Override
    public boolean isFlowers(@NotNull BlockPos pos) {
        if (getBeeData().hasEntityFlower()){
            return this.world.getEntityByID(this.getFlowerEntityID()) != null;
        }else if (getBeeData().hasBlockFlowers()) {
            return this.world.isBlockPresent(pos) && getBeeData().getBlockFlowers().contains(this.world.getBlockState(pos).getBlock());
        }
        return false;
    }

    @Override
    protected void updateAITasks() {
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
        super.updateAITasks();
    }

    private void doAngryEffect() {
        if (getActivePotionEffect(ModEffects.CALMING.get()) == null) {
            Entity player = world.getClosestPlayer(getEntity(), 20);
            setAngryAt(player != null ? player.getUniqueID() : null);
            setAngerTime(1000);
        }
    }

    private void doTeleportEffect() {
        if (canTeleport() && !hasHiveInRange() && !getDisruptorInRange()) {
            this.teleportRandomly();
        }
    }

    private void doFlameEffect() {
        if (this.ticksExisted % 150 == 0) this.setFire(3);
    }

    private void doSlimeEffect() {
        if (!isNotColliding(world) && !wasColliding) {
            for (int j = 0; j < 8; ++j) {
                float f = this.rand.nextFloat() * ((float) Math.PI * 2F);
                float f1 = this.rand.nextFloat() * 0.5F + 0.5F;
                float f2 = MathHelper.sin(f) * 1 * 0.5F * f1;
                float f3 = MathHelper.cos(f) * 1 * 0.5F * f1;
                this.world.addParticle(ParticleTypes.ITEM_SLIME, this.getX() + (double) f2, this.getY(), this.getZ() + (double) f3, 0.0D, 0.0D, 0.0D);
            }

            this.playSound(SoundEvents.ENTITY_SLIME_SQUISH, this.getSoundVolume(), ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) / 0.8F);
            wasColliding = true;
        }
    }

    private boolean canTeleport() {
        return !hasCustomName() && this.ticksExisted % 150 == 0 && this.world.isDaytime() && !this.beePollinateGoal.isRunning();
    }

    protected void teleportRandomly() {
        if (!this.world.isRemote() && this.isAlive()) {
            double d0 = this.getX() + (this.rand.nextDouble() - 0.5D) * 4.0D;
            double d1 = this.getY() + (double) (this.rand.nextInt(4) - 2);
            double d2 = this.getZ() + (this.rand.nextDouble() - 0.5D) * 4.0D;
            this.teleportTo(d0, d1, d2);
        }
    }

    private void teleportTo(double x, double y, double z) {
        BlockPos.Mutable blockPos = new BlockPos.Mutable(x, y, z);

        while (blockPos.getY() > 0 && !this.world.getBlockState(blockPos).getMaterial().blocksMovement()) {
            blockPos.move(Direction.DOWN);
        }

        BlockState blockstate = this.world.getBlockState(blockPos);
        boolean canMove = blockstate.getMaterial().blocksMovement();
        boolean water = blockstate.getFluidState().isTagged(FluidTags.WATER);
        if (canMove && !water) {
            EnderTeleportEvent event = new EnderTeleportEvent(this, x, y, z, 0);
            if (MinecraftForge.EVENT_BUS.post(event)) return;
            boolean teleported = this.attemptTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true);
            if (teleported) {
                this.world.playSound(null, this.prevPosX, this.prevPosY, this.prevPosZ, SoundEvents.ENTITY_ENDERMAN_TELEPORT, this.getSoundCategory(), 1.0F, 1.0F);
                this.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
            }
        }
    }

    @Override
    public boolean attackEntityAsMob(@NotNull Entity entityIn) {
        float damage = (float)this.getAttributeValue(Attributes.GENERIC_ATTACK_DAMAGE);
        TraitData info = this.getBeeData().getTraitData();
        boolean flag = entityIn.attackEntityFrom(DamageSource.sting(this), damage);
        if (flag && this.getBeeData().getCombatData().removeStingerOnAttack()) {
            this.applyEnchantments(this, entityIn);
            if (entityIn instanceof LivingEntity) {
                ((LivingEntity) entityIn).setStingerCount(((LivingEntity) entityIn).getStingerCount() + 1);
            }
        }
        if (entityIn instanceof LivingEntity) {
            int i = 0;
            switch (this.world.getDifficulty()) {
                case EASY:
                    i = 5;
                    break;
                case NORMAL:
                    i = 10;
                    break;
                case HARD:
                    i = 18;
                    break;
                default:
                    //do nothing
            }
            if (info.hasTraits() && info.hasDamageTypes()) {
                int finalI1 = i;
                info.getDamageTypes().forEach(stringIntegerPair -> {
                    if (stringIntegerPair.getLeft().equals(TraitConstants.SET_ON_FIRE))
                        entityIn.setFire(finalI1 * stringIntegerPair.getRight());
                    if (stringIntegerPair.getLeft().equals(TraitConstants.EXPLOSIVE)) this.explode(finalI1 / stringIntegerPair.getRight());
                });
            }
            if (i > 0 && info.hasTraits() && info.hasDamagePotionEffects()) {
                int finalI = i;
                info.getPotionDamageEffects().forEach(effectIntegerPair -> ((LivingEntity) entityIn).addPotionEffect(new EffectInstance(effectIntegerPair.getLeft(), finalI * 20, effectIntegerPair.getRight())));
            }
            if (canPoison(info))
                ((LivingEntity) entityIn).addPotionEffect(new EffectInstance(Effects.POISON, i * 20, 0));
        }

        this.setAttackTarget(null);

        this.setHasStung(Config.BEE_DIES_FROM_STING.get() && this.getBeeData().getCombatData().removeStingerOnAttack());
        this.playSound(SoundEvents.ENTITY_BEE_STING, 1.0F, 1.0F);

        return flag;
    }

    private boolean canPoison(TraitData info) {
        return (Config.BEES_INFLICT_POISON.get() && this.getBeeData().getCombatData().inflictsPoison()) && info.hasTraits() && !info.hasDamagePotionEffects() && !info.hasDamageTypes();
    }

    private void explode(int radius) {
        if (!this.world.isRemote) {
            Explosion.Mode mode = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.world, this) ? Explosion.Mode.BREAK : Explosion.Mode.NONE;
            this.dead = true;
            this.world.createExplosion(this, this.getX(), this.getY(), this.getZ(), rand.nextFloat() * radius, mode);
            this.remove();
            this.spawnLingeringCloud();
        }
    }

    private void spawnLingeringCloud() {
        Collection<EffectInstance> collection = this.getActivePotionEffects();
        if (!collection.isEmpty()) {
            AreaEffectCloudEntity areaeffectcloudentity = new AreaEffectCloudEntity(this.world, this.getX(), this.getY(), this.getZ());
            areaeffectcloudentity.setRadius(2.5F);
            areaeffectcloudentity.setRadiusOnUse(-0.5F);
            areaeffectcloudentity.setWaitTime(10);
            areaeffectcloudentity.setDuration(areaeffectcloudentity.getDuration() / 2);
            areaeffectcloudentity.setRadiusPerTick(-areaeffectcloudentity.getRadius() / (float) areaeffectcloudentity.getDuration());

            for (EffectInstance effectinstance : collection) {
                areaeffectcloudentity.addEffect(new EffectInstance(effectinstance));
            }

            this.world.addEntity(areaeffectcloudentity);
        }

    }

    @Override
    public void readAdditional(@NotNull CompoundNBT compound) {
        super.readAdditional(compound);
        this.numberOfMutations = compound.getInt(NBTConstants.NBT_MUTATION_COUNT);
    }

    @Override
    public void writeAdditional(@NotNull CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt(NBTConstants.NBT_MUTATION_COUNT, this.numberOfMutations);
    }

    public class EnterBeehiveGoal2 extends BeeEntity.EnterBeehiveGoal {
        public EnterBeehiveGoal2() {
            //constructor
        }

        @Override
        public boolean canBeeStart() {
            if (ResourcefulBee.this.hasHive() && ResourcefulBee.this.canEnterHive() && ResourcefulBee.this.hivePos != null && ResourcefulBee.this.hivePos.withinDistance(ResourcefulBee.this.getPositionVec(), 2.0D)) {
                TileEntity tileentity = ResourcefulBee.this.world.getTileEntity(ResourcefulBee.this.hivePos);
                if (tileentity instanceof BeehiveTileEntity) {
                    BeehiveTileEntity beehivetileentity = (BeehiveTileEntity) tileentity;
                    if (!beehivetileentity.isFullOfBees()) {
                        return true;
                    }

                    ResourcefulBee.this.hivePos = null;
                } else if (tileentity instanceof ApiaryTileEntity) {
                    ApiaryTileEntity beehivetileentity = (ApiaryTileEntity) tileentity;
                    if (!beehivetileentity.isFullOfBees()) {
                        return true;
                    }

                    ResourcefulBee.this.hivePos = null;
                }
            }

            return false;
        }

        @Override
        public boolean canBeeContinue() {
            return false;
        }

        @Override
        public void startExecuting() {
            if (ResourcefulBee.this.hivePos != null) {
                TileEntity tileentity = ResourcefulBee.this.world.getTileEntity(ResourcefulBee.this.hivePos);
                if (tileentity != null) {
                    if (tileentity instanceof BeehiveTileEntity) {
                        BeehiveTileEntity beehivetileentity = (BeehiveTileEntity) tileentity;
                        beehivetileentity.tryEnterHive(ResourcefulBee.this, ResourcefulBee.this.hasNectar());
                    } else if (tileentity instanceof ApiaryTileEntity) {
                        ApiaryTileEntity beehivetileentity = (ApiaryTileEntity) tileentity;
                        beehivetileentity.tryEnterHive(ResourcefulBee.this, ResourcefulBee.this.hasNectar(), false);
                    }
                }
            }
        }
    }

    @Override
    public ItemStack getPickedResult(RayTraceResult target) {
        return new ItemStack(beeData.getSpawnEggItemRegistryObject().get());
    }

    protected class UpdateBeehiveGoal2 extends BeeEntity.UpdateBeehiveGoal {

        public UpdateBeehiveGoal2() {
            super();
        }

        @Override
        public @NotNull List<BlockPos> getNearbyFreeHives() {
            BlockPos blockpos = ResourcefulBee.this.getBlockPos();
            PointOfInterestManager pointofinterestmanager = ((ServerWorld) world).getPointOfInterestManager();
            Stream<PointOfInterest> stream = pointofinterestmanager.func_219146_b(pointOfInterestType ->
                            pointOfInterestType == ModPOIs.TIERED_BEEHIVE_POI.get() || pointOfInterestType == PointOfInterestType.BEE_NEST || pointOfInterestType == PointOfInterestType.BEEHIVE, blockpos,
                    20, PointOfInterestManager.Status.ANY);
            return stream.map(PointOfInterest::getPos).filter(ResourcefulBee.this::doesHiveHaveSpace)
                    .sorted(Comparator.comparingDouble(pos -> pos.distanceSq(blockpos))).collect(Collectors.toList());
        }
    }

    protected class FindPollinationTargetGoal2 extends BeeEntity.FindPollinationTargetGoal {

        public FindPollinationTargetGoal2() {
            super();
        }

        @Override
        public boolean canBeeStart() {
            if (ResourcefulBee.this.numberOfMutations >= getBeeData().getMutationData().getMutationCount()) {
                return false;
            } else if (ResourcefulBee.this.rand.nextFloat() < 0.3F) {
                return false;
            } else {
                return ResourcefulBee.this.hasNectar();
            }
        }

        @Override
        public void tick() {
            if (ResourcefulBee.this.ticksExisted % 5 == 0) {
                MutationData mutationData = getBeeData().getMutationData();
                if (mutationData.hasMutation() && (mutationData.hasBlockMutations() || mutationData.hasItemMutations() || mutationData.hasEntityMutations())) {
                    applyPollinationEffect();
                }
            }
        }
    }

    public class FindBeehiveGoal2 extends BeeEntity.FindBeehiveGoal {

        public FindBeehiveGoal2() {
            super();
        }

        @Override
        public boolean canBeeStart() {
            return hivePos != null && !detachHome() && canEnterHive() && !this.isCloseEnough(hivePos) && isHiveValid();
        }
    }

    public class FakePollinateGoal extends BeeEntity.PollinateGoal {
        public FakePollinateGoal() {
            super();
        }

        @Override
        public boolean isRunning() {
            return beePollinateGoal.isRunning();
        }

        @Override
        public void cancel() {
            beePollinateGoal.cancel();
        }
    }

}
