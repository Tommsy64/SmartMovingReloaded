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

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.tommsy.smartmoving.common.SmartMovingEntityLivingBase;

import net.minecraft.entity.EntityLivingBase;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase extends MixinEntity implements SmartMovingEntityLivingBase {

    @Shadow
    protected boolean isJumping;
    @Shadow
    public float prevLimbSwingAmount;
    @Shadow
    public float limbSwingAmount;
    @Shadow
    public float limbSwing;

    @Override
    public float getPrevLimbSwingAmount() {
        return this.prevLimbSwingAmount;
    }

    @Override
    public float getLimbSwingAmount() {
        return this.limbSwingAmount;
    }

    @Override
    public float getLimbSwing() {
        return this.limbSwing;
    }

    @Override
    public void setPrevLimbSwingAmount(float prevLimbSwingAmount) {
        this.prevLimbSwingAmount = prevLimbSwingAmount;
    }

    @Override
    public void setLimbSwingAmount(float limbSwingAmount) {
        this.limbSwingAmount = limbSwingAmount;
    }

    @Override
    public void setLimbSwing(float limbSwing) {
        this.limbSwing = limbSwing;
    }

    @Shadow
    @Override
    public abstract Random getRNG();
}
