package com.resourcefulbees.resourcefulbees.block;

import com.resourcefulbees.resourcefulbees.fluids.HoneyFlowingFluid;
import com.resourcefulbees.resourcefulbees.tileentity.HoneyTankTileEntity;
import com.resourcefulbees.resourcefulbees.utils.TooltipBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
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
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;

public class HoneyTank extends Block {

    protected static final VoxelShape VOXEL_SHAPE = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

    public static final IntegerProperty LEVEL = IntegerProperty.create("level", 0, 14);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public static final AbstractBlock.Properties PROPERTIES = Block.Properties.create(Material.GLASS)
            .sound(SoundType.GLASS)
            .harvestTool(ToolType.PICKAXE)
            .hardnessAndResistance(1)
            .nonOpaque();

    public final HoneyTankTileEntity.TankTier tier;

    public HoneyTank(Properties properties, HoneyTankTileEntity.TankTier tier) {
        super(properties);
        this.tier = tier;
        BlockState defaultState = this.stateContainer.getBaseState()
                .with(LEVEL, 0)
                .with(WATERLOGGED, false);
        this.setDefaultState(defaultState);
    }

    private static HoneyTankTileEntity getTileEntity(@javax.annotation.Nullable IBlockReader world, @javax.annotation.Nullable BlockPos pos) {
        TileEntity entity = world.getTileEntity(pos);
        if (entity instanceof HoneyTankTileEntity) {
            return (HoneyTankTileEntity) entity;
        }
        return null;
    }

    public static int getBlockColor(BlockState state, @javax.annotation.Nullable IBlockReader world, @javax.annotation.Nullable BlockPos pos, int tintIndex) {
        if (tintIndex == 1) {
            TileEntity fluidTank = getTileEntity(world, pos);
            if (fluidTank != null) return ((HoneyTankTileEntity) fluidTank).getFluidColor();
        }
        return -1;
    }

    @Override
    public void animateTick(@Nonnull BlockState stateIn, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Random rand) {
        HoneyTankTileEntity tank = getTileEntity(world, pos);
        if (tank == null) {
            return;
        }
        if (tank.fluidTank.getFluid().getFluid() instanceof HoneyFlowingFluid) {
            HoneyFlowingFluid fluid = (HoneyFlowingFluid) tank.fluidTank.getFluid().getFluid();
            if (fluid.getHoneyData().isRainbow()) {
                world.notifyBlockUpdate(pos, stateIn, stateIn, 2);
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
    public ActionResultType onUse(@Nonnull BlockState state, World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull BlockRayTraceResult blockRayTraceResult) {

        ItemStack heldItem = player.getHeldItem(hand);
        boolean usingHoney = heldItem.getItem() instanceof HoneyBottleItem;
        boolean usingBottle = heldItem.getItem() instanceof GlassBottleItem;
        boolean usingBucket = heldItem.getItem() instanceof BucketItem;
        TileEntity tileEntity = world.getTileEntity(pos);

        if (!world.isRemote) {
            if (tileEntity instanceof HoneyTankTileEntity) {
                HoneyTankTileEntity tank = (HoneyTankTileEntity) tileEntity;
                if (usingBucket) {
                    tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
                            .ifPresent(iFluidHandler -> FluidUtil.interactWithFluidHandler(player, hand, world, pos, null));
                } else if (usingBottle) {
                    world.playSound(player, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.PLAYERS, 1.0f, 1.0f);
                    tank.fillBottle(player, hand);
                } else if (usingHoney) {
                    world.playSound(player, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.PLAYERS, 1.0f, 1.0f);
                    tank.emptyBottle(player, hand);
                }
                updateBlockState(world, pos);
                world.notifyBlockUpdate(pos, state, state, 2);
            }
        }
        if (usingBottle || usingBucket || usingHoney) {
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.FAIL;
    }

    public void updateBlockState(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof HoneyTank) {
            world.setBlockState(pos, state.with(LEVEL, getLevel(world, pos)));
        }
    }


    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : Fluids.EMPTY.getDefaultState();
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        FluidState fluidState = context.getWorld().getFluidState(context.getPos());
        return this.getDefaultState();
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED, LEVEL);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return VOXEL_SHAPE;
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
        if (stateIn.get(WATERLOGGED)) {
            world.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        if (!world.isRemote()) {
            stateIn.with(LEVEL, getLevel(world, currentPos));
        }
        return stateIn;
    }

    private int getLevel(IWorld world, BlockPos currentPos) {
        TileEntity tileEntity = world.getTileEntity(currentPos);
        if (tileEntity instanceof HoneyTankTileEntity) {
            HoneyTankTileEntity tank = (HoneyTankTileEntity) tileEntity;
            float fillPercentage = ((float) tank.fluidTank.getFluidAmount()) / ((float) tank.fluidTank.getTankCapacity(0));
            return (int) Math.ceil(fillPercentage * 14);
        }
        return 0;
    }

    @Override
    public void addInformation(@Nonnull ItemStack stack, @javax.annotation.Nullable IBlockReader worldIn, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flagIn) {
        if (!stack.hasTag() || stack.getTag() == null || stack.getTag().isEmpty() || !stack.getTag().contains("fluid"))
            return;
        HoneyTankTileEntity.TankTier tier = HoneyTankTileEntity.TankTier.getTier(stack.getTag().getInt("tier"));
        FluidTank tank = new FluidTank(tier.getMaxFillAmount()).readFromNBT(stack.getTag().getCompound("fluid"));
        FluidStack fluid = tank.getFluid();
        if (!fluid.isEmpty()) {
            tooltip.addAll(new TooltipBuilder()
                    .addTip(I18n.format(fluid.getTranslationKey()))
                    .appendText(": [" + tank.getFluidAmount() + "/" + tank.getCapacity() + "]")
                    .applyStyle(TextFormatting.GOLD).build());
        }
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof HoneyTankTileEntity) {
            HoneyTankTileEntity tank = (HoneyTankTileEntity) tileEntity;
            if (itemStack.getTag() != null) {
                tank.readNBT(itemStack.getTag());
            }
        }
        updateBlockState(world, pos);
    }


    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof HoneyTankTileEntity) {
            HoneyTankTileEntity tank = (HoneyTankTileEntity) tileEntity;
            ItemStack stack = new ItemStack(state.getBlock().asItem());
            stack.setTag(tank.writeNBT(new CompoundNBT()));
            return stack;
        }
        return tier.getTankItem().get().getDefaultInstance();
    }
}
