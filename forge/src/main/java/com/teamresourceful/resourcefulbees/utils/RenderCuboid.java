package com.teamresourceful.resourcefulbees.utils;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;

import java.util.Arrays;

public class RenderCuboid {
    public static final RenderCuboid INSTANCE = new RenderCuboid();
    private static final Vector3f VEC_ZERO = new Vector3f(0.0F, 0.0F, 0.0F);


    private static Vector3f withValue(Vector3f vector, Direction.Axis axis, float value) {
        if (axis == Direction.Axis.X) {
            return new Vector3f(value, vector.y(), vector.z());
        } else if (axis == Direction.Axis.Y) {
            return new Vector3f(vector.x(), value, vector.z());
        } else if (axis == Direction.Axis.Z) {
            return new Vector3f(vector.x(), vector.y(), value);
        } else {
            throw new CustomException("Was given a null axis! That was probably not intentional, consider this a bug! (Vector = " + vector + ")");
        }
    }

    public static double getValue(Vector3f vector, Direction.Axis axis) {
        if (axis == Direction.Axis.X) {
            return vector.x();
        } else if (axis == Direction.Axis.Y) {
            return vector.y();
        } else if (axis == Direction.Axis.Z) {
            return vector.z();
        } else {
            throw new CustomException("Was given a null axis! That was probably not intentional, consider this a bug! (Vector = " + vector + ")");
        }
    }

    public static float getRed(int color) {
        return (float) (color >> 16 & 255) / 255.0F;
    }

    public static float getGreen(int color) {
        return (float) (color >> 8 & 255) / 255.0F;
    }

    public static float getBlue(int color) {
        return (float) (color & 255) / 255.0F;
    }

    public static float getAlpha(int color) {
        return (float) (color >> 24 & 255) / 255.0F;
    }

    public void renderCube(CubeModel cube, PoseStack matrix, VertexConsumer buffer, int argb, int light, int overlay) {
        float red = getRed(argb);
        float green = getGreen(argb);
        float blue = getBlue(argb);
        float alpha = getAlpha(argb);
        Vector3f size = cube.getSize();
        matrix.pushPose();
        matrix.translate(cube.getStart().x(), cube.getStart().y(), cube.getStart().z());
        PoseStack.Pose lastMatrix = matrix.last();
        Matrix4f matrix4f = lastMatrix.pose();
        Matrix3f normal = lastMatrix.normal();
        Direction[] directions = Direction.values();

        for (Direction direction : directions) {
            Direction face = direction;
            int ordinal = face.ordinal();
            TextureAtlasSprite sprite = cube.sprites[ordinal];
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
                for (int uIndex = 0; (double) uIndex < sizeU; ++uIndex) {
                    float[] baseUV = new float[]{minU, maxU, minV, maxV};
                    double addU = 1.0D;
                    if ((double) uIndex + addU > sizeU) {
                        addU = sizeU - (double) uIndex;
                        baseUV[1] = baseUV[0] + (baseUV[1] - baseUV[0]) * (float) addU;
                    }
                    for (int vIndex = 0; (double) vIndex < sizeV; ++vIndex) {
                        float[] uv = Arrays.copyOf(baseUV, 4);
                        double addV = 1.0D;
                        if ((double) vIndex + addV > sizeV) {
                            addV = sizeV - (double) vIndex;
                            uv[3] = uv[2] + (uv[3] - uv[2]) * (float) addV;
                        }
                        float[] xyz = new float[]{(float) uIndex, (float) ((double) uIndex + addU), (float) vIndex, (float) ((double) vIndex + addV)};
                        this.renderPoint(matrix4f, normal, buffer, face, u, v, other, uv, xyz, true, false, red, green, blue, alpha, light, overlay);
                        this.renderPoint(matrix4f, normal, buffer, face, u, v, other, uv, xyz, true, true, red, green, blue, alpha, light, overlay);
                        this.renderPoint(matrix4f, normal, buffer, face, u, v, other, uv, xyz, false, true, red, green, blue, alpha, light, overlay);
                        this.renderPoint(matrix4f, normal, buffer, face, u, v, other, uv, xyz, false, false, red, green, blue, alpha, light, overlay);
                        this.renderPoint(matrix4f, normal, buffer, opposite, u, v, other, uv, xyz, false, false, red, green, blue, alpha, light, overlay);
                        this.renderPoint(matrix4f, normal, buffer, opposite, u, v, other, uv, xyz, false, true, red, green, blue, alpha, light, overlay);
                        this.renderPoint(matrix4f, normal, buffer, opposite, u, v, other, uv, xyz, true, true, red, green, blue, alpha, light, overlay);
                        this.renderPoint(matrix4f, normal, buffer, opposite, u, v, other, uv, xyz, true, false, red, green, blue, alpha, light, overlay);
                    }
                }
            }
        }

        matrix.popPose();
    }

    private void renderPoint(Matrix4f matrix4f, Matrix3f normal, VertexConsumer buffer, Direction face, Direction.Axis u, Direction.Axis v, float other, float[] uv, float[] xyz, boolean minU, boolean minV, float red, float green, float blue, float alpha, int light, int overlay) {
        int uArray = minU ? 0 : 1;
        int vArray = minV ? 2 : 3;
        Vector3f vertex = withValue(VEC_ZERO, u, xyz[uArray]);
        vertex = withValue(vertex, v, xyz[vArray]);
        vertex = withValue(vertex, face.getAxis(), other);
        Vec3i normalForFace = face.getNormal();
        float adjustment = 2.5F;
        Vector3f norm = new Vector3f((float) normalForFace.getX() + adjustment, (float) normalForFace.getY() + adjustment, (float) normalForFace.getZ() + adjustment);
        norm.normalize();
        buffer.vertex(matrix4f, vertex.x(), vertex.y(), vertex.z()).color(red, green, blue, alpha).uv(uv[uArray], uv[vArray]).overlayCoords(overlay).uv2(light).normal(normal, norm.x(), norm.y(), norm.z()).endVertex();
    }

    private static class CustomException extends RuntimeException {
        public CustomException(String message) {
            super(message);
        }
    }
}

