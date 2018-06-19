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

import static com.tommsy.smartmoving.client.render.RenderUtils.Half;
import static com.tommsy.smartmoving.client.render.RenderUtils.Quarter;
import static com.tommsy.smartmoving.client.render.RenderUtils.RadiantToAngle;

import java.util.ListIterator;

import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.tommsy.smartmoving.client.AbstractSmartMovingClientPlayerHandler;
import com.tommsy.smartmoving.client.AbstractSmartMovingClientPlayerHandler.SmartMovingRenderState;
import com.tommsy.smartmoving.client.SmartMovingAbstractClientPlayer;
import com.tommsy.smartmoving.client.SmartMovingClient;
import com.tommsy.smartmoving.client.SmartMovingClientPlayer;
import com.tommsy.smartmoving.client.SmartMovingOtherPlayer;
import com.tommsy.smartmoving.client.SmartMovingOtherPlayerHandler;
import com.tommsy.smartmoving.client.model.LayerPlayerArmor;
import com.tommsy.smartmoving.client.model.SmartMovingModelBiped;
import com.tommsy.smartmoving.client.model.SmartMovingModelPlayer;
import com.tommsy.smartmoving.client.render.ISmartMovingRenderPlayer;
import com.tommsy.smartmoving.client.render.RenderDataTracker;
import com.tommsy.smartmoving.common.statistics.SmartStatistics;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.entity.player.EntityPlayer;

