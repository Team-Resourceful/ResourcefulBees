package com.teamresourceful.resourcefulbees.common.logistics;

public record TransferResult(
        TransferStatus status,
        int itemsMoved
) {
}
