package com.teamresourceful.resourcefulbees.api.beedata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.beedata.mutation.Mutation;
import com.teamresourceful.resourcefulbees.api.beedata.outputs.BlockOutput;
import com.teamresourceful.resourcefulbees.api.beedata.outputs.EntityOutput;
import com.teamresourceful.resourcefulbees.api.beedata.outputs.ItemOutput;
import com.teamresourceful.resourcefulbees.lib.MutationTypes;
import com.teamresourceful.resourcefulbees.utils.BeeInfoUtils;
import com.teamresourceful.resourcefulbees.utils.RandomCollection;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public class MutationData {
    public static final MutationData DEFAULT = new MutationData(false, 0, Collections.emptyList());

    public static final Codec<MutationData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("hasMutation").orElse(false).forGetter(MutationData::hasMutation),
            Codec.intRange(1, Integer.MAX_VALUE).fieldOf("mutationCount").orElse(10).forGetter(MutationData::getMutationCount),
            Mutation.CODEC.listOf().fieldOf("mutations").orElse(new ArrayList<>()).forGetter(mutationDataCodec -> mutationDataCodec.mutations)
    ).apply(instance, MutationData::new));

    /**
     * If the bee has a mutation
     */
    private final boolean hasMutation;

    /**
     * how many inputs in a radius get mutated
     */
    private final int mutationCount;

    /**
     * List of block mutations
     */
    private final List<Mutation> mutations;

    private final Map<Block, RandomCollection<BlockOutput>> blockMutations = new HashMap<>();
    private final Map<EntityType<?>, RandomCollection<EntityOutput>> entityMutations = new HashMap<>();
    private final Map<Block, RandomCollection<ItemOutput>> itemMutations = new HashMap<>();

    private MutationData(boolean hasMutation, int mutationCount, List<Mutation> mutations) {
        this.mutationCount = mutationCount;
        this.hasMutation = hasMutation;
        this.mutations = mutations;
        setupMutations();
    }

    public int getMutationCount() {
        return mutationCount;
    }

    public boolean hasMutation() {
        return hasMutation;
    }

    public Map<EntityType<?>, RandomCollection<EntityOutput>> getEntityMutations() {
        return Collections.unmodifiableMap(entityMutations);
    }

    public Map<Block, RandomCollection<BlockOutput>> getBlockMutations() {
        return Collections.unmodifiableMap(blockMutations);
    }

    public Map<Block, RandomCollection<ItemOutput>> getItemMutations() {
        return Collections.unmodifiableMap(itemMutations);
    }

    public boolean testMutations() {
        return hasMutation && (!blockMutations.isEmpty() || !entityMutations.isEmpty() || !itemMutations.isEmpty());
    }


    //Setup methods below this line

    private void setupMutations() {
        mutations.stream()
                .filter(mutation -> !mutation.getType().equals(MutationTypes.NONE))
                .forEach(mutation -> {
                    switch (mutation.getType()) {
                        case ITEM:
                            addBlockItemMutation(mutation);
                            break;
                        case ENTITY:
                            addEntityMutation(mutation);
                            break;
                        case BLOCK:
                            addBlockMutation(mutation);
                            break;
                        default:
                            //do nothing
                    }
                });
    }

    private void addBlockItemMutation(Mutation mutation) {
        Set<Block> inputBlocks = mutation.getBlockInputs();
        RandomCollection<ItemOutput> randomCollection = createRandomItemCollection(mutation);
        inputBlocks.stream().filter(block -> !block.equals(Blocks.AIR)).forEach(block -> {
            if (!randomCollection.isEmpty()) {
                itemMutations.computeIfAbsent(block, block1 -> randomCollection);
            }
        });
    }

    private void addBlockMutation(Mutation mutation) {
        Set<Block> inputBlocks = mutation.getBlockInputs();
        RandomCollection<BlockOutput> randomCollection = createRandomBlockCollection(mutation);
        inputBlocks.stream().filter(block -> !block.equals(Blocks.AIR)).forEach(block -> {
            if (!randomCollection.isEmpty()) {
                blockMutations.computeIfAbsent(block, block1 -> randomCollection);
            }
        });
    }

    private void addEntityMutation(Mutation mutation) {
        EntityType<?> input = BeeInfoUtils.getEntityType(mutation.getEntityInput().orElse(null));

        RandomCollection<EntityOutput> randomCollection = new RandomCollection<>();
        mutation.getOutputs().forEach(mutationOutput -> {
            if (mutationOutput.getOutput() != null) {
                EntityType<?> output = BeeInfoUtils.getEntityType(mutationOutput.getOutput());
                if (output != null) {
                    randomCollection.add(mutationOutput.getWeight(), new EntityOutput(output, Optional.of(mutationOutput.getNbt()), mutationOutput.getWeight(), mutation.getChance()));
                }
            }
        });

        if (input != null && !randomCollection.isEmpty()) {
            entityMutations.put(input, randomCollection);
        }
    }

    private RandomCollection<ItemOutput> createRandomItemCollection(Mutation mutation) {
        RandomCollection<ItemOutput> randomCollection = new RandomCollection<>();
        mutation.getOutputs().forEach(mutationOutput -> {
            ItemStack output = new ItemStack(ForgeRegistries.ITEMS.getValue(mutationOutput.getOutput()), mutationOutput.getCount());
            if (!output.equals(ItemStack.EMPTY)) {
                output.setTag(mutationOutput.getNbt());
                randomCollection.add(mutationOutput.getWeight(), new ItemOutput(output, mutationOutput.getWeight(), mutation.getChance()));
            }
        });
        return randomCollection;
    }

    private RandomCollection<BlockOutput> createRandomBlockCollection(Mutation mutation) {
        RandomCollection<BlockOutput> randomCollection = new RandomCollection<>();
        mutation.getOutputs().forEach(mutationOutput -> {
            Block output = ForgeRegistries.BLOCKS.getValue(mutationOutput.getOutput());
            if (output != (Blocks.AIR)) {
                randomCollection.add(mutationOutput.getWeight(), new BlockOutput(output, Optional.of(mutationOutput.getNbt()), mutationOutput.getWeight(), mutation.getChance()));
            }
        });
        return randomCollection;
    }

}

