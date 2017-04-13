package org.base.io.classloader;

import java.util.ServiceLoader;

public class MessageConsumer {
	public static void main(String[] args) {  
        ServiceLoader<MessageService> serviceLoader =   
                ServiceLoader.load(MessageService.class);  
        for(MessageService service : serviceLoader) {  
            System.out.println(service.getMessage());  
        }  
    } 
}
