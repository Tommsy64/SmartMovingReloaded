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
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.AxisAlignedBB;

import com.tommsy.smartmoving.client.SmartMovingClientPlayer;
import com.tommsy.smartmoving.client.SmartMovingInput;
import com.tommsy.smartmoving.common.SmartMovingPlayerState;
import com.tommsy.smartmoving.config.SmartMovingConfigAccess;
import com.tommsy.smartmoving.network.SmartMovingNetworkHandler;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP extends MixinAbstractClientPlayer implements SmartMovingClientPlayer {

    private SmartMovingInput playerInput;
    private SmartMovingPlayerState previousPlayerState;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onConstructed(CallbackInfo ci) {
        this.playerInput = new SmartMovingInput(mc.gameSettings);
        previousPlayerState = new SmartMovingPlayerState();
    }

    @Shadow
    protected Minecraft mc;

    @Override
    public Minecraft getMinecraft() {
        return mc;
    }

    @Shadow
    public MovementInput movementInput;

    @Override
    protected void jump() {
        super.jump();
    }

    @Overwrite
    public boolean isSneaking() {
        return playerState.isSneaking();
    }

    @Override
    public void travel(float strafe, float vertical, float forward) {
        super.travel(strafe, vertical, forward);
    }

    @Inject(method = "onUpdate", at = @At("RETURN"))
    private void postUpdateRidden(CallbackInfo ci) {

    }

    @Inject(method = "onUpdate", at = @At("HEAD"))
    private void preOnUpdate(CallbackInfo ci) {

    }

    @Inject(method = "onUpdate", at = @At("RETURN"))
    private void postOnUpdate(CallbackInfo ci) {

    }

    @Inject(method = "onLivingUpdate", at = @At("HEAD"))
    private void preOnLivingUpdate(CallbackInfo ci) {

    }

    @Redirect(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/MovementInput;updatePlayerMoveState()V"))
    private void movementInputCorrection(MovementInput movementInput) {
        movementInput.updatePlayerMoveState();
        if (this.playerState.isCrouching) {
            movementInput.moveStrafe = (float) ((double) movementInput.moveStrafe * 0.3D);
            movementInput.moveForward = (float) ((double) movementInput.moveForward * 0.3D);
        } else if (this.playerState.isCrawling) {
            movementInput.moveStrafe = (float) ((double) movementInput.moveStrafe * 0.15D);
            movementInput.moveForward = (float) ((double) movementInput.moveForward * 0.15D);
        }
    }

    @Inject(method = "onLivingUpdate", at = @At("RETURN"))
    private void postOnLivingUpdate(CallbackInfo ci) {

    }

    @Inject(method = "updateEntityActionState", at = @At("RETURN"), cancellable = true)
    private void postUpdateEntityActionState(CallbackInfo ci) {
        playerInput.update();

        if (this.isElytraFlying())
            playerState.isCrawling = playerState.isCrouching = false;

        boolean mustCrawl = false;
        if (playerState.isCrawling) {
            mustCrawl = checkForCollision(0.6F, 1.8F);
        }

        if (!mustCrawl) {
            boolean canCrawl = this.fallDistance < SmartMovingConfigAccess.config.movement.fallingDistanceStart;
            if (canCrawl) {
                if (playerState.isCrawling)
                    playerState.isCrawling = playerInput.grab.pressed;
                else
                    playerState.isCrawling = (playerInput.grab.startPressed && playerInput.sneak.pressed && this.onGround) && !this.capabilities.isFlying;
            } else
                playerState.isCrawling = false;
        }

        playerState.isCrouching = !playerState.isCrawling && playerInput.sneak.pressed;

        sendPlayerState();
    }

    @Unique
    private boolean checkForCollision(float width, float height) {
        AxisAlignedBB axisalignedbb = this.getEntityBoundingBox();
        axisalignedbb = new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ,
                axisalignedbb.minX + (double) width, axisalignedbb.minY + (double) height, axisalignedbb.minZ + (double) width);
        return this.world.collidesWithAnyBlock(axisalignedbb);
    }

    @Unique
    private void sendPlayerState() {
        if (!playerState.equals(previousPlayerState))
            SmartMovingNetworkHandler.sendClientPlayerStateChange(playerState);
        previousPlayerState.copy(this.playerState);
    }
}
