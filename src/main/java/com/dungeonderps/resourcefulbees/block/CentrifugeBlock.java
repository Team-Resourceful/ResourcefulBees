package com.dungeonderps.resourcefulbees.block;

import com.dungeonderps.resourcefulbees.tileentity.CentrifugeBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.stream.IntStream;

//import com.tfar.beesourceful.CentrifugeBlockEntity;
//import com.dungeonderps.resourcefulbees.tileentity.CentrifugeBlockEntity;

public class CentrifugeBlock extends Block {
    public CentrifugeBlock(Properties properties) {
        super(properties);
        setDefaultState(getDefaultState().with(PROPERTY_FACING, Direction.NORTH).with(PROPERTY_ON,false));
    }

    public static final DirectionProperty PROPERTY_FACING = BlockStateProperties.FACING;
    public static final BooleanProperty PROPERTY_ON = BooleanProperty.create("on");

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult blockRayTraceResult) {
        if (!world.isRemote) {
            INamedContainerProvider blockEntity = state.getContainer(world,pos);
            if (blockEntity != null) {
                NetworkHooks.openGui((ServerPlayerEntity) player, blockEntity, pos);
                player.addStat(Stats.OPEN_BARREL);
            }
        }
        return ActionResultType.SUCCESS;
    }

    @Nullable
    @Override
    public INamedContainerProvider getContainer(BlockState state, World worldIn, BlockPos pos) {
        return (INamedContainerProvider)worldIn.getTileEntity(pos);
    }

    public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
        return p_185499_1_.with(PROPERTY_FACING, p_185499_2_.rotate(p_185499_1_.get(PROPERTY_FACING)));
    }

    public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
        return p_185471_1_.rotate(p_185471_2_.toRotation(p_185471_1_.get(PROPERTY_FACING)));
    }

    public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
        return this.getDefaultState().with(PROPERTY_FACING, p_196258_1_.getNearestLookingDirection().getOpposite());
    }

    @Override
    public void onReplaced(BlockState state1, World world, BlockPos pos, BlockState state, boolean p_196243_5_) {
        TileEntity blockEntity = world.getTileEntity(pos);
        if (blockEntity instanceof CentrifugeBlockEntity && state.getBlock() != state1.getBlock()){
            CentrifugeBlockEntity centrifugeBlockEntity = (CentrifugeBlockEntity)blockEntity;
            ItemStackHandler h = centrifugeBlockEntity.h;
            IntStream.range(0, h.getSlots()).mapToObj(h::getStackInSlot).filter(s -> !s.isEmpty()).forEach(stack -> InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack));
        }
        super.onReplaced(state1, world, pos, state, p_196243_5_);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new CentrifugeBlockEntity();
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(PROPERTY_FACING,PROPERTY_ON);
    }
}



