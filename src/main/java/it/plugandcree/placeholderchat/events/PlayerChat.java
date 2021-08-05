package it.plugandcree.placeholderchat.events;

import java.util.Map;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import it.plugandcree.placeholderchat.PlaceholderChat;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class PlayerChat implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerChat(AsyncPlayerChatEvent e) {

		if (e.isCancelled())
			return;

		System.out.println("---> " + e.getPlayer().getDisplayName());
		
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

		TextComponent component = new TextComponent();
		String usernameString = e.getPlayer().getDisplayName();
		String[] splitted = e.getFormat().split(Pattern.quote("%s"), 2);
		System.out.println(e.getFormat());
		
		if (splitted.length > 1) {
			component = new TextComponent(splitted[0]);
			usernameString = ChatColor.getLastColors(splitted[0]) + usernameString;
		}

		BaseComponent[] usernameSplitted = TextComponent.fromLegacyText(usernameString);
		BaseComponent username = new TextComponent("");
		for(BaseComponent comp : usernameSplitted) username.addExtra(comp);
		
		username.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
				new BaseComponent[] { new TextComponent(PlaceholderAPI.setPlaceholders(e.getPlayer(),
						PlaceholderChat.getInstance().getMainConfig().getRawString("user-hover-text"))) }));

		component.addExtra(username);

		if (splitted.length == 1) {
			component.addExtra(new TextComponent(String.format(splitted[0], e.getMessage())));
		} else
			component.addExtra(new TextComponent(String.format(splitted[1], e.getMessage())));

		for (Player p : e.getRecipients()) {
			p.spigot().sendMessage(component);
		}

		Bukkit.getLogger().info(e.getPlayer().getName() + " > " + e.getMessage());
	}
}
