package org.base.utils.eventbus;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class EventBusExample {
	private EventBus bus= null;
	public class MessageEvent {

	    private String message;

	    public MessageEvent(String message) {
	        this.message = message;
	    }

	    public String getMessage() {
	        return message;
	    }

	    public void setMessage(String message) {
	        this.message = message;
	    }
	}
	public EventBusExample()
	{
		bus = new EventBus();
	}
	public void Register()
	{
		bus.register(this);
	}
	
	public void UnRegister()
	{
		bus.unregister(this);
	}
	
	public void Post()
	{
		MessageEvent event = new MessageEvent("test");
		bus.post(event);
	}
	
	@Subscribe
	public void lister(MessageEvent messageEvent) {
		System.out.println(messageEvent.getMessage());
	}
	public static void main(String[]args)
	{
		EventBusExample example = new EventBusExample();
		example.Register();
		example.Post();
		example.Post();
		example.UnRegister();
		
		final EventBus eventBus = new EventBus();
		eventBus.register(new Object() {

		    @Subscribe
		    public void lister(Integer integer) {
		        System.out.printf("%s from int%n", integer);
		    }

		    @Subscribe
		    public void lister(Number integer) {
		        System.out.printf("%s from Number%n", integer);
		    }

		    @Subscribe
		    public void lister(Long integer) {
		        System.out.printf("%s from long%n", integer);
		    }
		});

		eventBus.post(1);
		eventBus.post(1L);
	}
}
