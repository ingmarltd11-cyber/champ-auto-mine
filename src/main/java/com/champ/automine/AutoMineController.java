package com.champ.automine;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public final class AutoMineController {
    private static float startYaw;
    private static int avoidTicks;
    private static Direction avoidDirection;
    private static BlockPos miningTarget;
    private static String status = "Ready";

    private AutoMineController() {
    }

    public static boolean isEnabled() {
        return AutoMineConfig.enabled;
    }

    public static String status() {
        return status;
    }

    public static void toggle(MinecraftClient client) {
        if (AutoMineConfig.enabled) {
            disable(client, "Stopped");
            return;
        }
        if (client.player == null || client.world == null || client.interactionManager == null) {
            status = "Join a world first";
            return;
        }
        startYaw = client.player.getYaw();
        avoidTicks = 0;
        avoidDirection = null;
        miningTarget = null;
        AutoMineConfig.enabled = true;
        status = "Mining forward";
        AutoMineConfig.save();
    }

    public static void tick(MinecraftClient client) {
        if (!AutoMineConfig.enabled) return;
        if (client.player == null || client.world == null || client.interactionManager == null) {
            disable(client, "World unloaded");
            return;
        }

        if (AutoMineConfig.lockPitch) client.player.setPitch(AutoMineConfig.pitch);
        if (AutoMineConfig.lockYaw) client.player.setYaw(startYaw);

        if (AutoMineConfig.avoidLava) {
            if (avoidTicks > 0) {
                applyAvoidance(client);
                return;
            }
            Direction hazard = hazardAhead(client);
            if (hazard != null) {
                avoidDirection = saferSide(client, hazard);
                avoidTicks = 8;
                status = "Avoiding hazard";
                applyAvoidance(client);
                return;
            }
        }

        releaseStrafe(client);
        setKey(client, client.options.forwardKey, AutoMineConfig.autoForward);
        setKey(client, client.options.attackKey, AutoMineConfig.autoBreak);
        if (AutoMineConfig.autoBreak) mineAhead(client);
    }

    private static void mineAhead(MinecraftClient client) {
        Direction facing = Direction.fromHorizontalDegrees(startYaw);
        if (miningTarget != null) {
            BlockState state = client.world.getBlockState(miningTarget);
            if (breakable(client, state, miningTarget)) {
                client.interactionManager.updateBlockBreakingProgress(miningTarget, facing.getOpposite());
                client.player.swingHand(Hand.MAIN_HAND);
                return;
            }
            miningTarget = null;
        }

        BlockPos ahead = client.player.getBlockPos().offset(facing);
        if (breakable(client, client.world.getBlockState(ahead), ahead)) {
            miningTarget = ahead;
            client.interactionManager.attackBlock(ahead, facing.getOpposite());
            client.player.swingHand(Hand.MAIN_HAND);
            status = "Mining forward";
        } else {
            status = "No block ahead";
        }
    }

    private static boolean breakable(MinecraftClient client, BlockState state, BlockPos pos) {
        if (state.isAir()) return false;
        Block block = state.getBlock();
        if (block == Blocks.WATER || block == Blocks.LAVA || block == Blocks.BEDROCK) return false;
        return state.getHardness(client.world, pos) >= 0.0F;
    }

    private static boolean hazardous(MinecraftClient client, BlockPos pos) {
        Block block = client.world.getBlockState(pos).getBlock();
        return block == Blocks.LAVA || block == Blocks.FIRE || block == Blocks.SOUL_FIRE;
    }

    private static Direction hazardAhead(MinecraftClient client) {
        Direction facing = Direction.fromHorizontalDegrees(startYaw);
        BlockPos feet = client.player.getBlockPos();
        for (int distance = 1; distance <= AutoMineConfig.scanRange; distance++) {
            BlockPos ahead = feet.offset(facing, distance);
            for (int up = -1; up <= 2; up++) {
                if (hazardous(client, ahead.up(up))) return facing;
            }
            for (int side = -1; side <= 1; side += 2) {
                Direction strafe = side < 0 ? facing.rotateYCounterclockwise() : facing.rotateYClockwise();
                for (int up = -1; up <= 2; up++) {
                    if (hazardous(client, ahead.offset(strafe).up(up))) return facing;
                }
            }
        }
        return null;
    }

    private static Direction saferSide(MinecraftClient client, Direction facing) {
        int left = hazardCount(client, facing, facing.rotateYCounterclockwise());
        int right = hazardCount(client, facing, facing.rotateYClockwise());
        return left <= right ? facing.rotateYCounterclockwise() : facing.rotateYClockwise();
    }

    private static int hazardCount(MinecraftClient client, Direction facing, Direction side) {
        BlockPos feet = client.player.getBlockPos();
        int count = 0;
        for (int distance = 0; distance <= AutoMineConfig.scanRange; distance++) {
            BlockPos pos = feet.offset(facing, distance).offset(side, 2);
            for (int up = -1; up <= 2; up++) {
                if (hazardous(client, pos.up(up))) count++;
            }
        }
        return count;
    }

    private static void applyAvoidance(MinecraftClient client) {
        Direction facing = Direction.fromHorizontalDegrees(startYaw);
        if (avoidTicks-- <= 0) {
            releaseStrafe(client);
            avoidDirection = null;
            return;
        }
        if (AutoMineConfig.lockPitch) client.player.setPitch(AutoMineConfig.pitch);
        if (AutoMineConfig.lockYaw) client.player.setYaw(startYaw);
        boolean left = avoidDirection == facing.rotateYCounterclockwise();
        setKey(client, client.options.leftKey, left);
        setKey(client, client.options.rightKey, !left);
        setKey(client, client.options.forwardKey, AutoMineConfig.autoForward);
        setKey(client, client.options.attackKey, AutoMineConfig.autoBreak);
    }

    public static void disable(MinecraftClient client, String reason) {
        AutoMineConfig.enabled = false;
        miningTarget = null;
        avoidTicks = 0;
        avoidDirection = null;
        releaseKeys(client);
        status = reason;
        AutoMineConfig.save();
        if (client.player != null) client.player.sendMessage(Text.literal("Auto Mine: " + reason), true);
    }

    private static void releaseKeys(MinecraftClient client) {
        releaseStrafe(client);
        if (client.options == null) return;
        setKey(client, client.options.forwardKey, false);
        setKey(client, client.options.attackKey, false);
    }

    private static void releaseStrafe(MinecraftClient client) {
        if (client.options == null) return;
        setKey(client, client.options.leftKey, false);
        setKey(client, client.options.rightKey, false);
    }

    private static void setKey(MinecraftClient client, KeyBinding key, boolean pressed) {
        if (key != null) key.setPressed(pressed);
    }
}
