package net.firecodeit.telebot.model;

public final class Update {
    public final long update_id;
    public final Message message;

    public Update(long update_id, Message message){
        this.update_id = update_id;
        this.message = message;
    }

    public static final class Message {
        public final long message_id;
        public final From from;
        public final Chat chat;
        public final long date;
        public final String text;

        public Message(long message_id, From from, Chat chat, long date, String text){
            this.message_id = message_id;
            this.from = from;
            this.chat = chat;
            this.date = date;
            this.text = text;
        }

        public static final class From {
            public final long id;
            public final String first_name;
            public final String last_name;
            public final String username;
    
            public From(long id, String first_name, String last_name, String username){
                this.id = id;
                this.first_name = first_name;
                this.last_name = last_name;
                this.username = username;
            }
        }

        public static final class Chat {
            public final long id;
            public final String first_name;
            public final String last_name;
            public final String username;
    
            public Chat(long id, String first_name, String last_name, String username){
                this.id = id;
                this.first_name = first_name;
                this.last_name = last_name;
                this.username = username;
            }
        }
    }
}