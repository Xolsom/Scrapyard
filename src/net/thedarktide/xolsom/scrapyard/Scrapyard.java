package net.thedarktide.xolsom.scrapyard;

import java.io.File;
import java.util.Random;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.util.config.Configuration;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import org.bukkit.Material;

/**
 * The Scrapyard base class
 * @author Xolsom
 */
public class Scrapyard extends JavaPlugin {
    private final ScrapyardYardSaleListener scrapyardYardSaleListener = new ScrapyardYardSaleListener(this);
    public ScrapyardItemDatabase scrapyardItemDatabase = new ScrapyardItemDatabase(this);;
    public static PermissionHandler permissionHandler;
    
    public String logPrefix = "";
    
    public static final Logger log = Logger.getLogger("Minecraft");

    public double cost;
    public ChatColor messageColor, successColor, errorColor, dataColor;

    /**
     * Executed when the plugin's disabled
     */
    public void onDisable() {
        this.logInfo("Successfully disabled.");
    }
    
    /**
     * Executed when the plugin's enabled
     * Loads the database, gets config data, sets the permission up and registers the custom event.
     */
    public void onEnable() {
        PluginDescriptionFile pdf = this.getDescription();
        
        this.logPrefix = "[" + pdf.getName() + "] ";
        
        this.setupPermissions();

        // Loading the database
        this.scrapyardItemDatabase.load(new File(this.getDataFolder(), "itemdb.yml"));
        
        // Get the configurations, may need some improvements
        Configuration config = this.getConfiguration();
        this.cost = config.getDouble("options.cost", 0.25);
        messageColor = getConfigColor(config, "options.messageColor", "Aqua");
        successColor = getConfigColor(config, "options.successColor", "Green");
        errorColor = getConfigColor(config, "options.errorColor", "Dark_Red");
        dataColor = getConfigColor(config, "options.dataColor", "Gold");
        
        // And register the custom event
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.CUSTOM_EVENT, scrapyardYardSaleListener, Event.Priority.Normal, this);
        
        // Temporary command
        this.getCommand("scrapyard").setExecutor(new ScrapyardCommand(this));
        
        this.logInfo("Successfully enabled.");
    }
    
    /**
     * Three log methods which simply prepend the plugin name
     * @param message 
     */
    public void logInfo(String message) {
        Scrapyard.log.info((this.logPrefix + message));
    }
    public void logWarning(String message) {
        Scrapyard.log.warning((this.logPrefix + message));
    }
    public void logSevere(String message) {
        Scrapyard.log.severe((this.logPrefix + message));
    }
    
    /**
     * Gets a color from a configuration and trys to get a real chatcolor out of it, otherwise the default color is being used.
     * @param config
     * @param configPath
     * @param def
     * @return 
     */
    public ChatColor getConfigColor(Configuration config, String configPath, String def) {
        String colorString = config.getString(configPath, def);

        try {
            ChatColor color = ChatColor.valueOf(colorString.toUpperCase());

            return color;
        } catch(Exception e) {
            this.logWarning("Bad color definition in the config file. (" + configPath + ")");
            
            return ChatColor.valueOf(def.toUpperCase());
        }
    }
    
    /**
     * Converts material names into a nicer form.
     * Replaces the underlines with spaces and capitalizes each word.
     * @param materialId
     * @return 
     */
    public String getMaterialName(int materialId) {
        String materialName = "";
        
        for(String string : Material.getMaterial(materialId).name().split("_")) {
            materialName += string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase() + " ";
        }
        
        return materialName.trim();
    }
    
    /**
     * Temporary event call method
     */
    public void callScrapyardYardSaleEvent(Player player) {
        ScrapyardYardSaleEvent scrapyardYardSaleEvent = new ScrapyardYardSaleEvent("YardSaleEvent", player);
        
        this.getServer().getPluginManager().callEvent(scrapyardYardSaleEvent);
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
            player.sendMessage(this.errorColor + "You don't have permission to use this.");

            return false;
        }
        
        int playerItemId = player.getItemInHand().getTypeId();

        // Is item in database?
        if(this.scrapyardItemDatabase.getItem(playerItemId) != null) {
            int materialId = this.scrapyardItemDatabase.getItem(playerItemId).get("material");
            int amount = this.scrapyardItemDatabase.getItem(playerItemId).get("amount");
            int successes = 0;

            Random random = new Random();

            // Items durability is the amount of times used, so it should probably be subtracted from the max durability
            double formula = ((player.getItemInHand().getType().getMaxDurability() - player.getItemInHand().getDurability()) / player.getItemInHand().getType().getMaxDurability()) * (1 - this.cost);

            // Check for successses
            for(int i = 0; i < amount; i++, successes += random.nextDouble() < formula ? 1 : 0);
            
            this.logInfo(player.getName() + " scrapped " + player.getItemInHand().getType().name() + " for " + successes + " " + Material.getMaterial(materialId).name());

            ItemStack itemStack;
            if(successes < 1) {
                player.sendMessage(this.messageColor + "The scrapping of " + this.dataColor + this.getMaterialName(playerItemId) + this.messageColor + " was not successful.");

                // Set the players hand item to null / AIR
                itemStack = null;
            } else {
                player.sendMessage(this.successColor + "You successfully scrapped " + this.dataColor + successes + " " + this.getMaterialName(materialId) + this.successColor + " out of your " + this.dataColor + this.getMaterialName(playerItemId) + this.successColor + ".");

                // Set the players hand to success amount fo valuable blocks
                itemStack = new ItemStack(materialId, successes);
            }

            player.setItemInHand(itemStack);

            return true;
        } else {
            player.sendMessage(this.dataColor + this.getMaterialName(playerItemId) + this.errorColor + " cannot be scrapped.");

            return false;
        }
    }
}
