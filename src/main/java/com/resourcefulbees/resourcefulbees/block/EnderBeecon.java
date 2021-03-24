package com.resourcefulbees.resourcefulbees.block;

import com.resourcefulbees.resourcefulbees.registry.ModTileEntityTypes;
import com.resourcefulbees.resourcefulbees.tileentity.EnderBeeconTileEntity;
import com.resourcefulbees.resourcefulbees.tileentity.HoneyTankTileEntity;
import com.resourcefulbees.resourcefulbees.utils.TooltipBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.ItemTags;
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
import net.minecraft.world.World;
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

import javax.annotation.Nonnull;
import java.util.List;

import net.minecraft.block.AbstractBlock.Properties;

public class EnderBeecon extends HoneyTank {

    protected static final VoxelShape VOXEL_SHAPE_TOP = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 13.0D, 14.0D);

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public static final AbstractBlock.Properties PROPERTIES = AbstractBlock.Properties.of(Material.GLASS, MaterialColor.COLOR_PURPLE)
            .requiresCorrectToolForDrops()
            .harvestTool(ToolType.PICKAXE)
            .strength(5)
            .harvestLevel(2)
            .sound(SoundType.LODESTONE)
            .lightLevel(luminance -> 15)
            .noOcclusion()
            .dynamicShape();

    public EnderBeecon(Properties properties) {
        super(properties, HoneyTankTileEntity.TankTier.NETHER);
        BlockState defaultState = this.stateDefinition.any()
                .setValue(WATERLOGGED, false);
        this.registerDefaultState(defaultState);
    }

    @Nonnull
    @Override
    @Deprecated
    public ActionResultType use(@Nonnull BlockState state, World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull BlockRayTraceResult rayTraceResult) {

        ItemStack heldItem = player.getItemInHand(hand);
        boolean usingHoney = heldItem.getItem() instanceof HoneyBottleItem;
        boolean usingBottle = heldItem.getItem() instanceof GlassBottleItem;
        boolean hasCapability = heldItem.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent();
        boolean usingWool = heldItem.getItem().is(ItemTags.createOptional(new ResourceLocation("minecraft", "wool")));
        boolean usingStick = heldItem.getItem() == Items.STICK;
        TileEntity tileEntity = world.getBlockEntity(pos);

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
                    NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) tileEntity, pos);
                }
            }
            return ActionResultType.SUCCESS;
        }
        return super.use(state, world, pos, player, hand, rayTraceResult);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new EnderBeeconTileEntity(ModTileEntityTypes.ENDER_BEECON_TILE_ENTITY.get());
    }

    @NotNull
    @Override
    @Deprecated
    public VoxelShape getShape(@NotNull BlockState state, @NotNull IBlockReader worldIn, @NotNull BlockPos pos, @NotNull ISelectionContext context) {
        return VOXEL_SHAPE_TOP;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @javax.annotation.Nullable IBlockReader worldIn, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flagIn) {
        tooltip.addAll(new TooltipBuilder()
                .addTranslatableTip("block.resourcefulbees.beecon.tooltip.info", TextFormatting.LIGHT_PURPLE)
                .addTranslatableTip("block.resourcefulbees.beecon.tooltip.info.1", TextFormatting.LIGHT_PURPLE)
                .build());
        if (!stack.hasTag() || stack.getTag() == null || stack.getTag().isEmpty() || !stack.getTag().contains("fluid"))
            return;
        FluidTank tank = new FluidTank(16000).readFromNBT(stack.getTag().getCompound("fluid"));
        FluidStack fluid = tank.getFluid();
        if (!fluid.isEmpty()) {
            tooltip.addAll(new TooltipBuilder()
                    .addTip(I18n.get(fluid.getTranslationKey()))
                    .appendText(": [" + tank.getFluidAmount() + "/" + tank.getCapacity() + "]")
                    .applyStyle(TextFormatting.GOLD).build());
        }
    }

    @Override
    public void setPlacedBy(World world, @NotNull BlockPos pos, @NotNull BlockState blockState, @Nullable LivingEntity livingEntity, @NotNull ItemStack itemStack) {
        TileEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof EnderBeeconTileEntity) {
            EnderBeeconTileEntity tank = (EnderBeeconTileEntity) tileEntity;
            if (itemStack.getTag() != null) {
                tank.readNBT(itemStack.getTag());
            }
        }
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        TileEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof EnderBeeconTileEntity) {
            EnderBeeconTileEntity tank = (EnderBeeconTileEntity) tileEntity;
            ItemStack stack = new ItemStack(state.getBlock().asItem());
            stack.setTag(tank.writeNBT(new CompoundNBT()));
            return stack;
        }
        return new ItemStack(state.getBlock().asItem());
    }
}
