package org.base.reflection;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ParameterizedTypeExample {
	public static abstract class MySuperClass<T,V> {
	    public abstract void onSuccess(T data);
	}
	public static interface MyInterface<T,V> {
	    void onSuccess(T data);
	}
	public static class Student {
	}
	 public static void main(String[] args) {
	        classTest();
	        interfaceTest();
	    }
	    private static void classTest() {
	        MySuperClass<Student, String> mySuperClass = new MySuperClass<Student, String>() {
	            @Override
	            public void onSuccess(Student data) {
	            }
	        };
	        //getClass().getGenericSuperclass()返回表示此 Class 所表示的实体的直接超类的 Type
	        ParameterizedType type = (ParameterizedType) mySuperClass.getClass().getGenericSuperclass();
	        sysoType(type);
	    }
	    private static void interfaceTest() {
	        MyInterface<Student, String> myInterface = new MyInterface<Student, String>() {
	            @Override
	            public void onSuccess(Student data) {
	            }
	        };
	        ParameterizedType type = (ParameterizedType) myInterface.getClass().getGenericInterfaces()[0];
	        sysoType(type);
	    }
	    private static void sysoType(ParameterizedType type) {
	        /*com.bqt.generic.MySuperClass<com.bqt.generic.Student, java.lang.String>
	        class sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl
	        class com.bqt.generic.Student
	        class java.lang.Class
	        class java.lang.String
	        class java.lang.Class
	        
	        com.bqt.generic.MyInterface<com.bqt.generic.Student, java.lang.String>
	        class sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl
	        class com.bqt.generic.Student
	        class java.lang.Class
	        class java.lang.String
	        class java.lang.Class*/
	        System.out.println(type + "\n" + type.getClass());
	        //返回表示此类型实际类型参数的 Type 对象的数组，泛型的参数可能有多个，我们需要哪个就取哪个
	        Type[] targets = type.getActualTypeArguments();
	        for (int i = 0; i < targets.length; i++) {
	            System.out.println(targets[i] + "\n" + targets[i].getClass());
	        }
	    }
}
