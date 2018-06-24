/*
* Smart Moving Reloaded
* Copyright (C) 2018  Tommsy64
*
* Smart Moving Reloaded is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Smart Moving Reloaded is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Smart Moving Reloaded.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.tommsy.smartmoving.config;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Name;

import com.tommsy.smartmoving.SmartMovingMod.SmartMovingInfo;

/**
 * do not set the config variables client-side. Instead have two sets of config variables, the server set (always updated from the config file) and the client set (always received from the
 * server).
 */
@Config(modid = SmartMovingInfo.MODID, name = SmartMovingInfo.CONFIG_FILE_NAME)
public final class SmartMovingConfig {

    @Name("Subcat Name")
    public static SubCategory subcat = new SubCategory();

    private static class SubCategory {
        public boolean someBool;
        public int relatedInt;
    }

    private SmartMovingConfig() {}
}
