package net.thedarktide.xolsom.scrapyard;

import java.util.Map;
import java.io.File;

import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

/**
 * @author Xolsom
 */
public class ScrapyardItemDatabase extends Configuration {
    private Scrapyard plugin;
    
    public ScrapyardItemDatabase(File file, Scrapyard plugin) {
        super(file);
        
        this.plugin = plugin;
    }

    public int[] getItem(int itemId) {
        Map<Integer, Object> items;
        Map<String, Integer> item;
        int[] material = new int[2];
        
        try {
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