@Mixin(RenderPlayer.class)
@Implements(@Interface(iface = ISmartMovingRenderPlayer.class, prefix = "sm$"))
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
        SmartMovingAbstractClientPlayer smPlayer = (SmartMovingAbstractClientPlayer) entityClientPlayer;
        AbstractSmartMovingClientPlayerHandler handle = smPlayer.getPlayerHandler();

        boolean isInventory = x == 0 && y == 0 && z == 0 && entityYaw == 0 && partialTicks == 1.0;

        SmartMovingRenderState renderState = handle.getAndUpdateRenderState();

        float currentHorizontalSpeedFlattened = handle.statistics != null ? handle.statistics.getCurrentHorizontalSpeedFlattened(partialTicks, -1) : Float.NaN;
        float smallOverGroundHeight = renderState.crawlClimb || renderState.headJump ? (float) handle.getOverGroundHeight(5D) : 0F;
        Block overGroundBlock = renderState.headJump && smallOverGroundHeight < 5F ? handle.getOverGroundBlockId(smallOverGroundHeight) : null;

        ListIterator<SmartMovingModelBiped> iterator = SmartMovingClient.modelBipedInstances.listIterator();
        while (iterator.hasNext()) {
            // SmartMovingModelPlayer modelPlayer = this.sm$getMainModel();
            SmartMovingModelBiped modelPlayer = iterator.next();
            modelPlayer.setRenderState(renderState);
            modelPlayer.setCurrentHorizontalSpeedFlattened(currentHorizontalSpeedFlattened);
            modelPlayer.setSmallOverGroundHeight(smallOverGroundHeight);
            modelPlayer.setOverGroundBlock(overGroundBlock);
        }

        if (!isInventory && entityClientPlayer.isSneaking() && !(entityClientPlayer instanceof EntityPlayerSP) && renderState.crawl)
            y += 0.125D;

        SmartStatistics statistics = handle.statistics;
        if (statistics != null) {
            boolean isSleeping = entityClientPlayer.isPlayerSleeping();

            float totalVerticalDistance = statistics.getTotalVerticalDistance(partialTicks);
            float currentVerticalSpeed = statistics.getCurrentVerticalSpeed(partialTicks);
            float totalDistance = statistics.getTotalDistance(partialTicks);
            float currentSpeed = statistics.getCurrentSpeed(partialTicks);

            double distance = 0;
            double verticalDistance = 0;
            double horizontalDistance = 0;
            float currentCameraAngle = 0;
            float currentVerticalAngle = 0;
            float currentHorizontalAngle = 0;

            if (!isInventory) {
                double xDiff = entityClientPlayer.posX - entityClientPlayer.prevPosX;
                double yDiff = entityClientPlayer.posY - entityClientPlayer.prevPosY;
                double zDiff = entityClientPlayer.posZ - entityClientPlayer.prevPosZ;

                verticalDistance = Math.abs(yDiff);
                horizontalDistance = Math.sqrt(xDiff * xDiff + zDiff * zDiff);
                distance = Math.sqrt(horizontalDistance * horizontalDistance + verticalDistance * verticalDistance);

                currentCameraAngle = entityClientPlayer.rotationYaw / RadiantToAngle;
                currentVerticalAngle = (float) Math.atan(yDiff / horizontalDistance);
                if (Float.isNaN(currentVerticalAngle))
                    currentVerticalAngle = Quarter;

                currentHorizontalAngle = (float) -Math.atan(xDiff / zDiff);
                if (Float.isNaN(currentHorizontalAngle))
                    if (Float.isNaN(statistics.prevHorizontalAngle))
                        currentHorizontalAngle = currentCameraAngle;
                    else
                        currentHorizontalAngle = statistics.prevHorizontalAngle;
                else if (zDiff < 0)
                    currentHorizontalAngle += Half;

                statistics.prevHorizontalAngle = currentHorizontalAngle;
            }

            iterator = SmartMovingClient.modelBipedInstances.listIterator();
            while (iterator.hasNext()) {
                // SmartMovingModelPlayer modelPlayer = this.sm$getMainModel();
                SmartMovingModelBiped modelPlayer = iterator.next();
                modelPlayer.setInventory(isInventory);
                modelPlayer.setSleeping(isSleeping);

                modelPlayer.setTotalVerticalDistance(totalVerticalDistance);
                modelPlayer.setCurrentVerticalSpeed(currentVerticalSpeed);
                modelPlayer.setTotalDistance(totalDistance);
                modelPlayer.setCurrentSpeed(currentSpeed);

                modelPlayer.setDistance(distance);
                modelPlayer.setVerticalDistance(verticalDistance);
                modelPlayer.setHorizontalDistance(horizontalDistance);
                modelPlayer.setCurrentCameraAngle(currentCameraAngle);
                modelPlayer.setCurrentVerticalAngle(currentVerticalAngle);
                modelPlayer.setCurrentHorizontalAngle(currentHorizontalAngle);
                modelPlayer.setPrevOuterRenderData(RenderDataTracker.getPreviousRendererData(entityClientPlayer));
            }
        }

        // CurrentMainModel = modelBipedMain;
    }

    @Inject(method = "doRender", at = @At("RETURN"))
    private void postDoRender(AbstractClientPlayer entityClientPlayer, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
        // CurrentMainModel = null;

        SmartMovingAbstractClientPlayer smPlayer = (SmartMovingAbstractClientPlayer) entityClientPlayer;
        AbstractSmartMovingClientPlayerHandler handle = smPlayer.getPlayerHandler();
        SmartMovingRenderState renderState = handle.getAndUpdateRenderState();

        if (renderState.levitate) {
            // for (int i = 0; i < modelPlayers.length; i++)
            // modelPlayers[i].getMovingModel().md.currentHorizontalAngle = modelPlayers[i].getMovingModel().md.currentCameraAngle;
            SmartMovingModelPlayer modelPlayer = this.sm$getMainModel();
            modelPlayer.setCurrentHorizontalAngle(modelPlayer.getCurrentCameraAngle());
        }
    }

    @Inject(method = "applyRotations", at = @At("HEAD"))
    private void applyRotations(AbstractClientPlayer entityClientPlayer, float totalTime, float rotationYaw, float partialTicks, CallbackInfo ci) {
        SmartMovingAbstractClientPlayer smPlayer = (SmartMovingAbstractClientPlayer) entityClientPlayer;
        AbstractSmartMovingClientPlayerHandler handle = smPlayer.getPlayerHandler();

        boolean isClientPlayer = smPlayer instanceof SmartMovingClientPlayer;
        boolean isInventory = partialTicks == 1.0F && isClientPlayer &&
                ((SmartMovingClientPlayer) smPlayer).getMinecraft().currentScreen instanceof GuiInventory;
        if (!isInventory) {
            float forwardRotation = entityClientPlayer.prevRotationYaw + (entityClientPlayer.rotationYaw - entityClientPlayer.prevRotationYaw) * partialTicks;

            if (handle.isClimbing() || handle.isClimbCrawling() || handle.isCrawlClimbing() || handle.isFlying() || handle.isSwimming() || handle.isDiving() ||
                    handle.isCeilingClimbing() || handle.isHeadJumping || handle.isSliding() || handle.isAngleJumping())
                entityClientPlayer.renderYawOffset = forwardRotation;

            if (entityClientPlayer.isPlayerSleeping()) {
                rotationYaw = 0;
                forwardRotation = 0;
            }

            float workingAngle;
            Minecraft minecraft = Minecraft.getMinecraft();
            if (!isClientPlayer) {
                workingAngle = -entityClientPlayer.rotationYaw;
                workingAngle += minecraft.getRenderViewEntity().rotationYaw;
            } else
                workingAngle = rotationYaw - RenderDataTracker.getPreviousRendererData(entityClientPlayer).rotateAngleY * RadiantToAngle;

            if (minecraft.gameSettings.thirdPersonView == 2 && !((EntityPlayer) minecraft.getRenderViewEntity()).isPlayerSleeping())
                workingAngle += 180F;

            // IModelPlayer[] modelPlayers = irp.getRenderModels();

            // for (int i = 0; i < modelPlayers.length; i++) {
            // SmartRenderModel modelPlayer = modelPlayers[i].getRenderModel();
            SmartMovingModelPlayer modelPlayer = this.sm$getMainModel();

            modelPlayer.setRotationYaw(rotationYaw);
            modelPlayer.setForwardRotation(forwardRotation);
            modelPlayer.setWorkingAngle(workingAngle);
            // }

            rotationYaw = 0;
        }
    }

    @Override
    protected void renderLayers(AbstractClientPlayer entityClientPlayer, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw,
            float headPitch, float scaleIn) {
        sm$getMainModel().getBipedEars().beforeRender(entityClientPlayer);
        sm$getMainModel().getBipedCape().beforeRender(entityClientPlayer, partialTicks);
        super.renderLayers(entityClientPlayer, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scaleIn);
        sm$getMainModel().getBipedCape().afterRender();
        sm$getMainModel().getBipedEars().afterRender();
    }

    @Inject(method = "renderLivingAt", at = @At("HEAD"))
    private void preRenderLivingAt(AbstractClientPlayer entityClientPlayer, double x, double y, double z, CallbackInfo ci) {
        if (entityClientPlayer instanceof SmartMovingOtherPlayer) {
            SmartMovingOtherPlayerHandler handle = ((SmartMovingOtherPlayer) entityClientPlayer).getPlayerHandler();

            if (handle.heightOffset != 0)
                y += handle.heightOffset;
        }
    }

    @Override
    public void renderName(AbstractClientPlayer entityClientPlayer, double x, double y, double z) {
        boolean changedIsSneaking = false, originalIsSneaking = false;
        if (Minecraft.isGuiEnabled() && entityClientPlayer != renderManager.pointedEntity) {
            // SmartMoving moving = SmartMovingFactory.getInstance(entityClientPlayer);
            AbstractSmartMovingClientPlayerHandler handle = ((SmartMovingAbstractClientPlayer) entityClientPlayer).getPlayerHandler();
            // if (moving != null) {
            originalIsSneaking = entityClientPlayer.isSneaking();
            boolean temporaryIsSneaking = originalIsSneaking;
            if (handle.isCrawling() && !handle.isClimbing())
                temporaryIsSneaking = !false;// SmartMovingContext.Config._crawlNameTag.value;
            else if (originalIsSneaking)
                temporaryIsSneaking = !false;// SmartMovingContext.Config._sneakNameTag.value;

            changedIsSneaking = temporaryIsSneaking != originalIsSneaking;
            if (changedIsSneaking)
                entityClientPlayer.setSneaking(temporaryIsSneaking);

            if (handle.heightOffset == -1)
                y -= 0.2F;
            else if (originalIsSneaking && !temporaryIsSneaking)
                y -= 0.05F;
            // }
        }

        super.renderName(entityClientPlayer, x, y, z);

        if (changedIsSneaking)
            entityClientPlayer.setSneaking(originalIsSneaking);
    }

    @Override
    protected float handleRotationFloat(AbstractClientPlayer entityClientPlayer, float partialTicks) {
        SmartStatistics statistics = ((SmartMovingAbstractClientPlayer) entityClientPlayer).getPlayerHandler().statistics;
        entityClientPlayer.ticksExisted += statistics.ticksRiding;
        float result = super.handleRotationFloat(entityClientPlayer, partialTicks);
        entityClientPlayer.ticksExisted -= statistics.ticksRiding;
        return result;
    }
}
