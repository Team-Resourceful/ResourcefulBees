package com.resourcefulbees.resourcefulbees.tileentity.multiblocks;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;

import java.util.List;
import java.util.function.Predicate;

public class MultiBlockHelper {

/*    public static MutableBoundingBox buildStructureBounds(BlockPos pos,  int hOffset1, int vOffset1, int hOffset2, int vOffset2, int hCenterOffset, int vCenterOffset, Direction direction) {
        return buildStructureBounds(pos, hOffset1, vOffset1, hOffset2, vOffset2, hCenterOffset, vCenterOffset, direction, false);
    }*/

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


    // TODO Optimize this further - try building a box in front ALWAYS then offsetting the box in the appropriate directions.
/*    public static MutableBoundingBox buildStructureBounds(BlockPos pos, int hOffset1, int vOffset1, int hOffset2, int vOffset2, int hCenterOffset, int vCenterOffset, Direction direction, boolean flipped) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        switch (direction) {
            case NORTH:
                x -= hCenterOffset + hOffset1;
                y -= vCenterOffset + vOffset1;
                return new MutableBoundingBox(x, y, z, flipped ? x - hOffset2 : x + hOffset2, y + vOffset2, flipped ? z + hOffset2 : z - hOffset2);
            case EAST:
                z -= hCenterOffset + hOffset1;
                y -= vCenterOffset + vOffset1;
                return new MutableBoundingBox(x, y, z, flipped ? x - hOffset2 : x + hOffset2, y + vOffset2, flipped ? z - hOffset2 : z + hOffset2);
            case SOUTH:
                hCenterOffset = flipped ? -hCenterOffset : hCenterOffset;
                x -= hCenterOffset - hOffset1;
                y -= vCenterOffset + vOffset1;
                return new MutableBoundingBox(x, y, z, x + hOffset2, y + vOffset2, flipped ? z - hOffset2 : z + hOffset2);
            default:
                hCenterOffset = flipped ? -hCenterOffset : hCenterOffset;
                z -= hCenterOffset - hOffset1;
                y -= vCenterOffset + vOffset1;
                return new MutableBoundingBox(x, y, z, flipped ? x + hOffset2 : x - hOffset2, y + vOffset2, z + hOffset2);
        }
    }*/

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
