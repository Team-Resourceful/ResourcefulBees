package com.resourcefulbees.resourcefulbees.init;

import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.api.beedata.MutationData;
import com.resourcefulbees.resourcefulbees.api.beedata.mutation.Mutation;
import com.resourcefulbees.resourcefulbees.api.beedata.mutation.MutationOutput;
import com.resourcefulbees.resourcefulbees.api.beedata.mutation.outputs.BlockOutput;
import com.resourcefulbees.resourcefulbees.api.beedata.mutation.outputs.EntityOutput;
import com.resourcefulbees.resourcefulbees.api.beedata.mutation.outputs.ItemOutput;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.lib.ModConstants;
import com.resourcefulbees.resourcefulbees.lib.MutationTypes;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import com.resourcefulbees.resourcefulbees.utils.RandomCollection;
import com.resourcefulbees.resourcefulbees.utils.validation.SecondPhaseValidator;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.ITag;

import java.util.LinkedList;
import java.util.List;

import static com.resourcefulbees.resourcefulbees.ResourcefulBees.LOGGER;

public class MutationSetup {

    private MutationSetup() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static void setupMutations() {
        BeeRegistry.getRegistry().getBees().values()
                .forEach(MutationSetup::initMutationData);
    }

    private static void initMutationData(CustomBeeData beeData) {
        beeData.getMutationData().initializeMutations();
        if (beeData.getMutationData().hasMutation()) {
            setupMutations(beeData);
        }
    }


    private static void setupMutations(CustomBeeData customBeeData) {
        MutationData mutationData = customBeeData.getMutationData();
        List<Mutation> mutations;
        //TODO remove legacy mutation hooks in 1.17
        mutations = mutationData.getMutations() == null ? new LinkedList<>() : mutationData.getMutations();
        if (hasLegacyMutation(mutationData)) {
            mutations.add(new Mutation(mutationData.getMutationType(), mutationData.getMutationInput(), 1D, new MutationOutput(mutationData.getMutationOutput(), 1, null)));
        }
        mutations.removeIf(mutation -> SecondPhaseValidator.validateMutation(customBeeData.getName(), mutation));
        mutationData.initializeMutations();
        addMutations(customBeeData.getMutationData(), mutations);
    }

    private static boolean hasMutation(CustomBeeData customBeeData) {
        return customBeeData.getMutationData().hasMutation();
    }

    private static boolean hasLegacyMutation(MutationData mutationData) {
        return !mutationData.getMutationInput().isEmpty() && !mutationData.getMutationOutput().isEmpty() && !mutationData.getMutationType().equals(MutationTypes.NONE);
    }

    private static void addMutations(MutationData mutationData, List<Mutation> mutations) {
        mutations.stream()
                .filter(mutation -> !mutation.getType().equals(MutationTypes.NONE))
                .forEach(mutation -> {
                    if (mutation.isTag()) {
                        if (mutation.getType().equals(MutationTypes.BLOCK_TO_ITEM) || mutation.getType().equals(MutationTypes.ITEM)) {
                            addBlockItemTagMutation(mutationData, mutation);
                        } else {
                            addBlockTagMutation(mutationData, mutation);
                        }
                    } else if (mutation.getType().equals(MutationTypes.ENTITY_TO_ENTITY) || mutation.getType().equals(MutationTypes.ENTITY)) {
                        addEntityMutation(mutationData, mutation);
                    } else if (mutation.getType().equals(MutationTypes.BLOCK_TO_ITEM) || mutation.getType().equals(MutationTypes.ITEM)) {
                        addBlockItemMutation(mutationData, mutation);
                    } else {
                        addBlockMutation(mutationData, mutation);
                    }
                });
    }

    private static void addBlockItemTagMutation(MutationData mutationData, Mutation mutation) {
        String tag = mutation.getInputID().replace(BeeConstants.TAG_PREFIX, "");
        ITag<?> input = BeeInfoUtils.getBlockTag(tag);
        if (input == null) {
            input = BeeInfoUtils.getFluidTag(tag);
        }

        RandomCollection<ItemOutput> randomCollection = createRandomItemCollection(mutation);

        if (input != null && !randomCollection.isEmpty() && mutation.getType() != MutationTypes.NONE) {
            input.getValues().forEach(o -> {
                if (o instanceof Block) {
                    mutationData.addItemMutation((Block) o, randomCollection, mutation.getChance());
                    return;
                }

                if (o instanceof Fluid) {
                    Block block = ((Fluid) o).defaultFluidState().createLegacyBlock().getBlock();
                    mutationData.addItemMutation(block, randomCollection, mutation.getChance());
                }
            });
            mutationData.addJeiBlockTagItemMutation(input, randomCollection, mutation.getChance());
        } else {
            logWarning(mutation);
        }
    }

