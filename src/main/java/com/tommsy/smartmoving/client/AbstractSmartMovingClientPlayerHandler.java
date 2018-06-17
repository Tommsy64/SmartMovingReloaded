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

import static com.tommsy.smartmoving.client.render.RenderUtils.Half;
import static com.tommsy.smartmoving.client.render.RenderUtils.Quarter;
import static com.tommsy.smartmoving.client.render.RenderUtils.RadiantToAngle;

import java.util.List;

import com.tommsy.smartmoving.client.render.RenderDataTracker;
import com.tommsy.smartmoving.common.statistics.SmartStatistics;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleSplash;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public abstract class AbstractSmartMovingClientPlayerHandler {

    private final SmartMovingAbstractClientPlayer player;
    public final SmartStatistics statistics;

    protected AbstractSmartMovingClientPlayerHandler(SmartMovingAbstractClientPlayer player) {
        this.player = player;
        this.statistics = new SmartStatistics(player);
    }

    @Getter
    protected boolean isSlow;
    @Getter
    protected boolean isFast;

    @Getter
    public boolean isClimbing;
    @Getter
    public boolean isHandsVineClimbing;
    @Getter
    public boolean isFeetVineClimbing;

    @Getter
    protected boolean isClimbJumping;
    @Getter
    protected boolean isClimbBackJumping;
    @Getter
    protected boolean isWallJumping;
    @Getter
    protected boolean isClimbCrawling;
    @Getter
    protected boolean isCrawlClimbing;
    @Getter
    protected boolean isCeilingClimbing;

    @Getter
    public boolean isDipping;
    @Getter
    public boolean isSwimming;
    @Getter
    public boolean isDiving;
    @Getter
    public boolean isLevitating;
    public boolean isHeadJumping;
    @Getter
    public boolean isCrawling;
    @Getter
    protected boolean isSliding;
    @Getter
    protected boolean isFlying;

    public int handsClimbType;
    public int feetClimbType;

    public int angleJumpType;

    private float spawnSlindingParticle;
    private float spawnSwimmingParticle;

    public float heightOffset;

    public float exhaustion;
    public float jumpCharge, headJumpCharge;

    public float maxExhaustionForAction;
    public float maxExhaustionToStartAction;

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

    public boolean isAngleJumping() {
        return angleJumpType > 1 && angleJumpType < 7;
    }

    @ToString
    @NoArgsConstructor(access = AccessLevel.PUBLIC)
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

    public void spawnParticles(Minecraft minecraft) {
        spawnParticles(minecraft, player.getPosX() - player.getPrevPosX(), player.getPosZ() - player.getPrevPosZ());
    }

    private void spawnParticles(Minecraft minecraft, double playerMotionX, double playerMotionZ) {
        float horizontalSpeedSquare = 0;
        if (isSliding || isSwimming)
            horizontalSpeedSquare = (float) (playerMotionX * playerMotionX + playerMotionZ * playerMotionZ);

        if (isSliding) {

            AxisAlignedBB playerBoundingBox = player.getEntityBoundingBox();
            int i = MathHelper.floor(player.getPosX());
            int j = MathHelper.floor(playerBoundingBox.minY - 0.1F);
            int k = MathHelper.floor(player.getPosZ());
            Block block = getBlock(i, j, k);
            if (block != null) {
                double posY = playerBoundingBox.minY + 0.1D;
                double motionX = -playerMotionX * 4D;
                double motionY = 1.5D;
                double motionZ = -playerMotionZ * 4D;

                spawnSlindingParticle += horizontalSpeedSquare;

                // float maxSpawnSlindingParticle = Config._slideParticlePeriodFactor.value * 0.1F;
                float maxSpawnSlindingParticle = 0.5f * 0.1F; // Sliding particle spawning period factor (>= 0)
                while (spawnSlindingParticle > maxSpawnSlindingParticle) {
                    double posX = player.getPosX() + getSpawnOffset();
                    double posZ = player.getPosZ() + getSpawnOffset();
                    player.getWorld().spawnParticle(EnumParticleTypes.BLOCK_CRACK,
                            posX, posY, posZ,
                            motionX, motionY, motionZ,
                            new int[] { Block.getStateId(getBlockState(i, j, k)) });
                    spawnSlindingParticle -= maxSpawnSlindingParticle;
                }
            }
        }

        if (isSwimming) {
            float posY = MathHelper.floor(player.getEntityBoundingBox().minY) + 1.0F;
            int x = (int) Math.floor(player.getPosX());
            int y = (int) Math.floor(posY - 0.5);
            int z = (int) Math.floor(player.getPosZ());

            boolean isLava = getBlockState(x, y, z).getMaterial() == Material.LAVA;
            spawnSwimmingParticle += horizontalSpeedSquare;

            // Config._lavaSwimParticlePeriodFactor.value = 4f
            // Config._swimParticlePeriodFactor.value = 0f
            // float maxSpawnSwimmingParticle = (isLava ? Config._lavaSwimParticlePeriodFactor.value : Config._swimParticlePeriodFactor.value) * 0.01F;
            float maxSpawnSwimmingParticle = (isLava ? 4f : 0f) * 0.01F;
            while (spawnSwimmingParticle > maxSpawnSwimmingParticle) {
                double posX = player.getPosX() + getSpawnOffset();
                double posZ = player.getPosZ() + getSpawnOffset();
                Particle splash = isLava ? new ParticleSplash.Factory().createParticle(EnumParticleTypes.LAVA.getParticleID(), player.getWorld(), posX, posY, posZ, 0, 0.2, 0)
                        : new ParticleSplash.Factory().createParticle(EnumParticleTypes.WATER_SPLASH.getParticleID(), player.getWorld(), posX, posY, posZ, 0, 0.2, 0);
                minecraft.effectRenderer.addEffect(splash);

                spawnSwimmingParticle -= maxSpawnSwimmingParticle;
            }
        }
    }

    private float getSpawnOffset() {
        return (player.getRNG().nextFloat() - 0.5F) * 2F * player.getWidth();
    }

    public abstract double getOverGroundHeight(double maximum);

    public abstract Block getOverGroundBlockId(double distance);

    protected AxisAlignedBB getBoundingBox() {
        return player.getEntityBoundingBox();
    }

    protected Block getBlock(int x, int y, int z) {
        return player.getWorld().getBlockState(new BlockPos(x, y, z)).getBlock();
    }

    protected IBlockState getBlockState(int x, int y, int z) {
        return player.getWorld().getBlockState(new BlockPos(x, y, z));
    }

    private List<AxisAlignedBB> getPlayerSolidBetween(double yMin, double yMax, double horizontalTolerance) {
        AxisAlignedBB bb = getBoundingBox();
        bb = new AxisAlignedBB(bb.minX, yMin, bb.minZ, bb.maxX, yMax, bb.maxZ);
        return player.getIntersectingCollisionBoxes(horizontalTolerance == 0 ? bb : bb.expand(horizontalTolerance, 0, horizontalTolerance));
    }

    public double getMaxPlayerSolidBetween(double yMin, double yMax, double horizontalTolerance) {
        List<AxisAlignedBB> solids = getPlayerSolidBetween(yMin, yMax, horizontalTolerance);
        double result = yMin;
        for (int i = 0; i < solids.size(); i++) {
            AxisAlignedBB box = solids.get(i);
            if (isCollided(box, yMin, yMax, horizontalTolerance))
                result = Math.max(result, box.maxY);
        }
        return Math.min(result, yMax);
    }

    public double getMinPlayerSolidBetween(double yMin, double yMax, double horizontalTolerance) {
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

    protected void onStartClimbBackJump() {
        RenderDataTracker.getPreviousRendererData(player).rotateAngleY += isHeadJumping ? Half : Quarter;
        isClimbBackJumping = true;
    }

    protected void onStartWallJump(Float angle) {
        if (angle != null)
            RenderDataTracker.getPreviousRendererData(player).rotateAngleY = angle / RadiantToAngle;
        isWallJumping = true;
        player.setFallDistance(0);
    }
}
