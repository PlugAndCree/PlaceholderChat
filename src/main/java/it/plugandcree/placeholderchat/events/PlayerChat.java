package it.plugandcree.placeholderchat.events;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import dev.vankka.enhancedlegacytext.EnhancedLegacyText;
import it.plugandcree.placeholderchat.PlaceholderChat;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;

public class PlayerChat implements Listener {

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerChat(AsyncPlayerChatEvent e) {

		if (e.isCancelled())
			return;

		Map<String, String> formats = PlaceholderChat.getInstance().getFormats();

		String perms = PlaceholderChat.getInstance().getPerms().getPrimaryGroup(e.getPlayer());

		String format;

		if (perms == null)
			format = PlaceholderChat.getInstance().getMainConfig().superGetString("default-format");
		else
			format = formats.get(perms);

		if (!formats.keySet().contains(perms))
			format = PlaceholderChat.getInstance().getMainConfig().superGetString("default-format");

		format = format.replace("%player%", "%s").replace("%message%", "%s");

		String prefix = PlaceholderChat.getInstance().getChat().getPlayerPrefix(e.getPlayer());
		String suffix = PlaceholderChat.getInstance().getChat().getPlayerSuffix(e.getPlayer());

		format = format.replace("%prefix%", prefix).replace("%suffix%", suffix);

		format = PlaceholderAPI.setPlaceholders(e.getPlayer(), format);

		try {
			e.setFormat(format);
		} catch (Exception err) {
			PlaceholderChat.getInstance().getLogger()
					.severe(String.format("Some placeholder in group %s does not exist", perms));
			return;
		}

		e.setCancelled(true);

		String[] adventureString = e.getFormat().split("%s");
		Component adventureComponent = EnhancedLegacyText.get().buildComponent("").build();

		// %s > %s - len = 2 splitted
		// ''
		// ' > '
		
		// something %s > %s - len = 2 splitted
		// 'something '
		// ' > '
		
		// %s > %s something - len = 3 splitted
		// ''
		// ' > '
		// ' something'
		
		// something %s > %s something - len = 3 splitted
		// 'something '
		// ' >asdasdasdsda '
		// ' something'

		
		adventureComponent = adventureComponent
				.append(EnhancedLegacyText.get().buildComponent(adventureString[0]).build());
		
		//Player name component and hover event
		adventureComponent = adventureComponent
				.append(EnhancedLegacyText.get().buildComponent(getLastColor(adventureString[0]) + e.getPlayer().getDisplayName()).build()
						.hoverEvent(HoverEvent.showText(
								EnhancedLegacyText.get().buildComponent(PlaceholderAPI.setPlaceholders(e.getPlayer(),
										PlaceholderChat.getInstance().getMainConfig().getRawString("user-hover-text"))

								).build().asComponent())));
		
		//Chat separator between player name and message
		adventureComponent = adventureComponent
				.append(EnhancedLegacyText.get().buildComponent(adventureString[1]).build());
				
		//Chat message
		if (e.getPlayer().hasPermission("placeholderchat.colorchat"))
			adventureComponent = adventureComponent
				.append(EnhancedLegacyText.get().buildComponent(getLastColor(adventureString[1]) + e.getMessage()).build());
		else
			adventureComponent = adventureComponent
				.append(EnhancedLegacyText.get().buildComponent(getLastColor(adventureString[1]) + stripColor(e.getMessage())).build());
		
		
		if (adventureString.length == 3)
			adventureComponent = adventureComponent.append(
					EnhancedLegacyText.get().buildComponent(getLastColor(adventureString[1]) + adventureString[2]).build());

		for (Player p : e.getRecipients()) {
			PlaceholderChat.getInstance().getAdventure().player(p).sendMessage(adventureComponent);
		}

		Bukkit.getLogger().info(e.getPlayer().getName() + " > " + e.getMessage());
	}
	
	private String getLastColor(String message) {
		  String regex = ".*([&§][0-9a-fA-F]|[&§]#[0-9a-fA-F]{6})|.*([&§][olknm])";
		  Pattern p = Pattern.compile(regex);
		  Matcher m = p.matcher(message);
		  
		  if(!m.find())
			  return "";
		  
		  String g1 = m.group(1);
		  String g2 = m.group(2);
		  
		  if(String.valueOf(g1).equals("null"))
			  g2 = "";
		  
		  if(String.valueOf(g2).equals("null"))
			  g2 = "";
		  
		  return g1 + g2;	
	}
	
	private String stripColor(String message) {
		String regex = "([&§][0-9a-fA-F]|[&§]#[0-9a-fA-F]{6})|([&§][olknm])";
		
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(message);
		  
		if(!m.find())
			return message;
		 
		return m.replaceAll("");
	}
	
}
