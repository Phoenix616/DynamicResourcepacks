package de.rene_zeidler.dynamicresourcepacks.command;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import de.rene_zeidler.dynamicresourcepacks.DynamicResourcepacks;

public class CommandHelp extends DynamicResourcepacksCommand {
	
	public CommandHelp(DynamicResourcepacks plugin, String label,
			String dynamicResourcepacksAlias, String setresourcepackAlias,
			String[] args) {
		super(plugin, label, dynamicResourcepacksAlias, setresourcepackAlias, args);
	}

	@Override
	public boolean run(CommandSender sender) {
		//TODO: canSee
		StringBuilder msg = new StringBuilder();
		
		boolean foundTopic = false;
		if(this.args.length > 0) {
			if("aliases".equalsIgnoreCase(this.args[0])) {
				foundTopic = true;
				msg.append(ChatColor.GOLD);
				msg.append(ChatColor.ITALIC);
				msg.append("Showing aliases for DynamicResourcepacks commands:\n");
				
				msg.append(ChatColor.DARK_AQUA);
				msg.append("/dynamicresourcepacks");
				for(String alias : this.plugin.getCommand("dynamicresourcepacks").getAliases())
					this.appendAlias(msg, alias);
				msg.append("\n");
				
				
				this.appendAliases(msg, this.dynamicResourcepacksAlias, "help", "?");
				if(sender.hasPermission("dynamicresourcepacks.view.selected"))
					this.appendAliases(msg, this.dynamicResourcepacksAlias, "view", "show", "info");
				if(sender.hasPermission("dynamicresourcepacks.created"))
					this.appendAliases(msg, this.dynamicResourcepacksAlias, "create", "add");
				if(sender.hasPermission("dynamicresourcepacks.remove"))
					this.appendAliases(msg, this.dynamicResourcepacksAlias, "remove", "delete");
				this.appendAliases(msg, this.dynamicResourcepacksAlias, "version", "ver");
				
				msg.append(ChatColor.BLUE);
				msg.append("/setresourcepack");
				for(String alias : this.plugin.getCommand("setresourcepack").getAliases())
					this.appendAlias(msg, alias);
				msg.append("\n");
				
				this.appendAliases(msg, this.setresourcepackAlias, "...", this.dynamicResourcepacksAlias + " use ...", this.dynamicResourcepacksAlias + " switch ...");
			}
		}
		
		if(!foundTopic) {
			msg.append(ChatColor.GOLD);
			msg.append(ChatColor.ITALIC);
			msg.append("Showing help for DynamicResourcepacks:\n");
			
			this.appendHelpEntry(msg, this.dynamicResourcepacksAlias, "help",           "Shows a list of available commands");
			this.appendHelpEntry(msg, this.dynamicResourcepacksAlias, "help aliases",   "Shows a list of available command aliases");
			
			this.appendHelpEntry(msg, this.dynamicResourcepacksAlias, "list",          "Lists all available resourcepacks",                      sender, "dynamicresourcepacks.list.selectable");
			this.appendHelpEntry(msg, this.dynamicResourcepacksAlias, "list <player>", "Lists all for the given player available resourcepacks", sender, "dynamicresourcepacks.list.others");
			this.appendHelpEntry(msg, this.dynamicResourcepacksAlias, "view",          "Shows which resourcepack you have currently selected",   sender, "dynamicresourcepacks.view.selected"  );
			this.appendHelpEntry(msg, this.dynamicResourcepacksAlias, "view <pack>",   "Shows information on the given resourcepack",            sender, "dynamicresourcepacks.view.selectable");
			
			this.appendHelpEntry(msg, this.dynamicResourcepacksAlias, "create <name> <url> [displayName] [generalPermission] [useSelfPermission]",
					                                                              null, sender, "dynamicresourcepacks.create"      );
			this.appendHelpEntry(msg, this.dynamicResourcepacksAlias, "rename <oldName> <newName>",    null, sender, "dynamicresourcepacks.rename"      );
			this.appendHelpEntry(msg, this.dynamicResourcepacksAlias, "edit <name> <setting> <value>", null, sender, "dynamicresourcepacks.edit"        );
			this.appendHelpEntry(msg, this.dynamicResourcepacksAlias, "remove <name>",                 null, sender, "dynamicresourcepacks.remove"      );
			this.appendHelpEntry(msg, this.dynamicResourcepacksAlias, "lock <player>",                 null, sender, "dynamicresourcepacks.setpack.lock");
			this.appendHelpEntry(msg, this.dynamicResourcepacksAlias, "unlock <player>",               null, sender, "dynamicresourcepacks.unlock"      );
			
			this.appendHelpEntry(msg, this.dynamicResourcepacksAlias, "version", "Show the version of this plugin");
			
			boolean canLock = sender.hasPermission("dynamicresourcepacks.setpack.lock");
			this.appendHelpEntry(msg, this.setresourcepackAlias, "<pack>" + (canLock ? " [lock]" : ""), "Use the given resourcepack");
			this.appendHelpEntry(msg, this.setresourcepackAlias, "<player> <pack>" + (canLock ? " [lock]" : ""), "Sets the resourcepack of a given player", sender, "dynamicresourcepacks.setpack.others");
		}
	
		sender.sendMessage(msg.toString());
		
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender) {
		//TODO: tab complete
		return null;
	}
	
	public void appendAliases(StringBuilder stringBuilder, String mainCommand, String mainAlias, String... aliases) {
		stringBuilder.append(ChatColor.BLUE);
		stringBuilder.append(mainCommand);
		stringBuilder.append(' ');
		stringBuilder.append(ChatColor.DARK_AQUA);
		stringBuilder.append(mainAlias);
		for(String alias : aliases)
			this.appendAlias(stringBuilder, alias);
		stringBuilder.append("\n");
	}
	
	public void appendAlias(StringBuilder stringBuilder, String alias) {
		stringBuilder.append(ChatColor.DARK_GRAY);
		stringBuilder.append("/");
		stringBuilder.append(ChatColor.AQUA);
		stringBuilder.append(alias);
	}
	
	public void appendHelpEntry(StringBuilder stringBuilder, String mainCommand, String commandArgs, String description) {
		this.appendHelpEntry(stringBuilder, mainCommand, commandArgs, description, null, null);
	}
	
	public void appendHelpEntry(StringBuilder stringBuilder, String mainCommand, String commandArgs, String description, CommandSender sender, String permission) {
		if(permission == null || permission.isEmpty() || (sender != null && sender.hasPermission(permission))) {
			stringBuilder.append(ChatColor.BLUE);
			stringBuilder.append(mainCommand);
			if(commandArgs != null) {
				stringBuilder.append(' ');
				stringBuilder.append(commandArgs);
			}
			if(description != null) {
				stringBuilder.append(ChatColor.DARK_AQUA);
				stringBuilder.append(" - ");
				stringBuilder.append(description);
			}
			stringBuilder.append("\n");
		}
	}
	
}
