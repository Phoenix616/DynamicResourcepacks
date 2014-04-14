package de.rene_zeidler.dynamicresourcepacks.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permissible;

import de.rene_zeidler.dynamicresourcepacks.DynamicResourcepacks;
import de.rene_zeidler.dynamicresourcepacks.Resourcepack;

public class CommandRemove extends DynamicResourcepacksCommand {

	public CommandRemove(DynamicResourcepacks plugin, String label,
			String dynamicResourcepacksAlias, String setresourcepackAlias,
			String[] args) {
		super(plugin, label, dynamicResourcepacksAlias, setresourcepackAlias, args);
	}

	@Override
	public boolean run(CommandSender sender) {
		if(!sender.hasPermission("dynamicresourcepacks.remove")) {
			sender.sendMessage(ChatColor.RED + "You don't have permission!");
			return true;
		}

		if(this.args.length == 1) {
			Resourcepack pack = this.getResourcepackForInputString(sender, this.args[0]);
			if(pack != null) {
				this.packManager.removeResourcepack(pack);
				sender.sendMessage(ChatColor.GREEN      + "Successfully removed the resourcepack " +
						           ChatColor.DARK_GREEN + pack.getDisplayName());
				this.packManager.saveConfig();
				this.plugin.saveConfig();
			}
		} else {
			sender.sendMessage(ChatColor.RED + "Usage: /" + this.label + " <name>");
		}
		
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender) {
		if(this.args.length == 1)
			return this.completeResourcepack(sender, this.args[0]);
		else
			return new ArrayList<String>();
	}
	
	public static boolean canSee(Permissible permissible) {
		return permissible.hasPermission("dynamicresourcepacks.remove");
	}
}
