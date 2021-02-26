package com.resourcefulbees.resourcefulbees.tileentity.multiblocks;

import com.resourcefulbees.resourcefulbees.lib.ModConstants;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;

import java.util.List;
import java.util.function.Predicate;

public class MultiBlockHelper {

    private MultiBlockHelper() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static MutableBoundingBox buildStructureBounds(BlockPos startPos, int width, int height, int depth, int hOffset, int vOffset, int dOffset, Direction direction) {
        int x = startPos.getX();
        int y = startPos.getY();
        int z = startPos.getZ();

        width -= 1;
        height -= 1;
        depth -= 1;

        switch (direction) {
            case NORTH:
                return new MutableBoundingBox(x + hOffset, y + vOffset, z - dOffset, x + width + hOffset, y + height + vOffset, z - depth - dOffset);
            case EAST:
                return new MutableBoundingBox(x + dOffset, y + vOffset, z + hOffset, x + depth + dOffset, y + height + vOffset, z + width + hOffset);
            case SOUTH:
                return new MutableBoundingBox(x - hOffset, y + vOffset, z + dOffset, x - width - hOffset, y + height + vOffset, z + depth + dOffset);
            default:
                return new MutableBoundingBox(x - dOffset, y + vOffset, z - hOffset, x - depth - dOffset, y + height + vOffset, z - width - hOffset);
        }
    }

    public static void buildStructureList(MutableBoundingBox box, List<BlockPos> list, Predicate<BlockPos> predicate, BlockPos validatorPosition) {
        list.clear();
        BlockPos.stream(box)
                .filter(blockPos -> !blockPos.equals(validatorPosition))
                .filter(predicate)
                .forEach(blockPos -> list.add(blockPos.toImmutable()));
    }

    public static boolean validateStructure(List<BlockPos> list, Predicate<BlockPos> predicate, int totalBlocks) {
        return list.size() == totalBlocks && list.stream().allMatch(predicate);
    }
}
