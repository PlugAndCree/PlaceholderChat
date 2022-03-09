package it.plugandcree.placeholderchat.commands;

import org.bukkit.command.CommandSender;
import org.spigot.commons.commands.ExecutionContext;
import org.spigot.commons.commands.builtin.PermissibleCommand;

import it.plugandcree.placeholderchat.PlaceholderChat;
import it.plugandcree.placeholderchat.config.CustomConfig;


public class Reload extends PermissibleCommand {
	
	public Reload() {
		super("reload", "placeholderchat.reload", PlaceholderChat.getInstance().getLangConfig().noPerm());
	}
	
	@Override
	public boolean execute(CommandSender sender, ExecutionContext context) {
		CustomConfig lang = PlaceholderChat.getInstance().getLangConfig();
		
		PlaceholderChat.getInstance().reload();
		sender.sendMessage(lang.getString("messages.reload-complete"));
		
		return true;
	}
}
