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

package com.tommsy.smartmoving.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.util.math.AxisAlignedBB;

import com.tommsy.smartmoving.common.SmartMovingEntityPlayer;
import com.tommsy.smartmoving.common.SmartMovingPlayerState;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer extends MixinEntityLivingBase implements SmartMovingEntityPlayer {

    protected SmartMovingPlayerState playerState;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onConstructed(CallbackInfo ci) {
        playerState = new SmartMovingPlayerState();
    }

    public SmartMovingPlayerState getState() {
        return playerState;
    }

    @Shadow
    protected void jump() {}

    @Shadow
    public PlayerCapabilities capabilities;

    @Shadow
    public void travel(float strafe, float vertical, float forward) {}

    @Shadow
    public void onLivingUpdate() {}

    @Shadow
    public boolean isEntityInsideOpaqueBlock() {
        return false;
    }

    @Shadow
    protected boolean sleeping;

    @Shadow
    public abstract boolean isPlayerSleeping();

    @Shadow
    public abstract float getAIMoveSpeed();

    @Shadow
    public abstract void addMovementStat(double diffX, double diffY, double diffZ);

    @Overwrite
    protected void updateSize() {
        float width, height;

        if (this.isElytraFlying()) {
            width = 0.6F;
            height = 0.6F;
        } else if (this.isPlayerSleeping()) {
            width = 0.2F;
            height = 0.2F;
        } else if (playerState.isCrouching) {
            width = 0.6F;
            height = 1.65F;
        } else if (playerState.isCrawling) {
            width = 0.6F;
            height = 0.65F;
        } else {
            width = 0.6F;
            height = 1.8F;
        }

        if (width != this.width || height != this.height) {
            AxisAlignedBB axisalignedbb = this.getEntityBoundingBox();
            axisalignedbb = new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ,
                    axisalignedbb.minX + (double) width, axisalignedbb.minY + (double) height, axisalignedbb.minZ + (double) width);

            if (width <= this.width && height <= this.height)
                this.setSize(width, height);
            else if (!this.world.collidesWithAnyBlock(axisalignedbb))
                this.setSize(width, height);
        }
        net.minecraftforge.fml.common.FMLCommonHandler.instance().onPlayerPostTick((EntityPlayer) ((Object) this));
    }

    // Not obfuscated. Added by forge?
    @Shadow(remap = false)
    public float eyeHeight;

    /**
     * Fix for MC-90598
     *
     * @author Tommsy64
     * @reason Fixes MC-90598 and adjusts eye height according to the {{@link #playerState}.
     */
    @Overwrite
    public float getEyeHeight() {
        if (this.isPlayerSleeping())
            return 0.2F;
        else if (this.isElytraFlying())
            return 0.4F;
        else if (playerState.isCrawling)
            return eyeHeight - 1;
        else if (!this.isSneaking() && this.height != 1.65F)
            return this.height == 0.6F ? 0.4F : eyeHeight;

        return eyeHeight - 0.08F;
    }
}
