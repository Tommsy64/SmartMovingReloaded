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

package com.tommsy.smartmoving.mixin.server;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.server.MinecraftServer;

import com.tommsy.smartmoving.mixin.MixinEntityPlayer;
import com.tommsy.smartmoving.server.SmartMovingServerPlayer;

@Mixin(EntityPlayerMP.class)
public abstract class MixinEntityPlayerMP extends MixinEntityPlayer implements SmartMovingServerPlayer {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void onConstructed(CallbackInfo ci) {

    }

    @Shadow
    @Final
    public MinecraftServer mcServer;

    @Shadow
    public NetHandlerPlayServer connection;

    @Override
    public MinecraftServer getMinecraftServer() {
        return this.mcServer;
    }

    public boolean crawlingInitialized;

    @Inject(method = "onUpdate", at = @At("HEAD"))
    public void preOnUpdate(CallbackInfo ci) {

    }

    @Inject(method = "onUpdate", at = @At("RETURN"))
    public void postOnUpdate(CallbackInfo ci) {}

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
    }

    @Override
    public boolean isEntityInsideOpaqueBlock() {
        return super.isEntityInsideOpaqueBlock();
    }

    @Override
    public void setPosition(double x, double y, double z) {
        super.setPosition(x, y, z);
    }
}
