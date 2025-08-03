package com.github.andrew0030.pandora_core.client.screen_shaker.shakes.curve_shake;

import com.github.andrew0030.pandora_core.client.screen_shaker.shakes.ScreenShake;
import com.github.andrew0030.pandora_core.client.screen_shaker.shakes.enums.*;
import com.github.andrew0030.pandora_core.utils.easing.Easing;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

/** A curve based {@link ScreenShake}, that allows for {@link EasingDirection} & {@link Easing} on each transform type. */
public class CurveScreenShake extends ScreenShake {
    protected static final RandomSource random = RandomSource.create();
    // Rotation
    protected float yawDegrees, yawBounces = 0.0F;
    protected EasingDirection yawEasingDirection = EasingDirection.NONE;
    protected Easing yawEasingType = Easing.LINEAR;
    protected float pitchDegrees, pitchBounces = 0.0F;
    protected EasingDirection pitchEasingDirection = EasingDirection.NONE;
    protected Easing pitchEasingType = Easing.LINEAR;
    protected float rollDegrees, rollBounces = 0.0F;
    protected EasingDirection rollEasingDirection = EasingDirection.NONE;
    protected Easing rollEasingType = Easing.LINEAR;
    // Position Relative
    protected float horizontalDistance, horizontalBounces = 0.0F;
    protected EasingDirection horizontalEasingDirection = EasingDirection.NONE;
    protected Easing horizontalEasingType = Easing.LINEAR;
    protected float verticalDistance, verticalBounces = 0.0F;
    protected EasingDirection verticalEasingDirection = EasingDirection.NONE;
    protected Easing verticalEasingType = Easing.LINEAR;
    protected float depthDistance, depthBounces = 0.0F;
    protected EasingDirection depthEasingDirection = EasingDirection.NONE;
    protected Easing depthEasingType = Easing.LINEAR;
    // Position Absolute
    protected float xDistance, xBounces = 0.0F;
    protected EasingDirection xEasingDirection = EasingDirection.NONE;
    protected Easing xEasingType = Easing.LINEAR;
    protected float yDistance, yBounces = 0.0F;
    protected EasingDirection yEasingDirection = EasingDirection.NONE;
    protected Easing yEasingType = Easing.LINEAR;
    protected float zDistance, zBounces = 0.0F;
    protected EasingDirection zEasingDirection = EasingDirection.NONE;
    protected Easing zEasingType = Easing.LINEAR;
    // Fov
    protected float fovDistance, fovBounces = 0.0F;
    protected EasingDirection fovEasingDirection = EasingDirection.NONE;
    protected Easing fovEasingType = Easing.LINEAR;

    /**
     * A new {@link CurveScreenShake} instance, methods can/should be
     * chained onto this to specify the exact behavior it should have.
     * @param duration How long this {@link CurveScreenShake} should last, measured in ticks.
     */
    public CurveScreenShake(int duration) {
        super(duration);
    }

    /**
     * Specifies a "shake" curve, that gets applied to the camera's yaw (left/right movement).
     * <br/> This method uses {@link YawDirection}, which has {@code RANDOM} and easy directional logic.
     * <br/> For passing direct values use {@link #setYaw(float, float)} instead.
     * @param direction The initial direction in which the curve will move.
     * @param degrees   The number of degrees the camera should move in the specified direction.
     * @param bounces   The number of times the curve should "bounce".<br/>
     *                  A bounce is defined as starting at 0° and moving the specified number of degrees in one direction, then returning to 0°.<br/>
     * @implNote While {@code bounces} is typically an integer, decimal values can be used to end the movement at a position different from the start position.
     */
    public CurveScreenShake setYaw(YawDirection direction, float degrees, float bounces) {
        return this.setYaw(
                Math.abs(degrees) * direction.getValue(random),
                bounces
        );
    }

