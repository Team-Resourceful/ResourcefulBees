package com.teamresourceful.resourcefulbees.datagen.providers;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jspecify.annotations.NonNull;

import java.util.Optional;
import java.util.stream.Stream;

public class RBeesModelProvider extends ModelProvider {
    public RBeesModelProvider(PackOutput output) {
        super(output, ResourcefulBees.MODID);
    }

    private static final TextureSlot APIARY_TEXTURE =
            TextureSlot.create("texture", TextureSlot.ALL);

    private static final ModelTemplate APIARY_TEMPLATE =
            new ModelTemplate(
                    Optional.of(
                            Identifier.fromNamespaceAndPath(
                                    ResourcefulBees.MODID,
                                    "block/beehouse"
                            )
                    ),
                    Optional.empty(),
                    TextureSlot.PARTICLE,
                    APIARY_TEXTURE
            );

    private static final TextureSlot BOTTOM =
            TextureSlot.create("bottom");

    private static final TextureSlot TOP =
            TextureSlot.create("top");

    private static final TextureSlot FRONT =
            TextureSlot.create("front");

    private static final TextureSlot SIDE =
            TextureSlot.create("side");

    private static final TextureSlot TIER =
            TextureSlot.create("tier");

    private static final ModelTemplate NEST_TEMPLATE =
            new ModelTemplate(
                    Optional.of(
                            Identifier.fromNamespaceAndPath(
                                    ResourcefulBees.MODID,
                                    "block/nest"
                            )
                    ),
                    Optional.empty(),
                    TextureSlot.PARTICLE,
                    BOTTOM,
                    TOP,
                    FRONT,
                    SIDE,
                    TIER
            );

    private static final ModelTemplate NEST_HONEY_TEMPLATE =
            new ModelTemplate(
                    Optional.of(
                            Identifier.fromNamespaceAndPath(
                                    ResourcefulBees.MODID,
                                    "block/nest"
                            )
                    ),
                    Optional.of("_honey"),
                    TextureSlot.PARTICLE,
                    BOTTOM,
                    TOP,
                    FRONT,
                    SIDE,
                    TIER
            );

    private static final ModelTemplate PARTICLE_ONLY_TEMPLATE =
            new ModelTemplate(
                    Optional.of(
                            Identifier.withDefaultNamespace("block/particle")
                    ),
                    Optional.empty(),
                    TextureSlot.PARTICLE
            );

    private static final ModelTemplate CUBE_BOTTOM_TOP_TEMPLATE =
            new ModelTemplate(
                    Optional.of(
                            Identifier.withDefaultNamespace("block/cube_bottom_top")
                    ),
                    Optional.empty(),
                    TextureSlot.SIDE,
                    TextureSlot.BOTTOM,
                    TextureSlot.TOP
            );

    @Override
    protected void registerModels(@NonNull BlockModelGenerators blockModels, @NonNull ItemModelGenerators itemModels) {
        registerApiary(blockModels, ModBlocks.T1_APIARY_BLOCK);
        registerApiary(blockModels, ModBlocks.T2_APIARY_BLOCK);
        registerApiary(blockModels, ModBlocks.T3_APIARY_BLOCK);
        registerApiary(blockModels, ModBlocks.T4_APIARY_BLOCK);
        registerApiary(blockModels, ModBlocks.FLOW_HIVE);
        registerApiary(blockModels, ModBlocks.BREEDER_BLOCK);
        ModBlocks.HIVES.getEntries().forEach(block -> registerNest(blockModels, block));
        registerWaxedBlocks(blockModels);
        registerHoneyGlass(blockModels);
        registerMiscBlocks(blockModels);
        registerItems(itemModels);
    }

    @Override
    protected @NonNull Stream<? extends Holder<Block>> getKnownBlocks() {
        return Stream.of(
                ModBlocks.T1_APIARY_BLOCK.get(),
                ModBlocks.T2_APIARY_BLOCK.get(),
                ModBlocks.T3_APIARY_BLOCK.get(),
                ModBlocks.T4_APIARY_BLOCK.get(),
                ModBlocks.WAXED_FENCE_GATE.get(),
                ModBlocks.WAXED_PLANKS.get(),
                ModBlocks.WAXED_PRESSURE_PLATE.get(),
                ModBlocks.WAXED_HANGING_SIGN.get(),
                ModBlocks.WAXED_WALL_HANGING_SIGN.get()
        ).map(BuiltInRegistries.BLOCK::wrapAsHolder);
    }

