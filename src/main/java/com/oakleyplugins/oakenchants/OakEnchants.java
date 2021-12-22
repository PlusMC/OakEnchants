package com.oakleyplugins.oakenchants;

import com.oakleyplugins.oakenchants.enchants.CustomEnchant;
import org.bukkit.plugin.java.JavaPlugin;

public final class OakEnchants extends JavaPlugin {

    @Override
    public void onEnable() {
        CustomEnchant.loadAll();
        getServer().getPluginManager().registerEvents(new Events(), getInstance());
    }

    public static OakEnchants getInstance() {
        return JavaPlugin.getPlugin(OakEnchants.class);
    }

    @Override
    public void onDisable() {
        CustomEnchant.unloadAll();
    }


}
