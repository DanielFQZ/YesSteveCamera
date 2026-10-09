package com.github.exopandora.shouldersurfing.api.target;

import com.github.exopandora.shouldersurfing.camera.target.TargetService;

import java.util.UUID;

/** Stable client-side target snapshot access for optional integrations. */
public final class TargetApi {
    private TargetApi() {
    }

    public static TargetSnapshot snapshot() {
        return TargetService.snapshot();
    }

    public static TargetSnapshot snapshotFor(UUID ownerUuid) {
        TargetSnapshot value = TargetService.snapshot();
        return value != null && ownerUuid != null && ownerUuid.equals(value.ownerUuid()) ? value : null;
    }
}
