package it.plugandcree.placeholderchat.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.spigot.libraries.commands.Command;

import it.plugandcree.placeholderchat.PlaceholderChat;
import it.plugandcree.placeholderchat.config.CustomConfig;


public class Reload extends Command {
	
	public Reload() {
		super("reload", "placeholderchat.reload", PlaceholderChat.getInstance().getLangConfig().noPerm());
	}
	
	
	@Override
	public boolean execute(CommandSender sender, org.bukkit.command.Command cmd, List<String> args) {
		CustomConfig lang = PlaceholderChat.getInstance().getLangConfig();
		
		PlaceholderChat.getInstance().reload();
		sender.sendMessage(lang.getString("messages.reload-complete"));
		
		return true;
	}
}
