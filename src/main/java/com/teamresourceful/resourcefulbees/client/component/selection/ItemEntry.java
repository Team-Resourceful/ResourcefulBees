package com.teamresourceful.resourcefulbees.client.component.selection;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefullib.client.CloseablePoseStack;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class ItemEntry<T> extends BaseListEntry {

    public static final ResourceLocation SLOT_TEXTURE = new ResourceLocation(ModConstants.MOD_ID, "textures/gui/beepedia/list_button.png");

    private final T data;
    private final Function<T, ItemStack> getter;
    private final Function<T, Component> nameGetter;

    public ItemEntry(T data, Function<T, ItemStack> getter, Function<T, Component> nameGetter) {
        this.data = data;
        this.getter = getter;
        this.nameGetter = nameGetter;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, @NotNull ScissorBoxStack scissorStack, int id, int left, int top, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick, boolean selected) {
        try (var pose = new CloseablePoseStack(graphics)) {
            pose.translate(left, top, 0);
            Minecraft instance = Minecraft.getInstance();
            Font font = instance.font;
            graphics.renderItem(this.getter.apply(this.data), 3, 2);

            graphics.blit(SLOT_TEXTURE, 1, 0, 0, selected ? 40 : hovered ? 20 : 0, 20, 20, 20, 60);

            int color = selected ? 16777215 : 11184810;
            graphics.drawString(font, this.nameGetter.apply(this.data), 22, 5, color);
        }
    }

    public T getData() {
        return this.data;
    }

}
