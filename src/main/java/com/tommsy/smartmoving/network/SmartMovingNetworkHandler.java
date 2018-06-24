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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import com.tommsy.smartmoving.SmartMovingMod.SmartMovingInfo;
import com.tommsy.smartmoving.common.SmartMovingPlayerState;
import com.tommsy.smartmoving.network.ClientPlayerStateChangeMessage.ClientPlayerStateChangeHandler;
import com.tommsy.smartmoving.network.ServerPlayerStateChangeMessage.ServerPlayerStateChangeHandler;

import io.netty.buffer.ByteBuf;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SmartMovingNetworkHandler {
    private static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(SmartMovingInfo.NETWORK_ID);

    public static void registerMessages() {
        int i = 0; // Message discriminator
        INSTANCE.registerMessage(ClientPlayerStateChangeHandler.class, ClientPlayerStateChangeMessage.class, i++, Side.SERVER);
        INSTANCE.registerMessage(ServerPlayerStateChangeHandler.class, ServerPlayerStateChangeMessage.class, i++, Side.CLIENT);
    }

    public static void sendClientPlayerStateChange(SmartMovingPlayerState state) {
        INSTANCE.sendToServer(new ClientPlayerStateChangeMessage(state));
    }

    public static void sendServerPlayerStateChange(ByteBuf stateBytes, Entity entity) {
        INSTANCE.sendToAllTracking(new ServerPlayerStateChangeMessage(stateBytes, entity.getEntityId()), entity);
    }
}
