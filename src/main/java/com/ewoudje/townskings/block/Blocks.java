package com.ewoudje.townskings.block;

import com.ewoudje.townskings.api.TKPlugin;
import com.ewoudje.townskings.api.block.BlockType;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Blocks {

    private static Map<Class<? extends BlockType>, BlockType> blocksByClass = new HashMap<>();
    private static HashMap<String, BlockType> blocksByName = new HashMap<>();

    public static void register(TKPlugin plugin, HashMap<String, BlockType> blocks) {
        blocksByName = blocks;

        Consumer<BlockType> r = (i) -> {
            blocksByName.put(i.getName(), i);
            blocksByClass.put(i.getClass(), i);
        };

        //REGISTER
        r.accept(new FoundingBlock(plugin));
        r.accept(new OutpostBlock(plugin));
        //END REGISTER

    }

    public static BlockType getBlockType(String s) {
        return blocksByName.get(s);
    }

    public static BlockType getBlockType(Class<? extends BlockType> c) {
        return blocksByClass.get(c);
    }

    public static Collection<BlockType> getTypes() {
        return blocksByClass.values();
    }
}
