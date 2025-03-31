package com.teamresourceful.resourcefulbees.client.component.search;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.teamresourceful.resourcefulbees.client.screen.beepedia.BeepediaScreen;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2ic;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;

public class SearchBox extends AbstractWidget {

    private static final ResourceLocation TEXTURE = new ResourceLocation(ModConstants.MOD_ID, "textures/gui/beepedia/search_box.png");

    private final Font font;
    private String value = "";
    private int frame;
    private int displayPos;
    private int cursorPos;
    private int highlightPos;
    private boolean shiftPressed;

    private final BeepediaScreen screen;

    public SearchBox(Font font, int x, int y, BeepediaScreen screen) {
        super(x, y, 90, 12, CommonComponents.EMPTY);
        this.font = font;
        this.screen = screen;
        if (screen.getState().getSearch() != null) this.setValue(screen.getState().getSearch());
    }

    public void tick() {
        ++this.frame;
    }

    @Override
    protected @NotNull MutableComponent createNarrationMessage() {
        return Component.translatable("gui.narrate.editBox", this.getMessage(), this.value);
    }

    public void setValue(String text) {
        if (Objects.nonNull(text)) {
            if (text.length() > 32) {
                this.value = text.substring(0, 32);
            } else {
                this.value = text;
            }

            this.moveCursorToEnd();
            this.setHighlightPos(this.cursorPos);
            this.onValueChange(text);
        }
    }

    public String getValue() {
        return this.value;
    }

    public String getHighlighted() {
        int minPos = Math.min(this.cursorPos, this.highlightPos);
        int maxPos = Math.max(this.cursorPos, this.highlightPos);
        return this.value.substring(minPos, maxPos);
    }

    public void insertText(String text) {
        int minPos = Math.min(this.cursorPos, this.highlightPos);
        int maxPos = Math.max(this.cursorPos, this.highlightPos);
        int k = 32 - this.value.length() - (minPos - maxPos);
        String filteredText = SharedConstants.filterText(text);
        int l = filteredText.length();
        if (k < l) {
            filteredText = filteredText.substring(0, k);
            l = k;
        }

        this.value = new StringBuilder(this.value).replace(minPos, maxPos, filteredText).toString();
        this.setCursorPosition(minPos + l);
        this.setHighlightPos(this.cursorPos);
        this.onValueChange(this.value);
    }

    private void onValueChange(String text) {
        this.screen.getState().setSearch(text);
        this.screen.updateSelections();
    }

    private void deleteText(int amount) {
        if (Screen.hasControlDown()) {
            this.deleteWords(amount);
        } else {
            this.deleteChars(amount);
        }
    }

    public void deleteWords(int amount) {
        if (this.value.isEmpty()) return;
        if (this.highlightPos != this.cursorPos) {
            this.insertText("");
        } else {
            this.deleteChars(this.getWordPosition(amount) - this.cursorPos);
        }
    }

    public void deleteChars(int amount) {
        if (this.value.isEmpty()) return;
        if (this.highlightPos != this.cursorPos) {
            this.insertText("");
        } else {
            int amountPos = this.getCursorPos(amount);
            int minPos = Math.min(amountPos, this.cursorPos);
            int maxPos = Math.max(amountPos, this.cursorPos);
            if (minPos != maxPos) {
                this.value = new StringBuilder(this.value).delete(minPos, maxPos).toString();
                this.moveCursorTo(minPos);
            }
        }
    }

    public int getWordPosition(int amount) {
        return this.getWordPosition(amount, this.getCursorPosition());
    }

    private int getWordPosition(int amount, int pos) {
        int newPos = pos;
        boolean backwards = amount < 0;

        for(int i = 0; i < Math.abs(amount); ++i) {
            if (!backwards) {
                int textLength = this.value.length();
                newPos = this.value.indexOf(' ', newPos);
                if (newPos == -1) {
                    newPos = textLength;
                } else {
                    while(newPos < textLength && this.value.charAt(newPos) == ' ') {
                        ++newPos;
                    }
                }
            } else {
                while(newPos > 0 && this.value.charAt(newPos - 1) == ' ') {
                    --newPos;
                }

                while(newPos > 0 && this.value.charAt(newPos - 1) != ' ') {
                    --newPos;
                }
            }
        }

        return newPos;
    }

