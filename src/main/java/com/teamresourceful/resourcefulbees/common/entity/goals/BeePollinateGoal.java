package com.teamresourceful.resourcefulbees.common.entity.goals;

import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.entity.passive.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.common.mixin.accessors.BeeEntityAccessor;
import com.teamresourceful.resourcefulbees.common.mixin.invokers.BeeEntityInvoker;
import com.teamresourceful.resourcefulbees.common.utils.MathUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class BeePollinateGoal extends Goal {

    private int pollinationTicks;
    private int lastPollinationTick;
    private boolean running;
    private Vector3d nextTarget;
    private int ticks;
    private final CustomBeeEntity bee;
    private Vector3d boundingBox;
    private static final ArrayList<BlockPos> positionOffsets = new ArrayList<>();

    static {
        for(int i = 0; (double)i <= 5; i = i > 0 ? -i : 1 - i) {
            for(int j = 0; (double)j < 5; ++j) {
                for(int k = 0; k <= j; k = k > 0 ? -k : 1 - k) {
                    for(int l = k < j && k > -j ? j : 0; l <= j; l = l > 0 ? -l : 1 - l) {
                        positionOffsets.add(new BlockPos(k,i-1, l));
                    }
                }
            }
        }
    }

    public BeePollinateGoal(CustomBeeEntity beeEntity) {
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        this.bee = beeEntity;
    }

    public boolean canBeeStart() {
        if (((BeeEntityAccessor)bee).getRemainingCooldownBeforeLocatingNewFlower() > 0) {
            return false;
        } else if (bee.hasNectar()) {
            return false;
        } else if (bee.getRandom().nextFloat() < 0.7F) {
            return false;
        } else if ((Boolean.FALSE.equals(CommonConfig.MANUAL_MODE.get()) || bee.getCoreData().getEntityFlower().isPresent()) && bee.getSavedFlowerPos() == null && (bee.tickCount < 20 || bee.tickCount % 5 == 0)) {
            Optional<BlockPos> optional = this.findFlower(5.0D);
            if (optional.isPresent()) {
                bee.setSavedFlowerPos(optional.get());
                bee.getNavigation().moveTo(bee.getSavedFlowerPos().getX() + 0.5D, bee.getSavedFlowerPos().getY() + 0.5D, bee.getSavedFlowerPos().getZ() + 0.5D, 1.2D);
                return true;
            } else {
                ((BeeEntityAccessor)bee).setRemainingCooldownBeforeLocatingNewFlower(600);
            }
        } else if (bee.getSavedFlowerPos() != null) {
            bee.getNavigation().moveTo(bee.getSavedFlowerPos().getX() + 0.5D, bee.getSavedFlowerPos().getY() + 0.5D, bee.getSavedFlowerPos().getZ() + 0.5D, 1.2D);
            return true;
        }
        return false;
    }

    public boolean canBeeContinue() {
        if (!this.running) {
            return false;
        } else if (bee.getSavedFlowerPos() == null) {
            return false;
        } else if (this.completedPollination()) {
            return bee.getRandom().nextFloat() < 0.2F;
        } else if (!bee.isFlowerValid(bee.getSavedFlowerPos())) {
            bee.setSavedFlowerPos(null);
            return false;
        } else {
            return true;
        }
    }

    private boolean completedPollination() {
        return this.pollinationTicks > 400;
    }

    public boolean isPollinating() {
        return this.running;
    }

    public void stopPollinating() {
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
            ((BeeEntityInvoker) bee).callSetFlag(8, true);
        }

        this.running = false;
        bee.getNavigation().stop();
        ((BeeEntityAccessor)bee).setRemainingCooldownBeforeLocatingNewFlower(200);
    }

    public void clearTask() {
        if (Boolean.FALSE.equals(CommonConfig.MANUAL_MODE.get())) {
            bee.setSavedFlowerPos(null);
            bee.setFlowerEntityID(-1);
            boundingBox = null;
        }
    }

    @Override
    public void tick() {
        ++this.ticks;
        if (this.ticks > 600) {
            this.clearTask();
        } else if ((bee.getCoreData().getEntityFlower().isPresent() || bee.getFlowerEntityID() >= 0)) {
            handleEntityFlower();
            handleBlockFlower();
        }
    }

    private void handleBlockFlower() {
        if (bee.getSavedFlowerPos() != null) {
            Vector3d vector3d = Vector3d.atBottomCenterOf(bee.getSavedFlowerPos()).add(0.0D, 0.6F, 0.0D);
            if (boundingBox != null) vector3d = boundingBox.add(0.0D, 0.4F, 0.0D);
            if (vector3d.distanceTo(bee.position()) > 1D) {
                this.nextTarget = vector3d;
                this.moveToNextTarget();
            } else {
                if (this.nextTarget == null) {
                    this.nextTarget = vector3d;
                }

                pollinateFlower(vector3d);
            }
        }
    }

    private void pollinateFlower(Vector3d vector3d) {
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
            playPollinationSound();

        }
    }

    private void playPollinationSound() {
        if (bee.getRandom().nextFloat() < 0.05F && this.pollinationTicks > this.lastPollinationTick + 60) {
            this.lastPollinationTick = this.pollinationTicks;
            bee.playSound(SoundEvents.BEE_POLLINATE, 1.0F, 1.0F);
        }
    }

    private void handleEntityFlower() {
        if (bee.tickCount % 5 == 0 && bee.getCoreData().getEntityFlower().isPresent()) {
            Entity flowerEntity = bee.level.getEntity(bee.getFlowerEntityID());
            if (flowerEntity != null) {
                boundingBox = new Vector3d(flowerEntity.getBoundingBox().getCenter().x(), flowerEntity.getBoundingBox().maxY, flowerEntity.getBoundingBox().getCenter().z());
                bee.setSavedFlowerPos(flowerEntity.blockPosition());
            } else this.clearTask();
        }
    }

    private void moveToNextTarget() {
        bee.getMoveControl().setWantedPosition(this.nextTarget.x(), this.nextTarget.y(), this.nextTarget.z(), (float) 0.5);
    }

    private double getRandomOffset() {
        return (bee.getRandom().nextFloat() * 2.0D - 1.0D) * 0.33333334D;
    }

    public Optional<BlockPos> findFlower(double range) {
        BlockPos beePos = bee.blockPosition();
        Optional<EntityType<?>> entityFlower = bee.getCoreData().getEntityFlower();
        if (entityFlower.isPresent()) {
            List<?> entityList = bee.level.getEntities(entityFlower.get(), (new AxisAlignedBB(bee.blockPosition())).inflate(range),
                    entity -> entity.getEncodeId() != null && entity.getEncodeId().equals(bee.getCoreData().getEntityFlowerRegistryID()));
            if (!entityList.isEmpty()) {
                Entity firstEntity = (Entity) entityList.get(0);
                bee.setFlowerEntityID(firstEntity.getId());
                return Optional.of(firstEntity.blockPosition());
            }
        } else {
            BlockPos.Mutable flowerPos = beePos.mutable();
            for (BlockPos blockPos : positionOffsets){
                flowerPos.setWithOffset(beePos, blockPos.getX(), blockPos.getY(), blockPos.getZ());
                if (getFlowerBlockPredicate().test(flowerPos)){
                    return Optional.of(flowerPos);
                }
            }
            return Optional.empty();
        }

        return Optional.empty();
    }

    public Predicate<BlockPos> getFlowerBlockPredicate() {
        return pos -> {
            if (bee.level != null && !bee.getCoreData().getBlockFlowers().isEmpty()){
                if (!MathUtils.inRangeInclusive(pos.getY(), 0, 256)) return false;
                BlockState state = bee.level.getBlockState(pos);
                if (state.isAir()) return false;
                return bee.getCoreData().getBlockFlowers().contains(state.getBlock());
            }
            return false;
        };
    }
}
