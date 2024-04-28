package cn.rh.flash.sdk.telegram;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 * Telegram Machine   Telegram机器人
 */
@Slf4j
@Component
public class TelegramMachine extends TelegramLongPollingBot {

    //@PostConstruct
    public void init() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi( DefaultBotSession.class);
            botsApi.registerBot( new TelegramMachine() );
        } catch (TelegramApiException e) {
            e.printStackTrace();
            log.error(" 初始化 TelegramBotsApi 出错了 ");
        }
    }


    /**
     * 使用一下链接 打开  机器人之父
     * https://web.telegram.org/k/#@BotFather
     * 1. 使用  /newbot  创建机器人
     * 2. 创建成功后 获取 getBotUsername 和 getBotToken
     * 3. https://api.telegram.org/bot6170934265:AAGyeNpuxB1_cNzwzxPlvRAOoFQBBeq03_w/getUpdates
     * 查找 id: -xxx 的一段值，这里的 -xxx 就是群组ID，机器人下发消息的时候的chat_id字段使用这个即可发送消息到群组了
     * @return
     */
    @Override
    public String getBotUsername() {
        return "jkHousekeepBot";
    }

    @Override
    public String getBotToken() {
        return "6170934265:AAGyeNpuxB1_cNzwzxPlvRAOoFQBBeq03_w";
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message msg = update.getMessage();  //新传入消息-文本、照片、贴纸等。
        String text = msg.getText();        //自定义模板对于文本消息，消息的当前UTF-8文本
        Long chatId =  update.getMessage().getChat().getId(); // 发送者 id

        SendMessage response = new SendMessage();
        response.setChatId( chatId.toString() );  //  要向其发送消息的聊天室的唯一标识符（或频道的用户名）
        response.setText( text );
        try {
            execute(response);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    //文本消息
    public void sendMsg(String text){
        SendMessage response = new SendMessage();
        response.setChatId( "-1001539119883" );  //  要向其发送消息的聊天室的唯一标识符（或频道的用户名）
        response.setText(text);
        try {
            execute(response);
        } catch (TelegramApiException e) {

        }
    }

}
