package com.resourcefulbees.resourcefulbees.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class CubeModel {

    //TODO - ummm lots of unused methods and duplicate code.....

    public Vector3f start;
    public Vector3f end;
    public final TextureAtlasSprite[] sprites = new TextureAtlasSprite[6];

    public CubeModel(Vector3f start, Vector3f end) {
        this.start = start;
        this.end = end;
    }

    public CubeModel(float startX, float startY, float startZ, float endX, float endY, float endZ) {
        this.start = new Vector3f(startX, startY, startZ);
        this.end = new Vector3f(endX, endY, endZ);
    }

    public void setTextures(TextureAtlasSprite down, TextureAtlasSprite up, TextureAtlasSprite north, TextureAtlasSprite south, TextureAtlasSprite west, TextureAtlasSprite east) {
        this.sprites[0] = down;
        this.sprites[1] = up;
        this.sprites[2] = north;
        this.sprites[3] = south;
        this.sprites[4] = west;
        this.sprites[5] = east;
    }

    public void setTextures(TextureAtlasSprite texture) {
        this.sprites[0] = texture;
        this.sprites[1] = texture;
        this.sprites[2] = texture;
        this.sprites[3] = texture;
        this.sprites[4] = texture;
        this.sprites[5] = texture;
    }

    public void setTextures(TextureAtlasSprite top, TextureAtlasSprite sides, TextureAtlasSprite bottom) {
        this.sprites[0] = bottom;
        this.sprites[1] = top;
        this.sprites[2] = sides;
        this.sprites[3] = sides;
        this.sprites[4] = sides;
        this.sprites[5] = sides;
    }

    public void setTextures(ResourceLocation down, ResourceLocation up, ResourceLocation north, ResourceLocation south, ResourceLocation west, ResourceLocation east) {
        this.sprites[0] = getSprite(down);
        this.sprites[1] = getSprite(up);
        this.sprites[2] = getSprite(north);
        this.sprites[3] = getSprite(south);
        this.sprites[4] = getSprite(west);
        this.sprites[5] = getSprite(east);
    }

    public void setTextures(ResourceLocation top, ResourceLocation sides, ResourceLocation bottom) {
        this.sprites[0] = getSprite(bottom);
        this.sprites[1] = getSprite(top);
        this.sprites[2] = getSprite(sides);
        this.sprites[3] = getSprite(sides);
        this.sprites[4] = getSprite(sides);
        this.sprites[5] = getSprite(sides);
    }

    public void setTextures(ResourceLocation texture) {
        TextureAtlasSprite sprite = getSprite(texture);
        this.sprites[0] = sprite;
        this.sprites[1] = sprite;
        this.sprites[2] = sprite;
        this.sprites[3] = sprite;
        this.sprites[4] = sprite;
        this.sprites[5] = sprite;
    }

    public static TextureAtlasSprite getSprite(ResourceLocation spriteLocation) {
        return Minecraft.getInstance().getSpriteAtlas(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(spriteLocation);
    }

    public Vector3f getSize() {
        return new Vector3f(this.end.getX()- this.start.getX(), this.end.getY()- this.start.getY(), this.end.getZ()- this.start.getZ());
    }

    public float sizeX() {
        return this.end.getX() - this.start.getX();
    }

    public float sizeY() {
        return this.end.getY() - this.start.getY();
    }

    public float sizeZ() {
        return this.end.getZ() - this.start.getZ();
    }
}
