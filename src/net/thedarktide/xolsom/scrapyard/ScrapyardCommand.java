package net.thedarktide.xolsom.scrapyard;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Xolsom
 */
public class ScrapyardCommand implements CommandExecutor {
    private final Scrapyard plugin;
    
    public ScrapyardCommand(Scrapyard plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) return false;

        plugin.callScrapyardYardSaleEvent((Player)sender);
        
        return true;
    }
}
