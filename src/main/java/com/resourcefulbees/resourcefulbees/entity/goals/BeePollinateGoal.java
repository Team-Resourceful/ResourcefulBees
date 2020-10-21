package com.resourcefulbees.resourcefulbees.entity.goals;

import com.resourcefulbees.resourcefulbees.entity.passive.CustomBeeEntity;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.mixin.BeeEntityAccessor;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import com.resourcefulbees.resourcefulbees.utils.validation.ValidatorUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.vector.Vector3d;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

public class BeePollinateGoal extends Goal {

    private int pollinationTicks = 0;
    private int lastPollinationTick = 0;
    private boolean running;
    private Vector3d nextTarget;
    private int ticks = 0;
    private final CustomBeeEntity bee;
    private final String flower;
    private Vector3d boundingBox;
    private MutableBoundingBox box = null;

    public BeePollinateGoal(CustomBeeEntity beeEntity, String flowerData) {
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
        this.bee = beeEntity;
        this.flower = flowerData;
    }

    public boolean canBeeStart() {
        if (bee.ticksUntilCanPollinate > 0) {
            return false;
        } else if (bee.hasNectar()) {
            return false;
        } else if (bee.getRNG().nextFloat() < 0.7F) {
            return false;
        } else {
            Optional<BlockPos> optional = this.findFlower(5.0D, flower.startsWith(BeeConstants.ENTITY_PREFIX), flower.replace(BeeConstants.ENTITY_PREFIX, ""));
            if (optional.isPresent()) {
                bee.flowerPos = optional.get();
                bee.getNavigator().tryMoveToXYZ(bee.flowerPos.getX() + 0.5D, bee.flowerPos.getY() + 0.5D, bee.flowerPos.getZ() + 0.5D, 1.2D);
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean canBeeContinue() {
        if (!this.running) {
            return false;
        } else if (bee.flowerPos == null) {
            return false;
        } else if (this.completedPollination()) {
            return bee.getRNG().nextFloat() < 0.2F;
        } else if (bee.ticksExisted % 20 == 0 && !bee.isFlowers(bee.flowerPos)) {
            bee.flowerPos = null;
            return false;
        } else {
            return true;
        }
    }

    private boolean completedPollination() {
        return this.pollinationTicks > 400;
    }

    public boolean isRunning() {
        return this.running;
    }

    public void cancel() {
        this.clearTask();
        this.running = false;
    }

    @Override
    public boolean shouldExecute() {
        return this.canBeeStart() && !bee.hasAngerTime();
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.canBeeContinue() && !bee.hasAngerTime();
    }


    public void startExecuting() {
        this.pollinationTicks = 0;
        this.ticks = 0;
        this.lastPollinationTick = 0;
        this.running = true;
        bee.resetPollinationTicks();
    }

    public void resetTask() {
        if (this.completedPollination()) {
            bee.resetPollinationTicks();
            ((BeeEntityAccessor)bee).callSetBeeFlag(8, true);
        }

        this.running = false;
        bee.getNavigator().clearPath();
        bee.ticksUntilCanPollinate = 200;
    }

    public void clearTask(){
        bee.flowerPos = null;
        bee.setFlowerEntityID(-1);
        boundingBox = null;
    }

    public void tick() {
        ++this.ticks;
        if (this.ticks > 600) {
            this.clearTask();
        } else if ((!flower.startsWith(BeeConstants.ENTITY_PREFIX) || bee.getFlowerEntityID() >= 0)) {
            if (this.ticks % 5 == 0 && flower.startsWith(BeeConstants.ENTITY_PREFIX)){
                Entity flowerEntity = bee.world.getEntityByID(bee.getFlowerEntityID());
                if (flowerEntity != null){
                    boundingBox = new Vector3d(flowerEntity.getBoundingBox().getCenter().getX(), flowerEntity.getBoundingBox().maxY, flowerEntity.getBoundingBox().getCenter().getZ());
                    bee.setFlowerPos(flowerEntity.getBlockPos());
                }
                else this.clearTask();
            }
            if (bee.flowerPos != null) {
                Vector3d vector3d = Vector3d.ofBottomCenter(bee.flowerPos).add(0.0D, 0.6F, 0.0D);
                if (boundingBox != null) vector3d = boundingBox.add(0.0D, 0.4F, 0.0D);
                if (vector3d.distanceTo(bee.getPositionVec()) > 0.5D) {
                    this.nextTarget = vector3d;
                    this.moveToNextTarget();
                } else {
                    if (this.nextTarget == null) {
                        this.nextTarget = vector3d;
                    }

                    boolean closeToTarget = bee.getPositionVec().distanceTo(this.nextTarget) <= 0.1D;
                    boolean shouldMoveToNewTraget = true;
                    if (!closeToTarget && this.ticks > 600) {
                        this.clearTask();
                    } else {
                        if (closeToTarget) {
                            if (bee.getRNG().nextInt(25) == 0) {
                                this.nextTarget = new Vector3d(vector3d.getX() + this.getRandomOffset(), vector3d.getY(), vector3d.getZ() + this.getRandomOffset());
                                bee.getNavigator().clearPath();
                            } else {
                                shouldMoveToNewTraget = false;
                            }

                            bee.getLookController().setLookPosition(vector3d.getX(), vector3d.getY(), vector3d.getZ());
                        }

                        if (shouldMoveToNewTraget) {
                            this.moveToNextTarget();
                        }

                        ++this.pollinationTicks;
                        if (bee.getRNG().nextFloat() < 0.05F && this.pollinationTicks > this.lastPollinationTick + 60) {
                            this.lastPollinationTick = this.pollinationTicks;
                            bee.playSound(SoundEvents.ENTITY_BEE_POLLINATE, 1.0F, 1.0F);
                        }

                    }
                }
            }
        }
    }

    private void moveToNextTarget() {
        bee.getMoveHelper().setMoveTo(this.nextTarget.getX(), this.nextTarget.getY(), this.nextTarget.getZ(), (float) 0.5);
    }

    private double getRandomOffset() { return ((double)bee.getRNG().nextFloat() * 2.0D - 1.0D) * 0.33333334D; }

    public Optional<BlockPos> findFlower(double range, boolean isEntity, String entityRegistryName) {

        BlockPos blockpos = bee.getBlockPos();
        BlockPos.Mutable mutableBlockPos = new BlockPos.Mutable(0,0,0);

        if (!isEntity) {
            if (box == null)
                box = MutableBoundingBox.createProper(blockpos.getX() + 5 , blockpos.getY() + 5 , blockpos.getZ() + 5, blockpos.getX() - 5, blockpos.getY() - 5, blockpos.getZ() - 5);
            else {
                box.maxX = blockpos.getX() + 5;
                box.maxY = blockpos.getY() + 5;
                box.maxZ = blockpos.getZ() + 5;
                box.minX = blockpos.getX() - 5;
                box.minY = blockpos.getY() - 5;
                box.minZ = blockpos.getZ() - 5;
            }
            AtomicReference<Double> lastDistance = new AtomicReference<>(100.0D);
            BlockPos.stream(box).filter(getFlowerBlockPredicate()).forEach(blockPos -> {
                double currDistance = blockpos.distanceSq(blockPos);
                if (currDistance < lastDistance.get()){
                    lastDistance.set(currDistance);
                    mutableBlockPos.setPos(blockPos);
                }
            });
            return Optional.of(mutableBlockPos);
        } else {
            List<Entity> entityList = bee.world.getEntitiesInAABBexcluding(bee, (new AxisAlignedBB(bee.getBlockPos())).grow(range),
                    (entity) -> entity.getEntityString() != null && entity.getEntityString().equals(entityRegistryName));
            if (!entityList.isEmpty()){
                Entity firstEntity = entityList.get(0);
                bee.setFlowerEntityID(firstEntity.getEntityId());
                return Optional.of(firstEntity.getBlockPos());
            }
        }

        return Optional.empty();
    }

    public Predicate<BlockPos> getFlowerBlockPredicate() {
        return pos -> {

            if (bee.world != null) {
                BlockState state = bee.world.getBlockState(pos);

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
            } else return false;
        };
    }
}
