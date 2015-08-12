package net.firecodeit.telebot.proccess;

import java.util.Map;

import net.firecodeit.telebot.model.Update;

public abstract class TelegramProccess implements Runnable {

	
	Map<String, String> parametros;
	Update update;
	
	public TelegramProccess(Update update, Map<String, String> parametros) {
		this.parametros = parametros;
		this.update = update;
	}

	

}
