package com.teamresourceful.resourcefulbees.common.lib.constants;

import net.minecraft.resources.Identifier;

public class ModIdentifier {

    public static Identifier of(String path) {
        return Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, path);
    }
}
