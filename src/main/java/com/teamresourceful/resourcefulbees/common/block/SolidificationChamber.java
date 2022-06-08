package com.teamresourceful.resourcefulbees.common.block;

import com.teamresourceful.resourcefulbees.common.block.base.SidedTickingBlock;
import com.teamresourceful.resourcefulbees.common.blockentity.SolidificationChamberBlockEntity;
import com.teamresourceful.resourcefulbees.common.capabilities.HoneyFluidTank;
import com.teamresourceful.resourcefulbees.common.fluids.CustomHoneyFluid;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlockEntityTypes;
import com.teamresourceful.resourcefulbees.common.utils.ModUtils;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BottleItem;
import net.minecraft.world.item.HoneyBottleItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("deprecation")
public class SolidificationChamber extends SidedTickingBlock<SolidificationChamberBlockEntity> {

    protected static final VoxelShape VOXEL_SHAPE = Util.make(() -> {
        VoxelShape shape = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 5.0D, 15.0D);
        shape = Shapes.join(shape, Block.box(6.0D, 5.0D, 1.0D, 10.0D, 9.0D, 15.0D), BooleanOp.OR);
        shape = Shapes.join(shape, Block.box(1.0D, 5.0D, 6.0D, 15.0D, 9.0D, 10.0D), BooleanOp.OR);
        shape = Shapes.join(shape, Block.box(3.0D, 5.0D, 3.0D, 13.0D, 16.0D, 13.0D), BooleanOp.OR);
        return shape;
    });

    public SolidificationChamber() {
        super(ModBlockEntityTypes.SOLIDIFICATION_CHAMBER_TILE_ENTITY,
                SolidificationChamberBlockEntity::serverTick,
                null,
                Properties.of(Material.STONE).sound(SoundType.GLASS).strength(1.5f).requiresCorrectToolForDrops());
    }

    private static SolidificationChamberBlockEntity getBlockEntity(@NotNull BlockGetter level, @NotNull BlockPos pos) {
        return level.getBlockEntity(pos) instanceof SolidificationChamberBlockEntity entity ? entity : null;
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
        BlockEntity tileEntity = level.getBlockEntity(pos);

        if (tileEntity instanceof SolidificationChamberBlockEntity) {
            if (!level.isClientSide) {
                FluidTank tank = ((SolidificationChamberBlockEntity) tileEntity).getTank();
                Item item = player.getItemInHand(hand).getItem();
                if (item instanceof HoneyBottleItem) {
                    HoneyFluidTank.emptyBottle(tank, player, hand);
                } else if (item instanceof BottleItem) {
                    HoneyFluidTank.fillBottle(tank, player, hand);
                } else {
                    ModUtils.capabilityOrGuiUse(tileEntity, player, level, pos, hand);
                }
            }
            return InteractionResult.SUCCESS;
        }
        return super.use(state, level, pos, player, hand, hitResult);
    }

    @Override
    public void animateTick(@NotNull BlockState stateIn, @NotNull Level level, @NotNull BlockPos pos, @NotNull RandomSource rand) {
        SolidificationChamberBlockEntity tank = getBlockEntity(level, pos);
        if (tank == null) return;
        if (tank.getTank().getFluid().getFluid() instanceof CustomHoneyFluid fluid && fluid.getHoneyData().color().isRainbow()) {
            level.sendBlockUpdated(pos, stateIn, stateIn, 2);
        }
        super.animateTick(stateIn, level, pos, rand);
    }


    @NotNull
    @Override
    public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter getter, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return VOXEL_SHAPE;
    }
}
