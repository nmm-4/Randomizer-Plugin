package com.nmm.code.randomizer;

import org.bukkit.Material;
import org.bukkit.entity.Player;
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
                .toList();

        for (Material block : blockMaterials) {
            Material drop = dropMaterials.get(rand.nextInt(dropMaterials.size()));
            int amount = rand.nextInt(3) + 1;
            amount = Math.min(amount, drop.getMaxStackSize());
            drops.put(block, new ItemStack(drop, amount));
        }

        Material[] mustHave = {
                Material.ENDER_PEARL,
                Material.ENDER_EYE,
                Material.BLAZE_ROD,
                Material.BLAZE_POWDER,
                Material.CARVED_PUMPKIN
        };
        for (int i = 0; i < 2; i++) {
            for (Material m : mustHave) {
                Material block = blockMaterials.get(rand.nextInt(blockMaterials.size()));
                drops.put(block, new ItemStack(m,1));
                System.out.println(block);
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        e.setDropItems(false);

        ItemStack drop = drops.get(e.getBlock().getType());
        if (drop == null) return;


        e.getBlock().getWorld().dropItem(
                e.getBlock().getLocation(), drop
        );
    }
}
