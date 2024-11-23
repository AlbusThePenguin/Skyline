/*
 * This file is part of Skyline.
 *
 * Skyline is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Skyline is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Skyline. If not, see <http://www.gnu.org/licenses/>.
 */
package me.albusthepenguin.skyline.Config;

import me.albusthepenguin.skyline.Skyline;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Configuration {

    private final Skyline skyline;

    private final static Map<ConfigType, YamlConfiguration> configurations = new ConcurrentHashMap<>();

    public Configuration(Skyline skyline) {
        this.skyline = skyline;
    }

    public void load() {
        for(ConfigType configType : ConfigType.values()) {
            File file = getFile(configType);

            if(!file.exists()) {
                skyline.saveResource(file.getName(), false);
            }

            YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
            yamlConfiguration.options().copyDefaults(true);
            configurations.putIfAbsent(configType, yamlConfiguration);
        }
    }

    public YamlConfiguration getConfig(ConfigType configType) {
        return configurations.getOrDefault(configType, null);
    }

    public void save(ConfigType configType) {
        try {
            getConfig(configType).save(getFile(configType));
        } catch (IOException e) {
            skyline.getLogger().severe("Could not save " + configType.name() + " because " + e);
        }
    }

    public void reload(ConfigType configType) {
        configurations.put(configType, YamlConfiguration.loadConfiguration(getFile(configType)));
    }

    @SuppressWarnings("all")
    private File getFile(ConfigType configType) {
        if(configType == ConfigType.Messages) {
            return new File(skyline.getDataFolder(), "messages.yml");
        } else {
            return new File(skyline.getDataFolder(), "config.yml");
        }
    }

}