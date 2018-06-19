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

package com.tommsy.smartmoving.client.model;

import com.tommsy.smartmoving.client.AbstractSmartMovingClientPlayerHandler.SmartMovingRenderState;
import com.tommsy.smartmoving.client.render.RendererData;

import net.minecraft.block.Block;

public interface SmartMovingModelBiped {
    public void setRenderState(SmartMovingRenderState state);

    public void setCurrentHorizontalSpeedFlattened(float val);

    public void setSmallOverGroundHeight(float val);

    public void setOverGroundBlock(Block block);

    public void setInventory(boolean isInventory);

    public void setSleeping(boolean isSleeping);

    public void setTotalVerticalDistance(float val);

    public void setCurrentVerticalSpeed(float val);

    public void setTotalDistance(float val);

    public void setCurrentSpeed(float val);

    public void setDistance(double val);

    public void setVerticalDistance(double val);

    public void setHorizontalDistance(double val);

    public void setCurrentCameraAngle(float val);

    public float getCurrentCameraAngle();

    public void setCurrentVerticalAngle(float val);

    public void setCurrentHorizontalAngle(float val);

    public void setPrevOuterRenderData(RendererData data);

    public void setRotationYaw(float val);

    public void setForwardRotation(float val);

    public void setWorkingAngle(float val);

}
