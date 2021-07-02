package de.hakuyamu.skybee.votesystem.models;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class CustomText {

    private final TextComponent result;

    public CustomText(String initial, String clickable, String url, String hover) {
        result = new TextComponent(TextComponent.fromLegacyText(initial));
        TextComponent extra = new TextComponent(TextComponent.fromLegacyText(clickable));
        extra.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
        extra.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hover)));
        result.addExtra(extra);
    }

    public TextComponent getResult() {
        return result;
    }

}
