package com.teamresourceful.resourcefulbees.common.entities.goals;

import com.teamresourceful.resourcefulbees.common.config.BeeConfig;
import com.teamresourceful.resourcefulbees.common.entities.entity.ResourcefulBee;
import com.teamresourceful.resourcefulbees.mixin.common.BeeEntityAccessor;
import com.teamresourceful.resourcefulbees.mixin.common.BeeInvoker;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Optional;
import java.util.function.Predicate;

public class ModBeePollinateGoal extends Goal {

    private static final ArrayList<BlockPos> OFFSETS = Util.make(new ArrayList<>(), offsets -> {
        for(int i = 0; (double)i <= 5; i = i > 0 ? -i : 1 - i) {
            for(int j = 0; (double)j < 5; ++j) {
                for(int k = 0; k <= j; k = k > 0 ? -k : 1 - k) {
                    for(int l = k < j && k > -j ? j : 0; l <= j; l = l > 0 ? -l : 1 - l) {
                        offsets.add(new BlockPos(k,i-1, l));
                    }
                }
            }
        }
    });

    private int pollinationTicks;
    private int lastPollinationTick;
    private boolean running;
    private Vec3 nextTarget;
    private int ticks;
    private final ResourcefulBee bee;
    private Vec3 boundingBox;

    public ModBeePollinateGoal(ResourcefulBee beeEntity) {
        this.setFlags(EnumSet.of(Flag.MOVE));
        this.bee = beeEntity;
    }

    public boolean canBeeStart() {
        if (((BeeEntityAccessor)bee).getRemainingCooldownBeforeLocatingNewFlower() > 0) {
            return false;
        } else if (bee.hasNectar()) {
            return false;
        } else if (bee.getRandom().nextFloat() < 0.7F) {
            return false;
        } else if ((canBeesFreelyFindFlowers() || hasEntityFlower()) && bee.getSavedFlowerPos() == null && randomFlag()) {
            return this.findFlower(5.0D)
                .map(pos -> {
                    bee.setSavedFlowerPos(pos);
                    bee.getNavigation().moveTo(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 1.2D);
                    return true;
                })
                .orElseGet(() -> {
                    ((BeeEntityAccessor)bee).setRemainingCooldownBeforeLocatingNewFlower(600);
                    return false;
                });
        } else if (bee.getSavedFlowerPos() != null) {
            bee.getNavigation().moveTo(bee.getSavedFlowerPos().getX() + 0.5D, bee.getSavedFlowerPos().getY() + 0.5D, bee.getSavedFlowerPos().getZ() + 0.5D, 1.2D);
            return true;
        }
        return false;
    }

    private static boolean canBeesFreelyFindFlowers() {
        return !BeeConfig.manualMode;
    }

    private boolean hasEntityFlower() {
        return bee.getCoreData().hasEntityFlower();
    }

    private boolean randomFlag() {
        return bee.tickCount < 20 || bee.tickCount % 5 == 0;
    }

    public boolean canBeeContinue() {
        if (!isPollinating()) return false;
        if (bee.getSavedFlowerPos() == null) return false;
        if (this.completedPollination()) return bee.getRandom().nextFloat() < 0.2F;
        if (!bee.isFlowerValid(bee.getSavedFlowerPos())) {
            bee.setSavedFlowerPos(null);
            return false;
        }
        return true;
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
            ((BeeInvoker) bee).callSetFlag(8, true);
        }

        this.running = false;
        bee.getNavigation().stop();
        ((BeeEntityAccessor)bee).setRemainingCooldownBeforeLocatingNewFlower(200);
    }

    public void clearTask() {
        if (canBeesFreelyFindFlowers()) {
            bee.setSavedFlowerPos(null);
            bee.entityFlower.clear();
            boundingBox = null;
        }
    }

    @Override
    public void tick() {
        ++this.ticks;
        if (this.ticks > 600) {
            this.clearTask();
        } else if ((bee.getCoreData().hasEntityFlower() || bee.entityFlower.hasData())) {
            handleEntityFlower();
        } else {
            handleBlockFlower();
        }
    }

    private void handleBlockFlower() {
        if (bee.getSavedFlowerPos() != null) {
            Vec3 vector3d = Vec3.atBottomCenterOf(bee.getSavedFlowerPos()).add(0.0D, 0.6F, 0.0D);
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

    private void pollinateFlower(Vec3 vector3d) {
        boolean closeToTarget = bee.position().distanceTo(this.nextTarget) <= 0.1D;
        boolean shouldMoveToNewTarget = true;
        if (!closeToTarget && this.ticks > 600) {
            this.clearTask();
        } else {
            if (closeToTarget) {
                if (bee.getRandom().nextInt(25) == 0) {
                    this.nextTarget = new Vec3(vector3d.x() + this.getRandomOffset(), vector3d.y(), vector3d.z() + this.getRandomOffset());
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
        if (bee.tickCount % 5 == 0 && bee.getCoreData().hasEntityFlower()) {
            Entity flowerEntity = bee.level.getEntity(bee.entityFlower.get());
            if (flowerEntity != null) {
                boundingBox = new Vec3(flowerEntity.getBoundingBox().getCenter().x(), flowerEntity.getBoundingBox().maxY, flowerEntity.getBoundingBox().getCenter().z());
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
        if (bee.getCoreData().hasEntityFlower()) {
            return bee.level.getEntities(bee, new AABB(bee.blockPosition()).inflate(range), entity -> bee.getCoreData().isEntityFlower(entity.getType()))
                    .stream()
                    .filter(Entity::isAlive)
                    .findFirst()
                    .map(entity -> {
                        bee.entityFlower.set(entity.getId());
                        return entity.blockPosition();
                    });
        } else {
            BlockPos.MutableBlockPos flowerPos = beePos.mutable();
            for (BlockPos blockPos : OFFSETS){
                flowerPos.setWithOffset(beePos, blockPos);
                if (getFlowerBlockPredicate().test(flowerPos)){
                    return Optional.of(flowerPos);
                }
            }
        }

        return Optional.empty();
    }

    public Predicate<BlockPos> getFlowerBlockPredicate() {
        return pos -> {
            if (bee.getCoreData().flowers().size() > 0){
                if (!bee.level.isInWorldBounds(pos)) return false;
                BlockState state = bee.level.getBlockState(pos);
                if (state.isAir()) return false;
                return state.is(bee.getCoreData().flowers());
            }
            return false;
        };
    }
}
