package it.plugandcree.placeholderchat.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.spigot.libraries.commands.Command;

import it.plugandcree.placeholderchat.PlaceholderChat;
import it.plugandcree.placeholderchat.config.CustomConfig;

public class Help extends Command {
	public Help() {
		super("help", "placeholderchat.help", PlaceholderChat.getInstance().getLangConfig().noPerm());
	}
	
	public boolean execute(CommandSender sender, org.bukkit.command.Command cmd, List<String> args) {
		
		CustomConfig lang = PlaceholderChat.getInstance().getLangConfig();
		
		sender.sendMessage(lang.getString("messages.help"));
		sender.sendMessage(ChatColor.GRAY + "Version: " + PlaceholderChat.getInstance().getDescription().getVersion());
		sender.sendMessage(String.format(lang.getRawString("messages.help-format"), "/placeholderchat", "Show the credits"));
		sender.sendMessage(String.format(lang.getRawString("messages.help-format"), "/placeholderchat reload", "Reload the plugin"));
		return true;
	}
}
