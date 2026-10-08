package com.teamresourceful.resourcefulbees.common.registries.minecraft;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.lib.tags.ModItemTags;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Util;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAssets;

import java.util.EnumMap;

public final class ModMaterials {

    public static final ToolMaterial WAXED = new ToolMaterial(BlockTags.INCORRECT_FOR_IRON_TOOL, 128, 6.0F, 0F, 32, ModItemTags.WAXED_TOOL_MATERIALS);

    public static final ArmorMaterial BEEKEEPER = new ArmorMaterial(
            15,
            Util.make(new EnumMap<>(ArmorType.class), map -> {
                map.put(ArmorType.BOOTS, 2);
                map.put(ArmorType.LEGGINGS, 5);
                map.put(ArmorType.CHESTPLATE, 6);
                map.put(ArmorType.HELMET, 2);
                map.put(ArmorType.BODY, 5);
            }),
            15,
            SoundEvents.ARMOR_EQUIP_LEATHER,
            0.0F,
            0.0F,
            ModItemTags.WAXED_TOOL_MATERIALS,
            ResourceKey.create(EquipmentAssets.ROOT_ID, ModIdentifier.of("beekeeper"))
    );

    private ModMaterials() {}
}
