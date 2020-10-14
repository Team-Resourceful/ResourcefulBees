package com.resourcefulbees.resourcefulbees.entity.passive;

import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.api.beedata.TraitData;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.entity.goals.BeeAngerGoal;
import com.resourcefulbees.resourcefulbees.entity.goals.BeeBreedGoal;
import com.resourcefulbees.resourcefulbees.entity.goals.BeeTemptGoal;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.lib.TraitConstants;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.registry.RegistryHandler;
import com.resourcefulbees.resourcefulbees.tileentity.TieredBeehiveTileEntity;
import com.resourcefulbees.resourcefulbees.tileentity.multiblocks.apiary.ApiaryTileEntity;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import com.resourcefulbees.resourcefulbees.utils.validation.ValidatorUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.PointOfInterest;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ResourcefulBee extends CustomBeeEntity {

    protected final Predicate<BlockState> flowerPredicate = state -> {

        String flower = getBeeData().getFlower();

        if (ValidatorUtils.TAG_RESOURCE_PATTERN.matcher(flower).matches()) {
            ITag<Block> blockTag = BeeInfoUtils.getBlockTag(flower.replace(BeeConstants.TAG_PREFIX, ""));
            return blockTag != null && state.isIn(blockTag);
        } else {
            switch (flower) {
                case BeeConstants.FLOWER_TAG_ALL:
                    return state.isIn(BlockTags.TALL_FLOWERS)
                            ? state.getBlock() != Blocks.SUNFLOWER
                            || state.get(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER
                            : state.isIn(BlockTags.SMALL_FLOWERS);
                case BeeConstants.FLOWER_TAG_SMALL:
                    return state.isIn(BlockTags.SMALL_FLOWERS);
                case BeeConstants.FLOWER_TAG_TALL:
                    return state.isIn(BlockTags.TALL_FLOWERS) && (state.getBlock() != Blocks.SUNFLOWER || state.get(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER);
                default:
                    return state.getBlock().equals(BeeInfoUtils.getBlock(flower));
            }
        }
    };

    private boolean wasColliding;
    private int numberOfMutations;

    public ResourcefulBee(EntityType<? extends BeeEntity> type, World world, CustomBeeData beeData) {
        super(type, world, beeData);
    }

    @Override
    protected void registerGoals() {

        String namespaceID = this.getEntityString();
        assert namespaceID != null;
        String beeType = namespaceID.substring(namespaceID.lastIndexOf(":") + 1, namespaceID.length() - 4);
        CustomBeeData customBeeData = BeeRegistry.getRegistry().getBeeData(beeType);

        this.goalSelector.addGoal(0, new BeeEntity.StingGoal(this, 1.4, true));
        this.goalSelector.addGoal(1, new EnterBeehiveGoal2());
        if (customBeeData.getBreedData().isBreedable()) {
            this.goalSelector.addGoal(2, new BeeBreedGoal(this, 1.0D));
            this.goalSelector.addGoal(3, new BeeTemptGoal(this, 1.25D, false));
        }
        this.pollinateGoal = new ResourcefulBee.PollinateGoal2();
        this.goalSelector.addGoal(4, this.pollinateGoal);
        this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.25D));
        this.goalSelector.addGoal(5, new ResourcefulBee.UpdateBeehiveGoal2());
        this.moveToHiveGoal = new ResourcefulBee.FindBeehiveGoal2();
        this.goalSelector.addGoal(5, this.moveToHiveGoal);
        this.moveToFlowerGoal = new BeeEntity.FindFlowerGoal();
        this.goalSelector.addGoal(6, this.moveToFlowerGoal);
        if(customBeeData.getMutationData().hasMutation()) {
            this.goalSelector.addGoal(7, new ResourcefulBee.FindPollinationTargetGoal2());
        }
        this.goalSelector.addGoal(8, new BeeEntity.WanderGoal());
        this.goalSelector.addGoal(9, new SwimGoal(this));
        this.targetSelector.addGoal(1, (new BeeAngerGoal(this)).setCallsForHelp());
        this.targetSelector.addGoal(2, new BeeEntity.AttackPlayerGoal(this));
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
        if (this.cannotEnterHiveTicks <= 0 && !this.pollinateGoal.isRunning() && !this.hasStung() && this.getAttackTarget() == null) {
            boolean flag = this.failedPollinatingTooLong() || this.hasNectar();
            return flag && !this.isHiveNearFire();
        } else {
            return false;
        }
    }

    private int getCropsGrownSincePollination() {
        return this.numberOfMutations;
    }

    private void resetCropCounter() {
        this.numberOfMutations = 0;
    }

    public void addCropCounter() {
        ++this.numberOfMutations;
    }

    public void applyPollinationEffect() {
        if (getBeeData().getMutationData().hasMutation()) {
            for (int i = 1; i <= 2; ++i) {
                BlockPos beePosDown = this.getBlockPos().down(i);
                BlockState state = world.getBlockState(beePosDown);
                Block block = state.getBlock();
                if (validFillerBlock(block)) {
                    world.playEvent(2005, beePosDown, 0);
                    String mutationOutput = getBeeData().getMutationData().getMutationOutput();
                    Block newBlock = BeeInfoUtils.getBlock(mutationOutput);
                    if (newBlock != null)
                        world.setBlockState(beePosDown, newBlock.getDefaultState());
                    addCropCounter();
                }
            }
        }
    }

    public boolean validFillerBlock(Block block) {
        String baseBlock = this.getBeeData().getMutationData().getMutationInput();
        if (ValidatorUtils.TAG_RESOURCE_PATTERN.matcher(baseBlock).matches()) {
            ITag<Block> blockTag = BeeInfoUtils.getBlockTag(baseBlock.replace(BeeConstants.TAG_PREFIX, ""));
            return blockTag != null && block.isIn(blockTag);
        }
        ResourceLocation testBlock = block.getRegistryName();
        ResourceLocation fillerBlock = BeeInfoUtils.getResource(baseBlock);
        return testBlock != null && testBlock.equals(fillerBlock);
    }

    @Override
    public boolean isFlowers(@Nonnull BlockPos pos) {
        String flower = getBeeData().getFlower();

        if (ValidatorUtils.TAG_RESOURCE_PATTERN.matcher(flower).matches()) {
            ITag<Block> blockTag = BeeInfoUtils.getBlockTag(flower.replace(BeeConstants.TAG_PREFIX, ""));
            return blockTag != null && this.world.getBlockState(pos).getBlock().isIn(blockTag);
        } else {
            switch (flower) {
                case BeeConstants.FLOWER_TAG_ALL:
                    return this.world.isBlockPresent(pos) && this.world.getBlockState(pos).getBlock().isIn(BlockTags.FLOWERS);
                case BeeConstants.FLOWER_TAG_SMALL:
                    return this.world.isBlockPresent(pos) && this.world.getBlockState(pos).getBlock().isIn(BlockTags.SMALL_FLOWERS);
                case BeeConstants.FLOWER_TAG_TALL:
                    return this.world.isBlockPresent(pos) && this.world.getBlockState(pos).getBlock().isIn(BlockTags.TALL_FLOWERS);
                default:
                    return this.world.isBlockPresent(pos) && BeeInfoUtils.isValidBlock(BeeInfoUtils.getBlock(flower)) && this.world.getBlockState(pos).getBlock().equals(BeeInfoUtils.getBlock(flower));
            }
        }
    }

    public Predicate<BlockState> getFlowerPredicate() {
        return flowerPredicate;
    }

    protected void updateAITasks() {
        TraitData info = getBeeData().getTraitData();

        if (info.hasSpecialAbilities()) {
            info.getSpecialAbilities().forEach(ability -> {
                if (ability.equals(TraitConstants.TELEPORT)) {
                    if (!hasHiveInRange() && !this.pollinateGoal.isRunning()) {
                        if (this.world.isDaytime() && this.ticksExisted % 150 == 0) {
                            this.teleportRandomly();
                        }
                    }
                }
                if (ability.equals(TraitConstants.FLAMMABLE)) {
                    if (this.ticksExisted % 150 == 0)
                        this.setFire(3);
                }
                if (ability.equals(TraitConstants.SLIMY)) {
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
            });
        }
        super.updateAITasks();
    }

    protected boolean hasHiveInRange() {
        return this.hasHive() && this.canEnterHive() && this.hivePos != null && this.hivePos.withinDistance(this.getPositionVec(), 5.0D);
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
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(x, y, z);

        while (blockpos$mutable.getY() > 0 && !this.world.getBlockState(blockpos$mutable).getMaterial().blocksMovement()) {
            blockpos$mutable.move(Direction.DOWN);
        }

        BlockState blockstate = this.world.getBlockState(blockpos$mutable);
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
    public boolean attackEntityAsMob(@Nonnull Entity entityIn) {
        TraitData info = this.getBeeData().getTraitData();
        boolean flag = entityIn.attackEntityFrom(DamageSource.sting(this), getBeeData().getAttackDamage());
        if (flag) {
            this.applyEnchantments(this, entityIn);
            if (entityIn instanceof LivingEntity) {
                ((LivingEntity) entityIn).setStingerCount(((LivingEntity) entityIn).getStingerCount() + 1);
            }
        }
        if (entityIn instanceof LivingEntity) {
            int i = 0;
            switch (this.world.getDifficulty()){
                case EASY: i = 5; break;
                case NORMAL: i = 10; break;
                case HARD: i = 18; break;
            }
            if (info.hasDamageTypes()){
                for (Pair<String, Integer> damageType : info.getDamageTypes()){
                    if (damageType.getLeft().equals(TraitConstants.SET_ON_FIRE)) entityIn.setFire(i * damageType.getRight());
                    if (damageType.getLeft().equals(TraitConstants.EXPLOSIVE)) this.explode(i/damageType.getRight());
                }
            }
            if (info.hasDamagePotionEffects()){
                if (i > 0) {
                    for (Pair<Effect, Integer> effect : info.getPotionDamageEffects()){
                        ((LivingEntity) entityIn).addPotionEffect(new EffectInstance(effect.getLeft(), i * 20, effect.getRight()));
                    }
                }
            }
            if (!info.hasDamagePotionEffects() && !info.hasDamageTypes()) ((LivingEntity) entityIn).addPotionEffect(new EffectInstance(Effects.POISON, i * 20, 0));
        }
        this.setAttackTarget(null);
        if (!this.getBeeType().equals(BeeConstants.OREO_BEE)) {
            this.setHasStung(Config.BEE_DIES_FROM_STING.get());
            this.playSound(SoundEvents.ENTITY_BEE_STING, 1.0F, 1.0F);
        }

        return flag;
    }

    private void explode(int radius) {
        if (!this.world.isRemote) {
            Explosion.Mode explosion$mode = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.world, this) ? Explosion.Mode.BREAK : Explosion.Mode.NONE;
            this.dead = true;
            this.world.createExplosion(this, this.getX(), this.getY(), this.getZ(), rand.nextFloat() * radius, explosion$mode);
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

    public class EnterBeehiveGoal2 extends BeeEntity.EnterBeehiveGoal {
        public EnterBeehiveGoal2() {
        }

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

        public boolean canBeeContinue() {
            return false;
        }

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

    protected class UpdateBeehiveGoal2 extends BeeEntity.UpdateBeehiveGoal {

        public UpdateBeehiveGoal2() {
            super();
        }

        @Nonnull
        public List<BlockPos> getNearbyFreeHives() {
            BlockPos blockpos = ResourcefulBee.this.getBlockPos();
            PointOfInterestManager pointofinterestmanager = ((ServerWorld) world).getPointOfInterestManager();
            Stream<PointOfInterest> stream = pointofinterestmanager.func_219146_b(pointOfInterestType ->
                            pointOfInterestType == RegistryHandler.TIERED_BEEHIVE_POI.get() || pointOfInterestType == PointOfInterestType.BEE_NEST || pointOfInterestType == PointOfInterestType.BEEHIVE, blockpos,
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
            if (ResourcefulBee.this.getCropsGrownSincePollination() >= getBeeData().getMutationData().getMutationCount()) {
                return false;
            } else if (ResourcefulBee.this.rand.nextFloat() < 0.3F) {
                return false;
            } else {
                return ResourcefulBee.this.hasNectar() && ResourcefulBee.this.isHiveValid();
            }
        }

        @Override
        public void tick() {
            if (getBeeData().getMutationData().hasMutation() && !getBeeData().getMutationData().getMutationInput().isEmpty() && !getBeeData().getMutationData().getMutationOutput().isEmpty())
                applyPollinationEffect();
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

    public class PollinateGoal2 extends BeeEntity.PollinateGoal {

        public PollinateGoal2() {
            super();
        }

        @Override
        public boolean canBeeStart() {
            if (ResourcefulBee.this.ticksUntilCanPollinate > 0) {
                return false;
            } else if (ResourcefulBee.this.hasNectar()) {
                return false;
            } else if (ResourcefulBee.this.rand.nextFloat() < 0.7F) {
                return false;
            } else {
                Optional<BlockPos> optional = this.getFlower();
                if (optional.isPresent()) {
                    ResourcefulBee.this.flowerPos = optional.get();
                    ResourcefulBee.this.navigator.tryMoveToXYZ((double)ResourcefulBee.this.flowerPos.getX() + 0.5D, (double)ResourcefulBee.this.flowerPos.getY() + 0.5D, (double)ResourcefulBee.this.flowerPos.getZ() + 0.5D, 1.2F);
                    return true;
                } else {
                    return false;
                }
            }
        }

        @Nonnull
        @Override
        public Optional<BlockPos> getFlower() {
            return this.findFlower(getFlowerPredicate(), 5.0D);
        }

        @Override
        public boolean isRunning() {
            return super.isRunning();
        }
    }


}
