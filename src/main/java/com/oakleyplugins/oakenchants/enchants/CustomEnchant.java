package com.oakleyplugins.oakenchants.enchants;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Consumer;

import java.util.Arrays;

public class CustomEnchant extends Enchantment {

    private final String name;
    private final int maxLvl;
    private final EnchantmentTarget[] target;
    private final Material enchantMaterial;


    private Consumer<EntityDamageByEntityEvent> onDamage;
    private Consumer<EntityShootBowEvent> onBow;

    public CustomEnchant(String key, String name, int maxLvl, Material enchantMaterial, EnchantmentTarget... target) {
        super(NamespacedKey.minecraft(key));
        this.name = name;
        this.maxLvl = maxLvl;
        this.target = target;
        this.enchantMaterial = enchantMaterial;
    }

    public void setOnBow(Consumer<EntityShootBowEvent> onBow) {
        this.onBow = onBow;
    }

    public void onBow(EntityShootBowEvent event) {
        if (onBow != null) {
            onBow.accept(event);
        }
    }

    public void setOnDamage(Consumer<EntityDamageByEntityEvent> onDamage) {
        this.onDamage = onDamage;
    }

    public void onDamage(EntityDamageByEntityEvent event) {
        if (onDamage != null) {
            onDamage.accept(event);
        }
    }

    public Material getEnchantMaterial() {
        return enchantMaterial;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getMaxLevel() {
        return maxLvl;
    }

    @Override
    public int getStartLevel() {
        return 0;
    }

    @Override
    public EnchantmentTarget getItemTarget() {
        return target[0];
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

    @Override
    public boolean canEnchantItem(ItemStack item) {
        return Arrays.stream(target).anyMatch(t -> t.includes(item.getType()));
    }

}
