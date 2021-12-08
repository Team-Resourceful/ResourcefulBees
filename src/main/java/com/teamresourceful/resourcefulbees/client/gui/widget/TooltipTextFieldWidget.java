package com.teamresourceful.resourcefulbees.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

@OnlyIn(Dist.CLIENT)
public class TooltipTextFieldWidget extends TooltipWidget{

    private final Font font;
    private String value = "";
    private int maxLength = 32;
    private int frame;
    private boolean bordered = true;
    private boolean canLoseFocus = true;
    private boolean isEditable = true;
    private boolean shiftPressed;
    private int displayPos;
    private int cursorPos;
    private int highlightPos;
    private int textColor = 14737632;
    private int textColorUneditable = 7368816;
    private String suggestion;
    private Consumer<String> responder;
    private Predicate<String> filter = Objects::nonNull;
    private BiFunction<String, Integer, FormattedCharSequence> formatter = (p_195610_0_, p_195610_1_) -> {
        return FormattedCharSequence.forward(p_195610_0_, Style.EMPTY);
    };

    public TooltipTextFieldWidget(Font p_i232260_1_, int p_i232260_2_, int p_i232260_3_, int p_i232260_4_, int p_i232260_5_, Component p_i232260_6_) {
        this(p_i232260_1_, p_i232260_2_, p_i232260_3_, p_i232260_4_, p_i232260_5_, null, p_i232260_6_);
    }

    public TooltipTextFieldWidget(Font p_i232259_1_, int p_i232259_2_, int p_i232259_3_, int p_i232259_4_, int p_i232259_5_, @Nullable EditBox p_i232259_6_, Component p_i232259_7_) {
        super(p_i232259_2_, p_i232259_3_, p_i232259_4_, p_i232259_5_, p_i232259_7_);
        this.font = p_i232259_1_;
        if (p_i232259_6_ != null) {
            this.setValue(p_i232259_6_.getValue());
        }

    }

    public void setResponder(Consumer<String> p_212954_1_) {
        this.responder = p_212954_1_;
    }

    public void setFormatter(BiFunction<String, Integer, FormattedCharSequence> p_195607_1_) {
        this.formatter = p_195607_1_;
    }

    public void tick() {
        ++this.frame;
    }

    protected MutableComponent createNarrationMessage() {
        return new TranslatableComponent("gui.narrate.editBox", this.getMessage(), this.value);
    }

    public void setValue(String p_146180_1_) {
        if (this.filter.test(p_146180_1_)) {
            if (p_146180_1_.length() > this.maxLength) {
                this.value = p_146180_1_.substring(0, this.maxLength);
            } else {
                this.value = p_146180_1_;
            }

            this.moveCursorToEnd();
            this.setHighlightPos(this.cursorPos);
            this.onValueChange(p_146180_1_);
        }
    }

    public String getValue() {
        return this.value;
    }

    public String getHighlighted() {
        int i = this.cursorPos < this.highlightPos ? this.cursorPos : this.highlightPos;
        int j = this.cursorPos < this.highlightPos ? this.highlightPos : this.cursorPos;
        return this.value.substring(i, j);
    }

    public void setFilter(Predicate<String> p_200675_1_) {
        this.filter = p_200675_1_;
    }

    public void insertText(String p_146191_1_) {
        int i = this.cursorPos < this.highlightPos ? this.cursorPos : this.highlightPos;
        int j = this.cursorPos < this.highlightPos ? this.highlightPos : this.cursorPos;
        int k = this.maxLength - this.value.length() - (i - j);
        String s = SharedConstants.filterText(p_146191_1_);
        int l = s.length();
        if (k < l) {
            s = s.substring(0, k);
            l = k;
        }

        String s1 = (new StringBuilder(this.value)).replace(i, j, s).toString();
        if (this.filter.test(s1)) {
            this.value = s1;
            this.setCursorPosition(i + l);
            this.setHighlightPos(this.cursorPos);
            this.onValueChange(this.value);
        }
    }

    private void onValueChange(String p_212951_1_) {
        if (this.responder != null) {
            this.responder.accept(p_212951_1_);
        }

        //TODO this.nextNarration = Util.getMillis() + 500L;
    }

    private void deleteText(int p_212950_1_) {
        if (Screen.hasControlDown()) {
            this.deleteWords(p_212950_1_);
        } else {
            this.deleteChars(p_212950_1_);
        }

    }

