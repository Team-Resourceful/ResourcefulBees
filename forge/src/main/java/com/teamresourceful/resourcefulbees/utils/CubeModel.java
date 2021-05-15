package com.teamresourceful.resourcefulbees.utils;

import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

public class CubeModel {

    private final Vector3f start;
    private final Vector3f end;
    public final TextureAtlasSprite[] sprites = new TextureAtlasSprite[6];

    public CubeModel(Vector3f start, Vector3f end) {
        this.start = start;
        this.end = end;
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
        return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(spriteLocation);
    }

    public Vector3f getSize() {
        return new Vector3f(this.getEnd().x()- this.getStart().x(), this.getEnd().y()- this.getStart().y(), this.getEnd().z()- this.getStart().z());
    }

    public Vector3f getStart() {
        return start;
    }

    public Vector3f getEnd() {
        return end;
    }
}
