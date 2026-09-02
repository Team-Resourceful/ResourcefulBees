package com.teamresourceful.resourcefulbees.common.items.locator;

import com.teamresourceful.resourcefulbees.client.screen.locator.BeeLocatorScreen;
import com.teamresourceful.resourcefulbees.common.components.BeeLocatorData;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModDataComponents;
import com.teamresourceful.resourcefulbees.common.world.workers.LevelWorkManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public class BeeLocatorItem extends Item {

    private static final Set<SearchKey> ACTIVE_SEARCHES = new HashSet<>();

    public BeeLocatorItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        if (level.isClientSide()) {
            BeeLocatorScreen.openScreen(player, hand);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.SUCCESS_SERVER;
    }

    public static void run(Player player, Identifier bee, int slot) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        if (slot < 0 || slot >= player.getInventory().getContainerSize()) {
            return;
        }

        ItemStack stack = player.getInventory().getItem(slot);

        if (!(stack.getItem() instanceof BeeLocatorItem)) {
            return;
        }

        if (!player.getAbilities().instabuild && player.getCooldowns().isOnCooldown(stack)) {
            return;
        }

        SearchKey key = new SearchKey(player.getUUID(), slot);

        if (!ACTIVE_SEARCHES.add(key)) {
            return;
        }
        stack.set(ModDataComponents.BEE_LOCATOR_SEARCHING.get(), true);

        LevelWorkManager.addWork(new BeeLocatorWorker(serverPlayer, slot, bee, 100, () -> {
            ACTIVE_SEARCHES.remove(key);

            ItemStack currentStack = serverPlayer.getInventory().getItem(slot);

            if (currentStack.getItem() instanceof BeeLocatorItem) {
                currentStack.remove(ModDataComponents.BEE_LOCATOR_SEARCHING.get());
            }
        }));
    }

    @Override
    public void appendHoverText(ItemStack stack, @NonNull TooltipContext context, @NonNull TooltipDisplay display, @NonNull Consumer<Component> tooltip, @NonNull TooltipFlag flag) {
        if (Boolean.TRUE.equals(stack.get(ModDataComponents.BEE_LOCATOR_SEARCHING.get()))) {
            tooltip.accept(Component.translatable("tooltip.resourcefulbees.bee_locator.searching").withStyle(ChatFormatting.YELLOW));
            return;
        }

        BeeLocatorData data = stack.get(ModDataComponents.BEE_LOCATOR_DATA.get());

        if (data != null) {
            tooltip.accept(Component.translatable("tooltip.resourcefulbees.bee_locator.bee", data.bee().toString()).withStyle(ChatFormatting.GRAY));
            tooltip.accept(Component.translatable("tooltip.resourcefulbees.bee_locator.biome", data.biome().toString()).withStyle(ChatFormatting.GRAY));
            BlockPos pos = data.position();
            tooltip.accept(Component.translatable("tooltip.resourcefulbees.bee_locator.position", pos.getX(), pos.getY(), pos.getZ()).withStyle(ChatFormatting.GRAY));

            return;
        }

        tooltip.accept(Component.translatable("tooltip.resourcefulbees.bee_locator.not_searching").withStyle(ChatFormatting.DARK_GRAY));
    }

    private record SearchKey(UUID player, int slot) {}
}