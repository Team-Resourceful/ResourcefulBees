package com.resourcefulbees.resourcefulbees.compat.patchouli;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.IVariable;

import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;

@OnlyIn(Dist.CLIENT)
public class PotionRecipeComponent implements ICustomComponent {

    IVariable effectID;
    IVariable potionItemID;

    private transient boolean ready = false;
    private transient Potion potion;
    private transient ItemStack stack;
    private transient BrewingRecipe mix;
    private transient List<ItemStack> ingredients;
    private transient List<ItemStack> inputs;
    private transient int inputCounter = 0;
    private transient int ingredientCounter = 0;
    private transient int lastTick = -1;

    private int xOffset;
    private int yOffset;

    @Override
    public void build(int x, int y, int page) {
        xOffset = x;
        yOffset = y;
    }

    @Override
    public void render(@NotNull MatrixStack matrix, @NotNull IComponentRenderContext render, float partialTick, int mouseX, int mouseY) {
        if (!ready) return;

        render.renderItemStack(matrix,  xOffset + 27, yOffset + 5, mouseX, mouseY, ingredients.get(ingredientCounter));
        render.renderItemStack(matrix,  xOffset + 4, yOffset + 39, mouseX, mouseY, inputs.get(inputCounter));
        render.renderItemStack(matrix,  xOffset + 50, yOffset + 39, mouseX, mouseY, mix.getOutput());

        Minecraft.getInstance().textureManager.bind(new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/patchouli/brewing_stand.png"));
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableAlphaTest();
        RenderSystem.enableBlend();
        AbstractGui.blit(matrix, xOffset, yOffset, 0, 0, 70, 59, 70, 59);
        if (lastTick != render.getTicksInBook()) handleCounter(render.getTicksInBook());
    }

    private void handleCounter(int tick) {
        if (tick % 20 == 0 && !Screen.hasShiftDown()) {
            lastTick = tick;
            ingredientCounter++;
            if (ingredientCounter > ingredients.size() - 1) {
                ingredientCounter = 0;
            }
            inputCounter++;
            if (inputCounter > inputs.size() - 1) {
                inputCounter = 0;
            }
        }
    }

    @Override
    public void onVariablesAvailable(UnaryOperator<IVariable> lookup) {
        effectID = lookup.apply(effectID);
        potionItemID = lookup.apply(potionItemID);
        potion = ForgeRegistries.POTION_TYPES.getValue(new ResourceLocation(effectID.asString()));
        stack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(potionItemID.asString())));
        mix = BeeInfoUtils.getMix(potion, stack);
        ready = true;
        if (mix == null) throw new IllegalArgumentException("Could not find mix for ("+ potionItemID.asString() + ":" + effectID.asString() + ")");
        this.ingredients = Arrays.asList(mix.getIngredient().getItems());
        this.inputs = Arrays.asList(mix.getInput().getItems());
    }
}
