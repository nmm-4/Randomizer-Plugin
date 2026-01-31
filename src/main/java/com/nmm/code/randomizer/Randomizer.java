package com.nmm.code.randomizer;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class Randomizer extends JavaPlugin implements Listener {

    private final Map<Material, ItemStack> drops = new HashMap<>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);

        Random rand = new Random();

        List<Material> blockMaterials = Arrays.stream(Material.values())
                .filter(Material::isBlock)
                .toList();

        List<Material> dropMaterials = Arrays.stream(Material.values())
                .filter(Material::isItem)
                .filter(m -> m != Material.AIR)
                .filter(m -> !m.name().endsWith("_SPAWN_EGG"))
                .toList();

        for (Material block : blockMaterials) {
            Material drop = dropMaterials.get(rand.nextInt(dropMaterials.size()));
            int amount = rand.nextInt(5) + 1;
            drops.put(block, new ItemStack(drop, amount));
        }

        Material[] mustHave = {
                Material.ENDER_PEARL,
                Material.BLAZE_ROD,
                Material.BLAZE_POWDER,
                Material.CARVED_PUMPKIN
        };

        for (Material m : mustHave) {
            Material block = blockMaterials.get(rand.nextInt(blockMaterials.size()));
            drops.put(block, new ItemStack(m, rand.nextInt(3) + 1));
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        e.setDropItems(false);

        ItemStack drop = drops.get(e.getBlock().getType());
        if (drop != null) {
            e.getBlock().getWorld().dropItemNaturally(
                    e.getBlock().getLocation(),
                    drop.clone()
            );
        }
    }
}
