package com.teamresourceful.resourcefulbees.common.lib.defaults;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.lib.records.HiveType;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public final class DefaultHiveTypes {

    private DefaultHiveTypes() throws UtilityClassException {
        throw new UtilityClassException();
    }

    private static final BlockBehaviour.Properties WOOD_NEST_PROPERTIES = BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(1F).sound(SoundType.WOOD);

    private static BlockBehaviour.Properties makeNestProperty(MapColor color, SoundType soundType){
        return BlockBehaviour.Properties.of().strength(1.0F).mapColor(color).sound(soundType);
    }

    public static final HiveType ACACIA = new HiveType.Builder()
            .type("acacia")
            .properties(() -> WOOD_NEST_PROPERTIES)
            .build(ModIdentifier.of("acacia"));

    public static final HiveType BIRCH = new HiveType.Builder()
            .type("birch")
            .properties(() -> WOOD_NEST_PROPERTIES)
            .build(ModIdentifier.of("birch"));

    public static final HiveType BROWN_MUSHROOM = new HiveType.Builder()
            .type("brown_mushroom")
            .properties(() -> makeNestProperty(MapColor.DIRT, SoundType.WOOD))
            .build(ModIdentifier.of("brown_mushroom"));

    public static final HiveType CRIMSON = new HiveType.Builder()
            .type("crimson")
            .properties(() -> makeNestProperty(MapColor.CRIMSON_STEM, SoundType.STEM))
            .build(ModIdentifier.of("crimson"));

    public static final HiveType CRIMSON_NYLIUM = new HiveType.Builder()
            .type("crimson_nylium")
            .properties(() -> makeNestProperty(MapColor.CRIMSON_NYLIUM, SoundType.STEM))
            .build(ModIdentifier.of("crimson_nylium"));

    public static final HiveType DARK_OAK = new HiveType.Builder()
            .type("dark_oak")
            .properties(() -> WOOD_NEST_PROPERTIES)
            .build(ModIdentifier.of("dark_oak"));

    public static final HiveType GRASS = new HiveType.Builder()
            .type("grass")
            .properties(() -> makeNestProperty(MapColor.GRASS, SoundType.GRASS))
            .build(ModIdentifier.of("grass"));

    public static final HiveType JUNGLE = new HiveType.Builder()
            .type("jungle")
            .properties(() -> WOOD_NEST_PROPERTIES)
            .build(ModIdentifier.of("jungle"));

    public static final HiveType NETHERRACK = new HiveType.Builder()
            .type("netherrack")
            .properties(() -> makeNestProperty(MapColor.NETHER, SoundType.NETHERRACK))
            .build(ModIdentifier.of("netherrack"));

    public static final HiveType OAK = new HiveType.Builder()
            .type("oak")
            .properties(() -> WOOD_NEST_PROPERTIES)
            .build(ModIdentifier.of("oak"));

    public static final HiveType PRISMARINE = new HiveType.Builder()
            .type("prismarine")
            .properties(() -> makeNestProperty(MapColor.DIAMOND, SoundType.STONE))
            .build(ModIdentifier.of("prismarine"));

    public static final HiveType CHORUS = new HiveType.Builder()
            .type("chorus")
            .properties(() -> makeNestProperty(MapColor.COLOR_MAGENTA, SoundType.STONE))
            .build(ModIdentifier.of("chorus"));

    public static final HiveType RED_MUSHROOM = new HiveType.Builder()
            .type("red_mushroom")
            .properties(() -> makeNestProperty(MapColor.COLOR_RED, SoundType.STEM))
            .build(ModIdentifier.of("red_mushroom"));

    public static final HiveType SPRUCE = new HiveType.Builder()
            .type("spruce")
            .properties(() -> WOOD_NEST_PROPERTIES)
            .build(ModIdentifier.of("spruce"));

    public static final HiveType WARPED = new HiveType.Builder()
            .type("warped")
            .properties(() -> makeNestProperty(MapColor.WARPED_STEM, SoundType.STEM))
            .build(ModIdentifier.of("warped"));

    public static final HiveType WARPED_NYLIUM = new HiveType.Builder()
            .type("warped_nylium")
            .properties(() -> makeNestProperty(MapColor.WARPED_NYLIUM, SoundType.STEM))
            .build(ModIdentifier.of("warped_nylium"));

    public static final HiveType WITHER = new HiveType.Builder()
            .type("wither")
            .properties(() -> makeNestProperty(MapColor.COLOR_BLACK, SoundType.BASALT))
            .build(ModIdentifier.of("wither"));

    public static void loadDefaults() {
        // NO-OP
    }

}
