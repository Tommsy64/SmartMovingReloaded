// ==========================================================================
// Mixin Player API
// Copyright (C) 2018  Tommsy64
//
// Mixin Player API is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// Mixin Player API is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with Mixin Player API.  If not, see <http://www.gnu.org/licenses/>.
// ==========================================================================
package com.tommsy.playerapi;

import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = MixinPlayerAPI.MODID, name = MixinPlayerAPI.NAME, version = MixinPlayerAPI.VERSION)
public class MixinPlayerAPI {
    public static final String MODID = "@MODID@";
    public static final String NAME = "@NAME@";
    public static final String VERSION = "@VERSION@";

    public static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {

    }
}
