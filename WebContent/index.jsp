<%@page import="org.apache.http.client.fluent.Form"%>
<%@page import="org.apache.http.client.fluent.Content"%>
<%@page import="org.apache.http.entity.mime.FormBodyPartBuilder"%>
<%@page import="org.apache.http.entity.mime.FormBodyPart"%>
<%@page import="org.apache.http.entity.mime.HttpMultipartMode"%>
<%@page import="java.io.FileInputStream"%>
<%@page import="org.apache.http.client.entity.InputStreamFactory"%>
<%@page import="org.apache.http.entity.mime.content.InputStreamBody"%>
<%@page import="org.apache.http.entity.ContentType"%>
<%@page import="org.apache.http.params.HttpParams"%>
<%@page import="org.apache.commons.io.FileUtils"%>
<%@page import="org.apache.http.util.CharsetUtils"%>
<%@page import="java.nio.charset.Charset"%>
<%@page import="java.net.URI"%>
<%@page import="java.io.File"%>
<%@page import="org.apache.http.client.fluent.Request"%>
<%@page import="org.apache.http.entity.mime.MultipartEntityBuilder"%>
<%@page import="org.apache.http.entity.mime.content.StringBody"%>
<%@page import="org.apache.http.entity.mime.content.FileBody"%>
<%@page import="com.restfb.Parameter"%>
<%@page import="com.restfb.FacebookClient.AccessToken"%>
<%@page import="com.restfb.Version"%>
<%@page import="facebook4j.FacebookFactory"%>
<%@page import="facebook4j.Facebook"%>
<%@page import="com.restfb.json.JsonObject"%>
<%@page import="java.util.HashMap"%>
<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@page import="com.restfb.types.Album"%>
<%@page import="com.restfb.Connection"%>
<%@page import="com.restfb.types.Page"%>
<%@page import="com.restfb.DefaultFacebookClient"%>
<%@page import="com.restfb.FacebookClient"%>
<%@page import="java.util.Date"%>
<%@page import="org.apache.commons.lang3.time.DateUtils"%>
<%@page import="org.apache.http.message.BasicNameValuePair"%>
<%@page import="java.util.ArrayList"%>
<%@page import="org.apache.http.NameValuePair"%>
<%@page import="org.apache.http.client.methods.HttpPost"%>
<%@page import="org.apache.http.client.entity.UrlEncodedFormEntity"%>
<%@page import="net.htmlparser.jericho.Source"%>
<%@page import="java.util.List"%>
<%@page import="com.sun.syndication.feed.synd.SyndEntry"%>
<%@page import="org.apache.jasper.tagplugins.jstl.core.ForEach"%>
<%@page import="java.net.URL"%>
<%@page import="com.sun.syndication.io.XmlReader"%>
<%@page import="com.sun.syndication.feed.synd.SyndFeed"%>
<%@page import="com.sun.syndication.io.SyndFeedInput"%>
<%@page import="net.firecodeit.telebot.model.Update"%>
<%@page import="com.google.gson.Gson"%>
<%@page import="org.apache.http.client.ClientProtocolException"%>
<%@page import="org.apache.http.HttpResponse"%>
<%@page import="org.apache.http.client.ResponseHandler"%>
<%@page import="org.apache.http.util.EntityUtils"%>
<%@page import="org.apache.http.HttpEntity"%>
<%@page import="org.apache.http.client.methods.CloseableHttpResponse"%>
<%@page import="org.apache.http.client.methods.HttpGet"%>
<%@page import="org.apache.http.impl.client.HttpClients"%>
<%@page import="org.apache.http.impl.client.CloseableHttpClient"%>
<%@page import="java.io.IOException"%>
<%@page import="java.io.InputStreamReader"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="java.io.InputStream"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%!HashMap<String, String> mapAlbums = new HashMap<String, String>(); %>
<%

String body = null;
StringBuilder stringBuilder = new StringBuilder();
BufferedReader bufferedReader = null;


try {
    InputStream inputStream = request.getInputStream();
    if (inputStream != null) {
        bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        char[] charBuffer = new char[128];
        int bytesRead = -1;
        while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
            stringBuilder.append(charBuffer, 0, bytesRead);
        }
    } else {
        stringBuilder.append("");
    }
} catch (IOException ex) {
    throw ex;
} finally {
    if (bufferedReader != null) {
        try {
            bufferedReader.close();
        } catch (IOException ex) {
            throw ex;
        }
    }
}

Gson gson = new Gson();

Update update = gson.fromJson(stringBuilder.toString(), Update.class);

String message = "";

String keyboard = null;

Long reply_to_message_id = null;

SyndFeedInput input = new SyndFeedInput();

if(update.message.text.equals("/trilhas")) {
	
	SyndFeed feed = input.build(new XmlReader(new URL("http://turmadopedal.groupsite.com/rss/events")));
	
	message = feed.getTitle() + "\n";
	
	for(SyndEntry entry: (List<SyndEntry>)feed.getEntries() ) {
		
		message = message + "\n>>>>>>>>>>>>>>>>>>>>>>>>\n" + entry.getTitle() + "\n";
		
		message = message + "\nResponsavel: " +entry.getAuthor() + "\n";
		
		Source source = new Source(entry.getDescription().getValue());
		
		message = message + "\n" + source.getRenderer().toString() + "\n";
	
	}
} else if(update.message.text.equals("/atividades")){
	
	SyndFeed feed = input.build(new XmlReader(new URL("http://turmadopedal.groupsite.com/rss/log")));
	
	message = feed.getTitle() + "\n\n";
	
	for(SyndEntry entry: (List<SyndEntry>)feed.getEntries() ) {
		
		if(entry.getPublishedDate().after(DateUtils.addDays(new Date(), -20))) {
		
			Source source = new Source(entry.getDescription().getValue());
			
			message = message + ">>>>>>>>>>>>>>>>>>>>>>>>\n" + source.getRenderer().toString() + "\n";
			
		}
	
	}
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
		
		response.flushBuffer();
		
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
            		
            
			
            HttpEntity reqEntity = MultipartEntityBuilder.create()
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
	
		return;
		
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

%>
