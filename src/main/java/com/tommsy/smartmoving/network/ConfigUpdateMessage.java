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

package com.tommsy.smartmoving.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import lombok.NoArgsConstructor;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.tommsy.smartmoving.SmartMovingMod;
import com.tommsy.smartmoving.config.SmartMovingConfigAccess;
import com.tommsy.smartmoving.config.SmartMovingConfigAccess.SmartMovingConfig;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;

@NoArgsConstructor
public class ConfigUpdateMessage implements IMessage {
    private volatile static byte[] cacheBytes;
    private volatile static SmartMovingConfig cacheConfig;

    private SmartMovingConfig config;

    public ConfigUpdateMessage(SmartMovingConfig config) {
        this.config = config;
    }

    private static class AccessibleByteArrayOutputStream extends ByteArrayOutputStream {
        public byte[] getBuffer() {
            return this.buf;
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        synchronized (this) {
            if (!config.equals(cacheConfig)) {
                AccessibleByteArrayOutputStream bytes = new AccessibleByteArrayOutputStream();
                SmartMovingConfigAccess.serialize(config, bytes);
                cacheBytes = bytes.getBuffer();
                // This creates a deep clone of the config object
                cacheConfig = SmartMovingConfigAccess.deserialize(new ByteArrayInputStream(cacheBytes));
            }
        }

        buf.writeBytes(cacheBytes);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        config = SmartMovingConfigAccess.deserialize(new ByteBufInputStream(buf));
    }

    public static class ConfigUpdateMessageHandler implements IMessageHandler<ConfigUpdateMessage, IMessage> {
        @Override
        public IMessage onMessage(ConfigUpdateMessage message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                SmartMovingConfigAccess.config = message.config;
                SmartMovingMod.logger.debug("Received config from server.");
            });
            return null;
        }
    }
}
