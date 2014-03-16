package de.rene_zeidler.dynamicresourcepacks;

import org.bukkit.configuration.Configuration;

public class ResourcepackManager {
	
	private DynamicResourcepacks plugin;
	private Configuration config;
	
	public ResourcepackManager(DynamicResourcepacks plugin) {
		this.plugin = plugin;
		this.config = plugin.getConfig();
	}
}
