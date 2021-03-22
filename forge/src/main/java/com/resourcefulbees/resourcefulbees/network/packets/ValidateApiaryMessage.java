package com.resourcefulbees.resourcefulbees.network.packets;

import com.resourcefulbees.resourcefulbees.tileentity.multiblocks.apiary.ApiaryTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ValidateApiaryMessage {

    private final BlockPos pos;
    private final int verticalOffset;
    private final int horizontalOffset;

    public ValidateApiaryMessage(BlockPos pos, int verticalOffset, int horizontalOffset){
        this.pos = pos;
        this.verticalOffset = verticalOffset;
        this.horizontalOffset = horizontalOffset;
    }

    public static void encode(ValidateApiaryMessage message, FriendlyByteBuf buffer){
        buffer.writeBlockPos(message.pos);
        buffer.writeInt(message.verticalOffset);
        buffer.writeInt(message.horizontalOffset);
    }

    public static ValidateApiaryMessage decode(FriendlyByteBuf buffer){
        return new ValidateApiaryMessage(buffer.readBlockPos(), buffer.readInt(), buffer.readInt());
    }

    public static void handle(ValidateApiaryMessage message, Supplier<NetworkEvent.Context> context){
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player != null) {
                if (player.level.isLoaded(message.pos)) {
                    BlockEntity tileEntity = player.level.getBlockEntity(message.pos);
                    if (tileEntity instanceof ApiaryTileEntity) {
                        ApiaryTileEntity apiaryTileEntity = (ApiaryTileEntity) tileEntity;
                        apiaryTileEntity.setVerticalOffset(message.verticalOffset);
                        apiaryTileEntity.setHorizontalOffset(message.horizontalOffset);
                        apiaryTileEntity.runStructureValidation(player);
                    }
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}
