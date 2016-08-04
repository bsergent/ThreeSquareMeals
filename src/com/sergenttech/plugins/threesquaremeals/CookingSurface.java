
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
import org.bukkit.event.inventory.InventoryDragEvent;
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
    
    /*
    0  | - B - F F - - - i | 8      B = Bowl    i = Information
    9  | - - - F F - - - - | 17     F = Food Ingredients
    18 | - - - F F - R - - | 26     R = Result
       | 00 01 02 03 04 05 06 07 08 |
       | 09 10 11 12 13 14 15 16 17 |
       | 18 19 20 21 22 23 24 25 26 |
    */
    private final int bowlSlot = 1;
    private final int[] ingredientSlots = {3,4,12,13,21,22};
    private final int resultSlot = 24;
    private final int infoSlot = 7;
   
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
        if (!plugin.getConfig().getBoolean("useRetexturedBarriers", false)) {
            bar.setType(Material.STAINED_GLASS_PANE);
            bar.setDurability((short) 15);
        }
        setItemNameAndLore(bar, " ", new String[0]);
        ItemStack info = new ItemStack(Material.INK_SACK, 1, (short) 12);
        setItemNameAndLore(info, ChatColor.RESET+""+ChatColor.AQUA+"Help", new String[] {ChatColor.GRAY+"Click for more info"});
        
        
        for (int i = 0; i <= 26; i++) {
            inventory.setItem(i, bar);
        }
        inventory.setItem(bowlSlot, null);
        for (int i : ingredientSlots) {
            inventory.setItem(i, null);
        }
        inventory.setItem(resultSlot, null);
        inventory.setItem(infoSlot, info);
        
        player.openInventory(inventory);
    }
    
    private void destroy() {
        HandlerList.unregisterAll(this);
        plugin = null;
        viewer = null;
        inventory = null;
    }
    
    private ItemStack processRecipe(ItemStack[] contents) {
        if (contents[bowlSlot] == null || contents[bowlSlot].getType() != Material.BOWL) {
            return null;
        }
        
        // Make sure all ingredients are valid
        boolean allNull = true;
        for (int i : ingredientSlots) {
            if (contents[i] != null) {
                allNull = false;
                try {
                    Nutrition.valueOf(contents[i].getType().toString());
                } catch (IllegalArgumentException ex) {
                    return null;
                }
            }
        }
        if (allNull) return null;
        
        float portions = 0.0f;
        int hunger = 0;
        float saturation = 0.0f;
        int[] nutrition = new int[Nutrition.NUMOFNUTS];
        
        // Calculate nutrional values
        for (int i : ingredientSlots) {
            if (contents[i] != null) {
                hunger += Nutrition.valueOf(contents[i].getType().toString()).hunger;
                saturation += Nutrition.valueOf(contents[i].getType().toString()).saturation;
                portions += Nutrition.valueOf(contents[i].getType().toString()).portions;
                for (int n = 0; n < Nutrition.NUMOFNUTS; n++) {
                    nutrition[n] += Nutrition.valueOf(contents[i].getType().toString()).nutrition[n];
                }
            }
        }
        
        // Calculate portions
        portions = (float) Math.ceil(portions);
        hunger = (int) Math.round(hunger * 1.3f / portions);
        saturation = saturation * 1.5f / portions;
        saturation = Math.round(saturation * 10) / 10f;
        for (int n = 0; n < Nutrition.NUMOFNUTS; n++) {
            nutrition[n] = (int) Math.ceil(nutrition[n] / portions);
        }
        
        // Determine material based on nutrition
        ItemStack result = new ItemStack(Material.MUSHROOM_SOUP);
        // Protein > Rabbit Stew
        // Vegetables > Retextured Mushroom Soup
        // Fruits > Beetroot Stew
        
        // Add meta data based on nutrition
        ItemMeta im = result.getItemMeta();
        // Change name based upon nutrition
        im.setDisplayName(ChatColor.RESET+"Meal");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Nutrition.NUMOFNUTS; i++) {
            sb.append(Nutrition.symbols[i]);
            //sb.append(':');
            sb.append(nutrition[i]);
            sb.append(' ');
        }
        im.setLore(Arrays.asList(new String[] {
            sb.toString().trim(),
            ChatColor.GRAY+""+(int) portions+" portion(s)",
            ChatColor.GRAY+""+(hunger / 2f)+" shanks",
            ChatColor.GRAY+""+saturation+" saturation"
        }));
        
        // Save effects somehow?
        result.setItemMeta(im);
        
        return result;
    }
    
    public static boolean isSimilarMeal(ItemStack is1, ItemStack is2) {
        if (!isMeal(is1) || !isMeal(is2)) return false;
        if (!is1.getItemMeta().getLore().get(0).equals(is2.getItemMeta().getLore().get(0))) return false;
        if (!is1.getItemMeta().getLore().get(2).equals(is2.getItemMeta().getLore().get(2))) return false;
        return is1.getItemMeta().getLore().get(3).equals(is2.getItemMeta().getLore().get(3));
    }
    
    public static boolean isMeal(ItemStack is) {
        if (is.getType() != Material.MUSHROOM_SOUP && is.getType() != Material.RABBIT_STEW && is.getType() != Material.BEETROOT_SOUP) return false;
        if (!is.hasItemMeta()) return false;
        if (!is.getItemMeta().hasDisplayName()) return false;
        if (!is.getItemMeta().hasLore()) return false;
        return is.getItemMeta().getDisplayName().contains("Meal");
    }
   
    @EventHandler(priority=EventPriority.MONITOR)
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().getTitle().equals("Cooking Surface")) {
            ItemStack[] contents = e.getInventory().getContents();
            int slot = e.getRawSlot();
            if (slot == infoSlot) {
                e.getWhoClicked().sendMessage(plugin.getPrefix()+" Visit http://bit.ly/32M-Cooking for help with cooking your meals.");
                e.setResult(Event.Result.DENY);
            } else if (e.getCurrentItem() != null && (e.getCurrentItem().getType() == Material.BARRIER || e.getCurrentItem().getType() == Material.STAINED_GLASS_PANE)) {
                e.setResult(Event.Result.DENY);
            } else if (slot == resultSlot && !(e.getCursor() == null || e.getCursor().getType() == Material.AIR)) {
                e.setResult(Event.Result.DENY);
            }
            if (slot >= 0 && slot <= 26) {
                contents[slot] = e.getCursor();
            }
            e.getInventory().setItem(resultSlot, processRecipe(contents));
            if (slot == resultSlot && (e.getCursor() == null || e.getCursor().getType() == Material.AIR)) {
                for (int iSlot : ingredientSlots) {
                    if (contents[iSlot] != null) {
                        if (contents[iSlot].getType() == Material.MILK_BUCKET) {
                            contents[iSlot].setType(Material.BUCKET);
                        } else {
                            if (contents[iSlot].getAmount() <= 1) {
                                e.getInventory().setItem(iSlot, null);
                            } else {
                                contents[iSlot].setAmount(contents[iSlot].getAmount() - 1);
                            }
                        }
                    }
                }
                if (contents[bowlSlot] != null) {
                    if (contents[bowlSlot].getAmount() <= 1) {
                        e.getInventory().setItem(bowlSlot, null);
                    } else {
                        contents[bowlSlot].setAmount(contents[bowlSlot].getAmount() - 1);
                    }
                }
            }
            new BukkitRunnable() {
                    @Override
                    public void run() {
                      viewer.updateInventory();
                    }
                }.runTaskLater(plugin, 0);
        }
    }
    
    @EventHandler(priority=EventPriority.MONITOR)
    public void onInventoryDrag(InventoryDragEvent e) {
        if (e.getInventory().getTitle().equals("Cooking Surface")) {
            // TODO Update recipes on drag as well
        }
    }
    
    @EventHandler(priority=EventPriority.MONITOR)
    void onInventoryClose(org.bukkit.event.inventory.InventoryCloseEvent e) {
        if (e.getInventory().getTitle().equals("Cooking Surface") && viewer != null) {
            ItemStack[] contents = inventory.getContents();
            if (contents[bowlSlot] != null)
                viewer.getWorld().dropItemNaturally(viewer.getLocation(), contents[bowlSlot]);
            for (int slot : ingredientSlots) {
                if (contents[slot] != null)
                    viewer.getWorld().dropItemNaturally(viewer.getLocation(), contents[slot]);
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
