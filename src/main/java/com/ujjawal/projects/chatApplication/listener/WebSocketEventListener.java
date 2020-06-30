package com.ujjawal.projects.chatApplication.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.ujjawal.projects.chatApplication.model.ChatMessage;

@Component
public class WebSocketEventListener {
	
	@Autowired
	private SimpMessageSendingOperations messagingTemplate;
	
	@EventListener
	public void handleWebSocketConnectListener(SessionConnectedEvent event)
	{
		System.out.println("Recieved a new web socket connection request");
	}
	
	@EventListener
	public void handleWebSocketDisconnectListener(SessionDisconnectEvent event)
	{
		StompHeaderAccessor headerAccessor=StompHeaderAccessor.wrap(event.getMessage());
		String username=(String) headerAccessor.getSessionAttributes().get("username");
		if(username!=null)
		{
			System.out.println("User Disconnected:"+username);
			
			ChatMessage chatMessage=new ChatMessage();
			chatMessage.setType(ChatMessage.MessageType.LEAVE);
			chatMessage.setSender(username);
			messagingTemplate.convertAndSend("/topic/publicChatRoom", chatMessage);
		}
	}

}
