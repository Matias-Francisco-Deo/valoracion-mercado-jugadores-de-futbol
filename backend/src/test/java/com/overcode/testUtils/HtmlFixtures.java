package com.overcode.testUtils;

public final class HtmlFixtures {

    private HtmlFixtures() {
        // Utility class
    }

    public static String htmlConJsonValido() {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta name="description" content="Position: Defender">
                </head>
                <body>
                <script type="text/javascript">
                    require.config.params['args'] = {
                        "goals": 10,
                        "assists": 5,
                        "shotsOnTarget": 8,
                        "tackles": 12,
                        "keyPasses": 6,
                        "rating": 7.8,
                        "successfulDribbles": 9
                    };
                </script>
                </body>
                </html>
                """;
    }

    public static String htmlSinBloqueJson() {
        return """
                <!DOCTYPE html>
                <html>
                <body>
                    <div>No JSON block here.</div>
                </body>
                </html>
                """;
    }

    public static String htmlConMetaDescriptionDeDelantero() {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta name="description" content="This player is a Forward and attacker for the team">
                </head>
                <body></body>
                </html>
                """;
    }

    public static String htmlSinPosicion() {
        return """
                <!DOCTYPE html>
                <html>
                <head><title>No position</title></head>
                <body><div>Un jugador sin datos relevantes.</div></body>
                </html>
                """;
    }
}