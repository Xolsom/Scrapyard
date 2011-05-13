package net.thedarktide.xolsom.scrapyard;

import java.io.File;
import java.util.Random;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.config.Configuration;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

/**
 * The Scrapyard base class
 * @author Xolsom
 */
public class Scrapyard extends JavaPlugin {
    private final ScrapyardYardSaleListener scrapyardYardSaleListener = new ScrapyardYardSaleListener(this);
    private ScrapyardItemDatabase scrapyardItemDatabase;
    public static PermissionHandler permissionHandler;
    
    public Logger log = Logger.getLogger("Minecraft");

    public double Cost;
    
    public void onDisable() {}
    
    public void onEnable() {
        setupPermissions();
        
        // Loading the database
        scrapyardItemDatabase = new ScrapyardItemDatabase(new File(this.getDataFolder(), "itemdb.yml"), this);
        scrapyardItemDatabase.load();
        
        // Get the configurations, may need some improvements
        Configuration config = this.getConfiguration();
        this.Cost = config.getDouble("options.cost", 0.25);
        
        // And register the custom event
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.CUSTOM_EVENT, scrapyardYardSaleListener, Event.Priority.Normal, this);
        
        // Temporary command
        getCommand("scrapyard").setExecutor(new ScrapyardCommand(this));
    }
    
    /**
     * Temporary event call method
     */
    public void callScrapyardYardSaleEvent(Player player) {
        ScrapyardYardSaleEvent scrapyardYardSaleEvent = new ScrapyardYardSaleEvent("YardSaleEvent", player);
        
        this.getServer().getPluginManager().callEvent(scrapyardYardSaleEvent);
    }
    
    /**
     * Returns the item database
     * @return 
     */
    public ScrapyardItemDatabase getScrapyardItemDatabase() {
        return scrapyardItemDatabase;
    }
    
    /**
     * Sets the permissions up, may need some improvement and error handling and and and
     */
    public void setupPermissions() {
        Plugin permissionsPlugin = this.getServer().getPluginManager().getPlugin("Permissions");
        
        if(Scrapyard.permissionHandler == null) {
            if(permissionsPlugin != null) {
                Scrapyard.permissionHandler = ((Permissions)permissionsPlugin).getHandler();
            } else {
                // Blablabla
            }
        }
    }
    
    /**
     * A simple permission helper
     * @param player
     * @param permissionNode
     * @return 
     */
    public boolean hasPermission(Player player, String permissionNode) {
        return Scrapyard.permissionHandler.has(player, permissionNode);
    }

    /**
     * The actual and most important method.
     * It scraps the item currently held by the player if it is specified in the database.
     * @param player
     * @return 
     */
    public boolean startScrap(Player player) {
        // Check for permission
        if(!this.hasPermission(player, "scrapyard.use")) {
            player.sendMessage("You don't have permission.");
            return false;
        }
        
        // [0] = MaterialId, [1] = Amount
        int[] item = this.getScrapyardItemDatabase().getItem(player.getItemInHand().getTypeId());

        // Is item in list?
        if(item != null) {
            int successes = 0;

            Random random = new Random();

            // Items durability is the amount of times used, so it should probably be subtracted from the max durability
            double formula = ((player.getItemInHand().getType().getMaxDurability() - player.getItemInHand().getDurability()) / player.getItemInHand().getType().getMaxDurability()) * (1 - this.Cost);

            // Check for successses
            for(int i = 0; i < item[1]; i++, successes += random.nextDouble() < formula ? 1 : 0);

            ItemStack itemStack;
            if(successes < 1) {
                player.sendMessage("The scrapping was not successful.");

                // Set the players hand item to null / AIR
                itemStack = null;
            } else {
                // Get valuable material type

                player.sendMessage("You scrapped " + successes + " materials out of your item.");

                itemStack = new ItemStack(item[0], successes);
            }

            player.setItemInHand(itemStack);

            return true;
        } else {
            player.sendMessage("This item cannnot be scrapped.");
            return false;
        }
    }
}
