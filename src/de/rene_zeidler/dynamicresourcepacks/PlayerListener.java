package de.rene_zeidler.dynamicresourcepacks;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import de.rene_zeidler.dynamicresourcepacks.command.CommandDynamicResourcepacks;
import de.rene_zeidler.dynamicresourcepacks.command.CommandSetresourcepack;
import de.rene_zeidler.dynamicresourcepacks.command.DynamicResourcepacksCommand;

public class PlayerListener implements Listener, CommandExecutor, TabExecutor {
	private DynamicResourcepacks plugin;
	private ResourcepackManager packManager;
	
	public PlayerListener(DynamicResourcepacks plugin) {
		this.plugin = plugin;
		this.packManager = this.plugin.getResourcepackManager();
	}
	
	@EventHandler
	public void onLogin(PlayerJoinEvent event) {
		this.packManager.loadPlayerFromConfig(event.getPlayer());
	}
	
	@EventHandler
	public void onDisconnect(PlayerQuitEvent event) {
		this.packManager.saveConfigForPlayer(event.getPlayer());
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		DynamicResourcepacksCommand cmd = this.getCommand(command, label, args);
		
		if(cmd == null) return false;
		else return cmd.run(sender);
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		DynamicResourcepacksCommand cmd = this.getCommand(command, alias, args);
		
		if(cmd == null) return null;
		else return cmd.tabComplete(sender);
	}
	
	public DynamicResourcepacksCommand getCommand(Command command, String label, String[] args) {
		if(command.getName().equalsIgnoreCase("dynamicresourcepacks"))
			return new CommandDynamicResourcepacks(this.plugin, label, label, null, args);
		else if(command.getName().equalsIgnoreCase("setresourcepack"))
			return new CommandSetresourcepack(this.plugin, label, null, label, args);
		
		return null;
	}
}
