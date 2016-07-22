
package com.sergenttech.plugins.threesquaremeals;

import java.util.logging.Level;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Ben Sergent V <bsergentv@gmail.com>
 */
public class ThreeSquareMeals extends JavaPlugin {
    
    private final String version = "0.0.1";
    private String prefix = ChatColor.WHITE+"["+ChatColor.GOLD+"Nut"+ChatColor.WHITE+"]";

    @Override
    public void onEnable() {
        getLogger().log(Level.INFO, "ThreeSquareMeals v{0} enabled.", version);
        getConfig().options().copyDefaults(true);
        saveConfig();
        
        if (getConfig().getString("prefix") != null) {
            prefix = getConfig().getString("prefix").replaceAll("&", "§");
        }
        
        getServer().getPluginManager().registerEvents(new MealListener(), this);
    }

    @Override
    public void onDisable() {
        getLogger().log(Level.INFO, "ThreeSquareMeals v{0} disabled.", version);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("threesquaremeals")) {
            if (args.length == 0) {
                // TODO Show nutritional information
            } else if (args.length >= 1 && args[0].equalsIgnoreCase("version")) {
                sender.sendMessage(new String[] {ChatColor.GOLD+"ThreeSquareMeals v"+version});
            }
        }
        
        return true;
    }

    public String getPrefix() {
        return prefix;
    }

    private final class MealListener implements Listener {
        
        /*@org.bukkit.event.EventHandler(priority = org.bukkit.event.EventPriority.NORMAL)
        public void onPlayerCraft(org.bukkit.event.inventory.PrepareItemCraftEvent e) {
            if (!e.isRepair()) {
                ItemStack[] matrix = e.getInventory().getMatrix();
                boolean foundBowl = false;
                for (ItemStack is : matrix) {
                    if (is.getType() == Material.BOWL) {
                        e.getViewers().get(0).sendMessage("Found a bowl!");
                    }
                }
                if (!foundBowl)
                    return;
                e.getInventory().setResult(new ItemStack(Material.SPONGE));
            }
        }*/
        
        @org.bukkit.event.EventHandler(priority = org.bukkit.event.EventPriority.NORMAL)
        public void onPlayerInteract(org.bukkit.event.player.PlayerInteractEvent e) {
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (e.getClickedBlock().getType() == Material.WOOD_PLATE || e.getClickedBlock().getType() == Material.STONE_PLATE) {
                    Block clkdBlk = e.getClickedBlock();
                    Material typeBelow = clkdBlk.getWorld().getBlockAt(clkdBlk.getLocation().clone().add(0, -1, 0)).getType();
                    if (typeBelow == Material.FURNACE || typeBelow == Material.BURNING_FURNACE) {
                        new CookingSurface(ThreeSquareMeals.this).open(e.getPlayer());
                    }
                }
            }
        }
        
    }

}
