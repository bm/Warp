package com.gmail.brysonm26.warp;

import com.gmail.brysonm26.warp.commands.GoCommand;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public final class Warp extends JavaPlugin {

    private static Warp instance;
    private static HashMap<UUID, HashMap<String, Location>> warps;

    public static Warp getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        instance = this;
        warps = new HashMap<UUID, HashMap<String, Location>>();
        loadWarps();
        getCommand("go").setExecutor(new GoCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        saveWarps();
    }

    private void loadWarps() {
        Set<String> uuidSet = getConfig().getKeys(false);
        for (String uuid : uuidSet) {
            Set<String> warpSet = getConfig().getConfigurationSection(uuid).getKeys(false);
            HashMap<String, Location> warpMap = new HashMap<String, Location>();
            for (String warp : warpSet) {
                String path = uuid + "." + warp;
                World world = getServer().getWorld(getConfig().getString(path + ".world"));
                double x = getConfig().getDouble(path + ".x");
                double y = getConfig().getDouble(path + ".y");
                double z = getConfig().getDouble(path + ".z");
                float yaw = (float) getConfig().getDouble(path + ".yaw");
                float pitch = (float) getConfig().getDouble(path + ".pitch");
                Location loc = new Location(world, x, y, z, yaw, pitch);
                warpMap.put(warp, loc);
            }
            warps.put(UUID.fromString(uuid), warpMap);
        }
    }

    private void saveWarps() {
        // clear config
        for (String s : getConfig().getKeys(false)) {
            getConfig().set(s, null);
        }
        // set values from hashmap
        if (warps.isEmpty()) {
            saveConfig();
            return;
        }
        for (UUID uuid : warps.keySet()) {
            HashMap<String, Location> warpMap = getWarps(uuid);
            //todo NPE checks
            for (String warpName : warpMap.keySet()) {
                String path = uuid.toString() + "." + warpName;
                Location loc = warpMap.get(warpName);
                getConfig().set(path + ".world", loc.getWorld().getName());
                getConfig().set(path + ".x", loc.getX());
                getConfig().set(path + ".y", loc.getY());
                getConfig().set(path + ".z", loc.getZ());
                getConfig().set(path + ".yaw", loc.getYaw());
                getConfig().set(path + ".pitch", loc.getPitch());
            }
        }
        saveConfig();;
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

    public HashMap<String, Location> getWarps(UUID uuid) {
        return warps.get(uuid);
    }
}
