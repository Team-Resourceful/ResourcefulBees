package com.resourcefulbees.resourcefulbees.utils;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector3i;

import java.util.Arrays;

public class RenderCuboid {
    public static final RenderCuboid INSTANCE = new RenderCuboid();
    private static final Vector3f VEC_ZERO = new Vector3f(0.0F, 0.0F, 0.0F);


    private static Vector3f withValue(Vector3f vector, Axis axis, float value) {
        if (axis == Axis.X) {
            return new Vector3f(value, vector.getY(), vector.getZ());
        } else if (axis == Axis.Y) {
            return new Vector3f(vector.getX(), value, vector.getZ());
        } else if (axis == Axis.Z) {
            return new Vector3f(vector.getX(), vector.getY(), value);
        } else {
            throw new CustomException("Was given a null axis! That was probably not intentional, consider this a bug! (Vector = " + vector + ")");
        }
    }

    public static double getValue(Vector3f vector, Axis axis) {
        if (axis == Axis.X) {
            return vector.getX();
        } else if (axis == Axis.Y) {
            return vector.getY();
        } else if (axis == Axis.Z) {
            return vector.getZ();
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

    public void renderCube(CubeModel cube, MatrixStack matrix, IVertexBuilder buffer, int argb, int light, int overlay) {
        float red = getRed(argb);
        float green = getGreen(argb);
        float blue = getBlue(argb);
        float alpha = getAlpha(argb);
        Vector3f size = cube.getSize();
        matrix.push();
        matrix.translate(cube.getStart().getX(), cube.getStart().getY(), cube.getStart().getZ());
        MatrixStack.Entry lastMatrix = matrix.peek();
        Matrix4f matrix4f = lastMatrix.getModel();
        Matrix3f normal = lastMatrix.getNormal();
        Direction[] directions = Direction.values();

        for (Direction direction : directions) {
            Direction face = direction;
            int ordinal = face.ordinal();
            TextureAtlasSprite sprite = cube.sprites[ordinal];
            if (sprite != null) {
                Axis u = face.getAxis() == Axis.X ? Axis.Z : Axis.X;
                Axis v = face.getAxis() == Axis.Y ? Axis.Z : Axis.Y;
                float other = face.getAxisDirection() == Direction.AxisDirection.POSITIVE ? (float) getValue(size, face.getAxis()) : 0.0F;
                face = face.getAxisDirection() == Direction.AxisDirection.NEGATIVE ? face : face.getOpposite();
                Direction opposite = face.getOpposite();
                float minU = sprite.getMinU();
                float maxU = sprite.getMaxU();
                float minV = sprite.getMaxV();
                float maxV = sprite.getMinV();
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

        matrix.pop();
    }

    private void renderPoint(Matrix4f matrix4f, Matrix3f normal, IVertexBuilder buffer, Direction face, Axis u, Axis v, float other, float[] uv, float[] xyz, boolean minU, boolean minV, float red, float green, float blue, float alpha, int light, int overlay) {
        int uArray = minU ? 0 : 1;
        int vArray = minV ? 2 : 3;
        Vector3f vertex = withValue(VEC_ZERO, u, xyz[uArray]);
        vertex = withValue(vertex, v, xyz[vArray]);
        vertex = withValue(vertex, face.getAxis(), other);
        Vector3i normalForFace = face.getDirectionVec();
        float adjustment = 2.5F;
        Vector3f norm = new Vector3f((float) normalForFace.getX() + adjustment, (float) normalForFace.getY() + adjustment, (float) normalForFace.getZ() + adjustment);
        norm.normalize();
        buffer.vertex(matrix4f, vertex.getX(), vertex.getY(), vertex.getZ()).color(red, green, blue, alpha).texture(uv[uArray], uv[vArray]).overlay(overlay).light(light).normal(normal, norm.getX(), norm.getY(), norm.getZ()).endVertex();
    }

    private static class CustomException extends RuntimeException {
        public CustomException(String message) {
            super(message);
        }
    }
}