    /**
     * Specifies a "shake" curve, that gets applied to the camera's yaw (left/right movement).
     * <br/> For easier direction logic and randomization, use {@link #setYaw(YawDirection, float, float)} instead.
     * @param degrees The number of degrees the camera should move.
     * @param bounces The number of times the curve should "bounce".<br/>
     *                A bounce is defined as starting at 0° and moving the specified number of degrees in one direction, then returning to 0°.<br/>
     * @implNote While {@code bounces} is typically an integer, decimal values can be used to end the movement at a position different from the start position.
     */
    public CurveScreenShake setYaw(float degrees, float bounces) {
        this.yawDegrees = degrees;
        this.yawBounces = Math.max(0, bounces);
        return this;
    }

    /**
     * Specifies a "shake" curve, that gets applied to the camera's pitch (up/down movement).
     * <br/> This method uses {@link PitchDirection}, which has {@code RANDOM} and easy directional logic.
     * <br/> For passing direct values use {@link #setPitch(float, float)} instead.
     * @param direction The initial direction in which the curve will move.
     * @param degrees   The number of degrees the camera should move in the specified direction.
     * @param bounces   The number of times the curve should "bounce".<br/>
     *                  A bounce is defined as starting at 0° and moving the specified number of degrees in one direction, then returning to 0°.<br/>
     * @implNote While {@code bounces} is typically an integer, decimal values can be used to end the movement at a position different from the start position.
     */
    public CurveScreenShake setPitch(PitchDirection direction, float degrees, float bounces) {
        return this.setPitch(
                Math.abs(degrees) * direction.getValue(random),
                bounces
        );
    }

    /**
     * Specifies a "shake" curve, that gets applied to the camera's pitch (up/down movement).
     * <br/> For easier direction logic and randomization, use {@link #setPitch(PitchDirection, float, float)} instead.
     * @param degrees The number of degrees the camera should move.
     * @param bounces The number of times the curve should "bounce".<br/>
     *                A bounce is defined as starting at 0° and moving the specified number of degrees in one direction, then returning to 0°.<br/>
     * @implNote While {@code bounces} is typically an integer, decimal values can be used to end the movement at a position different from the start position.
     */
    public CurveScreenShake setPitch(float degrees, float bounces) {
        this.pitchDegrees = degrees;
        this.pitchBounces = Math.max(0, bounces);
        return this;
    }

    /**
     * Specifies a "shake" curve, that gets applied to the camera's roll (rotational movement around the forward axis).
     * <br/> This method uses {@link RollDirection}, which has {@code RANDOM} and easy directional logic.
     * <br/> For passing direct values use {@link #setRoll(float, float)} instead.
     * @param direction The initial direction in which the curve will move.
     * @param degrees   The number of degrees the camera should move in the specified direction.
     * @param bounces   The number of times the curve should "bounce".<br/>
     *                  A bounce is defined as starting at 0° and moving the specified number of degrees in one direction, then returning to 0°.<br/>
     * @implNote While {@code bounces} is typically an integer, decimal values can be used to end the movement at a position different from the start position.
     */
    public CurveScreenShake setRoll(RollDirection direction, float degrees, float bounces) {
        return this.setRoll(
                Math.abs(degrees) * direction.getValue(random),
                bounces
        );
    }

    /**
     * Specifies a "shake" curve, that gets applied to the camera's roll (rotational movement around the forward axis).
     * <br/> For easier direction logic and randomization, use {@link #setRoll(RollDirection, float, float)} instead.
     * @param degrees The number of degrees the camera should move.
     * @param bounces The number of times the curve should "bounce".<br/>
     *                A bounce is defined as starting at 0° and moving the specified number of degrees in one direction, then returning to 0°.<br/>
     * @implNote While {@code bounces} is typically an integer, decimal values can be used to end the movement at a position different from the start position.
     */
    public CurveScreenShake setRoll(float degrees, float bounces) {
        this.rollDegrees = degrees;
        this.rollBounces = Math.max(0, bounces);
        return this;
    }

