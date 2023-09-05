package ru.practicum.shareit.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class ShareItPageable extends PageRequest {
    public ShareItPageable(int from, int size, Sort sort) {
        super(from, size, sort);
    }

    public static ShareItPageable checkPageable(Integer from, Integer size, Sort sort) {
        if (from == null) {
            from = 0;
        }
        if (size == null) {
            size = 20;
        }
        return new ShareItPageable(from / size, size, sort);
    }
}
