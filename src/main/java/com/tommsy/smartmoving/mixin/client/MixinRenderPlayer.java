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
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;

import com.tommsy.smartmoving.client.SmartMovingAbstractClientPlayer;
import com.tommsy.smartmoving.client.SmartMovingClientPlayer;
import com.tommsy.smartmoving.client.model.ModelPlayerArmor;
import com.tommsy.smartmoving.client.model.SmartMovingModelBipedHandler;
import com.tommsy.smartmoving.client.model.SmartMovingModelPlayer;
import com.tommsy.smartmoving.client.model.SmartMovingModelPlayerHandler;
import com.tommsy.smartmoving.client.renderer.ModelCapeRenderer;
import com.tommsy.smartmoving.client.renderer.ModelEarsRenderer;
import com.tommsy.smartmoving.client.renderer.layers.LayerPlayerArmor;
import com.tommsy.smartmoving.client.renderer.layers.SmartMovingLayerElytra;

@Mixin(RenderPlayer.class)
public abstract class MixinRenderPlayer extends RenderLivingBase<AbstractClientPlayer> {

    private MixinRenderPlayer(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn) {
        super(renderManagerIn, modelBaseIn, shadowSizeIn);
    }

    private SmartMovingModelBipedHandler modelArmor, modelLeggings;

    @Shadow
    public abstract ModelPlayer getMainModel();

    @Redirect(method = "<init>(Lnet/minecraft/client/renderer/entity/RenderManager;Z)V", at = @At(value = "NEW", target = "Lnet/minecraft/client/renderer/entity/layers/LayerBipedArmor;"))
    private LayerBipedArmor constructLayerPlayerArmor(RenderLivingBase<AbstractClientPlayer> $this) {
        LayerPlayerArmor layerPlayerArmor = new LayerPlayerArmor($this);
        modelArmor = ((ModelPlayerArmor) layerPlayerArmor.modelArmor).getHandler();
        modelLeggings = ((ModelPlayerArmor) layerPlayerArmor.modelLeggings).getHandler();
        return layerPlayerArmor;
    }

    @ModifyArg(method = "<init>(Lnet/minecraft/client/renderer/entity/RenderManager;Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/RenderPlayer;addLayer(Lnet/minecraft/client/renderer/entity/layers/LayerRenderer;)Z", ordinal = 0), slice = @Slice(from = @At(value = "NEW", target = "Lnet/minecraft/client/renderer/entity/layers/LayerElytra;")), index = 0)
    private LayerRenderer<? extends EntityLivingBase> constructSmartMovingLayerElytra(LayerRenderer<? extends EntityLivingBase> layerRenderer) {
        return new SmartMovingLayerElytra(this);
    }

    @Inject(method = "doRender", at = @At("HEAD"))
    private void preDoRender(AbstractClientPlayer entityClientPlayer, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
        SmartMovingAbstractClientPlayer player = (SmartMovingAbstractClientPlayer) entityClientPlayer;

    }

    @Inject(method = "doRender", at = @At("RETURN"))
    private void postDoRender(AbstractClientPlayer entityClientPlayer, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {

    }

    @Inject(method = "applyRotations", at = @At("HEAD"))
    private void preApplyRotations(AbstractClientPlayer entityClientPlayer, float totalTime, float rotationYaw, float partialTicks, CallbackInfo ci) {
        SmartMovingModelPlayerHandler handle = ((SmartMovingModelPlayer) ((Object) getMainModel())).getHandler();

        SmartMovingAbstractClientPlayer smPlayer = (SmartMovingAbstractClientPlayer) entityClientPlayer;

        boolean isClientPlayer = smPlayer instanceof SmartMovingClientPlayer;
        boolean isBeingRenderedInInventory = partialTicks == 1.0F && isClientPlayer &&
                ((SmartMovingClientPlayer) smPlayer).getMinecraft().currentScreen instanceof GuiInventory;
        if (!isBeingRenderedInInventory) {
            // float forwardRotation = entityClientPlayer.prevRotationYaw + (entityClientPlayer.rotationYaw - entityClientPlayer.prevRotationYaw) * partialTicks;

            // if (handle.isClimbing() || handle.isClimbCrawling() || handle.isCrawlClimbing() || handle.isFlying() || handle.isSwimming() || handle.isDiving() ||
            // handle.isCeilingClimbing() || handle.isHeadJumping || handle.isSliding() || handle.isAngleJumping())
            // entityClientPlayer.renderYawOffset = forwardRotation;

            if (entityClientPlayer.isPlayerSleeping()) {
                rotationYaw = 0;
                // forwardRotation = 0;
            }

            // float workingAngle;
            // Minecraft minecraft = Minecraft.getMinecraft();
            // if (!isClientPlayer) {
            // workingAngle = -entityClientPlayer.rotationYaw;
            // workingAngle += minecraft.getRenderViewEntity().rotationYaw;
            // } else
            // workingAngle = rotationYaw - entityClientPlayer.prevRotationYaw * RadianToAngle;
            //
            // if (minecraft.gameSettings.thirdPersonView == 2 && !((EntityPlayer) minecraft.getRenderViewEntity()).isPlayerSleeping())
            // workingAngle += 180F;

            modelLeggings.rotationYaw = modelArmor.rotationYaw = handle.rotationYaw = rotationYaw;
            modelLeggings.isBeingRenderedInInventory = modelArmor.isBeingRenderedInInventory = handle.isBeingRenderedInInventory = false;
            // IModelPlayer[] modelPlayers = irp.getRenderModels();

            // for (int i = 0; i < modelPlayers.length; i++) {
            // SmartRenderModel modelPlayer = modelPlayers[i].getRenderModel();
            // SmartMovingModelPlayer modelPlayer = ((SmartMovingModelPlayer) ((Object) getMainModel()));
            //
            // modelPlayer.setRotationYaw(rotationYaw);
            // modelPlayer.setForwardRotation(forwardRotation);
            // modelPlayer.setWorkingAngle(workingAngle);
            // }
        }
    }

    @Redirect(method = "applyRotations", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/RenderLivingBase;applyRotations(Lnet/minecraft/entity/EntityLivingBase;FFF)V"), require = 2)
    private void zeroRotationYaw(RenderLivingBase<AbstractClientPlayer> $this, EntityLivingBase entityLiving, float p_77043_2_, float rotationYaw, float partialTicks) {
        super.applyRotations((AbstractClientPlayer) entityLiving, p_77043_2_, 0, partialTicks);
    }

    @Redirect(method = "applyRotations", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/AbstractClientPlayer;isElytraFlying()Z"))
    private boolean ignoreElytraFlying(AbstractClientPlayer abstractClientPlayer) {
        return false;
    }

    @Override
    protected void renderLayers(AbstractClientPlayer entityClientPlayer, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw,
            float headPitch, float scaleIn) {
        ModelPlayer model = ((SmartMovingModelPlayer) ((Object) getMainModel())).getImplementation();
        ((ModelEarsRenderer) model.bipedDeadmau5Head).beforeRender(entityClientPlayer);
        ((ModelCapeRenderer) model.bipedCape).beforeRender(entityClientPlayer, partialTicks);
        super.renderLayers(entityClientPlayer, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scaleIn);
        ((ModelCapeRenderer) model.bipedCape).afterRender();
        ((ModelEarsRenderer) model.bipedDeadmau5Head).afterRender();
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