    private static void addBlockItemMutation(MutationData mutationData, Mutation mutation) {
        Block input = BeeInfoUtils.getBlock(mutation.getInputID());
        RandomCollection<ItemOutput> randomCollection = createRandomItemCollection(mutation);
        if (input != Blocks.AIR && !randomCollection.isEmpty() && mutation.getType() != MutationTypes.NONE) {
            mutationData.addItemMutation(input, randomCollection, mutation.getChance());
            mutationData.addJeiItemMutation(input, randomCollection, mutation.getChance());
        } else {
            logWarning(mutation);
        }
    }

    private static void addBlockMutation(MutationData mutationData, Mutation mutation) {
        Block input = BeeInfoUtils.getBlock(mutation.getInputID());
        RandomCollection<BlockOutput> randomCollection = createRandomBlockCollection(mutation);
        if (input != Blocks.AIR && !randomCollection.isEmpty() && mutation.getType() != MutationTypes.NONE) {
            mutationData.addBlockMutation(input, randomCollection, mutation.getChance());
            mutationData.addJeiBlockMutation(input, randomCollection, mutation.getChance());
        } else {
            logWarning(mutation);
        }
    }

    private static void addBlockTagMutation(MutationData mutationData, Mutation mutation) {
        String tag = mutation.getInputID().replace(BeeConstants.TAG_PREFIX, "");
        ITag<?> input = BeeInfoUtils.getBlockTag(tag);
        if (input == null) {
            input = BeeInfoUtils.getFluidTag(tag);
        }

        RandomCollection<BlockOutput> randomCollection = createRandomBlockCollection(mutation);

        if (input != null && !randomCollection.isEmpty() && mutation.getType() != MutationTypes.NONE) {
            input.getValues().forEach(o -> {
                if (o instanceof Block) {
                    mutationData.addBlockMutation((Block) o, randomCollection, mutation.getChance()); //needs testing
                    return;
                }

                if (o instanceof Fluid) {
                    Block block = ((Fluid) o).defaultFluidState().createLegacyBlock().getBlock();
                    mutationData.addBlockMutation(block, randomCollection, mutation.getChance());
                }
            });

            mutationData.addJeiBlockTagMutation(input, randomCollection, mutation.getChance());
        } else {
            logWarning(mutation);
        }
    }

    private static void addEntityMutation(MutationData mutationData, Mutation mutation) {
        EntityType<?> input = BeeInfoUtils.getEntityType(mutation.getInputID().replace(BeeConstants.ENTITY_PREFIX, ""));

        RandomCollection<EntityOutput> randomCollection = new RandomCollection<>();
        mutation.getOutputs().forEach(mutationOutput -> {
            if (mutationOutput.getOutputID() != null) {
                EntityType<?> output = BeeInfoUtils.getEntityType(mutationOutput.getOutputID().replace(BeeConstants.ENTITY_PREFIX, ""));
                if (output != null) {
                    randomCollection.add(mutationOutput.getWeight(), new EntityOutput(output, mutationOutput.getNbt(), mutationOutput.getWeight()));
                }
            }
        });

        if (input != null && !randomCollection.isEmpty() && mutation.getType() != MutationTypes.NONE) {
            mutationData.addEntityMutation(input, randomCollection, mutation.getChance());
        } else {
            logWarning(mutation);
        }
    }

    private static RandomCollection<ItemOutput> createRandomItemCollection(Mutation mutation) {
        RandomCollection<ItemOutput> randomCollection = new RandomCollection<>();
        mutation.getOutputs().forEach(mutationOutput -> {
            Item output = BeeInfoUtils.getItem(mutationOutput.getOutputID());
            CompoundNBT compoundNBT = mutationOutput.getNbt();
            if (!output.equals(Items.AIR)) {
                randomCollection.add(mutationOutput.getWeight(), new ItemOutput(output, compoundNBT, mutationOutput.getWeight()));
            }
        });
        return randomCollection;
    }

    private static RandomCollection<BlockOutput> createRandomBlockCollection(Mutation mutation) {
        RandomCollection<BlockOutput> randomCollection = new RandomCollection<>();
        mutation.getOutputs().forEach(mutationOutput -> {
            Block output = BeeInfoUtils.getBlock(mutationOutput.getOutputID());
            CompoundNBT compoundNBT = mutationOutput.getNbt();
            if (!output.equals(Blocks.AIR)) {
                randomCollection.add(mutationOutput.getWeight(), new BlockOutput(output, compoundNBT, mutationOutput.getWeight()));
            }
        });
        return randomCollection;
    }

    private static void logWarning(Mutation mutation) {
        LOGGER.warn("Could not validate mutation: {}", mutation);
    }
}