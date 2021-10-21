package com.teamresourceful.resourcefulbees.common.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TextComponentCodec {

    private TextComponentCodec()  {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static final Codec<ITextComponent> CODEC = Codec.PASSTHROUGH.comapFlatMap(TextComponentCodec::decodeComponent,
            component -> new Dynamic<>(JsonOps.INSTANCE));

    public static final Codec<Color> COLOR_CODEC = Codec.STRING.comapFlatMap(input -> {
        Color color = Color.parseColor(input);
        return color == null ? DataResult.error("Invalid Color") : DataResult.success(color);
    }, Color::toString);

    private static final Codec<Style> STYLE_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            COLOR_CODEC.fieldOf("color").orElse(Color.fromLegacyFormat(TextFormatting.WHITE)).forGetter(Style::getColor),
            Codec.BOOL.fieldOf("bold").orElse(false).forGetter(Style::isBold),
            Codec.BOOL.fieldOf("italic").orElse(false).forGetter(Style::isItalic),
            Codec.BOOL.fieldOf("underlined").orElse(false).forGetter(Style::isUnderlined),
            Codec.BOOL.fieldOf("strikethrough").orElse(false).forGetter(Style::isStrikethrough),
            Codec.BOOL.fieldOf("obfuscated").orElse(false).forGetter(Style::isObfuscated),
            Codec.STRING.optionalFieldOf("insertion").forGetter(style -> Optional.ofNullable(style.getInsertion())),
            ResourceLocation.CODEC.fieldOf("font").orElse(Style.DEFAULT_FONT).forGetter(Style::getFont)
    ).apply(instance, (color, bold, italic, underlined, strikethrough, obfuscated, insertion, font) -> Style.EMPTY.withColor(color).withBold(bold).withItalic(italic).setUnderlined(underlined).setStrikethrough(strikethrough).setObfuscated(obfuscated).withInsertion(insertion.orElse(null)).withFont(font)));

    private static final Codec<StringTextComponent> TEXT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("text").forGetter(StringTextComponent::getText),
            STYLE_CODEC.fieldOf("style").orElse(Style.EMPTY).forGetter(StringTextComponent::getStyle)
    ).apply(instance, (text, style) -> (StringTextComponent)new StringTextComponent(text).withStyle(style)));

    private static final Codec<TranslationTextComponent> TRANSLATABLE_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("translate").forGetter(TranslationTextComponent::getKey),
            Codec.STRING.listOf().fieldOf("args").orElse(new ArrayList<>()).forGetter((t) -> Arrays.stream(t.getArgs()).map(Object::toString).collect(Collectors.toList())),
            STYLE_CODEC.fieldOf("style").orElse(Style.EMPTY).forGetter(TranslationTextComponent::getStyle)
    ).apply(instance, (text, args, style) -> (TranslationTextComponent)new TranslationTextComponent(text, args.toArray()).withStyle(style)));

    private static final Codec<ScoreTextComponent> SCORE_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(ScoreTextComponent::getName),
            Codec.STRING.fieldOf("objective").forGetter(ScoreTextComponent::getObjective),
            STYLE_CODEC.fieldOf("style").orElse(Style.EMPTY).forGetter(ScoreTextComponent::getStyle)
    ).apply(instance, (name, objective, style) -> (ScoreTextComponent)new ScoreTextComponent(name, objective).withStyle(style)));

    private static final Codec<SelectorTextComponent> SELECTOR_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("selector").forGetter(SelectorTextComponent::getPattern),
            STYLE_CODEC.fieldOf("style").orElse(Style.EMPTY).forGetter(SelectorTextComponent::getStyle)
    ).apply(instance, (selector, style) -> (SelectorTextComponent)new SelectorTextComponent(selector).withStyle(style)));

    private static final Codec<KeybindTextComponent> KEYBIND_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("keybind").forGetter(KeybindTextComponent::getName),
            STYLE_CODEC.fieldOf("style").orElse(Style.EMPTY).forGetter(KeybindTextComponent::getStyle)
    ).apply(instance, (keybind, style) -> (KeybindTextComponent)new KeybindTextComponent(keybind).withStyle(style)));

    private static final Codec<NBTTextComponent.Block> NBT_BLOCK_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("nbt").forGetter(NBTTextComponent.Block::getNbtPath),
            Codec.BOOL.fieldOf("interpret").orElse(false).forGetter(NBTTextComponent.Block::isInterpreting),
            Codec.STRING.fieldOf("block").forGetter(NBTTextComponent.Block::getPos),
            STYLE_CODEC.fieldOf("style").orElse(Style.EMPTY).forGetter(NBTTextComponent.Block::getStyle)
    ).apply(instance, (nbt, interpret, pos, style) -> (NBTTextComponent.Block)new NBTTextComponent.Block(nbt, interpret, pos).withStyle(style)));

    private static final Codec<NBTTextComponent.Entity> NBT_ENTITY_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("nbt").forGetter(NBTTextComponent.Entity::getNbtPath),
            Codec.BOOL.fieldOf("interpret").orElse(false).forGetter(NBTTextComponent.Entity::isInterpreting),
            Codec.STRING.fieldOf("entity").forGetter(NBTTextComponent.Entity::getSelector),
            STYLE_CODEC.fieldOf("style").orElse(Style.EMPTY).forGetter(NBTTextComponent.Entity::getStyle)
    ).apply(instance, (nbt, interpret, entity, style) -> (NBTTextComponent.Entity)new NBTTextComponent.Entity(nbt, interpret, entity).withStyle(style)));

    private static final Codec<NBTTextComponent.Storage> NBT_STORAGE_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("nbt").forGetter(NBTTextComponent.Storage::getNbtPath),
            Codec.BOOL.fieldOf("interpret").orElse(false).forGetter(NBTTextComponent.Storage::isInterpreting),
            ResourceLocation.CODEC.fieldOf("storage").forGetter(NBTTextComponent.Storage::getId),
            STYLE_CODEC.fieldOf("style").orElse(Style.EMPTY).forGetter(NBTTextComponent.Storage::getStyle)
    ).apply(instance, (nbt, interpret, id, style) -> (NBTTextComponent.Storage)new NBTTextComponent.Storage(nbt, interpret, id).withStyle(style)));

    private static DataResult<ITextComponent> decodeComponent(Dynamic<?> dynamic) {
        Optional<String> stringResult = dynamic.asString().result();
        Optional<StringTextComponent> textResult = TEXT_CODEC.parse(dynamic).result();
        Optional<TranslationTextComponent> translationResult = TRANSLATABLE_CODEC.parse(dynamic).result();
        Optional<ScoreTextComponent> scoreResult = SCORE_CODEC.parse(dynamic).result();
        Optional<SelectorTextComponent> selectorResult = SELECTOR_CODEC.parse(dynamic).result();
        Optional<KeybindTextComponent> keybindResult = KEYBIND_CODEC.parse(dynamic).result();
        Optional<NBTTextComponent.Block> blockResult = NBT_BLOCK_CODEC.parse(dynamic).result();
        Optional<NBTTextComponent.Entity> entityResult = NBT_ENTITY_CODEC.parse(dynamic).result();
        Optional<NBTTextComponent.Storage> storageResult = NBT_STORAGE_CODEC.parse(dynamic).result();
        Optional<List<DataResult<ITextComponent>>> listResult = dynamic.asListOpt(TextComponentCodec::decodeComponent).result();

        if (stringResult.isPresent()) return DataResult.success(new StringTextComponent(stringResult.get()));
        else if (textResult.isPresent()) return DataResult.success(textResult.get());
        else if (translationResult.isPresent()) return DataResult.success(translationResult.get());
        else if (scoreResult.isPresent()) return DataResult.success(scoreResult.get());
        else if (selectorResult.isPresent()) return DataResult.success(selectorResult.get());
        else if (keybindResult.isPresent()) return DataResult.success(keybindResult.get());
        else if (blockResult.isPresent()) return DataResult.success(blockResult.get());
        else if (entityResult.isPresent()) return DataResult.success(entityResult.get());
        else if (storageResult.isPresent()) return DataResult.success(storageResult.get());
        else if (listResult.isPresent()) {
            IFormattableTextComponent startComponent = null;
            for (DataResult<ITextComponent> iTextComponentDataResult : listResult.get()) {
                Optional<ITextComponent> result = iTextComponentDataResult.result();
                if (result.isPresent()) {
                    if (startComponent == null) {
                        startComponent = result.get().copy();
                    } else {
                        startComponent.append(result.get());
                    }
                }
            }
            if (startComponent != null) {
                return DataResult.success(startComponent);
            }
        }
        return DataResult.error("Component Invalid");
    }

}
