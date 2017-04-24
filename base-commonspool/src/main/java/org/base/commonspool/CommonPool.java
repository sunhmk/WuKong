package org.base.commonspool;

import java.lang.reflect.Constructor;

import org.apache.commons.pool.BaseKeyedPoolableObjectFactory;
import org.apache.commons.pool.KeyedObjectPool;
import org.apache.commons.pool.KeyedObjectPoolFactory;
import org.apache.commons.pool.KeyedPoolableObjectFactory;
import org.apache.commons.pool.impl.StackKeyedObjectPoolFactory;

public class CommonPool {

	public class Person {  
		  
	    String id;  
	    String name;  
	  
	    public Person() {  
	  
	    }  
	  
	    public Person(String id, String name) {  
	        this.id = id;  
	        this.name = name;  
	    }  
	  
	    public String getId() {  
	        return id;  
	    }  
	  
	    public void setId(String id) {  
	        this.id = id;  
	    }  
	  
	    public String getName() {  
	        return name;  
	    }  
	  
	    public void setName(String name) {  
	        this.name = name;  
	    }  
	  
	    @Override  
	    public String toString() {  
	        return "id:" + id + "---name:" + name;  
	    }  
	  
	} 
	
	class KeyedPoolableObjectFactorySample extends BaseKeyedPoolableObjectFactory {  
	      
	    /** 
	     * 创建对象方法 
	     */  
	    @SuppressWarnings("unchecked")  
	    public Object makeObject(Object clsName) throws Exception {  
	  
	        if (clsName == null || !(clsName instanceof String)  
	                || "".equals(clsName)) {  
	            throw new RuntimeException("类名为空！");  
	        }  
	  
	        System.out.println("创建一个新的对象:" + clsName);  
	  
	        Class<?>[]innerClas= CommonPool.this.getClass().getDeclaredClasses();
	        Object obj =null;

	        for(Class<?>cls:innerClas)
	        {
	        	if(cls.getName().compareTo(clsName.toString()) == 0)
	        	{
	        		Constructor con = cls.getDeclaredConstructor(CommonPool.this.getClass());
	        		con.setAccessible(true);
	        		obj = con.newInstance(CommonPool.this);
	        		break;
	        	}
	        }
	        /*inner class vice outer class instance
	         * static inner class directly
	         * @SuppressWarnings("rawtypes")
			Class cls = Person.class;//Class.forName((String) clsName);  
	        Constructor constructor =  cls.getDeclaredConstructor();
	        constructor.setAccessible(true);
	       // Object obj = constructor.newInstance(); //cls.newInstance();  
	        Object obj = cls.newInstance();*/
	        return obj;  
	    }  
	  
	    @Override  
	    public void activateObject(Object key, Object obj) throws Exception {  
	        // TODO Auto-generated method stub  
	        super.activateObject(key, obj);  
	        System.out.println("激活对象");  
	    }  
	  
	    @Override  
	    public void destroyObject(Object key, Object obj) throws Exception {  
	        // TODO Auto-generated method stub  
	        super.destroyObject(key, obj);  
	        System.out.println("销毁对象");  
	    }  
	  
	    @Override  
	    public void passivateObject(Object key, Object obj) throws Exception {  
	        // TODO Auto-generated method stub  
	        super.passivateObject(key, obj);  
	        System.out.println("挂起对象");  
	    }  
	  
	    @Override  
	    public boolean validateObject(Object key, Object obj) {  
	        // TODO Auto-generated method stub  
	        System.out.println("验证对象");  
	        return super.validateObject(key, obj);  
	  
	    }  
	  
	}  
	
	@SuppressWarnings("unchecked")
	public void Test()
	{

        Object obj = null;  
        KeyedPoolableObjectFactory factory = new KeyedPoolableObjectFactorySample();  
  
        KeyedObjectPoolFactory poolFactory = new StackKeyedObjectPoolFactory(  
                factory);  
  
        KeyedObjectPool pool = poolFactory.createPool();  
  
        String key = null;  
  
        try {  
  
            /*key = "java.lang.String";  
  
            obj = pool.borrowObject(key);  
            obj = "obj1";  
            // pool.returnObject(key, obj);  
            obj = pool.borrowObject(key);  
            pool.returnObject(key, obj);  
            obj = pool.borrowObject(key);  
            System.out.println(obj);  
  
            System.out.println("============看另一个对象Person=============");  */
  
            key = "org.base.commonspool.CommonPool$Person";  
  
            Person person1 = (Person) pool.borrowObject(key);  
              
            person1.setId("1");  
            person1.setName("素还真");  
            System.out.println(person1);  
            pool.returnObject(key, person1);  
            System.out.println(person1);  
  
            Person person2 = (Person) pool.borrowObject(key);  
            person2.setId("2");  
            person2.setName("青阳子");  
  
            Person person3 = (Person) pool.borrowObject(key);  
            person3.setId("3");  
            person3.setName("一页书");  
  
            Person person4 = (Person) pool.borrowObject(key);  
            person4.setId("4");  
            person4.setName("业途灵");  
  
            System.out.println(person1);  
            System.out.println(person2);  
            System.out.println(person3);  
            System.out.println(person4);  
              
            pool.returnObject(key, person3);  
            Person person5 = (Person) pool.borrowObject(key);  
            System.out.println(person5);  
              
  
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            try {  
                pool.close();  
                System.out.println(pool);  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
        }  
	}
	/** 
     * @param args 
     */  
    public static void main(String[] args) {  
    	CommonPool pool = new CommonPool();
    	@SuppressWarnings("unused")
		Class<?>[]csz = pool.getClass().getClasses();
    	pool.Test();
    }  
}
