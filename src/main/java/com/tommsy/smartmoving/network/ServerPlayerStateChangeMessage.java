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

import lombok.NoArgsConstructor;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.tommsy.smartmoving.SmartMovingMod;
import com.tommsy.smartmoving.client.SmartMovingOtherPlayer;
import com.tommsy.smartmoving.common.SmartMovingPlayerState;

import io.netty.buffer.ByteBuf;

@NoArgsConstructor
public class ServerPlayerStateChangeMessage implements IMessage {
    private ByteBuf stateBytes;
    private int entityId;

    public ServerPlayerStateChangeMessage(ByteBuf stateBytes, int entityId) {
        this.stateBytes = stateBytes;
        this.entityId = entityId;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeBytes(stateBytes);
        stateBytes.release();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.entityId = buf.readInt();
        this.stateBytes = buf.asReadOnly();
        stateBytes.retain();
    }

    private void writeToPlayerState(SmartMovingPlayerState state) {
        state.readFromBuffer(stateBytes);
        stateBytes.release();
    }

    // Default constructor implicitly defined
    public static class ServerPlayerStateChangeHandler implements IMessageHandler<ServerPlayerStateChangeMessage, IMessage> {
        private static final Minecraft MC = Minecraft.getMinecraft();

        @Override
        public IMessage onMessage(ServerPlayerStateChangeMessage message, MessageContext ctx) {
            Entity entity = MC.world.getEntityByID(message.entityId);
            if (entity == null)
                SmartMovingMod.logger.warn("Null entity when handling {}", message.getClass().getSimpleName());
            else if (!(entity instanceof SmartMovingOtherPlayer))
                SmartMovingMod.logger.warn("Entity not instance of {} when handling {}", SmartMovingOtherPlayer.class.getSimpleName(), message.getClass().getSimpleName());
            else
                message.writeToPlayerState(((SmartMovingOtherPlayer) entity).getState());
            return null;
        }
    }
}
