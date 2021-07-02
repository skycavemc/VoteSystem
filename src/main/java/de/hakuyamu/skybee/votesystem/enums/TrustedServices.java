package de.hakuyamu.skybee.votesystem.enums;

import de.hakuyamu.skybee.votesystem.models.CustomText;
import net.md_5.bungee.api.chat.BaseComponent;

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

    public BaseComponent getBaseComponent(String name) {
        return new CustomText("§8» §2Link " + number + ": ", "§e§nKlicke hier", url + name, "§aKlicke, um Vote " + number + " aufzurufen!").getResult();
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
