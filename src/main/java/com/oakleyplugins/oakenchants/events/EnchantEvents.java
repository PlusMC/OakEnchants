package com.oakleyplugins.oakenchants.events;

import com.oakleyplugins.oakenchants.OakEnchants;
import com.oakleyplugins.oakenchants.enchants.CustomEnchant;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import static com.oakleyplugins.oakenchants.Utils.EnchantUtils.enchantCustom;
import static com.oakleyplugins.oakenchants.enchants.CustomEnchant.ENCHANTS;

public class EnchantEvents implements Listener {
    @EventHandler
    public void onAnvilPrepare(PrepareAnvilEvent e) {
        ItemStack item = e.getInventory().getItem(0);
        if (item == null) return;
        if (e.getResult() == null) return;

        ENCHANTS.forEach(enchant -> {
            if (item.containsEnchantment(enchant))
                e.getResult().addUnsafeEnchantment(enchant, item.getEnchantmentLevel(enchant) + 1);
        });
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.isCancelled()) return;
        if (!(e.getInventory() instanceof AnvilInventory)) return;

        Bukkit.getScheduler().runTask(OakEnchants.getInstance(), () -> {
            AnvilInventory anvil = (AnvilInventory) e.getInventory();
            ItemStack item0 = anvil.getItem(0);
            ItemStack item1 = anvil.getItem(1);
            if (item0 == null || item1 == null) return;
            if (item1.getAmount() != 1) return;

            for(CustomEnchant enchant : ENCHANTS) {
                if (!(enchant.isEnchantItem(item1) && enchant.canEnchantItem(item0))) continue;
                ItemStack dummy = item0.clone();
                int curLevel = item0.getEnchantmentLevel(enchant);
                if (!(curLevel < enchant.getMaxLevel())) continue;
                int newLevel = curLevel + 1;
                enchantCustom(dummy, newLevel, enchant);
                anvil.setItem(2, dummy);
                anvil.setRepairCost(1);
                for (HumanEntity viewer : anvil.getViewers())
                    viewer.setWindowProperty(InventoryView.Property.REPAIR_COST, 1);
            }

            if(e.getWhoClicked() instanceof Player)
                ((Player) e.getWhoClicked()).updateInventory();
        });
    }
}
