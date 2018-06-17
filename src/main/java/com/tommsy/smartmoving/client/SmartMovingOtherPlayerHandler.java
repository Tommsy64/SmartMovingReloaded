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

import net.minecraft.block.Block;
import net.minecraft.util.math.MathHelper;

public class SmartMovingOtherPlayerHandler extends AbstractSmartMovingClientPlayerHandler {

    private final SmartMovingOtherPlayer player;

    public SmartMovingOtherPlayerHandler(SmartMovingOtherPlayer player) {
        super(player);
        this.player = player;
    }

    @Override
    public double getOverGroundHeight(double maximum) {
        return (getBoundingBox().minY + 1D - getMaxPlayerSolidBetween(getBoundingBox().minY - maximum + 1D, getBoundingBox().minY + 1D, 0.1));
    }

    @Override
    public Block getOverGroundBlockId(double distance) {
        int x = MathHelper.floor(player.getPosX());
        int y = MathHelper.floor(getBoundingBox().minY);
        int z = MathHelper.floor(player.getPosZ());
        int minY = y - (int) Math.ceil(distance);

        y++;
        minY++;

        for (; y >= minY; y--) {
            Block block = getBlock(x, y, z);
            if (block != null)
                return block;
        }
        return null;
    }

    private boolean isJumping, doingFlyingAnimation, doingFallingAnimation;

    public void processStatePacket(long state) {
        feetClimbType = (int) (state & 15);
        state >>>= 4;

        handsClimbType = (int) (state & 15);
        state >>>= 4;

        isJumping = (state & 1) != 0;
        state >>>= 1;

        isDiving = (state & 1) != 0;
        state >>>= 1;

        isDipping = (state & 1) != 0;
        state >>>= 1;

        isSwimming = (state & 1) != 0;
        state >>>= 1;

        isCrawlClimbing = (state & 1) != 0;
        state >>>= 1;

        isCrawling = (state & 1) != 0;
        state >>>= 1;

        isClimbing = (state & 1) != 0;
        state >>>= 1;

        boolean isSmall = (state & 1) != 0;
        heightOffset = isSmall ? -1 : 0;
        player.setHeight(1.8F + heightOffset);
        state >>>= 1;

        doingFallingAnimation = (state & 1) != 0;
        state >>>= 1;

        doingFlyingAnimation = (state & 1) != 0;
        state >>>= 1;

        isCeilingClimbing = (state & 1) != 0;
        state >>>= 1;

        isLevitating = (state & 1) != 0;
        state >>>= 1;

        isHeadJumping = (state & 1) != 0;
        state >>>= 1;

        isSliding = (state & 1) != 0;
        state >>>= 1;

        angleJumpType = (int) (state & 7);
        state >>>= 3;

        isFeetVineClimbing = (state & 1) != 0;
        state >>>= 1;

        isHandsVineClimbing = (state & 1) != 0;
        state >>>= 1;

        isClimbJumping = (state & 1) != 0;
        state >>>= 1;

        boolean wasClimbBackJumping = isClimbBackJumping;
        isClimbBackJumping = (state & 1) != 0;
        if (!wasClimbBackJumping && isClimbBackJumping)
            onStartClimbBackJump();
        state >>>= 1;

        isSlow = (state & 1) != 0;
        state >>>= 1;

        isFast = (state & 1) != 0;
        state >>>= 1;

        boolean wasWallJumping = isWallJumping;
        isWallJumping = (state & 1) != 0;
        if (!wasWallJumping && isWallJumping)
            onStartWallJump(null);
        state >>>= 1;
    }

    @Override
    public SmartMovingRenderState getAndUpdateRenderState() {
        super.getAndUpdateRenderState();
        renderState.jump = isJumping;
        renderState.flying = doingFlyingAnimation;
        renderState.falling = doingFallingAnimation;
        return this.renderState;
    }
}
