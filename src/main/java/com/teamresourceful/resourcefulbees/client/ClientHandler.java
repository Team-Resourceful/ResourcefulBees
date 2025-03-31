package com.teamresourceful.resourcefulbees.client;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;

public class ClientHandler {
    public static void init() {
        Sheets.SIGN_MATERIALS.put(ModBlocks.WAXED_WOOD_TYPE, new Material(Sheets.SIGN_SHEET, new ResourceLocation(ModConstants.MOD_ID, "entity/signs/waxed")));
        Sheets.HANGING_SIGN_MATERIALS.put(ModBlocks.WAXED_WOOD_TYPE, new Material(Sheets.SIGN_SHEET, new ResourceLocation(ModConstants.MOD_ID, "entity/signs/hanging/waxed")));
    }
}
