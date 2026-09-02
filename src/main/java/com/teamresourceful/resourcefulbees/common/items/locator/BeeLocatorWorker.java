package com.teamresourceful.resourcefulbees.common.items.locator;

import com.teamresourceful.resourcefulbees.api.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.components.BeeLocatorData;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModDataComponents;
import com.teamresourceful.resourcefulbees.common.world.workers.LevelWorker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public final class BeeLocatorWorker implements LevelWorker {

    private final ServerPlayer player;
    private final ServerLevel level;
    private final int slot;
    private final Queue<Vector2ic> queue;
    private final ChunkPos originChunk;
    private final Set<Identifier> visitedBiomes = new HashSet<>();
    private final EntityType<?> beeEntityType;
    private final Identifier bee;
    private final Runnable onFinished;

    private boolean running = true;
    private boolean finished = false;

    public BeeLocatorWorker(ServerPlayer player, int slot, Identifier bee, int range, Runnable onFinished) {
        this.player = player;
        this.level = player.level();
        this.slot = slot;
        this.queue = createRange(range);
        BlockPos pos = player.blockPosition();
        this.originChunk = new ChunkPos(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()));
        this.bee = bee;
        this.onFinished = onFinished;
        this.beeEntityType = BeeRegistry.get().getBeeData(bee).entityType();

        if (this.beeEntityType == null) {
            finish();
        }
    }

    @Override
    public ServerLevel level() {
        return level;
    }

    @Override
    public boolean work() {
        if (!running) {
            return false;
        }

        if (player.hasDisconnected()) {
            finish();
            return false;
        }

        if (player.level() != level) {
            finish();
            return false;
        }

        ItemStack stack = getLocatorStack();

        if (stack == null) {
            finish();
            return false;
        }

        Vector2ic offset = queue.poll();

        if (offset == null) {
            fail(stack);
            return false;
        }

        ChunkPos chunk = new ChunkPos(originChunk.x() + offset.x(), originChunk.z() + offset.y());
        BlockPos pos = chunk.getMiddleBlockPosition(player.getBlockY());
        Holder<Biome> biomeHolder = level.getBiome(pos);
        var biomeKey = biomeHolder.unwrapKey();

        if (biomeKey.isEmpty()) {
            return true;
        }

        Identifier biomeId = biomeKey.get().identifier();

        if (!visitedBiomes.add(biomeId)) {
            return true;
        }

        for (var weightedSpawner :
                biomeHolder.value()
                        .getMobSettings()
                        .getMobs(ModConstants.RESOURCEFUL_BEE_CATEGORY)
                        .unwrap()) {

            MobSpawnSettings.SpawnerData spawnData = weightedSpawner.value();

            if (beeEntityType.equals(spawnData.type())) {
                success(stack, pos, biomeId);
                return true;
            }
        }

        return true;
    }

    private ItemStack getLocatorStack() {
        if (slot < 0 || slot >= player.getInventory().getContainerSize()) {
            return null;
        }

        ItemStack stack = player.getInventory().getItem(slot);

        if (!(stack.getItem() instanceof BeeLocatorItem)) {
            return null;
        }

        return stack;
    }

    private void success(ItemStack stack, BlockPos pos, Identifier biome) {
        stack.set(ModDataComponents.BEE_LOCATOR_DATA.get(), new BeeLocatorData(pos, biome, bee, level.dimension()));
        applyCooldown(stack);
        finish();
    }

    private void fail(ItemStack stack) {
        stack.remove(ModDataComponents.BEE_LOCATOR_DATA.get());

        applyCooldown(stack);
        finish();
    }

    private void applyCooldown(ItemStack stack) {
        if (!player.getAbilities().instabuild) {
            player.getCooldowns().addCooldown(stack, 3000);
        }
    }

    private void finish() {
        if (finished) {
            return;
        }

        finished = true;
        running = false;
        onFinished.run();
    }

    private static Queue<Vector2ic> createRange(int range) {
        Queue<Vector2ic> queue = new ArrayDeque<>();

        // Starting chunk.
        queue.add(new Vector2i(0, 0));

        // Add each successive square ring around the origin.
        for (int radius = 1; radius < range; radius++) {

            // Top and bottom edges.
            for (int x = -radius; x <= radius; x++) {
                queue.add(new Vector2i(x, -radius));
                queue.add(new Vector2i(x, radius));
            }

            // Left and right edges, excluding corners already added above.
            for (int z = -radius + 1; z < radius; z++) {
                queue.add(new Vector2i(-radius, z));
                queue.add(new Vector2i(radius, z));
            }
        }

        return queue;
    }

//    private static Queue<Vector2ic> createRange(int range) {
//        Queue<Vector2ic> queue = new ArrayDeque<>();
//
//        for (int i = 0; i < range; i++) {
//            for (int j = 0; j <= i; j = j > 0 ? -j : 1 - j) {
//                for (
//                        int k = j < i && j > -i ? i : 0;
//                        k <= i;
//                        k = k > 0 ? -k : 1 - k
//                ) {
//                    queue.add(new Vector2i(j, k));
//                }
//            }
//        }
//
//        return queue;
//    }
}