    /**
     * Specifies a "shake" curve, that gets applied to the camera's horizontal position (relative left/right movement).
     * <br/> This method uses {@link HorizontalDirection}, which has {@code RANDOM} and easy directional logic.
     * <br/> For passing direct values use {@link #setHorizontal(float, float)} instead.
     * @param direction The initial direction in which the curve will move.
     * @param distance  The number of blocks the camera should move in the specified direction.
     * @param bounces   The number of times the curve should "bounce".<br/>
     *                  A bounce is defined as starting at 0° and moving the specified number of degrees in one direction, then returning to 0°.<br/>
     * @implNote While {@code bounces} is typically an integer, decimal values can be used to end the movement at a position different from the start position.
     */
    public CurveScreenShake setHorizontal(HorizontalDirection direction, float distance, float bounces) {
        return this.setHorizontal(
                Math.abs(distance) * direction.getValue(random),
                bounces
        );
    }

    /**
     * Specifies a "shake" curve, that gets applied to the camera's horizontal position (relative left/right movement).
     * <br/> For easier direction logic and randomization, use {@link #setHorizontal(HorizontalDirection, float, float)} instead.
     * @param distance The number of blocks the camera should move.
     * @param bounces  The number of times the curve should "bounce".<br/>
     *                 A bounce is defined as starting at 0° and moving the specified number of degrees in one direction, then returning to 0°.<br/>
     * @implNote While {@code bounces} is typically an integer, decimal values can be used to end the movement at a position different from the start position.
     */
    public CurveScreenShake setHorizontal(float distance, float bounces) {
        this.horizontalDistance = distance;
        this.horizontalBounces = Math.max(0, bounces);
        return this;
    }

    /**
     * Specifies a "shake" curve, that gets applied to the camera's vertical position (relative up/down movement).
     * <br/> This method uses {@link VerticalDirection}, which has {@code RANDOM} and easy directional logic.
     * <br/> For passing direct values use {@link #setVertical(float, float)} instead.
     * @param direction The initial direction in which the curve will move.
     * @param distance  The number of blocks the camera should move in the specified direction.
     * @param bounces   The number of times the curve should "bounce".<br/>
     *                  A bounce is defined as starting at 0° and moving the specified number of degrees in one direction, then returning to 0°.<br/>
     * @implNote While {@code bounces} is typically an integer, decimal values can be used to end the movement at a position different from the start position.
     */
    public CurveScreenShake setVertical(VerticalDirection direction, float distance, float bounces) {
        return this.setVertical(
                Math.abs(distance) * direction.getValue(random),
                bounces
        );
    }

    /**
     * Specifies a "shake" curve, that gets applied to the camera's vertical position (relative up/down movement).
     * <br/> For easier direction logic and randomization, use {@link #setVertical(VerticalDirection, float, float)} instead.
     * @param distance The number of blocks the camera should move.
     * @param bounces  The number of times the curve should "bounce".<br/>
     *                 A bounce is defined as starting at 0° and moving the specified number of degrees in one direction, then returning to 0°.<br/>
     * @implNote While {@code bounces} is typically an integer, decimal values can be used to end the movement at a position different from the start position.
     */
    public CurveScreenShake setVertical(float distance, float bounces) {
        this.verticalDistance = distance;
        this.verticalBounces = Math.max(0, bounces);
        return this;
    }

    /**
     * Specifies a "shake" curve, that gets applied to the camera's depth position (relative movement along the forward axis).
     * <br/> This method uses {@link DepthDirection}, which has {@code RANDOM} and easy directional logic.
     * <br/> For passing direct values use {@link #setDepth(float, float)} instead.
     * @param direction The initial direction in which the curve will move.
     * @param distance  The number of blocks the camera should move in the specified direction.
     * @param bounces   The number of times the curve should "bounce".<br/>
     *                  A bounce is defined as starting at 0° and moving the specified number of degrees in one direction, then returning to 0°.<br/>
     * @implNote While {@code bounces} is typically an integer, decimal values can be used to end the movement at a position different from the start position.
     */
    public CurveScreenShake setDepth(DepthDirection direction, float distance, float bounces) {
        return this.setDepth(
                Math.abs(distance) * direction.getValue(random),
                bounces
        );
    }

