package de.rene_zeidler.dynamicresourcepacks;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerListener implements Listener, CommandExecutor {
	private DynamicResourcepacks plugin;
	private PlayerManager playerManager;
	
	public PlayerListener(DynamicResourcepacks plugin) {
		this.plugin = plugin;
		this.playerManager = this.plugin.getPlayerManager();
	}
	
	@EventHandler
	public void onLogin(PlayerLoginEvent event) {
		this.playerManager.getResourcepackManager().loadPlayerFromConfig(event.getPlayer());
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equalsIgnoreCase("dynamicresourcepacks"))
			return this.playerManager.handleDynamicResourcepacksCommand(sender, label, args);
		else if(command.getName().equalsIgnoreCase("setresourcepack"))
			return this.playerManager.handleSetResourcepackCommand(sender, label, args);
		
		return false;
	}
}
