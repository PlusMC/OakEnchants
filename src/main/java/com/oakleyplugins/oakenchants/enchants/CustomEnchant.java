package com.oakleyplugins.oakenchants.enchants;

import com.oakleyplugins.oakenchants.OakEnchants;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class CustomEnchant extends Enchantment {
    public static List<CustomEnchant> ENCHANTS;


    public CustomEnchant(String key) {
        super(new NamespacedKey(OakEnchants.getInstance(), key));
    }

    public abstract EnchantmentTarget[] getEnchantTargets();
    public abstract boolean isEnchantItem(ItemStack stack);

    public void onDamageEntity(EntityDamageByEntityEvent event, ItemStack item, int level) {
        //ignore
    }
    public void onShootBow(EntityShootBowEvent event, ItemStack item, int level) {
        //ignore
    }
    void load() {
        //ignore
    }
    void unload() {
        //ignore
    }


    public static void loadAll() {
        ENCHANTS = Arrays.asList(
                new Decapitator(),
                new MindArrows(),
                new QuiteIncredible()
        );
        ENCHANTS.forEach(CustomEnchant::load);
    }

    public static void unloadAll() {
        ENCHANTS.forEach(CustomEnchant::unload);
    }


    private static void register() {
        boolean registered = Arrays.stream(Enchantment.values()).collect(Collectors.toList()).containsAll(ENCHANTS);

        if (!registered)
            ENCHANTS.forEach(CustomEnchant::registerEnchant);

    }

     private static void registerEnchant(Enchantment enchant) {
        try {
            Field f = Enchantment.class.getDeclaredField("acceptingNew");
            f.setAccessible(true);
            f.set(null, true);
            Enchantment.registerEnchantment(enchant);
            OakEnchants.getInstance().getLogger().info("Registered Enchantment " + enchant.getKey());
        } catch (Exception e) {
            OakEnchants.getInstance().getLogger().warning("Could not register Enchantment: " + enchant.getKey());
        }
    }

    @Override
    public boolean canEnchantItem(ItemStack item) {
        return Arrays.stream(getEnchantTargets()).anyMatch(t -> t.includes(item.getType()));
    }

    @Override
    public EnchantmentTarget getItemTarget() {
        return getEnchantTargets()[0];
    }

    @Override
    public boolean isTreasure() {
        return false;
    }

    @Override
    public boolean isCursed() {
        return false;
    }

    @Override
    public boolean conflictsWith(Enchantment other) {
        return false;
    }

}
