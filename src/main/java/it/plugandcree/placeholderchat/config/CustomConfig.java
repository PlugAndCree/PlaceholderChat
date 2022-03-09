package it.plugandcree.placeholderchat.config;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import com.haroldstudios.hexitextlib.HexResolver;


public class CustomConfig extends YamlConfiguration {
	
	public String superGetString(String path) {
		return super.getString(path);
	}
	
	public String getRawString(String path) {
		
		return ChatColor.translateAlternateColorCodes('&', HexResolver.parseHexString(super.getString(path)));
	}
	
	public String getPrefix() {
		return ChatColor.translateAlternateColorCodes('&', HexResolver.parseHexString(super.getString("messages.prefix")));
	}
	
	@Override
	public String getString(String path) {
		return getPrefix() + getRawString(path);
	}
	
	public String noPerm() {
		return getString("messages.no-perm");
	}
}