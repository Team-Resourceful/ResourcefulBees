package com.resourcefulbees.resourcefulbees.block.multiblocks.apiary;

import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.tileentity.multiblocks.apiary.ApiaryBreederTileEntity;
import com.resourcefulbees.resourcefulbees.utils.TooltipBuilder;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings("deprecation")
public class ApiaryBreederBlock extends Block {

    public ApiaryBreederBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any());
    }



    @Nonnull
    @Override
    public InteractionResult use(@Nonnull BlockState state, @NotNull Level world, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult blockRayTraceResult) {
        if (!player.isShiftKeyDown() && !world.isClientSide) {
            MenuProvider blockEntity = state.getMenuProvider(world,pos);
            NetworkHooks.openGui((ServerPlayer) player, blockEntity, pos);
        }
        return InteractionResult.SUCCESS;
    }


    @Nullable
    @Override
    public MenuProvider getMenuProvider(@Nonnull BlockState state, Level worldIn, @Nonnull BlockPos pos) {
        return (MenuProvider)worldIn.getBlockEntity(pos);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
        return new ApiaryBreederTileEntity();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable BlockGetter worldIn, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flagIn) {
        tooltip.addAll(new TooltipBuilder()
                .addTip(I18n.get("block.resourcefulbees.apiary_breeder.tooltip.info"), ChatFormatting.GOLD)
                .addTip(I18n.get("block.resourcefulbees.apiary_breeder.tooltip.info1"), ChatFormatting.GOLD)
                .appendText(String.format("%1$s ticks", Config.APIARY_MAX_BREED_TIME.get()), ChatFormatting.GOLD)
                .addTip(I18n.get("block.resourcefulbees.apiary_breeder.tooltip.info2"), ChatFormatting.GOLD)
                .build());
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }
}