    /**
     * Specifies a "shake" curve, that gets applied to the camera's depth position (relative movement along the forward axis).
     * <br/> For easier direction logic and randomization, use {@link #setDepth(DepthDirection, float, float)} instead.
     * @param distance The number of blocks the camera should move.
     * @param bounces  The number of times the curve should "bounce".<br/>
     *                 A bounce is defined as starting at 0° and moving the specified number of degrees in one direction, then returning to 0°.<br/>
     * @implNote While {@code bounces} is typically an integer, decimal values can be used to end the movement at a position different from the start position.
     */
    public CurveScreenShake setDepth(float distance, float bounces) {
        this.depthDistance = distance;
        this.depthBounces = Math.max(0, bounces);
        return this;
    }

    /**
     * Specifies a "shake" curve, that gets applied to the camera's x position (absolute x position).
     * <br/> This method uses {@link AxisDirection}, which has {@code RANDOM} and easy directional logic.
     * <br/> For passing direct values use {@link #setX(float, float)} instead.
     * @param direction The initial direction in which the curve will move.
     * @param distance  The number of blocks the camera should move in the specified direction.
     * @param bounces   The number of times the curve should "bounce".<br/>
     *                  A bounce is defined as starting at 0° and moving the specified number of degrees in one direction, then returning to 0°.<br/>
     * @implNote While {@code bounces} is typically an integer, decimal values can be used to end the movement at a position different from the start position.
     */
    public CurveScreenShake setX(AxisDirection direction, float distance, float bounces) {
        return this.setX(
                Math.abs(distance) * direction.getValue(random),
                bounces
        );
    }

    /**
     * Specifies a "shake" curve, that gets applied to the camera's x position (absolute x position).
     * <br/> For easier direction logic and randomization, use {@link #setX(AxisDirection, float, float)} instead.
     * @param distance The number of blocks the camera should move.
     * @param bounces  The number of times the curve should "bounce".<br/>
     *                 A bounce is defined as starting at 0° and moving the specified number of degrees in one direction, then returning to 0°.<br/>
     * @implNote While {@code bounces} is typically an integer, decimal values can be used to end the movement at a position different from the start position.
     */
    public CurveScreenShake setX(float distance, float bounces) {
        this.xDistance = distance;
        this.xBounces = Math.max(0, bounces);
        return this;
    }

    /**
     * Specifies a "shake" curve, that gets applied to the camera's y position (absolute y position).
     * <br/> This method uses {@link AxisDirection}, which has {@code RANDOM} and easy directional logic.
     * <br/> For passing direct values use {@link #setY(float, float)} instead.
     * @param direction The initial direction in which the curve will move.
     * @param distance  The number of blocks the camera should move in the specified direction.
     * @param bounces   The number of times the curve should "bounce".<br/>
     *                  A bounce is defined as starting at 0° and moving the specified number of degrees in one direction, then returning to 0°.<br/>
     * @implNote While {@code bounces} is typically an integer, decimal values can be used to end the movement at a position different from the start position.
     */
    public CurveScreenShake setY(AxisDirection direction, float distance, float bounces) {
        return this.setY(
                Math.abs(distance) * direction.getValue(random),
                bounces
        );
    }

    /**
     * Specifies a "shake" curve, that gets applied to the camera's y position (absolute y position).
     * <br/> For easier direction logic and randomization, use {@link #setY(AxisDirection, float, float)} instead.
     * @param distance The number of blocks the camera should move.
     * @param bounces  The number of times the curve should "bounce".<br/>
     *                 A bounce is defined as starting at 0° and moving the specified number of degrees in one direction, then returning to 0°.<br/>
     * @implNote While {@code bounces} is typically an integer, decimal values can be used to end the movement at a position different from the start position.
     */
    public CurveScreenShake setY(float distance, float bounces) {
        this.yDistance = distance;
        this.yBounces = Math.max(0, bounces);
        return this;
    }

