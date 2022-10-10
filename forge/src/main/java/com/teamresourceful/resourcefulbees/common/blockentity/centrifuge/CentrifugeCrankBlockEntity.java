package com.teamresourceful.resourcefulbees.common.blockentity.centrifuge;

import com.teamresourceful.resourcefulbees.common.block.centrifuge.CentrifugeBlock;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class CentrifugeCrankBlockEntity extends BlockEntity implements IAnimatable {

    private final AnimationFactory factory = new AnimationFactory(this);

    public CentrifugeCrankBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public CentrifugeCrankBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.CENTRIFUGE_CRANK_ENTITY.get(), pos, state);
    }

    protected  <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        if (getLevel() != null) {
            var state = getLevel().getBlockState(getBlockPos().below());
            int value = state.hasProperty(CentrifugeBlock.ROTATION) ? state.getValue(CentrifugeBlock.ROTATION) : 1;
            var animationBuilder = new AnimationBuilder();
            switch (value) {
                case 1 -> animationBuilder.addAnimation("animation.crank.360", false).addAnimation("animation.crank.0", false);
                case 2 -> animationBuilder.addAnimation("animation.crank.45", false);
                case 3 -> animationBuilder.addAnimation("animation.crank.90", false);
                case 4 -> animationBuilder.addAnimation("animation.crank.135", false);
                case 5 -> animationBuilder.addAnimation("animation.crank.180", false);
                case 6 -> animationBuilder.addAnimation("animation.crank.225", false);
                case 7 -> animationBuilder.addAnimation("animation.crank.270", false);
                case 8 -> animationBuilder.addAnimation("animation.crank.315", false);
            }
            event.getController().setAnimation(animationBuilder);
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller", 10, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
}
