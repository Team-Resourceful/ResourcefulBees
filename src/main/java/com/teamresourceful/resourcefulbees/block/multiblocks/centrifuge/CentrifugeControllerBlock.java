package com.teamresourceful.resourcefulbees.block.multiblocks.centrifuge;

import com.teamresourceful.resourcefulbees.registry.ModBlockEntityTypes;
import com.teamresourceful.resourcefulbees.tileentity.CentrifugeTileEntity;
import com.teamresourceful.resourcefulbees.tileentity.multiblocks.centrifuge.CentrifugeControllerTileEntity;
import com.teamresourceful.resourcefulbees.utils.TooltipBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
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
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("deprecation")
public class CentrifugeControllerBlock extends Block {
    public static final DirectionProperty FACING = HorizontalBlock.FACING;
    public static final BooleanProperty PROPERTY_VALID = BooleanProperty.create("valid");

    public CentrifugeControllerBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(PROPERTY_VALID,false).setValue(FACING, Direction.NORTH));
    }

    protected CentrifugeControllerTileEntity getControllerEntity(World world, BlockPos pos) {
        TileEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof CentrifugeControllerTileEntity) {
            return (CentrifugeControllerTileEntity) tileEntity;
        }
        return null;
    }

    @NotNull
    @Override
    public ActionResultType use(@NotNull BlockState state, @NotNull World world, @NotNull BlockPos pos, @NotNull PlayerEntity player, @NotNull Hand hand, @NotNull BlockRayTraceResult rayTraceResult) {
        ItemStack heldItem = player.getItemInHand(hand);
        boolean hasCapability = heldItem.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent();
        CentrifugeControllerTileEntity controller = getControllerEntity(world, pos);

        if (controller != null && controller.isValidStructure()) {
            if (hasCapability) {
                controller.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
                        .ifPresent(iFluidHandler -> FluidUtil.interactWithFluidHandler(player, hand, world, pos, null));
            } else if (!player.isShiftKeyDown() && !world.isClientSide) {
                NetworkHooks.openGui((ServerPlayerEntity) player, controller, pos);
            }
            return ActionResultType.SUCCESS;
        }

        return super.use(state, world, pos, player, hand, rayTraceResult);
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, World world, @NotNull BlockPos pos, @NotNull Block changedBlock, @NotNull BlockPos changedBlockPos, boolean bool) {
        TileEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof CentrifugeTileEntity) {
            CentrifugeTileEntity centrifugeTileEntity = (CentrifugeTileEntity) tileEntity;
            centrifugeTileEntity.setIsPoweredByRedstone(world.hasNeighborSignal(pos));
        }
    }

    @Nullable
    @Override
    public INamedContainerProvider getMenuProvider(@NotNull BlockState state, @NotNull World worldIn, @NotNull BlockPos pos) {
        return getControllerEntity(worldIn, pos);
    }

    @Override
    public boolean hasTileEntity(BlockState state) { return true; }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) { return new CentrifugeControllerTileEntity(ModBlockEntityTypes.CENTRIFUGE_CONTROLLER_ENTITY.get()); }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) { return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()); }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) { builder.add(PROPERTY_VALID, FACING); }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable IBlockReader worldIn, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flagIn) {
        tooltip.addAll(new TooltipBuilder()
                .addTranslatableTip("block.resourcefulbees.centrifuge.tooltip.info", TextFormatting.GOLD)
                .build());
        if (Screen.hasControlDown()){
            tooltip.addAll(new TooltipBuilder()
                    .addTranslatableTip("block.resourcefulbees.centrifuge.tooltip.structure_size", TextFormatting.AQUA)
                    .addTranslatableTip("block.resourcefulbees.centrifuge.tooltip.requisites", TextFormatting.AQUA)
                    .addTranslatableTip("block.resourcefulbees.centrifuge.tooltip.capabilities", TextFormatting.AQUA)
                    .build());
        } else {
            tooltip.add(new TranslationTextComponent("resourcefulbees.ctrl_info").withStyle(TextFormatting.AQUA));
        }
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }


}



