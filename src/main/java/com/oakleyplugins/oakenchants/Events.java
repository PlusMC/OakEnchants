package com.oakleyplugins.oakenchants;

import com.oakleyplugins.oakenchants.enchants.MindArrows;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import static com.oakleyplugins.oakenchants.Utils.Utils.enchantCustom;
import static com.oakleyplugins.oakenchants.enchants.CustomEnchant.ENCHANTS;

public class Events implements org.bukkit.event.Listener {
    @EventHandler
    public void onKill(EntityDamageByEntityEvent e) {
        if (e.isCancelled()) return;
        if (e.getDamager() instanceof LivingEntity) {
            LivingEntity p = (LivingEntity) e.getDamager();
            if (p.getEquipment() == null) return;
            ItemStack item = p.getEquipment().getItemInMainHand();
            if (p.getEquipment().getItemInMainHand().getItemMeta() == null) return;
            ENCHANTS.forEach(enchant -> {
                if (item.containsEnchantment(enchant))
                    enchant.onDamageEntity(e, item, item.getEnchantmentLevel(enchant));
            });
        }
    }

    @EventHandler
    public void onBow(EntityShootBowEvent e) {
        if (e.isCancelled()) return;
        if (e.getBow() == null) return;
        ENCHANTS.forEach(enchant -> {
            if (e.getBow().containsEnchantment(enchant))
                enchant.onShootBow(e, e.getBow(), e.getBow().getEnchantmentLevel(enchant));
        });
    }

    @EventHandler
    public void onHitGround(ProjectileHitEvent e) {
        if (MindArrows.HOMING.remove(e.getEntity())) {
            if (e.getHitEntity() != null)
                e.getEntity().getWorld().playSound(e.getEntity().getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1f, 2f);
        }
    }

    @EventHandler
    public void onEnchant(PrepareAnvilEvent e) {
        ItemStack item = e.getInventory().getItem(0);
        if (item != null)
            if (e.getResult() != null)
                ENCHANTS.forEach(enchant -> {
                    if (item.containsEnchantment(enchant))
                        e.getResult().addUnsafeEnchantment(enchant, item.getEnchantmentLevel(enchant) + 1);
                });
    }

    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent e) {
        if (e.getRightClicked().getPersistentDataContainer().has(NamespacedKey.minecraft("armorstandanim"), PersistentDataType.INTEGER)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.isCancelled()) return;
        Bukkit.getScheduler().runTask(OakEnchants.getInstance(), () -> {
            if (e.getInventory() instanceof AnvilInventory) {
                AnvilInventory anvil = (AnvilInventory) e.getInventory();
                ItemStack item0 = anvil.getItem(0);
                ItemStack item1 = anvil.getItem(1);
                if (item0 == null) return;
                if (item1 == null) return;
                ENCHANTS.forEach(enchant -> {
                    if (item1.getAmount() == 1 &&
                            enchant.isEnchantItem(item1) &&
                            enchant.canEnchantItem(item0)
                    ) {
                        ItemStack clone = item0.clone();
                        int lvl = 0;
                        if (item0.getEnchantments().containsKey(enchant)) {
                            lvl = item0.getEnchantments().get(enchant);
                        }
                        if (lvl > 0 && lvl < enchant.getMaxLevel()) {
                            int test = lvl + 1;
                            enchantCustom(clone, test, enchant);
                            anvil.setItem(2, clone);
                            anvil.setRepairCost(1);
                            for (HumanEntity viewer : anvil.getViewers())
                                viewer.setWindowProperty(InventoryView.Property.REPAIR_COST, 1);
                        } else if (lvl < enchant.getMaxLevel()) {
                            enchantCustom(clone, 1, enchant);
                            anvil.setItem(2, clone);
                            anvil.setRepairCost(1);
                            for (HumanEntity viewer : anvil.getViewers())
                                viewer.setWindowProperty(InventoryView.Property.REPAIR_COST, 1);
                        }
                    }
                });
            }
        });
    }

}