    public void deleteWords(int p_146177_1_) {
        if (!this.value.isEmpty()) {
            if (this.highlightPos != this.cursorPos) {
                this.insertText("");
            } else {
                this.deleteChars(this.getWordPosition(p_146177_1_) - this.cursorPos);
            }
        }
    }

    public void deleteChars(int p_146175_1_) {
        if (!this.value.isEmpty()) {
            if (this.highlightPos != this.cursorPos) {
                this.insertText("");
            } else {
                int i = this.getCursorPos(p_146175_1_);
                int j = Math.min(i, this.cursorPos);
                int k = Math.max(i, this.cursorPos);
                if (j != k) {
                    String s = (new StringBuilder(this.value)).delete(j, k).toString();
                    if (this.filter.test(s)) {
                        this.value = s;
                        this.moveCursorTo(j);
                    }
                }
            }
        }
    }

    public int getWordPosition(int p_146187_1_) {
        return this.getWordPosition(p_146187_1_, this.getCursorPosition());
    }

    private int getWordPosition(int p_146183_1_, int p_146183_2_) {
        return this.getWordPosition(p_146183_1_, p_146183_2_, true);
    }

    private int getWordPosition(int p_146197_1_, int p_146197_2_, boolean p_146197_3_) {
        int i = p_146197_2_;
        boolean flag = p_146197_1_ < 0;
        int j = Math.abs(p_146197_1_);

        for(int k = 0; k < j; ++k) {
            if (!flag) {
                int l = this.value.length();
                i = this.value.indexOf(32, i);
                if (i == -1) {
                    i = l;
                } else {
                    while(p_146197_3_ && i < l && this.value.charAt(i) == ' ') {
                        ++i;
                    }
                }
            } else {
                while(p_146197_3_ && i > 0 && this.value.charAt(i - 1) == ' ') {
                    --i;
                }

                while(i > 0 && this.value.charAt(i - 1) != ' ') {
                    --i;
                }
            }
        }

        return i;
    }

    public void moveCursor(int p_146182_1_) {
        this.moveCursorTo(this.getCursorPos(p_146182_1_));
    }

    private int getCursorPos(int p_238516_1_) {
        return Util.offsetByCodepoints(this.value, this.cursorPos, p_238516_1_);
    }

    public void moveCursorTo(int p_146190_1_) {
        this.setCursorPosition(p_146190_1_);
        if (!this.shiftPressed) {
            this.setHighlightPos(this.cursorPos);
        }

        this.onValueChange(this.value);
    }

    public void setCursorPosition(int p_212422_1_) {
        this.cursorPos = Mth.clamp(p_212422_1_, 0, this.value.length());
    }

    public void moveCursorToStart() {
        this.moveCursorTo(0);
    }

    public void moveCursorToEnd() {
        this.moveCursorTo(this.value.length());
    }

