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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import com.tommsy.smartmoving.client.SmartMovingAbstractClientPlayer;
import com.tommsy.smartmoving.mixin.MixinEntityPlayer;

@Mixin(AbstractClientPlayer.class)
public abstract class MixinAbstractClientPlayer extends MixinEntityPlayer implements SmartMovingAbstractClientPlayer {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onConstructed(CallbackInfo ci) {
        fadingPerspectiveFactor = this.getAIMoveSpeed();
    }

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

    @Unique
    float fadingPerspectiveFactor;

    @Redirect(method = "getFovModifier", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/attributes/IAttributeInstance;getAttributeValue()D"))
    public double adjustMovementSpeedAttribute(IAttributeInstance iAttributeInstance) {
        return fadingPerspectiveFactor;
    }
}
