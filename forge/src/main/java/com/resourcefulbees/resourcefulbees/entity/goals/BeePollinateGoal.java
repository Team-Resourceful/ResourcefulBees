package com.resourcefulbees.resourcefulbees.entity.goals;

import com.resourcefulbees.resourcefulbees.entity.passive.CustomBeeEntity;
import com.resourcefulbees.resourcefulbees.mixin.BeeEntityAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.vector.Vector3d;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class BeePollinateGoal extends Goal {

    private int pollinationTicks = 0;
    private int lastPollinationTick = 0;
    private boolean running;
    private Vector3d nextTarget;
    private int ticks = 0;
    private final CustomBeeEntity bee;
    private Vector3d boundingBox;
    private MutableBoundingBox box = null;

    public BeePollinateGoal(CustomBeeEntity beeEntity) {
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        this.bee = beeEntity;
    }

    public boolean canBeeStart() {
        if (bee.remainingCooldownBeforeLocatingNewFlower > 0) {
            return false;
        } else if (bee.hasNectar()) {
            return false;
        } else if (bee.getRandom().nextFloat() < 0.7F) {
            return false;
        } else if (bee.tickCount < 20 || bee.tickCount % 5 == 0) {
            if (bee.savedFlowerPos == null) {
                Optional<BlockPos> optional = this.findFlower(5.0D, bee.getBeeData().hasEntityFlower(), bee.getBeeData().getEntityFlower());
                if (optional.isPresent()) {
                    bee.savedFlowerPos = optional.get();
                    bee.getNavigation().moveTo((double) bee.savedFlowerPos.getX() + 0.5D, (double) bee.savedFlowerPos.getY() + 0.5D, (double) bee.savedFlowerPos.getZ() + 0.5D, 1.2D);
                    return true;
                }
            }else {
                bee.getNavigation().moveTo((double) bee.savedFlowerPos.getX() + 0.5D, (double) bee.savedFlowerPos.getY() + 0.5D, (double) bee.savedFlowerPos.getZ() + 0.5D, 1.2D);
                return true;
            }
        }
        return false;
    }

    public boolean canBeeContinue() {
        if (!this.running) {
            return false;
        } else if (bee.savedFlowerPos == null) {
            return false;
        } else if (this.completedPollination()) {
            return bee.getRandom().nextFloat() < 0.2F;
        } else if (!bee.isFlowerValid(bee.savedFlowerPos)) {
            bee.savedFlowerPos = null;
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
    public boolean canUse() {
        return !bee.isAngry() && this.canBeeStart();
    }

    @Override
    public boolean canContinueToUse() {
        return !bee.isAngry() && this.canBeeContinue();
    }


    @Override
    public void start() {
        this.pollinationTicks = 0;
        this.ticks = 0;
        this.lastPollinationTick = 0;
        this.running = true;
        bee.resetTicksWithoutNectarSinceExitingHive();
        super.start();
    }

    @Override
    public void stop() {
        if (this.completedPollination()) {
            bee.resetTicksWithoutNectarSinceExitingHive();
            ((BeeEntityAccessor) bee).callSetFlag(8, true);
        }

        this.running = false;
        bee.getNavigation().stop();
        bee.remainingCooldownBeforeLocatingNewFlower = 200;
    }

    public void clearTask() {
        bee.savedFlowerPos = null;
        bee.setFlowerEntityID(-1);
        boundingBox = null;
    }

    @Override
    public void tick() {
        ++this.ticks;
        if (this.ticks > 600) {
            this.clearTask();
        } else if ((!bee.getBeeData().hasEntityFlower() || bee.getFlowerEntityID() >= 0)) {
            if (bee.tickCount % 5 == 0 && bee.getBeeData().hasEntityFlower()) {
                Entity flowerEntity = bee.level.getEntity(bee.getFlowerEntityID());
                if (flowerEntity != null) {
                    boundingBox = new Vector3d(flowerEntity.getBoundingBox().getCenter().x(), flowerEntity.getBoundingBox().maxY, flowerEntity.getBoundingBox().getCenter().z());
                    bee.setSavedFlowerPos(flowerEntity.blockPosition());
                } else this.clearTask();
            }
            if (bee.savedFlowerPos != null) {
                Vector3d vector3d = Vector3d.atBottomCenterOf(bee.savedFlowerPos).add(0.0D, 0.6F, 0.0D);
                if (boundingBox != null) vector3d = boundingBox.add(0.0D, 0.4F, 0.0D);
                if (vector3d.distanceTo(bee.position()) > 0.5D) {
                    this.nextTarget = vector3d;
                    this.moveToNextTarget();
                } else {
                    if (this.nextTarget == null) {
                        this.nextTarget = vector3d;
                    }

                    boolean closeToTarget = bee.position().distanceTo(this.nextTarget) <= 0.1D;
                    boolean shouldMoveToNewTarget = true;
                    if (!closeToTarget && this.ticks > 600) {
                        this.clearTask();
                    } else {
                        if (closeToTarget) {
                            if (bee.getRandom().nextInt(25) == 0) {
                                this.nextTarget = new Vector3d(vector3d.x() + this.getRandomOffset(), vector3d.y(), vector3d.z() + this.getRandomOffset());
                                bee.getNavigation().stop();
                            } else {
                                shouldMoveToNewTarget = false;
                            }

                            bee.getLookControl().setLookAt(vector3d.x(), vector3d.y(), vector3d.z());
                        }

                        if (shouldMoveToNewTarget) {
                            this.moveToNextTarget();
                        }

                        ++this.pollinationTicks;
                        if (bee.getRandom().nextFloat() < 0.05F && this.pollinationTicks > this.lastPollinationTick + 60) {
                            this.lastPollinationTick = this.pollinationTicks;
                            bee.playSound(SoundEvents.BEE_POLLINATE, 1.0F, 1.0F);
                        }

                    }
                }
            }
        }
    }

    private void moveToNextTarget() {
        bee.getMoveControl().setWantedPosition(this.nextTarget.x(), this.nextTarget.y(), this.nextTarget.z(), (float) 0.5);
    }

    private double getRandomOffset() {
        return ((double) bee.getRandom().nextFloat() * 2.0D - 1.0D) * 0.33333334D;
    }

    public Optional<BlockPos> findFlower(double range, boolean isEntity, ResourceLocation entityRegistryName) {
        BlockPos blockpos = bee.blockPosition();

        if (!isEntity) {
            if (box == null)
                box = MutableBoundingBox.createProper(blockpos.getX() + 5, blockpos.getY() + 5, blockpos.getZ() + 5, blockpos.getX() - 5, blockpos.getY() - 5, blockpos.getZ() - 5);
            else {
                box.x1 = blockpos.getX() + 5;
                box.y1 = blockpos.getY() + 5;
                box.z1 = blockpos.getZ() + 5;
                box.x0 = blockpos.getX() - 5;
                box.y0 = blockpos.getY() - 5;
                box.z0 = blockpos.getZ() - 5;
            }
            return BlockPos.betweenClosedStream(box).filter(getFlowerBlockPredicate()).findAny();
        } else {
            List<Entity> entityList = bee.level.getEntities(bee, (new AxisAlignedBB(bee.blockPosition())).inflate(range),
                    entity -> entity.getEncodeId() != null && entity.getEncodeId().equals(entityRegistryName.toString()));
            if (!entityList.isEmpty()) {
                Entity firstEntity = entityList.get(0);
                bee.setFlowerEntityID(firstEntity.getId());
                return Optional.of(firstEntity.blockPosition());
            }
        }

        return Optional.empty();
    }

    public Predicate<BlockPos> getFlowerBlockPredicate() {
        return pos -> {
            if (bee.level != null && bee.getBeeData().hasBlockFlowers()){
                return bee.getBeeData().getBlockFlowers().contains(bee.level.getBlockState(pos).getBlock());
            }
            return false;
        };
    }
}
