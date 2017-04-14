package org.base.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

public class AnnotationInit {
	public class ModuleEntry {
	    int id;
	    String name;
	    String des;
	    public ModuleEntry(int id, String name, String des) {
	        this.id = id;
	        this.name = name;
	        this.des = des;
	    }
	}
	@Target({ElementType.FIELD})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Module {
	    int id();
	     //String value();//注意注解中名为value的元素，如果应用该注解时，value元素是唯一需要赋值的元素，那么只需在括号内给出value元素所需的值即可
	    String moduleName() default "";
	    String moduleDes() default "";
	}
	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface ModuleSet {
	    String value();//其实这里的value没有什么意义，是为了表示一下value元素的特殊，这点稍后会看到
	}
	class Activity{}
	@ModuleSet("Main")
	class MainActivity extends Activity{
	    @Module(id = 0,
	            moduleName = "模块管理",
	            moduleDes = "对模块进行管理")
	    ModuleEntry mManagerModule;

	    @Module(id = 1,
	            moduleName = "校园网",
	            moduleDes = "校园网管理")
	    ModuleEntry mSeunetModule;

	    ModuleEntry mOtherModule;
	  
	    public MainActivity(){
	        configureModule(this);
	    }
	}
	public void configureModule(Activity activity){
        //输出传入对象类型
        System.out.println(activity.getClass());
        //查看其是否添加了ModuleSet注解并获取注解
        System.out.println(activity.getClass().isAnnotationPresent(ModuleSet.class));
        ModuleSet moduleSetAnnotation = activity.getClass().getAnnotation(ModuleSet.class);
        //必须添加了该注解的类才能使用这个方法，否则不做任何操作
        if (moduleSetAnnotation == null)return;
        
        //遍历所有的域
        for (Field field : activity.getClass().getDeclaredFields()){
        	Boolean b = field.isAnnotationPresent(Module.class);
            Module module = field.getAnnotation(Module.class);

            //如果没有模块注解，或者其类型不是模块实体，则跳过
            if(module == null || field.getType() != ModuleEntry.class)continue;
            //对所有的满足条件的Field，输出模块对应的名字和描述
            System.out.println(module.moduleName() + " " + module.moduleDes());
            System.out.println(field.getName());
            
            //生成模块条目
            ModuleEntry moduleEntry = new ModuleEntry(module.id(),module.moduleName(),module.moduleDes());
            try {
                field.setAccessible(true);
                //使用set函数可以为当前的field实际代表的对象进行赋值,如果是static对象则可以把第一个参数置为null
                field.set(activity,moduleEntry);
                //使用get函数可以获得这个对象
                //ModuleEntry moduleEntry0 = (ModuleEntry) field.get(activity);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }    
	public static void main(String[] args){
		AnnotationInit init = new AnnotationInit();
        MainActivity mainActivity = init.new MainActivity();
        System.out.println(mainActivity.mManagerModule.des);
        //NullPointerException，因为mOtherModule没有添加注解，不会被自动初始化
        //System.out.println(mainActivity.mOtherModule.des);
    }
}
