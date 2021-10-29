package de.hakuyamu.skybee.votesystem.enums;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

public enum TrustedServices {

    MINECRAFT_SERVER_EU("https://minecraft-server.eu/vote/index/1BE38/", 1, "minecraft-server.eu"),
    MINECRAFT_SERVERLIST_NET("https://www.minecraft-serverlist.net/vote/30194/", 2, "minecraft-serverlist.net"),
    SERVERLISTE_NET("https://www.serverliste.net/vote/3859/", 3, "serverliste.net"),
    ;

    private final String url;
    private final int number;
    private final String service;

    TrustedServices(String url, int number, String service) {
        this.url = url;
        this.number = number;
        this.service = service;
    }

    public String getUrl() {
        return url;
    }

    public int getNumber() {
        return number;
    }

    public String getService() {
        return service;
    }

    public BaseComponent getVoteLink(String name) {
        TextComponent result = new TextComponent(TextComponent.fromLegacyText("§8» §2Link " + number + ": "));
        TextComponent extra = new TextComponent(TextComponent.fromLegacyText("§e§nKlicke hier"));
        extra.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url + name));
        extra.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§aKlicke, um Vote " + number + " aufzurufen!")));
        result.addExtra(extra);
        return result;
    }

    public static boolean isListed(String service) {
        for (TrustedServices link : TrustedServices.values()) {
            if (link.getService().equalsIgnoreCase(service)) {
                return true;
            }
        }
        return false;
    }

}
