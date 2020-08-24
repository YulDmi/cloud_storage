package lesson7;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MyClassTest {
    private MyClass mc;

    @Before
    public void init() {
        mc = new MyClass();
    }
    @Test
    public void change1() {
        int[] a = {1, 2, 4, 3, 2};
        int[] b = {3, 2};
        Assert.assertArrayEquals(b, mc.change(a));
    }
    @Test
    public void change2() {
        int[] a = {4, 2, 1, 3, 2};
        int[] b = {2, 1, 3, 2};
        Assert.assertArrayEquals(b, mc.change(a));
    }
    @Test
    public void change3() {
        int[] a = {1, 2, 4};
        int[] b = {};
        Assert.assertArrayEquals(b, mc.change(a));
    }
    @Test(expected = RuntimeException.class)
    public void change4() {
        int[] a = {1, 2, 3, 2};
        mc.change(a);
    }

    @Test
    public void check1() {
        int[] a = {4, 1, 4, 1};
        boolean actual = mc.check(a);
        Assert.assertTrue(actual);
    }
    @Test
    public void check2() {
        int[] a = {4, 4, 4};
        boolean actual = mc.check(a);
        Assert.assertFalse(actual);
    }
    @Test
    public void check3() {
        int[] a = {1, 1, 1, 1};
        boolean actual = mc.check(a);
        Assert.assertFalse(actual);
    }

    @Test
    public void check4() {
        int[] a = {4, 1, 4, 3, 1};
        boolean actual = mc.check(a);
        Assert.assertFalse(actual);
    }
}