package com.resourcefulbees.resourcefulbees.network;

import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.network.packets.*;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class NetPacketHandler {

    private static int id = 0;
    private static final String PROTOCOL_VERSION = Integer.toString(1);
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(ResourcefulBees.MOD_ID, "main_channel"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void init() {
        INSTANCE.registerMessage(++id, ValidateApiaryMessage.class, ValidateApiaryMessage::encode, ValidateApiaryMessage::decode, ValidateApiaryMessage::handle);
        INSTANCE.registerMessage(++id, BuildApiaryMessage.class, BuildApiaryMessage::encode, BuildApiaryMessage::decode, BuildApiaryMessage::handle);
        INSTANCE.registerMessage(++id, UpdateClientApiaryMessage.class, UpdateClientApiaryMessage::encode, UpdateClientApiaryMessage::decode, UpdateClientApiaryMessage::handle);
        INSTANCE.registerMessage(++id, LockBeeMessage.class, LockBeeMessage::encode, LockBeeMessage::decode, LockBeeMessage::handle);
        INSTANCE.registerMessage(++id, ExportBeeMessage.class, ExportBeeMessage::encode, ExportBeeMessage::decode, ExportBeeMessage::handle);
        INSTANCE.registerMessage(++id, ImportBeeMessage.class, ImportBeeMessage::encode, ImportBeeMessage::decode, ImportBeeMessage::handle);
        INSTANCE.registerMessage(++id, ApiaryTabMessage.class, ApiaryTabMessage::encode, ApiaryTabMessage::decode, ApiaryTabMessage::handle);
        INSTANCE.registerMessage(++id, DrainCentrifugeTankMessage.class, DrainCentrifugeTankMessage::encode, DrainCentrifugeTankMessage::decode, DrainCentrifugeTankMessage::handle);
        INSTANCE.registerMessage(++id, UpdateRedstoneReqMessage.class, UpdateRedstoneReqMessage::encode, UpdateRedstoneReqMessage::decode, UpdateRedstoneReqMessage::handle);
        INSTANCE.registerMessage(++id, SyncGUIMessage.class, SyncGUIMessage::encode, SyncGUIMessage::decode, SyncGUIMessage::handle);
        INSTANCE.registerMessage(++id, UpdateBeeconMessage.class, UpdateBeeconMessage::encode, UpdateBeeconMessage::decode, UpdateBeeconMessage::handle);
        INSTANCE.registerMessage(++id, UpdateClientBeeconMessage.class, UpdateClientBeeconMessage::encode, UpdateClientBeeconMessage::decode, UpdateClientBeeconMessage::handle);
    }

    public static void sendToServer(Object message) {
        INSTANCE.sendToServer(message);
    }

    public static void sendToAllLoaded(Object message, World world, BlockPos pos) {
        INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> world.getChunkAt(pos)), message);
    }

    public static void sendToPlayer(Object message, ServerPlayerEntity playerEntity) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> playerEntity), message);
    }
}
