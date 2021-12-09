package com.teamresourceful.resourcefulbees.api.beedata.mutation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.beedata.outputs.BlockOutput;
import com.teamresourceful.resourcefulbees.api.beedata.outputs.EntityOutput;
import com.teamresourceful.resourcefulbees.api.beedata.outputs.ItemOutput;
import com.teamresourceful.resourcefulbees.common.lib.enums.MutationType;
import com.teamresourceful.resourcefulbees.common.utils.BeeInfoUtils;
import com.teamresourceful.resourcefulbees.common.utils.RandomCollection;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

@Unmodifiable
public class MutationData {
    public static final MutationData DEFAULT = new MutationData(0, Collections.emptyList());

    /**
     * A {@link Codec<MutationData>} that can be parsed to create a
     * {@link MutationData} object.
     */
    public static final Codec<MutationData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.intRange(1, Integer.MAX_VALUE).fieldOf("mutationCount").orElse(10).forGetter(MutationData::getMutationCount),
            Mutation.CODEC.listOf().fieldOf("mutations").orElse(new ArrayList<>()).forGetter(mutationDataCodec -> mutationDataCodec.mutations)
    ).apply(instance, MutationData::new));

    private final boolean hasMutation;
    private final int mutationCount;
    private final List<Mutation> mutations;
    private final Map<Block, RandomCollection<BlockOutput>> blockMutations = new LinkedHashMap<>();
    private final Map<EntityType<?>, RandomCollection<EntityOutput>> entityMutations = new LinkedHashMap<>();
    private final Map<Block, RandomCollection<ItemOutput>> itemMutations = new LinkedHashMap<>();

    private MutationData(int mutationCount, List<Mutation> mutations) {
        this.mutationCount = mutationCount;
        this.mutations = mutations;
        this.hasMutation = hasMutations();
        setupMutations();
    }

    private boolean hasMutations() {
        if (mutations.isEmpty()) return false;
        List<Mutation> test = new LinkedList<>(mutations);
        test.removeIf(mutation -> mutation.getType() == MutationType.ENTITY && !mutation.getEntityInput().isPresent());
        return !test.isEmpty();
    }

    /**
     * @return Returns the number of times this bee can attempt a mutation
     * before needing to enter a hive or apiary
     */
    public int getMutationCount() {
        return mutationCount;
    }

    /**
     * @return Returns <tt>true</tt> if the mutations list is empty.
     */
    public boolean hasMutation() {
        return hasMutation;
    }

    /**
     * @return Returns and unmodifiable map of {@link EntityType} inputs to {@link EntityOutput}s.
     */
    public Map<EntityType<?>, RandomCollection<EntityOutput>> getEntityMutations() {
        return Collections.unmodifiableMap(entityMutations);
    }

    /**
     * @return Returns and unmodifiable map of {@link Block} inputs to {@link BlockOutput}s.
     */
    public Map<Block, RandomCollection<BlockOutput>> getBlockMutations() {
        return Collections.unmodifiableMap(blockMutations);
    }

    /**
     * @return Returns and unmodifiable map of {@link Block} inputs to {@link ItemOutput}s.
     */
    public Map<Block, RandomCollection<ItemOutput>> getItemMutations() {
        return Collections.unmodifiableMap(itemMutations);
    }

    //region Setup
    //TODO Cleanup/Simplify the methods below when adding in Block to Entity mutations
    private void setupMutations() {
        mutations.stream()
                .filter(mutation -> !mutation.getType().equals(MutationType.NONE))
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
        EntityType<?> input = mutation.getEntityInput().orElse(null);

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
    //endregion

}

