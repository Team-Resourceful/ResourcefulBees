package com.resourcefulbees.resourcefulbees.init;

import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.block.TieredBeehiveBlock;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.registry.ModItems;
import com.resourcefulbees.resourcefulbees.utils.color.RainbowColor;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.OptionalDispenseBehavior;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.FolderPack;
import net.minecraft.resources.IPackNameDecorator;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.IForgeShearable;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.loading.FMLPaths;

import javax.annotation.Nonnull;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

import static com.resourcefulbees.resourcefulbees.ResourcefulBees.LOGGER;
import static com.resourcefulbees.resourcefulbees.init.BeeSetup.BEE_PATH;
import static com.resourcefulbees.resourcefulbees.init.BeeSetup.RESOURCE_PATH;

public class ModSetup {

    public static void initialize(){
        setupPaths();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ModSetup::loadResources);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> RainbowColor::init);
    }

    private static void setupPaths(){
        Path configPath = FMLPaths.CONFIGDIR.get();
        Path rbBeesPath = Paths.get(configPath.toAbsolutePath().toString(), ResourcefulBees.MOD_ID, "bees");
        Path rbBiomePath = Paths.get(configPath.toAbsolutePath().toString(), ResourcefulBees.MOD_ID, "biome_dictionary");
        Path rbTraitPath = Paths.get(configPath.toAbsolutePath().toString(), ResourcefulBees.MOD_ID, "bee_traits");
        Path rbAssetsPath = Paths.get(configPath.toAbsolutePath().toString(),ResourcefulBees.MOD_ID, "resources");
        BEE_PATH = rbBeesPath;
        RESOURCE_PATH = rbAssetsPath;
        BiomeDictonarySetup.DICTIONARY_PATH = rbBiomePath;
        TraitSetup.DICTIONARY_PATH = rbTraitPath;

        try { Files.createDirectories(rbBeesPath);
        } catch (FileAlreadyExistsException ignored) {
        } catch (IOException e) { LOGGER.error("failed to create \"bees\" directory");}

        try { Files.createDirectories(rbBiomePath);
        } catch (FileAlreadyExistsException ignored) {
        } catch (IOException e) { LOGGER.error("failed to create \"biome_dictionary\" directory");}

        try { Files.createDirectories(rbTraitPath);
        } catch (FileAlreadyExistsException ignored) {
        } catch (IOException e) { LOGGER.error("failed to create \"bee_traits\" directory");}

        try { Files.createDirectory(rbAssetsPath);
        } catch (FileAlreadyExistsException ignored) {
        } catch (IOException e) { LOGGER.error("Failed to create \"assets\" directory");}

        try {
            FileWriter file = new FileWriter(Paths.get(rbAssetsPath.toAbsolutePath().toString(), "pack.mcmeta").toFile());
            String mcMetaContent = "{\"pack\":{\"pack_format\":6,\"description\":\"Resourceful Bees resource pack used for lang purposes for the user to add lang for bee/items.\"}}";
            file.write(mcMetaContent);
            file.close();
        } catch (FileAlreadyExistsException ignored){
        } catch (IOException e) { LOGGER.error("Failed to create pack.mcmeta file for resource loading");}
    }

    public static void setupDispenserCollectionBehavior() {
        if (Config.ALLOW_SHEARS.get()) {
            DispenserBlock.registerDispenseBehavior(net.minecraft.item.Items.SHEARS.asItem(), new OptionalDispenseBehavior() {
                @Nonnull
                protected ItemStack dispenseStack(@Nonnull IBlockSource source, @Nonnull ItemStack stack) {
                    ServerWorld world = source.getWorld();
                    if (!world.isRemote()) {
                        this.setSuccess(false);
                        BlockPos blockpos = source.getBlockPos().offset(source.getBlockState().get(DispenserBlock.FACING));

                        for (Entity entity : world.getEntitiesInAABBexcluding(null, new AxisAlignedBB(blockpos), e -> !e.isSpectator() && e instanceof IForgeShearable)) {
                            IForgeShearable target = (IForgeShearable) entity;
                            if (target.isShearable(stack, world, blockpos)) {
                                FakePlayer fakie = FakePlayerFactory.getMinecraft(world);
                                List<ItemStack> drops = target.onSheared(fakie, stack, entity.world, blockpos,
                                        net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack));
                                Random rand = new Random();
                                drops.forEach(d -> {
                                    ItemEntity ent = entity.entityDropItem(d, 1.0F);
                                    if (ent != null)
                                        ent.setMotion(ent.getMotion().add((rand.nextFloat() - rand.nextFloat()) * 0.1F, rand.nextFloat() * 0.05F, (rand.nextFloat() - rand.nextFloat()) * 0.1F));
                                });
                                if (stack.attemptDamageItem(1, world.rand, null)) {
                                    stack.setCount(0);
                                }

                                this.setSuccess(true);
                                break;
                            }
                        }

                        if (!this.isSuccess()) {
                            BlockState blockstate = world.getBlockState(blockpos);
                            if (blockstate.getBlock() instanceof TieredBeehiveBlock) {
                                int i = blockstate.get(BeehiveBlock.HONEY_LEVEL);
                                if (i >= 5) {
                                    if (stack.attemptDamageItem(1, world.rand, null)) {
                                        stack.setCount(0);
                                    }

                                    TieredBeehiveBlock.dropResourceHoneycomb((TieredBeehiveBlock) blockstate.getBlock(), world, blockpos, false);
                                    ((BeehiveBlock) blockstate.getBlock()).takeHoney(world, blockstate, blockpos, null, BeehiveTileEntity.State.BEE_RELEASED);
                                    this.setSuccess(true);
                                }
                            } else if (blockstate.isIn(BlockTags.BEEHIVES)) {
                                int i = blockstate.get(BeehiveBlock.HONEY_LEVEL);
                                if (i >= 5) {
                                    if (stack.attemptDamageItem(1, world.rand, null)) {
                                        stack.setCount(0);
                                    }

                                    BeehiveBlock.dropHoneycomb(world, blockpos);
                                    ((BeehiveBlock) blockstate.getBlock()).takeHoney(world, blockstate, blockpos, null, BeehiveTileEntity.State.BEE_RELEASED);
                                    this.setSuccess(true);
                                }
                            }
                        }
                    }
                    return stack;
                }
            });
        }

        DispenserBlock.registerDispenseBehavior(ModItems.SCRAPER.get(), new OptionalDispenseBehavior() {
            @Nonnull
            protected ItemStack dispenseStack(@Nonnull IBlockSource source, @Nonnull ItemStack stack) {
                ServerWorld world = source.getWorld();
                if (!world.isRemote()) {
                    this.setSuccess(false);
                    BlockPos blockpos = source.getBlockPos().offset(source.getBlockState().get(DispenserBlock.FACING));
                    BlockState blockstate = world.getBlockState(blockpos);
                    if (blockstate.getBlock() instanceof TieredBeehiveBlock) {
                        int i = blockstate.get(BeehiveBlock.HONEY_LEVEL);
                        if (i >= 5) {
                            if (stack.attemptDamageItem(1, world.rand, null)) {
                                stack.setCount(0);
                            }

                            if (TieredBeehiveBlock.dropResourceHoneycomb((TieredBeehiveBlock) blockstate.getBlock(), world, blockpos, true)) {
                                ((BeehiveBlock) blockstate.getBlock()).takeHoney(world, blockstate, blockpos, null, BeehiveTileEntity.State.BEE_RELEASED);
                            }
                            this.setSuccess(true);
                        }
                    }
                }

                return stack;
            }
        });
    }

    public static void loadResources() {
        Minecraft.getInstance().getResourcePackList().addPackFinder((p_230230_1_, factory) -> {
            final ResourcePackInfo packInfo = ResourcePackInfo.createResourcePack(
                    ResourcefulBees.MOD_ID,
                    true,
                    () -> new FolderPack(RESOURCE_PATH.toFile()),
                    factory,
                    ResourcePackInfo.Priority.TOP,
                    IPackNameDecorator.method_29485()
            );
            if (packInfo == null) {
                LOGGER.error("Failed to load resource pack, some things may not work.");
                return;
            }
            p_230230_1_.accept(packInfo);
        });
    }
}
