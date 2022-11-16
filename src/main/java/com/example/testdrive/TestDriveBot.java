package com.example.testdrive;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class TestDriveBot extends TelegramLongPollingBot {
    private MessageBuilder messageBuilder;
    SimpleDateFormat sdf;

    List<Calendar> dailies;
    List<Long> userIds;

    public TestDriveBot() {
        sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
        dailies = new ArrayList<>();
        userIds = new ArrayList<>();
        messageBuilder = new MessageBuilder();
        fillDailies(dailies);
    }

    private static void fillDailies(List<Calendar> dailies) {
        Calendar daily1 = new GregorianCalendar();

        daily1.set(Calendar.YEAR, 2022);
        daily1.set(Calendar.MONTH, 10);
        daily1.set(Calendar.DAY_OF_MONTH, 16);
        daily1.set(Calendar.HOUR_OF_DAY, 17);
        daily1.set(Calendar.MINUTE, 50);
        dailies.add(daily1);

        Calendar daily2 = new GregorianCalendar();
        daily2.set(Calendar.YEAR, 2022);
        daily2.set(Calendar.MONTH, 10);
        daily2.set(Calendar.DAY_OF_MONTH, 20);
        daily2.set(Calendar.HOUR_OF_DAY, 16);
        daily2.set(Calendar.MINUTE, 40);
        dailies.add(daily2);
    }

    @Override
    public void onUpdateReceived(Update update) {
        String reply = "";
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText) {
                // Регистрируем пользователя
                case "/start":
                    registerUser(chatId);
                    reply = messageBuilder.prepareStartMessage();
                    break;
                case "calendar":
                    reply = messageBuilder.prepareCalendar(dailies);
                    break;
                case "info":
                    reply = messageBuilder.prepareInfo();
                    break;
                case "add":
                    reply = messageBuilder.prepareAdd();
                    break;
                case "nearest":
                    reply = messageBuilder.prepareNearest(dailies);
                    break;
                default:
                    try {
                        Calendar daily = Calendar.getInstance();
                        sdf.parse(messageText);
                        dailies.add(daily);
                        reply = "Daily был успешно записан";
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
            }
            prepareAndSendMessage(chatId, reply);
        }
    }

    private void registerUser(long userId) {
        userIds.add(userId);
    }

    private void prepareAndSendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        executeMessage(message);
    }

    private void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    // Напоминание каждый день в 12 часов дня до ближайшего дейли
    @Scheduled(cron = "0 0 12 * * ?")
    private void sendNotifications() {
        for (Long id : userIds) {
            prepareAndSendMessage(id, messageBuilder.prepareNearest(dailies));
        }
    }

    private Calendar getNearestDailyTimeUnit() {
        int min = 0;
        for (int i = 0; i < dailies.size(); i++) {
            if (dailies.get(i).getTime().before(dailies.get(min).getTime())) {
                min = i;
            }
        }
        return dailies.get(min);
    }

    // Напоминание за 15 минут до ближайшего дейли
    @Scheduled(cron = "0 * * * * *")
    private void send15MinuteNotifications() {
        Date current = new Date();
        if (current.getTime() - getNearestDailyTimeUnit().getTimeInMillis() < 900000) {
            for (Long id : userIds) {
                prepareAndSendMessage(id, messageBuilder.prepareNearest(dailies));
            }
        }
    }

    @Override
    public String getBotUsername() {
        return "TestDrive_Anikin_bot";
    }

    @Override
    public String getBotToken() {
        return "5627293818:AAEWHzVyaxfpYjGoVgYEuZSaVCLwjRtWkeY";
    }
}
