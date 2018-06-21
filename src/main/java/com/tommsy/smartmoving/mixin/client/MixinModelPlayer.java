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

import java.util.Random;

import lombok.Getter;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

import com.tommsy.smartmoving.client.model.SmartMovingModelPlayer;
import com.tommsy.smartmoving.client.model.SmartMovingModelPlayerHandler;

@Mixin(ModelPlayer.class)
public abstract class MixinModelPlayer extends ModelBiped implements SmartMovingModelPlayer {

    @Getter
    private SmartMovingModelPlayerHandler handler;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onConstructed(float modelSize, boolean useSmallArms, CallbackInfo ci) {
        this.handler = new SmartMovingModelPlayerHandler(this);
        handler.initialize();
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void preRender(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale, CallbackInfo ci) {
        handler.preRender(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void postRender(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale, CallbackInfo ci) {
        handler.postRender(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
    }

    @Inject(method = "setRotationAngles", at = @At("HEAD"), cancellable = true)
    private void preSetRotationAngles(float totalHorizontalDistance, float currentHorizontalSpeed, float ageInTicks, float headYawAngle, float headPitchAngle, float scaleFactor,
            Entity entity, CallbackInfo ci) {
        boolean cancel = handler.preSetRotationAngles(totalHorizontalDistance, currentHorizontalSpeed, ageInTicks, headYawAngle, headPitchAngle, scaleFactor, entity);
        if (cancel)
            ci.cancel();
    }

    @Inject(method = "setRotationAngles", at = @At("RETURN"))
    private void postSetRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn,
            CallbackInfo ci) {
        handler.postSetRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
    }

    @Override
    public ModelRenderer getRandomModelBox(Random rand) {
        return handler.getRandomModelBox(rand);
    }
}
