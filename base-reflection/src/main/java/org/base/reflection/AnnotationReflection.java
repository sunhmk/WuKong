package org.base.reflection;

import java.lang.annotation.Annotation;

public class AnnotationReflection {
	@SuppressWarnings(":deprecation")
	public void test()
	{
		System.out.println("test");
	}
	
	public int get()
	{
		return 1;
	}
	
	public static void main(String[]args)
	{
		AnnotationReflection ref = new AnnotationReflection();
		Annotation[] anos = null;
		try {
			anos = ref.getClass().getMethod("test").getAnnotations();//getAnnotationsByType(SuppressWarnings.class);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(Annotation an:anos)
		{
			System.out.println(an.annotationType().getName());
		}
	}
}
