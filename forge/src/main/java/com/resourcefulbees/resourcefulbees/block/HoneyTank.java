package com.resourcefulbees.resourcefulbees.block;

import com.resourcefulbees.resourcefulbees.fluids.HoneyFlowingFluid;
import com.resourcefulbees.resourcefulbees.tileentity.HoneyTankTileEntity;
import com.resourcefulbees.resourcefulbees.utils.TooltipBuilder;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;

@SuppressWarnings("deprecation")
public class HoneyTank extends Block {

    protected static final VoxelShape VOXEL_SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public static final AbstractBlock.Properties WOODEN = AbstractBlock.Properties.of(Material.GLASS)
            .sound(SoundType.GLASS)
            .harvestTool(ToolType.AXE)
            .strength(1.0f)
            .noOcclusion();

    public static final AbstractBlock.Properties PURPUR = AbstractBlock.Properties.of(Material.STONE, MaterialColor.COLOR_MAGENTA)
            .sound(SoundType.GLASS)
            .harvestTool(ToolType.PICKAXE)
            .strength(1.5f)
            .noOcclusion()
            .requiresCorrectToolForDrops();

    public static final AbstractBlock.Properties NETHER = AbstractBlock.Properties.of(Material.STONE, MaterialColor.NETHER)
            .sound(SoundType.GLASS)
            .harvestTool(ToolType.PICKAXE)
            .strength(1.5f)
            .noOcclusion()
            .requiresCorrectToolForDrops();

    public final HoneyTankTileEntity.TankTier tier;

    public HoneyTank(Properties properties, HoneyTankTileEntity.TankTier tier) {
        super(properties);
        this.tier = tier;
        BlockState defaultState = this.stateDefinition.any()
                .setValue(BlockStateProperties.WATERLOGGED, false);
        this.registerDefaultState(defaultState);
    }

    private static HoneyTankTileEntity getTileEntity(@Nonnull IBlockReader world, @Nonnull BlockPos pos) {
        TileEntity entity = world.getBlockEntity(pos);
        if (entity instanceof HoneyTankTileEntity) {
            return (HoneyTankTileEntity) entity;
        }
        return null;
    }

    @Override
    public void animateTick(@Nonnull BlockState stateIn, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Random rand) {
        HoneyTankTileEntity tank = getTileEntity(world, pos);
        if (tank == null) {
            return;
        }
        if (tank.getFluidTank().getFluid().getFluid() instanceof HoneyFlowingFluid) {
            HoneyFlowingFluid fluid = (HoneyFlowingFluid) tank.getFluidTank().getFluid().getFluid();
            if (fluid.getHoneyData().isRainbow()) {
                world.sendBlockUpdated(pos, stateIn, stateIn, 2);
            }
        }
        super.animateTick(stateIn, world, pos, rand);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new HoneyTankTileEntity(tier);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nonnull
    @Override
    public ActionResultType use(@Nonnull BlockState state, World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull BlockRayTraceResult blockRayTraceResult) {

        ItemStack heldItem = player.getItemInHand(hand);
        boolean usingHoney = heldItem.getItem() instanceof HoneyBottleItem;
        boolean usingBottle = heldItem.getItem() instanceof GlassBottleItem;
        boolean hasCapability = heldItem.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent();
        TileEntity tileEntity = world.getBlockEntity(pos);

        if (tileEntity instanceof HoneyTankTileEntity) {
            HoneyTankTileEntity tank = (HoneyTankTileEntity) tileEntity;
            if (!world.isClientSide) {
                if (usingBottle) {
                    tank.fillBottle(player, hand);
                } else if (usingHoney) {
                    tank.emptyBottle(player, hand);
                } else if (hasCapability) {
                    tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
                            .ifPresent(iFluidHandler -> FluidUtil.interactWithFluidHandler(player, hand, world, pos, null));
                }
            }
            return ActionResultType.SUCCESS;
        }
        return super.use(state, world, pos, player, hand, blockRayTraceResult);
    }

    @Nonnull
    @Override
    public FluidState getFluidState(BlockState state) {
        return Boolean.TRUE.equals(state.getValue(BlockStateProperties.WATERLOGGED)) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
    }

    @Override
    public BlockState getStateForPlacement(@NotNull BlockItemUseContext context) {
        return this.defaultBlockState();
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }

    @Nonnull
    @Override
    public VoxelShape getShape(@NotNull BlockState state, @NotNull IBlockReader worldIn, @NotNull BlockPos pos, @NotNull ISelectionContext context) {
        return VOXEL_SHAPE;
    }

    @Nonnull
    @Override
    public BlockState updateShape(BlockState stateIn, @NotNull Direction facing, @NotNull BlockState facingState, @NotNull IWorld world, @NotNull BlockPos currentPos, @NotNull BlockPos facingPos) {
        if (Boolean.TRUE.equals(stateIn.getValue(BlockStateProperties.WATERLOGGED))) {
            world.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }
        return stateIn;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @javax.annotation.Nullable IBlockReader worldIn, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flagIn) {
        if (!stack.hasTag() || stack.getTag() == null || stack.getTag().isEmpty() || !stack.getTag().contains("fluid"))
            return;
        HoneyTankTileEntity.TankTier tankTier = HoneyTankTileEntity.TankTier.getTier(stack.getTag().getInt("tier"));
        FluidTank tank = new FluidTank(tankTier.getMaxFillAmount()).readFromNBT(stack.getTag().getCompound("fluid"));
        FluidStack fluid = tank.getFluid();
        if (!fluid.isEmpty()) {
            tooltip.addAll(new TooltipBuilder()
                    .addTip(I18n.get(fluid.getTranslationKey()))
                    .appendText(": [" + tank.getFluidAmount() + "/" + tank.getCapacity() + "]")
                    .applyStyle(TextFormatting.GOLD).build());
        }
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public void setPlacedBy(World world, @NotNull BlockPos pos, @NotNull BlockState blockState, @Nullable LivingEntity livingEntity, @NotNull ItemStack itemStack) {
        TileEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof HoneyTankTileEntity) {
            HoneyTankTileEntity tank = (HoneyTankTileEntity) tileEntity;
            if (itemStack.getTag() != null) {
                tank.readNBT(itemStack.getTag());
            }
        }
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        TileEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof HoneyTankTileEntity) {
            HoneyTankTileEntity tank = (HoneyTankTileEntity) tileEntity;
            ItemStack stack = new ItemStack(state.getBlock().asItem());
            stack.setTag(tank.writeNBT(new CompoundNBT()));
            return stack;
        }
        return tier.getTankItem().get().getDefaultInstance();
    }

    @Override
    public @NotNull BlockRenderType getRenderShape(@NotNull BlockState blockState) {
        return BlockRenderType.MODEL;
    }
}
