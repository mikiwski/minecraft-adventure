package com.adventure.task;

import com.adventure.task.tasks.CollectItemTask;
import com.adventure.task.tasks.CraftItemTask;
import com.adventure.task.tasks.KillMobTask;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class TaskDatabase {
    private final List<Task> allTasks;

    public TaskDatabase() {
        this.allTasks = new ArrayList<>();
        initializeTasks();
    }

    private Identifier mc(String path) {
        return Identifier.of("minecraft", path);
    }

    private TagKey<Item> itemTag(String path) {
        return TagKey.of(RegistryKeys.ITEM, Identifier.of("minecraft", path));
    }

    private TagKey<EntityType<?>> entityTag(String path) {
        return TagKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of("minecraft", path));
    }

    private void initializeTasks() {
        // Poziomy 1-20 (Podstawowe)
        allTasks.add(new CollectItemTask(1, 1, 10, null, itemTag("logs"), "task.adventure.collect_logs"));
        allTasks.add(new CollectItemTask(2, 2, 20, null, itemTag("planks"), "task.adventure.collect_planks"));
        allTasks.add(new CraftItemTask(3, 3, 16, mc("stick"), null, "task.adventure.craft_sticks"));
        allTasks.add(new CraftItemTask(4, 4, 32, mc("torch"), null, "task.adventure.craft_torches"));
        allTasks.add(new CollectItemTask(5, 5, 20, mc("cobblestone"), null, "task.adventure.collect_cobblestone"));
        allTasks.add(new CraftItemTask(6, 6, 1, mc("furnace"), null, "task.adventure.craft_furnace"));
        allTasks.add(new CollectItemTask(7, 7, 10, mc("coal"), null, "task.adventure.collect_coal"));
        allTasks.add(new CraftItemTask(8, 8, 1, mc("chest"), null, "task.adventure.craft_chest"));
        allTasks.add(new CollectItemTask(9, 9, 15, mc("iron_ingot"), null, "task.adventure.collect_iron"));
        allTasks.add(new CraftItemTask(10, 10, 1, mc("iron_pickaxe"), null, "task.adventure.craft_iron_pickaxe"));
        allTasks.add(new KillMobTask(11, 11, 5, null, entityTag("zombies"), "task.adventure.kill_zombies"));
        allTasks.add(new KillMobTask(12, 12, 3, null, entityTag("skeletons"), "task.adventure.kill_skeletons"));
        allTasks.add(new CollectItemTask(13, 13, 20, mc("wheat"), null, "task.adventure.collect_wheat"));
        allTasks.add(new CollectItemTask(14, 14, 10, mc("carrot"), null, "task.adventure.collect_carrots"));
        allTasks.add(new CollectItemTask(15, 15, 10, mc("potato"), null, "task.adventure.collect_potatoes"));
        allTasks.add(new KillMobTask(16, 16, 3, mc("cow"), null, "task.adventure.kill_cows"));
        allTasks.add(new KillMobTask(17, 17, 3, mc("pig"), null, "task.adventure.kill_pigs"));
        allTasks.add(new KillMobTask(18, 18, 5, mc("chicken"), null, "task.adventure.kill_chickens"));
        allTasks.add(new CraftItemTask(19, 19, 1, mc("wooden_sword"), null, "task.adventure.craft_wooden_sword"));
        allTasks.add(new CraftItemTask(20, 20, 1, mc("stone_axe"), null, "task.adventure.craft_stone_axe"));

        // Poziomy 21-40 (Średnie)
        allTasks.add(new CollectItemTask(21, 21, 30, mc("iron_ingot"), null, "task.adventure.collect_iron_30"));
        allTasks.add(new CraftItemTask(22, 22, 1, mc("iron_helmet"), null, "task.adventure.craft_iron_armor"));
        allTasks.add(new CollectItemTask(23, 23, 10, mc("gold_ingot"), null, "task.adventure.collect_gold"));
        allTasks.add(new CraftItemTask(24, 24, 1, mc("golden_sword"), null, "task.adventure.craft_golden_sword"));
        allTasks.add(new CollectItemTask(25, 25, 5, mc("diamond"), null, "task.adventure.collect_diamonds"));
        allTasks.add(new CraftItemTask(26, 26, 1, mc("diamond_sword"), null, "task.adventure.craft_diamond_sword"));
        allTasks.add(new CollectItemTask(27, 27, 20, mc("redstone"), null, "task.adventure.collect_redstone"));
        allTasks.add(new CraftItemTask(28, 28, 1, mc("redstone_torch"), null, "task.adventure.craft_redstone_torch"));
        allTasks.add(new CollectItemTask(29, 29, 15, mc("lapis_lazuli"), null, "task.adventure.collect_lapis"));
        allTasks.add(new CraftItemTask(30, 30, 1, mc("enchanting_table"), null, "task.adventure.craft_enchanting_table"));
        allTasks.add(new KillMobTask(31, 31, 10, mc("creeper"), null, "task.adventure.kill_creepers"));
        allTasks.add(new KillMobTask(32, 32, 5, mc("spider"), null, "task.adventure.kill_spiders"));
        allTasks.add(new KillMobTask(33, 33, 3, mc("enderman"), null, "task.adventure.kill_endermen"));
        allTasks.add(new CollectItemTask(34, 34, 10, mc("obsidian"), null, "task.adventure.collect_obsidian"));
        allTasks.add(new CraftItemTask(35, 35, 1, mc("anvil"), null, "task.adventure.craft_anvil"));
        allTasks.add(new CollectItemTask(36, 36, 20, mc("copper_ingot"), null, "task.adventure.collect_copper"));
        allTasks.add(new CraftItemTask(37, 37, 1, mc("copper_block"), null, "task.adventure.craft_copper_block"));
        allTasks.add(new KillMobTask(38, 38, 5, mc("zombified_piglin"), null, "task.adventure.kill_zombie_piglins"));
        allTasks.add(new CollectItemTask(39, 39, 10, mc("emerald"), null, "task.adventure.collect_emeralds"));
        allTasks.add(new CraftItemTask(40, 40, 1, mc("iron_chestplate"), null, "task.adventure.craft_iron_armor_full"));

        // Poziomy 41-60 (Zaawansowane)
        allTasks.add(new CollectItemTask(41, 41, 50, mc("iron_ingot"), null, "task.adventure.collect_iron_50"));
        allTasks.add(new CraftItemTask(42, 42, 1, mc("beacon"), null, "task.adventure.craft_beacon"));
        allTasks.add(new CollectItemTask(43, 43, 20, mc("diamond"), null, "task.adventure.collect_diamonds_20"));
        allTasks.add(new CraftItemTask(44, 44, 1, mc("diamond_chestplate"), null, "task.adventure.craft_diamond_armor"));
        allTasks.add(new KillMobTask(45, 45, 10, mc("ghast"), null, "task.adventure.kill_ghasts"));
        allTasks.add(new CollectItemTask(46, 46, 20, mc("blaze_rod"), null, "task.adventure.collect_blaze_rods"));
        allTasks.add(new CraftItemTask(47, 47, 1, mc("brewing_stand"), null, "task.adventure.craft_brewing_stand"));
        allTasks.add(new CollectItemTask(48, 48, 10, mc("ender_pearl"), null, "task.adventure.collect_ender_pearls"));
        allTasks.add(new KillMobTask(49, 49, 15, mc("wither_skeleton"), null, "task.adventure.kill_wither_skeletons"));
        allTasks.add(new CollectItemTask(50, 50, 3, mc("wither_skeleton_skull"), null, "task.adventure.collect_wither_skulls"));
        allTasks.add(new CraftItemTask(51, 51, 1, mc("nether_star"), null, "task.adventure.craft_nether_star"));
        allTasks.add(new CollectItemTask(52, 52, 30, mc("netherrack"), null, "task.adventure.collect_netherrack"));
        allTasks.add(new CraftItemTask(53, 53, 1, mc("nether_brick"), null, "task.adventure.craft_nether_brick"));
        allTasks.add(new KillMobTask(54, 54, 20, mc("zombified_piglin"), null, "task.adventure.kill_zombie_piglins_20"));
        allTasks.add(new CollectItemTask(55, 55, 50, mc("gold_nugget"), null, "task.adventure.collect_gold_nuggets"));
        allTasks.add(new CraftItemTask(56, 56, 1, mc("golden_apple"), null, "task.adventure.craft_golden_apple"));
        allTasks.add(new KillMobTask(57, 57, 10, mc("blaze"), null, "task.adventure.kill_blazes"));
        allTasks.add(new CollectItemTask(58, 58, 15, mc("magma_cream"), null, "task.adventure.collect_magma_cream"));
        allTasks.add(new CraftItemTask(59, 59, 1, mc("potion"), null, "task.adventure.craft_fire_resistance_potion"));
        allTasks.add(new CollectItemTask(60, 60, 20, mc("glowstone_dust"), null, "task.adventure.collect_glowstone_dust"));

        // Poziomy 61-80 (Ekspert) - continuing with key tasks
        allTasks.add(new CollectItemTask(61, 61, 10, mc("ancient_debris"), null, "task.adventure.collect_ancient_debris"));
        allTasks.add(new CraftItemTask(62, 62, 1, mc("netherite_ingot"), null, "task.adventure.craft_netherite_ingot"));
        allTasks.add(new CraftItemTask(63, 63, 1, mc("netherite_sword"), null, "task.adventure.craft_netherite_sword"));
        allTasks.add(new CraftItemTask(64, 64, 1, mc("netherite_chestplate"), null, "task.adventure.craft_netherite_armor"));
        allTasks.add(new KillMobTask(65, 65, 1, mc("ender_dragon"), null, "task.adventure.kill_ender_dragon"));
        allTasks.add(new CollectItemTask(66, 66, 20, mc("shulker_shell"), null, "task.adventure.collect_shulker_shells"));
        allTasks.add(new CraftItemTask(67, 67, 1, mc("shulker_box"), null, "task.adventure.craft_shulker_box"));
        allTasks.add(new CollectItemTask(68, 68, 1, mc("elytra"), null, "task.adventure.collect_elytra"));
        allTasks.add(new KillMobTask(69, 69, 5, mc("ravager"), null, "task.adventure.kill_ravagers"));
        allTasks.add(new KillMobTask(70, 70, 10, mc("pillager"), null, "task.adventure.kill_pillagers"));
        allTasks.add(new CollectItemTask(71, 71, 30, mc("end_stone"), null, "task.adventure.collect_end_stone"));
        allTasks.add(new CraftItemTask(72, 72, 1, mc("end_crystal"), null, "task.adventure.craft_end_crystal"));
        allTasks.add(new KillMobTask(73, 73, 20, mc("endermite"), null, "task.adventure.kill_endermites"));
        allTasks.add(new CollectItemTask(74, 74, 50, mc("chorus_fruit"), null, "task.adventure.collect_chorus_fruit"));
        allTasks.add(new CraftItemTask(75, 75, 1, mc("chorus_plant"), null, "task.adventure.craft_chorus_plant"));
        allTasks.add(new CollectItemTask(76, 76, 10, mc("netherite_scrap"), null, "task.adventure.collect_netherite_scraps"));
        allTasks.add(new CraftItemTask(77, 77, 1, mc("netherite_pickaxe"), null, "task.adventure.craft_netherite_tools"));
        allTasks.add(new KillMobTask(78, 78, 3, mc("wither"), null, "task.adventure.kill_wither_bosses"));
        allTasks.add(new CollectItemTask(79, 79, 100, mc("obsidian"), null, "task.adventure.collect_obsidian_100"));
        allTasks.add(new CraftItemTask(80, 80, 1, mc("ender_chest"), null, "task.adventure.craft_ender_chest"));

        // Poziomy 81-200 - generating remaining tasks with increasing difficulty
        for (int i = 81; i <= 200; i++) {
            int difficulty = i;
            int baseAmount = 50 + (i - 81) * 10;
            if (i % 4 == 1) {
                allTasks.add(new CollectItemTask(i, difficulty, baseAmount, mc("diamond"), null, "task.adventure.collect_diamond_" + i));
            } else if (i % 4 == 2) {
                allTasks.add(new CraftItemTask(i, difficulty, Math.max(1, baseAmount / 20), mc("beacon"), null, "task.adventure.craft_" + i));
            } else if (i % 4 == 3) {
                allTasks.add(new KillMobTask(i, difficulty, Math.max(1, baseAmount / 10), mc("zombie"), null, "task.adventure.kill_" + i));
            } else {
                allTasks.add(new CollectItemTask(i, difficulty, baseAmount, mc("iron_ingot"), null, "task.adventure.collect_iron_" + i));
            }
        }
    }

    public List<Task> getAllTasks() {
        return allTasks;
    }

    public Task getTask(int id) {
        if (id > 0 && id <= allTasks.size()) {
            return allTasks.get(id - 1);
        }
        return null;
    }
}
