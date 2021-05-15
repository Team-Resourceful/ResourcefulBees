package com.teamresourceful.resourcefulbees.block;

import com.teamresourceful.resourcefulbees.registry.ModBlockEntityTypes;
import com.teamresourceful.resourcefulbees.tileentity.CentrifugeTileEntity;
import com.teamresourceful.resourcefulbees.utils.TooltipBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings("deprecation")
public class CentrifugeBlock extends Block {
    public static final BooleanProperty PROPERTY_ON = BooleanProperty.create("on");

    public CentrifugeBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(PROPERTY_ON,false));
    }

    @NotNull
    @Override
    public InteractionResult use(@Nonnull BlockState state, Level world, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult rayTraceResult) {
        ItemStack heldItem = player.getItemInHand(hand);
        boolean hasCapability = heldItem.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent();
        BlockEntity tileEntity = world.getBlockEntity(pos);

        if (tileEntity instanceof CentrifugeTileEntity) {
            capabilityOrGuiUse(tileEntity, player, world, pos, hand);
            return InteractionResult.SUCCESS;
        }

        return super.use(state, world, pos, player, hand, rayTraceResult);
    }

    public static void capabilityOrGuiUse(BlockEntity tileEntity, Player player, Level world, BlockPos pos, InteractionHand hand){
        if (player.getItemInHand(hand).getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent()) {
            tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
                    .ifPresent(iFluidHandler -> FluidUtil.interactWithFluidHandler(player, hand, world, pos, null));
        } else if (!player.isShiftKeyDown() && !world.isClientSide) {
            NetworkHooks.openGui((ServerPlayer) player, (MenuProvider) tileEntity, pos);
        }
    }

    @Nullable
    @Override
    public MenuProvider getMenuProvider(@NotNull BlockState state, Level worldIn, @NotNull BlockPos pos) {
        return (MenuProvider)worldIn.getBlockEntity(pos);
    }

    @Override
    public void neighborChanged(@Nonnull BlockState state, Level world, @Nonnull BlockPos pos, @Nonnull Block changedBlock, @Nonnull BlockPos changedBlockPos, boolean bool) {
        BlockEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof CentrifugeTileEntity) {
            CentrifugeTileEntity centrifugeTileEntity = (CentrifugeTileEntity) tileEntity;
            centrifugeTileEntity.setIsPoweredByRedstone(world.hasNeighborSignal(pos));
        }
    }

    @Override
    public void onRemove(@NotNull BlockState state1, Level world, @NotNull BlockPos pos, @NotNull BlockState state, boolean isMoving) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof CentrifugeTileEntity && state.getBlock() != state1.getBlock()){
            ((CentrifugeTileEntity) blockEntity).dropInventory(world, pos);
        }
        super.onRemove(state1, world, pos, state, isMoving);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
        return new CentrifugeTileEntity(ModBlockEntityTypes.CENTRIFUGE_ENTITY.get());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PROPERTY_ON);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable BlockGetter worldIn, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flagIn) {
            tooltip.addAll(new TooltipBuilder()
                    .addTip(I18n.get("block.resourcefulbees.centrifuge.tooltip.info"), ChatFormatting.GOLD)
                    .build());
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }
}



