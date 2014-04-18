package de.rene_zeidler.dynamicresourcepacks.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

import de.rene_zeidler.dynamicresourcepacks.DynamicResourcepacks;
import de.rene_zeidler.dynamicresourcepacks.Resourcepack;

public class CommandResend extends DynamicResourcepacksCommand {

	public CommandResend(DynamicResourcepacks plugin, String label,
			String dynamicResourcepacksAlias, String setresourcepackAlias,
			String[] args) {
		super(plugin, label, dynamicResourcepacksAlias, setresourcepackAlias, args);
	}

	@Override
	public boolean run(CommandSender sender) {
		if(sender.hasPermission("dynamicresourcepacks.resend.others")) {
			if(this.args.length > 1) {
				sender.sendMessage(ChatColor.RED + "Usage: /" + this.label + " [player|pack|all|empty]");
				return true;
			}
		} else if(args.length > 0) {
			sender.sendMessage(ChatColor.RED + "Usage: /" + this.label);
			return true;
		}
		
		if(this.args.length == 0) {
			if(sender instanceof Player) {
				this.packManager.resend((Player)sender);
				sender.sendMessage(ChatColor.GREEN + "Successfully resend your resourcepack");
			} else
				sender.sendMessage(ChatColor.RED + "Usage: /" + this.label + " <player|pack|all|empty>");
			
		} else {
			if("all".equalsIgnoreCase(this.args[0])) {
				this.packManager.resendAll();
				sender.sendMessage(ChatColor.GREEN + "Successfully resend all (non-empty) resourcepacks");
			} else if("empty".equalsIgnoreCase(this.args[0])) {
				this.packManager.resendEmpty();
				sender.sendMessage(ChatColor.GREEN + "Successfully resend the empty resourcepack");
			} else if(this.packManager.resourcepackExists(this.args[0].toLowerCase())) {
				Resourcepack pack = this.getResourcepackForInputString(sender, this.args[0]);
				if(pack == null) return true; //Should never happen, but just in case
				this.packManager.resend(pack);
				sender.sendMessage(ChatColor.GREEN + "Successfully resend the resourcepack " + pack.getDisplayName());
			} else {
				@SuppressWarnings("deprecation")
				Player p = sender.getServer().getPlayer(this.args[0]);
				if(p != null) {
					this.packManager.resend(p);
					sender.sendMessage(ChatColor.GREEN + "Successfully resend the resourcepack of " + p.getName());
				} else {
					//This also autocompletes rp names and shows a message when no rp matches
					Resourcepack pack = this.getResourcepackForInputString(sender, this.args[0]);
					if(pack != null) {
						this.packManager.resend(pack);
						sender.sendMessage(ChatColor.GREEN + "Successfully resend the resourcepack " + pack.getDisplayName());
					}
				}
			}	
		}
		
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender) {
		if(this.args.length == 1 && sender.hasPermission("dynamicresourcepacks.resend.others")) {
			List<String> completions = this.completeResourcepack(sender, this.args[0]);
			this.addCompletions(completions, this.args[0], "all", "empty");
			return (completions.size() == 0) ? null : completions; //Complete names if nothing else applies
		} else
			return new ArrayList<String>();
	}
	
	public static boolean canSee(Permissible permissible) {
		return permissible.hasPermission("dynamicresourcepacks.resend");
	}

}
