package de.hakuyamu.skybee.votesystem.models;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

@SuppressWarnings("unused")
public class AutoSaveConfig extends YamlConfiguration {

    private File source;

    /**
     * Constructs a YamlConfiguration from the given source.
     * @param source The source file
     */
    public AutoSaveConfig(@NotNull File source) {
        super();
        this.source = source;
        load(source);
    }

    /**
     * Loads from the given source file and remembers the source.
     * @param file The source file
     */
    @Override
    public void load(@NotNull File file) {
        try {
            super.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        this.source = file;
    }

    /**
     * Stores the value at the given path of the YAML configuration. Automatically saves the changes.
     * @param path Path to the object
     * @param value Object to store
     */
    @Override
    public void set(@NotNull String path, @Nullable Object value) {
        super.set(path, value);
        save();
    }

    /**
     * Gets the current source file
     * @return The source file
     */
    public File getSource() {
        return source;
    }

    /**
     * Saves the YamlConfiguration to the source that has been set.
     */
    public void save() {
        try {
            save(source);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
