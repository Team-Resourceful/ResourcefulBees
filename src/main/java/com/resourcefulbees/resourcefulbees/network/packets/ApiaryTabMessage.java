package com.resourcefulbees.resourcefulbees.network.packets;

import com.resourcefulbees.resourcefulbees.lib.ApiaryTabs;
import com.resourcefulbees.resourcefulbees.tileentity.multiblocks.apiary.IApiaryMultiblock;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ApiaryTabMessage {

        private final BlockPos pos;
        private final ApiaryTabs tab;

        public ApiaryTabMessage(BlockPos pos, ApiaryTabs tab){
            this.pos = pos;
            this.tab = tab;
        }

        public static void encode(ApiaryTabMessage message, PacketBuffer buffer){
            buffer.writeBlockPos(message.pos);
            buffer.writeEnumValue(message.tab);
        }

        public static ApiaryTabMessage decode(PacketBuffer buffer){
            return new ApiaryTabMessage(buffer.readBlockPos(), buffer.readEnumValue(ApiaryTabs.class));
        }

        public static void handle(ApiaryTabMessage message, Supplier<NetworkEvent.Context> context){
            context.get().enqueueWork(() -> {
                ServerPlayerEntity player = context.get().getSender();
                if (player != null) {
                    if (player.world.isBlockPresent(message.pos)) {
                        TileEntity tileEntity = player.world.getTileEntity(message.pos);
                        if (tileEntity instanceof IApiaryMultiblock) {
                            ((IApiaryMultiblock) tileEntity).switchTab(player, message.tab);
                        }
                    }
                }
            });
            context.get().setPacketHandled(true);
        }
    }




