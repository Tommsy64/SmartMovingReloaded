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

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.tommsy.smartmoving.common.SmartMovingPlayerState;
import com.tommsy.smartmoving.server.SmartMovingServerPlayer;

import io.netty.buffer.ByteBuf;

// A default constructor is always required for an IMessage
@NoArgsConstructor
public class ClientPlayerStateChangeMessage implements IMessage {
    private SmartMovingPlayerState state;

    public ClientPlayerStateChangeMessage(SmartMovingPlayerState state) {
        this.state = state;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        state.writeToBuffer(buf);
    }

    private ByteBuf stateBytes;

    @Override
    public void fromBytes(ByteBuf buf) {
        this.stateBytes = buf.asReadOnly();
        stateBytes.retain();
    }

    private void writeToPlayerState(SmartMovingPlayerState state) {
        state.readFromBuffer(stateBytes);
        stateBytes.release();
    }

    // Default constructor implicitly defined
    public static class ClientPlayerStateChangeHandler implements IMessageHandler<ClientPlayerStateChangeMessage, IMessage> {
        @Override
        public IMessage onMessage(ClientPlayerStateChangeMessage message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            message.stateBytes.retain();
            SmartMovingNetworkHandler.sendServerPlayerStateChange(message.stateBytes.duplicate(), player);
            player.getServerWorld().addScheduledTask(() -> {
                message.writeToPlayerState(((SmartMovingServerPlayer) player).getState());
            });
            return null;
        }
    }
}
