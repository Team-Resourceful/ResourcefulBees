package com.teamresourceful.resourcefulbees.common.items.locator;

import com.teamresourceful.resourcefulbees.api.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.platform.common.workers.LevelWorker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
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

public class BeeLocatorWorker implements LevelWorker {

    private boolean isRunning = true;
    private final Player player;
    private final int slot;
    private final Queue<Vector2ic> queue;
    private final ChunkPos chunk;
    private final Set<Biome> visited = new HashSet<>();
    private final EntityType<?> type;
    private final String bee;

    public BeeLocatorWorker(Player player, int slot, String bee, int range) {
        this.player = player;
        this.slot = slot;
        this.queue = createRange(range);
        this.chunk = player.level().getChunk(player.blockPosition()).getPos();
        this.type = BeeRegistry.get().containsBeeType(bee) ? BeeRegistry.get().getBeeData(bee).entityType() : null;
        this.bee = bee;
        if (this.type == null) {
            this.isRunning = false;
        }
    }

    @Override
    public boolean canWork() {
        return isRunning;
    }

    @Override
    public boolean work() {
        Vector2ic offset = queue.poll();
        if (offset == null) {
            fail();
            return false;
        }
        BlockPos pos = new ChunkPos(this.chunk.x + offset.x(), this.chunk.z + offset.y()).getMiddleBlockPosition(0);
        Holder<Biome> holder = player.level().getBiome(pos);
        if (holder.isBound()) {
            Biome biome = holder.value();
            if (visited.contains(biome)) return true;
            visited.add(biome);
            for (MobSpawnSettings.SpawnerData data : biome.getMobSettings().getMobs(ModConstants.BEE_CATEGORY).unwrap()) {
                if (this.type.equals(data.type)) {
                    success(pos, holder);
                }
            }
        }
        return true;
    }

    public void fail() {
        this.isRunning = false;
        ItemStack stack = this.player.getInventory().getItem(slot);
        if (!(stack.getItem() instanceof BeeLocatorItem)) return;

        var stackTag = stack.getOrCreateTag();
        stackTag.remove(NBTConstants.BeeLocator.LAST_BIOME);
        stackTag.remove(NBTConstants.BeeLocator.LAST_BEE);
        stack.setTag(stackTag);
        if (!this.player.getAbilities().instabuild) {
            this.player.getCooldowns().addCooldown(stack.getItem(), 3000);
        }
    }

    public void success(BlockPos pos, Holder<Biome> biome) {
        this.isRunning = false;
        ItemStack stack = this.player.getInventory().getItem(slot);
        if (!(stack.getItem() instanceof BeeLocatorItem)) return;
        var stackTag = stack.getOrCreateTag();
        stackTag.put(NBTConstants.BeeLocator.LAST_BIOME, NbtUtils.writeBlockPos(pos));
        biome.unwrapKey()
                .map(ResourceKey::location)
                .map(Object::toString)
                .ifPresent(loc -> stackTag.putString(NBTConstants.BeeLocator.LAST_BIOME_ID, loc));
        stackTag.putString(NBTConstants.BeeLocator.LAST_BEE, this.bee);
        stack.setTag(stackTag);
        if (!this.player.getAbilities().instabuild) {
            this.player.getCooldowns().addCooldown(stack.getItem(), 3000);
        }
    }

    private static Queue<Vector2ic> createRange(int range) {
        Queue<Vector2ic> queue = new ArrayDeque<>();
        for (int i = 0; (double) i < range; ++i) {
            for (int j = 0; j <= i; j = j > 0 ? -j : 1 - j) {
                for (int k = j < i && j > -i ? i : 0; k <= i; k = k > 0 ? -k : 1 - k) {
                    queue.add(new Vector2i(j, k));
                }
            }
        }
        return queue;
    }
}
