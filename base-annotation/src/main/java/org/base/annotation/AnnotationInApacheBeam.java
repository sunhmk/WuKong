package org.base.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;

public class AnnotationInApacheBeam {
	@Target({ElementType.TYPE,ElementType.FIELD})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface AvroName {
		String value() default "avro";
	}

	@AvroName
	public class MyAvro {
		
	}

	public class MyAvro2{
		@AvroName
		public String value;
	}
	/*
	 * source from hadoop AvroCoder.java and AvroName is redefined.
	 */
	private static Field getField(Class<?> originalClazz, String name) {
		Class<?> clazz = originalClazz;
		while (clazz != null) {
			for (Field field : clazz.getDeclaredFields()) {
				// org.apache.avro.reflect.AvroName
				AvroName avroName = field.getAnnotation(AvroName.class);
				if (avroName != null && name.equals(avroName.value())) {
					return field;
				} else if (avroName == null && name.equals(field.getName())) {
					return field;
				}
			}
			clazz = clazz.getSuperclass();
		}

		throw new IllegalArgumentException("Unable to get field " + name
				+ " from " + originalClazz);
	}

	private static Optional<String> getDefaultValueFromAnnotation(Method method) {
		for (Annotation annotation : method.getAnnotations()) {
			/*
			 * if (annotation instanceof Default.Class) { return
			 * Optional.of(((Default.Class)
			 * annotation).value().getSimpleName()); } else if (annotation
			 * instanceof Default.String) { return Optional.of(((Default.String)
			 * annotation).value()); } else if (annotation instanceof
			 * Default.Boolean) { return
			 * Optional.of(Boolean.toString(((Default.Boolean)
			 * annotation).value())); } else if (annotation instanceof
			 * Default.Character) { return
			 * Optional.of(Character.toString(((Default.Character)
			 * annotation).value())); } else if (annotation instanceof
			 * Default.Byte) { return Optional.of(Byte.toString(((Default.Byte)
			 * annotation).value())); } else if (annotation instanceof
			 * Default.Short) { return
			 * Optional.of(Short.toString(((Default.Short)
			 * annotation).value())); } else if (annotation instanceof
			 * Default.Integer) { return
			 * Optional.of(Integer.toString(((Default.Integer)
			 * annotation).value())); } else if (annotation instanceof
			 * Default.Long) { return Optional.of(Long.toString(((Default.Long)
			 * annotation).value())); } else if (annotation instanceof
			 * Default.Float) { return
			 * Optional.of(Float.toString(((Default.Float)
			 * annotation).value())); } else if (annotation instanceof
			 * Default.Double) { return
			 * Optional.of(Double.toString(((Default.Double)
			 * annotation).value())); } else if (annotation instanceof
			 * Default.Enum) { return Optional.of(((Default.Enum)
			 * annotation).value()); } else if (annotation instanceof
			 * Default.InstanceFactory) { return
			 * Optional.of(((Default.InstanceFactory)
			 * annotation).value().getSimpleName()); }
			 */
		}
		return Optional.empty();
	}
	public static void main(String[]args)
	{
		getField(MyAvro2.class,"value");
	}
}
