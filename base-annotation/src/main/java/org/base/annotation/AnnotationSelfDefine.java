package org.base.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

public class AnnotationSelfDefine {
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface CustomAnnotationMethod {

		public String author() default "danibuiza";

		public String date();

		public String description();

	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface CustomAnnotationClass {

		public String author() default "danibuiza";

		public String date();

	}

	@CustomAnnotationClass(date = "2014-05-05")
	public class AnnotatedClass {
	}

	@CustomAnnotationMethod(date = "2014-06-05", description = "annotated method")
	public String annotatedMethod() {
		return "nothing niente";
	}

	@CustomAnnotationMethod(author = "friend of mine", date = "2014-06-05", description = "annotated method")
	public String annotatedMethodFromAFriend() {
		return "nothing niente";
	}

	public static void main(String[] args) throws Exception {

		Class<AnnotatedClass> object = AnnotatedClass.class;
		// Retrieve all annotations from the class
		Annotation[] annotations = object.getAnnotations();
		for (Annotation annotation : annotations) {
			System.out.println(annotation);
		}

		// Checks if an annotation is present
		if (object.isAnnotationPresent(CustomAnnotationClass.class)) {

			// Gets the desired annotation
			Annotation annotation = object.getAnnotation(CustomAnnotationClass.class);

			System.out.println(annotation);

		}
		// the same for all methods of the class
		for (Method method : object.getDeclaredMethods()) {

			if (method.isAnnotationPresent(CustomAnnotationMethod.class)) {

				Annotation annotation = method.getAnnotation(CustomAnnotationMethod.class);

				System.out.println(annotation);

			}

		}
	}
}
