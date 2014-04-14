package de.rene_zeidler.dynamicresourcepacks.command;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permissible;

import de.rene_zeidler.dynamicresourcepacks.DynamicResourcepacks;
import de.rene_zeidler.dynamicresourcepacks.Resourcepack;

public class CommandRename extends DynamicResourcepacksCommand {

	public CommandRename(DynamicResourcepacks plugin, String label,
			String dynamicResourcepacksAlias, String setresourcepackAlias,
			String[] args) {
		super(plugin, label, dynamicResourcepacksAlias, setresourcepackAlias, args);
	}

	@Override
	public boolean run(CommandSender sender) {
		if(!sender.hasPermission("dynamicresourcepacks.edit")) {
			sender.sendMessage(ChatColor.RED + "You don't have permission!");
			return true;
		}
		
		if(args.length == 2) {
			Resourcepack pack = this.getResourcepackForInputString(sender, args[0]);
			if(pack != null) {
				this.packManager.renameResourcepack(pack, args[1]);
				sender.sendMessage(ChatColor.GREEN      + "Successfully set the name (id) of " +
                                   ChatColor.DARK_GREEN + pack.getDisplayName() +
                                   ChatColor.GREEN      + " to " +
                                   ChatColor.DARK_GREEN + pack.getName());
				this.packManager.saveConfig();
				this.plugin.saveConfig();
			}
		} else {
			sender.sendMessage(ChatColor.RED + "Usage: /" + this.label + " <oldName> <newName>");
		}
		
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender) {
		//TODO: tab complete
		return null;
	}
	
	public static boolean canSee(Permissible permissible) {
		return permissible.hasPermission("dynamicresourcepacks.rename");
	}
}
