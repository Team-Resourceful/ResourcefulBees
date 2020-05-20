package com.dungeonderps.resourcefulbees.init;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.block.IronBeehiveBlock;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.OptionalDispenseBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.resources.FolderPack;
import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static com.dungeonderps.resourcefulbees.ResourcefulBees.LOGGER;
import static com.dungeonderps.resourcefulbees.config.BeeBuilder.BEE_PATH;
import static com.dungeonderps.resourcefulbees.config.BeeBuilder.RESOURCE_PATH;

public class ModSetup {

    public static void initialize(){
        setupPaths();
    }

    private static void setupPaths(){
        try {
            FileUtils.deleteDirectory(Paths.get(FMLPaths.CONFIGDIR.get().toString(), "resourcefulbees", "resources", "datapack", "data", "resourcefulbees","recipes").toFile());
        } catch (IOException e) {
            LOGGER.error("Failed to delete recipe directory.");
        }

        Path configPath = FMLPaths.CONFIGDIR.get();
        //Path rbConfigPath = Paths.get(configPath.toAbsolutePath().toString(), "resourcefulbees");
        // subfolder for bees
        Path rbBeesPath = Paths.get(configPath.toAbsolutePath().toString(), ResourcefulBees.MOD_ID, "bees");
        Path rbAssetsPath = Paths.get(configPath.toAbsolutePath().toString(),ResourcefulBees.MOD_ID, "resources");
        BEE_PATH = rbBeesPath;
        RESOURCE_PATH = rbAssetsPath;

        try { Files.createDirectories(rbBeesPath);
        } catch (FileAlreadyExistsException e) { // do nothing
        } catch (IOException e) { LOGGER.error("failed to create resourcefulbees config directory");}

        try { Files.createDirectory(rbAssetsPath);
        } catch (FileAlreadyExistsException e) { // do nothing
        } catch (IOException e) { LOGGER.error("Failed to create assets directory");}
    }

    public static void setupDispenserCollectionBehavior() {
        DispenserBlock.registerDispenseBehavior(Items.SHEARS.asItem(), new OptionalDispenseBehavior() {
            protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
                World world = source.getWorld();
                if (!world.isRemote()) {
                    this.successful = false;
                    BlockPos blockpos = source.getBlockPos().offset(source.getBlockState().get(DispenserBlock.FACING));

                    for(net.minecraft.entity.Entity entity : world.getEntitiesInAABBexcluding(null, new AxisAlignedBB(blockpos), e -> !e.isSpectator() && e instanceof net.minecraftforge.common.IShearable)) {
                        net.minecraftforge.common.IShearable target = (net.minecraftforge.common.IShearable)entity;
                        if (target.isShearable(stack, world, blockpos)) {
                            java.util.List<ItemStack> drops = target.onSheared(stack, entity.world, blockpos,
                                    net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel(net.minecraft.enchantment.Enchantments.FORTUNE, stack));
                            java.util.Random rand = new java.util.Random();
                            drops.forEach(d -> {
                                net.minecraft.entity.item.ItemEntity ent = entity.entityDropItem(d, 1.0F);
                                ent.setMotion(ent.getMotion().add((rand.nextFloat() - rand.nextFloat()) * 0.1F, rand.nextFloat() * 0.05F, (rand.nextFloat() - rand.nextFloat()) * 0.1F));
                            });
                            if (stack.attemptDamageItem(1, world.rand, null)) {
                                stack.setCount(0);
                            }

                            this.successful = true;
                            break;
                        }
                    }

                    if (!this.successful) {
                        BlockState blockstate = world.getBlockState(blockpos);
                        if (blockstate.isIn(BlockTags.BEEHIVES)) {
                            int i = blockstate.get(BeehiveBlock.HONEY_LEVEL);
                            if (i >= 5) {
                                if (stack.attemptDamageItem(1, world.rand, null)) {
                                    stack.setCount(0);
                                }

                                BeehiveBlock.dropHoneyComb(world, blockpos);
                                ((BeehiveBlock)blockstate.getBlock()).takeHoney(world, blockstate, blockpos, null, BeehiveTileEntity.State.BEE_RELEASED);
                                this.successful = true;
                            }
                        }
                        else if (blockstate.getBlock() instanceof IronBeehiveBlock) {
                            int i = blockstate.get(BeehiveBlock.HONEY_LEVEL);
                            if (i >= 5) {
                                if (stack.attemptDamageItem(1, world.rand, null)) {
                                    stack.setCount(0);
                                }

                                IronBeehiveBlock.dropResourceHoneycomb((IronBeehiveBlock) blockstate.getBlock(), world, blockpos);
                                ((BeehiveBlock) blockstate.getBlock()).takeHoney(world, blockstate, blockpos, null,
                                        BeehiveTileEntity.State.BEE_RELEASED);
                                this.successful = true;
                            }
                        }
                    }
                }
                return stack;
            }
        });
    }

    public static void loadResources() {
        Minecraft.getInstance().getResourcePackList().addPackFinder(new IPackFinder() {
            @Override
            public <T extends ResourcePackInfo> void addPackInfosToMap(Map<String, T> map, ResourcePackInfo.IFactory<T> factory) {
                final T packInfo = ResourcePackInfo.createResourcePack(ResourcefulBees.MOD_ID, true, () -> new FolderPack(RESOURCE_PATH.toFile()), factory, ResourcePackInfo.Priority.TOP);
                if (packInfo == null) {
                    LOGGER.error("Failed to load resource pack, some things may not work.");
                    return;
                }
                map.put(ResourcefulBees.MOD_ID, packInfo);
            }
        });
    }
}
