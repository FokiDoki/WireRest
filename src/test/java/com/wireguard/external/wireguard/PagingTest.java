package com.wireguard.external.wireguard;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

class PagingTest {

    Paging<String> paging = new Paging<>(String.class);
    Paging<TestClass> pagingTestClass = new Paging<>(TestClass.class);
    List<TestClass> testList = List.of(
            new TestClass("a", 1),
            new TestClass("b", 2),
            new TestClass("c", 3),
            new TestClass("d", 4)
    );
    @Test
    void applyNoRules() {
        Page<String> paged = paging.apply(Pageable.ofSize(3), List.of("a", "b", "c", "d"));
        Assertions.assertEquals(2, paged.getTotalPages());
        Assertions.assertEquals(4, paged.getTotalElements());
        Assertions.assertEquals(3, paged.getSize());
    }

    @Test
    void applyNoFieldException(){
        Assertions.assertThrows(ParsingException.class, () -> {
            paging.apply(PageRequest.of(0, 1, Sort.by("not_exits")), List.of("a", "b", "c", "d"));
        });
    }

    @Test
    void emptyListTest(){
        Page<String> paged = paging.apply(Pageable.ofSize(3), List.of());
        Assertions.assertEquals(0, paged.getTotalPages());
        Assertions.assertEquals(0, paged.getTotalElements());
    }

    @Test
    void normalSortingTest(){
        Page<TestClass> paged = pagingTestClass.apply(PageRequest.of(0, 2, Sort.by("str")), testList);
        Assertions.assertEquals(2, paged.getTotalPages());
        Assertions.assertEquals(4, paged.getTotalElements());
        Assertions.assertEquals(2, paged.getSize());
        Assertions.assertEquals("a", paged.getContent().get(0).str);
        Assertions.assertEquals("b", paged.getContent().get(1).str);
    }

    @Test
    void reverseSortingTest(){
        Page<TestClass> paged = pagingTestClass.apply(PageRequest.of(0, 2, Sort.by("str").descending()), testList);
        Assertions.assertEquals("d", paged.getContent().get(0).str);
        Assertions.assertEquals("c", paged.getContent().get(1).str);
    }

    @Test
    void nullObjectsSortingTest(){
        List<TestClass> testListNulls = new ArrayList<>();
        testListNulls.add(new TestClass("a", 1));
        testListNulls.add(null);
        testListNulls.add(new TestClass("b", 2));
        testListNulls.add(null);
        testListNulls.add(null);
        Page<TestClass> paged = pagingTestClass.apply(PageRequest.of(0, 10, Sort.by("str").descending()), testListNulls);
        Assertions.assertEquals("b", paged.getContent().get(0).str);
        Assertions.assertEquals("a", paged.getContent().get(1).str);
        Assertions.assertNull(paged.getContent().get(2));
    }

    @Test
    void nullValuesSortingTest(){
        List<TestClass> testListNulls = new ArrayList<>();
        testListNulls.add(new TestClass("a", 1));
        testListNulls.add(new TestClass(null, 2));
        testListNulls.add(new TestClass("b", 3));
        testListNulls.add(new TestClass(null, 4));
        testListNulls.add(new TestClass(null, 5));
        Page<TestClass> paged = pagingTestClass.apply(PageRequest.of(0, 3, Sort.by("str").descending()), testListNulls);
        Assertions.assertEquals("b", paged.getContent().get(0).str);
        Assertions.assertEquals("a", paged.getContent().get(1).str);
        Assertions.assertNull(paged.getContent().get(2).str);
    }

    private static class TestClass{
        private final String str;
        private final int num;

        private TestClass(String str, int num) {
            this.str = str;
            this.num = num;
        }
    }



}