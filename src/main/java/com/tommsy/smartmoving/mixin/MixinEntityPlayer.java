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

package com.tommsy.smartmoving.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer extends MixinEntityLivingBase {
    @Shadow
    protected void jump() {}

    @Shadow
    public PlayerCapabilities capabilities;

    @Shadow
    public void travel(float strafe, float vertical, float forward) {}

    @Shadow
    public void onLivingUpdate() {}

    @Shadow
    public boolean isEntityInsideOpaqueBlock() {
        return false;
    }

    @Shadow
    protected boolean sleeping;

    @Shadow
    public boolean isPlayerSleeping() {
        return false;
    }

    @Shadow
    public abstract float getAIMoveSpeed();

    @Shadow
    public abstract void addMovementStat(double diffX, double diffY, double diffZ);
}
