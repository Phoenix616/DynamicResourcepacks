package de.rene_zeidler.dynamicresourcepacks.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

import de.rene_zeidler.dynamicresourcepacks.DynamicResourcepacks;

public class CommandPlayerinfo extends DynamicResourcepacksCommand {

	public CommandPlayerinfo(DynamicResourcepacks plugin, String label,
			String dynamicResourcepacksAlias, String setresourcepackAlias,
			String[] args) {
		super(plugin, label, dynamicResourcepacksAlias, setresourcepackAlias, args);
	}

	@Override
	public boolean run(CommandSender sender) {
		if(!sender.hasPermission("dynamicresourcepacks.playerinfo")) {
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
		
		StringBuilder msg = new StringBuilder();
		msg.append(ChatColor.BLUE);
		msg.append("Showing info for player ");
		msg.append(ChatColor.DARK_AQUA);
		msg.append(player.getName());
		msg.append(ChatColor.BLUE);
		msg.append(":\n");
		
		msg.append(ChatColor.DARK_AQUA);
		msg.append("UUID: ");
		msg.append(ChatColor.AQUA);
		msg.append(player.getUniqueId().toString());
		msg.append("\n");

		msg.append(ChatColor.DARK_AQUA);
		if(this.packManager.hasResourcepack(player)) {
			msg.append("Resourcepack: ");
			msg.append(ChatColor.AQUA);
			msg.append(this.packManager.getResourcepack(player));
		} else {
			msg.append("No resourcepack selected");
		}
		msg.append("\n");
		
		msg.append(ChatColor.DARK_AQUA);
		msg.append("Locked: ");
		msg.append(ChatColor.AQUA);
		msg.append(this.packManager.getLocked(player));
		
		sender.sendMessage(msg.toString());
		
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
		return permissible.hasPermission("dynamicresourcepacks.playerinfo");
	}
}