    private int getCursorPos(int amount) {
        return Util.offsetByCodepoints(this.value, this.cursorPos, amount);
    }

    public void moveCursorTo(int pos) {
        this.setCursorPosition(pos);
        if (!this.shiftPressed) {
            this.setHighlightPos(this.cursorPos);
        }

        this.onValueChange(this.value);
    }

    public void setCursorPosition(int pos) {
        this.cursorPos = Mth.clamp(pos, 0, this.value.length());
    }

    public void moveCursorToStart() {
        this.moveCursorTo(0);
    }

    public void moveCursorToEnd() {
        this.moveCursorTo(this.value.length());
    }

    @Override
    public boolean keyPressed(int key, int scan, int modifiers) {
        if (this.cantConsumeInput()) return false;

        this.shiftPressed = Screen.hasShiftDown();
        if (Screen.isSelectAll(key)) {
            this.moveCursorToEnd();
            this.setHighlightPos(0);
            return true;
        } else if (Screen.isCopy(key)) {
            Minecraft.getInstance().keyboardHandler.setClipboard(this.getHighlighted());
            return true;
        } else if (Screen.isPaste(key)) {
            this.insertText(Minecraft.getInstance().keyboardHandler.getClipboard());
            return true;
        } else if (Screen.isCut(key)) {
            Minecraft.getInstance().keyboardHandler.setClipboard(this.getHighlighted());
            this.insertText("");

            return true;
        } else {
            return switch (key) {
                case GLFW.GLFW_KEY_DELETE, GLFW.GLFW_KEY_BACKSPACE -> {
                    this.shiftPressed = false;
                    this.deleteText(key == GLFW.GLFW_KEY_BACKSPACE ? -1 : 1);
                    this.shiftPressed = Screen.hasShiftDown();
                    yield true;
                }
                case GLFW.GLFW_KEY_LEFT, GLFW.GLFW_KEY_RIGHT -> {
                    int amount = key == GLFW.GLFW_KEY_RIGHT ? 1 : -1;
                    this.moveCursorTo(Screen.hasControlDown() ? this.getWordPosition(amount) : amount);
                    yield true;
                }
                case GLFW.GLFW_KEY_HOME -> {
                    this.moveCursorToStart();
                    yield true;
                }
                case GLFW.GLFW_KEY_END -> {
                    this.moveCursorToEnd();
                    yield true;
                }
                default -> false;
            };
        }
    }

    public boolean cantConsumeInput() {
        return !this.isVisible() || !this.isFocused();
    }

