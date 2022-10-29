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
import org.mvel2.MVEL;

import it.plugandcree.placeholderchat.PlaceholderChat;
import it.plugandcree.placeholderchat.config.AdvancedFormat;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class PlayerChat implements Listener {

	private static final Pattern PATTERN_HEX = Pattern.compile("[§&](#[0-9a-fA-F]{6})");
	private static final Pattern PATTERN_GRADIENT = Pattern.compile("\\{(([&§][0-9a-f]|[&§]#[0-9a-f]{6})(\\,[&§][0-9a-f]|\\,[&§]#[0-9a-f]{6})+)\\}");
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		 
		if (e.isCancelled())
			return;

		String format = null;
		
		if (PlaceholderChat.getInstance().isAdvancedMode()) {
			if (getAdvancedFormat(e.getPlayer()) == null)
				format = PlaceholderChat.getInstance().getDefaultFormat();
			else
				format = getAdvancedFormat(e.getPlayer()).getChatFormat();
		}
		else
			format = getSimpleFormat(e.getPlayer());
		
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
					.severe(String.format("Some placeholders for player %s does not exist", e.getPlayer().getName()));
			return;
		}
		
		e.setCancelled(true);
		
		sendChatMessage(e);
	}
	
	private AdvancedFormat getAdvancedFormat(Player p) {
		Map<String, AdvancedFormat> formats = PlaceholderChat.getInstance().getAdvancedFormats();
		String found = null;
		
		for (String key : formats.keySet()) {
			boolean result = (boolean) MVEL.eval(PlaceholderAPI.setPlaceholders(p, formats.get(key).getCondition()));
			
			if (!result)
				continue;
			
			if (found == null || formats.get(found).getPriority() < formats.get(key).getPriority())
				found = key;
		}
		
		return formats.get(found);
	}
	
	private String getSimpleFormat(Player p) {
		Map<String, String> formats = PlaceholderChat.getInstance().getSimpleFormats();

		String perms = PlaceholderChat.getInstance().getPerms().getPrimaryGroup(p);

		String format;

		if (perms == null)
			format = PlaceholderChat.getInstance().getDefaultFormat();
		else
			format = formats.get(perms);

		if (!formats.keySet().contains(perms))
			format = PlaceholderChat.getInstance().getDefaultFormat();
		
		return format;
	}
	
	private void sendChatMessage(AsyncPlayerChatEvent e) {
	
		MiniMessage mm = MiniMessage.miniMessage();

		String format = e.getFormat();

		if (PlaceholderChat.getInstance().isAdvancedMode())
			if (getAdvancedFormat(e.getPlayer()) == null)
				format = format.replaceFirst("%s", "<hover:show_text:'" + placeholders(e.getPlayer(), PlaceholderChat.getInstance().getSimpleUserHoverText()) + "'>" + e.getPlayer().getDisplayName() + "</hover>");
			else
				format = format.replaceFirst("%s", "<hover:show_text:'" + placeholders(e.getPlayer(), getAdvancedFormat(e.getPlayer()).getUserHoverText() + "'>" + e.getPlayer().getDisplayName() + "</hover>"));
		else
			format = format.replaceFirst("%s", "<hover:show_text:'" + placeholders(e.getPlayer(), PlaceholderChat.getInstance().getSimpleUserHoverText()) + "'>" + e.getPlayer().getDisplayName() + "</hover>");

		String chat;
		
		if (e.getPlayer().hasPermission("placeholderchat.colorchat")) 
			chat = e.getMessage();
		else 
			chat = ((TextComponent) mm.deserialize(legacyToMinimessage(e.getMessage()))).content();
		
		format = format.replaceFirst("%s", chat);
		
		System.out.println(format);
		
		for (Player p : e.getRecipients()) {
			String recipentCondition = getAdvancedFormat(e.getPlayer()).getRecipentCondition();
				
			boolean result = ((boolean) MVEL.eval(PlaceholderAPI.setPlaceholders(p, recipentCondition))) || !PlaceholderChat.getInstance().isAdvancedMode();
			if (result)
				PlaceholderChat.getInstance().getAdventure().player(p).sendMessage(mm.deserialize(legacyToMinimessage(format)));
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
