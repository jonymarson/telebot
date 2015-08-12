package net.firecodeit.telebot.proccess;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import net.firecodeit.telebot.model.Update;

public abstract class TelegramProccess implements Runnable {

	
	Map<String, String> parametros;
	Update update;
	String mensagem;
	String teclado;
	String mensagemRetornoID;
	
	public TelegramProccess(Update update, Map<String, String> parametros) {
		this.parametros = parametros;
		this.update = update;
	}

	abstract void proccess();
	
	
	public final void run() {
		proccess();
		
		CloseableHttpClient httpclient = HttpClients.createDefault();

		HttpPost httpPost = new HttpPost("https://api.telegram.org/bot115447632:AAGiH7bX_7dpywsXWONvsJPESQe-N7EmcQI/sendMessage");
		httpPost.addHeader("Content-type", "application/x-www-form-urlencoded");
		httpPost.addHeader("charset", "UTF-8");
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("chat_id", String.valueOf(update.message.chat.id)));
		urlParameters.add(new BasicNameValuePair("text", mensagem));
		if(teclado!=null)
			urlParameters.add(new BasicNameValuePair("reply_markup", teclado));
		if(mensagemRetornoID!=null)
			urlParameters.add(new BasicNameValuePair("reply_to_message_id", mensagemRetornoID.toString()));

		try {
			httpPost.setEntity(new UrlEncodedFormEntity(urlParameters, "UTF-8"));
			httpclient.execute(httpPost);
		} catch (IOException e) {
			e.printStackTrace();
		}
		

		
	}
	
	
	
	

}
