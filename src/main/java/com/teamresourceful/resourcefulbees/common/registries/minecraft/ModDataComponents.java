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
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NullMarked;

import java.util.List;

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

    public static final RegistryEntry<DataComponentType<BeeconData>> BEECON_DATA = COMPONENTS.register("beecon_data", () -> DataComponentType.<BeeconData>builder()
            .persistent(BeeconData.CODEC)
            .networkSynchronized(BeeconData.STREAM_CODEC)
            .cacheEncoding()
            .build());

    public static final RegistryEntry<DataComponentType<TankData>> SINGLE_TANK_DATA = COMPONENTS.register("single_tank_data", () -> DataComponentType.<TankData>builder()
            .persistent(TankData.CODEC)
            .networkSynchronized(TankData.STREAM_CODEC)
            .cacheEncoding()
            .build());

    public static final RegistryEntry<DataComponentType<List<TankData>>> MULTI_TANK_DATA = COMPONENTS.register("multi_tank_data", () -> DataComponentType.<List<TankData>>builder()
            .persistent(TankData.LIST_CODEC)
            .networkSynchronized(TankData.LIST_STREAM_CODEC)
            .cacheEncoding()
            .build());

    public static final RegistryEntry<DataComponentType<BatteryData>> BATTERY_DATA = COMPONENTS.register("battery_data", () -> DataComponentType.<BatteryData>builder()
            .persistent(BatteryData.CODEC)
            .networkSynchronized(BatteryData.STREAM_CODEC)
            .cacheEncoding()
            .build());

    public static final RegistryEntry<DataComponentType<BeeLocatorData>> BEE_LOCATOR_DATA = COMPONENTS.register("bee_locator_data", () -> DataComponentType.<BeeLocatorData>builder()
            .persistent(BeeLocatorData.CODEC)
            .networkSynchronized(BeeLocatorData.STREAM_CODEC)
            .cacheEncoding()
            .build());

    public static final RegistryEntry<DataComponentType<Boolean>> BEE_LOCATOR_SEARCHING = COMPONENTS.register("bee_locator_searching", () -> DataComponentType.<Boolean>builder()
            .networkSynchronized(ByteBufCodecs.BOOL)
            .build());
}
