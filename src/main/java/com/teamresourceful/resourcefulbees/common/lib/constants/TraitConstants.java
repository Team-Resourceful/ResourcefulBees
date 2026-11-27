package com.teamresourceful.resourcefulbees.common.lib.constants;

import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import net.minecraft.resources.Identifier;

public final class TraitConstants {

    private TraitConstants() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static final Identifier SLIMY = ModIdentifier.of("slimy");
    public static final Identifier FLAMMABLE = ModIdentifier.of("flammable");
    public static final Identifier TELEPORT = ModIdentifier.of("teleport");
    public static final Identifier ANGRY = ModIdentifier.of("angry");
    public static final Identifier SPIDER = ModIdentifier.of("spider");
    public static final Identifier SCULK = ModIdentifier.of("sculk");
    public static final Identifier CLINGY = ModIdentifier.of("clingy");
    public static final Identifier COURIER = ModIdentifier.of("courier");
}
