package com.asesoria.contable.app_ac.utils.enums;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum CronogramaVencimientoSunat {;

    private static final Map<String, Map<Integer, LocalDate>> cronograma = new HashMap<>();

    static {
        // Ejemplo: Enero 2025 (clave: "2025-01")
        cronograma.put("2025-01", Map.of(
                0, LocalDate.of(2025, 2, 17),
                1, LocalDate.of(2025, 2, 18),
                2, LocalDate.of(2025, 2, 19),
                3, LocalDate.of(2025, 2, 19),
                4, LocalDate.of(2025, 2, 20),
                5, LocalDate.of(2025, 2, 20),
                6, LocalDate.of(2025, 2, 21),
                7, LocalDate.of(2025, 2, 21),
                8, LocalDate.of(2025, 2, 24),
                9, LocalDate.of(2025, 2, 24)
        ));

        cronograma.put("2025-02", Map.of(
                0, LocalDate.of(2025, 2, 17),
                1, LocalDate.of(2025, 2, 18),
                2, LocalDate.of(2025, 2, 19),
                3, LocalDate.of(2025, 2, 19),
                4, LocalDate.of(2025, 2, 20),
                5, LocalDate.of(2025, 2, 20),
                6, LocalDate.of(2025, 2, 21),
                7, LocalDate.of(2025, 2, 21),
                8, LocalDate.of(2025, 2, 24),
                9, LocalDate.of(2025, 2, 24)
        ));

        cronograma.put("2025-03", Map.of(
                0, LocalDate.of(2025, 3, 15),
                1, LocalDate.of(2025, 3, 16),
                2, LocalDate.of(2025, 3, 21),
                3, LocalDate.of(2025, 3, 21),
                4, LocalDate.of(2025, 3, 22),
                5, LocalDate.of(2025, 3, 22),
                6, LocalDate.of(2025, 3, 23),
                7, LocalDate.of(2025, 3, 23),
                8, LocalDate.of(2025, 3, 24),
                9, LocalDate.of(2025, 3, 24)
        ));

        cronograma.put("2025-04", Map.of(
                0, LocalDate.of(2025, 4, 16),
                1, LocalDate.of(2025, 4, 19),
                2, LocalDate.of(2025, 4, 20),
                3, LocalDate.of(2025, 4, 20),
                4, LocalDate.of(2025, 4, 21),
                5, LocalDate.of(2025, 4, 21),
                6, LocalDate.of(2025, 4, 22),
                7, LocalDate.of(2025, 4, 22),
                8, LocalDate.of(2025, 4, 23),
                9, LocalDate.of(2025, 4, 23)
        ));

        cronograma.put("2025-05", Map.of(
                0, LocalDate.of(2025, 5, 16),
                1, LocalDate.of(2025, 5, 17),
                2, LocalDate.of(2025, 5, 18),
                3, LocalDate.of(2025, 5, 18),
                4, LocalDate.of(2025, 5, 19),
                5, LocalDate.of(2025, 5, 19),
                6, LocalDate.of(2025, 5, 20),
                7, LocalDate.of(2025, 5, 20),
                8, LocalDate.of(2025, 5, 23),
                9, LocalDate.of(2025, 5, 23)
        ));

        cronograma.put("2025-06", Map.of(
                0, LocalDate.of(2025, 6, 15),
                1, LocalDate.of(2025, 6, 16),
                2, LocalDate.of(2025, 6, 17),
                3, LocalDate.of(2025, 6, 17),
                4, LocalDate.of(2025, 6, 18),
                5, LocalDate.of(2025, 6, 18),
                6, LocalDate.of(2025, 6, 21),
                7, LocalDate.of(2025, 6, 21),
                8, LocalDate.of(2025, 6, 22),
                9, LocalDate.of(2025, 6, 22)
        ));

        cronograma.put("2025-07", Map.of(
                0, LocalDate.of(2025, 7, 18),
                1, LocalDate.of(2025, 7, 19),
                2, LocalDate.of(2025, 7, 20),
                3, LocalDate.of(2025, 7, 20),
                4, LocalDate.of(2025, 7, 21),
                5, LocalDate.of(2025, 7, 21),
                6, LocalDate.of(2025, 7, 22),
                7, LocalDate.of(2025, 7, 22),
                8, LocalDate.of(2025, 7, 25),
                9, LocalDate.of(2025, 7, 25)
        ));

        cronograma.put("2025-05", Map.of(
                0, LocalDate.of(2025, 5, 16),
                1, LocalDate.of(2025, 5, 17),
                2, LocalDate.of(2025, 5, 18),
                3, LocalDate.of(2025, 5, 18),
                4, LocalDate.of(2025, 5, 19),
                5, LocalDate.of(2025, 5, 19),
                6, LocalDate.of(2025, 5, 20),
                7, LocalDate.of(2025, 5, 20),
                8, LocalDate.of(2025, 5, 23),
                9, LocalDate.of(2025, 5, 23)
        ));

        cronograma.put("2025-06", Map.of(
                0, LocalDate.of(2025, 6, 15),
                1, LocalDate.of(2025, 6, 16),
                2, LocalDate.of(2025, 6, 17),
                3, LocalDate.of(2025, 6, 17),
                4, LocalDate.of(2025, 6, 18),
                5, LocalDate.of(2025, 6, 18),
                6, LocalDate.of(2025, 6, 21),
                7, LocalDate.of(2025, 6, 21),
                8, LocalDate.of(2025, 6, 22),
                9, LocalDate.of(2025, 6, 22)
        ));

        cronograma.put("2025-07", Map.of(
                0, LocalDate.of(2025, 7, 18),
                1, LocalDate.of(2025, 7, 19),
                2, LocalDate.of(2025, 7, 20),
                3, LocalDate.of(2025, 7, 20),
                4, LocalDate.of(2025, 7, 21),
                5, LocalDate.of(2025, 7, 21),
                6, LocalDate.of(2025, 7, 22),
                7, LocalDate.of(2025, 7, 22),
                8, LocalDate.of(2025, 7, 25),
                9, LocalDate.of(2025, 7, 25)
        ));

        cronograma.put("2025-08", Map.of(
                0, LocalDate.of(2025, 8, 15),
                1, LocalDate.of(2025, 8, 16),
                2, LocalDate.of(2025, 8, 17),
                3, LocalDate.of(2025, 8, 17),
                4, LocalDate.of(2025, 8, 18),
                5, LocalDate.of(2025, 8, 18),
                6, LocalDate.of(2025, 8, 19),
                7, LocalDate.of(2025, 8, 19),
                8, LocalDate.of(2025, 8, 22),
                9, LocalDate.of(2025, 8, 22)
        ));

        cronograma.put("2025-09", Map.of(
                0, LocalDate.of(2025, 9, 16),
                1, LocalDate.of(2025, 9, 17),
                2, LocalDate.of(2025, 9, 20),
                3, LocalDate.of(2025, 9, 20),
                4, LocalDate.of(2025, 9, 21),
                5, LocalDate.of(2025, 9, 21),
                6, LocalDate.of(2025, 9, 22),
                7, LocalDate.of(2025, 9, 22),
                8, LocalDate.of(2025, 9, 23),
                9, LocalDate.of(2025, 9, 23)
        ));

        cronograma.put("2025-10", Map.of(
                0, LocalDate.of(2025, 10, 17),
                1, LocalDate.of(2025, 10, 18),
                2, LocalDate.of(2025, 10, 19),
                3, LocalDate.of(2025, 10, 19),
                4, LocalDate.of(2025, 10, 20),
                5, LocalDate.of(2025, 10, 20),
                6, LocalDate.of(2025, 10, 21),
                7, LocalDate.of(2025, 10, 21),
                8, LocalDate.of(2025, 10, 24),
                9, LocalDate.of(2025, 10, 24)
        ));

        cronograma.put("2025-11", Map.of(
                0, LocalDate.of(2025, 11, 17),
                1, LocalDate.of(2025, 11, 18),
                2, LocalDate.of(2025, 11, 19),
                3, LocalDate.of(2025, 11, 19),
                4, LocalDate.of(2025, 11, 20),
                5, LocalDate.of(2025, 11, 20),
                6, LocalDate.of(2025, 11, 21),
                7, LocalDate.of(2025, 11, 21),
                8, LocalDate.of(2025, 11, 24),
                9, LocalDate.of(2025, 11, 24)
        ));

        cronograma.put("2025-12", Map.of(
                0, LocalDate.of(2025, 12, 16),
                1, LocalDate.of(2025, 12, 19),
                2, LocalDate.of(2025, 12, 20),
                3, LocalDate.of(2025, 12, 20),
                4, LocalDate.of(2025, 12, 21),
                5, LocalDate.of(2025, 12, 21),
                6, LocalDate.of(2025, 12, 22),
                7, LocalDate.of(2025, 12, 22),
                8, LocalDate.of(2025, 12, 23),
                9, LocalDate.of(2025, 12, 23)
        ));
    }

    public static LocalDate getFechaVencimiento(String periodo, int ultimoDigitoRuc) {
        return cronograma.getOrDefault(periodo, Collections.emptyMap())
                .getOrDefault(ultimoDigitoRuc, null);
    }

}
