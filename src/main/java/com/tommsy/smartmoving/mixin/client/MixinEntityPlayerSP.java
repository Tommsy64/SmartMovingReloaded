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

import com.tommsy.smartmoving.SmartMovingMod;
import com.tommsy.smartmoving.SmartMovingMod.ClientProxy;
import com.tommsy.smartmoving.client.SmartMovingClientPlayer;
import com.tommsy.smartmoving.client.SmartMovingClientPlayerHandler;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.world.World;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP extends MixinAbstractClientPlayer implements SmartMovingClientPlayer {

    protected MixinEntityPlayerSP(World worldIn) {
        super(worldIn);
    }

    private SmartMovingClientPlayerHandler playerHandler;

    @Inject(method = "<init>*", at = @At("RETURN"))
    private void onConstructed(CallbackInfo ci) {
        playerHandler = new SmartMovingClientPlayerHandler(this);
    }

    public SmartMovingClientPlayerHandler getPlayerHandler() {
        return this.playerHandler;
    }

    @Inject(method = "updateEntityActionState", at = @At("HEAD"), cancellable = true)
    private void updateEntityActionState(CallbackInfo cir) {
        SmartMovingMod.logger.info("Update EntityPlayerSP!!!");
        if (((ClientProxy) SmartMovingMod.proxy).keyBindGrab.isKeyDown()) {
            cir.cancel();
            SmartMovingMod.logger.info("Cancelled.");
        }
    }
}
