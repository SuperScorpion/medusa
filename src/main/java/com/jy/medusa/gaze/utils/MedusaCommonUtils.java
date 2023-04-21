package com.jy.medusa.gaze.utils;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * Created by SuperScorpion on 2016/12/4.
 */
public class MedusaCommonUtils {

    /**
     * 实现StringBuilder的replaceAll
     * @param sbb    整个字符串
     * @param oldStr 被替换的字符串
     * @param newStr 替换oldStr
     * @return 结果
     */
    public static StringBuilder replaceAll(StringBuilder sbb, String oldStr, String newStr) {
        if (sbb == null || oldStr == null || newStr == null || sbb.length() == 0 || oldStr.length() == 0)
            return sbb;
        int index = sbb.indexOf(oldStr);
        if (index > -1 && !oldStr.equals(newStr)) {
            int lastIndex = 0;
            while (index > -1) {
                sbb.replace(index, index + oldStr.length(), newStr);
                lastIndex = index + newStr.length();
                index = sbb.indexOf(oldStr, lastIndex);
            }
        }
        return sbb;
    }

    /**
     * 实现String对象的新旧字符串替换
     * @param allCs 参数
     * @param oldCs 参数
     * @param newCs 参数
     * @return 结果
     */
    public static String replace(String allCs, String oldCs, String newCs) {
        if (isEmpty(allCs)) return allCs;
        if (!allCs.contains(oldCs)) return allCs;
        return allCs.replace(oldCs, newCs);
    }

    /**
     * copy code from org.apache.commons.lang3.StringUtils.isEmpty
     * @param cs 参数
     * @return 返回值
     */
    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    /**
     * copy code from org.apache.commons.lang3.StringUtils.isNotEmpty
     * @param cs 参数
     * @return 返回值
     */
    public static boolean isNotEmpty(CharSequence cs) {
        return !isEmpty(cs);
    }


    /**
     * copy code from org.apache.commons.lang3.StringUtils.isBlank
     * @param cs 参数
     * @return 返回值
     */
    public static Boolean isBlank(CharSequence cs) {

        int strLen;

        if(cs != null && (strLen = cs.length()) != 0) {

            for(int i = 0; i < strLen; ++i) {
                if(!Character.isWhitespace(cs.charAt(i))) return false;
            }

            return true;

        } else {

            return true;
        }
    }

    /**
     * copy code from org.apache.commons.lang3.StringUtils.isNotBlank
     * @param cs 参数
     * @return 返回值
     */
    public static Boolean isNotBlank(CharSequence cs) {
        return !isBlank(cs);
    }

    public static void writeString2File(File file, String content, String encoding) throws IOException {

        FileOutputStream out = null;

        try {
            out = new FileOutputStream(file);
            out.write(content.getBytes(Charset.forName(encoding)));

        }  finally {
            out.flush();
            out.close();
        }
    }

    /**
     * copy code from org.apache.commons.lang3.StringUtils.join(Iterator iterator, String separator)
     * @param iterable 参数
     * @param separator 参数
     * @return 返回值
     */
    public static String join(Iterable<?> iterable, String separator) {

        Iterator iterator = iterable.iterator();

        if(iterator == null) {
            return null;
        } else if(!iterator.hasNext()) {
            return "";
        } else {
            Object first = iterator.next();
            if(!iterator.hasNext()) {
                String buf1 = first == null ? "" : first.toString();
                return buf1;
            } else {
                StringBuilder buf = new StringBuilder(256);
                if(first != null) {
                    buf.append(first);
                }

                while(iterator.hasNext()) {
                    if(separator != null) {
                        buf.append(separator);
                    }

                    Object obj = iterator.next();
                    if(obj != null) {
                        buf.append(obj);
                    }
                }

                return buf.toString();
            }
        }
    }

    /**
     * copy code from org.apache.commons.lang3.ArrayUtils.addAll(T[] array1, T... array2)
     * @param array1 参数
     * @param array2 参数
     * @param <T> 泛型
     * @return 返回值
     */
    public static <T> T[] addArrayAll(T[] array1, T... array2) {
        if(array1 == null) {
            return array2.clone();
        } else if(array2 == null) {
            return array1.clone();
        } else {
            Class type1 = array1.getClass().getComponentType();
            T[] joinedArray = (T[])(Array.newInstance(type1, array1.length + array2.length));
            System.arraycopy(array1, 0, joinedArray, 0, array1.length);

            try {
                System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
                return joinedArray;
            } catch (ArrayStoreException e) {
                Class type2 = array2.getClass().getComponentType();
                if(!type1.isAssignableFrom(type2)) {
                    throw new IllegalArgumentException("Cannot store " + type2.getName() + " in an array of " + type1.getName(), e);
                } else {
                    throw e;
                }
            }
        }
    }



    /**
     * 为测试性能方面 从类中取出属性值来 实验证明出jdk1.8 反射的速度最快
     * @param args 参数
     */
    //public static void main(String[] args) throws OgnlException, NoSuchFieldException, IllegalAccessException {


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

//}

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

        public Object clone() {
            Object obj = null ;
            try{
                obj = super.clone();
            } catch(Exception e) {
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
