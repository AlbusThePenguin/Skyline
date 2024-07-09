/*
 * This file is part of Skyline, licensed under the MIT License.
 *
 *  Copyright (c) AlbusThePenguin (Albus) <SlapTheTroll@Spigot>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package me.albusthepenguin.skyline.Misc;

import me.albusthepenguin.skyline.API.ConfigType;
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
            skyline.getDebug().write("Could not save " + configType.name() + " because " + e);
        }
    }

    public void reload(ConfigType configType) {
        configurations.put(configType, YamlConfiguration.loadConfiguration(getFile(configType)));
    }

    @SuppressWarnings("all")
    private File getFile(ConfigType configType) {
        return switch (configType) {
            case Messages -> new File(skyline.getDataFolder(), "messages.yml");
            default -> new File(skyline.getDataFolder(), "config.yml");
        };
    }

}