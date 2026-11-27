package com.teamresourceful.resourcefulbees.common.logistics;

public enum TransferStatus {
    SUCCESS,
    PARTIAL,
    DISABLED,
    UNSUPPORTED_RESOURCE,
    INVALID_LIMIT,
    SOURCE_UNAVAILABLE,
    DESTINATION_UNAVAILABLE,
    SOURCE_EMPTY,
    DESTINATION_FULL
}
