package it.plugandcree.placeholderchat.events;

import java.util.Map;

import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.permissions.PermissionAttachmentInfo;

import it.plugandcree.placeholderchat.PlaceholderChat;
import me.clip.placeholderapi.PlaceholderAPI;

public class PlayerChat implements Listener {

	public void onPlayerChat(AsyncPlayerChatEvent e) {
		if (e.isCancelled())
			return;

		Map<String, String> formats = PlaceholderChat.getInstance().getFormats();

		String perms = null;
		
		for (PermissionAttachmentInfo p : e.getPlayer().getEffectivePermissions()) {
			if (p.toString().startsWith("group."))
				perms = p.toString().split(".")[1];
		}
		
		if (perms == null)
			return;
		
		if(!formats.keySet().contains(perms)) return;
		
		String format = formats.get(perms);
		format = format.replace("%player%", "%s").replace("%message%", "%s");
		
		format = PlaceholderAPI.setPlaceholders(e.getPlayer(), format);
		
		e.setFormat(format);
		
	}
}
