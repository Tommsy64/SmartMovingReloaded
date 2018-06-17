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

package com.tommsy.smartmoving.server;

import com.tommsy.smartmoving.network.SmartMovingPacketReceiver;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SmartMovingServer implements SmartMovingPacketReceiver {

    public static final SmartMovingServer INSTANCE = new SmartMovingServer();

    // public static ILocalUserNameProvider localUserNameProvider = null;

    @Override
    public boolean processStatePacket(FMLProxyPacket packet, SmartMovingServerPlayer player, int entityId, long state) {
        player.getPlayerHandler().processStatePacket(packet, state);
        return true;
    }

    @Override
    public boolean processConfigInfoPacket(FMLProxyPacket packet, SmartMovingServerPlayer player, String info) {
        // player.getPlayerHandler().processConfigPacket(info);
        return true;
    }

    @Override
    public boolean processConfigContentPacket(FMLProxyPacket packet, SmartMovingServerPlayer player, String[] content, String username) {
        return false;
    }

    @Override
    public boolean processConfigChangePacket(FMLProxyPacket packet, SmartMovingServerPlayer player) {
        // player.getPlayerHandler().processConfigChangePacket(localUserNameProvider != null ? localUserNameProvider.getLocalConfigUserName() : null);
        return true;
    }

    @Override
    public boolean processSpeedChangePacket(FMLProxyPacket packet, SmartMovingServerPlayer player, int difference, String username) {
        // player.getPlayerHandler().processSpeedChangePacket(difference, localUserNameProvider != null ? localUserNameProvider.getLocalSpeedUserName() : null);
        return true;
    }

    @Override
    public boolean processHungerChangePacket(FMLProxyPacket packet, SmartMovingServerPlayer player, float hunger) {
        // player.getPlayerHandler().processHungerChangePacket(hunger);
        return true;
    }

    @Override
    public boolean processSoundPacket(FMLProxyPacket packet, SmartMovingServerPlayer player, String soundId, float volume, float pitch) {
        // player.getPlayerHandler().processSoundPacket(soundId, volume, pitch);
        return true;
    }
}
