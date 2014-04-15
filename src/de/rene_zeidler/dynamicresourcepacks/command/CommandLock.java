package de.rene_zeidler.dynamicresourcepacks.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

import de.rene_zeidler.dynamicresourcepacks.DynamicResourcepacks;

public class CommandLock extends DynamicResourcepacksCommand {

	public CommandLock(DynamicResourcepacks plugin, String label,
			String dynamicResourcepacksAlias, String setresourcepackAlias,
			String[] args) {
		super(plugin, label, dynamicResourcepacksAlias, setresourcepackAlias, args);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean run(CommandSender sender) {
		if(!sender.hasPermission("dynamicresourcepacks.setpack.lock")) {
			sender.sendMessage(ChatColor.RED + "You don't have permission!");
			return true;
		}

		Player player;
		if(this.args.length == 0) {
			if(sender instanceof Player)
				player = (Player)sender;
			else {
				sender.sendMessage(ChatColor.RED + "Usage: /" + this.label + " <player>");
				return true;
			}
		} else {
			player = sender.getServer().getPlayer(this.args[0]);
			if(player == null) {
				sender.sendMessage(ChatColor.RED + "There is no online player named " + this.args[0]);
				return true;
			}
		}
		
		if(this.packManager.getLocked(player))
			sender.sendMessage(ChatColor.GOLD + "The resourcepack of " + player.getName() + " is already locked");
		else {
			this.packManager.setLocked(player, true);
			sender.sendMessage(ChatColor.GREEN + "Locked the resourcepack of " + player.getName());
		}
		
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender) {
		if(this.args.length == 1)
			return null; //Complete names
		else
			return new ArrayList<String>();
	}

	public static boolean canSee(Permissible permissible) {
		return permissible.hasPermission("dynamicresourcepacks.setpack.lock");
	}
}
