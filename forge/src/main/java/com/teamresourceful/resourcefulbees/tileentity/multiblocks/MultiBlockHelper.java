package com.teamresourceful.resourcefulbees.tileentity.multiblocks;

import com.teamresourceful.resourcefulbees.lib.ModConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.util.List;
import java.util.function.Predicate;

public class MultiBlockHelper {

    private MultiBlockHelper() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static BoundingBox buildStructureBounds(BlockPos startPos, int width, int height, int depth, int hOffset, int vOffset, int dOffset, Direction direction) {
        int x = startPos.getX();
        int y = startPos.getY();
        int z = startPos.getZ();

        width -= 1;
        height -= 1;
        depth -= 1;

        switch (direction) {
            case NORTH:
                return new BoundingBox(x + hOffset, y + vOffset, z - dOffset, x + width + hOffset, y + height + vOffset, z - depth - dOffset);
            case EAST:
                return new BoundingBox(x + dOffset, y + vOffset, z + hOffset, x + depth + dOffset, y + height + vOffset, z + width + hOffset);
            case SOUTH:
                return new BoundingBox(x - hOffset, y + vOffset, z + dOffset, x - width - hOffset, y + height + vOffset, z + depth + dOffset);
            default:
                return new BoundingBox(x - dOffset, y + vOffset, z - hOffset, x - depth - dOffset, y + height + vOffset, z - width - hOffset);
        }
    }

    public static void buildStructureList(BoundingBox box, List<BlockPos> list, Predicate<BlockPos> predicate, BlockPos validatorPosition) {
        list.clear();
        BlockPos.betweenClosedStream(box)
                .filter(blockPos -> !blockPos.equals(validatorPosition))
                .filter(predicate)
                .forEach(blockPos -> list.add(blockPos.immutable()));
    }

    public static boolean validateStructure(List<BlockPos> list, Predicate<BlockPos> predicate, int totalBlocks) {
        return list.size() == totalBlocks && list.stream().allMatch(predicate);
    }
}
