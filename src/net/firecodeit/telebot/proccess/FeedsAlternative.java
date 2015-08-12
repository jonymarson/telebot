package net.firecodeit.telebot.proccess;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.DateUtils;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

import net.firecodeit.telebot.model.Update;
import net.htmlparser.jericho.Source;

public class FeedsAlternative extends TelegramProccess {
	
	SyndFeedInput input = new SyndFeedInput();
	
	public FeedsAlternative(Update update, Map<String, String> parametros){
		super(update, parametros);
	}

	@Override
	public void run() {
		SyndFeed feed;
		try {
			feed = input.build(new XmlReader(new URL(parametros.get("url"))));
		} catch (IllegalArgumentException | FeedException | IOException e) {
			e.printStackTrace();
			return;
		}
		
		String message = feed.getTitle() + "\n\n";
		
		for(SyndEntry entry: (List<SyndEntry>)feed.getEntries() ) {
			
			if(entry.getPublishedDate().after(DateUtils.addDays(new Date(), -20))) {
			
				Source source = new Source(entry.getDescription().getValue());
				
				message = message + ">>>>>>>>>>>>>>>>>>>>>>>>\n" + source.getRenderer().toString() + "\n";
				
			}
		
		}
	}

}
