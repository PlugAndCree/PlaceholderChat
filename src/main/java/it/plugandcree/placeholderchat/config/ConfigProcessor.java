package it.plugandcree.placeholderchat.config;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

import it.plugandcree.placeholderchat.PlaceholderChat;

public class ConfigProcessor {
	public static Map<String, String> getFormats() {
		Map<String, String> formats = new HashMap<>();
		
		CustomConfig conf = PlaceholderChat.getInstance().getMainConfig();
		ConfigurationSection section = conf.getConfigurationSection("chat-format");
		
		for(String key : section.getKeys(false)) 
			formats.put(key, section.getString(key));
		
		return formats;
	}
}
