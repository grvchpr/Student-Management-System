package com.kodewala.sms.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Set;

public final class PageRequestUtil {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 10;
    private static final int MAX_SIZE = 50;

    private PageRequestUtil() {
    }

    public static Pageable create(
            int page,
            int size,
            String sortBy,
            String direction,
            Set<String> allowedSortFields) {

        if (page < 0) {
            page = DEFAULT_PAGE;
        }

        if (size <= 0) {
            size = DEFAULT_SIZE;
        }

        if (size > MAX_SIZE) {
            size = MAX_SIZE;
        }

        if (!allowedSortFields.contains(sortBy)) {
            sortBy = "id";
        }

        Sort.Direction sortDirection =
                "desc".equalsIgnoreCase(direction)
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        return PageRequest.of(
                page,
                size,
                Sort.by(sortDirection, sortBy)
        );
    }
}