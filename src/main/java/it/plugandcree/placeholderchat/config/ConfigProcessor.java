package it.plugandcree.placeholderchat.config;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

import it.plugandcree.placeholderchat.PlaceholderChat;

public class ConfigProcessor {
	public static Map<String, String> getSimpleFormats() {
		Map<String, String> formats = new HashMap<>();
		
		CustomConfig conf = PlaceholderChat.getInstance().getMainConfig();
		ConfigurationSection section = conf.getConfigurationSection("chat-format");
		
		for(String key : section.getKeys(false)) 
			formats.put(key, section.getString(key));
		
		return formats;
	}
	
	public static Map<String, AdvancedFormat> getAdvancedFormats() {
		Map<String, AdvancedFormat> formats = new HashMap<>();
		
		CustomConfig conf = PlaceholderChat.getInstance().getMainConfig();
		ConfigurationSection section = conf.getConfigurationSection("advanced");
		
		for(String key : section.getKeys(false)) {
			ConfigurationSection sub = section.getConfigurationSection(key);
			String logicExpression = sub.getString("condition");
			String recipentCondition = sub.getString("recipent-condition", "true");
		    int priority = sub.getInt("priority", 0);
		    String chatFormat = sub.getString("chat-format");
		    String userHoverText = sub.getString("user-hover-text", "");
		    
		    formats.put(key, new AdvancedFormat(logicExpression, recipentCondition, priority, chatFormat, userHoverText));
		}
		
		return formats;
	}
}
