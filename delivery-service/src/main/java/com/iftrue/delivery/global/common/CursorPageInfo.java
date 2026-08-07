package com.iftrue.delivery.global.common;

import org.hibernate.query.SortDirection;

import java.util.UUID;

public record CursorPageInfo(
        String paginationType,
        String nextCursor,
        UUID nextIdAfter,
        boolean hasNext,
        String sortBy,
        SortDirection sortDirection
) {
}
