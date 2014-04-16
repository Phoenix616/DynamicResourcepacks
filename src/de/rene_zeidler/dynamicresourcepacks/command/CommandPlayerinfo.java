package de.rene_zeidler.dynamicresourcepacks.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

import de.rene_zeidler.dynamicresourcepacks.DynamicResourcepacks;
import de.rene_zeidler.dynamicresourcepacks.Resourcepack;

public class CommandPlayerinfo extends DynamicResourcepacksCommand {

	public CommandPlayerinfo(DynamicResourcepacks plugin, String label,
			String dynamicResourcepacksAlias, String setresourcepackAlias,
			String[] args) {
		super(plugin, label, dynamicResourcepacksAlias, setresourcepackAlias, args);
	}

	@SuppressWarnings("deprecation")
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
		
		StringBuilder msg = new StringBuilder()
		
		.append(ChatColor.BLUE).     append("Showing info for player ")
		.append(ChatColor.DARK_AQUA).append(player.getName())
		.append(ChatColor.BLUE).     append(":\n")
		
		.append(ChatColor.DARK_AQUA).append("UUID: ")
		.append(ChatColor.AQUA)      .append(player.getUniqueId().toString())
		.append("\n")

		.append(ChatColor.DARK_AQUA);
		if(this.packManager.hasResourcepack(player)) {
			Resourcepack pack = this.packManager.getResourcepack(player);
			msg.append("Resourcepack: ")
			.append(ChatColor.AQUA).append(pack.getDisplayName())
			                       .append(" (").append(pack.getName()).append(")");
		} else {
			msg.append("No resourcepack selected");
		}
		msg.append("\n")
		
		.append(ChatColor.DARK_AQUA).append("Locked: ")
		.append(ChatColor.AQUA)     .append(this.packManager.getLocked(player))
		.append("\n")
		
		.append(ChatColor.DARK_AQUA).append("Permissions: ")
		.append(ChatColor.AQUA);
		if(player.hasPermission("dynamicresourcepacks.use.*")) {
			msg.append(ChatColor.ITALIC).append("All (*)");
		} else {
			int count = 0;
			for(Resourcepack pack : this.packManager.getResourcepacks()) {
				if(pack.checkGeneralPermission(player)) {
					if(pack.checkUseSelfPermission(player))
						msg.append(ChatColor.ITALIC);
					msg.append(pack.getName())
					.append(ChatColor.AQUA).append(", ");
					count++;
				}
			}
			if(count > 0)
				msg.replace(msg.length() - 2, msg.length(), " (")
				.append(count).append(")");
			else
				msg.append(ChatColor.ITALIC).append("None");
		}
		
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
