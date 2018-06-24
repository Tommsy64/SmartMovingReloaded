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

package com.tommsy.smartmoving;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tommsy.smartmoving.SmartMovingMod.SmartMovingInfo;
import com.tommsy.smartmoving.client.SmartMovingClientEventHandler;
import com.tommsy.smartmoving.network.SmartMovingNetworkHandler;

@Mod(modid = SmartMovingInfo.MODID, name = SmartMovingInfo.NAME, version = SmartMovingInfo.VERSION/* @MCVERSIONDEP@ */)
public class SmartMovingMod {
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public final class SmartMovingInfo {
        public static final String MODID = "@MODID@";
        public static final String NAME = "@NAME@";
        public static final String VERSION = "@VERSION@";

        // public static final String COMMUNICATION_VERSION = "0.0.1";
        public static final String NETWORK_ID = "SmrtMvng";

        public static final String CONFIG_FILE_NAME = "SmartMoving";
    }

    public static Logger logger;

    @SidedProxy(serverSide = "com.tommsy.smartmoving.SmartMovingMod$CommonProxy", clientSide = "com.tommsy.smartmoving.SmartMovingMod$ClientProxy")
    public static CommonProxy proxy;

    /**
     * Utility field so that casting to {@linkplain ClientProxy } isn't needed every time.
     */
    @SideOnly(Side.CLIENT)
    public static ClientProxy clientProxy;

    public static class CommonProxy {
        public void preInit(FMLPreInitializationEvent event) {
            logger = event.getModLog();
        }

        public void init(FMLInitializationEvent event) {
            SmartMovingNetworkHandler.registerMessages();
        }
    }

    public static class ClientProxy extends CommonProxy {
        public KeyBinding keyBindGrab;

        @Override
        public void preInit(FMLPreInitializationEvent event) {
            clientProxy = this;
            super.preInit(event);
        }

        @Override
        public void init(FMLInitializationEvent event) {
            super.init(event);

            keyBindGrab = new KeyBinding("key.grab.desc", Keyboard.KEY_R, "key.categories.movement");

            ClientRegistry.registerKeyBinding(keyBindGrab);

            MinecraftForge.EVENT_BUS.register(new SmartMovingClientEventHandler());

            // ClientRegistry.registerKeyBinding(Options.keyBindGrab);
            // ClientRegistry.registerKeyBinding(Options.keyBindConfigToggle);
            // ClientRegistry.registerKeyBinding(Options.keyBindSpeedIncrease);
            // ClientRegistry.registerKeyBinding(Options.keyBindSpeedDecrease);

            // SmartMovingMod.logger.info("Using communication protocal version {}", SmartMovingInfo.COMMUNICATION_VERSION);
        }
    }

    @EventHandler
    public static void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @EventHandler
    public static void init(FMLInitializationEvent event) {
        proxy.init(event);
    }
}
