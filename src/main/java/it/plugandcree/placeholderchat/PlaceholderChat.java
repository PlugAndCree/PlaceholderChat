package it.plugandcree.placeholderchat;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import it.plugandcree.placeholderchat.commands.MainCommand;
import it.plugandcree.placeholderchat.config.ConfigProcessor;
import it.plugandcree.placeholderchat.config.CustomConfig;
import it.plugandcree.placeholderchat.events.PlayerChat;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;

public class PlaceholderChat extends JavaPlugin {

	private static PlaceholderChat instance;
	private CustomConfig langConfig;
	private CustomConfig mainConfig;
	private Map<String, String> formats;
	private Chat chat = null;
	private Permission perms = null;
	private BukkitAudiences adventure;

	@Override
	public void onEnable() {
		instance = this;

		adventure = BukkitAudiences.create(this);

		reload();

		getServer().getPluginManager().registerEvents(new PlayerChat(), this);

		new MainCommand().register(this);

		if (!setupChat()) {
			getLogger().severe("VAULT NOT FOUND");
		}

		setupPermissions();
	}

	public void reload() {
		setLangConfig(createConfigFile("lang.yml"));
		setMainConfig(createConfigFile("config.yml"));
		setFormats(ConfigProcessor.getFormats());
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

	private boolean setupChat() {
		if (getServer().getPluginManager().getPlugin("Vault") == null)
			return false;

		RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
		chat = rsp.getProvider();
		return chat != null;
	}

	private boolean setupPermissions() {
		RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
		perms = rsp.getProvider();
		return perms != null;
	}

	public Chat getChat() {
		return chat;
	}

	public Permission getPerms() {
		return perms;
	}

	public BukkitAudiences getAdventure() {
		return adventure;
	}
}
