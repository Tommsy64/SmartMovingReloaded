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

package com.tommsy.smartmoving.client;

import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

import com.tommsy.smartmoving.SmartMovingMod;
import com.tommsy.smartmoving.SmartMovingMod.SmartMovingInfo;
import com.tommsy.smartmoving.network.SmartMovingNetworkHandler;

public class SmartMovingClientEventHandler {
    @SubscribeEvent
    public void onConfigChangedEvent(OnConfigChangedEvent event) {
        if (!event.getModID().equals(SmartMovingInfo.MODID))
            return;
        ConfigManager.sync(SmartMovingInfo.MODID, Type.INSTANCE);
        SmartMovingMod.logger.debug("Config updated, synchronizing to all players.");
        SmartMovingNetworkHandler.sendConfigUpdateToAll();
    }

    @SubscribeEvent
    public void tickStart(ClientTickEvent event) {

    }
}
