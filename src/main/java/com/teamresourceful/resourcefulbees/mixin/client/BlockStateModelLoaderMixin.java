package com.teamresourceful.resourcefulbees.mixin.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelDispatcher;
import net.minecraft.client.resources.model.BlockStateModelLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.StrictJsonParser;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.io.StringReader;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(BlockStateModelLoader.class)
public class BlockStateModelLoaderMixin {
    private BlockStateModelLoaderMixin() throws UtilityClassException {
        throw new UtilityClassException();
    }


    @ModifyReturnValue(method = "loadBlockStates", at = @At("RETURN"))
    private static CompletableFuture<BlockStateModelLoader.LoadedModels>
    rbees$addFallbackBlockStates(
            CompletableFuture<BlockStateModelLoader.LoadedModels> original,
            ResourceManager manager,
            Executor executor
    ) {
        return original.thenApplyAsync(
                BlockStateModelLoaderMixin::rbees$addFallbackBlockStates,
                executor
        );
    }

    @Unique
    private static BlockStateModelLoader.LoadedModels rbees$addFallbackBlockStates(BlockStateModelLoader.LoadedModels loaded) {
        Map<BlockState, BlockStateModel.UnbakedRoot> result = new IdentityHashMap<>(loaded.models());

        for (Block block : BuiltInRegistries.BLOCK) {
            Identifier blockId = BuiltInRegistries.BLOCK.getKey(block);

            if (!blockId.getNamespace().equals(ModConstants.MOD_ID)) {
                continue;
            }

            String path = blockId.getPath();

            if (!path.endsWith("_honeycomb_block") && !path.endsWith("_honey_block")) {
                continue;
            }

            /*
             * If vanilla already loaded a custom blockstate JSON for this
             * block, don't touch it.
             */
            boolean alreadyLoaded = block.getStateDefinition()
                    .getPossibleStates()
                    .stream()
                    .anyMatch(result::containsKey);

            if (alreadyLoaded) {
                ModConstants.LOGGER.debug("[RBEES BLOCKSTATE] custom blockstate exists: {}", blockId);

                continue;
            }

            rbees$addSyntheticBlockState(
                    result,
                    block,
                    blockId
            );
        }

        return new BlockStateModelLoader.LoadedModels(result);
    }

    @Unique
    private static void rbees$addSyntheticBlockState(
            Map<BlockState, BlockStateModel.UnbakedRoot> models,
            Block block,
            Identifier blockId
    ) {
        Identifier modelId = Identifier.fromNamespaceAndPath(blockId.getNamespace(), "block/" + blockId.getPath());

        String json = """
                {
                  "variants": {
                    "": {
                      "model": "%s"
                    }
                  }
                }
                """.formatted(modelId);

        try {
            JsonElement element = StrictJsonParser.parse(new StringReader(json));

            BlockStateModelDispatcher dispatcher =
                    BlockStateModelDispatcher.CODEC
                            .parse(JsonOps.INSTANCE, element)
                            .getOrThrow(JsonParseException::new);

            Map<BlockState, BlockStateModel.UnbakedRoot> generated =
                    dispatcher.instantiate(
                            block.getStateDefinition(),
                            () -> "resourcefulbees fallback/" + blockId
                    );

            models.putAll(generated);

            ModConstants.LOGGER.debug("[RBEES BLOCKSTATE] inserted fallback blockstate {} -> {}", blockId, modelId);
        } catch (Exception e) {
            ModConstants.LOGGER.error("Failed to create fallback blockstate for {}", blockId, e);
        }
    }
}