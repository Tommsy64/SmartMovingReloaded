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

package com.tommsy.smartmoving.common;

import java.util.List;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.minecraft.block.Block;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public abstract class SmartMovingPlayerHandler {

    private final SmartMovingPlayer player;

    protected SmartMovingPlayerHandler(SmartMovingPlayer player) {
        this.player = player;
    }

    protected boolean isSlow;

    protected boolean isClimbing;
    protected boolean isHandsVineClimbing;
    protected boolean isFeetVineClimbing;

    protected boolean isClimbJumping;
    protected boolean isClimbBackJumping;
    protected boolean isWallJumping;
    protected boolean isClimbCrawling;
    protected boolean isCrawlClimbing;
    protected boolean isCeilingClimbing;

    protected boolean isDipping;
    protected boolean isSwimming;
    protected boolean isDiving;
    protected boolean isLevitating;
    protected boolean isHeadJumping;
    protected boolean isCrawling;
    protected boolean isSliding;
    protected boolean isFlying;

    protected int handsClimbType;
    protected int feetClimbType;

    protected int angleJumpType;

    public final SmartMovingRenderState renderState = new SmartMovingRenderState();

    public SmartMovingRenderState getAndUpdateRenderState() {
        renderState.climb = isClimbing && !isCrawling && !isCrawlClimbing && !isClimbJumping;
        renderState.climbJump = isClimbJumping;
        renderState.handsClimbType = handsClimbType;
        renderState.feetClimbType = feetClimbType;

        renderState.handsVineClimb = isHandsVineClimbing;
        renderState.feetVineClimb = isFeetVineClimbing;

        renderState.ceilingClimb = isCeilingClimbing;

        renderState.swim = isSwimming && !isDipping;
        renderState.dive = isDiving;
        renderState.levitate = isLevitating;
        renderState.crawl = isCrawling && !isClimbing;
        renderState.crawlClimb = isCrawlClimbing || (isClimbing && isCrawling); // Why two ways?
        // Skipping renderState.jump
        renderState.headJump = isHeadJumping;
        // Skipping renderState.flying
        renderState.slide = isSliding;
        // Skipping renderState.falling
        renderState.genericSneak = isSlow;
        renderState.angleJump = isAngleJumping();
        renderState.angleJumpType = angleJumpType;

        return renderState;
    }

    private boolean isAngleJumping() {
        return angleJumpType > 1 && angleJumpType < 7;
    }

    @ToString
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class SmartMovingRenderState {
        public boolean climb;
        public boolean climbJump;
        public int handsClimbType;
        public int feetClimbType;

        public boolean handsVineClimb;
        public boolean feetVineClimb;

        public boolean ceilingClimb;

        public boolean swim;
        public boolean dive;
        public boolean levitate;
        public boolean crawl;
        public boolean crawlClimb;
        public boolean jump;
        public boolean headJump;
        public boolean flying;
        public boolean slide;
        public boolean falling;
        public boolean genericSneak;

        public boolean angleJump;
        public int angleJumpType;
    }

    public abstract double getOverGroundHeight(double maximum);

    public abstract Block getOverGroundBlockId(double distance);

    protected AxisAlignedBB getBoundingBox() {
        return player.getEntityBoundingBox();
    }

    protected Block getBlock(int x, int y, int z) {
        return player.getWorld().getBlockState(new BlockPos(x, y, z)).getBlock();
    }

    private List<AxisAlignedBB> getPlayerSolidBetween(double yMin, double yMax, double horizontalTolerance) {
        AxisAlignedBB bb = getBoundingBox();
        bb = new AxisAlignedBB(bb.minX, yMin, bb.minZ, bb.maxX, yMax, bb.maxZ);
        return player.getIntersectingCollisionBoxes(horizontalTolerance == 0 ? bb : bb.expand(horizontalTolerance, 0, horizontalTolerance));
    }

    protected double getMaxPlayerSolidBetween(double yMin, double yMax, double horizontalTolerance) {
        List<AxisAlignedBB> solids = getPlayerSolidBetween(yMin, yMax, horizontalTolerance);
        double result = yMin;
        for (int i = 0; i < solids.size(); i++) {
            AxisAlignedBB box = solids.get(i);
            if (isCollided(box, yMin, yMax, horizontalTolerance))
                result = Math.max(result, box.maxY);
        }
        return Math.min(result, yMax);
    }

    protected double getMinPlayerSolidBetween(double yMin, double yMax, double horizontalTolerance) {
        List<AxisAlignedBB> solids = getPlayerSolidBetween(yMin, yMax, horizontalTolerance);
        double result = yMax;
        for (int i = 0; i < solids.size(); i++) {
            AxisAlignedBB box = solids.get(i);
            if (isCollided(box, yMin, yMax, horizontalTolerance))
                result = Math.min(result, box.minY);
        }
        return Math.max(result, yMin);
    }

    public boolean isCollided(AxisAlignedBB box, double yMin, double yMax, double horizontalTolerance) {
        return box.maxX >= getBoundingBox().minX - horizontalTolerance &&
                box.minX <= getBoundingBox().maxX + horizontalTolerance &&
                box.maxY >= yMin &&
                box.minY <= yMax &&
                box.maxZ >= getBoundingBox().minZ - horizontalTolerance &&
                box.minZ <= getBoundingBox().maxZ + horizontalTolerance;
    }
}
