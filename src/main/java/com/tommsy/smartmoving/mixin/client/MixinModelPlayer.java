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

import static com.tommsy.smartmoving.client.render.RenderUtils.Eighth;
import static com.tommsy.smartmoving.client.render.RenderUtils.Half;
import static com.tommsy.smartmoving.client.render.RenderUtils.Quarter;
import static com.tommsy.smartmoving.client.render.RenderUtils.RadianToAngle;
import static com.tommsy.smartmoving.client.render.RenderUtils.Sixteenth;
import static com.tommsy.smartmoving.client.render.RenderUtils.Sixtyfourth;
import static com.tommsy.smartmoving.client.render.RenderUtils.Thirtysecond;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.tommsy.smartmoving.client.SmartMovingAbstractClientPlayer;
import com.tommsy.smartmoving.client.model.SmartMovingModelPlayer;
import com.tommsy.smartmoving.common.SmartMovingPlayerState;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Mixin(ModelPlayer.class)
public abstract class MixinModelPlayer extends MixinModelBiped implements SmartMovingModelPlayer {

    @Shadow
    @Final
    private boolean smallArms;

    @Shadow
    public ModelRenderer bipedLeftArmwear;
    @Shadow
    public ModelRenderer bipedRightArmwear;
    @Shadow
    public ModelRenderer bipedLeftLegwear;
    @Shadow
    public ModelRenderer bipedRightLegwear;
    @Shadow
    public ModelRenderer bipedBodyWear;

    @Shadow
    @Final
    private ModelRenderer bipedCape;
    @Shadow
    @Final
    private ModelRenderer bipedDeadmau5Head;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onConstructed(float modelSize, boolean useSmallArms, CallbackInfo ci) {

    }

    @Inject(method = "render", at = @At("HEAD"))
    private void preRender(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale, CallbackInfo ci) {

    }

    @Inject(method = "render", at = @At("RETURN"))
    private void postRender(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale, CallbackInfo ci) {

    }

    @Inject(method = "setRotationAngles", at = @At("HEAD"), cancellable = true)
    private void preSetRotationAngles(float totalHorizontalDistance, float currentHorizontalSpeed, float ageInTicks, float headYawAngle, float headPitchAngle, float scaleFactor,
            Entity entity, CallbackInfo ci) {
        SmartMovingAbstractClientPlayer player = (SmartMovingAbstractClientPlayer) entity;
        SmartMovingPlayerState state = player.getState();
        if (state.isCrawling) {
            double horizontalMagnitudeSquared = entity.motionX * entity.motionX + entity.motionZ + entity.motionZ;
            float viewHorizontalAngelOffset = ((AbstractClientPlayer) entity).cameraYaw;
            float currentHorizontalSpeedFlattened = (float) (horizontalMagnitudeSquared / MathHelper.fastInvSqrt(horizontalMagnitudeSquared));

            float distance = totalHorizontalDistance * 1.3F;
            float walkFactor = factor(currentHorizontalSpeedFlattened, 0F, 0.12951545F);
            float standFactor = factor(currentHorizontalSpeedFlattened, 0.12951545F, 0F);

            bipedHead.rotateAngleZ = -viewHorizontalAngelOffset / RadianToAngle;
            bipedHead.rotateAngleX = -Eighth;
            bipedHead.rotationPointZ = -2F;

            // bipedTorso.rotationOrder = ModelRotationRenderer.YZX;
            // bipedTorso.rotateAngleX = Quarter - Thirtysecond;
            // bipedTorso.rotationPointY = 3F;
            // bipedTorso.rotateAngleZ = MathHelper.cos(distance + Quarter) * Sixtyfourth * walkFactor;
            bipedBody.rotateAngleY = MathHelper.cos(distance + Half) * Sixtyfourth * walkFactor;

            bipedRightLeg.rotateAngleX = (MathHelper.cos(distance - Quarter) * Sixtyfourth + Thirtysecond) * walkFactor + Thirtysecond * standFactor;
            bipedLeftLeg.rotateAngleX = (MathHelper.cos(distance - Half - Quarter) * Sixtyfourth + Thirtysecond) * walkFactor + Thirtysecond * standFactor;

            bipedRightLeg.rotateAngleZ = (MathHelper.cos(distance - Quarter) + 1F) * 0.25F * walkFactor + Thirtysecond * standFactor;
            bipedLeftLeg.rotateAngleZ = (MathHelper.cos(distance - Quarter) - 1F) * 0.25F * walkFactor - Thirtysecond * standFactor;

            // if (scaleLegType != NoScaleStart)
            // setLegScales(
            // 1F + (MathHelper.cos(distance + Quarter - Quarter) - 1F) * 0.25F * walkFactor,
            // 1F + (MathHelper.cos(distance - Quarter - Quarter) - 1F) * 0.25F * walkFactor);
            //
            // bipedRightArm.rotationOrder = ModelRotationRenderer.YZX;
            // bipedLeftArm.rotationOrder = ModelRotationRenderer.YZX;

            bipedRightArm.rotateAngleX = Half + Eighth;
            bipedLeftArm.rotateAngleX = Half + Eighth;

            bipedRightArm.rotateAngleZ = ((MathHelper.cos(distance + Half)) * Sixtyfourth + Thirtysecond) * walkFactor + Sixteenth * standFactor;
            bipedLeftArm.rotateAngleZ = ((MathHelper.cos(distance + Half)) * Sixtyfourth - Thirtysecond) * walkFactor - Sixteenth * standFactor;

            bipedRightArm.rotateAngleY = -Quarter;
            bipedLeftArm.rotateAngleY = Quarter;

            // if (scaleArmType != NoScaleStart)
            // setArmScales(
            // 1F + (MathHelper.cos(distance + Quarter) - 1F) * 0.15F * walkFactor,
            // 1F + (MathHelper.cos(distance - Quarter) - 1F) * 0.15F * walkFactor);
        }
    }

    private static float factor(float x, float x0, float x1) {
        if (x0 > x1) {
            if (x <= x1)
                return 1F;
            if (x >= x0)
                return 0F;
            return (x0 - x) / (x0 - x1);
        } else {
            if (x >= x1)
                return 1F;
            if (x <= x0)
                return 0F;
            return (x - x0) / (x1 - x0);
        }
    }

    @Inject(method = "setRotationAngles", at = @At("RETURN"))
    public void postSetRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn,
            CallbackInfo ci) {

    }
}
