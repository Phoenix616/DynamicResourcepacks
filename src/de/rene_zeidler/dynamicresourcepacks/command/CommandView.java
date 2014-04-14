package de.rene_zeidler.dynamicresourcepacks.command;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permissible;

import de.rene_zeidler.dynamicresourcepacks.DynamicResourcepacks;
import de.rene_zeidler.dynamicresourcepacks.Resourcepack;

public class CommandView extends DynamicResourcepacksCommand {

	public CommandView(DynamicResourcepacks plugin, String label,
			String dynamicResourcepacksAlias, String setresourcepackAlias,
			String[] args) {
		super(plugin, label, dynamicResourcepacksAlias, setresourcepackAlias, args);
	}

	@Override
	public boolean run(CommandSender sender) {
		if(this.args.length == 0) {
			this.printCurrentPackInfo(sender);
			return true;
		}
		
		Resourcepack pack = this.getResourcepackForInputString(sender, this.args[0]);
		if(pack != null) {
			if(sender.hasPermission("dynamicresourcepacks.view.all")
			|| sender.hasPermission("dynamicresourcepacks.view.usable") && pack.checkGeneralPermission(sender)
			|| sender.hasPermission("dynamicresourcepacks.view.selectable") && pack.checkUseSelfPermission(sender))
				this.printPackInfo(sender, pack);
			else
				sender.sendMessage(ChatColor.RED + "You don't have permission!");
		}
		
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender) {
		//TODO: tab complete
		return null;
	}
	
	public static boolean canSee(Permissible permissible) {
		return permissible.hasPermission("dynamicresourcepacks.view.selectable");
	}
	
}
