package com.dungeonderps.resourcefulbees.entity.passive;

import com.dungeonderps.resourcefulbees.data.BeeData;
import com.dungeonderps.resourcefulbees.entity.goals.BeeAngerGoal;
import com.dungeonderps.resourcefulbees.entity.goals.BeeBreedGoal;
import com.dungeonderps.resourcefulbees.entity.goals.BeeTemptGoal;
import com.dungeonderps.resourcefulbees.lib.BeeConstants;
import com.dungeonderps.resourcefulbees.registry.RegistryHandler;
import com.dungeonderps.resourcefulbees.tileentity.ApiaryTileEntity;
import com.dungeonderps.resourcefulbees.tileentity.TieredBeehiveTileEntity;
import com.dungeonderps.resourcefulbees.utils.BeeInfoUtils;
import com.dungeonderps.resourcefulbees.utils.BeeValidator;
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
import net.minecraft.village.PointOfInterest;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.world.Difficulty;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;

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

        String flower = getBeeInfo().getFlower();

        if (BeeValidator.TAG_RESOURCE_PATTERN.matcher(flower).matches()) {
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

    private int numberOfMutations;

    public ResourcefulBee(EntityType<? extends BeeEntity> type, World world) {
        super(type, world);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new BeeEntity.StingGoal(this, 1.4, true));
        this.goalSelector.addGoal(1, new EnterBeehiveGoal2());
        this.goalSelector.addGoal(2, new BeeBreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new BeeTemptGoal(this, 1.25D, false));
        this.pollinateGoal = new ResourcefulBee.PollinateGoal2();
        this.goalSelector.addGoal(4, this.pollinateGoal);
        this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.25D));
        this.goalSelector.addGoal(5, new ResourcefulBee.UpdateBeehiveGoal2());
        this.findBeehiveGoal = new ResourcefulBee.FindBeehiveGoal2();
        this.goalSelector.addGoal(5, this.findBeehiveGoal);
        this.findFlowerGoal = new BeeEntity.FindFlowerGoal();
        this.goalSelector.addGoal(6, this.findFlowerGoal);
        this.goalSelector.addGoal(7, new ResourcefulBee.FindPollinationTargetGoal2());
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
                        || blockEntity instanceof ApiaryTileEntity && ((ApiaryTileEntity) blockEntity).isAllowedBee();
            } else
                return false;
        }
    }

    public boolean doesHiveHaveSpace(BlockPos pos) {
        TileEntity blockEntity = this.world.getTileEntity(pos);
        return (blockEntity instanceof TieredBeehiveTileEntity && !((TieredBeehiveTileEntity) blockEntity).isFullOfBees())
                || (blockEntity instanceof ApiaryTileEntity && !((ApiaryTileEntity) blockEntity).isFullOfBees());
    }

    @Override
    public void onHoneyDelivered() {
        super.onHoneyDelivered();
        this.resetCropCounter();
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
        if (rand.nextInt(1) == 0) {
            for (int i = 1; i <= 2; ++i) {
                BlockPos beePosDown = this.getPosition().down(i);
                BlockState state = world.getBlockState(beePosDown);
                Block block = state.getBlock();
                if (validFillerBlock(block)) {
                    world.playEvent(2005, beePosDown, 0);
                    String mutationOutput = getBeeInfo().getMutationOutput();
                    Block newBlock = BeeInfoUtils.getBlock(mutationOutput);
                    if (newBlock != null)
                        world.setBlockState(beePosDown, newBlock.getDefaultState());
                    addCropCounter();
                }
            }
        }
    }

    public boolean validFillerBlock(Block block) {
        String baseBlock = this.getBeeInfo().getMutationInput();
        if (BeeValidator.TAG_RESOURCE_PATTERN.matcher(baseBlock).matches()) {
            ITag<Block> blockTag = BeeInfoUtils.getBlockTag(baseBlock.replace(BeeConstants.TAG_PREFIX, ""));
            return blockTag != null && block.isIn(blockTag);
        }
        ResourceLocation testBlock = block.getRegistryName();
        ResourceLocation fillerBlock = BeeInfoUtils.getResource(baseBlock);
        return testBlock != null && testBlock.equals(fillerBlock);
    }

    @Override
    public boolean isFlowers(@Nonnull BlockPos pos) {
        String flower = getBeeInfo().getFlower();

        if (BeeValidator.TAG_RESOURCE_PATTERN.matcher(flower).matches()) {
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
                    return this.world.isBlockPresent(pos) && this.world.getBlockState(pos).getBlock().equals(BeeInfoUtils.getBlock(flower));
            }
        }
    }

    public Predicate<BlockState> getFlowerPredicate() {
        return flowerPredicate;
    }

    protected void updateAITasks() {
        if (getBeeInfo().isEnderBee()) {
            if (this.world.isDaytime() && this.ticksExisted % 150 == 0) {
                this.teleportRandomly();
            }
        }
        if (getBeeInfo().isBlazeBee()) {
            if (this.ticksExisted % 150 == 0)
                this.setFire(3);
        }
        super.updateAITasks();
    }

    protected void teleportRandomly() {
        if (!this.world.isRemote() && this.isAlive()) {
            double d0 = this.getPosX() + (this.rand.nextDouble() - 0.5D) * 4.0D;
            double d1 = this.getPosY() + (double) (this.rand.nextInt(4) - 2);
            double d2 = this.getPosZ() + (this.rand.nextDouble() - 0.5D) * 4.0D;
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
        boolean flag = entityIn.attackEntityFrom(DamageSource.causeBeeStingDamage(this), getBeeInfo().getAttackDamage());
        if (flag) {
            this.applyEnchantments(this, entityIn);
            if (entityIn instanceof LivingEntity) {
                ((LivingEntity) entityIn).setBeeStingCount(((LivingEntity) entityIn).getBeeStingCount() + 1);
                int i = 0;
                if (this.world.getDifficulty() == Difficulty.NORMAL) {
                    i = 10;
                } else if (this.world.getDifficulty() == Difficulty.HARD) {
                    i = 18;
                }
                BeeData info = this.getBeeInfo();

                if (info.isCreeperBee()) {
                    this.explode();
                }
                if (info.isBlazeBee()) {
                    entityIn.setFire(5);
                }
                if (i > 0) {
                    if (info.isWitherBee())
                        ((LivingEntity) entityIn).addPotionEffect(new EffectInstance(Effects.WITHER, i * 20, 1));
                    if (info.isZomBee())
                        ((LivingEntity) entityIn).addPotionEffect(new EffectInstance(Effects.HUNGER, i * 20, 20));
                    if (info.isPigmanBee())
                        ((LivingEntity) entityIn).addPotionEffect(new EffectInstance(Effects.MINING_FATIGUE, i * 20, 0));
                    if (!info.isPigmanBee() && !info.isZomBee() && !info.isWitherBee() && !info.isBlazeBee() && !info.isCreeperBee())
                        ((LivingEntity) entityIn).addPotionEffect(new EffectInstance(Effects.POISON, i * 20, 0));
                }
            }

            this.setHasStung(true);
            this.setAttackTarget(null);
            this.playSound(SoundEvents.ENTITY_BEE_STING, 1.0F, 1.0F);
        }

        return flag;
    }

    private void explode() {
        if (!this.world.isRemote) {
            Explosion.Mode explosion$mode = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.world, this) ? Explosion.Mode.BREAK : Explosion.Mode.NONE;
            this.dead = true;
            this.world.createExplosion(this, this.getPosX(), this.getPosY(), this.getPosZ(), rand.nextFloat() * 3, explosion$mode);
            this.remove();
            this.spawnLingeringCloud();
        }

    }

    private void spawnLingeringCloud() {
        Collection<EffectInstance> collection = this.getActivePotionEffects();
        if (!collection.isEmpty()) {
            AreaEffectCloudEntity areaeffectcloudentity = new AreaEffectCloudEntity(this.world, this.getPosX(), this.getPosY(), this.getPosZ());
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
            BlockPos blockpos = ResourcefulBee.this.getPosition();
            PointOfInterestManager pointofinterestmanager = ((ServerWorld) world).getPointOfInterestManager();
            Stream<PointOfInterest> stream = pointofinterestmanager.func_219146_b(pointOfInterestType ->
                            pointOfInterestType == RegistryHandler.TIERED_BEEHIVE_POI.get(), blockpos,
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
            if (ResourcefulBee.this.getCropsGrownSincePollination() >= getBeeInfo().getMutationCount()) {
                return false;
            } else if (ResourcefulBee.this.rand.nextFloat() < 0.3F) {
                return false;
            } else {
                return ResourcefulBee.this.hasNectar() && ResourcefulBee.this.isHiveValid();
            }
        }

        @Override
        public void tick() {
            if (!getBeeInfo().getMutationInput().isEmpty() && !getBeeInfo().getMutationOutput().isEmpty())
                applyPollinationEffect();
        }
    }

    public class FindBeehiveGoal2 extends BeeEntity.FindBeehiveGoal {

        public FindBeehiveGoal2() {
            super();
        }

        @Override
        public boolean canBeeStart() {
            return hivePos != null && !detachHome() && canEnterHive() && !this.isCloseEnough(hivePos)
                    && isHiveValid();
        }
    }

    public class PollinateGoal2 extends BeeEntity.PollinateGoal {

        public PollinateGoal2() {
            super();
        }

        @Nonnull
        @Override
        public Optional<BlockPos> getFlower() {
            return this.findFlower(getFlowerPredicate(), 5.0D);
        }
    }


}
