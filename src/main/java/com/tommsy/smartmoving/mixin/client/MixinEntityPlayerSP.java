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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.tommsy.smartmoving.SmartMovingMod;
import com.tommsy.smartmoving.client.SmartMovingClientPlayer;
import com.tommsy.smartmoving.client.SmartMovingClientPlayerHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.PlayerCapabilities;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP extends MixinAbstractClientPlayer implements SmartMovingClientPlayer {
    private SmartMovingClientPlayerHandler playerHandler;

    @Inject(method = "<init>*", at = @At("RETURN"))
    private void onConstructed(CallbackInfo ci) {
        playerHandler = new SmartMovingClientPlayerHandler(this);
    }

    @Override
    public SmartMovingClientPlayerHandler getPlayerHandler() {
        return this.playerHandler;
    }

    @Shadow
    protected boolean sleeping;
    @Shadow
    protected Minecraft mc;

    @Override
    protected void jump() {
        super.jump();
    }

    @Inject(method = "onUpdate", at = @At("HEAD"))
    private void beforeOnUpdate() {

    }

    @Inject(method = "onUpdate", at = @At("RETURN"))
    private void afterOnUpdate() {

    }

    @Inject(method = "onLivingUpdate", at = @At("HEAD"))
    private void beforeOnLivingUpdate() {

    }

    @Inject(method = "onLivingUpdate", at = @At("RETURN"))
    private void afterOnLivingUpdate() {

    }

    @Inject(method = "updateEntityActionState", at = @At("HEAD"), cancellable = true)
    private void updateEntityActionState(CallbackInfo ci) {
        if (SmartMovingMod.clientProxy.keyBindGrab.isKeyDown()) {
            ci.cancel();
        }
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
}
