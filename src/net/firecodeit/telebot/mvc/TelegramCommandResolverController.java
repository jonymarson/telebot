package net.firecodeit.telebot.mvc;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.FormBodyPartBuilder;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.json.JsonObject;
import com.restfb.types.Album;
import com.sun.syndication.io.FeedException;

import net.firecodeit.telebot.model.Update;
import net.firecodeit.telebot.proccess.Feeds;
import net.firecodeit.telebot.proccess.FeedsAlternative;

@RestController
public class TelegramCommandResolverController {
	
	@RequestMapping(value="tdpbot")
    public @ResponseBody String tdpBotCommandResolver(HttpEntity<String> tdpbot) throws IllegalArgumentException, MalformedURLException, FeedException, IOException {
		
		HashMap<String, String> mapAlbums = new HashMap<String, String>();
		
		Map<String, String> params = new HashMap<String, String>();

		Gson gson = new Gson();

		Update update = gson.fromJson(tdpbot.getBody(), Update.class);

		String message = "";

		String keyboard = null;

		Long reply_to_message_id = null;


		if(update.message.text.equals("/trilhas")) {
			
			params.put("url", "http://turmadopedal.groupsite.com/rss/events");
			
			Runnable process = new Feeds(update, params);
			
			process.run();
			
			return "OK";
			
		} else if(update.message.text.equals("/atividades")){
			
			params.put("url", "http://turmadopedal.groupsite.com/rss/log");
			
			Runnable process = new FeedsAlternative(update, params);
			
			process.run();
			
			return "OK";
			
		} else if(update.message.text.equals("/fotos")) {
			
			try {
			
				FacebookClient facebookClient = new DefaultFacebookClient("1682976801921971|uKdVvLRnXlhb2_iL9E_WuLl7R6Y");
				
				Connection<Album> albums = facebookClient.fetchConnection("tdpturmadopedal/albums", Album.class);
				
				keyboard = "{\"keyboard\":[";
				
				for (Album album : albums.getData()) {
					keyboard = keyboard + "[\"/album " + album.getName() + "\"],";
					mapAlbums.put(album.getName(), album.getId());
			 	}
				
				keyboard = StringUtils.removeEnd(keyboard, ",");
				
				keyboard = keyboard + "],\"one_time_keyboard\":true,\"resize_keyboard\":true}";
				
				reply_to_message_id = update.message.message_id;
				
				message = "Escolha um Album";
			}catch(Exception e) {
				message = "Ocorreu um problema. Sorry.";
				System.out.println(e);
			}
			
		} else if(update.message.text.startsWith("/album ")) {
			
			try {
				
				FacebookClient facebookClient = new DefaultFacebookClient("1682976801921971|uKdVvLRnXlhb2_iL9E_WuLl7R6Y");
				
				String  albumID = mapAlbums.get(update.message.text.split("/album ")[1]);
				
				JsonObject photosConnection = facebookClient.fetchObject(albumID+"/photos", JsonObject.class, Parameter.with("fields", "source"));
				
				reply_to_message_id = update.message.message_id;
				
				
				for(int cont = 0; cont<photosConnection.getJsonArray("data").length(); cont++) {
					
					Content content = Request.Post("https://api.telegram.org/bot115447632:AAGiH7bX_7dpywsXWONvsJPESQe-N7EmcQI/sendChatAction")
							.bodyForm(Form.form().add("chat_id", String.valueOf(update.message.chat.id)).add("action",  "upload_photo").build())
						    .execute().returnContent();
					
					JsonObject photo = (JsonObject)photosConnection.getJsonArray("data").get(cont);
					
					CloseableHttpClient httpclient = HttpClients.createMinimal();
			
					HttpPost httpPost = new HttpPost("https://api.telegram.org/bot115447632:AAGiH7bX_7dpywsXWONvsJPESQe-N7EmcQI/sendPhoto");
					httpPost.addHeader("Content-type", "multipart/form-data; boundary=_______telebot_______");
					
					File temp = File.createTempFile(photo.getString("id"), "jpg");
					
					FileUtils.copyURLToFile(new URL(photo.getString("source")), temp);
					
					ContentType ct = ContentType.create("image/jpeg");
					
					ContentType ct2 = ContentType.create("text/plain", "UTF-8");
					
					FileBody bin = new FileBody(temp, ct);
					
		            StringBody chatIdBody = new StringBody(String.valueOf(update.message.chat.id), ct2);
		            
		            StringBody replyIdBody = new StringBody(String.valueOf(reply_to_message_id), ct2);
		            
		            StringBody captionBody = new StringBody(String.valueOf("Foto"), ct2);
		            

		            FormBodyPart chatid = FormBodyPartBuilder.create()
		            		.setName("chat_id")
		            		.setBody(chatIdBody)
		            		.build();
		            
		            FormBodyPart replyid = FormBodyPartBuilder.create()
		            		.setName("reply_to_message_id")
		            		.setBody(replyIdBody)
		            		.build();
		            
		            FormBodyPart caption = FormBodyPartBuilder.create()
		            		.setName("caption")
		            		.setBody(captionBody)
		            		.build();
		            
		            FormBodyPart partBin = FormBodyPartBuilder.create()
		            		.setName("photo")
		            		.setBody(bin)
		            		.build();
		            		
		            
					
		            org.apache.http.HttpEntity reqEntity = MultipartEntityBuilder.create()
		            		.addPart(chatid)
		            		.addPart(replyid)
		                  	.addPart(partBin)
		                  	.addPart(caption)
		                  	.setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
		                  	.setBoundary("_______telebot_______")
		                    .build();

		  
		            httpPost.setEntity(reqEntity);
		            
					CloseableHttpResponse response2 = httpclient.execute(httpPost);
					
					response2.close();
			
				}
			
				return new String();
				
			}catch(Exception e) {
				message = "Ocorreu um problema. Sorry.";
				System.out.println(e);
			}
			
		}else {

			message = "Desculpe, nao reconheco este comando.";
		}

		CloseableHttpClient httpclient = HttpClients.createDefault();

		HttpPost httpPost = new HttpPost("https://api.telegram.org/bot115447632:AAGiH7bX_7dpywsXWONvsJPESQe-N7EmcQI/sendMessage");
		httpPost.addHeader("Content-type", "application/x-www-form-urlencoded");
		httpPost.addHeader("charset", "UTF-8");
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("chat_id", String.valueOf(update.message.chat.id)));
		urlParameters.add(new BasicNameValuePair("text", message));
		if(keyboard!=null)
			urlParameters.add(new BasicNameValuePair("reply_markup", keyboard));
		if(reply_to_message_id!=null)
			urlParameters.add(new BasicNameValuePair("reply_to_message_id", reply_to_message_id.toString()));

		httpPost.setEntity(new UrlEncodedFormEntity(urlParameters, "UTF-8"));
		CloseableHttpResponse response2 = httpclient.execute(httpPost);

		response2.close();
		
		return new String();


	}
}
