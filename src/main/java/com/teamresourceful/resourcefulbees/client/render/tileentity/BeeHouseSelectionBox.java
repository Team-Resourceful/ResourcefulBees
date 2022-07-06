package com.teamresourceful.resourcefulbees.client.render.tileentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefullib.common.color.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public final class BeeHouseSelectionBox {

    private static final Color SELECTION_COLOR = new Color(0x66000000);

    private BeeHouseSelectionBox() {
        throw new IllegalAccessError(ModConstants.UTILITY_CLASS);
    }

    public static void renderSelectionBox(VertexConsumer consumer, PoseStack poseStack, Vec3 cameraPos, BlockPos blockPos, double offset, boolean shouldFlip) {
        poseStack.pushPose();
        {
            float x = (float)(blockPos.getX() - cameraPos.x());
            float y = (float)(blockPos.getY() - cameraPos.y() - offset);
            float z = (float)(blockPos.getZ() - cameraPos.z());

            //SIDE AND BOTTOM
            renderFromPointToPoint(poseStack, consumer, x, y, z, 0.0625f, 0f, 0.0625f, 0.0625f, 0f, 0.9375f, shouldFlip);
            renderFromPointToPoint(poseStack, consumer, x, y, z, 0.0625f, 0f, 0.0625f, 0.9375f, 0f, 0.0625f, shouldFlip);
            renderFromPointToPoint(poseStack, consumer, x, y, z, 0.9375f, 0f, 0.9375f, 0.9375f, 0f, 0.0625f, shouldFlip);
            renderFromPointToPoint(poseStack, consumer, x, y, z, 0.9375f, 0f, 0.9375f, 0.0625f, 0f, 0.9375f, shouldFlip);

            renderFromPointToPoint(poseStack, consumer, x, y, z, 0.0625f, 0f, 0.0625f, 0.0625f, 0.85f, 0.0625f, shouldFlip);
            renderFromPointToPoint(poseStack, consumer, x, y, z, 0.0625f, 0f, 0.9375f, 0.0625f, 0.85f, 0.9375f, shouldFlip);
            renderFromPointToPoint(poseStack, consumer, x, y, z, 0.9375f, 0f, 0.0625f, 0.9375f, 0.85f, 0.0625f, shouldFlip);
            renderFromPointToPoint(poseStack, consumer, x, y, z, 0.9375f, 0f, 0.9375f, 0.9375f, 0.85f, 0.9375f, shouldFlip);

            //UNDER EDGE
            renderFromPointToPoint(poseStack, consumer, x, y, z, 0.9375f, 0.85f, 0.9375f, 0.9375f, 0.85f, 0.0625f, shouldFlip);
            renderFromPointToPoint(poseStack, consumer, x, y, z, 0.0625f, 0.85f, 0.9375f, 0.0625f, 0.85f, 0.0625f, shouldFlip);

            //ROOF SIDE
            renderFromPointToPoint(poseStack, consumer, x, y, z, 0.0125f, 0.8f, 0f, 0.5f, 1.2875f, 0f, shouldFlip);
            renderFromPointToPoint(poseStack, consumer, x, y, z, 0.9875f, 0.8f, 0f, 0.5f, 1.2875f, 0f, shouldFlip);
            renderFromPointToPoint(poseStack, consumer, x, y, z, -0.075f, 0.8875f, 0f, 0.4375f, 1.4f, 0f, shouldFlip);
            renderFromPointToPoint(poseStack, consumer, x, y, z, 1.075f, 0.8875f, 0f, 0.5625f, 1.4f, 0f, shouldFlip);

            renderFromPointToPoint(poseStack, consumer, x, y, z, 0.0125f, 0.8f, 1f, 0.5f, 1.2875f, 1f, shouldFlip);
            renderFromPointToPoint(poseStack, consumer, x, y, z, 0.9875f, 0.8f, 1f, 0.5f, 1.2875f, 1f, shouldFlip);
            renderFromPointToPoint(poseStack, consumer, x, y, z, -0.075f, 0.8875f, 1f, 0.4375f, 1.4f, 1f, shouldFlip);
            renderFromPointToPoint(poseStack, consumer, x, y, z, 1.075f, 0.8875f, 1f, 0.5625f, 1.4f, 1f, shouldFlip);

            //UNDER ROOF SIDE
            renderFromPointToPoint(poseStack, consumer, x, y, z, 0.0625f, 0.85f, 0.0625f, 0.5f, 1.2875f, 0.0625f, shouldFlip);
            renderFromPointToPoint(poseStack, consumer, x, y, z, 0.9375f, 0.85f, 0.0625f, 0.5f, 1.2875f, 0.0625f, shouldFlip);

            renderFromPointToPoint(poseStack, consumer, x, y, z, 0.0625f, 0.85f, 0.9375f, 0.5f, 1.2875f, 0.9375f, shouldFlip);
            renderFromPointToPoint(poseStack, consumer, x, y, z, 0.9375f, 0.85f, 0.9375f, 0.5f, 1.2875f, 0.9375f, shouldFlip);

            //ROOF EDGE
            renderFromPointToPoint(poseStack, consumer, x, y, z, 1.075f, 0.8875f, 1f, 1.075f, 0.8875f, 0f, shouldFlip);
            renderFromPointToPoint(poseStack, consumer, x, y, z, 0.9875f, 0.8f, 1f, 0.9875f, 0.8f, 0f, shouldFlip);

            renderFromPointToPoint(poseStack, consumer, x, y, z, 0.9875f, 0.8f, 1f, 1.075f, 0.8875f, 1f, shouldFlip);
            renderFromPointToPoint(poseStack, consumer, x, y, z, 0.9875f, 0.8f, 0f, 1.075f, 0.8875f, 0f, shouldFlip);

            renderFromPointToPoint(poseStack, consumer, x, y, z, -0.075f, 0.8875f, 1f, -0.075f, 0.8875f, 0f, shouldFlip);
            renderFromPointToPoint(poseStack, consumer, x, y, z, 0.0125f, 0.8f, 1f, 0.0125f, 0.8f, 0f, shouldFlip);

            renderFromPointToPoint(poseStack, consumer, x, y, z, 0.0125f, 0.8f, 1f, -0.075f, 0.8875f, 1f, shouldFlip);
            renderFromPointToPoint(poseStack, consumer, x, y, z, 0.0125f, 0.8f, 0f, -0.075f, 0.8875f, 0f, shouldFlip);

            //TOP ROOF EDGE
            renderFromPointToPoint(poseStack, consumer, x, y, z, 0.5625f, 1.4f, 0f, 0.5625f, 1.4f, 1f, shouldFlip);
            renderFromPointToPoint(poseStack, consumer, x, y, z, 0.4375f, 1.4f, 0f, 0.4375f, 1.4f, 1f, shouldFlip);

            renderFromPointToPoint(poseStack, consumer, x, y, z, 0.4375f, 1.4375f, 0.0625f, 0.4375f, 1.4375f, 0.9375f, shouldFlip);
            renderFromPointToPoint(poseStack, consumer, x, y, z, 0.5625f, 1.4375f, 0.0625f, 0.5625f, 1.4375f, 0.9375f, shouldFlip);

            //END VERTICAL LINES BLOCKS
            renderFromPointToPoint(poseStack, consumer, x, y, z, 0.4375f, 1.4375f, 0.9375f, 0.4375f, 1.5f, 0.9375f, shouldFlip);
            renderFromPointToPoint(poseStack, consumer, x, y, z, 0.4375f, 1.4375f, 0.0625f, 0.4375f, 1.5f, 0.0625f, shouldFlip);
            renderFromPointToPoint(poseStack, consumer, x, y, z, 0.5625f, 1.4375f, 0.9375f, 0.5625f, 1.5f, 0.9375f, shouldFlip);
            renderFromPointToPoint(poseStack, consumer, x, y, z, 0.5625f, 1.4375f, 0.0625f, 0.5625f, 1.5f, 0.0625f, shouldFlip);
            renderFromPointToPoint(poseStack, consumer, x, y, z, 0.5625f, 1.375f, -0.0625f, 0.5625f, 1.5f, -0.0625f, shouldFlip);
            renderFromPointToPoint(poseStack, consumer, x, y, z, 0.4375f, 1.375f, -0.0625f, 0.4375f, 1.5f, -0.0625f, shouldFlip);
            renderFromPointToPoint(poseStack, consumer, x, y, z, 0.5625f, 1.375f, 1.0625f, 0.5625f, 1.5f, 1.0625f, shouldFlip);
            renderFromPointToPoint(poseStack, consumer, x, y, z, 0.4375f, 1.375f, 1.0625f, 0.4375f, 1.5f, 1.0625f, shouldFlip);

            //END HORIZONTAL LONG WAYS LINES BLOCKS
            renderFromPointToPoint(poseStack, consumer, x, y, z, 0.4375f, 1.375f, -0.0625f, 0.4375f, 1.375f, 1.0625f, shouldFlip);
            renderFromPointToPoint(poseStack, consumer, x, y, z, 0.5625f, 1.375f, -0.0625f, 0.5625f, 1.375f, 1.0625f, shouldFlip);

            renderFromPointToPoint(poseStack, consumer, x, y, z, 0.5625f, 1.5f, -0.0625f, 0.5625f, 1.5f, 0.0625f, shouldFlip);
            renderFromPointToPoint(poseStack, consumer, x, y, z, 0.4375f, 1.5f, -0.0625f, 0.4375f, 1.5f, 0.0625f, shouldFlip);

            renderFromPointToPoint(poseStack, consumer, x, y, z, 0.5625f, 1.5f, 0.9375f, 0.5625f, 1.5f, 1.0625f, shouldFlip);
            renderFromPointToPoint(poseStack, consumer, x, y, z, 0.4375f, 1.5f, 0.9375f, 0.4375f, 1.5f, 1.0625f, shouldFlip);

            //END HORIZONTAL SHORT WAYS LINES BLOCKS
            renderFromPointToPoint(poseStack, consumer, x, y, z, 0.4375f, 1.5f, 0.9375f, 0.5625f, 1.5f, 0.9375f, shouldFlip);
            renderFromPointToPoint(poseStack, consumer, x, y, z, 0.4375f, 1.5f, 1.0625f, 0.5625f, 1.5f, 1.0625f, shouldFlip);
            renderFromPointToPoint(poseStack, consumer, x, y, z, 0.4375f, 1.375f, 1.0625f, 0.5625f, 1.375f, 1.0625f, shouldFlip);
            renderFromPointToPoint(poseStack, consumer, x, y, z, 0.4375f, 1.4375f, 0.9375f, 0.5625f, 1.4375f, 0.9375f, shouldFlip);

            renderFromPointToPoint(poseStack, consumer, x, y, z, 0.4375f, 1.5f, 0.0625f, 0.5625f, 1.5f, 0.0625f, shouldFlip);
            renderFromPointToPoint(poseStack, consumer, x, y, z, 0.4375f, 1.5f, -0.0625f, 0.5625f, 1.5f, -0.0625f, shouldFlip);
            renderFromPointToPoint(poseStack, consumer, x, y, z, 0.4375f, 1.375f, -0.0625f, 0.5625f, 1.375f, -0.0625f, shouldFlip);
            renderFromPointToPoint(poseStack, consumer, x, y, z, 0.4375f, 1.4375f, 0.0625f, 0.5625f, 1.4375f, 0.0625f, shouldFlip);

            renderFromPointToPoint(poseStack, consumer, x, y, z, 0.4375f, 1.375f, 0f, 0.5625f, 1.375f, 0f, shouldFlip);
            renderFromPointToPoint(poseStack, consumer, x, y, z, 0.4375f, 1.375f, 1f, 0.5625f, 1.375f, 1f, shouldFlip);

            //ROOF TO BLOCKS INTERSECTION
            renderFromPointToPoint(poseStack, consumer, x, y, z, 0.4375f, 1.375f, 1f, 0.4375f, 1.4f, 1f, shouldFlip);
            renderFromPointToPoint(poseStack, consumer, x, y, z, 0.4375f, 1.375f, 0f, 0.4375f, 1.4f, 0f, shouldFlip);
            renderFromPointToPoint(poseStack, consumer, x, y, z, 0.5625f, 1.375f, 1f, 0.5625f, 1.4f, 1f, shouldFlip);
            renderFromPointToPoint(poseStack, consumer, x, y, z, 0.5625f, 1.375f, 0f, 0.5625f, 1.4f, 0f, shouldFlip);
        }
        poseStack.popPose();
    }

    private static void renderFromPointToPoint(PoseStack poseStack, VertexConsumer consumer, float x, float y, float z, float startX, float startY, float startZ, float endX, float endY, float endZ, boolean flip) {
        PoseStack.Pose last = poseStack.last();
        if (flip) {
            float tempSX = startX;
            float tempEX = endX;
            startX = startZ;
            endX = endZ;
            startZ = tempSX;
            endZ = tempEX;
        }

        float f = startX - endX;
        float f1 = startY - endY;
        float f2 = startZ - endZ;
        float f3 = Mth.sqrt(f * f + f1 * f1 + f2 * f2);
        f /= f3;
        f1 /= f3;
        f2 /= f3;
        consumer.vertex(last.pose(), x+startX, y+startY, z+startZ)
                .color(SELECTION_COLOR.getFloatRed(), SELECTION_COLOR.getFloatGreen(), SELECTION_COLOR.getFloatBlue(), 0.4f)
                .normal(last.normal(), f, f1, f2).endVertex();
        consumer.vertex(last.pose(), x+endX, y+endY, z+endZ)
                .color(SELECTION_COLOR.getFloatRed(), SELECTION_COLOR.getFloatGreen(), SELECTION_COLOR.getFloatBlue(), 0.4f)
                .normal(last.normal(), f, f1, f2).endVertex();
    }
}
