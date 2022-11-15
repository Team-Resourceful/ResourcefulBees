package com.teamresourceful.resourcefulbees.client.utils;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefullib.client.CloseablePoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;

public final class RenderCuboid {

    private static Vector3f withValue(Vector3f vector, Direction.Axis axis, float value) {
        return switch (axis) {
            case X -> new Vector3f(value, vector.y(), vector.z());
            case Y -> new Vector3f(vector.x(), value, vector.z());
            case Z -> new Vector3f(vector.x(), vector.y(), value);
        };
    }

    public static double getValue(Vec3 vector, Direction.Axis axis) {
        return switch (axis) {
            case X -> vector.x();
            case Y -> vector.y();
            case Z -> vector.z();
        };
    }

    public static void renderCube(AABB box, ResourceLocation texture, PoseStack matrix, VertexConsumer buffer, int argb, int light, int overlay) {
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(texture);
        renderCube(box, sprite, matrix, buffer, argb, light, overlay);
    }

    public static void renderCube(AABB box, TextureAtlasSprite sprite, PoseStack poseStack, VertexConsumer buffer, int argb, int light, int overlay) {
        Vec3 size = new Vec3(box.getXsize(), box.getYsize(), box.getZsize());
        try (var stack = new CloseablePoseStack(poseStack)) {
            stack.translate(box.minX, box.minY, box.minZ);
            PoseStack.Pose lastMatrix = stack.last();
            Matrix4f matrix4f = lastMatrix.pose();
            Matrix3f normal = lastMatrix.normal();
            Direction[] directions = Direction.values();

            for (Direction direction : directions) {
                Direction face = direction;
                if (sprite != null) {
                    Direction.Axis u = face.getAxis() == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
                    Direction.Axis v = face.getAxis() == Direction.Axis.Y ? Direction.Axis.Z : Direction.Axis.Y;
                    float other = face.getAxisDirection() == Direction.AxisDirection.POSITIVE ? (float) getValue(size, face.getAxis()) : 0.0F;
                    face = face.getAxisDirection() == Direction.AxisDirection.NEGATIVE ? face : face.getOpposite();
                    Direction opposite = face.getOpposite();
                    float minU = sprite.getU0();
                    float maxU = sprite.getU1();
                    float minV = sprite.getV1();
                    float maxV = sprite.getV0();
                    double sizeU = getValue(size, u);
                    double sizeV = getValue(size, v);
                    for (int uIndex = 0; uIndex < sizeU; ++uIndex) {
                        float[] baseUV = new float[]{minU, maxU, minV, maxV};
                        double addU = 1.0D;
                        if (uIndex + addU > sizeU) {
                            addU = sizeU - uIndex;
                            baseUV[1] = baseUV[0] + (baseUV[1] - baseUV[0]) * (float) addU;
                        }
                        for (int vIndex = 0; vIndex < sizeV; ++vIndex) {
                            float[] uv = Arrays.copyOf(baseUV, 4);
                            double addV = 1.0D;
                            if (vIndex + addV > sizeV) {
                                addV = sizeV - vIndex;
                                uv[3] = uv[2] + (uv[3] - uv[2]) * (float) addV;
                            }
                            float[] xyz = new float[]{uIndex, (float) (uIndex + addU), vIndex, (float) (vIndex + addV)};
                            renderPoint(matrix4f, normal, buffer, face, u, v, other, uv, xyz, true, false, argb, light, overlay);
                            renderPoint(matrix4f, normal, buffer, face, u, v, other, uv, xyz, true, true, argb, light, overlay);
                            renderPoint(matrix4f, normal, buffer, face, u, v, other, uv, xyz, false, true, argb, light, overlay);
                            renderPoint(matrix4f, normal, buffer, face, u, v, other, uv, xyz, false, false, argb, light, overlay);
                            renderPoint(matrix4f, normal, buffer, opposite, u, v, other, uv, xyz, false, false, argb, light, overlay);
                            renderPoint(matrix4f, normal, buffer, opposite, u, v, other, uv, xyz, false, true, argb, light, overlay);
                            renderPoint(matrix4f, normal, buffer, opposite, u, v, other, uv, xyz, true, true, argb, light, overlay);
                            renderPoint(matrix4f, normal, buffer, opposite, u, v, other, uv, xyz, true, false, argb, light, overlay);
                        }
                    }
                }
            }
        }
    }

    private static void renderPoint(Matrix4f matrix4f, Matrix3f normal, VertexConsumer buffer, Direction face, Direction.Axis u, Direction.Axis v, float other, float[] uv, float[] xyz, boolean minU, boolean minV, int color, int light, int overlay) {
        int uArray = minU ? 0 : 1;
        int vArray = minV ? 2 : 3;
        Vector3f vertex = withValue(Vector3f.ZERO, u, xyz[uArray]);
        vertex = withValue(vertex, v, xyz[vArray]);
        vertex = withValue(vertex, face.getAxis(), other);
        Vec3i normalForFace = face.getNormal();
        float adjustment = 2.5F;
        Vector3f norm = new Vector3f(normalForFace.getX() + adjustment, normalForFace.getY() + adjustment, normalForFace.getZ() + adjustment);
        norm.normalize();
        buffer.vertex(matrix4f, vertex.x(), vertex.y(), vertex.z()).color(color).uv(uv[uArray], uv[vArray]).overlayCoords(overlay).uv2(light).normal(normal, norm.x(), norm.y(), norm.z()).endVertex();
    }

    private RenderCuboid() {
        throw new UtilityClassError();
    }
}