    /**
     * Specifies a "shake" curve, that gets applied to the camera's z position (absolute z position).
     * <br/> This method uses {@link AxisDirection}, which has {@code RANDOM} and easy directional logic.
     * <br/> For passing direct values use {@link #setZ(float, float)} instead.
     * @param direction The initial direction in which the curve will move.
     * @param distance  The number of blocks the camera should move in the specified direction.
     * @param bounces   The number of times the curve should "bounce".<br/>
     *                  A bounce is defined as starting at 0° and moving the specified number of degrees in one direction, then returning to 0°.<br/>
     * @implNote While {@code bounces} is typically an integer, decimal values can be used to end the movement at a position different from the start position.
     */
    public CurveScreenShake setZ(AxisDirection direction, float distance, float bounces) {
        return this.setZ(
                Math.abs(distance) * direction.getValue(random),
                bounces
        );
    }

    /**
     * Specifies a "shake" curve, that gets applied to the camera's z position (absolute z position).
     * <br/> For easier direction logic and randomization, use {@link #setY(AxisDirection, float, float)} instead.
     * @param distance The number of blocks the camera should move.
     * @param bounces  The number of times the curve should "bounce".<br/>
     *                 A bounce is defined as starting at 0° and moving the specified number of degrees in one direction, then returning to 0°.<br/>
     * @implNote While {@code bounces} is typically an integer, decimal values can be used to end the movement at a position different from the start position.
     */
    public CurveScreenShake setZ(float distance, float bounces) {
        this.zDistance = distance;
        this.zBounces = Math.max(0, bounces);
        return this;
    }

    /**
     * Specifies a "shake" curve, that gets applied to the camera's fov offset.
     * <br/> This method uses {@link FovDirection}, which has {@code RANDOM} and easy directional logic.
     * <br/> For passing direct values use {@link #setFov(float, float)} instead.
     * @param direction The initial direction in which the curve will move.
     * @param degrees   The number of degrees the fov angle should change.
     * @param bounces   The number of times the curve should "bounce".<br/>
     *                  A bounce is defined as starting at 0° and moving the specified number of degrees in one direction, then returning to 0°.<br/>
     * @implNote While {@code bounces} is typically an integer, decimal values can be used to end the movement at a position different from the start position.
     */
    public CurveScreenShake setFov(FovDirection direction, float degrees, float bounces) {
        return this.setFov(
                Math.abs(degrees) * direction.getValue(random),
                bounces
        );
    }

    /**
     * Specifies a "shake" curve, that gets applied to the camera's fov offset.
     * <br/> For easier direction logic and randomization, use {@link #setFov(FovDirection, float, float)} instead.
     * @param degrees The number of degrees the fov angle should change.
     * @param bounces The number of times the curve should "bounce".<br/>
     *                A bounce is defined as starting at 0° and moving the specified number of degrees in one direction, then returning to 0°.<br/>
     * @implNote While {@code bounces} is typically an integer, decimal values can be used to end the movement at a position different from the start position.
     */
    public CurveScreenShake setFov(float degrees, float bounces) {
        this.fovDistance = degrees;
        this.fovBounces = Math.max(0, bounces);
        return this;
    }

    /**
     * Specifies the easing direction of the yaw curve, and the easing type that will get applied on it.<br/>
     * Essentially with this a curve can be "eased" so the start/end or both aren't so abrupt, and then fine-tuned
     * with an easing type.
     * @param easingDirection The easing direction of the curve
     * @param easingType      The easing type of the curve, that gets applied on the direction
     */
    public CurveScreenShake setYawEasing(EasingDirection easingDirection, Easing easingType) {
        this.yawEasingDirection = easingDirection;
        this.yawEasingType = easingType;
        return this;
    }

    /**
     * Specifies the easing direction of the pitch curve, and the easing type that will get applied on it.<br/>
     * Essentially with this a curve can be "eased" so the start/end or both aren't so abrupt, and then fine-tuned
     * with an easing type.
     * @param easingDirection The easing direction of the curve
     * @param easingType      The easing type of the curve, that gets applied on the direction
     */
    public CurveScreenShake setPitchEasing(EasingDirection easingDirection, Easing easingType) {
        this.pitchEasingDirection = easingDirection;
        this.pitchEasingType = easingType;
        return this;
    }

