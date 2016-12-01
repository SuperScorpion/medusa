package com.jy.medusa.utils;

/**
 * Created by neo on 2016/11/11.
 */

import org.apache.ibatis.ognl.OgnlException;

import java.lang.reflect.Method;

public class OgnlAccess {

    /**
     * 为测试性能方面 从类中取出属性值来 实验证明出jdk1.8 反射的速度最快
     * @param args
     */
    public static void main(String[] args) throws OgnlException, NoSuchFieldException, IllegalAccessException {


/*测试了 ognl 反射和 asm 的性能*/

        /*Vm nnn = new Vm();

        nnn.setBbb("ffdsafdsafsd");

//        long d = System.nanoTime();

        long h = System.currentTimeMillis();

        int i = 0;

        while (i < 100000) {
            System.out.println(Ognl.getValue("bbb", nnn));
            i++;
        }

//        System.out.println(System.nanoTime() - d);

        System.out.println(System.currentTimeMillis() - h);
        System.out.println("qwe");*/


        // 953522256
        //3580 3615

/*
        Vm mmm = new Vm();

        mmm.setBbb("ffdsafdsafsd");

//        long f = System.nanoTime();

        long p = System.currentTimeMillis();

        int j = 0;

        while (j < 100000) {

            Field u = mmm.getClass().getDeclaredField("bbb");

            u.setAccessible(true);

            System.out.println(u.get(mmm).toString());

            j++;
        }
//        System.out.println(System.nanoTime() - f);

        System.out.println(System.currentTimeMillis() - p);
        System.out.println("qwe");*/

        //85881321 708 569




       /* Vm mmm = new Vm();

        mmm.setBbb("ffdsafdsafsd");

//        long f = System.nanoTime();

        long p = System.currentTimeMillis();

        int j = 0;

        while (j < 100000) {

            MethodAccess access = MethodAccess.get(Vm.class);
            System.out.println((String) access.invoke(mmm, "getBbb"));

            j++;
        }
//        System.out.println(System.nanoTime() - f);

        System.out.println(System.currentTimeMillis() - p);
        System.out.println("qwe");*/
//1153 1059

//    }




        /*测试乐性能 拿到所有的方法 和 单独的方法的呢 的性能*/

        /*long f = System.nanoTime();
        Method[] paramMethods = Vm.class.getDeclaredMethods();
        System.out.println(System.nanoTime() - f);

        try {
            long g = System.nanoTime();
            Method paramMethod = Vm.class.getDeclaredMethod("getBbb");
            System.out.println(System.nanoTime() - g);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }*/






        /*测试了new 和 clone 的性能*/

/*        long g = System.nanoTime();

        int i = 0;

        while (i < 1000000) {
            Vm gj = new Vm();
            System.out.println(i);
            i++;
        }

        System.out.println(System.nanoTime() - g);//3732106785 3571740396*/



/*        Vm2 gjr = new Vm2();

        long g = System.nanoTime();

        int i = 1;

        while (i < 1000000) {
            Vm2 gj = (Vm2) gjr.clone();
            System.out.println(i);
            i++;
        }

        System.out.println(System.nanoTime() - g);//3288476262 3622226493*/

    }

   /*static class Vm {

        private String bbb;
        private String mmm;

        public String getBbb() {
            return bbb;
        }

        public void setBbb(String bbb) {
            this.bbb = bbb;
        }

        public String getMmm() {
            return mmm;
        }

        public void setMmm(String mmm) {
            this.mmm = mmm;
        }
    }

    static class Vm2 implements Cloneable{

        public Object clone(){
            Object obj = null ;
            try{
                obj = super.clone();
            }catch(Exception e){
            }
            return obj;
        }

        private String bbb;
        private String mmm;

        public String getBbb() {
            return bbb;
        }

        public void setBbb(String bbb) {
            this.bbb = bbb;
        }

        public String getMmm() {
            return mmm;
        }

        public void setMmm(String mmm) {
            this.mmm = mmm;
        }
    }*/
}