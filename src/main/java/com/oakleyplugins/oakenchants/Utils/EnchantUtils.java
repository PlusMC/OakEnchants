package com.oakleyplugins.oakenchants.Utils;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class EnchantUtils {

    public static void enchantCustom(ItemStack stack, int lvl, Enchantment enchant) {
        if (stack.getItemMeta() == null) return;
        ItemMeta meta = stack.getItemMeta();

        List<String> str = new ArrayList<>();
        //remove old lore
        if (meta.getLore() != null) {
            List<String> oldLore = meta.getLore();
            oldLore.removeIf(s -> s.startsWith(enchant.getName()));
            str.addAll(oldLore);
        }

        if(lvl <= 0) {
            meta.removeEnchant(enchant);
        } else {
            str.add(enchant.getName() + " " + RomanNumerals(lvl));
            stack.addUnsafeEnchantment(enchant, lvl);
        }

        meta.setLore(str);
        stack.setItemMeta(meta);

    }

    public static String RomanNumerals(int Int) {
        LinkedHashMap<String, Integer> roman_numerals = new LinkedHashMap<>();
        roman_numerals.put("M", 1000);
        roman_numerals.put("CM", 900);
        roman_numerals.put("D", 500);
        roman_numerals.put("CD", 400);
        roman_numerals.put("C", 100);
        roman_numerals.put("XC", 90);
        roman_numerals.put("L", 50);
        roman_numerals.put("XL", 40);
        roman_numerals.put("X", 10);
        roman_numerals.put("IX", 9);
        roman_numerals.put("V", 5);
        roman_numerals.put("IV", 4);
        roman_numerals.put("I", 1);
        StringBuilder res = new StringBuilder();
        for (Map.Entry<String, Integer> entry : roman_numerals.entrySet()) {
            int matches = Int / entry.getValue();
            res.append(repeat(entry.getKey(), matches));
            Int = Int % entry.getValue();
        }
        return res.toString();
    }

    static String repeat(String s, int n) {
        if (s == null) {
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append(s);
        }
        return sb.toString();
    }
}
