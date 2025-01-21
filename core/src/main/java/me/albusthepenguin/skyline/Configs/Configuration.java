package me.albusthepenguin.skyline.Configs;

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

import lombok.Getter;
import me.albusthepenguin.skyline.Skyline;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Configuration {

    private final Skyline skyline;

    @Getter
    private YamlConfiguration yamlConfiguration;

    public Configuration(Skyline skyline) {
        this.skyline = skyline;

        this.load();
    }

    public void load() {
        File file = getFile();

        if(!file.exists()) {
            this.skyline.saveResource(file.getName(), false);
        }

        this.yamlConfiguration = YamlConfiguration.loadConfiguration(file);
    }

    public void reload() {
        this.load();

        this.save();
    }

    public void save() {
        try {
            this.yamlConfiguration.save(getFile());
        } catch (IOException e) {
            skyline.getLogger().severe("Could not save config.yml because " + e);
        }
    }

    private File getFile() {
        return new File(this.skyline.getDataFolder(), "config.yml");
    }

}