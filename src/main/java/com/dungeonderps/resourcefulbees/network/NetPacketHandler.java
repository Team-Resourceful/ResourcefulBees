package com.dungeonderps.resourcefulbees.network;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.network.packets.ValidateApiaryMessage;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
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
    }

    public static void sendToServer(Object message) {
        INSTANCE.sendToServer(message);
    }
}
