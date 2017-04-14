package org.base.annotation;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
//import java.lang.annotation.

public class TypeAnnotation {
	//对象类型转化时
    /*myString = (@NonNull String) str;
    //使用 implements 表达式时
    class MyList<T> implements @ReadOnly List<@ReadOnly T>{
                        ...
     }
     //使用 throws 表达式时
     public void validateValues() throws @Critical ValidationFailedException{
                        ...
      }*/
	@Target({ElementType.TYPE_PARAMETER,ElementType.TYPE_USE})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Color{
		
	}
	
	public class Test{
		 public @Color String type;
	}
	
	public void action(){
		//(new Test()).getClass().
	}
	
	@Retention( RetentionPolicy.RUNTIME )
	@Target( ElementType.TYPE_USE )
	@Repeatable( RepeatedValues.class )
	public @interface CanBeRepeated
	{
	 String value();
	}
	/**
	 * Container for the {@link CanBeRepeated} Annotation containing a list of values
	*/
	@Retention( RetentionPolicy.RUNTIME )
	@Target( ElementType.TYPE_USE )
	public @interface RepeatedValues
	{
	 CanBeRepeated[] value();
	}
	
	@CanBeRepeated( "the color is green" )
	@CanBeRepeated( "the color is red" )
	@CanBeRepeated( "the color is blue" )
	public class RepeatableAnnotated
	{

	}
	
	@Retention( RetentionPolicy.RUNTIME )
	@Target( ElementType.TYPE_USE )
	public @interface CannotBeRepeated
	{

	 String value();
	}

	@CannotBeRepeated( "info" )
	/*
	 * if we try repeat the annotation we will get an error: Duplicate annotation of non-repeatable type
	 *
	 * @CannotBeRepeated. Only annotation types marked
	 *
	 * @Repeatable can be used multiple times at one target.
	 */
    //@CannotBeRepeated( "more info" )
	public class RepeatableAnnotatedWrong
	{

	}
	
	/*@SuppressWarnings( "unused" )
	public static void main( String[] args )
	{
	 // type def
	 @TypeAnnotated
	 String cannotBeEmpty = null;

	 // type
	 List<@TypeAnnotated String> myList = new ArrayList<String>();

	 // values
	 String myString = new @TypeAnnotated String( "this is annotated in java 8" );

	}

	// in method params
	public void methodAnnotated( @TypeAnnotated int parameter )
	{
	 System.out.println( "do nothing" );
	}*/
	
	
}
