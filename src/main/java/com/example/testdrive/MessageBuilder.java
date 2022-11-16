package com.example.testdrive;

import java.util.Calendar;
import java.util.List;

public class MessageBuilder {
    public String prepareStartMessage() {
        return "Вы успешно зарегистрированы в системе! Ждите дальнейших оповещений \n" +
                "Пока можете посмотреть график запланированных daily с помощью команды: calendar \n" +
                "Или добавить daily с помошью команды: add \n" +
                "Для просмотра даты ближайшего daily введите: nearest \n" +
                "Если хотите узнать о функционале, введите команду: info";
    }

    public String prepareInfo() {
        return "Пока можете посмотреть график запланированных daily с помощью команды: calendar \n" +
                "Или добавить daily с помошью команды: add \n" +
                "Для просмотра даты ближайшего daily введите: nearest \n" +
                "Если хотите узнать о функционале, введите команду: info";
    }

    public String prepareCalendar(List<Calendar> dailies) {
        StringBuilder reply = new StringBuilder("Ближайшие daily запланированы на:\n ");
        for (Calendar daily : dailies) {
            reply.append(daily.getTime());
            reply.append("\n");
        }
        return reply.toString();
    }

    public String prepareAdd(){
        return "Для создания daily введите дату в формате: EEE MMM dd HH:mm:ss z yyyy \n" +
                "Пример: Mon Mar 14 16:02:37 GMT 2022";
    }
    public String prepareNearest(List<Calendar> dailies){
        int nearestDailyIndex = getNearestDailyIndex(dailies);
        return "Ближайшее daily назначено на " + dailies.get(nearestDailyIndex).getTime();
    }
    private int getNearestDailyIndex(List<Calendar> dailies) {
        int min = 0;
        for (int i = 0; i < dailies.size(); i++) {
            if (dailies.get(i).getTime().before(dailies.get(min).getTime())) {
                min = i;
            }
        }
        return min;
    }

}
