package org.base.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.Inherited;

public class AnnotationInherited {
	@Inherited
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface InheritedAnnotation {

	}

	@InheritedAnnotation
	public class AnnotatedSuperClass {

		public void oneMethod() {

		}
	}
	
	@InheritedAnnotation
	public interface AnnotatedInterface {

		public void oneMethod();

	}

	public class AnnotatedImplementedClass implements AnnotatedInterface {

		@Override
		public void oneMethod() {

		}

	}
	
	public static void main(String[] args) {
		System.out.println("is true: "
				+ AnnotatedSuperClass.class
						.isAnnotationPresent(InheritedAnnotation.class));
		// System.out.println( "is true: " +
		// AnnotatedSubClass.class.isAnnotationPresent(
		// InheritedAnnotation.class ) );
		//@Inheriated注解仅在存在继承关系的类上产生效果，在接口和实现类上并不工作。这条同样也适用在方法，变量，包等等。只有类才和这个注解连用。
		System.out.println( "is true: " + AnnotatedInterface.class.isAnnotationPresent( InheritedAnnotation.class ) );

		System.out.println( "is true: " + AnnotatedImplementedClass.class.isAnnotationPresent( InheritedAnnotation.class ) );
	}
}
