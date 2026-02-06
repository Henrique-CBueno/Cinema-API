package com.henrique.catalog.infra.constants;

public class ExceptionsConstants {

    public static String MOVIE_DONT_EXISTS = "Não existe filme com id %s";
    public static String CINEMA_DONT_EXISTS = "Não existe um cinema com id %s";
    public static String ROOM_IN_CINEMA_DONT_EXISTS = "Não existe uma sala com id %s no cinema com id %s";
    public static String SEAT_IN_ROOM_DONT_EXISTS = "Não existe um assento com id %s na sala com id %s no cinema com id %s";
    public static String SESSION_DONT_EXISTS = "Não existe uma sessão com id %s";
    public static String DUPLICATE_RESOURCE = "Ja existe um dado com esse valor no campo %s";
    public static String DUPLICATE_RESOURCE_ROOM = "Ja existe uma sala com o nome %s nesse cinema";
    public static String DUPLICATE_SEAT_POSITION = "Já existe um assento ativo na com fileira e numero igual para esta sala.";
    public static String IMPOSSIBLE_SEAT_POSITION = "Esse assento é impossivel nessa sala";
    public static String SESSION_IN_THIS_TIME = "Essa sessão é impossivel nesse horário para essa sala, ja existe uma sessão nesse horário para essa sala.";
}
