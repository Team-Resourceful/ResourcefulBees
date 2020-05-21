package com.dungeonderps.resourcefulbees.item;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.lib.BeeConst;
import com.dungeonderps.resourcefulbees.registry.ItemGroupResourcefulBees;
import com.dungeonderps.resourcefulbees.utils.Color;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.CustomBeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.List;

public class BeeJar extends Item {
    public BeeJar() {
        super(new Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES).maxStackSize(1));
        this.addPropertyOverride(new ResourceLocation("filled"), (stack, world, entity) -> {
            if(isFilled(stack)) {
                return 1.0F;
            }
            return 0.0F;
        });
    }

    public boolean isFilled(ItemStack stack) {
        return !stack.isEmpty() && stack.hasTag() && stack.getTag().contains(BeeConst.NBT_ENTITY);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        World playerWorld = context.getPlayer().getEntityWorld();
        ItemStack stack = context.getItem();
        if (playerWorld.isRemote() || !isFilled(stack)) return ActionResultType.FAIL;
        World worldIn = context.getWorld();
        BlockPos pos = context.getPos();
        Entity entity = getEntityFromStack(stack, worldIn, true);
        BlockPos blockPos = pos.offset(context.getFace());
        entity.setPositionAndRotation(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5, 0, 0);
        worldIn.addEntity(entity);
        stack.setTag(new CompoundNBT());
        return ActionResultType.SUCCESS;
    }

    @Nullable
    public Entity getEntityFromStack(ItemStack stack, World world, boolean withInfo) {
        EntityType type = EntityType.byKey(stack.getTag().getString(BeeConst.NBT_ENTITY)).orElse(null);
        if (type != null) {
            Entity entity = type.create(world);
            if (withInfo) entity.read(stack.getTag());
            return entity;
        }
        return null;
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, PlayerEntity player, LivingEntity targetIn, Hand hand) {
        if (targetIn.getEntityWorld().isRemote() || (!(targetIn instanceof BeeEntity) || !targetIn.isAlive()) || (isFilled(stack))) {
            return false;
        }

        BeeEntity target = (BeeEntity)targetIn;
        String type = EntityType.getKey(target.getType()).toString();
        CompoundNBT nbt = new CompoundNBT();
        nbt.putString(BeeConst.NBT_ENTITY, type);
        target.writeWithoutTypeId(nbt);
        if (target instanceof CustomBeeEntity){
            CustomBeeEntity beeEntity = (CustomBeeEntity) target;
            nbt.putString(BeeConst.NBT_COLOR, beeEntity.getBeeColor());
        }
        stack.setTag(nbt);
        player.swingArm(hand);
        player.setHeldItem(hand, stack);
        target.remove(true);
        return true;
    }


    public static int getColor(ItemStack stack, int tintIndex){
        if (tintIndex == 1 && stack.hasTag() && stack.getTag().contains(BeeConst.NBT_ENTITY)){
           CompoundNBT tag = stack.getTag();
           if (tag.contains(BeeConst.NBT_COLOR))
               return Color.parseInt(tag.getString(BeeConst.NBT_COLOR));
        }
        return 0xffffff;
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        String name;
        if(isFilled(stack)){
            name = I18n.format("item." + ResourcefulBees.MOD_ID + '.' + "bee_jar_filled");
        }
        else
            name = I18n.format("item." + ResourcefulBees.MOD_ID + '.' + "bee_jar_empty");
        return name;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);
        if (isFilled(stack)){
            if (stack.getTag().getString(BeeConst.NBT_ENTITY).equals("resourcefulbees:bee")){
                String type = stack.getTag().getString(BeeConst.NBT_BEE_TYPE);
                tooltip.add(new StringTextComponent(I18n.format(ResourcefulBees.MOD_ID + ".information.bee_type.custom") + StringUtils.capitalize(type)).applyTextStyle(TextFormatting.WHITE));
            }
            else if (stack.getTag().getString(BeeConst.NBT_ENTITY).equals("minecraft:bee")){
                tooltip.add(new TranslationTextComponent(ResourcefulBees.MOD_ID + ".information.bee_type.vanilla").applyTextStyle(TextFormatting.WHITE));
            }
            else
                tooltip.add(new TranslationTextComponent(ResourcefulBees.MOD_ID + ".information.bee_type.unknown"));
        }
    }
}
