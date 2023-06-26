package com.teamresourceful.resourcefulbees.common.items;

import com.teamresourceful.resourcefulbees.api.compat.CustomBee;
import com.teamresourceful.resourcefulbees.common.blockentities.ApiaryBlockEntity;
import com.teamresourceful.resourcefulbees.common.blockentities.FakeFlowerBlockEntity;
import com.teamresourceful.resourcefulbees.common.entities.entity.ResourcefulBee;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.HoneyDipperTranslations;
import com.teamresourceful.resourcefulbees.mixin.common.BeeEntityAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.Function;

public class HoneyDipperItem extends Item {

    public HoneyDipperItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        if (level instanceof ServerLevel serverLevel && player != null) {
            BlockState clickedBlock = level.getBlockState(context.getClickedPos());

            Entity entity = getEntity(serverLevel, context.getItemInHand());

            if (!(entity instanceof Bee bee)) return InteractionResult.FAIL;

            if (bee instanceof CustomBee customBee) {
                if (clickedBlock.is(customBee.getCoreData().flowers())) {
                    setFlowerPosition(bee, context);
                    return InteractionResult.SUCCESS;
                }
            } else if (clickedBlock.is(BlockTags.FLOWERS)) {
                setFlowerPosition(bee, context);
                return InteractionResult.SUCCESS;
            }

            BlockEntity clickedTile = level.getBlockEntity(context.getClickedPos());
            if (clickedTile instanceof BeehiveBlockEntity || clickedTile instanceof ApiaryBlockEntity) {
                ((BeeEntityAccessor)bee).setHivePos(context.getClickedPos());
                sendMessageToPlayer(bee, player, MessageTypes.HIVE, context.getClickedPos());
                player.setItemInHand(context.getHand(), setEntity(stack, null));
                return InteractionResult.SUCCESS;
            }

            if (clickedTile instanceof FakeFlowerBlockEntity && bee instanceof ResourcefulBee resourcefulBee) {
                resourcefulBee.fakeFlower.set(context.getClickedPos());
                sendMessageToPlayer(bee, player, MessageTypes.FAKE_FLOWER, context.getClickedPos());
                player.setItemInHand(context.getHand(), setEntity(stack, null));
                return InteractionResult.SUCCESS;
            }
        }
        return super.useOn(context);
    }

    private void setFlowerPosition(Bee bee, UseOnContext context) {
        if (((BeeEntityAccessor)bee).getPollinateGoal().isPollinating()) {
            ((BeeEntityAccessor)bee).getPollinateGoal().stopPollinating();
        }
        bee.setSavedFlowerPos(context.getClickedPos());
        sendMessageToPlayer(bee, context.getPlayer(), MessageTypes.FLOWER, context.getClickedPos());
        if (context.getPlayer() == null) return;
        context.getPlayer().setItemInHand(context.getHand(), setEntity(context.getItemInHand(), null));
    }

    private void sendMessageToPlayer(Bee bee, Player playerEntity, MessageTypes messageTypes, BlockPos pos) {
        switch (messageTypes) {
            case FLOWER, HIVE, FAKE_FLOWER -> playerEntity.displayClientMessage(messageTypes.create(bee.getDisplayName(), NbtUtils.writeBlockPos(pos)), false);
            case BEE_CLEARED -> playerEntity.displayClientMessage(messageTypes.create(), false);
            case BEE_SELECTED -> playerEntity.displayClientMessage(messageTypes.create(bee.getDisplayName()), false);
            default -> throw new IllegalStateException("Unexpected value: " + messageTypes);
        }
    }

    public static boolean isHoldingHoneyDipper(Player player) {
        return player.getMainHandItem().getItem() instanceof HoneyDipperItem || player.getOffhandItem().getItem() instanceof HoneyDipperItem;
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack stack, @NotNull Player player, @NotNull LivingEntity entity, @NotNull InteractionHand hand) {
        if (player.level() instanceof ServerLevel serverLevel) {
            Entity stackEntity = getEntity(serverLevel, stack);

            if (entity instanceof Bee bee && stackEntity == null) {
                player.setItemInHand(hand, setEntity(stack, entity));
                sendMessageToPlayer(bee, player, MessageTypes.BEE_SELECTED, null);
                return InteractionResult.SUCCESS;
            }

            if (stackEntity instanceof ResourcefulBee bee && bee.getCoreData().isEntityFlower(entity.getType())) {
                bee.entityFlower.set(entity.getId());
                bee.setSavedFlowerPos(entity.blockPosition());
                sendMessageToPlayer(bee, player, MessageTypes.FLOWER, entity.blockPosition());
                player.setItemInHand(hand, setEntity(stack, null));
                return InteractionResult.SUCCESS;
            }
        }
        return super.interactLivingEntity(stack, player, entity, hand);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        if (level instanceof ServerLevel serverLevel) {
            ItemStack itemInHand = player.getItemInHand(hand);
            if (player.isShiftKeyDown() && getEntity(serverLevel, itemInHand) != null) {
                player.setItemInHand(hand, setEntity(itemInHand, null));
                sendMessageToPlayer(null, player, MessageTypes.BEE_CLEARED, null);
            }
        }
        return super.use(level, player, hand);
    }

    private static Entity getEntity(ServerLevel level, ItemStack stack) {
        if (stack.isEmpty() || !stack.hasTag()) return null;
        CompoundTag stackTag = stack.getOrCreateTag();
        UUID uuid = stackTag.hasUUID(NBTConstants.HoneyDipper.Entity) ? stackTag.getUUID(NBTConstants.HoneyDipper.Entity) : null;
        return uuid != null ? level.getEntity(uuid) : null;
    }

    private static ItemStack setEntity(ItemStack stack, Entity entity) {
        if (entity == null) {
            stack.setTag(null);
        }else {
            CompoundTag stackTag = stack.getOrCreateTag();
            stackTag.putUUID(NBTConstants.HoneyDipper.Entity, entity.getUUID());
            stack.setTag(stackTag);
        }
        return stack;
    }

    private enum MessageTypes {
        FLOWER(args -> Component.translatable(HoneyDipperTranslations.FLOWER_SET, args)),
        HIVE(args -> Component.translatable(HoneyDipperTranslations.HIVE_SET, args)),
        FAKE_FLOWER(args -> Component.translatable(HoneyDipperTranslations.FAKE_FLOWER_SET, args)),
        BEE_SELECTED(args -> Component.translatable(HoneyDipperTranslations.BEE_SET, args)),
        BEE_CLEARED(args -> HoneyDipperTranslations.SELECTION_CLEARED);

        private final Function<Object[], MutableComponent> component;

        MessageTypes(Function<Object[], MutableComponent> component) {
            this.component = component;
        }

        public MutableComponent create(Object... args) {
            return this.component.apply(args);
        }
    }
}
