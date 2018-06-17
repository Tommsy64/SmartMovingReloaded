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

import com.tommsy.smartmoving.client.SmartMovingClient;
import com.tommsy.smartmoving.server.SmartMovingServer;

import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;

public class SmartMovingNetworkHandler {
    @SubscribeEvent
    public void onServerPacket(ServerCustomPacketEvent event) {
        SmartMovingPacketStream.receivePacket(event.getPacket(), SmartMovingServer.INSTANCE, ((NetHandlerPlayServer) event.getHandler()).player);
    }

    @SubscribeEvent
    public void onClientPacket(ClientCustomPacketEvent event) {
        SmartMovingPacketStream.receivePacket(event.getPacket(), SmartMovingClient.INSTANCE, null);
    }
}
