package com.teamresourceful.resourcefulbees.common.item;

import com.teamresourceful.resourcefulbees.api.beedata.CoreData;
import com.teamresourceful.resourcefulbees.common.entity.passive.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.mixin.accessors.BeeEntityAccessor;
import com.teamresourceful.resourcefulbees.common.blockentity.ApiaryBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
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
import java.util.Optional;

public class HoneyDipper extends Item {

    //TODO Is this correct? because Items are a single instance class - Gravy
    private @Nullable Bee selectedBee;

    public HoneyDipper(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext useContext) {
        if (!useContext.getLevel().isClientSide()) {
            Block clickedBlock = useContext.getLevel().getBlockState(useContext.getClickedPos()).getBlock();

            if (selectedBee instanceof CustomBeeEntity) {
                CoreData beeData = ((CustomBeeEntity) selectedBee).getCoreData();
                if (!beeData.getBlockFlowers().isEmpty() && beeData.getBlockFlowers().contains(clickedBlock)) {
                    setFlowerPosition(useContext);
                    return InteractionResult.SUCCESS;
                }
            } else if (selectedBee != null && BlockTags.FLOWERS.contains(clickedBlock)) {
                setFlowerPosition(useContext);
                return InteractionResult.SUCCESS;
            }

            BlockEntity clickedTile = useContext.getLevel().getBlockEntity(useContext.getClickedPos());
            if (selectedBee != null && (clickedTile instanceof BeehiveBlockEntity || clickedTile instanceof ApiaryBlockEntity)) {
                ((BeeEntityAccessor)selectedBee).setHivePos(useContext.getClickedPos());
                sendMessageToPlayer(useContext.getPlayer(), MessageTypes.HIVE, useContext.getClickedPos());
                selectedBee = null;
                return InteractionResult.SUCCESS;
            }
        }
        return super.useOn(useContext);
    }

    private void setFlowerPosition(UseOnContext useContext) {
        assert selectedBee != null : "setFlowerPos method called when selected bee was null";
        if (((BeeEntityAccessor)selectedBee).getPollinateGoal().isPollinating()) {
            ((BeeEntityAccessor)selectedBee).getPollinateGoal().stopPollinating();
        }
        selectedBee.setSavedFlowerPos(useContext.getClickedPos());
        sendMessageToPlayer(useContext.getPlayer(), MessageTypes.FLOWER, useContext.getClickedPos());
        selectedBee = null;
    }

    private void sendMessageToPlayer(Player playerEntity, MessageTypes messageTypes, BlockPos pos) {
        assert selectedBee != null : "bee went null before message was sent to player";
        switch (messageTypes) {
            case FLOWER, HIVE -> {
                String translation = messageTypes.equals(MessageTypes.FLOWER) ? TranslationConstants.HoneyDipper.FLOWER_SET : TranslationConstants.HoneyDipper.HIVE_SET;
                playerEntity.displayClientMessage(new TranslatableComponent(translation, selectedBee.getDisplayName(), NbtUtils.writeBlockPos(pos)), false);
            }
            case BEE_CLEARED -> playerEntity.displayClientMessage(TranslationConstants.HoneyDipper.SELECTION_CLEARED, false);
            case BEE_SELECTED -> playerEntity.displayClientMessage(new TranslatableComponent(TranslationConstants.HoneyDipper.BEE_SET, selectedBee.getDisplayName()), false);
        }
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack stack, @NotNull Player player, @NotNull LivingEntity entity, @NotNull InteractionHand hand) {
        if (!player.level.isClientSide()) {
            if (entity instanceof Bee bee && selectedBee == null) {
                selectedBee = bee;
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
        if (selectedBee instanceof CustomBeeEntity customBee) {
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
            textComponents.add(TranslationConstants.Items.HONEY_DIPPER_TOOLTIP
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
