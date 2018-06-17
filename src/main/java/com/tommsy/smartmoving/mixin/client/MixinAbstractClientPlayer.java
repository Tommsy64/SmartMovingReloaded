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

package com.tommsy.smartmoving.mixin.client;

import org.spongepowered.asm.mixin.Mixin;

import com.tommsy.smartmoving.client.SmartMovingAbstractClientPlayer;
import com.tommsy.smartmoving.mixin.MixinEntityPlayer;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.PlayerCapabilities;

@Mixin(AbstractClientPlayer.class)
public abstract class MixinAbstractClientPlayer extends MixinEntityPlayer implements SmartMovingAbstractClientPlayer {
    @Override
    public boolean isJumping() {
        return this.isJumping;
    }

    @Override
    public PlayerCapabilities getCapabilities() {
        return this.capabilities;
    }

    @Override
    public boolean isOnGround() {
        return this.onGround;
    }

    @Override
    public float getFallDistance() {
        return this.fallDistance;
    }

    @Override
    public void setFallDistance(float fallDistance) {
        this.fallDistance = fallDistance;
    }
}
