package net.firecodeit.telebot.proccess;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

import net.firecodeit.telebot.model.Update;
import net.htmlparser.jericho.Source;

public class Feeds extends TelegramProccess {
	
	SyndFeedInput input = new SyndFeedInput();
	
	public Feeds(Update update, Map<String, String> parametros){
		super(update, parametros);
	}

	@Override
	void proccess() {
		SyndFeed feed;
		try {
			feed = input.build(new XmlReader(new URL(parametros.get("url"))));
		} catch (IllegalArgumentException | FeedException | IOException e) {
			e.printStackTrace();
			return;
		}
		
		mensagem = feed.getTitle() + "\n";
		
		for(SyndEntry entry: (List<SyndEntry>)feed.getEntries() ) {
			
			mensagem = mensagem + "\n>>>>>>>>>>>>>>>>>>>>>>>>\n" + entry.getTitle() + "\n";
			
			mensagem = mensagem + "\nResponsavel: " +entry.getAuthor() + "\n";
			
			Source source = new Source(entry.getDescription().getValue());
			
			mensagem = mensagem + "\n" + source.getRenderer().toString() + "\n";
		
		}
		
	}

}
