
package com.sergenttech.plugins.threesquaremeals;

import java.util.Arrays;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Ben Sergent V/ha1fBit
 */
public class CookingSurface implements Listener {
    
    protected ThreeSquareMeals plugin;
    private Player viewer;
    private Inventory inventory;
   
    public CookingSurface(ThreeSquareMeals plugin) {
        this.plugin = plugin;
    }
   
    public void open(Player player) {
        if (player.getOpenInventory() != null && player.getOpenInventory().getTitle().equals("Cooking Surface")) {
            return;
        }
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        viewer = player;
        inventory = Bukkit.createInventory(player, 27, "Cooking Surface");
        
        ItemStack bar = new ItemStack(Material.BARRIER);
        setItemNameAndLore(bar, " ", new String[0]);
        ItemStack info = new ItemStack(Material.INK_SACK, 1, (short) 12);
        setItemNameAndLore(info, ChatColor.RESET+""+ChatColor.AQUA+"Help", new String[] {ChatColor.GRAY+"Click for more info"});
        
        /*
        0  | B - - - - - - - i | 8      B = Bowl    i = Information
        9  | - - - - - - - - - | 17     F = Food Ingredients
        18 | - F F F F F - R - | 26     R = Result
        */
        for (int i = 1; i <= 7; i++) {
            inventory.setItem(i, bar);
        }
        inventory.setItem(8, info);
        for (int i = 9; i <= 17; i++) {
            inventory.setItem(i, bar);
        }
        inventory.setItem(18, bar);
        inventory.setItem(24, bar);
        inventory.setItem(26, bar);
        
        player.openInventory(inventory);
    }
    
    private void destroy() {
        HandlerList.unregisterAll(this);
        plugin = null;
        viewer = null;
        inventory = null;
    }
    
    private ItemStack processRecipe(ItemStack[] contents) {
        if (contents[0] == null || contents[0].getType() != Material.BOWL) {
            return null;
        }
        return new ItemStack(Material.MUSHROOM_SOUP);
    }
   
    @EventHandler(priority=EventPriority.MONITOR)
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().getTitle().equals("Cooking Surface")) {
            ItemStack[] contents = e.getInventory().getContents();
            int slot = e.getRawSlot();
            if (slot == 8) {
                e.getWhoClicked().sendMessage(plugin.getPrefix()+" Visit http://bit.ly/1Cijgbl for help with cooking your meals.");
                e.setResult(Event.Result.DENY);
            } else if (e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.BARRIER) {
                e.setResult(Event.Result.DENY);
            //} else if (slot == 0 && e.getCursor() != null && e.getCursor().getType() != Material.BOWL) {
            //    e.setResult(Event.Result.DENY);
            } else if (slot == 25 && !(e.getCursor() == null || e.getCursor().getType() == Material.AIR)) {
                e.setResult(Event.Result.DENY);
            }
            if (slot <= 26) {
                contents[slot] = e.getCursor();
            }
            e.getInventory().setItem(25, processRecipe(contents));
            new BukkitRunnable() {
                    @Override
                    public void run() {
                      viewer.updateInventory();
                    }
                }.runTaskLater(plugin, 0);
        }
    }
    
    @EventHandler(priority=EventPriority.MONITOR)
    void onInventoryClose(org.bukkit.event.inventory.InventoryCloseEvent e) {
        if (e.getInventory().getTitle().equals("Cooking Surface") && viewer != null) {
            ItemStack[] contents = inventory.getContents();
            if (contents[0] != null)
                viewer.getWorld().dropItemNaturally(viewer.getLocation(), contents[0]);
            for (int i = 19; i <= 23; i++) {
                if (contents[i] != null)
                    viewer.getWorld().dropItemNaturally(viewer.getLocation(), contents[i]);
            }
            destroy();
        }
    }
   
    private ItemStack setItemNameAndLore(ItemStack item, String name, String[] lore) {
        ItemMeta im = item.getItemMeta();
            im.setDisplayName(name);
            im.setLore(Arrays.asList(lore));
        item.setItemMeta(im);
        return item;
    }
   
}