    @Override
    protected @NonNull Stream<? extends Holder<Item>> getKnownItems() {
        Stream<Item> explicitItems = Stream.of(
                ModItems.T1_APIARY_ITEM.get(),
                ModItems.T2_APIARY_ITEM.get(),
                ModItems.T3_APIARY_ITEM.get(),
                ModItems.T4_APIARY_ITEM.get(),

                ModItems.WAXED_FENCE_GATE.get(),
                ModItems.WAXED_PLANKS.get(),
                ModItems.WAXED_PRESSURE_PLATE.get(),
                ModItems.WAXED_HANGING_SIGN.get(),

                ModItems.ENERGY_CAP_UPGRADE.get(),
                ModItems.ENERGY_FILL_UPGRADE.get(),
                ModItems.ENERGY_XFER_UPGRADE.get(),
                ModItems.HONEY_CAP_UPGRADE.get(),
                ModItems.BREED_TIME_UPGRADE.get(),
                ModItems.STRAWBEERRY_MILKSHAKE.get(),
                ModItems.WAX.get(),
                ModItems.T2_NEST_UPGRADE.get(),
                ModItems.T3_NEST_UPGRADE.get(),
                ModItems.T4_NEST_UPGRADE.get()
        );

        Stream<Item> nestItems = ModItems.NEST_ITEMS.getEntries()
                .stream()
                .map(RegistryEntry::get);

        return Stream.concat(explicitItems, nestItems)
                .filter(item -> item != Items.AIR)
                .distinct()
                .map(BuiltInRegistries.ITEM::wrapAsHolder);
    }

    private void registerApiary(BlockModelGenerators blockModels, RegistryEntry<Block> registryEntry) {
        Block block = registryEntry.get();
        Identifier blockId = registryEntry.getId();

        // resourcefulbees:block/apiary/1
        Identifier textureId = Identifier.fromNamespaceAndPath(blockId.getNamespace(), "block/" + blockId.getPath());

        Material texture = new Material(textureId, false);

        TextureMapping textureMapping = new TextureMapping()
                .put(TextureSlot.PARTICLE, texture)
                .put(APIARY_TEXTURE, texture);

        Identifier modelLocation = APIARY_TEMPLATE.create(block, textureMapping, blockModels.modelOutput);

        Variant variant = new Variant(modelLocation);

        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(
                        block,
                        BlockModelGenerators.variant(variant)
                ).with(BlockModelGenerators.ROTATION_HORIZONTAL_FACING)
        );

