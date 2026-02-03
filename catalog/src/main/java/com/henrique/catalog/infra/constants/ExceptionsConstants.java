package com.henrique.catalog.infra.constants;

public class ExceptionsConstants {

    public static String MOVIE_DONT_EXISTS = "Não existe filme com id %s";
    public static String CINEMA_DONT_EXISTS = "Não existe um cinema com id %s";
    public static String ROOM_IN_CINEMA_DONT_EXISTS = "Não existe uma sala com id %s no cinema com id %s";
    public static String DUPLICATE_RESOURCE = "Ja existe um dado com esse valor no campo %s";
    public static String DUPLICATE_RESOURCE_ROOM = "Ja existe uma sala com o nome %s nesse cinema";
}
