package de.rene_zeidler.dynamicresourcepacks.command;

import java.util.List;

import org.bukkit.command.CommandSender;

import de.rene_zeidler.dynamicresourcepacks.DynamicResourcepacks;

public class CommandDynamicResourcepacks extends DynamicResourcepacksCommand {
	
	public CommandDynamicResourcepacks(DynamicResourcepacks plugin,
			String label, String dynamicResourcepacksAlias,
			String setresourcepackAlias, String[] args) {
		super(plugin, label, dynamicResourcepacksAlias, setresourcepackAlias, args);
	}

	@Override
	public boolean run(CommandSender sender) {
		return this.getCommand().run(sender);
	}

	@Override
	public List<String> tabComplete(CommandSender sender) {
		if(this.args.length == 1) {
			//TODO: tab complete
			return null;
		} else {
			return this.getCommand().tabComplete(sender);
		}
	}
	
	public DynamicResourcepacksCommand getCommand() {
		if(this.args.length == 0) {
			return new CommandHelp(this.plugin, this.label, this.dynamicResourcepacksAlias, this.setresourcepackAlias, this.args);
		} else {
			String command = this.args[0];
			String newLabel = this.label + " " + command;
			String[] newArgs = new String[this.args.length - 1];
			for(int i = 0; i < newArgs.length; i++)
				newArgs[i] = this.args[i + 1];
			
			if("help".equalsIgnoreCase(command) ||
			   "?"   .equalsIgnoreCase(command)) {
				return new CommandHelp(this.plugin, newLabel, this.dynamicResourcepacksAlias, this.setresourcepackAlias, newArgs);
				
			} else if("view".equalsIgnoreCase(command) ||
					  "show".equalsIgnoreCase(command) ||
					  "info".equalsIgnoreCase(command)) {
				return new CommandView(this.plugin, newLabel, this.dynamicResourcepacksAlias, this.setresourcepackAlias, newArgs);
				
			} else if("list".equalsIgnoreCase(command)) {
				return new CommandList(this.plugin, newLabel, this.dynamicResourcepacksAlias, this.setresourcepackAlias, newArgs);
				
			} else if("create".equalsIgnoreCase(command) ||
					  "add"   .equalsIgnoreCase(command)) {
				return new CommandCreate(this.plugin, newLabel, this.dynamicResourcepacksAlias, this.setresourcepackAlias, newArgs);
				
			} else if("edit".equalsIgnoreCase(command) ||
					  "set" .equalsIgnoreCase(command)) {
				return new CommandEdit(this.plugin, newLabel, this.dynamicResourcepacksAlias, this.setresourcepackAlias, newArgs);
			
			} else if(command.startsWith("set" ) ||
					  command.startsWith("edit")) {
				String property = command.substring(command.startsWith("set") ? 3 : 4);
				if(this.args.length > 1) {
					this.args[0] = this.args[1];
					this.args[1] = property;
				}
				return new CommandEdit(this.plugin, newLabel, this.dynamicResourcepacksAlias, this.setresourcepackAlias, this.args);
				
			} else if("rename".equalsIgnoreCase(command)) {
				return new CommandRename(this.plugin, newLabel, this.dynamicResourcepacksAlias, this.setresourcepackAlias, newArgs);
				
			} else if("remove".equalsIgnoreCase(command) ||
					  "delete".equalsIgnoreCase(command)) {
				return new CommandRemove(this.plugin, newLabel, this.dynamicResourcepacksAlias, this.setresourcepackAlias, newArgs);
				
			} else if("switch".equalsIgnoreCase(command) ||
					  "use"   .equalsIgnoreCase(command)) {
				return new CommandSetresourcepack(this.plugin, newLabel, this.dynamicResourcepacksAlias, this.setresourcepackAlias, newArgs);
				
			} else if("lock".equalsIgnoreCase(command)) {
				return new CommandLock(this.plugin, newLabel, this.dynamicResourcepacksAlias, this.setresourcepackAlias, newArgs);
			
			} else if("unlock".equalsIgnoreCase(command)) {
				return new CommandUnlock(this.plugin, newLabel, this.dynamicResourcepacksAlias, this.setresourcepackAlias, newArgs);
				
			} else if("version".equalsIgnoreCase(command) ||
					  "ver"    .equalsIgnoreCase(command)) {
				return new CommandVersion(this.plugin, this.label, this.dynamicResourcepacksAlias, this.setresourcepackAlias, newArgs);
				
			} else {
				return new CommandHelp(this.plugin, this.label, this.dynamicResourcepacksAlias, this.setresourcepackAlias, this.args);
				
			}
		}
	}
}
