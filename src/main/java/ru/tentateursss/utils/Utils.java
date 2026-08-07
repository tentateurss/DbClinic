package ru.tentateursss.utils;

import ru.tentateursss.clinic.model.Clinic;

import java.util.Arrays;

public class Utils {

    public static String generateClinicCode(Clinic clinic) {
        String prefix = getPrefix(clinic.getName());
        Long id = clinic.getId();
        if (id == null) {
            throw new IllegalStateException("Clinic must have an ID before generating code");
        }
        return prefix + "-" + id;
    }

    private static String getPrefix(String name) {
        if (name == null || name.isBlank()) return "CL";

        String[] words = name.trim().split("\\s+");

        if (words.length == 1) {
            int len = Math.min(words[0].length(), 2);
            return words[0].substring(0, len).toUpperCase();
        }

        if (words.length == 2) {
            return (words[0].charAt(0) + "" + words[1].charAt(0)).toUpperCase();
        }

        return (words[0].charAt(0) + "" + words[1].charAt(0) + "" + words[2].charAt(0)).toUpperCase();
    }

    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return "";
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    public static String splitBio(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "";
        }

        String[] parts = fullName.trim().split("\\s+");

        // Очищаем от служебных символов
        String[] formatted = Arrays.stream(parts)
                .filter(part -> !part.matches("[-—]|нет|0"))
                .map(Utils::capitalize)
                .toArray(String[]::new);

        if (formatted.length == 0) {
            return "";
        }

        if (formatted.length == 1) {
            return formatted[0];
        }

        if (formatted.length == 2) {
            // Если второе слово содержит точку - это инициал
            if (formatted[1].contains(".")) {
                return formatted[0] + " " + formatted[1];
            }
            return formatted[0] + " " + formatted[1] + " -";
        }

        // Если есть отчество
        if (formatted.length >= 3) {
            // Если отчество в виде инициала
            if (formatted[2].length() <= 2 && formatted[2].contains(".")) {
                return formatted[0] + " " + formatted[1] + " " + formatted[2];
            }
            // Полное отчество
            return formatted[0] + " " + formatted[1] + " " + formatted[2];
        }

        return String.join(" ", formatted);
    }

    public static String formatPhone(String phone) {
        if (phone == null || phone.isBlank()) {
            return "";
        }

        String cleaned = phone.replaceAll("[\\s\\-()]", "");

        if (cleaned.startsWith("8")) {
            cleaned = "+7" + cleaned.substring(1);
        } else if (cleaned.startsWith("7")) {
            cleaned = "+" + cleaned;
        } else if (!cleaned.startsWith("+7")) {
            return phone;
        }

        if (cleaned.length() != 12) {
            return phone;
        }

        return String.format("%s %s %s-%s-%s",
                cleaned.substring(0, 2),
                cleaned.substring(2, 5),
                cleaned.substring(5, 8),
                cleaned.substring(8, 10),
                cleaned.substring(10, 12)
        );
    }

    public static String generateTemporaryClinicCode() {
        return "TEMP-" + System.currentTimeMillis();
    }
}
