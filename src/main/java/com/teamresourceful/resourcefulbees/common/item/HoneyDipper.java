package com.teamresourceful.resourcefulbees.common.item;

import com.teamresourceful.resourcefulbees.api.beedata.CoreData;
import com.teamresourceful.resourcefulbees.common.entity.passive.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.mixin.accessors.BeeEntityAccessor;
import com.teamresourceful.resourcefulbees.common.tileentity.multiblocks.apiary.ApiaryTileEntity;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class HoneyDipper extends Item {

    private @Nullable BeeEntity selectedBee;

    public HoneyDipper(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull ActionResultType useOn(@NotNull ItemUseContext useContext) {
        if (!useContext.getLevel().isClientSide()) {
            Block clickedBlock = useContext.getLevel().getBlockState(useContext.getClickedPos()).getBlock();

            if (selectedBee instanceof CustomBeeEntity) {
                CoreData beeData = ((CustomBeeEntity) selectedBee).getCoreData();
                if (!beeData.getBlockFlowers().isEmpty() && beeData.getBlockFlowers().contains(clickedBlock)) {
                    setFlowerPosition(useContext);
                    return ActionResultType.SUCCESS;
                }
            } else if (selectedBee != null && clickedBlock.is(BlockTags.FLOWERS)) {
                setFlowerPosition(useContext);
                return ActionResultType.SUCCESS;
            }

            TileEntity clickedTile = useContext.getLevel().getBlockEntity(useContext.getClickedPos());
            if (selectedBee != null && (clickedTile instanceof BeehiveTileEntity || clickedTile instanceof ApiaryTileEntity)) {
                ((BeeEntityAccessor)selectedBee).setHivePos(useContext.getClickedPos());
                sendMessageToPlayer(useContext.getPlayer(), MessageTypes.HIVE, useContext.getClickedPos());
                selectedBee = null;
                return ActionResultType.SUCCESS;
            }
        }
        return super.useOn(useContext);
    }

    private void setFlowerPosition(ItemUseContext useContext) {
        assert selectedBee != null : "setFlowerPos method called when selected bee was null";
        if (((BeeEntityAccessor)selectedBee).getPollinateGoal().isPollinating()) {
            ((BeeEntityAccessor)selectedBee).getPollinateGoal().stopPollinating();
        }
        selectedBee.setSavedFlowerPos(useContext.getClickedPos());
        sendMessageToPlayer(useContext.getPlayer(), MessageTypes.FLOWER, useContext.getClickedPos());
        selectedBee = null;
    }

    private void sendMessageToPlayer(PlayerEntity playerEntity, MessageTypes messageTypes, BlockPos pos) {
        assert selectedBee != null : "bee went null before message was sent to player";
        switch (messageTypes) {
            case FLOWER:
            case HIVE:
                String translation = messageTypes.equals(MessageTypes.FLOWER) ? TranslationConstants.HoneyDipper.FLOWER_SET : TranslationConstants.HoneyDipper.HIVE_SET;
                playerEntity.displayClientMessage(new TranslationTextComponent(translation, selectedBee.getDisplayName(), NBTUtil.writeBlockPos(pos)), false);
                break;
            case BEE_CLEARED:
                playerEntity.displayClientMessage(TranslationConstants.HoneyDipper.SELECTION_CLEARED, false);
                break;
            case BEE_SELECTED:
                playerEntity.displayClientMessage(new TranslationTextComponent(TranslationConstants.HoneyDipper.BEE_SET, selectedBee.getDisplayName()), false);
                break;
            default: //Do Nothing
        }
    }

    @Override
    public @NotNull ActionResultType interactLivingEntity(@NotNull ItemStack stack, @NotNull PlayerEntity player, @NotNull LivingEntity entity, @NotNull Hand hand) {
        if (!player.level.isClientSide()) {
            if (entity instanceof BeeEntity && selectedBee == null) {
                selectedBee = (BeeEntity) entity;
                sendMessageToPlayer(player, MessageTypes.BEE_SELECTED, null);
                return ActionResultType.SUCCESS;
            }

            if (setEntityFlowerPos(player, entity)) {
                return ActionResultType.SUCCESS;
            }
        }
        return super.interactLivingEntity(stack, player, entity, hand);
    }

    private boolean setEntityFlowerPos(PlayerEntity player, LivingEntity entity) {
        if (selectedBee instanceof CustomBeeEntity) {
            CustomBeeEntity customBee = (CustomBeeEntity) selectedBee;
            if (entityTypesMatch(entity, customBee.getCoreData().getEntityFlower())) {
                customBee.setFlowerEntityID(entity.getId());
                customBee.setSavedFlowerPos(entity.blockPosition());
                sendMessageToPlayer(player, MessageTypes.FLOWER, entity.blockPosition());
                selectedBee = null;
                return true;
            }
        }
        return false;
    }

    private boolean entityTypesMatch(LivingEntity entity, Optional<EntityType<?>> entityType) {
        return entityType.isPresent() && entity.getType().getRegistryName() != null && entity.getType().getRegistryName().equals(entityType.get().getRegistryName());
    }

    @Override
    public @NotNull ActionResult<ItemStack> use(@NotNull World world, @NotNull PlayerEntity player, @NotNull Hand hand) {
        if (!world.isClientSide() && player.isShiftKeyDown() && selectedBee != null) {
            selectedBee = null;
            sendMessageToPlayer(player, MessageTypes.BEE_CLEARED, null);
        }
        return super.use(world, player, hand);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable World world, @NotNull List<ITextComponent> textComponents, @NotNull ITooltipFlag flag) {
        if (selectedBee != null) {
            textComponents.add(TranslationConstants.Items.HONEY_DIPPER_TOOLTIP
                    .append(new StringTextComponent(selectedBee.getDisplayName().getString()))
                    .withStyle(TextFormatting.GOLD));
        }
    }

    private enum MessageTypes {
        FLOWER,
        HIVE,
        BEE_SELECTED,
        BEE_CLEARED
    }
}