        blockModels.registerSimpleItemModel(block, modelLocation);
    }

    private void registerNest(BlockModelGenerators blockModels, RegistryEntry<Block> registryEntry) {
        Block block = registryEntry.get();
        Identifier blockId = registryEntry.getId();

        Identifier normalModel = buildNestModel(
                blockModels,
                block,
                blockId,
                false
        );

        Identifier honeyModel = buildNestModel(
                blockModels,
                block,
                blockId,
                true
        );

        MultiVariant normalVariant = BlockModelGenerators.variant(
                new Variant(normalModel)
        );

        MultiVariant honeyVariant = BlockModelGenerators.variant(
                new Variant(honeyModel)
        );

        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(block)
                        .with(
                                PropertyDispatch.initial(
                                                BlockStateProperties.LEVEL_HONEY
                                        )
                                        .select(0, normalVariant)
                                        .select(1, normalVariant)
                                        .select(2, normalVariant)
                                        .select(3, normalVariant)
                                        .select(4, normalVariant)
                                        .select(5, honeyVariant)
                        )
                        .with(
                                BlockModelGenerators.ROTATION_HORIZONTAL_FACING
                        )
        );

        blockModels.registerSimpleItemModel(block, normalModel);
    }

    private Identifier buildNestModel(BlockModelGenerators blockModels, Block block, Identifier blockId, boolean honey) {
        String path = blockId.getPath();

        int separator = path.lastIndexOf('/');

        if (separator < 0) {
            throw new IllegalArgumentException(
                    "Nest registry path must contain a tier segment: " + blockId
            );
        }

        String textureDirectory = path.substring(0, separator + 1);
        String tier = path.substring(separator + 1);

        TextureMapping textures = new TextureMapping()
                .put(
                        TextureSlot.PARTICLE,
                        blockMaterial(
                                blockId,
                                textureDirectory + "side"
                        )
                )
                .put(
                        BOTTOM,
                        blockMaterial(
                                blockId,
                                textureDirectory + "bottom"
                        )
                )
                .put(
                        TOP,
                        blockMaterial(
                                blockId,
                                textureDirectory + "top"
                        )
                )
                .put(
                        FRONT,
                        blockMaterial(
                                blockId,
                                textureDirectory
                                        + (honey ? "front_honey" : "front")
                        )
                )
                .put(
                        SIDE,
                        blockMaterial(
                                blockId,
                                textureDirectory + "side"
                        )
                )
                .put(
                        TIER,
                        blockMaterial(
                                blockId,
                                "nest/tier_overlay/tier_" + tier
                        )
                );

        ModelTemplate template = honey
                ? NEST_HONEY_TEMPLATE
                : NEST_TEMPLATE;

        return template.create(
                block,
                textures,
                blockModels.modelOutput
        );
    }

    private static final BlockFamily WAXED_FAMILY =
            new BlockFamily.Builder(ModBlocks.WAXED_PLANKS.get())
                    .button(ModBlocks.WAXED_BUTTON.get())
                    .fence(ModBlocks.WAXED_FENCE.get())
                    .fenceGate(ModBlocks.WAXED_FENCE_GATE.get())
                    .pressurePlate(ModBlocks.WAXED_PRESSURE_PLATE.get())
                    .slab(ModBlocks.WAXED_SLAB.get())
                    .stairs(ModBlocks.WAXED_STAIRS.get())
                    .door(ModBlocks.WAXED_DOOR.get())
                    .trapdoor(ModBlocks.WAXED_TRAPDOOR.get())
                    .sign(ModBlocks.WAXED_SIGN.get(), ModBlocks.WAXED_WALL_SIGN.get())
                    .hangingSign(ModBlocks.WAXED_HANGING_SIGN.get(), ModBlocks.WAXED_WALL_HANGING_SIGN.get())
                    .getFamily();

    private void registerWaxedBlocks(BlockModelGenerators blockModels) {
        blockModels.family(ModBlocks.WAXED_PLANKS.get()).generateFor(WAXED_FAMILY);

        registerCubeAll(
                blockModels,
                ModBlocks.TRIMMED_WAXED_PLANKS.get(),
                blockTexture("trimmed_waxed_planks"),
                false
        );

        registerCubeAll(
                blockModels,
                ModBlocks.WAXED_MACHINE_BLOCK.get(),
                blockTexture("waxed_machine_block"),
                false
        );

        registerCubeAll(
                blockModels,
                ModBlocks.WAX_BLOCK.get(),
                blockTexture("wax_block"),
                false
        );

    }

    private static void registerHoneyGlass(BlockModelGenerators blockModels) {
        Material honeyTexture = new Material(
                Identifier.withDefaultNamespace("block/honey_block_bottom"),
                true
        );

        registerCubeAll(
                blockModels,
                ModBlocks.HONEY_GLASS_PLAYER.get(),
                honeyTexture,
                true
        );

        registerCubeAll(
                blockModels,
                ModBlocks.HONEY_GLASS.get(),
                honeyTexture,
                true
        );
    }

    private static void registerCubeAll(BlockModelGenerators blockModels, Block block, Material texture, boolean forceTranslucent) {
        Material material = forceTranslucent
                ? new Material(texture.sprite(), true)
                : texture;

        TextureMapping textureMapping = new TextureMapping()
                .put(TextureSlot.ALL, material);

        Identifier model = ModelTemplates.CUBE_ALL.create(
                block,
                textureMapping,
                blockModels.modelOutput
        );

        blockModels.blockStateOutput.accept(
                BlockModelGenerators.createSimpleBlock(block, BlockModelGenerators.plainVariant(model))
        );

        blockModels.registerSimpleItemModel(block, model);
    }

    private void registerMiscBlocks(BlockModelGenerators blockModels) {
        // Particle-only model. No explicit item client model.
        registerParticleOnly(
                blockModels,
                ModBlocks.BEEHOUSE_TOP.get(),
                modMaterial("block/apiary/t1_apiary")
        );

        registerCubeBottomTop(
                blockModels,
                ModBlocks.BEE_BOX.get(),
                modMaterial("block/bee_box_side"),
                modMaterial("block/trimmed_waxed_planks"),
                modMaterial("block/bee_box_top"),
                true
        );

        registerCubeBottomTop(
                blockModels,
                ModBlocks.BEE_BOX_TEMP.get(),
                modMaterial("block/bee_box_side_temp"),
                modMaterial("block/trimmed_waxed_planks"),
                modMaterial("block/bee_box_top_temp"),
                true
        );

        // Cube-all blockstate/model, without explicitly registering an item model.
//        registerCubeAll(
//                blockModels,
//                ModBlocks.CREATIVE_GEN.get(),
//                TextureMapping.getBlockTexture(ModBlocks.CREATIVE_GEN.get()),
//                false
//        );

        // Cross model using the cutout render type.
        registerCross(
                blockModels,
                ModBlocks.GOLD_FLOWER.get(),
                modMaterial("block/gold_flower")
        );
    }

    private static void registerParticleOnly(
            BlockModelGenerators blockModels,
            Block block,
            Material particle
    ) {
        Identifier model = PARTICLE_ONLY_TEMPLATE.create(
                block,
                new TextureMapping()
                        .put(TextureSlot.PARTICLE, particle),
                blockModels.modelOutput
        );

        registerSimpleBlockState(blockModels, block, model);
    }

    private static void registerCubeBottomTop(
            BlockModelGenerators blockModels,
            Block block,
            Material side,
            Material bottom,
            Material top,
            boolean registerItemModel
    ) {
        TextureMapping textures = new TextureMapping()
                .put(TextureSlot.SIDE, side)
                .put(TextureSlot.BOTTOM, bottom)
                .put(TextureSlot.TOP, top);

        Identifier model = CUBE_BOTTOM_TOP_TEMPLATE.create(
                block,
                textures,
                blockModels.modelOutput
        );

        registerSimpleBlockState(blockModels, block, model);

        if (registerItemModel) {
            blockModels.registerSimpleItemModel(block, model);
        }
    }

    private static void registerCross(
            BlockModelGenerators blockModels,
            Block block,
            Material texture
    ) {
        Identifier model = ModelTemplates.CROSS.create(
                block,
                new TextureMapping()
                        .put(TextureSlot.CROSS, texture),
                blockModels.modelOutput
        );

        blockModels.blockStateOutput.accept(
                BlockModelGenerators.createSimpleBlock(
                        block,
                        BlockModelGenerators.plainVariant(model)
                )
        );

        blockModels.registerSimpleItemModel(block, model);
    }

    private static void registerSimpleBlockState(
            BlockModelGenerators blockModels,
            Block block,
            Identifier model
    ) {
        blockModels.blockStateOutput.accept(
                BlockModelGenerators.createSimpleBlock(
                        block,
                        BlockModelGenerators.plainVariant(model)
                )
        );
    }

    private static void registerItems(ItemModelGenerators itemModels) {
        itemModels.generateFlatItem(
                ModItems.WAX.get(),
                ModelTemplates.FLAT_ITEM
        );

        itemModels.generateFlatItem(
                ModItems.T2_NEST_UPGRADE.get(),
                ModelTemplates.FLAT_ITEM
        );

        itemModels.generateFlatItem(
                ModItems.T3_NEST_UPGRADE.get(),
                ModelTemplates.FLAT_ITEM
        );

        itemModels.generateFlatItem(
                ModItems.T4_NEST_UPGRADE.get(),
                ModelTemplates.FLAT_ITEM
        );

//        itemModels.generateFlatItem(
//                ModItems.SMOKER.get(),
//                ModelTemplates.FLAT_ITEM
//        );
//
//        itemModels.generateFlatItem(
//                ModItems.SMOKER_CAN.get(),
//                ModelTemplates.FLAT_ITEM
//        );
//
//        itemModels.generateFlatItem(
//                ModItems.BELLOW.get(),
//                ModelTemplates.FLAT_ITEM
//        );
//
//        itemModels.generateFlatItem(
//                ModItems.OREO_COOKIE.get(),
//                ModelTemplates.FLAT_ITEM
//        );

        itemModels.generateFlatItem(
                ModItems.STRAWBEERRY_MILKSHAKE.get(),
                ModelTemplates.FLAT_ITEM
        );

        itemModels.generateFlatItem(
                ModItems.ENERGY_CAP_UPGRADE.get(),
                ModelTemplates.FLAT_ITEM
        );

        itemModels.generateFlatItem(
                ModItems.ENERGY_XFER_UPGRADE.get(),
                ModelTemplates.FLAT_ITEM
        );

        itemModels.generateFlatItem(
                ModItems.ENERGY_FILL_UPGRADE.get(),
                ModelTemplates.FLAT_ITEM
        );

        itemModels.generateFlatItem(
                ModItems.HONEY_CAP_UPGRADE.get(),
                ModelTemplates.FLAT_ITEM
        );

        itemModels.generateFlatItem(
                ModItems.BREED_TIME_UPGRADE.get(),
                ModelTemplates.FLAT_ITEM
        );
    }

    private static Material blockMaterial(Identifier blockId, String path) {
        return new Material(Identifier.fromNamespaceAndPath(blockId.getNamespace(), "block/" + path), false);
    }

    private static Material blockTexture(String path) {
        return new Material(Identifier.fromNamespaceAndPath(ResourcefulBees.MODID, "block/" + path), false);
    }

    private static Material modMaterial(String path) {
        return new Material(ModIdentifier.of(path), false);
    }

    private static Material minecraftMaterial(String path) {
        return new Material(Identifier.withDefaultNamespace(path), false);
    }
}
