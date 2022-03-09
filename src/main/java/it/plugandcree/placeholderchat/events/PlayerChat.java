package it.plugandcree.placeholderchat.events;

import java.util.Map;

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

		// %s > %s - len = 1 splitted
		// qualcosa %s > %s - len = 2 splitted

		
		//If the player has a prefix, append it
		if (adventureString.length == 2)
			adventureComponent = adventureComponent
					.append(EnhancedLegacyText.get().buildComponent(adventureString[0]).build());
		
		//Player name component and hover event
		adventureComponent = adventureComponent
				.append(EnhancedLegacyText.get().buildComponent(e.getPlayer().getDisplayName()).build()
						.hoverEvent(HoverEvent.showText(
								EnhancedLegacyText.get().buildComponent(PlaceholderAPI.setPlaceholders(e.getPlayer(),
										PlaceholderChat.getInstance().getMainConfig().getRawString("user-hover-text"))

								).build().asComponent())));
		
		//Chat separator between player name and message
		adventureComponent = adventureComponent
				.append(EnhancedLegacyText.get().buildComponent(adventureString[adventureString.length - 1]).build());
		
		//Chat message
		if (e.getPlayer().hasPermission("placeholderchat.colorchat"))
			adventureComponent = adventureComponent
					.append(EnhancedLegacyText.get().buildComponent(e.getMessage()).build());
		else
			adventureComponent = adventureComponent.append(Component.text(e.getMessage()));

		for (Player p : e.getRecipients()) {
			PlaceholderChat.getInstance().getAdventure().player(p).sendMessage(adventureComponent);
		}

		Bukkit.getLogger().info(e.getPlayer().getName() + " > " + e.getMessage());
	}
}
