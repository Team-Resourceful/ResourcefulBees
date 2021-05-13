package com.resourcefulbees.resourcefulbees.block;

import com.resourcefulbees.resourcefulbees.registry.ModBlockEntityTypes;
import com.resourcefulbees.resourcefulbees.tileentity.EnderBeeconTileEntity;
import com.resourcefulbees.resourcefulbees.tileentity.HoneyTankTileEntity;
import com.resourcefulbees.resourcefulbees.utils.TooltipBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.fml.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EnderBeecon extends HoneyTank {

    protected static final VoxelShape VOXEL_SHAPE_TOP = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 13.0D, 14.0D);

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public static final BlockBehaviour.Properties PROPERTIES = BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_PURPLE)
            .requiresCorrectToolForDrops()
            .harvestTool(ToolType.PICKAXE)
            .strength(5)
            .harvestLevel(2)
            .sound(SoundType.LODESTONE)
            .lightLevel(luminance -> 15)
            .noOcclusion()
            .dynamicShape();

    public EnderBeecon(BlockBehaviour.Properties properties) {
        super(properties, HoneyTankTileEntity.TankTier.NETHER);
        BlockState defaultState = this.stateDefinition.any()
                .setValue(WATERLOGGED, false);
        this.registerDefaultState(defaultState);
    }

    @NotNull
    @Override
    @Deprecated
    public InteractionResult use(@NotNull BlockState state, Level world, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult rayTraceResult) {

        ItemStack heldItem = player.getItemInHand(hand);
        boolean usingHoney = heldItem.getItem() instanceof HoneyBottleItem;
        boolean usingBottle = heldItem.getItem() instanceof BottleItem;
        boolean hasCapability = heldItem.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent();
        boolean usingWool = heldItem.getItem().is(ItemTags.createOptional(new ResourceLocation("minecraft", "wool")));
        boolean usingStick = heldItem.getItem() == Items.STICK;
        BlockEntity tileEntity = world.getBlockEntity(pos);

        if (tileEntity instanceof EnderBeeconTileEntity) {
            EnderBeeconTileEntity beecon = (EnderBeeconTileEntity) tileEntity;
            if (!world.isClientSide) {
                if (usingWool) {
                    beecon.toggleSound();
                } else if (usingStick) {
                    beecon.toggleBeam();
                } else if (hasCapability) {
                    tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
                            .ifPresent(iFluidHandler -> FluidUtil.interactWithFluidHandler(player, hand, world, pos, null));
                } else if (usingBottle) {
                    beecon.fillBottle(player, hand);
                } else if (usingHoney) {
                    beecon.emptyBottle(player, hand);
                } else if (!player.isShiftKeyDown()) {
                    NetworkHooks.openGui((ServerPlayer) player, (MenuProvider) tileEntity, pos);
                }
            }
            return InteractionResult.SUCCESS;
        }
        return super.use(state, world, pos, player, hand, rayTraceResult);
    }

    @Override
    public @Nullable BlockEntity createTileEntity(BlockState state, BlockGetter world) {
        return new EnderBeeconTileEntity(ModBlockEntityTypes.ENDER_BEECON_TILE_ENTITY.get());
    }

    @NotNull
    @Override
    @Deprecated
    public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter worldIn, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return VOXEL_SHAPE_TOP;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @javax.annotation.Nullable BlockGetter worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        tooltip.addAll(new TooltipBuilder()
                .addTranslatableTip("block.resourcefulbees.beecon.tooltip.info", ChatFormatting.LIGHT_PURPLE)
                .addTranslatableTip("block.resourcefulbees.beecon.tooltip.info.1", ChatFormatting.LIGHT_PURPLE)
                .build());
        if (!stack.hasTag() || stack.getTag() == null || stack.getTag().isEmpty() || !stack.getTag().contains("fluid"))
            return;
        FluidTank tank = new FluidTank(16000).readFromNBT(stack.getTag().getCompound("fluid"));
        FluidStack fluid = tank.getFluid();
        if (!fluid.isEmpty()) {
            tooltip.addAll(new TooltipBuilder()
                    .addTip(I18n.get(fluid.getTranslationKey()))
                    .appendText(": [" + tank.getFluidAmount() + "/" + tank.getCapacity() + "]")
                    .applyStyle(ChatFormatting.GOLD).build());
        }
    }

    @Override
    public void setPlacedBy(Level world, @NotNull BlockPos pos, @NotNull BlockState blockState, @Nullable LivingEntity livingEntity, @NotNull ItemStack itemStack) {
        BlockEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof EnderBeeconTileEntity) {
            EnderBeeconTileEntity tank = (EnderBeeconTileEntity) tileEntity;
            if (itemStack.getTag() != null) {
                tank.readNBT(itemStack.getTag());
            }
        }
    }



    @Override
    public ItemStack getPickBlock(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
        BlockEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof EnderBeeconTileEntity) {
            EnderBeeconTileEntity tank = (EnderBeeconTileEntity) tileEntity;
            ItemStack stack = new ItemStack(state.getBlock().asItem());
            stack.setTag(tank.writeNBT(new CompoundTag()));
            return stack;
        }
        return new ItemStack(state.getBlock().asItem());
    }
}
