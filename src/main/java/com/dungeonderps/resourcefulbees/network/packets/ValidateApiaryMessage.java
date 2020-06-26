package com.dungeonderps.resourcefulbees.network.packets;

import com.dungeonderps.resourcefulbees.tileentity.beehive.ApiaryTileEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ValidateApiaryMessage {

    private final BlockPos pos;

    public ValidateApiaryMessage(BlockPos pos){
        this.pos = pos;
    }

    public static void encode(ValidateApiaryMessage message, PacketBuffer buffer){
        buffer.writeBlockPos(message.pos);
    }

    public static ValidateApiaryMessage decode(PacketBuffer buffer){
        return new ValidateApiaryMessage(buffer.readBlockPos());
    }

    public static void handle(ValidateApiaryMessage message, Supplier<NetworkEvent.Context> context){
        context.get().enqueueWork(() -> {
            ServerPlayerEntity player = context.get().getSender();
            if (player != null) {
                if (player.world.isBlockLoaded(message.pos)) {
                    TileEntity tileEntity = player.world.getTileEntity(message.pos);
                    if (tileEntity instanceof ApiaryTileEntity) {
                        ApiaryTileEntity apiaryTileEntity = (ApiaryTileEntity) tileEntity;
                        apiaryTileEntity.runStructureValidation(player);
                    }
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}
