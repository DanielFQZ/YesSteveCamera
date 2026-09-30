package com.github.exopandora.shouldersurfing.api.target;

/**
 * 分类服务对候选实体给出的战斗目标类别。
 */
public enum TargetClassification
{
	/** 可以被自动索敌和主动锁定。 */
	HOSTILE,
	/** 只能在玩家明确锁定后使用。 */
	MANUAL_ONLY,
	/** 任何自动或主动锁定都禁止。 */
	PROTECTED
}