    @Override
    public boolean charTyped(char character, int modifiers) {
        if (this.cantConsumeInput()) return false;

        if (SharedConstants.isAllowedChatCharacter(character)) {
            this.insertText(Character.toString(character));
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.isVisible()) return false;

        this.setFocused(mouseX >= this.getX() && mouseX < (this.getX() + this.width) && mouseY >= this.getY() && mouseY < (this.getY() + this.height));

        if (this.isFocused() && button == 0) {
            int i = Mth.floor(mouseX) - this.getX() - 4;

            String s = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), this.getInnerWidth());
            this.moveCursorTo(this.font.plainSubstrByWidth(s, i).length() + this.displayPos);
            return true;
        }
        return false;
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (!this.isVisible()) return;

        graphics.blit(TEXTURE, this.getX(), this.getY(), 0, 0, this.width, this.height, this.width, this.height);

        int j = this.cursorPos - this.displayPos;
        int k = this.highlightPos - this.displayPos;
        String displayText = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), this.getInnerWidth());
        boolean flag = j >= 0 && j <= displayText.length();
        boolean showFlash = this.isFocused() && this.frame / 6 % 2 == 0 && flag;
        int j1 = this.getX() + 3;
        if (k > displayText.length()) {
            k = displayText.length();
        }

        if (!displayText.isEmpty()) {
            String s1 = flag ? displayText.substring(0, j) : displayText;
            j1 = graphics.drawString(this.font, FormattedCharSequence.forward(s1, Style.EMPTY), this.getX() + 2, this.getY() + 2, 0xE0E0E0, false);
        }

        boolean showVerticalBar = this.cursorPos < this.value.length() || this.value.length() >= 32;
        int k1 = j1;
        if (!flag) {
            k1 = j > 0 ? this.getX() + this.width : this.getX();
        } else if (showVerticalBar) {
            k1 = j1 - 1;
            --j1;
        }

        if (!displayText.isEmpty() && flag && j < displayText.length()) {
            graphics.drawString(this.font, FormattedCharSequence.forward(displayText.substring(j), Style.EMPTY), j1, this.getY() + 2, 0xE0E0E0, false);
        }

        if (showFlash) {
            if (showVerticalBar) {
                graphics.fill(k1, this.getY() - 2, k1 + 1, this.getY() + 11, -3092272);
            } else {
                graphics.drawString(this.font, "_", k1, this.getY() + 1, 0xE0E0E0, false);
            }
        }

        if (k != j) {
            int l1 = this.getX() + this.font.width(displayText.substring(0, k));
            this.renderHighlight(graphics, k1, this.getY() + 1, l1, this.getY() + 11);
        }

    }

    private void renderHighlight(GuiGraphics graphics, int startX, int startY, int endX, int endY) {
        if (startX < endX) {
            int i = startX;
            startX = endX;
            endX = i;
        }

        if (startY < endY) {
            int j = startY;
            startY = endY;
            endY = j;
        }

        if (endX > this.getX() + this.width) {
            endX = this.getX() + this.width;
        }

        if (startX > this.getX() + this.width) {
            startX = this.getX() + this.width;
        }

        Vector2ic coords = RenderUtils.getTranslation(graphics.pose());
        startX += coords.x();
        startY += coords.y();
        endX += coords.x();
        endY += coords.y();

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionShader);
        RenderSystem.setShaderColor(0.0F, 0.0F, 1.0F, 1.0F);
        RenderSystem.enableColorLogicOp();
        RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
        bufferbuilder.vertex(startX, endY, 0.0D).endVertex();
        bufferbuilder.vertex(endX, endY, 0.0D).endVertex();
        bufferbuilder.vertex(endX, startY, 0.0D).endVertex();
        bufferbuilder.vertex(startX, startY, 0.0D).endVertex();
        tesselator.end();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableColorLogicOp();
    }

    public int getCursorPosition() {
        return this.cursorPos;
    }

    @Override
    public boolean isFocused() {
        return this.isVisible() && super.isFocused();
    }

    @Override
    public boolean isMouseOver(double x, double y) {
        return this.isVisible() && x >= this.getX() && x < (this.getX() + this.width) && y >= this.getY() && y < (this.getY() + this.height);
    }

    @Override
    public void setFocused(boolean focus) {
        super.setFocused(focus);
        if (focus) {
            this.frame = 0;
        }
    }

    public int getInnerWidth() {
        return this.width - 2;
    }

    public void setHighlightPos(int pos) {
        int textLength = this.value.length();
        this.highlightPos = Mth.clamp(pos, 0, textLength);
        if (this.font != null) {
            this.displayPos = Math.min(this.displayPos, textLength);

            String displayText = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), this.getInnerWidth());
            int k = displayText.length() + this.displayPos;
            if (this.highlightPos == this.displayPos) {
                this.displayPos -= this.font.plainSubstrByWidth(this.value, this.getInnerWidth(), true).length();
            }

            if (this.highlightPos > k) {
                this.displayPos += this.highlightPos - k;
            } else if (this.highlightPos <= this.displayPos) {
                this.displayPos -= this.displayPos - this.highlightPos;
            }

            this.displayPos = Mth.clamp(this.displayPos, 0, textLength);
        }

    }

    public boolean isVisible() {
        return this.screen.getState().getSearch() != null && this.visible;
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
        output.add(NarratedElementType.TITLE, Component.translatable("narration.edit_box", this.getValue()));
    }
}
