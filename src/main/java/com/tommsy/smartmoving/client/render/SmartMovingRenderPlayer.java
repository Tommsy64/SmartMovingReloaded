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

package com.tommsy.smartmoving.client.render;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;

public class SmartMovingRenderPlayer extends RenderPlayer {

    // private IModelPlayer[] allIModelPlayers;

    // private final SmartMovingRender render;

    public SmartMovingRenderPlayer(RenderManager renderManager) {
        this(renderManager, false);
    }

    public SmartMovingRenderPlayer(RenderManager renderManager, boolean useSmallArms) {
        super(renderManager, useSmallArms);

        // render = new SmartMovingRender(this);
    }

    public net.minecraft.client.model.ModelPlayer getModelBipedMain() {
        return this.getMainModel();
    }

    @SuppressWarnings("rawtypes")
    public net.minecraft.client.model.ModelBiped getModelArmorChestplate() {
        for (LayerRenderer layer : this.layerRenderers)
            if (layer instanceof LayerArmorBase)
                return (net.minecraft.client.model.ModelBiped) ((LayerArmorBase) layer).modelArmor;
        return null;
    }

    @SuppressWarnings("rawtypes")
    public net.minecraft.client.model.ModelBiped getModelArmor() {
        for (LayerRenderer layer : this.layerRenderers)
            if (layer instanceof LayerArmorBase)
                return (net.minecraft.client.model.ModelBiped) ((LayerArmorBase) layer).modelLeggings;
        return null;
    }

    // modelRenderer
    // private final static Field _playerHead = Reflect.GetField(LayerCustomHead.class, SmartRenderInstall.LayerCustomHead_playerHead);

}
