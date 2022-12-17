package com.teamresourceful.resourcefulbees.common.block;

import com.teamresourceful.resourcefulbees.common.blocks.base.TickingBlock;
import com.teamresourceful.resourcefulbees.common.blockentity.EnderBeeconBlockEntity;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlockEntityTypes;
import com.teamresourceful.resourcefulbees.common.utils.FluidUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EnderBeecon extends TickingBlock<EnderBeeconBlockEntity> {

    protected static final VoxelShape VOXEL_SHAPE_TOP = Util.make(() -> {
        VoxelShape shape = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 1.0D, 14.0D);
        shape = Shapes.join(shape, Block.box(3.0D, 1.0D, 3.0D, 13.0D, 3.0D, 13.0D), BooleanOp.OR);
        shape = Shapes.join(shape, Block.box(4.0D, 3.0D, 4.0D, 12.0D, 11.0D, 12.0D), BooleanOp.OR);
        shape = Shapes.join(shape, Block.box(3.0D, 11.0D, 3.0D, 13.0D, 13.0D, 13.0D), BooleanOp.OR);
        return shape;
    });

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty BEAM = BooleanProperty.create("beam");
    public static final BooleanProperty SOUND = BooleanProperty.create("sound");

    public static final Properties PROPERTIES = Properties.of(Material.GLASS, MaterialColor.COLOR_PURPLE)
            .requiresCorrectToolForDrops()
            .strength(5)
            .sound(SoundType.LODESTONE)
            .lightLevel(luminance -> 15)
            .noOcclusion()
            .dynamicShape();

    public EnderBeecon() {
        super(ModBlockEntityTypes.ENDER_BEECON_TILE_ENTITY, PROPERTIES);
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false).setValue(BEAM, true).setValue(SOUND, true));
    }

    @NotNull
    @Override
    @Deprecated
    public InteractionResult use(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult rayTraceResult) {
        if (level.getBlockEntity(pos) instanceof EnderBeeconBlockEntity beecon) {
            if (!level.isClientSide) {
                ItemStack stack = player.getItemInHand(hand);

                if (stack.is(ItemTags.WOOL)) {
                    level.setBlock(pos, state.cycle(SOUND), Block.UPDATE_ALL);
                } else if (stack.is(Items.STICK)) {
                    level.setBlock(pos, state.cycle(BEAM), Block.UPDATE_ALL);
                } else {
                    FluidUtils.checkBottleAndCapability(beecon.getTank(), beecon, player, level, pos, hand);
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.use(state, level, pos, player, hand, rayTraceResult);
    }

    @NotNull
    @Override
    @Deprecated
    public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter worldIn, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return VOXEL_SHAPE_TOP;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED).add(BEAM).add(SOUND);
    }

    @NotNull
    @Override
    public FluidState getFluidState(BlockState state) {
        return Boolean.TRUE.equals(state.getValue(BlockStateProperties.WATERLOGGED)) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
    }

    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        return this.defaultBlockState();
    }

    @NotNull
    @Override
    public BlockState updateShape(BlockState stateIn, @NotNull Direction facing, @NotNull BlockState facingState, @NotNull LevelAccessor world, @NotNull BlockPos currentPos, @NotNull BlockPos facingPos) {
        if (Boolean.TRUE.equals(stateIn.getValue(BlockStateProperties.WATERLOGGED))) {
            world.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }
        return stateIn;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable BlockGetter worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        tooltip.add(TranslationConstants.Items.BEECON_TOOLTIP.withStyle(ChatFormatting.LIGHT_PURPLE));
        tooltip.add(TranslationConstants.Items.BEECON_TOOLTIP_1.withStyle(ChatFormatting.LIGHT_PURPLE));

        if (!stack.hasTag()) return;
        if (!stack.getTag().contains(NBTConstants.NBT_BLOCK_ENTITY_TAG)) return;
        if (!stack.getTag().getCompound(NBTConstants.NBT_BLOCK_ENTITY_TAG).contains(NBTConstants.NBT_TANK)) return;

        FluidTank tank = new FluidTank(16000).readFromNBT(stack.getTag().getCompound(NBTConstants.NBT_BLOCK_ENTITY_TAG).getCompound(NBTConstants.NBT_TANK));
        FluidStack fluid = tank.getFluid();
        if (!fluid.isEmpty()) {
            tooltip.add(Component.translatable(fluid.getTranslationKey())
                    .append(": [" + tank.getFluidAmount() + "/" + tank.getCapacity() + "]")
                    .withStyle(ChatFormatting.GOLD));
        }
    }
}
