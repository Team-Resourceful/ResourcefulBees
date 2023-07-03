package com.teamresourceful.resourcefulbees.common.blockentities;

import com.teamresourceful.resourcefulbees.common.blocks.CentrifugeBlock;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class CentrifugeCrankBlockEntity extends BlockEntity implements GeoBlockEntity {

    private static final RawAnimation ROT_360 = RawAnimation.begin().thenPlay("animation.crank.360");
    private static final RawAnimation ROT_45 = RawAnimation.begin().thenPlay("animation.crank.45");
    private static final RawAnimation ROT_90 = RawAnimation.begin().thenPlay("animation.crank.90");
    private static final RawAnimation ROT_135 = RawAnimation.begin().thenPlay("animation.crank.135");
    private static final RawAnimation ROT_180 = RawAnimation.begin().thenPlay("animation.crank.180");
    private static final RawAnimation ROT_225 = RawAnimation.begin().thenPlay("animation.crank.225");
    private static final RawAnimation ROT_270 = RawAnimation.begin().thenPlay("animation.crank.270");
    private static final RawAnimation ROT_315 = RawAnimation.begin().thenPlay("animation.crank.315");

    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);

    public CentrifugeCrankBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public CentrifugeCrankBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.CENTRIFUGE_CRANK_ENTITY.get(), pos, state);
    }

    protected  <E extends GeoAnimatable> PlayState predicate(AnimationState<E> event) {
        if (getLevel() != null) {
            var state = getLevel().getBlockState(getBlockPos().below());
            int value = state.hasProperty(CentrifugeBlock.ROTATION) ? state.getValue(CentrifugeBlock.ROTATION) : 1;
            RawAnimation animation = switch (value) {
                case 2 -> ROT_45;
                case 3 -> ROT_90;
                case 4 -> ROT_135;
                case 5 -> ROT_180;
                case 6 -> ROT_225;
                case 7 -> ROT_270;
                case 8 -> ROT_315;
                default -> ROT_360;
            };
            event.getController().setAnimation(animation);
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "controller", 10, this::predicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animationCache;
    }
}
