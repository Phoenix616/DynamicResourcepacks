package de.rene_zeidler.dynamicresourcepacks.command;

import java.util.ArrayList;
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
				if(CommandView.  canSee(sender)) this.appendAliases(msg, this.dynamicResourcepacksAlias, "view", "show", "info");
				if(CommandCreate.canSee(sender)) this.appendAliases(msg, this.dynamicResourcepacksAlias, "create", "add");
				if(CommandRemove.canSee(sender)) this.appendAliases(msg, this.dynamicResourcepacksAlias, "remove", "delete");
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
			
			if(CommandList.canSee(sender)) {
				this.appendHelpEntry(msg, this.dynamicResourcepacksAlias, "list", "Lists all available resourcepacks");
				if(sender.hasPermission("dynamicresourcepacks.list.others"))
					this.appendHelpEntry(msg, this.dynamicResourcepacksAlias, "list <player>", "Lists all for the given player available resourcepacks");	
			}
			if(CommandView.canSee(sender)) {
				this.appendHelpEntry(msg, this.dynamicResourcepacksAlias, "view", "Shows which resourcepack you have currently selected");
				if(sender.hasPermission("dynamicresourcepacks.view.selectable"))
					this.appendHelpEntry(msg, this.dynamicResourcepacksAlias, "view <pack>", "Shows information on the given resourcepack");
			}
			
			if(CommandCreate.canSee(sender)) this.appendHelpEntry(msg, this.dynamicResourcepacksAlias, "create <name> <url> [displayName] [generalPermission] [useSelfPermission]");
			if(CommandRename.canSee(sender)) this.appendHelpEntry(msg, this.dynamicResourcepacksAlias, "rename <oldName> <newName>");
			if(CommandEdit.  canSee(sender)) this.appendHelpEntry(msg, this.dynamicResourcepacksAlias, "edit <name> <setting> <value>");
			if(CommandRemove.canSee(sender)) this.appendHelpEntry(msg, this.dynamicResourcepacksAlias, "remove <name>");
			if(CommandLock.  canSee(sender)) this.appendHelpEntry(msg, this.dynamicResourcepacksAlias, "lock <player>");
			if(CommandUnlock.canSee(sender)) this.appendHelpEntry(msg, this.dynamicResourcepacksAlias, "unlock <player>");
			
			this.appendHelpEntry(msg, this.dynamicResourcepacksAlias, "version", "Show the version of this plugin");
			
			if(CommandSetresourcepack.canSee(sender)) {
				boolean canLock = sender.hasPermission("dynamicresourcepacks.setpack.lock");
				this.appendHelpEntry(msg, this.setresourcepackAlias, "<pack>" + (canLock ? " [lock]" : ""), "Use the given resourcepack");
				if(sender.hasPermission("dynamicresourcepacks.setpack.others"))
					this.appendHelpEntry(msg, this.setresourcepackAlias, "<player> <pack>" + (canLock ? " [lock]" : ""), "Sets the resourcepack of a given player");
			}
		}
	
		sender.sendMessage(msg.toString());
		
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender) {
		if(this.args.length == 1)
			return this.completeValues(this.args[0], "aliases");
		else
			return new ArrayList<String>();
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
	
	public void appendHelpEntry(StringBuilder stringBuilder, String mainCommand, String commandArgs) {
		this.appendHelpEntry(stringBuilder, mainCommand, commandArgs, null);
	}
	
	public void appendHelpEntry(StringBuilder stringBuilder, String mainCommand, String commandArgs, String description) {
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
