
package com.sergenttech.plugins.threesquaremeals;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Ben Sergent V <bsergentv@gmail.com>
 */
public class ThreeSquareMeals extends JavaPlugin {
    
    private final String version = "0.1.0";
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
                    languageFile.createNewFile();
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
        
        try {
            org.mcstats.MetricsLite metrics = new org.mcstats.MetricsLite(this);
            metrics.start();
        } catch (IOException e) {
        }
        
        getServer().getPluginManager().registerEvents(new MealListener(), this);
        getServer().getScheduler().runTaskTimer(this, new NutritionTimer(), 20, getConfig().getInt("nutritionDecayTicks", 8000));
        
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
                sender.sendMessage(ChatColor.GOLD+""+ChatColor.BOLD+"ThreeSquareMeals - Current Nutrition");
                if (sender instanceof Player) {
                    Player ply = (Player) sender;
                    updateHealth(ply);
                    for (int i = 0; i < Nutrition.NUMOFNUTS; i++) {
                        ply.sendMessage(getNutritionMeter(ply, i));
                    }
                    ply.sendMessage("    "+(getMaxHealth(ply) / 2.0f)+" hrts  Max Health");
                    ply.sendMessage("Hunger: "+ply.getFoodLevel()+"/20    "+"Saturation: "+ply.getSaturation()+"/20");
                } else {
                    sender.sendMessage("    Nutritional details are shown here for players.");
                }
                sender.sendMessage(ChatColor.WHITE+"Visit "+ChatColor.GOLD+"http://bit.ly/32M-Wiki"+ChatColor.WHITE+" for mechanics, recipes, and more.");
            } else if (args.length >= 1 && args[0].equalsIgnoreCase("version")) {
                sender.sendMessage(new String[] {ChatColor.GOLD+"ThreeSquareMeals v"+version});
            } else if (args.length == 3 && args[0].equalsIgnoreCase("set") && sender instanceof Player && sender.isOp()) {
                nutConfig.set(((Player)sender).getUniqueId()+".nut."+args[1], Integer.parseInt(args[2]));
                try {
                    nutConfig.save(playerNutFile);
                } catch (IOException ex) {
                }
                updateHealth((Player) sender);
                sender.sendMessage(prefix+" Updated nutrtional value of "+args[1]+" to "+args[2]);
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
        int level = nutConfig.getInt(ply.getUniqueId()+".nut."+id, 20);
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
        int totalNut = 0;
        for (int i = 0; i < Nutrition.NUMOFNUTS; i++) {
            totalNut += nutConfig.getInt(ply.getUniqueId()+".nut."+i, 20);
        }
        if (totalNut > getConfig().getInt("fullHealthNutritionThreshould", 18) * Nutrition.NUMOFNUTS) totalNut = getConfig().getInt("fullHealthNutritionThreshould", 18) * Nutrition.NUMOFNUTS;
        return Math.round(
                (getConfig().getInt("maxHealth", 20) - getConfig().getInt("minHealth", 6)) // 14
                * (totalNut/(getConfig().getInt("fullHealthNutritionThreshould", 18) * 1.0f * Nutrition.NUMOFNUTS))  // 0 to 1
                + getConfig().getInt("minHealth", 6) // 6
            );
    }
    
    private void updateHealth(Player ply) {
        ply.setMaxHealth(getMaxHealth(ply));
        if (ply.getHealth() > ply.getMaxHealth()) {
            ply.setHealth(ply.getMaxHealth());
        }
    }

    private final class MealListener implements Listener {
        
        @org.bukkit.event.EventHandler(priority = org.bukkit.event.EventPriority.MONITOR)
        public void onConsumeItem(org.bukkit.event.player.PlayerItemConsumeEvent e) {
            if (e.getItem() == null) return;
            try {
                Nutrition.valueOf(e.getItem().getType().toString());
            } catch (IllegalArgumentException ex) {
                return;
            }
            Player ply = e.getPlayer();
            int[] nutrition = new int[Nutrition.NUMOFNUTS];
            if (e.getItem().hasItemMeta() 
                    && e.getItem().getItemMeta().hasLore() 
                    && e.getItem().getItemMeta().hasDisplayName() 
                    && e.getItem().getItemMeta().getDisplayName().contains("Meal")) {
                String nutritionLine = e.getItem().getItemMeta().getLore().get(0);
                String[] result = nutritionLine.split("\\s*"+ChatColor.COLOR_CHAR+"[0123456789abcdef][A-Z]", Nutrition.NUMOFNUTS+1);
                int nutId = 0;
                for (String r : result) {
                    if (r.equals("")) continue;
                    nutrition[nutId] = Integer.parseInt(r);
                    nutId++;
                }
                
            } else {
                System.arraycopy(Nutrition.valueOf(e.getItem().getType().toString()).nutrition, 0, nutrition, 0, Nutrition.NUMOFNUTS);
            }
            
            for (int id = 0; id < Nutrition.NUMOFNUTS; id++) {
                nutConfig.set(
                        ply.getUniqueId()+".nut."+id, 
                        nutConfig.getInt(ply.getUniqueId()+".nut."+id, 20) 
                        + Math.round(nutrition[id] * Nutrition.valueOf(e.getItem().getType().toString()).portions)
                );
                if (nutConfig.getInt(ply.getUniqueId()+".nut."+id, 20) > 20) nutConfig.set(ply.getUniqueId()+".nut."+id, 20);
            }
            
            if (e.getItem().hasItemMeta() 
                    && e.getItem().getItemMeta().hasLore() 
                    && e.getItem().getItemMeta().hasDisplayName() 
                    && e.getItem().getItemMeta().getDisplayName().contains("Meal")) {
                e.setCancelled(true);
                ItemStack is = e.getItem();
                ItemMeta im = is.getItemMeta();
                List<String> lore = im.getLore();
                
                // Hunger
                e.getPlayer().setFoodLevel(
                        Math.min(
                                e.getPlayer().getFoodLevel()+(int)(Float.parseFloat(lore.get(2).split(" ")[0].substring(2))*2)
                                ,20)
                );
                
                // Saturation
                e.getPlayer().setSaturation(
                        Math.min(
                                e.getPlayer().getSaturation()+Float.parseFloat(lore.get(3).split(" ")[0].substring(2))
                                ,20)
                );
                
                // Portions
                int portionsValue = Integer.parseInt(lore.get(1).split(" ")[0].substring(2));
                portionsValue--;
                lore.set(1, ChatColor.GRAY + "" + (portionsValue) + " portion(s)");
                
                if (portionsValue <= 0) {
                    PlayerInventory inv = e.getPlayer().getInventory();
                    if (inv.getItemInMainHand().isSimilar(is)) {
                        e.getPlayer().getInventory().setItemInMainHand(new ItemStack(Material.BOWL));
                    } else {
                        e.getPlayer().getInventory().setItemInOffHand(new ItemStack(Material.BOWL));
                    }
                } else {
                    PlayerInventory inv = e.getPlayer().getInventory();
                    if (inv.getItemInMainHand().isSimilar(is)) {
                        im.setLore(lore);
                        is.setItemMeta(im);
                        e.getPlayer().getInventory().setItemInMainHand(is);
                    } else {
                        im.setLore(lore);
                        is.setItemMeta(im);
                        e.getPlayer().getInventory().setItemInOffHand(is);
                    }
                }
            }
            
            updateHealth(e.getPlayer());
            try {
                nutConfig.save(playerNutFile);
            } catch (IOException ex) {
            }
            // TODO Show nutrition change in chat
        }
        
        @org.bukkit.event.EventHandler(priority = org.bukkit.event.EventPriority.NORMAL)
        public void onPlayerInteract(org.bukkit.event.player.PlayerInteractEvent e) {
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (e.getClickedBlock().getType() == Material.WOOD_PLATE
                        || e.getClickedBlock().getType() == Material.STONE_PLATE
                        || e.getClickedBlock().getType() == Material.IRON_PLATE
                        || e.getClickedBlock().getType() == Material.GOLD_PLATE) {
                    Block clkdBlk = e.getClickedBlock();
                    Material typeBelow = clkdBlk.getWorld().getBlockAt(clkdBlk.getLocation().clone().add(0, -1, 0)).getType();
                    if (typeBelow == Material.FURNACE || typeBelow == Material.BURNING_FURNACE) {
                        new CookingSurface(ThreeSquareMeals.this).open(e.getPlayer());
                    }
                }
            }
        }
        
    }
    
    public class NutritionTimer extends BukkitRunnable {

        @Override
        public void run() {
            Collection<Player> players = (Collection<Player>) getServer().getOnlinePlayers();
            for (Player ply : players) {
                for (int id = 0; id < Nutrition.NUMOFNUTS; id++) {
                    nutConfig.set(ply.getUniqueId()+".nut."+id, nutConfig.getInt(ply.getUniqueId()+".nut."+id, 20) - 1);
                    if (nutConfig.getInt(ply.getUniqueId()+".nut."+id, 20) < 0) nutConfig.set(ply.getUniqueId()+".nut."+id, 0);
                }
                updateHealth(ply);
                try {
                    nutConfig.save(playerNutFile);
                } catch (IOException ex) {
                }
            }
        }

    }

}
