/* MIT License
 *
 * Copyright (c) 2021 Team Resourceful
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * Methods below are taken from Mekanism and modified to fit Resourceful Bees -> https://github.com/mekanism/Mekanism
 */

package com.teamresourceful.resourcefulbees.common.utils;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class WorldUtils {

    private WorldUtils() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    @Nullable
    @Contract("_, null, _, _ -> null")
    public static <A extends TileEntity> A getTileEntity(@Nonnull Class<A> clazz, @Nullable World reader, @Nonnull BlockPos pos, boolean logWrongType) {
        TileEntity tile = getTileEntity(reader, pos);
        if (tile == null) {
            return null;
        } else if (clazz.isInstance(tile)) {
            return clazz.cast(tile);
        } else {
            if (logWrongType) {
                ResourcefulBees.LOGGER.warn("Unexpected TileEntity class at {}, expected {}, but found: {}", pos, clazz, tile.getClass());
            }

            return null;
        }
    }

    @Nullable
    @Contract("null, _ -> null")
    public static TileEntity getTileEntity(@Nullable World world, @Nonnull BlockPos pos) {
        return !isBlockLoaded(world, pos) ? null : world.getBlockEntity(pos);
    }

    @Contract("null, _ -> false")
    public static boolean isBlockLoaded(@Nullable World world, @Nonnull BlockPos pos) {
        return world != null && World.isInWorldBounds(pos) && world.isLoaded(pos);
    }

    @Nullable
    @Contract("_, null, _ -> null")
    public static <T extends TileEntity> T getTileEntity(@Nonnull Class<T> clazz, @Nullable World world, @Nonnull BlockPos pos) {
        return getTileEntity(clazz, world, pos, false);
    }
}
