package com.untamedears.JukeAlert.command.commands;

import com.untamedears.JukeAlert.util.Utility;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import vg.civcraft.mc.civmodcore.command.PlayerCommand;
import vg.civcraft.mc.namelayer.permission.PermissionType;
import vg.civcraft.mc.namelayer.NameAPI;

import com.untamedears.JukeAlert.JukeAlert;
import com.untamedears.JukeAlert.model.Snitch;

public class NameCommand extends PlayerCommand {

	public NameCommand() {

		super("Name");
		setDescription("Set snitch name");
		setUsage("/janame <name> [<x> <y> <z> [<world>]]");
		setArguments(1, 5);
		setIdentifier("janame");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {

		if (!(sender instanceof Player)) {
			sender.sendMessage(
				ChatColor.RED + "You do not own any snitches nearby or lack permission to rename them!");
			return false;
		}
		Player player = (Player) sender;
		Snitch snitch;
		if (args.length == 1) {
			snitch = Utility.findLookingAtOrClosestSnitch(
				player, PermissionType.getPermission("RENAME_SNITCH"));
			if (snitch == null) {
				sender.sendMessage(
					ChatColor.RED + "You do not own any snitches nearby or lack permission to rename them!");
				return false;
			}
		} else {
			int x;
			int y;
			int z;
			String world;
			try {
				x = Integer.parseInt(args[1]);
				y = Integer.parseInt(args[2]);
				z = Integer.parseInt(args[3]);
				if (args.length == 4) {
					world = player.getLocation().getWorld().getName();
				} else {
					world = args[4];
				}
			} catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
				sender.sendMessage(ChatColor.RED + "Invalid coordinates.");
				return false;
			}
			if (Bukkit.getWorld(world) == null) {
				sender.sendMessage(ChatColor.RED + "Invalid world.");
				return false;
			}
			Location loc = new Location(Bukkit.getWorld(world), x, y, z);
			snitch = JukeAlert.getInstance().getSnitchManager().getSnitch(loc.getWorld(), loc);
			if (snitch == null
					|| !Utility.doesSnitchExist(snitch, true)
					|| !NameAPI.getGroupManager().hasAccess(
						snitch.getGroup(),
						player.getUniqueId(),
						PermissionType.getPermission("RENAME_SNITCH"))) {
				sender.sendMessage(
					ChatColor.RED + "You do not own a snitch at those coordinates or lack permission to rename it!");
				return false;
			}
		}

		String name = "";
		if (args[0].length() > 40) {
			name = args[0].substring(0, 40);
		} else {
			name = args[0];
		}
		String prevName = snitch.getName();
		JukeAlert plugin = JukeAlert.getInstance();
		plugin.getJaLogger().updateSnitchName(snitch, name);
		snitch.setName(name);

		TextComponent lineText = new TextComponent(ChatColor.AQUA + " Changed snitch name to " + name);
		String hoverText = snitch.getHoverText(prevName, null);
		lineText.setHoverEvent(
			new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverText).create()));
		player.spigot().sendMessage(lineText);
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {

		return null;
	}
}
