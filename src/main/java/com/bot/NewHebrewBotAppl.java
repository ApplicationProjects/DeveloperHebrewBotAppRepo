package com.bot;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import com.bot.service.HebrewBotImpl;

public class NewHebrewBotAppl {

    public static void main(String[] args) {
        try {
            // Инициализируем API и регистрируем бот
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new HebrewBotImpl());
            System.out.println("Bot starts");
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
