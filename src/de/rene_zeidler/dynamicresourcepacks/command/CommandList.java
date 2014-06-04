package de.rene_zeidler.dynamicresourcepacks.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

import de.rene_zeidler.dynamicresourcepacks.DynamicResourcepacks;
import de.rene_zeidler.dynamicresourcepacks.resourcepacks.Resourcepack;

public class CommandList extends DynamicResourcepacksCommand {

	public CommandList(DynamicResourcepacks plugin, String label,
			String dynamicResourcepacksAlias, String setresourcepackAlias,
			String[] args) {
		super(plugin, label, dynamicResourcepacksAlias, setresourcepackAlias, args);
	}

	@Override
	public boolean run(CommandSender sender) {
		if(this.args.length == 0 || !sender.hasPermission("dynamicresourcepacks.list.others"))
			this.printResourcepackList(sender, sender);
		else {
			@SuppressWarnings("deprecation")
			Player player = sender.getServer().getPlayer(this.args[0]);
			if(player != null)
				this.printResourcepackList(sender, player);
			else
				sender.sendMessage(ChatColor.RED + "There is no online player named " + this.args[0]);
		}
		
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender) {
		if(this.args.length == 1 && sender.hasPermission("dynamicresourcepacks.list.others"))
			return null; //Complete names
		else
			return new ArrayList<String>();
	}
	
	public static boolean canSee(Permissible permissible) {
		return permissible.hasPermission("dynamicresourcepacks.list.selectable");
	}
	
	public void printResourcepackList(CommandSender sender, Permissible player) {
		List<Resourcepack> visiblePacks;
		
		if(player.hasPermission("dynamicresourcepacks.list.all"))
			visiblePacks = this.packManager.getResourcepacks();
		else if(player.hasPermission("dynamicresourcepacks.list.usable"))
			visiblePacks = this.packManager.getUsableResourcepacks(player);
		else if(player.hasPermission("dynamicresourcepacks.list.selectable"))
			visiblePacks = this.packManager.getSelectableResourcepacks(player);
		else {
			if(sender == player)
				sender.sendMessage(ChatColor.RED + "You don't have permission!");
			else
				sender.sendMessage(ChatColor.RED + "The player doesn't have permission to view the list of resourcepacks!");
			return;
		}
		
		if(visiblePacks.size() == 0) {
			sender.sendMessage(ChatColor.RED + "There are currently no available resourcepacks!");
		} else {
			Collections.sort(visiblePacks);
			sender.sendMessage(ChatColor.GOLD + "Available resourcepacks (" + visiblePacks.size() + "):");
			for(Resourcepack pack : visiblePacks)
				sender.sendMessage(ChatColor.BLUE + pack.getDisplayName()
						+ ChatColor.DARK_AQUA + " (" + pack.getName() + ")");
		}
	}

}
