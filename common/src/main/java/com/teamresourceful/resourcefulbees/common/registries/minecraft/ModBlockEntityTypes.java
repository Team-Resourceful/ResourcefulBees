package com.teamresourceful.resourcefulbees.common.registries.minecraft;

import com.teamresourceful.resourcefulbees.common.blockentities.AcceleratorBlockEntity;
import com.teamresourceful.resourcefulbees.common.blockentities.BeeBoxBlockEntity;
import com.teamresourceful.resourcefulbees.common.blockentities.WaxedSignBlockEntity;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefulbees.platform.common.registry.api.RegistryEntry;
import com.teamresourceful.resourcefulbees.platform.common.registry.api.ResourcefulRegistries;
import com.teamresourceful.resourcefulbees.platform.common.registry.api.ResourcefulRegistry;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class ModBlockEntityTypes {

    private ModBlockEntityTypes() {
        throw new UtilityClassError();
    }

    public static final ResourcefulRegistry<BlockEntityType<?>> BLOCK_ENTITY_TYPES = ResourcefulRegistries.create(Registry.BLOCK_ENTITY_TYPE, ModConstants.MOD_ID);

    public static final RegistryEntry<BlockEntityType<BeeBoxBlockEntity>> BEE_BOX_ENTITY = BLOCK_ENTITY_TYPES.register("bee_box", () -> build(BlockEntityType.Builder
            .of(BeeBoxBlockEntity::new, ModBlocks.BEE_BOX.get(), ModBlocks.BEE_BOX_TEMP.get())));
    public static final RegistryEntry<BlockEntityType<WaxedSignBlockEntity>> WAXED_SIGN_ENTITY = BLOCK_ENTITY_TYPES.register("waxed_sign", () -> build(BlockEntityType.Builder
            .of(WaxedSignBlockEntity::new, ModBlocks.WAXED_SIGN.get(), ModBlocks.WAXED_WALL_SIGN.get())));
    public static final RegistryEntry<BlockEntityType<AcceleratorBlockEntity>> ACCELERATOR_TILE_ENTITY = BLOCK_ENTITY_TYPES.register("accelerator", () -> build(BlockEntityType.Builder
            .of(AcceleratorBlockEntity::new, ModBlocks.ACCELERATOR.get())));

    private static <T extends BlockEntity> BlockEntityType<T> build(BlockEntityType.Builder<T> builder) {
        //noinspection ConstantConditions
        return builder.build(null);
    }
}
