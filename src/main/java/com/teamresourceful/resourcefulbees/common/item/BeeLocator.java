package com.teamresourceful.resourcefulbees.common.item;

import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.client.gui.screen.locator.BeeLocatorScreen;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.registry.custom.BeeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BeeLocator extends Item {

    private static List<Vec2i> offsets = getPositionalOffsets(5);

    public BeeLocator(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        if (level.isClientSide) {
            clientOpenScreen(hand);
        }
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    @OnlyIn(Dist.CLIENT)
    private static void clientOpenScreen(InteractionHand hand) {
        Minecraft.getInstance().setScreen(new BeeLocatorScreen(hand));
    }

    public static void run(Player player, String bee, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(stack.getItem())) return;
        if (!(stack.getItem() instanceof BeeLocator)) return;
        CustomBeeData data = BeeRegistry.containsBeeType(bee) ? BeeRegistry.getRegistry().getBeeData(bee) : null;
        if (data == null) return;
        BlockPos run = run(player.level, player.blockPosition(), data.getEntityType(), 100);
        if (run != null) {
            var stackTag = stack.getOrCreateTag();
            stackTag.put(NBTConstants.BeeLocator.LAST_BIOME, NbtUtils.writeBlockPos(run));
            stackTag.putString(NBTConstants.BeeLocator.LAST_BEE, bee);
            stack.setTag(stackTag);
        } else {
            var stackTag = stack.getOrCreateTag();
            stackTag.remove(NBTConstants.BeeLocator.LAST_BIOME);
            stackTag.remove(NBTConstants.BeeLocator.LAST_BEE);
            stack.setTag(stackTag);
        }
        player.getCooldowns().addCooldown(stack.getItem(), 3000);
    }

    @Nullable
    private static BlockPos run(Level level, BlockPos pos, EntityType<?> type, int radius) {
        //TODO Change to whatever natures compass uses to run off thread.
        //required!
        if (offsets.size() != (radius - 1) * (radius - 1) + 1) {
            offsets = getPositionalOffsets(radius);
        }
        if (!offsets.isEmpty()) {
            ChunkPos chunkPos = level.getChunk(pos).getPos();
            Set<Biome> foundBadBiomes = new HashSet<>();
            for (Vec2i offset : offsets) {
                BlockPos chunkBlockPos = new ChunkPos(chunkPos.x + offset.x, chunkPos.z + offset.z).getMiddleBlockPosition(0);
                Holder<Biome> biome = level.getBiome(chunkBlockPos);
                if (biome.isBound()) {
                    if (foundBadBiomes.contains(biome.get())) continue;
                    foundBadBiomes.add(biome.get());
                    for (MobSpawnSettings.SpawnerData data : biome.get().getMobSettings().getMobs(ModConstants.BEE_MOB_CATEGORY).unwrap()) {
                        if (type.equals(data.type)) {
                            return chunkBlockPos;
                        }
                    }
                }
            }
        }
        return null;
    }

    public static List<Vec2i> getPositionalOffsets(int range) {
        ArrayList<Vec2i> offsets = new ArrayList<>();

        for (int i = 0; (double) i < range; ++i) {
            for (int j = 0; j <= i; j = j > 0 ? -j : 1 - j) {
                for (int k = j < i && j > -i ? i : 0; k <= i; k = k > 0 ? -k : 1 - k) {
                    offsets.add(new Vec2i(j, k));
                }
            }
        }
        return offsets;
    }

    record Vec2i(int x, int z) {}
}
