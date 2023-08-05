package com.wirerest.wireguard;

import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class Paging<T> {
    Class<T> genericType;

    public Paging(Class<T> genericType) {
        this.genericType = genericType;
    }

    public Page<T> apply(Pageable pageable, List<T> list) {
        return buildPage(pageable, list);
    }

    private ArrayList<T> sort(Sort sort, ArrayList<T> list) {
        try {
            return sortList(sort.iterator(), list);
        } catch (NoSuchFieldException e) {
            throw new ParsingException("Field %s not exist".formatted(e.getMessage()), e);
        }
    }


    @SuppressWarnings("unchecked")
    private ArrayList<T> sortList(Iterator<Sort.Order> orders, ArrayList<T> list) throws NoSuchFieldException {
        if (!orders.hasNext() || list.isEmpty()) {
            return list;
        }
        Sort.Order order = orders.next();
        Field field = genericType.getDeclaredField(order.getProperty());
        field.setAccessible(true);

        Comparator<Object> comparator = (o1, o2) -> {
            try {
                if ((o1 == null && o2 == null) || (!(o1 == null || o2 == null) && (field.get(o1) == null && field.get(o2) == null))) {
                    return 0;
                } else if (o1 == null || field.get(o1) == null) {
                    return -1;
                } else if (o2 == null || field.get(o2) == null) {
                    return 1;
                }
                if (field.get(o1) instanceof Comparable) {
                    return ((Comparable<Object>) field.get(o1)).compareTo(field.get(o2));
                }
                return field.get(o1).toString().compareTo(field.get(o2).toString());
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Can't compare. ", e);
            }
        };
        if (order.isDescending()) {
            comparator = comparator.reversed();
        }
        list.sort(comparator);
        return sortList(orders, list);
    }

    private Page<T> buildPage(Pageable pageable, List<T> list) {
        ArrayList<T> sorted = sort(pageable.getSort(), new ArrayList<T>(list));
        PagedListHolder<T> page = new PagedListHolder<>(sorted);
        page.setPageSize(pageable.getPageSize());
        if (pageable.getPageNumber() >= page.getPageCount()) {
            throw new PageOutOfRangeException(pageable.getPageNumber(), page.getPageCount());
        }
        page.setPage(pageable.getPageNumber());
        return new PageImpl<T>(new ArrayList<>(page.getPageList()), pageable, list.size());
    }
}
