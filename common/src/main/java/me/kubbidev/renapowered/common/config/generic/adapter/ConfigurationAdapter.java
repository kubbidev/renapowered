package me.kubbidev.renapowered.common.config.generic.adapter;

import me.kubbidev.renapowered.common.plugin.RenaPlugin;

import java.util.List;
import java.util.Map;

public interface ConfigurationAdapter {

    RenaPlugin getPlugin();

    void reload();

    String getString(String path, String def);

    int getInteger(String path, int def);

    boolean getBoolean(String path, boolean def);

    List<String> getStringList(String path, List<String> def);

    Map<String, String> getStringMap(String path, Map<String, String> def);

}