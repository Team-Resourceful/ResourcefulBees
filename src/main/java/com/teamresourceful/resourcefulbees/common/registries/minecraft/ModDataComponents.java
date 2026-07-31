package com.teamresourceful.resourcefulbees.common.registries.minecraft;

import com.teamresourceful.resourcefulbees.common.components.*;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.registries.RegistryHelper;
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs;
import com.teamresourceful.resourcefullib.common.item.ResourcefulComponentType;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ModDataComponents {

    public static final ResourcefulRegistry<DataComponentType<?>> COMPONENTS = RegistryHelper.create(BuiltInRegistries.DATA_COMPONENT_TYPE, ModConstants.MOD_ID);

    public static final RegistryEntry<DataComponentType<Bees>> BEES = COMPONENTS.register("bees", () -> DataComponentType.<Bees>builder()
            .persistent(Bees.CODEC)
            .networkSynchronized(Bees.STREAM_CODEC)
            .cacheEncoding()
            .build()
    );

    public static final RegistryEntry<DataComponentType<JarOccupant>> JAR_BEE = COMPONENTS.register("jar_bee", () -> DataComponentType.<JarOccupant>builder()
            .persistent(JarOccupant.CODEC)
            .networkSynchronized(JarOccupant.STREAM_CODEC)
            .cacheEncoding()
            .build()
    );

    public static final RegistryEntry<DataComponentType<Identifier>> FALLBACK_ITEM_MODEL = COMPONENTS.register("fallback_item_model", () -> new ResourcefulComponentType<Identifier>()
            .persistent(Identifier.CODEC)
            .networkSynchronized(ExtraByteCodecs.IDENTIFIER)
            .cacheEncoding()
            .build()
    );

    public static final RegistryEntry<DataComponentType<CentrifugeRotations>> CENTRIFUGE_ROTATIONS = COMPONENTS.register("rotations", () -> DataComponentType.<CentrifugeRotations>builder()
            .persistent(CentrifugeRotations.CODEC)
            .networkSynchronized(CentrifugeRotations.STREAM_CODEC)
            .cacheEncoding()
            .build()
    );

    public static final RegistryEntry<DataComponentType<Upgrade>> UPGRADE = COMPONENTS.register("upgrade", () -> DataComponentType.<Upgrade>builder()
            .persistent(Upgrade.CODEC)
            .networkSynchronized(Upgrade.STREAM_CODEC)
            .cacheEncoding()
            .build());

    public static final RegistryEntry<DataComponentType<BeehiveUpgrade>> BEEHIVE_UPGRADE = COMPONENTS.register("beehive_upgrade", () -> DataComponentType.<BeehiveUpgrade>builder()
            .persistent(BeehiveUpgrade.CODEC)
            .networkSynchronized(BeehiveUpgrade.STREAM_CODEC)
            .cacheEncoding()
            .build());

    public static final RegistryEntry<DataComponentType<DipperEntity>> DIPPER_ENTITY = COMPONENTS.register("dipper_entity", () -> DataComponentType.<DipperEntity>builder()
            .persistent(DipperEntity.CODEC)
            .networkSynchronized(DipperEntity.STREAM_CODEC)
            .cacheEncoding()
            .build());
}
