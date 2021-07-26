package com.inmd.javabot.commands.music;

import com.inmd.javabot.Bot;
import com.inmd.javabot.audio.AudioHandler;
import com.inmd.javabot.audio.QueuedTrack;
import com.inmd.javabot.commands.DJCommand;
import com.inmd.javabot.commands.MusicCommand;
import com.inmd.javabot.spotify.parsing;
import com.inmd.javabot.utils.FormatUtil;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.menu.ButtonMenu;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.exceptions.PermissionException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;

import java.util.concurrent.TimeUnit;


public class spotify extends MusicCommand {

    private final static String LOAD = "\uD83D\uDCE5"; // 📥
    private final static String CANCEL = "\uD83D\uDEAB"; // 🚫

    private final String loadingEmoji;

    public static String song, artists, count;

    public static String id;

    public spotify(Bot bot) {
        super(bot);
        this.loadingEmoji = bot.getConfig().getLoading();
        this.name = "sy";
        this.help = "스포티파이에서 가져온 플레이리스트를 재생해줌니다.";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.beListening = true;
        this.bePlaying = false;
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
    }


    @Override
    public void doCommand(CommandEvent event) {


        if (event.getArgs().isEmpty() && event.getMessage().getAttachments().isEmpty()) {
            AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
            if (handler.getPlayer().getPlayingTrack() != null && handler.getPlayer().isPaused()) {
                if (DJCommand.checkDJPermission(event)) {
                    handler.getPlayer().setPaused(false);
                    event.replySuccess("Resumed **" + handler.getPlayer().getPlayingTrack().getInfo().title + "**.");
                } else
                    event.replyError("DJ 만 음악을 일시 정지 할 수 있습니다!");
                return;
            }
            StringBuilder builder = new StringBuilder(event.getClient().getWarning() + " spotify Commands:\n");
            builder.append("\n`").append(event.getClient().getPrefix()).append(name).append(" <spotify playlist url>` - 스포티파이에서 플레이리스트를 가지고와서 재생합니다.");
        }
        String args = event.getArgs().startsWith("<") && event.getArgs().endsWith(">")
                ? event.getArgs().substring(1, event.getArgs().length() - 1)
                : event.getArgs().isEmpty() ? event.getMessage().getAttachments().get(0).getUrl() : event.getArgs();


        id = args.replace("https://open.spotify.com/playlist/", "").replace("?si=Zy_qJ3aCQ9y1pxEM2mX2dw&utm_source=copy-link", "");
        try {
            parsing.main();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        JSONObject json = new JSONObject(parsing.getJson());
        JSONArray Ltrack = (JSONArray) json.get("items");

        event.reply("이 플레이 리스트는 총" + Ltrack.length() + "개의 노래를 가지고 있습니다.");
        event.reply(loadingEmoji+" 로딩중.... `["+count+"]`", m -> bot.getPlayerManager().loadItemOrdered(event.getGuild(), count, new ResultHandler(m,event,false)));

    }
    private class ResultHandler implements AudioLoadResultHandler
    {
        private final Message m;
        private final CommandEvent event;
        private final boolean ytsearch;

        private ResultHandler(Message m, CommandEvent event, boolean ytsearch)
        {
            this.m = m;
            this.event = event;
            this.ytsearch = ytsearch;
        }

        private void loadSingle(AudioTrack track, AudioPlaylist playlist)
        {
            if(bot.getConfig().isTooLong(track))
            {
                m.editMessage(FormatUtil.filter(event.getClient().getWarning()+" 이 트랙은  (**"+track.getInfo().title+"**)허용 된 최대 값보다 깁니다.: `"
                        +FormatUtil.formatTime(track.getDuration())+"` > `"+FormatUtil.formatTime(bot.getConfig().getMaxSeconds()*1000)+"`")).queue();
                return;
            }
            AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
            int pos = handler.addTrack(new QueuedTrack(track, event.getAuthor()))+1;
            String addMsg = FormatUtil.filter(event.getClient().getSuccess()+" 추가되었습니다. **"+track.getInfo().title
                    +"** (`"+FormatUtil.formatTime(track.getDuration())+"`) "+(pos==0?"재생을 시작했습니다.":" 대기번호 "+pos));
            if(playlist==null || !event.getSelfMember().hasPermission(event.getTextChannel(), Permission.MESSAGE_ADD_REACTION))
                m.editMessage(addMsg).queue();
            else
            {
                new ButtonMenu.Builder()
                        .setText(addMsg+"\n"+event.getClient().getWarning()+" 이 트랙에는 **"+playlist.getTracks().size()+"** 첨부 된 트랙. 선택 "+LOAD+" 재생 목록을로드합니다.")
                        .setChoices(LOAD, CANCEL)
                        .setEventWaiter(bot.getWaiter())
                        .setTimeout(30, TimeUnit.SECONDS)
                        .setAction(re ->
                        {
                            if(re.getName().equals(LOAD))
                                m.editMessage(addMsg+"\n"+event.getClient().getSuccess()+"로드했습니다. **"+loadPlaylist(playlist, track)+"** 추가 트랙!").queue();
                            else
                                m.editMessage(addMsg).queue();
                        }).setFinalAction(m ->
                {
                    try{ m.clearReactions().queue(); }catch(PermissionException ignore) {}
                }).build().display(m);
            }
        }

        private int loadPlaylist(AudioPlaylist playlist, AudioTrack exclude)
        {
            int[] count = {0};
            playlist.getTracks().stream().forEach((track) -> {
                if(!bot.getConfig().isTooLong(track) && !track.equals(exclude))
                {
                    AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
                    handler.addTrack(new QueuedTrack(track, event.getAuthor()));
                    count[0]++;
                }
            });
            return count[0];
        }

        public void trackLoaded(AudioTrack track)
        {
            loadSingle(track, null);
        }

        public void playlistLoaded(AudioPlaylist playlist)
        {
            if(playlist.getTracks().size()==1 || playlist.isSearchResult())
            {
                AudioTrack single = playlist.getSelectedTrack()==null ? playlist.getTracks().get(0) : playlist.getSelectedTrack();
                loadSingle(single, null);
            }
            else if (playlist.getSelectedTrack()!=null)
            {
                AudioTrack single = playlist.getSelectedTrack();
                loadSingle(single, playlist);
            }

        }

        public void noMatches()
        {
            if(ytsearch)
                m.editMessage(FormatUtil.filter(event.getClient().getWarning()+" 에 대한 검색 결과가 없습니다. `"+count+"`.")).queue();
            else
                // 요청
                try {
                    parsing.main();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            JSONObject json = new JSONObject(parsing.getJson());
            JSONArray Ltrack = (JSONArray) json.get("items");
            //노가다중
            for (int i = 0; i <= Ltrack.length(); i++) {
                Object jack = Ltrack.get(i);
                String data = jack.toString();
                //음악 제목
                JSONObject Data = new JSONObject(data);
                JSONObject track = (JSONObject) Data.get("track");
                JSONObject jsong = (JSONObject) track.get("album");
                song = jsong.get("name").toString();
                //가사
                String iartists = track.get("artists").toString();
                artists = iartists.replace("[", "").replace("{\"name\":\"", "").replace("\"}", "").replace("]", "");
                count = song + artists;

                bot.getPlayerManager().loadItemOrdered(event.getGuild(), "ytsearch:"+count, new ResultHandler(m,event,true));
            }


        }

        public void loadFailed(FriendlyException throwable)
        {
           
        }
    }

    public static String getplurl() {return id;}

}

