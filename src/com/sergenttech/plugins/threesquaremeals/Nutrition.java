
package com.sergenttech.plugins.threesquaremeals;

import java.util.ArrayList;
import java.util.Arrays;
import org.bukkit.ChatColor;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 *
 * @author Ben Sergent V/ha1fBit
 */
public enum Nutrition {
    
    // Nutrition value is per portion
    
    // Hunger, Saturation, Portion
    // Fruit, Vegetable, Grain, Protein, Dairy
    APPLE(4,2.4f,0.3f,4,0,0,0,0),
    BAKED_POTATO(5,6.0f,0.7f,0,3,0,1,0),
    BEETROOT(1,1.2f,0.6f,0,5,0,1,0),
    BEETROOT_SEEDS(1,0.5f,0.2f,0,1,0,0,0),
    BEETROOT_SOUP(6,7.2f,1.7f,0,6,0,1,0),
    BREAD(5,6.0f,1.2f,0,0,5,0,0),
    BROWN_MUSHROOM(1,0.5f,0.3f,0,1,0,1,0),
    CAKE(14,2.8f,6.0f,0,0,5,1,3),
    //CAKE_BLOCK(), TODO Add placed cake values
    CARROT_ITEM(3,3.6f,0.5f,0,2,0,0,0),
    CHORUS_FRUIT(4,2.4f,0.8f,4,0,0,0,0, new PotionEffect(PotionEffectType.LEVITATION, 200, 1)),
    COOKED_BEEF(8,12.8f,2.0f,0,0,0,4,0),
    COOKED_CHICKEN(6,7.2f,1.8f,0,0,0,3,0),
    COOKED_FISH(5,6.0f,1.0f,0,0,0,3,0),
    COOKED_MUTTON(6,9.6f,1.8f,0,0,0,3,0),
    COOKED_RABBIT(5,6.0f,1.5f,0,0,0,3,0),
    COOKIE(2,0.4f,0.5f,0,0,1,0,0, new PotionEffect(PotionEffectType.SATURATION, 100, 1)),
    EGG(2,0.8f,0.8f,0,0,0,2,0),
    GOLDEN_APPLE(4,9.6f,0.8f,7,1,1,1,1, new PotionEffect(PotionEffectType.ABSORPTION, 1200, 1)),
    GOLDEN_CARROT(6,14.4f,0.7f,1,7,1,1,1, new PotionEffect(PotionEffectType.NIGHT_VISION, 1200, 1)),
    GRILLED_PORK(8,12.8f,2.0f,0,0,0,4,0),
    LEAVES(1,0.5f,0.2f,0,1,0,0,0),
    LEAVES_2(1,0.5f,0.2f,0,1,0,0,0),
    LONG_GRASS(1,0.5f,0.2f,0,1,0,0,0),
    MELON(2,1.2f,0.3f,4,0,0,0,0),
    MELON_SEEDS(1,0.5f,0.2f,0,1,0,0,0),
    MILK_BUCKET(2,4.0f,2.0f,0,0,0,0,3),
    MUSHROOM_SOUP(6,7.2f,1.0f,0,2,0,1,0),
    NETHER_WARTS(2,1.2f,0.3f,4,0,0,1,0),
    POTATO_ITEM(1,0.6f,0.7f,0,1,0,0,0),
    PUMPKIN(2,1.8f,0.9f,0,3,0,0,0),
    PUMPKIN_PIE(8,4.8f,1.4f,0,5,0,0,0),
    PUMPKIN_SEEDS(1,0.5f,0.2f,0,1,0,0,0),
    RABBIT_STEW(10,12f,1.0f,0,1,0,4,0),
    RED_MUSHROOM(1,0.5f,0.3f,0,1,0,1,0),
    ROTTEN_FLESH(4,0.8f,0.8f,0,0,0,1,0, new PotionEffect(PotionEffectType.BLINDNESS,600,1)),
    SEEDS(1,0.5f,0.2f,0,1,0,0,0),
    SPIDER_EYE(4,0.8f,0.8f,0,0,0,1,0, new PotionEffect(PotionEffectType.SLOW_DIGGING,600,2)),
    SUGAR(1,0.5f,0.3f,0,0,0,0,0, new PotionEffect(PotionEffectType.SPEED,200,1)),
    VINE(1,0.5f,0.2f,0,1,0,0,0),
    WHEAT(1,2.2f,0.5f,0,0,2,0,0);
    
    public static final int NUMOFNUTS = 5;
    
    protected final int hunger;
    protected final float saturation;
    protected final float portions;
    protected final int[] nutrition = new int[NUMOFNUTS]; // F V G P D
    protected final ArrayList<PotionEffect> effects = new ArrayList();
    
    public static final String[] SYMBOLS = new String[] {
        ChatColor.AQUA+"F",
        ChatColor.GREEN+"V",
        ChatColor.YELLOW+"G",
        ChatColor.RED+"P",
        ChatColor.WHITE+"D"
    };
    public static final String[] NAMES = new String[] {
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
    
    private Nutrition(int hunger, float saturation, float portions, int fruit, int vegetable, int grain, int protein, int dairy, PotionEffect... effects) {
        this.hunger = hunger;
        this.saturation = saturation;
        this.portions = portions;
        nutrition[0] = fruit;
        nutrition[1] = vegetable;
        nutrition[2] = grain;
        nutrition[3] = protein;
        nutrition[4] = dairy;
        this.effects.addAll(Arrays.asList(effects));
    }

}
