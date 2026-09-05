package com.teamresourceful.resourcefulbees.common.items;

import com.teamresourceful.resourcefulbees.common.components.BeeBoxOccupant;
import com.teamresourceful.resourcefulbees.common.components.BeeBoxOccupants;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModDataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class BeeBoxItem extends BlockItem {

    private final boolean temp;

    protected BeeBoxItem(Block block, Properties properties, boolean temp) {
        super(block, properties);
        this.temp = temp;
    }

    public static BeeBoxItem temp(Block block, Properties properties) {
        return new BeeBoxItem(block, properties, true);
    }

    public static BeeBoxItem of(Block block, Properties properties) {
        return new BeeBoxItem(block, properties, false);
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(
            @NotNull ItemStack stack,
            @NotNull Player player,
            @NotNull LivingEntity entity,
            @NotNull InteractionHand hand
    ) {
        if (this.temp
                || entity.level().isClientSide()
                || !(entity instanceof Bee target)
                || !target.isAlive()) {
            return InteractionResult.FAIL;
        }

        BeeBoxOccupants occupants = stack.getOrDefault(
                ModDataComponents.BEE_BOX_OCCUPANTS.get(),
                BeeBoxOccupants.EMPTY
        );

        if (occupants.isFull()) {
            return InteractionResult.FAIL;
        }

        stack.set(
                ModDataComponents.BEE_BOX_OCCUPANTS.get(),
                occupants.add(BeeBoxOccupant.from(target))
        );

        player.swing(hand);
        target.discard();

        return InteractionResult.SUCCESS;
    }

    public static boolean isFilled(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        BeeBoxOccupants occupants = stack.get(
                ModDataComponents.BEE_BOX_OCCUPANTS.get()
        );

        return occupants != null && !occupants.isEmpty();
    }
}