    public boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_) {
        if (!this.canConsumeInput()) {
            return false;
        } else {
            this.shiftPressed = Screen.hasShiftDown();
            if (Screen.isSelectAll(p_231046_1_)) {
                this.moveCursorToEnd();
                this.setHighlightPos(0);
                return true;
            } else if (Screen.isCopy(p_231046_1_)) {
                Minecraft.getInstance().keyboardHandler.setClipboard(this.getHighlighted());
                return true;
            } else if (Screen.isPaste(p_231046_1_)) {
                if (this.isEditable) {
                    this.insertText(Minecraft.getInstance().keyboardHandler.getClipboard());
                }

                return true;
            } else if (Screen.isCut(p_231046_1_)) {
                Minecraft.getInstance().keyboardHandler.setClipboard(this.getHighlighted());
                if (this.isEditable) {
                    this.insertText("");
                }

                return true;
            } else {
                switch(p_231046_1_) {
                    case 259:
                        if (this.isEditable) {
                            this.shiftPressed = false;
                            this.deleteText(-1);
                            this.shiftPressed = Screen.hasShiftDown();
                        }

                        return true;
                    case 260:
                    case 264:
                    case 265:
                    case 266:
                    case 267:
                    default:
                        return false;
                    case 261:
                        if (this.isEditable) {
                            this.shiftPressed = false;
                            this.deleteText(1);
                            this.shiftPressed = Screen.hasShiftDown();
                        }

                        return true;
                    case 262:
                        if (Screen.hasControlDown()) {
                            this.moveCursorTo(this.getWordPosition(1));
                        } else {
                            this.moveCursor(1);
                        }

                        return true;
                    case 263:
                        if (Screen.hasControlDown()) {
                            this.moveCursorTo(this.getWordPosition(-1));
                        } else {
                            this.moveCursor(-1);
                        }

                        return true;
                    case 268:
                        this.moveCursorToStart();
                        return true;
                    case 269:
                        this.moveCursorToEnd();
                        return true;
                }
            }
        }
    }

    public boolean canConsumeInput() {
        return this.isVisible() && this.isFocused() && this.isEditable();
    }

    public boolean charTyped(char p_231042_1_, int p_231042_2_) {
        if (!this.canConsumeInput()) {
            return false;
        } else if (SharedConstants.isAllowedChatCharacter(p_231042_1_)) {
            if (this.isEditable) {
                this.insertText(Character.toString(p_231042_1_));
            }

            return true;
        } else {
            return false;
        }
    }

    public boolean mouseClicked(double p_231044_1_, double p_231044_3_, int p_231044_5_) {
        if (!this.isVisible()) {
            return false;
        } else {
            boolean flag = p_231044_1_ >= (double)this.x && p_231044_1_ < (double)(this.x + this.width) && p_231044_3_ >= (double)this.y && p_231044_3_ < (double)(this.y + this.height);
            if (this.canLoseFocus) {
                this.setFocus(flag);
            }

            if (this.isFocused() && flag && p_231044_5_ == 0) {
                int i = Mth.floor(p_231044_1_) - this.x;
                if (this.bordered) {
                    i -= 4;
                }

                String s = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), this.getInnerWidth());
                this.moveCursorTo(this.font.plainSubstrByWidth(s, i).length() + this.displayPos);
                return true;
            } else {
                return false;
            }
        }
    }

    public void setFocus(boolean p_146195_1_) {
        super.setFocused(p_146195_1_);
    }

    public void renderButton(PoseStack p_230431_1_, int p_230431_2_, int p_230431_3_, float p_230431_4_) {
        if (this.isVisible()) {
            if (this.isBordered()) {
                int i = this.isFocused() ? -1 : -6250336;
                fill(p_230431_1_, this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, i);
                fill(p_230431_1_, this.x, this.y, this.x + this.width, this.y + this.height, -16777216);
            }

            int i2 = this.isEditable ? this.textColor : this.textColorUneditable;
            int j = this.cursorPos - this.displayPos;
            int k = this.highlightPos - this.displayPos;
            String s = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), this.getInnerWidth());
            boolean flag = j >= 0 && j <= s.length();
            boolean flag1 = this.isFocused() && this.frame / 6 % 2 == 0 && flag;
            int l = this.bordered ? this.x + 4 : this.x;
            int i1 = this.bordered ? this.y + (this.height - 8) / 2 : this.y;
            int j1 = l;
            if (k > s.length()) {
                k = s.length();
            }

            if (!s.isEmpty()) {
                String s1 = flag ? s.substring(0, j) : s;
                j1 = this.font.drawShadow(p_230431_1_, this.formatter.apply(s1, this.displayPos), (float)l, (float)i1, i2);
            }

            boolean flag2 = this.cursorPos < this.value.length() || this.value.length() >= this.getMaxLength();
            int k1 = j1;
            if (!flag) {
                k1 = j > 0 ? l + this.width : l;
            } else if (flag2) {
                k1 = j1 - 1;
                --j1;
            }

            if (!s.isEmpty() && flag && j < s.length()) {
                this.font.drawShadow(p_230431_1_, this.formatter.apply(s.substring(j), this.cursorPos), (float)j1, (float)i1, i2);
            }

            if (!flag2 && this.suggestion != null) {
                this.font.drawShadow(p_230431_1_, this.suggestion, (float)(k1 - 1), (float)i1, -8355712);
            }

            if (flag1) {
                if (flag2) {
                    Gui.fill(p_230431_1_, k1, i1 - 1, k1 + 1, i1 + 1 + 9, -3092272);
                } else {
                    this.font.drawShadow(p_230431_1_, "_", (float)k1, (float)i1, i2);
                }
            }

            if (k != j) {
                int l1 = l + this.font.width(s.substring(0, k));
                this.renderHighlight(k1, i1 - 1, l1 - 1, i1 + 1 + 9);
            }

        }
    }

    private void renderHighlight(int p_146188_1_, int p_146188_2_, int p_146188_3_, int p_146188_4_) {
        if (p_146188_1_ < p_146188_3_) {
            int i = p_146188_1_;
            p_146188_1_ = p_146188_3_;
            p_146188_3_ = i;
        }

        if (p_146188_2_ < p_146188_4_) {
            int j = p_146188_2_;
            p_146188_2_ = p_146188_4_;
            p_146188_4_ = j;
        }

        if (p_146188_3_ > this.x + this.width) {
            p_146188_3_ = this.x + this.width;
        }

        if (p_146188_1_ > this.x + this.width) {
            p_146188_1_ = this.x + this.width;
        }

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        RenderSystem.setShaderColor(0.0F, 0.0F, 255.0F, 255.0F);
        RenderSystem.disableTexture();
        RenderSystem.enableColorLogicOp();
        RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
        bufferbuilder.vertex(p_146188_1_, p_146188_4_, 0.0D).endVertex();
        bufferbuilder.vertex(p_146188_3_, p_146188_4_, 0.0D).endVertex();
        bufferbuilder.vertex(p_146188_3_, p_146188_2_, 0.0D).endVertex();
        bufferbuilder.vertex(p_146188_1_, p_146188_2_, 0.0D).endVertex();
        tessellator.end();
        RenderSystem.disableColorLogicOp();
        RenderSystem.enableTexture();
    }

    public void setMaxLength(int p_146203_1_) {
        this.maxLength = p_146203_1_;
        if (this.value.length() > p_146203_1_) {
            this.value = this.value.substring(0, p_146203_1_);
            this.onValueChange(this.value);
        }

    }

    private int getMaxLength() {
        return this.maxLength;
    }

    public int getCursorPosition() {
        return this.cursorPos;
    }

    private boolean isBordered() {
        return this.bordered;
    }

    public void setBordered(boolean p_146185_1_) {
        this.bordered = p_146185_1_;
    }

    public void setTextColor(int p_146193_1_) {
        this.textColor = p_146193_1_;
    }

    public void setTextColorUneditable(int p_146204_1_) {
        this.textColorUneditable = p_146204_1_;
    }

    public boolean changeFocus(boolean p_231049_1_) {
        return this.visible && this.isEditable ? super.changeFocus(p_231049_1_) : false;
    }

    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.visible && mouseX >= (double)this.x && mouseX < (double)(this.x + this.width) && mouseY >= (double)this.y && mouseY < (double)(this.y + this.height);
    }

    protected void onFocusedChanged(boolean p_230995_1_) {
        if (p_230995_1_) {
            this.frame = 0;
        }

    }

    private boolean isEditable() {
        return this.isEditable;
    }

    public void setEditable(boolean p_146184_1_) {
        this.isEditable = p_146184_1_;
    }

    public int getInnerWidth() {
        return this.isBordered() ? this.width - 8 : this.width;
    }

    public void setHighlightPos(int p_146199_1_) {
        int i = this.value.length();
        this.highlightPos = Mth.clamp(p_146199_1_, 0, i);
        if (this.font != null) {
            if (this.displayPos > i) {
                this.displayPos = i;
            }

            int j = this.getInnerWidth();
            String s = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), j);
            int k = s.length() + this.displayPos;
            if (this.highlightPos == this.displayPos) {
                this.displayPos -= this.font.plainSubstrByWidth(this.value, j, true).length();
            }

            if (this.highlightPos > k) {
                this.displayPos += this.highlightPos - k;
            } else if (this.highlightPos <= this.displayPos) {
                this.displayPos -= this.displayPos - this.highlightPos;
            }

            this.displayPos = Mth.clamp(this.displayPos, 0, i);
        }

    }

    public void setCanLoseFocus(boolean p_146205_1_) {
        this.canLoseFocus = p_146205_1_;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean p_146189_1_) {
        this.visible = p_146189_1_;
    }

    public void setSuggestion(@Nullable String p_195612_1_) {
        this.suggestion = p_195612_1_;
    }

    public int getScreenX(int p_195611_1_) {
        return p_195611_1_ > this.value.length() ? this.x : this.x + this.font.width(this.value.substring(0, p_195611_1_));
    }

    public void setX(int p_212952_1_) {
        this.x = p_212952_1_;
    }
}
