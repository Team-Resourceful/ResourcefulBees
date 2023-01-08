package com.teamresourceful.resourcefulbees.centrifuge.common.network.client;

import com.teamresourceful.resourcefulbees.common.menus.AbstractModContainerMenu;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.centrifuge.common.containers.CentrifugeInputContainer;
import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record SetFilterSlotPacket(ItemStack stack, int slot) implements Packet<SetFilterSlotPacket> {

    public static final ResourceLocation ID = new ResourceLocation(ModConstants.MOD_ID, "set_filter_slot");
    public static final SetFilterSlotPacket.Handler HANDLER = new SetFilterSlotPacket.Handler();

    @Override
    public ResourceLocation getID() {
        return null;
    }

    @Override
    public PacketHandler<SetFilterSlotPacket> getHandler() {
        return null;
    }

    private static class Handler implements PacketHandler<SetFilterSlotPacket> {

        @Override
        public void encode(SetFilterSlotPacket message, FriendlyByteBuf buffer) {
            buffer.writeItem(message.stack);
            buffer.writeShort(message.slot);
        }

        @Override
        public SetFilterSlotPacket decode(FriendlyByteBuf buffer) {
            return new SetFilterSlotPacket(buffer.readItem(), buffer.readShort());
        }

        @Override
        public PacketContext handle(SetFilterSlotPacket message) {
            return ((player, level) -> {
                if (player != null && player.containerMenu instanceof AbstractModContainerMenu<?>) {
                    ItemStack stack1 = message.stack;
                    stack1.setCount(Integer.MAX_VALUE);
                    player.containerMenu.getSlot(message.slot).set(stack1);
                    if (player.containerMenu instanceof CentrifugeInputContainer inputContainer) {
                        inputContainer.getEntity().updateRecipe();
                    }
                }
            });
        }
    }
}
