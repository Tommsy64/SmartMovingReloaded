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

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.tommsy.smartmoving.mixin.MixinEntityPlayer;
import com.tommsy.smartmoving.server.SmartMovingServerPlayer;
import com.tommsy.smartmoving.server.SmartMovingServerPlayerHandler;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.AxisAlignedBB;

@Mixin(EntityPlayerMP.class)
public abstract class MixinEntityPlayerMP extends MixinEntityPlayer implements SmartMovingServerPlayer {
    private static final float SmallSizeItemGrabHeight = 0.25F;

    private SmartMovingServerPlayerHandler playerHandler;

    @Inject(method = "<init>*", at = @At("RETURN"))
    private void onConstructed(CallbackInfo ci) {
        playerHandler = new SmartMovingServerPlayerHandler(this);
    }

    @Override
    public SmartMovingServerPlayerHandler getPlayerHandler() {
        return this.playerHandler;
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
        if (playerHandler.crawlingCooldown > 0)
            playerHandler.crawlingCooldown--;
    }

    @Inject(method = "onUpdate", at = @At("RETURN"))
    public void postOnUpdate(CallbackInfo ci) {
        if (playerHandler.resetFallDistance) {
            this.fallDistance = 0;
            this.motionY = 0.08;
        }
        if (playerHandler.resetTicksForFloatKick) {
            connection.floatingTickCount = 0;
        }
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (!playerHandler.isSmall || this.getHealth() <= 0)
            return;

        double offset = SmallSizeItemGrabHeight;
        AxisAlignedBB box = this.getEntityBoundingBox().expand(1, offset, 1);

        List<Entity> offsetEntities = this.world.getEntitiesWithinAABBExcludingEntity((Entity) ((Object) this), box);
        if (offsetEntities != null && offsetEntities.size() > 0) {
            Entity[] offsetEntityArray = offsetEntities.toArray(new Entity[offsetEntities.size()]);

            box = box.expand(0, -offset, 0);
            List<Entity> standardEntities = this.world.getEntitiesWithinAABBExcludingEntity((Entity) ((Object) this), box);

            for (int i = 0; i < offsetEntityArray.length; i++) {
                Entity offsetEntity = offsetEntityArray[i];
                if (standardEntities != null && standardEntities.contains(offsetEntity))
                    continue;

                if (!offsetEntity.isDead)
                    offsetEntity.onCollideWithPlayer((EntityPlayer) ((Object) this));
            }
        }
    }

    @Override
    public boolean isEntityInsideOpaqueBlock() {
        if (playerHandler.crawlingCooldown > 0)
            return false;
        return super.isEntityInsideOpaqueBlock();
    }

    @Override
    public void setPosition(double x, double y, double z) {
        super.setPosition(x, y, z);
        if (!crawlingInitialized)
            setMaxY(getMinY() + getHeight() - 1);
    }

    @Override
    public boolean isSneaking() {
        return this.getItemInUseCount() > 0 || super.isSneaking();
    }

    private double getMinY() {
        return this.getEntityBoundingBox().minY;
    }

    private void setMaxY(double maxY) {
        AxisAlignedBB box = this.getEntityBoundingBox();
        this.setEntityBoundingBox(new AxisAlignedBB(box.minX, box.minY, box.minZ, box.maxX, maxY, box.maxZ));
    }

    @Override
    public boolean isPlayerSleeping() {
        if (!crawlingInitialized) {
            setMaxY(getMinY() + getHeight());
            crawlingInitialized = true;
        }
        return super.isPlayerSleeping();
    }
}
