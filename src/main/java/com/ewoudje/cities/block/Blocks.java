package com.ewoudje.cities.block;

import com.ewoudje.cities.CityBlock;
import com.ewoudje.cities.CityWorld;
import org.bukkit.block.Block;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class Blocks {

    private static Map<Class<? extends BlockType>, BlockType> blocksByClass = new HashMap<>();
    private static HashMap<String, BlockType> blocksByName = new HashMap<>();

    public static void register(HashMap<String, BlockType> blocks) {
        blocksByName = blocks;

        Consumer<BlockType> r = (i) -> {
            blocksByName.put(i.getName(), i);
            blocksByClass.put(i.getClass(), i);
        };

        //REGISTER
        r.accept(new FoundingBlock());
        //END REGISTER

    }

    public static BlockType getBlockType(String s) {
        return blocksByName.get(s);
    }

    public static CityBlock createBlock(CityWorld world, Block block, Class<? extends FoundingBlock> clazz) {
        BlockType type = blocksByClass.get(clazz);
        if (type == null) return new CityBlock(world, block, null, null, UUID.randomUUID());
        return new CityBlock(world, block, type, type.createBlockData(), UUID.randomUUID());
    }

    public static Collection<BlockType> getTypes() {
        return blocksByClass.values();
    }
}
