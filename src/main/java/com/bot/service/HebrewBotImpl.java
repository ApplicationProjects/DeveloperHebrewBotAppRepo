package com.bot.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


public class HebrewBotImpl extends TelegramLongPollingBot {
    private List<String> terms = new ArrayList<>();
    private Long myChatId = 1339320758L;
    private Set<Long> chatIds = new HashSet<>();

    public HebrewBotImpl() {
        super();
        loadTermsFromFile(); // Загрузка терминов из файла при создании бота
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            chatIds.add(chatId); // Сохранение chat ID

            if (messageText.equals("/start")) {
                sendTermWithButton(chatId);
            }
        } else if (update.hasCallbackQuery()) {
            if (update.getCallbackQuery() != null) {
                String callbackData = update.getCallbackQuery().getData();
                Long chatId = update.getCallbackQuery().getMessage().getChatId();
                if (callbackData.equals("getTerm")) {
                    sendDailyTerm(chatId);
                }
            } else {
                System.out.println("Received update with null callbackQuery: ");
                System.out.println(update);
            }
        }
    }

    private void sendTermWithButton(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("Press the button");

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        InlineKeyboardButton getTermButton = new InlineKeyboardButton();
        getTermButton.setText("Get Term");
        getTermButton.setCallbackData("getTerm");

        rowInline.add(getTermButton);
        rowsInline.add(rowInline);
        inlineKeyboardMarkup.setKeyboard(rowsInline);

        message.setReplyMarkup(inlineKeyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            System.out.println("Failed to send message with button to chat id ");
            System.out.println(chatId);
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendDailyTerm(Long chatId) {
        String term = getDailyTerm();
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(term);

        if (term == null || term.trim().isEmpty()) {
            System.out.println("Attempted to send an empty term to chat id");
            System.out.println(chatId);
            return;
        }

        try {
            execute(message);
            System.out.println("Sent daily term to chat id ");
            System.out.println(chatId);
            System.out.println(term);

            // После отправки термина, отправляем сообщение с кнопкой
            sendTermWithButton(chatId);
        } catch (TelegramApiException e) {
            System.out.println("Failed to send message to chat id ");
            System.out.println(chatId);
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendDailyTermToAllUsers() {
        for (Long chatId : chatIds) {
            sendDailyTerm(chatId);
        }
    }

    private String getDailyTerm() {
        if (terms.isEmpty()) {
            System.out.println("Terms list is empty. No terms available.");
            return "No terms available.";
        }

        Random random = new Random();
        int index = random.nextInt(terms.size());
        return terms.get(index);
    }

    private List<String> loadTermsFromFile() {
        try (InputStream inputStream = getClass().getResourceAsStream("/vocabular1.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                terms.add(line);
            }
            System.out.println("Loaded {} terms from file: " + terms.size());
        } catch (IOException e) {
            System.out.println("Failed to load terms from file: " + e.getMessage());
            e.printStackTrace();
        }
        return terms;
    }

    @Override
    public String getBotUsername() {
        return "DevHebrewBot";
    }

    @Override
    public String getBotToken() {
        return "7231922876:AAEHys4KnvD7Fa3PIQksGLIhnb_VQb4ulhU";
    }
}
