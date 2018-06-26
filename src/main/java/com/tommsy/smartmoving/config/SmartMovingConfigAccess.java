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

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeDouble;

import com.tommsy.smartmoving.SmartMovingMod;
import com.tommsy.smartmoving.SmartMovingMod.SmartMovingInfo;

@Config(modid = SmartMovingInfo.MODID, name = SmartMovingInfo.CONFIG_FILE_NAME, category = "")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SmartMovingConfigAccess {

    @Config.Name("general")
    public static final SmartMovingConfig LOCAL_CONFIG = new SmartMovingConfig();

    @Config.Ignore
    public static SmartMovingConfig config = LOCAL_CONFIG;

    @ToString
    @EqualsAndHashCode
    @SuppressWarnings("serial")
    public static class SmartMovingConfig implements Serializable {

        @Name("Movement")
        public Movement movement = new Movement();

        @ToString
        @EqualsAndHashCode
        public class Movement implements Serializable {
            @Comment("Fall distance for stopping ground based moves like crawling or sliding (>= 0)")
            @RangeDouble(min = 0)
            public float fallingDistanceStart = 3;
        }
    }

    public static void serialize(SmartMovingConfig config, OutputStream output) {
        ObjectOutputStream objectStream = null;
        try {
            objectStream = new ObjectOutputStream(output);
            objectStream.writeObject(config);
        } catch (IOException e) {
            SmartMovingMod.logger.error("Error writing to object stream", e);
        } finally {
            if (objectStream != null)
                try {
                    objectStream.close();
                } catch (IOException e) {
                    SmartMovingMod.logger.error("Error closing object output stream", e);
                }
        }
    }

    public static SmartMovingConfig deserialize(InputStream input) {
        ObjectInputStream objectStream = null;
        SmartMovingConfig config = SmartMovingConfigAccess.LOCAL_CONFIG;
        try {
            objectStream = new ObjectInputStream(input);
            config = (SmartMovingConfig) objectStream.readObject();
        } catch (ClassNotFoundException e) {
            SmartMovingMod.logger.fatal("SmartMovingConfig class not found! Are the client and server versions the same?", e);
        } catch (IOException e) {
            SmartMovingMod.logger.error("Error reading object stream", e);
        } finally {
            if (objectStream != null)
                try {
                    objectStream.close();
                } catch (IOException e) {
                    SmartMovingMod.logger.error("Error closing object input stream", e);
                }
        }
        return config;
    }
}
