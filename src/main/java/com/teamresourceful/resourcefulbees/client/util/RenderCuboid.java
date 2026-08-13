package com.teamresourceful.resourcefulbees.client.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;

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

/*    public static void renderCube(
            AABB box,
            Identifier texture,
            PoseStack.Pose pose,
            VertexConsumer buffer,
            int argb,
            int light,
            int overlay
    ) {
        TextureAtlasSprite sprite = Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(texture);

        renderCube(box, sprite, pose, buffer, argb, light, overlay);
    }*/

    public static void renderCube(
            AABB box,
            TextureAtlasSprite sprite,
            PoseStack.Pose pose,
            VertexConsumer buffer,
            int argb,
            int light,
            int overlay
    ) {
        Matrix4f matrix4f = pose.pose();

        Vec3 size = new Vec3(
                box.getXsize(),
                box.getYsize(),
                box.getZsize()
        );

        for (Direction direction : Direction.values()) {
            Direction face = direction;

            Direction.Axis u = face.getAxis() == Direction.Axis.X
                    ? Direction.Axis.Z
                    : Direction.Axis.X;

            Direction.Axis v = face.getAxis() == Direction.Axis.Y
                    ? Direction.Axis.Z
                    : Direction.Axis.Y;

            float other = face.getAxisDirection() == Direction.AxisDirection.POSITIVE
                    ? (float) getValue(size, face.getAxis())
                    : 0.0F;

            face = face.getAxisDirection() == Direction.AxisDirection.NEGATIVE
                    ? face
                    : face.getOpposite();

            Direction opposite = face.getOpposite();

            float minU = sprite.getU0();
            float maxU = sprite.getU1();
            float minV = sprite.getV1();
            float maxV = sprite.getV0();

            double sizeU = getValue(size, u);
            double sizeV = getValue(size, v);

            for (int uIndex = 0; uIndex < sizeU; ++uIndex) {
                float[] baseUV = {minU, maxU, minV, maxV};

                double addU = Math.min(1.0D, sizeU - uIndex);

                if (addU < 1.0D) {
                    baseUV[1] = baseUV[0]
                            + (baseUV[1] - baseUV[0]) * (float) addU;
                }

                for (int vIndex = 0; vIndex < sizeV; ++vIndex) {
                    float[] uv = Arrays.copyOf(baseUV, 4);

                    double addV = Math.min(1.0D, sizeV - vIndex);

                    if (addV < 1.0D) {
                        uv[3] = uv[2]
                                + (uv[3] - uv[2]) * (float) addV;
                    }

                    float[] xyz = {
                            uIndex,
                            (float) (uIndex + addU),
                            vIndex,
                            (float) (vIndex + addV)
                    };

                    renderPoint(box, matrix4f, buffer, face, u, v, other, uv, xyz, true, false, argb, light, overlay);
                    renderPoint(box, matrix4f, buffer, face, u, v, other, uv, xyz, true, true, argb, light, overlay);
                    renderPoint(box, matrix4f, buffer, face, u, v, other, uv, xyz, false, true, argb, light, overlay);
                    renderPoint(box, matrix4f, buffer, face, u, v, other, uv, xyz, false, false, argb, light, overlay);

                    renderPoint(box, matrix4f, buffer, opposite, u, v, other, uv, xyz, false, false, argb, light, overlay);
                    renderPoint(box, matrix4f, buffer, opposite, u, v, other, uv, xyz, false, true, argb, light, overlay);
                    renderPoint(box, matrix4f, buffer, opposite, u, v, other, uv, xyz, true, true, argb, light, overlay);
                    renderPoint(box, matrix4f, buffer, opposite, u, v, other, uv, xyz, true, false, argb, light, overlay);
                }
            }
        }
    }

    private static void renderPoint(
            AABB box,
            Matrix4f matrix4f,
            VertexConsumer buffer,
            Direction face,
            Direction.Axis u,
            Direction.Axis v,
            float other,
            float[] uv,
            float[] xyz,
            boolean minU,
            boolean minV,
            int color,
            int light,
            int overlay
    ) {
        int uArray = minU ? 0 : 1;
        int vArray = minV ? 2 : 3;

        Vector3f vertex = new Vector3f(
                (float) box.minX,
                (float) box.minY,
                (float) box.minZ
        );

        vertex = addValue(vertex, u, xyz[uArray]);
        vertex = addValue(vertex, v, xyz[vArray]);
        vertex = addValue(vertex, face.getAxis(), other);

        buffer.addVertex(
                        matrix4f,
                        vertex.x(),
                        vertex.y(),
                        vertex.z()
                )
                .setColor(color)
                .setUv(uv[uArray], uv[vArray])
                .setOverlay(overlay)
                .setLight(light)
                .setNormal(
                        face.getStepX(),
                        face.getStepY(),
                        face.getStepZ()
                );
    }

    private static Vector3f addValue(
            Vector3f vector,
            Direction.Axis axis,
            float value
    ) {
        return switch (axis) {
            case X -> new Vector3f(vector.x() + value, vector.y(), vector.z());
            case Y -> new Vector3f(vector.x(), vector.y() + value, vector.z());
            case Z -> new Vector3f(vector.x(), vector.y(), vector.z() + value);
        };
    }

    private RenderCuboid() throws UtilityClassException {
        throw new UtilityClassException();
    }
}

