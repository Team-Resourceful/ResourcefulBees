package com.teamresourceful.resourcefulbees.common.block;

import com.teamresourceful.resourcefulbees.common.capabilities.HoneyFluidTank;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.tileentity.EnderBeeconTileEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.core.BlockPos;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.ItemTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EnderBeecon extends AbstractTank {

    protected static final VoxelShape VOXEL_SHAPE_TOP = Util.make(() -> {
        VoxelShape shape = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 1.0D, 14.0D);
        shape = VoxelShapes.join(shape, Block.box(3.0D, 1.0D, 3.0D, 13.0D, 3.0D, 13.0D), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, Block.box(4.0D, 3.0D, 4.0D, 12.0D, 11.0D, 12.0D), IBooleanFunction.OR);
        shape = VoxelShapes.join(shape, Block.box(3.0D, 11.0D, 3.0D, 13.0D, 13.0D, 13.0D), IBooleanFunction.OR);
        return shape;
    });

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty BEAM = BooleanProperty.create("beam");
    public static final BooleanProperty SOUND = BooleanProperty.create("sound");

    public static final AbstractBlock.Properties PROPERTIES = AbstractBlock.Properties.of(Material.GLASS, MaterialColor.COLOR_PURPLE)
            .requiresCorrectToolForDrops()
            .harvestTool(ToolType.PICKAXE)
            .strength(5)
            .harvestLevel(2)
            .sound(SoundType.LODESTONE)
            .lightLevel(luminance -> 15)
            .noOcclusion()
            .dynamicShape();

    public EnderBeecon(AbstractBlock.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false).setValue(BEAM, true).setValue(SOUND, true));
    }

    @NotNull
    @Override
    @Deprecated
    public ActionResultType use(@NotNull BlockState state, World world, @NotNull BlockPos pos, @NotNull PlayerEntity player, @NotNull Hand hand, @NotNull BlockRayTraceResult rayTraceResult) {

        Item heldItem = player.getItemInHand(hand).getItem();
        TileEntity tileEntity = world.getBlockEntity(pos);

        if (tileEntity instanceof EnderBeeconTileEntity) {
            HoneyFluidTank tank = ((EnderBeeconTileEntity) tileEntity).getTank();
            if (!world.isClientSide) {
                boolean usingWool = heldItem.is(ItemTags.WOOL);
                boolean usingStick = heldItem.equals(Items.STICK);

                if (usingWool) {
                    world.setBlock(pos, state.cycle(SOUND), Constants.BlockFlags.DEFAULT);
                } else if (usingStick) {
                    world.setBlock(pos, state.cycle(BEAM), Constants.BlockFlags.DEFAULT);
                } else if (heldItem instanceof GlassBottleItem) {
                    tank.fillBottle(player, hand);
                } else if (heldItem instanceof HoneyBottleItem) {
                    tank.emptyBottle(player, hand);
                } else {
                    capabilityOrGuiUse(tileEntity, player, world, pos, hand);
                }
            }
            return ActionResultType.SUCCESS;
        }
        return super.use(state, world, pos, player, hand, rayTraceResult);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public @Nullable TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new EnderBeeconTileEntity();
    }

    @NotNull
    @Override
    @Deprecated
    public VoxelShape getShape(@NotNull BlockState state, @NotNull IBlockReader worldIn, @NotNull BlockPos pos, @NotNull ISelectionContext context) {
        return VOXEL_SHAPE_TOP;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED).add(BEAM).add(SOUND);
    }

    @NotNull
    @Override
    public FluidState getFluidState(BlockState state) {
        return Boolean.TRUE.equals(state.getValue(BlockStateProperties.WATERLOGGED)) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
    }

    @Override
    public BlockState getStateForPlacement(@NotNull BlockItemUseContext context) {
        return this.defaultBlockState();
    }

    @NotNull
    @Override
    public BlockState updateShape(BlockState stateIn, @NotNull Direction facing, @NotNull BlockState facingState, @NotNull IWorld world, @NotNull BlockPos currentPos, @NotNull BlockPos facingPos) {
        if (Boolean.TRUE.equals(stateIn.getValue(BlockStateProperties.WATERLOGGED))) {
            world.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }
        return stateIn;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable IBlockReader worldIn, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flagIn) {
        tooltip.add(TranslationConstants.Items.BEECON_TOOLTIP.withStyle(TextFormatting.LIGHT_PURPLE));
        tooltip.add(TranslationConstants.Items.BEECON_TOOLTIP_1.withStyle(TextFormatting.LIGHT_PURPLE));

        if (!stack.hasTag()) return;
        if (!stack.getTag().contains(NBTConstants.NBT_BLOCK_ENTITY_TAG)) return;
        if (!stack.getTag().getCompound(NBTConstants.NBT_BLOCK_ENTITY_TAG).contains(NBTConstants.NBT_TANK)) return;

        FluidTank tank = new FluidTank(16000).readFromNBT(stack.getTag().getCompound(NBTConstants.NBT_BLOCK_ENTITY_TAG).getCompound(NBTConstants.NBT_TANK));
        FluidStack fluid = tank.getFluid();
        if (!fluid.isEmpty()) {
            tooltip.add(new TranslationTextComponent(fluid.getTranslationKey())
                    .append(": [" + tank.getFluidAmount() + "/" + tank.getCapacity() + "]")
                    .withStyle(TextFormatting.GOLD));
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new EnderBeeconTileEntity();
    }
}
