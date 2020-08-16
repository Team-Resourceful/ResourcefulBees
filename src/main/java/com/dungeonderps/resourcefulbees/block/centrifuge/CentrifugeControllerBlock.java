package com.dungeonderps.resourcefulbees.block.centrifuge;

import com.dungeonderps.resourcefulbees.container.CentrifugeMultiblockContainer;
import com.dungeonderps.resourcefulbees.tileentity.centrifuge.CentrifugeControllerTileEntity;
import com.dungeonderps.resourcefulbees.utils.TooltipBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.IntStream;

public class CentrifugeControllerBlock extends Block {
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
    public static final BooleanProperty PROPERTY_VALID = BooleanProperty.create("valid");

    public CentrifugeControllerBlock(Properties properties) {
        super(properties);
        setDefaultState(getDefaultState().with(PROPERTY_VALID,false).with(FACING, Direction.NORTH));
    }

    @Nonnull
    @Override
    public ActionResultType onBlockActivated(@Nonnull BlockState state, World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull BlockRayTraceResult blockRayTraceResult) {
        if (!world.isRemote) {
            INamedContainerProvider blockEntity = state.getContainer(world,pos);
            if (blockEntity != null) {
                if (blockEntity instanceof CentrifugeControllerTileEntity){
                    CentrifugeControllerTileEntity multiblockCentrifuge =  (CentrifugeControllerTileEntity)blockEntity;
                    multiblockCentrifuge.validStrucutre = multiblockCentrifuge.validateStructure(world, (ServerPlayerEntity) player);
                    if (multiblockCentrifuge.validStrucutre){
                        NetworkHooks.openGui((ServerPlayerEntity) player, blockEntity, pos);
                    }
                }
            }
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, FluidState fluid) {
        if (!world.isRemote){
            CentrifugeControllerTileEntity controller = (CentrifugeControllerTileEntity)world.getTileEntity(pos);
            if (controller !=null){
                controller.setCasingsToNotLinked(controller.buildStructureBounds());
                return super.removedByPlayer(state,world,pos,player,willHarvest,fluid);
            }
        }
        return super.removedByPlayer(state,world,pos,player,willHarvest,fluid);
    }

    @Override
    public void onBlockExploded(BlockState state, World world, BlockPos pos, Explosion explosion) {
        if (!world.isRemote){
            CentrifugeControllerTileEntity controller = (CentrifugeControllerTileEntity)world.getTileEntity(pos);
            if (controller !=null){
                controller.setCasingsToNotLinked(controller.buildStructureBounds());
                super.onBlockExploded(state,world,pos,explosion);
            }
        } else {
            super.onBlockExploded(state,world,pos,explosion);
        }
    }

    @Nullable
    @Override
    public INamedContainerProvider getContainer(@Nonnull BlockState state, World worldIn, @Nonnull BlockPos pos) {
        return (INamedContainerProvider)worldIn.getTileEntity(pos);
    }

    @Override
    public void onReplaced(@Nonnull BlockState state1, World world, @Nonnull BlockPos pos, @Nonnull BlockState state, boolean isMoving) {
        TileEntity blockEntity = world.getTileEntity(pos);
        if (blockEntity instanceof CentrifugeControllerTileEntity && state.getBlock() != state1.getBlock()){
            CentrifugeControllerTileEntity centrifugeTileEntity = (CentrifugeControllerTileEntity)blockEntity;
            ItemStackHandler h = centrifugeTileEntity.h;
            IntStream.range(0, h.getSlots()).mapToObj(h::getStackInSlot).filter(s -> !s.isEmpty()).forEach(stack -> InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack));
        }
        super.onReplaced(state1, world, pos, state, isMoving);
    }



    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new CentrifugeControllerTileEntity();
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) { return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite()); }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) { builder.add(PROPERTY_VALID, FACING); }

    @Override
    public void addInformation(@Nonnull ItemStack stack, @Nullable IBlockReader worldIn, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flagIn) {
            tooltip.addAll(new TooltipBuilder()
                    .addTip(I18n.format("block.resourcefulbees.centrifuge.tooltip.info"), TextFormatting.GOLD)
                    .build());
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }
}