    /**
     * Specifies the easing direction of the roll curve, and the easing type that will get applied on it.<br/>
     * Essentially with this a curve can be "eased" so the start/end or both aren't so abrupt, and then fine-tuned
     * with an easing type.
     * @param easingDirection The easing direction of the curve
     * @param easingType      The easing type of the curve, that gets applied on the direction
     */
    public CurveScreenShake setRollEasing(EasingDirection easingDirection, Easing easingType) {
        this.rollEasingDirection = easingDirection;
        this.rollEasingType = easingType;
        return this;
    }

    /**
     * Specifies the easing direction of the horizontal curve, and the easing type that will get applied on it.<br/>
     * Essentially with this a curve can be "eased" so the start/end or both aren't so abrupt, and then fine-tuned
     * with an easing type.
     * @param easingDirection The easing direction of the curve
     * @param easingType      The easing type of the curve, that gets applied on the direction
     */
    public CurveScreenShake setHorizontalEasing(EasingDirection easingDirection, Easing easingType) {
        this.horizontalEasingDirection = easingDirection;
        this.horizontalEasingType = easingType;
        return this;
    }

    /**
     * Specifies the easing direction of the vertical curve, and the easing type that will get applied on it.<br/>
     * Essentially with this a curve can be "eased" so the start/end or both aren't so abrupt, and then fine-tuned
     * with an easing type.
     * @param easingDirection The easing direction of the curve
     * @param easingType      The easing type of the curve, that gets applied on the direction
     */
    public CurveScreenShake setVerticalEasing(EasingDirection easingDirection, Easing easingType) {
        this.verticalEasingDirection = easingDirection;
        this.verticalEasingType = easingType;
        return this;
    }

    /**
     * Specifies the easing direction of the depth curve, and the easing type that will get applied on it.<br/>
     * Essentially with this a curve can be "eased" so the start/end or both aren't so abrupt, and then fine-tuned
     * with an easing type.
     * @param easingDirection The easing direction of the curve
     * @param easingType      The easing type of the curve, that gets applied on the direction
     */
    public CurveScreenShake setDepthEasing(EasingDirection easingDirection, Easing easingType) {
        this.depthEasingDirection = easingDirection;
        this.depthEasingType = easingType;
        return this;
    }

    /**
     * Specifies the easing direction of the x curve, and the easing type that will get applied on it.<br/>
     * Essentially with this a curve can be "eased" so the start/end or both aren't so abrupt, and then fine-tuned
     * with an easing type.
     * @param easingDirection The easing direction of the curve
     * @param easingType      The easing type of the curve, that gets applied on the direction
     */
    public CurveScreenShake setXEasing(EasingDirection easingDirection, Easing easingType) {
        this.xEasingDirection = easingDirection;
        this.xEasingType = easingType;
        return this;
    }

    /**
     * Specifies the easing direction of the y curve, and the easing type that will get applied on it.<br/>
     * Essentially with this a curve can be "eased" so the start/end or both aren't so abrupt, and then fine-tuned
     * with an easing type.
     * @param easingDirection The easing direction of the curve
     * @param easingType      The easing type of the curve, that gets applied on the direction
     */
    public CurveScreenShake setYEasing(EasingDirection easingDirection, Easing easingType) {
        this.yEasingDirection = easingDirection;
        this.yEasingType = easingType;
        return this;
    }

    /**
     * Specifies the easing direction of the z curve, and the easing type that will get applied on it.<br/>
     * Essentially with this a curve can be "eased" so the start/end or both aren't so abrupt, and then fine-tuned
     * with an easing type.
     * @param easingDirection The easing direction of the curve
     * @param easingType      The easing type of the curve, that gets applied on the direction
     */
    public CurveScreenShake setZEasing(EasingDirection easingDirection, Easing easingType) {
        this.zEasingDirection = easingDirection;
        this.zEasingType = easingType;
        return this;
    }

