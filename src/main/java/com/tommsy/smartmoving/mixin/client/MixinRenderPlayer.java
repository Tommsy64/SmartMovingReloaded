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

import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;

import com.tommsy.smartmoving.client.SmartMovingAbstractClientPlayer;
import com.tommsy.smartmoving.client.model.LayerPlayerArmor;
import com.tommsy.smartmoving.client.model.SmartMovingModelPlayer;
import com.tommsy.smartmoving.client.render.IMixinRenderPlayer;

@Mixin(RenderPlayer.class)
@Implements(@Interface(iface = IMixinRenderPlayer.class, prefix = "sm$"))
public abstract class MixinRenderPlayer extends RenderLivingBase<AbstractClientPlayer> {

    private MixinRenderPlayer(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn) {
        super(renderManagerIn, modelBaseIn, shadowSizeIn);
    }

    @Redirect(method = "<init>(Lnet/minecraft/client/renderer/entity/RenderManager;Z)V", at = @At(value = "NEW", target = "Lnet/minecraft/client/renderer/entity/layers/LayerBipedArmor;"))
    private LayerBipedArmor constructLayerPlayerArmor(RenderLivingBase<?> $this) {
        return new LayerPlayerArmor($this);
    }

    @Intrinsic
    public SmartMovingModelPlayer sm$getMainModel() {
        return (SmartMovingModelPlayer) super.getMainModel();
    }

    @Inject(method = "doRender", at = @At("HEAD"))
    private void preDoRender(AbstractClientPlayer entityClientPlayer, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
        // SmartMovingAbstractClientPlayer player = (SmartMovingAbstractClientPlayer) entityClientPlayer;

    }

    @Inject(method = "doRender", at = @At("RETURN"))
    private void postDoRender(AbstractClientPlayer entityClientPlayer, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {

    }

    @Inject(method = "applyRotations", at = @At("HEAD"))
    private void applyRotations(AbstractClientPlayer entityClientPlayer, float totalTime, float rotationYaw, float partialTicks, CallbackInfo ci) {}

    @Override
    protected void renderLayers(AbstractClientPlayer entityClientPlayer, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw,
            float headPitch, float scaleIn) {
        super.renderLayers(entityClientPlayer, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scaleIn);
    }

    @Inject(method = "renderLivingAt", at = @At("HEAD"))
    private void preRenderLivingAt(AbstractClientPlayer entityClientPlayer, double x, double y, double z, CallbackInfo ci) {

    }

    @Override
    public void renderName(AbstractClientPlayer entityClientPlayer, double x, double y, double z) {
        super.renderName(entityClientPlayer, x, y, z);
    }

    /**
     * Defines what float the third param in setRotationAngles of ModelBase is
     */
    @Override
    protected float handleRotationFloat(AbstractClientPlayer entityClientPlayer, float partialTicks) {
        return super.handleRotationFloat(entityClientPlayer, partialTicks);
    }
}
