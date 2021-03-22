package com.resourcefulbees.resourcefulbees.item;

import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.entity.passive.CustomBeeEntity;
import com.resourcefulbees.resourcefulbees.tileentity.multiblocks.apiary.ApiaryTileEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HoneyDipper extends Item {

    private @Nullable Bee selectedBee;

    public HoneyDipper(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext useContext) {
        if (!useContext.getLevel().isClientSide()) {
            Block clickedBlock = useContext.getLevel().getBlockState(useContext.getClickedPos()).getBlock();

            if (selectedBee instanceof CustomBeeEntity) {
                CustomBeeData beeData = ((CustomBeeEntity) selectedBee).getBeeData();
                if (beeData.hasBlockFlowers() && beeData.getBlockFlowers().contains(clickedBlock)) {
                    setFlowerPosition(useContext);
                    return InteractionResult.SUCCESS;
                }
            } else if (selectedBee != null && clickedBlock.is(BlockTags.FLOWERS)) {
                setFlowerPosition(useContext);
                return InteractionResult.SUCCESS;
            }

            BlockEntity clickedTile = useContext.getLevel().getBlockEntity(useContext.getClickedPos());
            if (selectedBee != null && (clickedTile instanceof BeehiveBlockEntity || clickedTile instanceof ApiaryTileEntity)) {
                selectedBee.hivePos = useContext.getClickedPos();
                sendMessageToPlayer(useContext.getPlayer(), MessageTypes.HIVE, useContext.getClickedPos());
                selectedBee = null;
                return InteractionResult.SUCCESS;
            }
        }
        return super.useOn(useContext);
    }

    private void setFlowerPosition(UseOnContext useContext) {
        assert selectedBee != null : "setFlowerPos method called when selected bee was null";
        if (selectedBee.beePollinateGoal.isPollinating()) {
            selectedBee.beePollinateGoal.stopPollinating();
        }
        selectedBee.setSavedFlowerPos(useContext.getClickedPos());
        sendMessageToPlayer(useContext.getPlayer(), MessageTypes.FLOWER, useContext.getClickedPos());
        selectedBee = null;
    }

    private void sendMessageToPlayer(Player playerEntity, MessageTypes messageTypes, BlockPos pos) {
        assert selectedBee != null : "bee went null before message was sent to player";
        switch (messageTypes) {
            case HIVE:
                playerEntity.displayClientMessage(new TextComponent(String.format("Hive position for [%1$s] has been set to %2$s", selectedBee.getDisplayName().getString(), NbtUtils.writeBlockPos(pos))), false);
                break;
            case FLOWER:
                playerEntity.displayClientMessage(new TextComponent(String.format("Flower position for [%1$s] has been set to %2$s", selectedBee.getDisplayName().getString(), NbtUtils.writeBlockPos(pos))), false);
                break;
            case BEE_CLEARED:
                playerEntity.displayClientMessage(new TextComponent("Bee Selection Cleared!"), false);
                break;
            case BEE_SELECTED:
                playerEntity.displayClientMessage(new TextComponent(String.format("[%1$s] has been selected!", selectedBee.getDisplayName().getString())), false);
                break;
            default: //Do Nothing
        }
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack stack, @NotNull Player player, @NotNull LivingEntity entity, @NotNull InteractionHand hand) {
        if (!player.level.isClientSide()) {
            if (entity instanceof Bee && selectedBee == null) {
                selectedBee = (Bee) entity;
                sendMessageToPlayer(player, MessageTypes.BEE_SELECTED, null);
                return InteractionResult.SUCCESS;
            }

            if (setEntityFlowerPos(player, entity)) {
                return InteractionResult.SUCCESS;
            }
        }
        return super.interactLivingEntity(stack, player, entity, hand);
    }

    private boolean setEntityFlowerPos(Player player, LivingEntity entity) {
        if (selectedBee instanceof CustomBeeEntity) {
            CustomBeeEntity customBee = (CustomBeeEntity) selectedBee;
            if (customBee.getBeeData().hasEntityFlower()) {
                ResourceLocation resourceLocation = customBee.getBeeData().getEntityFlower();
                if (entity.getType().getRegistryName() != null && entity.getType().getRegistryName().equals(resourceLocation)) {
                    customBee.setFlowerEntityID(entity.getId());
                    customBee.setSavedFlowerPos(entity.blockPosition());
                    sendMessageToPlayer(player, MessageTypes.FLOWER, entity.blockPosition());
                    selectedBee = null;
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level world, @NotNull Player player, @NotNull InteractionHand hand) {
        if (!world.isClientSide() && player.isShiftKeyDown() && selectedBee != null) {
            selectedBee = null;
            sendMessageToPlayer(player, MessageTypes.BEE_CLEARED, null);
        }
        return super.use(world, player, hand);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level world, @NotNull List<Component> textComponents, @NotNull TooltipFlag flag) {
        if (selectedBee != null) {
            textComponents.add(new TranslatableComponent("item.resourcefulbees.honey_dipper.tooltip")
                    .append(new TextComponent(selectedBee.getDisplayName().getString()))
                    .withStyle(ChatFormatting.GOLD));
        }
    }

    private enum MessageTypes {
        FLOWER,
        HIVE,
        BEE_SELECTED,
        BEE_CLEARED
    }
}
