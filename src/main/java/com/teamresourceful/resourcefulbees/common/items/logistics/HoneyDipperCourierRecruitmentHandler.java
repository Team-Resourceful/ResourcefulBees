
package com.teamresourceful.resourcefulbees.common.items.logistics;

import com.teamresourceful.resourcefulbees.common.blockentities.ColonyControllerBlockEntity;
import com.teamresourceful.resourcefulbees.common.entities.entity.ResourcefulBee;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModDataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;

public final class HoneyDipperCourierRecruitmentHandler {

    private HoneyDipperCourierRecruitmentHandler() {}

    public static InteractionResult recruitOrTransfer(
            UseOnContext context,
            ColonyControllerBlockEntity controller,
            ResourcefulBee bee
    ) {
        if (!(context.getLevel() instanceof ServerLevel)) {
            return InteractionResult.SUCCESS;
        }

        Player player = context.getPlayer();

        if (player == null) {
            return InteractionResult.FAIL;
        }

        if (bee.getCourierAbility() == null) {
            player.sendSystemMessage(Component.literal(
                    "This bee does not have the Courier ability."
            ));
            return InteractionResult.FAIL;
        }

        boolean alreadyMember =
                bee.belongsToColony(controller.getColonyId())
                        && context.getClickedPos().equals(
                        bee.getColonyControllerPos());

        boolean transferring = bee.isColonyMember()
                && !alreadyMember;

        if (!controller.recruitOrTransferCourier(bee)) {
            player.sendSystemMessage(Component.literal(
                    "Courier recruitment failed. Check colony "
                            + "range and controller availability."
            ));
            return InteractionResult.FAIL;
        }

        String message;

        if (alreadyMember) {
            message = "Courier already belongs to this colony.";
        } else if (transferring) {
            message = "Courier transferred to the selected colony.";
        } else {
            message = "Courier recruited into the selected colony.";
        }

        player.sendSystemMessage(Component.literal(message));

        ItemStack stack = context.getItemInHand();
        stack.remove(ModDataComponents.DIPPER_ENTITY.get());

        return InteractionResult.SUCCESS;
    }
}
