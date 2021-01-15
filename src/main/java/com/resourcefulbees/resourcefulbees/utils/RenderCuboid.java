package com.resourcefulbees.resourcefulbees.utils;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.vector.*;

import java.awt.*;
import java.util.Arrays;

public class RenderCuboid {
    public static final RenderCuboid INSTANCE = new RenderCuboid();
    private static final Vector3f VEC_ZERO = new Vector3f(0.0F, 0.0F, 0.0F);
    private static final int U_MIN = 0;
    private static final int U_MAX = 1;
    private static final int V_MIN = 2;
    private static final int V_MAX = 3;
    protected EntityRendererManager manager = Minecraft.getInstance().getRenderManager();


    private static Vector3f withValue(Vector3f vector, Axis axis, float value) {
        if (axis == Axis.X) {
            return new Vector3f(value, vector.getY(), vector.getZ());
        } else if (axis == Axis.Y) {
            return new Vector3f(vector.getX(), value, vector.getZ());
        } else if (axis == Axis.Z) {
            return new Vector3f(vector.getX(), vector.getY(), value);
        } else {
            throw new RuntimeException("Was given a null axis! That was probably not intentional, consider this a bug! (Vector = " + vector + ")");
        }
    }

    public static double getValue(Vector3d vector, Axis axis) {
        if (axis == Axis.X) {
            return vector.x;
        } else if (axis == Axis.Y) {
            return vector.y;
        } else if (axis == Axis.Z) {
            return vector.z;
        } else {
            throw new RuntimeException("Was given a null axis! That was probably not intentional, consider this a bug! (Vector = " + vector + ")");
        }
    }

    public void renderCube(CubeModel cube, MatrixStack matrix, IVertexBuilder buffer, int argb, int light, int overlay) {
        Color color = new Color(argb);
        Vector3d size = new Vector3d(cube.sizeX(), cube.sizeY(), cube.sizeZ());
        matrix.push();
        matrix.translate(cube.start.getX(), cube.start.getY(), cube.start.getZ());
        MatrixStack.Entry lastMatrix = matrix.peek();
        Matrix4f matrix4f = lastMatrix.getModel();
        Matrix3f normal = lastMatrix.getNormal();
        Direction[] directions = Direction.values();

        for (int i = 0; i < directions.length; ++i) {
            Direction face = directions[i];
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
                        this.renderPoint(matrix4f, normal, buffer, face, u, v, other, uv, xyz, true, false, color, light, overlay);
                        this.renderPoint(matrix4f, normal, buffer, face, u, v, other, uv, xyz, true, true, color, light, overlay);
                        this.renderPoint(matrix4f, normal, buffer, face, u, v, other, uv, xyz, false, true, color, light, overlay);
                        this.renderPoint(matrix4f, normal, buffer, face, u, v, other, uv, xyz, false, false, color, light, overlay);
                        this.renderPoint(matrix4f, normal, buffer, opposite, u, v, other, uv, xyz, false, false, color, light, overlay);
                        this.renderPoint(matrix4f, normal, buffer, opposite, u, v, other, uv, xyz, false, true, color, light, overlay);
                        this.renderPoint(matrix4f, normal, buffer, opposite, u, v, other, uv, xyz, true, true, color, light, overlay);
                        this.renderPoint(matrix4f, normal, buffer, opposite, u, v, other, uv, xyz, true, false, color, light, overlay);
                    }
                }
            }
        }

        matrix.pop();
    }

    private void renderPoint(Matrix4f matrix4f, Matrix3f normal, IVertexBuilder buffer, Direction face, Axis u, Axis v, float other, float[] uv, float[] xyz, boolean minU, boolean minV, Color color, int light, int overlay) {
        int U_ARRAY = minU ? 0 : 1;
        int V_ARRAY = minV ? 2 : 3;
        Vector3f vertex = withValue(VEC_ZERO, u, xyz[U_ARRAY]);
        vertex = withValue(vertex, v, xyz[V_ARRAY]);
        vertex = withValue(vertex, face.getAxis(), other);
        Vector3i normalForFace = face.getDirectionVec();
        float adjustment = 2.5F;
        Vector3f norm = new Vector3f((float) normalForFace.getX() + adjustment, (float) normalForFace.getY() + adjustment, (float) normalForFace.getZ() + adjustment);
        norm.normalize();
        buffer.vertex(matrix4f, vertex.getX(), vertex.getY(), vertex.getZ()).color(color.getRed(), color.getBlue(), color.getGreen(), color.getAlpha()).texture(uv[U_ARRAY], uv[V_ARRAY]).overlay(overlay).light(light).normal(normal, norm.getX(), norm.getY(), norm.getZ()).endVertex();
    }
}

