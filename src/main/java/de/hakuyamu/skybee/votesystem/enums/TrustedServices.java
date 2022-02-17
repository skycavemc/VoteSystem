package de.hakuyamu.skybee.votesystem.enums;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

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

    public String getService() {
        return service;
    }

    public Component getVoteLink(String name) {
        return Component.text("§8» §2Link " + number + ": ")
                .append(Component.text("§e§nKlicke hier")
                        .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, url + name))
                        .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT,
                                Component.text("§aKlicke, um Vote " + number + " aufzurufen!"))));
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
