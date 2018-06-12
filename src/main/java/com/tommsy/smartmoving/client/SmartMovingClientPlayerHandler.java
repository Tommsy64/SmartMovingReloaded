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

package com.tommsy.smartmoving.client;

import com.tommsy.smartmoving.common.SmartMovingPlayerHandler;

import net.minecraft.block.Block;
import net.minecraft.util.math.MathHelper;

public class SmartMovingClientPlayerHandler extends SmartMovingPlayerHandler {

    private final SmartMovingClientPlayer player;

    public SmartMovingClientPlayerHandler(SmartMovingClientPlayer player) {
        super(player);
        this.player = player;
    }

    @Override
    public SmartMovingRenderState getAndUpdateRenderState() {
        super.getAndUpdateRenderState();
        renderState.jump = player.isJumping();
        renderState.flying = shouldDoFlyingAnimation();
        renderState.falling = shouldDoFallingAnimation();
        return this.renderState;
    }

    private boolean shouldDoFlyingAnimation() {
        // if(Config.isFlyingEnabled() || Config.isLevitationAnimationEnabled())
        return player.getCapabilities().isFlying;
    }

    private boolean shouldDoFallingAnimation() {
        // if(Config.isFallAnimationEnabled())
        return !player.isOnGround() && player.getFallDistance() > 2.5;// Config._fallAnimationDistanceMinimum.value;
    }

    @Override
    public double getOverGroundHeight(double maximum) {
        return (getBoundingBox().minY - getMaxPlayerSolidBetween(getBoundingBox().minY - maximum, getBoundingBox().minY, 0));
    }

    @Override
    public Block getOverGroundBlockId(double distance) {
        int x = MathHelper.floor(player.getPosX());
        int y = MathHelper.floor(getBoundingBox().minY);
        int z = MathHelper.floor(player.getPosZ());
        int minY = y - (int) Math.ceil(distance);

        for (; y >= minY; y--) {
            Block block = getBlock(x, y, z);
            if (block != null)
                return block;
        }
        return null;
    }
}
