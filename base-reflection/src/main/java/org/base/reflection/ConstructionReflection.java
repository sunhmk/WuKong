package org.base.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ConstructionReflection {
	public static void main(String[]args)
	{
		int[] a = new int[]{65,66,67};
		String str = new String(a, 0, a.length);
		System.out.println(str);
		//String.class.getConstructor(parameterTypes)
		
		try {
			Constructor cs =  String.class.getConstructor(int[].class,int.class,int.class);
			try {
				str = (String) cs.newInstance(a,0,a.length);
				System.out.println(str);
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//List ArrayList
		//return all constructor ,regardless of public or private
		List lst = new ArrayList(2);
		try {
			Constructor<?> cons = Class.forName("java.util.ArrayList").getDeclaredConstructor(java.util.Collection.class);
			try {
				lst = (List) cons.newInstance(new ArrayList(3));
				lst.add(3);
				lst.forEach(new Consumer(){

					@Override
					public void accept(Object arg0) {
						// TODO Auto-generated method stub
						System.out.println(arg0.toString());
					}
				});
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//getConstructors only public
	}
}
