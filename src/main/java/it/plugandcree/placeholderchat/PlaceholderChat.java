package it.plugandcree.placeholderchat;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

import it.plugandcree.placeholderchat.config.ConfigProcessor;
import it.plugandcree.placeholderchat.config.CustomConfig;
import it.plugandcree.placeholderchat.events.PlayerChat;

public class PlaceholderChat extends JavaPlugin {

	private static PlaceholderChat instance;
	private CustomConfig langConfig;
	private CustomConfig mainConfig;
	private Map<String, String> formats;

	@Override
	public void onEnable() {
		instance = this;

		setFormats(ConfigProcessor.getFormats());
		reloadConfig();

		getServer().getPluginManager().registerEvents(new PlayerChat(), this);

		
		
	}

	public void reloadConfig() {
		setLangConfig(createConfigFile("lang.yml"));
		setMainConfig(createConfigFile("config.yml"));
	}

	private CustomConfig createConfigFile(String name) {
		File fc = new File(getDataFolder(), name);
		if (!fc.exists()) {
			fc.getParentFile().mkdirs();
			saveResource(name, false);
		}

		CustomConfig configFile = new CustomConfig();
		try {
			configFile.load(fc);
			return configFile;
		} catch (IOException | InvalidConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	public static PlaceholderChat getInstance() {
		return instance;
	}

	public CustomConfig getLangConfig() {
		return langConfig;
	}

	public void setLangConfig(CustomConfig langConfig) {
		this.langConfig = langConfig;
	}

	public CustomConfig getMainConfig() {
		return mainConfig;
	}

	public void setMainConfig(CustomConfig mainConfig) {
		this.mainConfig = mainConfig;
	}

	public Map<String, String> getFormats() {
		return formats;
	}

	public void setFormats(Map<String, String> formats) {
		this.formats = formats;
	}
	
}
