package it.plugandcree.placeholderchat.events;

import java.util.Map;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.permissions.PermissionAttachmentInfo;

import it.plugandcree.placeholderchat.PlaceholderChat;
import me.clip.placeholderapi.PlaceholderAPI;

public class PlayerChat implements Listener {

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {

		if (e.isCancelled())
			return;

		Map<String, String> formats = PlaceholderChat.getInstance().getFormats();

		String perms = null;

		for (PermissionAttachmentInfo p : e.getPlayer().getEffectivePermissions()) {
			if (p.getPermission().startsWith("group.")) {
				perms = p.getPermission().split("\\.")[1];
			}
		}

		if (perms == null)
			return;

		if (!formats.keySet().contains(perms))
			return;

		String format = formats.get(perms);

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
		}
	}
}
