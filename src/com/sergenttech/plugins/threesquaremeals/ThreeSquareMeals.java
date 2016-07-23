
package com.sergenttech.plugins.threesquaremeals;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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
    
    private org.bukkit.configuration.file.FileConfiguration nutConfig;
    private java.io.File playerNutFile;
    private org.bukkit.configuration.file.FileConfiguration languageConfig;
    private java.io.File languageFile;

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(true);
        saveConfig();
        
        if (getConfig().getString("prefix") != null) {
            prefix = getConfig().getString("prefix").replaceAll("&", "§");
        }
        
        if (playerNutFile == null) {
            playerNutFile = new java.io.File(getDataFolder(), "playerNut.yml");
        }
        nutConfig = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(playerNutFile);
        
        if (languageFile == null) {
            languageFile = new java.io.File(getDataFolder(), "language.yml");
            if (!languageFile.exists()) {
                try {
                    InputStream in = getResource("language.yml");
                    OutputStream out = new FileOutputStream(languageFile);
                    byte[] buf = new byte[1024];
                    int len;
                    while((len=in.read(buf))>0){
                        out.write(buf,0,len);
                    }
                    out.close();
                    in.close();
                } catch (IOException ex) {
                    if (getConfig().getBoolean("verbose_errors", false)) {
                        getLogger().log(Level.WARNING, "Could not create a default languages.yml file.");
                    }
                }
            }
            
        }
        languageConfig = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(languageFile);
        
//        try {
//            org.mcstats.MetricsLite metrics = new org.mcstats.MetricsLite(this);
//            metrics.start();
//        } catch (IOException e) {
//            // Failed to submit the stats :-(
//        }
        
        getServer().getPluginManager().registerEvents(new MealListener(), this);
        
        getLogger().log(Level.INFO, "ThreeSquareMeals v{0} enabled.", version);
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
                sender.sendMessage(ChatColor.GOLD+""+ChatColor.BOLD+"ThreeSquareMeals - Current Nutrition");
                if (sender instanceof Player) {
                    for (int i = 0; i < 5; i++) {
                        sender.sendMessage(getNutritionMeter((Player) sender, i));
                    }
                    sender.sendMessage("         "+getMaxHealth((Player) sender)+"    Max Health");
                } else {
                    sender.sendMessage("    Nutritional details are shown here for players.");
                }
                sender.sendMessage(ChatColor.WHITE+"Visit "+ChatColor.GOLD+"http://bit.ly/1Cijgbl"+ChatColor.WHITE+" for mechanics, recipes, and more.");
            } else if (args.length >= 1 && args[0].equalsIgnoreCase("version")) {
                sender.sendMessage(new String[] {ChatColor.GOLD+"ThreeSquareMeals v"+version});
            }
        }
        
        return true;
    }

    public String getPrefix() {
        return prefix;
    }
    
    private String getNutritionMeter(Player ply, int id) {
        StringBuilder sb = new StringBuilder();
        sb.append("    [");
        int level = nutConfig.getInt(ply.getName()+".nut."+id, 20);
        if (level > 9) {
            sb.append(ChatColor.GREEN);
        } else if (level > 4) {
            sb.append(ChatColor.YELLOW);
        } else {
            sb.append(ChatColor.RED);
        }
        for (int i = 0; i < level; i++) {
            sb.append('|');
        }
        sb.append(ChatColor.DARK_GRAY);
        for (int j = level; j < 20; j++) {
            sb.append(".");
        }
        sb.append(ChatColor.WHITE).append("] ").append(Nutrition.names[id]);
        return sb.toString();
    }
    
    private int getMaxHealth(Player ply) {
        int max = 0;
        for (int i = 0; i < 5; i++) {
            max += nutConfig.getInt(ply.getName()+".nut."+i, 20);
        }
        max = Math.round(max / 6.666666f);
        max += 5;
        return max;
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
