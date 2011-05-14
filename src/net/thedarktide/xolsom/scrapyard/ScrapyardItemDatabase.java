package net.thedarktide.xolsom.scrapyard;

import java.util.HashMap;
import java.util.Map;
import java.io.File;

import org.bukkit.Material;
import org.bukkit.util.config.Configuration;

/**
 * This is the item database simplyfing the methods to get allowed items from the yaml file.
 * @author Xolsom
 */
public class ScrapyardItemDatabase {
    private final Scrapyard plugin;

    public Map<Integer, Map<String, Integer>> items = new HashMap<Integer, Map<String, Integer>>();

    public ScrapyardItemDatabase(Scrapyard plugin) {
        this.plugin = plugin;
    }

    /**
     * Loads a database file and writes its content into a nested map
     * @param file
     */
    public void load(File file) {
        Map<Object, Object> itemStrings = null;
        Map<String, Object> item = null;
        Material checkMaterial = null;

        Configuration database = new Configuration(file);
        database.load();

        // Load should reset the database
        this.items.clear();

        // KEy is an Object too, because the datatypes get assigned on runtime
        itemStrings = (Map<Object, Object>)database.getProperty("items");
        if(itemStrings == null) return;

        for(Object key : itemStrings.keySet()) {
            String realKey;

            // Check for the right instance
            if(key instanceof Integer) {
                realKey = Integer.toString((Integer)key);
            } else if(key instanceof String) {
                realKey = (String)key;
            } else {
                this.plugin.logWarning("The item database contains an invalid key.");
                continue;
            }

            // Match material automatically converts to uppercase and replaces spaces with underlines to make it easier
            // It aslo checks for the numeric id first
            checkMaterial = Material.matchMaterial(realKey);
            if(checkMaterial == null) {
                this.plugin.logWarning("The item database contains an invalid key. (" + realKey + ")");

                continue;
            }

            int itemId = checkMaterial.getId();
            if(this.items != null && this.items.containsKey(itemId)) {
                this.plugin.logWarning("The item database contains multiple entrys of an item. Only the first one will be considered. (" + checkMaterial.name() + ")");

                continue;
            }

            // Getting the item with an Object to allow strings and ids for the materials
            item = (Map<String, Object>)itemStrings.get(key);
            try {
                Map<String, Integer> material = new HashMap<String, Integer>();

                String materialKey;
                if(item.get("material") instanceof Integer) {
                    materialKey = Integer.toString((Integer)item.get("material"));
                } else {
                    materialKey = (String)item.get("material");
                }

                material.put("material", Material.matchMaterial(materialKey).getId());
                material.put("amount", (Integer)item.get("amount"));

                this.items.put(itemId, material);
            } catch(Exception e) {
                this.plugin.logWarning("The item database contains an entry with invalid values. (" + checkMaterial.name() + ")");
            }
        }
    }

    /**
     * Returns an item from the database or simply null if it's not available
     * @param itemId
     * @return
     */
    public Map<String, Integer> getItem(int itemId) {
        try {
            return this.items.get(itemId);
        } catch(Exception e) {
            return null;
        }
    }
}
