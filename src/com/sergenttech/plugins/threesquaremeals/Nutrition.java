
package com.sergenttech.plugins.threesquaremeals;

/**
 *
 * @author Ben Sergent V/ha1fBit
 */
public enum Nutrition {
    
    APPLE(4,2.4f,0,0,0,0,0,0)/*,
    BAKED_POTATO,
    BEETROOT,
    BEETROOT_SEEDS,
    BREAD,
    CAKE,
    CAKE_BLOCK,
    CARROT_ITEM,
    CHORUS_FRUIT,
    COOKED_BEEF,
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
    MELON_SEEDS,
    NETHER_WARTS,
    POTATO_ITEM,
    PUMPKIN,
    PUMPKIN_SEEDS,
    RED_MUSHROOM,
    ROTTEN_FLESH,
    SEEDS,
    SPIDER_EYE,
    SUGAR,
    VINE,
    WHEAT*/;
    
    private final int hunger;
    private final float saturation;
    private final float fruit;
    private final float vegetable;
    private final float grain;
    private final float protein;
    private final float dairy;
    private final float sweet;

    private Nutrition(int hunger, float saturation, float fruit, float vegetable, float grain, float protein, float dairy, float sweet) {
        this.hunger = hunger;
        this.saturation = saturation;
        this.fruit = fruit;
        this.vegetable = vegetable;
        this.grain = grain;
        this.protein = protein;
        this.dairy = dairy;
        this.sweet = sweet;
    }

}
