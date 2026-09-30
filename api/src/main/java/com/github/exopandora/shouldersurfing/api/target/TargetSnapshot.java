package com.github.exopandora.shouldersurfing.api.target;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

/**
 * 在客户端安全阶段发布的目标快照。
 *
 * <p>快照只包含跨模块和跨线程所需的不可变值，不持有 Entity 引用。
 * 具体目标是否仍然有效必须在消费方根据 UUID 和维度重新验证。</p>
 */
public record TargetSnapshot(
		UUID sessionId,
		UUID ownerUuid,
		UUID entityUuid,
		ResourceKey<Level> dimension,
		Vec3 anchor,
		long sampledGameTime,
		long selectionVersion,
		TargetClassification classification)
{
	public TargetSnapshot
	{
		if (sessionId == null || ownerUuid == null) throw new IllegalArgumentException("session and owner are required");
		if (entityUuid == null) throw new IllegalArgumentException("entityUuid cannot be null");
		if (dimension == null) throw new IllegalArgumentException("dimension cannot be null");
		if (anchor == null || !isFinite(anchor)) throw new IllegalArgumentException("anchor must be finite");
		if (sampledGameTime < 0) throw new IllegalArgumentException("sampledGameTime cannot be negative");
		if (selectionVersion < 0) throw new IllegalArgumentException("selectionVersion cannot be negative");
		if (classification == null) throw new IllegalArgumentException("classification cannot be null");
	}

	private static boolean isFinite(Vec3 value)
	{
		return Double.isFinite(value.x) && Double.isFinite(value.y) && Double.isFinite(value.z);
	}
}
