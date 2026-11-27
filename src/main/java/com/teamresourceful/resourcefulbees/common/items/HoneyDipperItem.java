package com.teamresourceful.resourcefulbees.common.items;

import com.teamresourceful.resourcefulbees.api.compat.CustomBee;
import com.teamresourceful.resourcefulbees.common.blockentities.ApiaryBlockEntity;
import com.teamresourceful.resourcefulbees.common.blockentities.ColonyControllerBlockEntity;
import com.teamresourceful.resourcefulbees.common.components.DipperEntity;
import com.teamresourceful.resourcefulbees.common.entities.entity.ResourcefulBee;
import com.teamresourceful.resourcefulbees.common.items.logistics.HoneyDipperCourierRecruitmentHandler;
import com.teamresourceful.resourcefulbees.common.items.logistics.HoneyDipperLogisticsHandler;
import com.teamresourceful.resourcefulbees.common.items.logistics.HoneyDipperRouteEditHandler;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.HoneyDipperTranslations;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModDataComponents;
import com.teamresourceful.resourcefulbees.mixin.common.BeeEntityAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.Objects;
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

        BlockPos clickedPos = context.getClickedPos();
        BlockEntity clickedTile = level.getBlockEntity(clickedPos);

        if (stack.has(ModDataComponents.DIPPER_ROUTE_EDIT.get())) {
            return HoneyDipperRouteEditHandler.handleEndpoint(context);
        }

        // Controller selection always takes precedence.
        if (clickedTile instanceof ColonyControllerBlockEntity controller) {

            DipperEntity selectedBee = stack.get(ModDataComponents.DIPPER_ENTITY);

            boolean hasBeeSelection = selectedBee != null
                    && !selectedBee.equals(DipperEntity.EMPTY);

            if (hasBeeSelection) {
                if (!(level instanceof ServerLevel serverLevel)) {
                    return InteractionResult.SUCCESS;
                }

                Entity entity = getEntity(serverLevel, stack);

                if (!(entity instanceof ResourcefulBee bee)) {
                    if (player != null) {
                        player.sendSystemMessage(Component.literal(
                                "Selected courier is unavailable or invalid."
                        ));
                    }
                    return InteractionResult.FAIL;
                }

                return HoneyDipperCourierRecruitmentHandler
                        .recruitOrTransfer(context, controller, bee);
            }

            // No selected bee: retain the existing logistics workflow.
            return HoneyDipperLogisticsHandler.selectController(
                    context, controller);
        }


        // Controller selected: reserve block interactions for logistics.
        if (HoneyDipperLogisticsHandler.hasSelection(stack)) {
            return HoneyDipperLogisticsHandler.handleEndpoint(context);
        }

        if (level instanceof ServerLevel serverLevel && player != null) {
            BlockState clickedBlock = level.getBlockState(clickedPos);

            Entity dipperEntity = getEntity(serverLevel, stack);

            if (!(dipperEntity instanceof Bee bee)) {
                return InteractionResult.FAIL;
            }

            if (bee instanceof CustomBee customBee) {
                if (customBee.getCoreData().isBlockFlower(clickedBlock)) {
                    setFlowerPosition(bee, context);
                    return InteractionResult.SUCCESS;
                }
            } else if (clickedBlock.is(BlockTags.FLOWERS)) {
                setFlowerPosition(bee, context);
                return InteractionResult.SUCCESS;
            }

            if (clickedTile instanceof BeehiveBlockEntity || clickedTile instanceof ApiaryBlockEntity) {
                bee.setHivePos(clickedPos);
                sendMessageToPlayer(bee, player, MessageTypes.HIVE, clickedPos);
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
        Player player = context.getPlayer();
        if (player != null) {
            sendMessageToPlayer(bee, player, MessageTypes.FLOWER, context.getClickedPos());
            player.setItemInHand(context.getHand(), setEntity(context.getItemInHand(), null));
        }
    }

    private void sendMessageToPlayer(@Nullable Bee bee, @NonNull Player playerEntity, MessageTypes messageTypes, @Nullable BlockPos pos) {
        switch (messageTypes) {
            case FLOWER, HIVE, FAKE_FLOWER -> playerEntity.sendSystemMessage(messageTypes.create(Objects.requireNonNull(bee, "Bee is required for flower, hive, and fake flower message types!").getDisplayName(), Objects.requireNonNull(pos, "Pos is required for flower, hive, and fake flower messages!").toShortString()));
            case BEE_CLEARED -> playerEntity.sendSystemMessage(messageTypes.create());
            case BEE_SELECTED -> playerEntity.sendSystemMessage(messageTypes.create(Objects.requireNonNull(bee, "Bee is required for bee_selected message type!").getDisplayName()));
            default -> throw new IllegalStateException("Unexpected value: " + messageTypes);
        }
    }

    public static boolean isHoldingHoneyDipper(Player player) {
        return player.getMainHandItem().getItem() instanceof HoneyDipperItem || player.getOffhandItem().getItem() instanceof HoneyDipperItem;
    }


    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack stack, @NotNull Player player, @NotNull LivingEntity entity, @NotNull InteractionHand hand) {
        if (player.level() instanceof ServerLevel serverLevel) {
            // Selecting a bee switches out of controller mode.
            if (entity instanceof Bee bee && HoneyDipperLogisticsHandler.hasSelection(stack)) {
                HoneyDipperLogisticsHandler.clearSelection(stack);
                player.setItemInHand(hand, setEntity(stack, bee));
                sendMessageToPlayer(bee, player, MessageTypes.BEE_SELECTED, null);
                return InteractionResult.SUCCESS;
            }

            Entity dipperEntity = getEntity(serverLevel, stack);

            if (entity instanceof Bee bee && dipperEntity == null) {
                player.setItemInHand(hand, setEntity(stack, bee));
                sendMessageToPlayer(bee, player, MessageTypes.BEE_SELECTED, null);
                return InteractionResult.SUCCESS;
            }

            if (dipperEntity instanceof ResourcefulBee bee && bee.getCoreData().isEntityFlower(entity.getType())) {
                bee.entityFlower = entity.getId();
                bee.setSavedFlowerPos(entity.blockPosition());
                sendMessageToPlayer(bee, player, MessageTypes.FLOWER, entity.blockPosition());
                player.setItemInHand(hand, setEntity(stack, null));
                return InteractionResult.SUCCESS;
            }
        }

        return super.interactLivingEntity(stack, player, entity, hand);
    }



    @Override
    public @NotNull InteractionResult use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.isShiftKeyDown()) {
            if (level instanceof ServerLevel) {
                boolean hadBee = stack.has(ModDataComponents.DIPPER_ENTITY.get());
                boolean hadController = HoneyDipperLogisticsHandler.hasSelection(stack);

                if (hadBee || hadController) {
                    stack.remove(ModDataComponents.DIPPER_ENTITY.get());
                    stack.remove(ModDataComponents.DIPPER_ROUTE_EDIT.get());
                    HoneyDipperLogisticsHandler.clearSelection(stack);
                    sendMessageToPlayer(null, player, MessageTypes.BEE_CLEARED, null);
                }
            }

            return InteractionResult.SUCCESS;
        }

        return super.use(level, player, hand);
    }


    private static @Nullable Entity getEntity(ServerLevel level, ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }

        DipperEntity dipperEntity = stack.get(ModDataComponents.DIPPER_ENTITY);

        if (dipperEntity == null || dipperEntity.equals(DipperEntity.EMPTY)) {
            return null;
        }

        return level.getEntity(dipperEntity.uuid());
    }


    private static ItemStack setEntity(ItemStack stack, @Nullable Entity entity) {
        if (entity == null) {
            stack.remove(ModDataComponents.DIPPER_ENTITY.get());
        } else {
            stack.set(ModDataComponents.DIPPER_ENTITY, DipperEntity.of(entity.getUUID()));
        }

        return stack;
    }


    @VisibleForDebug
    public static boolean hasBeeSelection(ItemStack stack) {
        DipperEntity selected = stack.get(ModDataComponents.DIPPER_ENTITY);
        return selected != null && !selected.equals(DipperEntity.EMPTY);
    }



    private enum MessageTypes {
        FLOWER(args -> Component.translatable(HoneyDipperTranslations.FLOWER_SET, args)),
        HIVE(args -> Component.translatable(HoneyDipperTranslations.HIVE_SET, args)),
        FAKE_FLOWER(args -> Component.translatable(HoneyDipperTranslations.FAKE_FLOWER_SET, args)),
        BEE_SELECTED(args -> Component.translatable(HoneyDipperTranslations.BEE_SET, args)),
        BEE_CLEARED(_ -> HoneyDipperTranslations.SELECTION_CLEARED);

        private final Function<Object[], MutableComponent> component;

        MessageTypes(Function<Object[], MutableComponent> component) {
            this.component = component;
        }

        public MutableComponent create(Object... args) {
            return this.component.apply(args);
        }
    }
}
