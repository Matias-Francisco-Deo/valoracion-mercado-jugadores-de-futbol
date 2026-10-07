package com.overcode.testUtils;

public final class WhoScoredTestFixtures {

    private WhoScoredTestFixtures() {
        // Utility class
    }

    public static String createHtmlWithArgsJson(String jsonBody) {
        return """
                <!DOCTYPE html>
                <html>
                <head><title>WhoScored Mock</title><meta name="description" content="Midfielder"></head>
                <body>
                <script type="text/javascript">
                require.config.params['args'] = %s;
                </script>
                </body>
                </html>
                """.formatted(jsonBody);
    }

    public static String createValidMultiLeagueHtml() {
        return createHtmlWithArgsJson("""
                {
                  "tournaments": [
                    {
                      "TournamentId": 4,
                      "Goals": 10,
                      "Assists": 4,
                      "ShotsOnTarget": 20,
                      "Interceptions": 6,
                      "TotalTackles": 8,
                      "KeyPasses": 12,
                      "Dribbles": 15,
                      "GameStarted": 10,
                      "SubOn": 2,
                      "TotalPasses": 400.0,
                      "AccuratePasses": 340.0,
                      "Rating": 7.80
                    },
                    {
                      "TournamentId": 2,
                      "Goals": 2,
                      "Assists": 1,
                      "ShotsOnTarget": 4,
                      "Interceptions": 2,
                      "TotalTackles": 3,
                      "KeyPasses": 4,
                      "Dribbles": 5,
                      "GameStarted": 3,
                      "SubOn": 1,
                      "TotalPasses": 100.0,
                      "AccuratePasses": 85.0,
                      "Rating": 7.20
                    },
                    {
                      "TournamentId": 999,
                      "Goals": 99,
                      "Assists": 99,
                      "ShotsOnTarget": 99,
                      "Interceptions": 99,
                      "TotalTackles": 99,
                      "KeyPasses": 99,
                      "Dribbles": 99,
                      "GameStarted": 10,
                      "SubOn": 0,
                      "TotalPasses": 200.0,
                      "AccuratePasses": 200.0,
                      "Rating": 9.99
                    }
                  ]
                }
                """);
    }

    public static String createZeroTop5AppearancesHtml() {
        return createHtmlWithArgsJson("""
                {
                  "tournaments": [
                    {
                      "TournamentId": 4,
                      "GameStarted": 0,
                      "SubOn": 0,
                      "Goals": 0
                    },
                    {
                      "TournamentId": 999,
                      "GameStarted": 15,
                      "SubOn": 2,
                      "Goals": 5
                    }
                  ]
                }
                """);
    }

    public static String createZeroPassesAndZeroRatingHtml() {
        return createHtmlWithArgsJson("""
                {
                  "tournaments": [
                    {
                      "TournamentId": 3,
                      "GameStarted": 1,
                      "SubOn": 0,
                      "Goals": 0,
                      "Assists": 0,
                      "ShotsOnTarget": 0,
                      "Interceptions": 0,
                      "TotalTackles": 0,
                      "KeyPasses": 0,
                      "Dribbles": 0,
                      "TotalPasses": 0.0,
                      "AccuratePasses": 0.0,
                      "Rating": 0.0
                    }
                  ]
                }
                """);
    }

    public static String createMissingTournamentsNodeHtml() {
        return createHtmlWithArgsJson("""
                {
                  "matchHeader": { "id": 12345 }
                }
                """);
    }

    public static String createNonArrayTournamentsNodeHtml() {
        return createHtmlWithArgsJson("""
                {
                  "tournaments": "invalid-non-array-string"
                }
                """);
    }

    public static String createCorruptedJsonHtml() {
        return """
                <!DOCTYPE html>
                <html>
                <head><title>Corrupted</title><meta name="description" content="Midfielder"></head>
                <body>
                <script type="text/javascript">
                require.config.params['args'] = { "tournaments": [ { "TournamentId": 4, brokenSyntax } ] };
                </script>
                </body>
                </html>
                """;
    }

    public static String createHtmlWithoutArgsBlock() {
        return """
                <!DOCTYPE html>
                <html>
                <head><title>No Args Block</title><meta name="description" content="Midfielder"></head>
                <body>
                <div>Some normal web content without require config</div>
                </body>
                </html>
                """;
    }
}
