package net.thedarktide.xolsom.scrapyard;

import java.util.Map;
import java.io.File;

import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

/**
 * This is the item database simplyfing the methods to get allowed items from the yaml file.
 * @author Xolsom
 */
public class ScrapyardItemDatabase extends Configuration {
    private Scrapyard plugin;
    
    public ScrapyardItemDatabase(File file, Scrapyard plugin) {
        super(file);
        
        this.plugin = plugin;
    }

    /**
     * Returns an item or more respectively the id and the amount of the valuable material
     * @param itemId
     * @return Integer array (0: MaterialId, 1: Amount) or null (the item doesn't exist in the database or the definition is corrupt.)
     */
    public int[] getItem(int itemId) {
        Map<Integer, Object> items;
        Map<String, Integer> item;
        int[] material = new int[2];
        
        try {
            // Some funny workaround for the case of numeric keys being bugged
            items = (Map<Integer, Object>)this.getProperty("items");
            
            item = (Map<String, Integer>)items.get(itemId);
            
            material[0] = item.get("material");
            material[1] = item.get("amount");
        } catch(NullPointerException e) {
            return null;
        }
        
        return material;
    }
    
/* Requires string keys and is not very effective because of that. Workaround: Numeric keys in quotes -> "12456".
    public int[] getItem(int itemId) {
        ConfigurationNode item;
        int[] material = new int[2];
        
        try {
            item = this.getNode("items." + itemId);
            
            material[0] = item.getInt("material", 0);
            material[1] = item.getInt("amount", 0);
        } catch(NullPointerException e) {
            return null;
        }
        
        return material;
    }
*/
}
