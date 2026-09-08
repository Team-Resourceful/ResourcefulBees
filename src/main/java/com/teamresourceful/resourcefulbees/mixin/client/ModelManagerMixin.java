package com.teamresourceful.resourcefulbees.mixin.client;


import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.hooks.client.ModelManagerHook;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.UnbakedModelParser;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

@Mixin(ModelManager.class)
public class ModelManagerMixin implements ModelManagerHook {

    @Shadow
    private Map<Identifier, ItemModel> bakedItemStackModels;

    @Unique
    private static final Identifier RBEES$HONEYCOMB_BLOCK_FALLBACK = ModIdentifier.of("block/honeycomb_block");

    @Unique
    private static final Identifier RBEES$HONEY_BLOCK_FALLBACK = ModIdentifier.of("block/honey_block");

    /*
     * Existing item-model lookup used by ItemModelResolverMixin.
     */
    @Override
    public boolean rbees$hasCustomModel(Identifier model) {
        return this.bakedItemStackModels.containsKey(model);
    }

    /*
     * Before ModelDiscovery receives the loaded model map, inject synthetic
     * models for dynamic honey/honeycomb blocks that don't have their own
     * models/*.json file.
     *
     * A synthetic model simply inherits the Resourceful Bees fallback model.
     */
    @ModifyArg(
            method = "discoverModelDependencies(Ljava/util/Map;Lnet/minecraft/client/resources/model/BlockStateModelLoader$LoadedModels;Lnet/minecraft/client/resources/model/ClientItemInfoLoader$LoadedClientInfos;Lnet/neoforged/neoforge/client/model/standalone/StandaloneModelLoader$LoadedModels;)Lnet/minecraft/client/resources/model/ModelManager$ResolvedModels;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/resources/model/ModelDiscovery;<init>(Ljava/util/Map;Lnet/minecraft/client/resources/model/UnbakedModel;)V"
            ),
            index = 0
    )
    private static Map<Identifier, UnbakedModel> rbees$addFallbackBlockModels(Map<Identifier, UnbakedModel> models) {
        Map<Identifier, UnbakedModel> result = new HashMap<>(models);

        boolean hasHoneycombFallback =
                models.containsKey(RBEES$HONEYCOMB_BLOCK_FALLBACK);

        boolean hasHoneyFallback =
                models.containsKey(RBEES$HONEY_BLOCK_FALLBACK);

        if (!hasHoneycombFallback) {
            ModConstants.LOGGER.error("Missing fallback block model: {}", RBEES$HONEYCOMB_BLOCK_FALLBACK);
        }

        if (!hasHoneyFallback) {
            ModConstants.LOGGER.error("Missing fallback block model: {}", RBEES$HONEY_BLOCK_FALLBACK);
        }

        for (Block block : BuiltInRegistries.BLOCK) {
            Identifier blockId = BuiltInRegistries.BLOCK.getKey(block);

            /*
             * Only touch Resourceful Bees blocks.
             */
            if (!blockId.getNamespace().equals(ModConstants.MOD_ID)) {
                continue;
            }

            String path = blockId.getPath();

            if (path.endsWith("_honeycomb_block") && hasHoneycombFallback) {
                rbees$putFallback(result, blockId, RBEES$HONEYCOMB_BLOCK_FALLBACK);
            } else if (path.endsWith("_honey_block") && hasHoneyFallback) {
                rbees$putFallback(result, blockId, RBEES$HONEY_BLOCK_FALLBACK);
            }
        }

        return result;
    }

    @Unique
    private static void rbees$putFallback(Map<Identifier, UnbakedModel> models, Identifier blockId, Identifier fallbackId) {
        Identifier modelId = Identifier.fromNamespaceAndPath(blockId.getNamespace(), "block/" + blockId.getPath());

        /*
         * A real model JSON exists.
         *
         * Resource packs therefore win automatically.
         */
        if (models.containsKey(modelId)) {
            ModConstants.LOGGER.debug("[RBEES MODEL] custom block model exists: {}", modelId);
            return;
        }

        UnbakedModel fallbackModel = rbees$createParentModel(fallbackId);
        models.put(modelId, fallbackModel);

        ModConstants.LOGGER.debug("[RBEES MODEL] inserted block model fallback {} -> {}", modelId, fallbackId);
    }

    @Unique
    private static UnbakedModel rbees$createParentModel(Identifier parent) {
        String json = """
                {
                  "parent": "%s"
                }
                """.formatted(parent);

        return UnbakedModelParser.parse(new StringReader(json));
    }
}