package com.nmm.code.randomizer;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class Randomizer extends JavaPlugin implements Listener {
    Map<Material, ItemStack> drops = new HashMap<>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("Randomizer aktiviert!");

        Random rand = new Random();
        List<Material> validMaterials = Arrays.stream(Material.values())
                .filter(Material::isBlock)
                .filter(Material::isItem)
                .filter(m -> !m.name().endsWith("_WALL_SIGN"))
                .filter(m -> !m.name().endsWith("_BED"))
                .filter(m -> !m.name().endsWith("_HEAD"))       
                .filter(m -> !m.name().endsWith("_BANNER"))   
                .toList();

        for (Material material : validMaterials) {
            if (!material.isBlock()) continue;
            setDrop(material, rand, validMaterials);
        }

        Material[] mustHave = {Material.ENDER_PEARL, Material.BLAZE_ROD, Material.BLAZE_POWDER};
        for (Material m : mustHave) {
            int idx = rand.nextInt(validMaterials.size());
            Material targetBlock = validMaterials.get(idx);
            drops.put(targetBlock, new ItemStack(m, rand.nextInt(5) + 1));
        }

        Predicate<Map.Entry<Material, ItemStack>> isPlayable = entry -> {
            Material m = entry.getValue().getType();
            return m == Material.ENDER_PEARL || m == Material.BLAZE_ROD || m == Material.BLAZE_POWDER;
        };

        var items = drops.entrySet()
                .stream()
                .filter(isPlayable);
        System.out.println(Arrays.toString(items.toArray()));
    }


    private void setDrop(Material material, Random rand, List<Material> validMaterials) {
        int amount;
        int idx = rand.nextInt(validMaterials.size());
        amount = rand.nextInt(10) + 1;
        ItemStack item = new ItemStack(validMaterials.get(idx), amount);
        drops.put(material, item);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        e.setDropItems(false);
        ItemStack drop = drops.get(e.getBlock().getType());
        if (drop != null) {
            e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), drop);
        }
    }
}

