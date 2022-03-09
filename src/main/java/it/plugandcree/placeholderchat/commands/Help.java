package it.plugandcree.placeholderchat.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.spigot.commons.commands.ExecutionContext;
import org.spigot.commons.commands.builtin.PermissibleCommand;

import it.plugandcree.placeholderchat.PlaceholderChat;
import it.plugandcree.placeholderchat.config.CustomConfig;

public class Help extends PermissibleCommand {
	public Help() {
		super("help", "placeholderchat.help", PlaceholderChat.getInstance().getLangConfig().noPerm());
	}

	@Override
	public boolean execute(CommandSender sender, ExecutionContext context) {
		CustomConfig lang = PlaceholderChat.getInstance().getLangConfig();
		
		sender.sendMessage(lang.getString("messages.help"));
		sender.sendMessage(ChatColor.GRAY + "Version: " + PlaceholderChat.getInstance().getDescription().getVersion());
		sender.sendMessage(String.format(lang.getRawString("messages.help-format"), "/placeholderchat", "Show the credits"));
		sender.sendMessage(String.format(lang.getRawString("messages.help-format"), "/placeholderchat reload", "Reload the plugin"));
		return true;
	}
}
