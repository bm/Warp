package com.gmail.brysonm26.warp;

import com.gmail.brysonm26.warp.commands.GoCommand;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public final class Warp extends JavaPlugin {

    private static Warp instance;
    private static HashMap<UUID, HashMap<String, Location>> warps;

    public static Warp getInstance() {
        return instance;
    }

    private File warpsFile = new File(getDataFolder(), "warps.yml");
    private FileConfiguration warpsConfig = YamlConfiguration.loadConfiguration(warpsFile);;

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        instance = this;
        warps = new HashMap<UUID, HashMap<String, Location>>();
        if (!warpsFile.exists()) {
            saveResource("warps.yml", false);
        }

        loadWarps();
        getCommand("go").setExecutor(new GoCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        saveWarps();
    }

    private void loadWarps() {
        Set<String> uuidSet = warpsConfig.getKeys(false);
        for (String uuid : uuidSet) {
            Set<String> warpSet = warpsConfig.getConfigurationSection(uuid).getKeys(false);
            HashMap<String, Location> warpMap = new HashMap<String, Location>();
            for (String warp : warpSet) {
                String path = uuid + "." + warp;
                World world = getServer().getWorld(warpsConfig.getString(path + ".world"));
                double x = warpsConfig.getDouble(path + ".x");
                double y = warpsConfig.getDouble(path + ".y");
                double z = warpsConfig.getDouble(path + ".z");
                float yaw = (float) warpsConfig.getDouble(path + ".yaw");
                float pitch = (float) warpsConfig.getDouble(path + ".pitch");
                Location loc = new Location(world, x, y, z, yaw, pitch);
                warpMap.put(warp, loc);
            }
            warps.put(UUID.fromString(uuid), warpMap);
        }
    }

    private void saveWarps() {
        // clear config
        for (String s : warpsConfig.getKeys(false)) {
            warpsConfig.set(s, null);
        }
        // set values from hashmap
        if (warps.isEmpty()) {
            try {
                warpsConfig.save(warpsFile);
            } catch (IOException ex) {
                ex.printStackTrace();;
            }
            return;
        }
        for (UUID uuid : warps.keySet()) {
            HashMap<String, Location> warpMap = getWarps(uuid);
            //todo NPE checks
            for (String warpName : warpMap.keySet()) {
                String path = uuid.toString() + "." + warpName;
                Location loc = warpMap.get(warpName);
                warpsConfig.set(path + ".world", loc.getWorld().getName());
                warpsConfig.set(path + ".x", loc.getX());
                warpsConfig.set(path + ".y", loc.getY());
                warpsConfig.set(path + ".z", loc.getZ());
                warpsConfig.set(path + ".yaw", loc.getYaw());
                warpsConfig.set(path + ".pitch", loc.getPitch());
            }
        }
        try {
            warpsConfig.save(warpsFile);
        } catch (IOException ex) {
            ex.printStackTrace();;
        }
    }

    public HashMap<UUID, HashMap<String, Location>> getWarps() {
        return warps;
    }

    public void setWarp(UUID uuid, String warpName, Location loc) {
        if (warps.containsKey(uuid)) {
            warps.get(uuid).put(warpName, loc);
        } else {
            HashMap<String, Location> map = new HashMap<String, Location>();
            map.put(warpName, loc);
            warps.put(uuid, map);
        }
    }

    public void deleteWarp(UUID uuid, String warpName) {
        if (warps.containsKey(uuid)) {
            warps.get(uuid).remove(warpName);
        } else {
            warps.put(uuid, new HashMap<String, Location>());
        }
    }

    public boolean hasWarp(UUID uuid, String warpName) {
        if (!warps.containsKey(uuid)) {
            return false;
        } else {
            return warps.get(uuid).containsKey(warpName);
        }
    }

    public HashMap<String, Location> getWarps(UUID uuid) {
        return warps.get(uuid);
    }

    public File getWarpsFile() {
        return warpsFile;
    }

    public FileConfiguration getWarpsConfig() {
        return warpsConfig;
    }
}
