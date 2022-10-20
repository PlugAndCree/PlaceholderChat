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

import it.plugandcree.placeholderchat.PlaceholderChat;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class PlayerChat implements Listener {

	private static final Pattern PATTERN_HEX = Pattern.compile("[§&](#[0-9a-fA-F]{6})");
	private static final Pattern PATTERN_GRADIENT = Pattern.compile("\\{(([&§][0-9a-f]|[&§]#[0-9a-f]{6})(\\,[&§][0-9a-f]|\\,[&§]#[0-9a-f]{6})+)\\}");
	
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

		format = format.replace('&',(char) 1);
		format = PlaceholderAPI.setPlaceholders(e.getPlayer(), format);
		format = format.replace((char) 1, '&');

		try {
			e.setFormat(format);
		} catch (Exception err) {
			PlaceholderChat.getInstance().getLogger()
					.severe(String.format("Some placeholder in group %s does not exist", perms));
			return;
		}
		
		e.setCancelled(true);
		
		MiniMessage mm = MiniMessage.miniMessage();

		Component adventureComponent = mm.deserialize(legacyToMinimessage(
				e.getFormat().replaceFirst("%s", "<hover:show_text:'" + placeholders(e.getPlayer(), PlaceholderChat.getInstance().getMainConfig().getRawString("user-hover-text")) + "'>" + e.getPlayer().getDisplayName() + "</hover>")));
		
		Component chat;
		
		if (e.getPlayer().hasPermission("placeholderchat.colorchat")) 
			chat = mm.deserialize(legacyToMinimessage(e.getMessage()));
		else 
			chat = Component.text(e.getMessage());
		
		adventureComponent = adventureComponent.replaceText(b -> {
			b.matchLiteral("%s");
			b.once();
			b.replacement(c -> c.content("").append(chat));
		});
		
		for (Player p : e.getRecipients()) {
			PlaceholderChat.getInstance().getAdventure().player(p).sendMessage(adventureComponent);
		}

		Bukkit.getLogger().info(e.getPlayer().getName() + " > " + e.getMessage());
	}
	
	private String placeholders(Player p, String message) {
		return PlaceholderAPI.setPlaceholders(p, legacyToMinimessage(message));
	}
	
	private String legacyToMinimessage(String message) {
		Matcher matcherGradient = PATTERN_GRADIENT.matcher(message);
		while (matcherGradient.find()) {
			String found = matcherGradient.group(1);
			found = found.replaceAll("[&§]0", "black")
					.replaceAll("[&§]1", "dark_blue")
					.replaceAll("[&§]2", "dark_green")
					.replaceAll("[&§]3", "dark_aqua")
					.replaceAll("[&§]4", "dark_red")
					.replaceAll("[&§]5", "dark_purple")
					.replaceAll("[&§]6", "gold")
					.replaceAll("[&§]7", "gray")
					.replaceAll("[&§]8", "dark_gray")
					.replaceAll("[&§]9", "blue")
					.replaceAll("[&§]a", "green")
					.replaceAll("[&§]b", "aqua")
					.replaceAll("[&§]c", "red")
					.replaceAll("[&§]d", "light_purple")
					.replaceAll("[&§]e", "yellow")
					.replaceAll("[&§]f", "white")
					.replaceAll("[&§]#", "#")
					.replace(',', ':');
			
			
			message = message.replaceFirst(PATTERN_GRADIENT.toString(), "<gradient:" + found + ">");
		}
		
		Matcher matcherHex = PATTERN_HEX.matcher(message);
		
		while (matcherHex.find())
			message = message.replaceFirst(PATTERN_HEX.toString(), "<color:" + matcherHex.group(1) + ">");
		
		message = message.replaceAll("[&§]0", "<black>")
				.replaceAll("[&§]1", "<dark_blue>")
				.replaceAll("[&§]2", "<dark_green>")
				.replaceAll("[&§]3", "<dark_aqua>")
				.replaceAll("[&§]4", "<dark_red>")
				.replaceAll("[&§]5", "<dark_purple>")
				.replaceAll("[&§]6", "<gold>")
				.replaceAll("[&§]7", "<gray>")
				.replaceAll("[&§]8", "<dark_gray>")
				.replaceAll("[&§]9", "<blue>")
				.replaceAll("[&§]a", "<green>")
				.replaceAll("[&§]b", "<aqua>")
				.replaceAll("[&§]c", "<red>")
				.replaceAll("[&§]d", "<light_purple>")
				.replaceAll("[&§]e", "<yellow>")
				.replaceAll("[&§]f", "<white>")
				.replaceAll("[&§]l", "<bold>")
				.replaceAll("[&§]m", "<strikethrough>")
				.replaceAll("[&§]n", "<underlined>")
				.replaceAll("[&§]o", "<italic>")
				.replaceAll("[&§]k", "<obfuscated>")
				.replaceAll("[&§]r", "<reset>");
		
		return message;
	}
}
