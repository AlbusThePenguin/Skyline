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
package me.albusthepenguin.skyline.Misc;

import me.albusthepenguin.skyline.API.ConfigType;
import me.albusthepenguin.skyline.Skyline;

public class Debug {

    private final Skyline skyline;

    public Debug(Skyline skyline) {
        this.skyline = skyline;
    }

    public void write(String text) {
        if(skyline.getConfiguration().getConfig(ConfigType.Config).getBoolean("Settings.debug")) {
            skyline.getLogger().info(text);
        }
    }

}