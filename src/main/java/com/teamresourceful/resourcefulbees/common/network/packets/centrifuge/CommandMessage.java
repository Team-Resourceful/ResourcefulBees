package com.teamresourceful.resourcefulbees.common.network.packets.centrifuge;

import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.commands.CentrifugeCommandHolder;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.commands.CentrifugeCommandSource;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base.AbstractCentrifugeEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class CommandMessage {

    private final BlockPos pos;
    private final String command;

    public CommandMessage(BlockPos pos, String command) {
        this.pos = pos;
        this.command = command;
    }

    public static void encode(CommandMessage message, PacketBuffer buffer){
        buffer.writeBlockPos(message.pos);
        buffer.writeUtf(message.command);
    }

    public static CommandMessage decode(PacketBuffer buffer){
        return new CommandMessage(buffer.readBlockPos(), buffer.readUtf());
    }

    public static void handle(CommandMessage message, Supplier<NetworkEvent.Context> context){
        context.get().enqueueWork(() -> {
            ServerPlayerEntity player = context.get().getSender();
            if (player != null && player.level.isLoaded(message.pos)) {
                TileEntity tileEntity = player.level.getBlockEntity(message.pos);
                if (tileEntity instanceof AbstractCentrifugeEntity) {
                    AbstractCentrifugeEntity centrifuge = (AbstractCentrifugeEntity) tileEntity;
                    CentrifugeCommandHolder.callDispatcher(message.command, new CentrifugeCommandSource(centrifuge.getController(), player));
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}
