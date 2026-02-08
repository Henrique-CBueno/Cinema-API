package com.bsys.reservation.infra.constants;

public class ExceptionConstants {

    public static String SEAT_UNAVAIBLE = "Assento %s Ja está reservado";
    public static String UNAVAIBLE_SESSION = "Essa seção nao esta disponivel para reservas";
    public static String SEAT_DONT_EXISTS_IN_ROOM = "esses assento nao existe na seçao %s";
    public static String RESERVATION_ALREADY_EXISTS = "Ja existe uma reserva para um ou mais assentos desta sessao";
    public static String SEAT_ALREADY_RESERVED = "Assento ja reservado para esta sessao";
    public static String RESERVATIONS_NOT_FOUND = "Não foram achadas reservas";
    public static String RESERVATIONS_WITH_ID_NOT_FOUND = "Não foram achadas reservas confirmadas com id %s";
    public static String SEAT_VALIDATION_FAILED = "Nao foi possivel validar os assentos";
}
