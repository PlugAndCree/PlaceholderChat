package it.plugandcree.placeholderchat.commands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.spigot.commons.commands.Command;
import org.spigot.commons.commands.ExecutionContext;

import net.md_5.bungee.api.ChatColor;

public class MainCommand extends Command {
	
	public MainCommand() {
		super("placeholderchat");
		
		registerSubcommand(new Help());
		registerSubcommand(new Reload());
		
	}

	@Override
	public boolean execute(CommandSender sender, ExecutionContext context) {
		if(context.isLastCommand()) {
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&lPLACEHOLDER CHAT &7plugin made by &aPlug_And_Cree"));
			if(sender.hasPermission("placeholderchat.help"))
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Type &a/placeholderchat help &7for more info"));
			
			return true;
		}
		
		return false;
	}

}
