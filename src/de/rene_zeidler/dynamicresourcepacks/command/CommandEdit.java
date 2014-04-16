package de.rene_zeidler.dynamicresourcepacks.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permissible;

import de.rene_zeidler.dynamicresourcepacks.DynamicResourcepacks;
import de.rene_zeidler.dynamicresourcepacks.Resourcepack;
import de.rene_zeidler.dynamicresourcepacks.Resourcepack.Permission;
import de.rene_zeidler.dynamicresourcepacks.ResourcepackManager;

public class CommandEdit extends DynamicResourcepacksCommand {

	public CommandEdit(DynamicResourcepacks plugin, String label,
			String dynamicResourcepacksAlias, String setresourcepackAlias,
			String[] args) {
		super(plugin, label, dynamicResourcepacksAlias, setresourcepackAlias, args);
	}

	@Override
	public boolean run(CommandSender sender) {
		boolean canRename = sender.hasPermission("dynamicresourcepacks.rename");
		boolean canEdit   = sender.hasPermission("dynamicresourcepacks.edit");
		
		if(this.args.length != 3 && !(this.args.length > 3 && "displayName".equalsIgnoreCase(this.args[1]))) {
			if(canEdit)
				sender.sendMessage(ChatColor.RED + "Usage: /" + this.label + " <name> <property> <value>");
			else if(canRename)
				sender.sendMessage(ChatColor.RED + "Usage: /" + this.dynamicResourcepacksAlias + " rename <oldName> <newName>");
			else
				sender.sendMessage(ChatColor.RED + "You don't have permission!");
			return true;
		}
		
		if("name".equalsIgnoreCase(this.args[1])) {
			return new CommandRename(plugin, this.label + " name", this.dynamicResourcepacksAlias, this.setresourcepackAlias,  new String[] {this.args[0], this.args[2]}).run(sender);
		} else if(canEdit) {
			Resourcepack pack = this.getResourcepackForInputString(sender, this.args[0]);
			if(pack == null)
				return true;
			
			if("url".equalsIgnoreCase(this.args[1])) {
				if(ResourcepackManager.isValidURL(this.args[2])) {
					pack.setURL(this.args[2]);
					sender.sendMessage(ChatColor.GREEN      + "Successfully set the URL of " +
					                   ChatColor.DARK_GREEN + pack.getDisplayName() +
					                   ChatColor.GREEN      + " to " +
					                   ChatColor.DARK_GREEN + pack.getURL());
				} else {
					sender.sendMessage(ChatColor.RED + "The URL you entered is not a valid URL!");
					return true;
				}
				
			} else if("displayName".equalsIgnoreCase(this.args[1])) {
				StringBuilder displayName = new StringBuilder(this.args[2]);
				for(int i = 3; i < this.args.length; i++)
					displayName.append(" ").append(this.args[i]);
				pack.setDisplayName(displayName.toString());
				sender.sendMessage(ChatColor.GREEN      + "Successfully set the display name of " +
				                   ChatColor.DARK_GREEN + pack.getName() +
				                   ChatColor.GREEN      + " to " +
				                   ChatColor.DARK_GREEN + pack.getDisplayName());
				
			} else if("generalPermission".equalsIgnoreCase(this.args[1]) ||
					  "permission"       .equalsIgnoreCase(this.args[1]) ||
					  "useSelfPermission".equalsIgnoreCase(this.args[1]) ||
					  "selfPermission"   .equalsIgnoreCase(this.args[1])) {
				Permission perm = this.getResourcepackPermission(this.args[2]);
				if(perm == null) {
					sender.sendMessage(ChatColor.RED + "The permission must be either NONE, GENERAL or SPECIFIC!");
					return true;
				}
				
				if("generalPermission".equalsIgnoreCase(this.args[1]) ||
				   "permission"       .equalsIgnoreCase(this.args[1])) {
					pack.setGeneralPermission(perm);
					sender.sendMessage(ChatColor.GREEN      + "Successfully set the general permission of " +
			                           ChatColor.DARK_GREEN + pack.getDisplayName() +
			                           ChatColor.GREEN      + " to " +
			                           ChatColor.DARK_GREEN + pack.getGeneralPermission().toString());
				} else {
					pack.setUseSelfPermission(perm);
					sender.sendMessage(ChatColor.GREEN      + "Successfully set the use self permission of " +
	                                   ChatColor.DARK_GREEN + pack.getDisplayName() +
	                                   ChatColor.GREEN      + " to " +
	                                   ChatColor.DARK_GREEN + pack.getUseSelfPermission().toString());
				}
			
			} else {
				sender.sendMessage(ChatColor.RED + "Unknown property \"" + this.args[1] + "\"!");
				return true;
			}
			
			this.packManager.saveConfigPacks();
			this.plugin.saveConfig();
			
		} else {
			sender.sendMessage(ChatColor.RED + "You don't have permission!");
		}
		
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender) {
		if(this.args.length == 1) {
			return this.completeResourcepack(sender, this.args[0]);
		} else if(this.args.length == 2) {
			List<String> completions = new ArrayList<String>();
			this.addCompletions(completions, this.args[1], "name", "url", "displayName");
			this.addCommandCompletions(completions, this.args[1], "generalPermission", "permission");
			this.addCommandCompletions(completions, this.args[1], "useSelfPermission", "selfPermission");
			return completions;
		} else if(this.args.length > 2) {
			if("url".equalsIgnoreCase(this.args[1]))
				return this.completeURL(sender, this.args[2]);
			else if("displayName".equalsIgnoreCase(this.args[1]))
				sender.sendMessage(ChatColor.GOLD + "Please enter the new display name (can contain spaces)");
			else if("generalPermission".equalsIgnoreCase(this.args[1]) ||
					"permission"       .equalsIgnoreCase(this.args[1]) ||
					"useSelfPermission".equalsIgnoreCase(this.args[1]) ||
					"selfPermission"   .equalsIgnoreCase(this.args[1]))
				return this.completePermission(this.args[2]);
		}
		
		return new ArrayList<String>();
	}

	public static boolean canSee(Permissible permissible) {
		return permissible.hasPermission("dynamicresourcepacks.edit");
	}
}
