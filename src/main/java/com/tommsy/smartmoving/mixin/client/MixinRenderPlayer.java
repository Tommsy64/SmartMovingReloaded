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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.tommsy.smartmoving.common.SmartMovingPlayer;
import com.tommsy.smartmoving.common.SmartMovingPlayerHandler;
import com.tommsy.smartmoving.common.SmartMovingPlayerHandler.SmartMovingRenderState;

import net.minecraft.block.Block;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.entity.RenderPlayer;

@Mixin(RenderPlayer.class)
public class MixinRenderPlayer {

    @Inject(method = "doRender", at = @At("HEAD"))
    private void doRender(AbstractClientPlayer entityClientPlayer, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
        SmartMovingPlayer smPlayer = (SmartMovingPlayer) entityClientPlayer;
        SmartMovingPlayerHandler handle = smPlayer.getPlayerHandler();

        boolean isInventory = x == 0 && y == 0 && z == 0 && entityYaw == 0 && partialTicks == 1.0;

        SmartMovingRenderState renderState = handle.getAndUpdateRenderState();

        SmartStatistics statistics = SmartStatisticsFactory.getInstance(entityplayer);
        float currentHorizontalSpeedFlattened = statistics != null ? statistics.getCurrentHorizontalSpeedFlattened(partialTicks, -1) : Float.NaN;
        float smallOverGroundHeight = renderState.crawlClimb || renderState.headJump ? (float) handle.getOverGroundHeight(5D) : 0F;
        Block overGroundBlock = renderState.headJump && smallOverGroundHeight < 5F ? handle.getOverGroundBlockId(smallOverGroundHeight) : null;

        IModelPlayer[] modelPlayers = irp.getMovingModels();

        for (int i = 0; i < modelPlayers.length; i++) {
            SmartMovingModel modelPlayer = modelPlayers[i].getMovingModel();
            modelPlayer.isClimb = isClimb;
            modelPlayer.isClimbJump = isClimbJump;
            modelPlayer.handsClimbType = handsClimbType;
            modelPlayer.feetClimbType = feetClimbType;
            modelPlayer.isHandsVineClimbing = isHandsVineClimbing;
            modelPlayer.isFeetVineClimbing = isFeetVineClimbing;
            modelPlayer.isCeilingClimb = isCeilingClimb;
            modelPlayer.isSwim = isSwim;
            modelPlayer.isDive = isDive;
            modelPlayer.isCrawl = isCrawl;
            modelPlayer.isCrawlClimb = isCrawlClimb;
            modelPlayer.isJump = isJump;
            modelPlayer.isHeadJump = isHeadJump;
            modelPlayer.isSlide = isSlide;
            modelPlayer.isFlying = isFlying;
            modelPlayer.isLevitate = isLevitate;
            modelPlayer.isFalling = isFalling;
            modelPlayer.isGenericSneaking = isGenericSneaking;
            modelPlayer.isAngleJumping = isAngleJumping;
            modelPlayer.angleJumpType = angleJumpType;

            modelPlayer.currentHorizontalSpeedFlattened = currentHorizontalSpeedFlattened;
            modelPlayer.smallOverGroundHeight = smallOverGroundHeight;
            modelPlayer.overGroundBlock = overGroundBlock;
        }

        if (!isInventory && entityClientPlayer.isSneaking() && !(entityClientPlayer instanceof EntityPlayerSP) && renderState.crawl)
            y += 0.125D;

        CurrentMainModel = modelBipedMain;
        irp.superRenderDoRender(entityClientPlayer, x, y, z, entityYaw, partialTicks);
        CurrentMainModel = null;

        if (moving != null && renderState.levitate && modelPlayers != null)
            for (int i = 0; i < modelPlayers.length; i++)
                modelPlayers[i].getMovingModel().md.currentHorizontalAngle = modelPlayers[i].getMovingModel().md.currentCameraAngle;
    }
}
