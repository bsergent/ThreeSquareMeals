
package com.sergenttech.plugins.threesquaremeals;

import org.bukkit.ChatColor;

/**
 *
 * @author Ben Sergent V/ha1fBit
 */
public enum Nutrition {
    
    // Nutrition value is per portion
    
    APPLE(4,2.4f,0.3f,4,0,0,0,0),
    /*BAKED_POTATO,
    BEETROOT,
    BEETROOT_SEEDS,
    BEETROOT_SOUP,
    BREAD,
    CAKE,
    CAKE_BLOCK,
    CARROT_ITEM,
    CHORUS_FRUIT,*/
    COOKED_BEEF(8,12.8f,2.0f,0,0,0,4,0)/*,
    COOKED_CHICKEN,
    COOKED_CHICKEN,
    COOKED_FISH,
    COOKED_MUTTON,
    COOKED_RABBIT,
    COOKIE,
    EGG,
    GOLDEN_APPLE,
    GOLDEN_CARROT,
    GRILLED_PORK,
    LEAVES,
    LEAVES_2,
    LONG_GRASS,
    MELON,
    MELON_SEEDS*/,
    MILK_BUCKET(2,4.0f,2.0f,0,0,0,0,3),
    MUSHROOM_SOUP(6,7.2f,1.0f,0,2,0,1,0)/*,
    NETHER_WARTS,
    POTATO_ITEM,
    PUMPKIN,
    PUMPKIN_SEEDS,
    RABBIT_STEW,
    RED_MUSHROOM,
    ROTTEN_FLESH,
    SEEDS,
    SPIDER_EYE,
    SUGAR,
    VINE*/,
    WHEAT(1,2.2f,0.5f,0,0,2,0,0);
    
    public static final int NUMOFNUTS = 5;
    
    protected final int hunger;
    protected final float saturation;
    protected final float portions;
    protected final int[] nutrition = new int[NUMOFNUTS]; // F V G P D
    
    public static final String[] symbols = new String[] {
        ChatColor.AQUA+"F",
        ChatColor.GREEN+"V",
        ChatColor.YELLOW+"G",
        ChatColor.RED+"P",
        ChatColor.WHITE+"D"
    };
    public static final String[] names = new String[] {
        ChatColor.AQUA+"F"+ChatColor.RESET+"ruit        ",
        ChatColor.GREEN+"V"+ChatColor.RESET+"egetables",
        ChatColor.YELLOW+"G"+ChatColor.RESET+"rain       ",
        ChatColor.RED+"P"+ChatColor.RESET+"rotein     ",
        ChatColor.WHITE+"D"+ChatColor.RESET+"airy       "
    };

    private Nutrition(int hunger, float saturation, float portions, int fruit, int vegetable, int grain, int protein, int dairy) {
        this.hunger = hunger;
        this.saturation = saturation;
        this.portions = portions;
        nutrition[0] = fruit;
        nutrition[1] = vegetable;
        nutrition[2] = grain;
        nutrition[3] = protein;
        nutrition[4] = dairy;
    }

}
