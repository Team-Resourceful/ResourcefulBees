package com.teamresourceful.resourcefulbees.network.packets;

import com.teamresourceful.resourcefulbees.lib.enums.ApiaryTab;
import com.teamresourceful.resourcefulbees.tileentity.multiblocks.apiary.IApiaryMultiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ApiaryTabMessage {

        private final BlockPos pos;
        private final ApiaryTab tab;

        public ApiaryTabMessage(BlockPos pos, ApiaryTab tab){
            this.pos = pos;
            this.tab = tab;
        }

        public static void encode(ApiaryTabMessage message, FriendlyByteBuf buffer){
            buffer.writeBlockPos(message.pos);
            buffer.writeEnum(message.tab);
        }

        public static ApiaryTabMessage decode(FriendlyByteBuf buffer){
            return new ApiaryTabMessage(buffer.readBlockPos(), buffer.readEnum(ApiaryTab.class));
        }

        public static void handle(ApiaryTabMessage message, Supplier<NetworkEvent.Context> context){
            context.get().enqueueWork(() -> {
                ServerPlayer player = context.get().getSender();
                if (player != null && player.level.isLoaded(message.pos)) {
                    BlockEntity tileEntity = player.level.getBlockEntity(message.pos);
                    if (tileEntity instanceof IApiaryMultiblock) {
                        ((IApiaryMultiblock) tileEntity).switchTab(player, message.tab);
                    }
                }
            });
            context.get().setPacketHandled(true);
        }
    }




