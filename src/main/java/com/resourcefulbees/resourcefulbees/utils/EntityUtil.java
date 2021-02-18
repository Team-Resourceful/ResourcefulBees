package com.resourcefulbees.resourcefulbees.utils;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.function.Function;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

public class EntityUtil {
    public EntityUtil() {
    }

    public static String getEntityName(String entityId) {
        Pair<String, String> nameAndNbt = splitNameAndNBT(entityId);
        EntityType<?> type = (EntityType)ForgeRegistries.ENTITIES.getValue(new ResourceLocation((String)nameAndNbt.getLeft()));
        return type.getTranslationKey();
    }

    public static Function<World, Entity> loadEntity(String entityId) {
        Pair<String, String> nameAndNbt = splitNameAndNBT(entityId);
        entityId = nameAndNbt.getLeft();
        String nbtStr = nameAndNbt.getRight();
        CompoundNBT nbt = null;
        if (!nbtStr.isEmpty()) {
            try {
                nbt = JsonToNBT.getTagFromJson(nbtStr);
            } catch (CommandSyntaxException var8) {

            }
        }

        ResourceLocation key = new ResourceLocation(entityId);
        if (!ForgeRegistries.ENTITIES.containsKey(key)) {
            throw new RuntimeException("Unknown entity id: " + entityId);
        } else {
            EntityType<?> type = ForgeRegistries.ENTITIES.getValue(key);
            String finalEntityId = entityId;
            CompoundNBT finalNbt = nbt;
            return (world) -> {
                try {
                    Entity entity = type.create(world);
                    if (finalNbt != null) {
                        entity.read(finalNbt);
                    }

                    return entity;
                } catch (Exception var6) {
                    throw new IllegalArgumentException("Can't load entity " + finalEntityId, var6);
                }
            };
        }
    }

    private static Pair<String, String> splitNameAndNBT(String entityId) {
        int nbtStart = entityId.indexOf("{");
        String nbtStr = "";
        if (nbtStart > 0) {
            nbtStr = entityId.substring(nbtStart).replaceAll("([^\\\\])'", "$1\"").replaceAll("\\\\'", "'");
            entityId = entityId.substring(0, nbtStart);
        }

        return Pair.of(entityId, nbtStr);
    }
}
