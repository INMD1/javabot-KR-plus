package com.inmd.javabot.commands.plugin;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jlyrics.LyricsClient;
import com.inmd.javabot.Bot;
import com.inmd.javabot.audio.AudioHandler;
import com.inmd.javabot.commands.pluginCommand;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;

public class lyrics extends pluginCommand {

	private final LyricsClient client = new LyricsClient();

	public lyrics(Bot bot) {
		super(bot);
		// TODO Auto-generated constructor stub
        this.name = "가사검색";
        this.arguments = "[song name]";
        this.help = "현재 재생중인 노래의 가사를 보여줍니다.";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.bePlaying = true;
	}

	@Override
	public void doCommand(CommandEvent event) {
		// TODO Auto-generated method stub
        event.getChannel().sendTyping().queue();
        String title;
        if(event.getArgs().isEmpty())
            title = ((AudioHandler)event.getGuild().getAudioManager().getSendingHandler()).getPlayer().getPlayingTrack().getInfo().title;
        else
            title = event.getArgs();
        client.getLyrics(title).thenAccept(lyrics -> 
        {
            if(lyrics == null)
            {
            	event.replyError("입력한정보 " + title + " 를 찾을수 없습니다." + (event.getArgs().isEmpty() ? " \r\n" + "노래 이름을 수동으로 입력 해보십시오 (`lyrics [song name]`)" : ""));
                return;
            }

            EmbedBuilder eb = new EmbedBuilder()
                    .setAuthor(lyrics.getAuthor())
                    .setColor(event.getSelfMember().getColor())
                    .setTitle(lyrics.getTitle(), lyrics.getURL());
            if(lyrics.getContent().length()>15000)
            {
                event.replyWarning("검색한 음악: " + title + " 발견되었지만 정확하지 않을 수 있습니다: " + lyrics.getURL());
            }
            else if(lyrics.getContent().length()>2000)
            {
                String content = lyrics.getContent().trim();
                while(content.length() > 2000)
                {
                    int index = content.lastIndexOf("\n\n", 2000);
                    if(index == -1)
                        index = content.lastIndexOf("\n", 2000);
                    if(index == -1)
                        index = content.lastIndexOf(" ", 2000);
                    if(index == -1)
                        index = 2000;
                    event.reply(eb.setDescription(content.substring(0, index).trim()).build());
                    content = content.substring(index).trim();
                    eb.setAuthor(null).setTitle(null, null);
                }
                event.reply(eb.setDescription(content).build());
            }
            else
                event.reply(eb.setDescription(lyrics.getContent()).build());
        });
    }	

}