    /**
     * Specifies the easing direction of the FOV curve, and the easing type that will get applied on it.<br/>
     * Essentially with this a curve can be "eased" so the start/end or both aren't so abrupt, and then fine-tuned
     * with an easing type.
     * @param easingDirection The easing direction of the curve
     * @param easingType      The easing type of the curve, that gets applied on the direction
     */
    public CurveScreenShake setFovEasing(EasingDirection easingDirection, Easing easingType) {
        this.fovEasingDirection = easingDirection;
        this.fovEasingType = easingType;
        return this;
    }

    @Override
    public float getYawOffset(float partialTick) {
        if (this.tickCount >= this.duration) return 0.0F;
        return this.calculateOffset(partialTick, this.yawDegrees, this.yawBounces, this.yawEasingDirection, this.yawEasingType);
    }

    @Override
    public float getPitchOffset(float partialTick) {
        if (this.tickCount >= this.duration) return 0.0F;
        return this.calculateOffset(partialTick, this.pitchDegrees, this.pitchBounces, this.pitchEasingDirection, this.pitchEasingType);
    }

    @Override
    public float getRollOffset(float partialTick) {
        if (this.tickCount >= this.duration) return 0.0F;
        return this.calculateOffset(partialTick, this.rollDegrees, this.rollBounces, this.rollEasingDirection, this.rollEasingType);
    }

    @Override
    public float getHorizontalOffset(float partialTick) {
        if (this.tickCount >= this.duration) return 0.0F;
        return this.calculateOffset(partialTick, this.horizontalDistance, this.horizontalBounces, this.horizontalEasingDirection, this.horizontalEasingType);
    }

    @Override
    public float getVerticalOffset(float partialTick) {
        if (this.tickCount >= this.duration) return 0.0F;
        return this.calculateOffset(partialTick, this.verticalDistance, this.verticalBounces, this.verticalEasingDirection, this.verticalEasingType);
    }

    @Override
    public float getDepthOffset(float partialTick) {
        if (this.tickCount >= this.duration) return 0.0F;
        return this.calculateOffset(partialTick, this.depthDistance, this.depthBounces, this.depthEasingDirection, this.depthEasingType);
    }

    @Override
    public float getXOffset(float partialTick) {
        if (this.tickCount >= this.duration) return 0.0F;
        return this.calculateOffset(partialTick, this.xDistance, this.xBounces, this.xEasingDirection, this.xEasingType);
    }

    @Override
    public float getYOffset(float partialTick) {
        if (this.tickCount >= this.duration) return 0.0F;
        return this.calculateOffset(partialTick, this.yDistance, this.yBounces, this.yEasingDirection, this.yEasingType);
    }

    @Override
    public float getZOffset(float partialTick) {
        if (this.tickCount >= this.duration) return 0.0F;
        return this.calculateOffset(partialTick, this.zDistance, this.zBounces, this.zEasingDirection, this.zEasingType);
    }

    @Override
    public float getFovOffset(float partialTick) {
        if (this.tickCount >= this.duration) return 0.0F;
        return this.calculateOffset(partialTick, this.fovDistance, this.fovBounces, this.fovEasingDirection, this.fovEasingType);
    }

    /** @return The offset based on the given values */
    protected float calculateOffset(float partialTick, float amount, float bounces, EasingDirection easingDirection, Easing easingType) {
        if (amount == 0 || bounces == 0) return 0;
        // We add partial ticks to the tick counter, so the value we work with is more precise
        float totalTime = this.tickCount + partialTick;
        // We get the time converted to a 0.0F -> 1.0F value, and we apply easing to it.
        float normalizedTime = easingType.apply(totalTime / this.duration);
        // Based on the given easing direction and the normalized time, we create a multiplier between 0.0F - 1.0F
        float easingDirectionMultiplier = switch (easingDirection) {
            case NONE -> 1.0F;
            case IN -> normalizedTime;
            case OUT -> 1F - normalizedTime;
            case IN_OUT -> (normalizedTime <= 0.5F) ? normalizedTime * 2 : 2 * (1 - normalizedTime);
        };
        // We calculate the phase of the sine wave.
        float phase = normalizedTime * Mth.PI * bounces;

        return amount * easingDirectionMultiplier * Mth.sin(phase);
    }
}