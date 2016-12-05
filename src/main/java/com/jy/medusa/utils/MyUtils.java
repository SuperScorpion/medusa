package com.jy.medusa.utils;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * Created by neo on 2016/12/4.
 */
public class MyUtils {

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
}
