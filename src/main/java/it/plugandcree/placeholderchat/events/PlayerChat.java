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
import net.md_5.bungee.api.chat.hover.content.Content;
import net.md_5.bungee.api.chat.hover.content.Text;

public class PlayerChat implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
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

		TextComponent component = new TextComponent();
		String usernameString = e.getPlayer().getDisplayName()
				.replace(ChatColor.translateAlternateColorCodes('&', prefix), "")
				.replace(ChatColor.translateAlternateColorCodes('&', suffix), "");

		String[] splitted = e.getFormat().split(Pattern.quote("%s"), 2);

		if (splitted.length > 1) {
			component = new TextComponent(splitted[0]);
			usernameString = ChatColor.getLastColors(splitted[0]) + usernameString;
		}

		BaseComponent[] usernameSplitted = TextComponent.fromLegacyText(usernameString);
		BaseComponent username = new TextComponent("");
		for (BaseComponent comp : usernameSplitted)
			username.addExtra(comp);

		username.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
				new Content[] { new Text(PlaceholderAPI.setPlaceholders(e.getPlayer(),
						PlaceholderChat.getInstance().getMainConfig().getRawString("user-hover-text"))) }));
		
		//TODO rgb codes not working on text component
		/* possible solutions:
		 * 
		 * 1) https://hub.spigotmc.org/jira/browse/SPIGOT-5829
		 * 2) https://github.com/KyoriPowered/adventure
		 *
		 */
		e.getPlayer().sendMessage(PlaceholderAPI.setPlaceholders(e.getPlayer(), PlaceholderChat.getInstance().getMainConfig().getRawString("user-hover-text")));

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
