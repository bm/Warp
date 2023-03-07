package com.gmail.brysonm26.warp.commands;


import com.gmail.brysonm26.warp.Warp;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class GoCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            UUID uuid = player.getUniqueId();
            Warp instance = Warp.getInstance();
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("list")) {
                    // LIST subcommand
                    Set<String> warpSet = new HashSet<String>();
                    if (instance.getWarps().containsKey(uuid)) {
                        warpSet.addAll(instance.getWarps(uuid).keySet());
                    }
                    player.sendMessage(ChatColor.GRAY + "Warps (" + warpSet.size() + "):");
                    player.sendMessage(ChatColor.GRAY + String.join(", ", warpSet));
                } else if (args[0].equalsIgnoreCase("help")) {
                    // HELP subcommand
                    player.sendMessage(ChatColor.RED + "/go <warp> - TP to a specific warp");
                    player.sendMessage(ChatColor.RED + "/go list - View list of your warps");
                    player.sendMessage(ChatColor.RED + "/go set <warp> - Sets a warp at your current location");
                    player.sendMessage(ChatColor.RED + "/go del <warp> - Deletes a warp");
                } else {
                    // TP
                    String warpName = args[0];
                    if (!instance.getWarps().containsKey(uuid) || !instance.getWarps().get(uuid).containsKey(warpName)) {
                        player.sendMessage(ChatColor.RED + "Warp " + warpName + " does not exist.");
                        return false;
                    }
                    Location loc = instance.getWarps().get(uuid).get(warpName);
                    player.teleport(loc);
                    player.sendMessage(ChatColor.GRAY + "Teleported to warp " + warpName);
                }
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("set")) {
                    // SET subcommand
                    String warpName = args[1];
                    if (warpName.matches("^.*[^a-zA-Z0-9 ].*$")) {
                        player.sendMessage(ChatColor.RED + "Use alphanumeric characters only to name your warps!");
                        return false;
                    }
                    instance.setWarp(uuid, warpName, player.getLocation());
                    player.sendMessage(ChatColor.GRAY + "Set warp " + warpName);
                } else if (args[0].equalsIgnoreCase("del")) {
                    // DEL subcommand
                    String warpName = args[1];
                    if (!instance.getWarps().get(uuid).containsKey(warpName)) {
                        player.sendMessage(ChatColor.RED + "Warp " + warpName + " does not exist.");
                        return false;
                    }
                    instance.deleteWarp(uuid, warpName);
                    player.sendMessage(ChatColor.GRAY + "Deleted warp " + warpName);
                }
            } else {
                player.sendMessage(ChatColor.RED + "/go <warp> - TP to a specific warp");
                player.sendMessage(ChatColor.RED + "/go list - View list of your warps");
                player.sendMessage(ChatColor.RED + "/go set <warp> - Sets a warp at your current location");
                player.sendMessage(ChatColor.RED + "/go del <warp> - Deletes a warp");
                return false;
            }
        }
        return false;
    }
}
