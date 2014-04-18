package de.rene_zeidler.dynamicresourcepacks.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

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
		if(CommandEdit.canSee(sender) && (StringUtil.startsWithIgnoreCase(this.args[0], "set") || StringUtil.startsWithIgnoreCase(this.args[0], "edit"))) {
			String cmd = StringUtil.startsWithIgnoreCase(this.args[0], "set") ? "set" : "edit";
			if(this.args.length == 1) {
				List<String> completions =
						new CommandEdit(this.plugin,
						this.label + " " + cmd,
						this.dynamicResourcepacksAlias, this.setresourcepackAlias,
						new String[] {"", (this.args[0].length() > cmd.length()) ? this.args[0].substring(cmd.length()) : ""}
						).tabComplete(sender); //complete property
				for(int i = 0; i < completions.size(); i++)
					completions.set(i, cmd + Character.toUpperCase(completions.get(i).charAt(0)) + completions.get(i).substring(1));
				return completions;
			} else if(this.args.length == 2) {
				return this.completeResourcepack(sender, this.args[1]);
			}
		}
		
		if(this.args.length == 1) {
			return this.getCommands(sender, this.args[0]);
		} else {
			return this.getCommand().tabComplete(sender);
		}
	}
	
	public List<String> getCommands(CommandSender sender, String arg) {
		List<String> completions = new ArrayList<String>();
		
		this.addCommandCompletions(completions, arg, "help");
		if(CommandView.      canSee(sender)) this.addCommandCompletions(completions, arg, "view", "show", "info");
		if(CommandList.      canSee(sender)) this.addCommandCompletions(completions, arg, "list");
		if(CommandResend.    canSee(sender)) this.addCommandCompletions(completions, arg, "resend");
		if(CommandPlayerinfo.canSee(sender)) this.addCommandCompletions(completions, arg, "playerinfo");
		if(CommandCreate.    canSee(sender)) this.addCommandCompletions(completions, arg, "create", "add");
		if(CommandEdit.      canSee(sender)) this.addCommandCompletions(completions, arg, "edit", "set");
		if(CommandRename.    canSee(sender)) this.addCommandCompletions(completions, arg, "rename");
		if(CommandRemove.    canSee(sender)) this.addCommandCompletions(completions, arg, "remove", "delete");
		if(CommandLock.      canSee(sender)) this.addCommandCompletions(completions, arg, "lock");
		if(CommandUnlock.    canSee(sender)) this.addCommandCompletions(completions, arg, "unlock");
		if(CommandSetresourcepack.canSee(sender)) this.addCommandCompletions(completions, arg, null, "use", "switch");
		this.addCommandCompletions(completions, arg, "version");
		
		return completions;
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
				
			} else if("resend".equalsIgnoreCase(command)) {
				return new CommandResend(this.plugin, newLabel, this.dynamicResourcepacksAlias, this.setresourcepackAlias, newArgs);
				
			} else if("playerinfo".equalsIgnoreCase(command) ||
					  "player"    .equalsIgnoreCase(command)) {
				return new CommandPlayerinfo(this.plugin, newLabel, this.dynamicResourcepacksAlias, this.setresourcepackAlias, newArgs);
				
			} else if("create".equalsIgnoreCase(command) ||
					  "add"   .equalsIgnoreCase(command)) {
				return new CommandCreate(this.plugin, newLabel, this.dynamicResourcepacksAlias, this.setresourcepackAlias, newArgs);
				
			} else if("edit".equalsIgnoreCase(command) ||
					  "set" .equalsIgnoreCase(command)) {
				return new CommandEdit(this.plugin, newLabel, this.dynamicResourcepacksAlias, this.setresourcepackAlias, newArgs);
			
			} else if(StringUtil.startsWithIgnoreCase(command, "set") ||
					  StringUtil.startsWithIgnoreCase(command, "edit")) {
				String property = command.substring(StringUtil.startsWithIgnoreCase(command, "set") ? 3 : 4);
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
