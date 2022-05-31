package de.hakuyamu.skybee.votesystem.util;

import de.hakuyamu.skybee.votesystem.VoteSystem;
import net.kyori.adventure.text.Component;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class Utils {

    private static final JavaPlugin main = JavaPlugin.getPlugin(VoteSystem.class);

    public static boolean extractPluginResource(JavaPlugin plugin, String resource) {
        File start = new File(plugin.getDataFolder(), resource);
        if (start.isFile()) {
            return true;
        }
        InputStream stream = plugin.getClass().getClassLoader().getResourceAsStream(start.getName());
        if (stream == null) {
            plugin.getLogger().severe(String.format("Missing \"%s\" file in resources.", resource));
            return false;
        }

        try {
            Files.copy(stream, start.toPath());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static long minutesToTicks(long minutes) {
        return minutes * 60 * 20;
    }

    public static void broadcast(String text) {
        main.getServer().broadcast(Component.text(text));
    }

}
