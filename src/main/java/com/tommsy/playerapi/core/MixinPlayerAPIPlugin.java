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
package com.tommsy.playerapi.core;

import java.util.Map;

import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.MCVersion("@MCVERSION@")
@IFMLLoadingPlugin.TransformerExclusions("api.player.forge")
public class MixinPlayerAPIPlugin implements IFMLLoadingPlugin {

    public MixinPlayerAPIPlugin() {
        MixinBootstrap.init();
        Mixins.addConfigurations("mixins.mixinplayerapi.core.json");
    }

    @Override
    public String[] getASMTransformerClass() {
        return null;
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {}

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
