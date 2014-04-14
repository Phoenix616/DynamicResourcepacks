package de.rene_zeidler.dynamicresourcepacks.command;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import de.rene_zeidler.dynamicresourcepacks.DynamicResourcepacks;
import de.rene_zeidler.dynamicresourcepacks.Resourcepack;
import de.rene_zeidler.dynamicresourcepacks.Resourcepack.Permission;
import de.rene_zeidler.dynamicresourcepacks.ResourcepackManager;

public class CommandCreate extends DynamicResourcepacksCommand {

	public CommandCreate(DynamicResourcepacks plugin, String label,
			String dynamicResourcepacksAlias, String setresourcepackAlias,
			String[] args) {
		super(plugin, label, dynamicResourcepacksAlias, setresourcepackAlias, args);
	}

	@Override
	public boolean run(CommandSender sender) {
		if(!sender.hasPermission("dynamicresourcepacks.create")) {
			sender.sendMessage(ChatColor.RED + "You don't have permission!");
			return true;
		}
		
		if(this.args.length >= 2) {
			if(!ResourcepackManager.isValidURL(this.args[1])) {
				sender.sendMessage(ChatColor.RED + "The URL you entered is not a valid URL!");
				return true;
			}
			
			Resourcepack pack = new Resourcepack(this.args[0], this.args[1], sender.getName());
			if(this.args.length >= 3)
				pack.setDisplayName(this.args[2]);
			if (this.args.length >= 4) {
				Permission perm = Permission.valueOf(this.args[3].toUpperCase());
				if(perm == null) {
					sender.sendMessage(ChatColor.RED + "The general permission must be either NONE, GENERAL or SPECIFIC!");
					return true;
				}
				pack.setGeneralPermission(perm);
			}
			if (this.args.length >= 5) {
				Permission perm = Permission.valueOf(this.args[4].toUpperCase());
				if(perm == null) {
					sender.sendMessage(ChatColor.RED + "The use self permission must be either NONE, GENERAL or SPECIFIC!");
					return true;
				}
				pack.setUseSelfPermission(perm);
			}
			
			this.packManager.addResourcepack(pack);
			
			sender.sendMessage(ChatColor.GREEN + "Successfully added resourcepack");
			this.printPackInfo(sender, pack);

		} else {
			sender.sendMessage(ChatColor.RED + "Usage: /" + this.label + " <name> <url> [displayName] [generalPermission] [useSelfPermission]");
		}
		
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender) {
		//TODO: tab complete
		return null;
	}
}
