package com.ewoudje.townskings.item;

import com.ewoudje.townskings.api.TKPlugin;
import com.ewoudje.townskings.api.wrappers.TKItem;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.*;

public class RecipesHandler {

    private final TKPlugin plugin;
    private final HashMap<NamespacedKey, Recipe> list = new HashMap<>();

    public RecipesHandler(TKPlugin plugin) {
        this.plugin = plugin;
        try {
            load();
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to load all recipes");
            e.printStackTrace();
        }
    }

    public void removeRecipes() {
        for (NamespacedKey recipe : list.keySet())
            plugin.getServer().removeRecipe(recipe);
    }

    private void load() {
        list.clear();

        File folder = new File(plugin.getDataFolder(), "recipes");
        if (folder.exists()) {
            for (File i : folder.listFiles()) {
                JsonReader reader = null;
                try {
                    reader = new JsonReader(new FileReader(i));
                    load(new JsonParser().parse(reader).getAsJsonObject(), i.getName()
                            .replaceFirst("[.][^.]+$", ""));

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (reader != null) {
                            reader.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else  {
            folder.mkdirs();
            try {
                FileSystem fs = FileSystems.newFileSystem(
                        plugin.getClass().getResource("/recipes").toURI(),
                        Collections.emptyMap());

                Files.walk(fs.getPath("/recipes/")).forEach((p) -> {
                    if (p.getFileName().toString().equals("recipes")) return;

                    try {
                        Files.copy(p, new File(folder, p.getFileName().toString()).toPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                fs.close();
            } catch (URISyntaxException | IOException e) {
                e.printStackTrace();
            }
            load();
        }
    }

    private void load(JsonObject json, String name) {
        if (list.get(name) == null) {

            NamespacedKey recipeKey = new NamespacedKey(plugin, name);
            Recipe recipe;

            if ("minecraft:crafting_shaped".equals(json.get("type").getAsString())) {
                JsonObject result = json.get("result").getAsJsonObject();
                recipe = new ShapedRecipe(recipeKey, jsonItem(result).getItem());
                ((ShapedRecipe) recipe).shape(
                        json.get("pattern").getAsJsonArray().get(0).getAsString(),
                        json.get("pattern").getAsJsonArray().get(1).getAsString(),
                        json.get("pattern").getAsJsonArray().get(2).getAsString()
                );
                for (Map.Entry<String, JsonElement> el : json.get("key").getAsJsonObject().entrySet()) {
                    JsonObject obj = el.getValue().getAsJsonObject();
                    if (obj.has("tag")) {
                        NamespacedKey key = getKey(obj.get("tag").getAsString());
                        ((ShapedRecipe) recipe).setIngredient(el.getKey().charAt(0),
                                new RecipeChoice.MaterialChoice(Bukkit.getTag("items", key, Material.class)));
                    } else if (obj.has("item")) {
                        NamespacedKey key = getKey(obj.get("item").getAsString());
                        Material mat = Material.getMaterial(key.getKey().toUpperCase(Locale.ROOT));
                        if (mat == null) {
                            plugin.getLogger().severe("INVALID RECIPE: " + name + " " + key.toString() +  " material does not exist...");
                            return;
                        }
                        ((ShapedRecipe) recipe).setIngredient(el.getKey().charAt(0), mat);
                    }

                }
            } else {
                return; //TODO shapeless etc
            }

            list.put(recipeKey, recipe);
            plugin.getServer().addRecipe(recipe);
        }
    }

    private TKItem jsonItem(JsonObject object) {
        int amount = object.has("amount") ? object.get("amount").getAsInt() : 1;

        return Items.createItem(null, getKey(object.get("item").getAsString()), amount);
    }

    private NamespacedKey getKey(String key) {
        String[] id = key.split(":");
        return new NamespacedKey(id[0], id[1]);
    }

}
