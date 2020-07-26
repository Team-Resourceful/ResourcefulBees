package com.dungeonderps.resourcefulbees.network;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.network.packets.*;
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

    public static void init(){
        INSTANCE.registerMessage(++id, ValidateApiaryMessage.class, ValidateApiaryMessage::encode, ValidateApiaryMessage::decode, ValidateApiaryMessage::handle);
        INSTANCE.registerMessage(++id, BuildApiaryMessage.class, BuildApiaryMessage::encode, BuildApiaryMessage::decode, BuildApiaryMessage::handle);
        INSTANCE.registerMessage(++id, UpdateClientApiaryMessage.class, UpdateClientApiaryMessage::encode, UpdateClientApiaryMessage::decode, UpdateClientApiaryMessage::handle);
        INSTANCE.registerMessage(++id, LockBeeMessage.class, LockBeeMessage::encode, LockBeeMessage::decode, LockBeeMessage::handle);
        INSTANCE.registerMessage(++id, ExportBeeMessage.class, ExportBeeMessage::encode, ExportBeeMessage::decode, ExportBeeMessage::handle);
        INSTANCE.registerMessage(++id, ImportBeeMessage.class, ImportBeeMessage::encode, ImportBeeMessage::decode, ImportBeeMessage::handle);
        INSTANCE.registerMessage(++id, ApiaryTabMessage.class, ApiaryTabMessage::encode, ApiaryTabMessage::decode, ApiaryTabMessage::handle);
    }

    public static void sendToServer(Object message) {
        INSTANCE.sendToServer(message);
    }

    public static void sendToAllLoaded(Object message, World world, BlockPos pos) {
        INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> world.getChunkAt(pos)), message);
    }
}
