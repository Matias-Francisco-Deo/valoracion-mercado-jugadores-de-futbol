package com.overcode.persistence.repository.dao.external.scrapper.util;

import com.overcode.persistence.repository.dao.external.scrapper.exception.ScraperExtractionException;
import com.overcode.testUtils.HtmlFixtures;
import org.junit.jupiter.api.Test;

import static com.overcode.persistence.repository.dao.external.scrapper.util.JsonExtractorUtil.stripQuotes;
import static org.junit.jupiter.api.Assertions.*;

class JsonExtractorUtilTest {

    @Test
    void extraeJsonValidoSinRuidoDelScript() {
        String payload = JsonExtractorUtil.extractPlayerStatsJson(HtmlFixtures.htmlConJsonValido());

        assertTrue(payload.contains("\"goals\": 10"));
        assertFalse(payload.contains("require.config"));
    }

    @Test
    void lanzaExcepcionCuandoHtmlEsNulo() {
        assertThrows(ScraperExtractionException.class, () -> JsonExtractorUtil.extractPlayerStatsJson(null));
    }

    @Test
    void lanzaExcepcionCuandoHtmlNoCierra() {
        String html = HtmlFixtures.htmlConJsonValidoSinCerrar();
        assertThrows(ScraperExtractionException.class, () -> JsonExtractorUtil.extractPlayerStatsJson(html));
    }

    @Test
    void lanzaExcepcionCuandoJsonNoCierra() {
        String html = HtmlFixtures.htmlConJsonQueNoCierra();
        assertThrows(ScraperExtractionException.class, () -> JsonExtractorUtil.extractPlayerStatsJson(html));
    }

    @Test
    void lanzaExcepcionCuandoHtmlEsVacio() {
        assertThrows(ScraperExtractionException.class, () -> JsonExtractorUtil.extractPlayerStatsJson(""));
    }

    @Test
    void lanzaExcepcionCuandoHtmlEsMalFormado() {
        String html = HtmlFixtures.htmlSinBloqueJson();
        assertThrows(ScraperExtractionException.class, () -> JsonExtractorUtil.extractPlayerStatsJson(html));
    }

    @Test
    void extraePosicionDefensorDesdeDescripcion() {
        assertEquals("Defender", JsonExtractorUtil.extractPlayerPosition(HtmlFixtures.htmlConJsonValido()));
    }

    @Test
    void extraePosicionDelanteroDesdeDescripcion() {
        assertEquals("Forward", JsonExtractorUtil.extractPlayerPosition(HtmlFixtures.htmlConMetaDescriptionDeDelantero()));
    }

    @Test
    void extraePosicionDesdeElAtributoContentDeLaMetaDescription() {
        assertEquals("Goalkeeper",
                JsonExtractorUtil.extractPlayerPosition(HtmlFixtures.htmlConMetaDescriptionContentDeArquero()));
    }

    @Test
    void extraePosicionHastaElCierreDelSpan() {
        assertEquals("Defender", JsonExtractorUtil.extractPlayerPosition(HtmlFixtures.htmlConPosicionEnSpan()));
    }

    @Test
    void extraePosicionHastaLaSiguienteEtiquetaCuandoNoHayCierreDelSpan() {
        assertEquals("Midfielder",
                JsonExtractorUtil.extractPlayerPosition(HtmlFixtures.htmlConPosicionSinSpanPeroConOtroTag()));
    }

    @Test
    void devuelveNullSiLaPosicionNoTieneDelimitadorDeCierre() {
        assertNull(JsonExtractorUtil.extractPlayerPosition(HtmlFixtures.htmlConPosicionSinDelimitador()));
    }

    @Test
    void stripQuotesRemueveComillasDobles(){
        assertEquals("Defender", stripQuotes("\"Defender\""));
    }

    @Test
    void stripQuotesRemueveComillasSimples(){
        assertEquals("Forward", stripQuotes("'Forward'"));
    }

    @Test
    void stripQuotesRecortaEspaciosSinComillas(){
        assertEquals("Midfielder", stripQuotes("  Midfielder  "));
    }

    @Test
    void stripQuotesDevuelveVacioParaValorNullOVacio(){
        assertEquals("", stripQuotes(null));
        assertEquals("", stripQuotes("  "));
    }

    @Test
    void noExtraePosicionSiEsNull() {
        assertNull(JsonExtractorUtil.extractPlayerPosition(null));
    }

    @Test
    void retornaNullCuandoNoHayMarcador() {
        assertNull(JsonExtractorUtil.extractPlayerPosition(HtmlFixtures.htmlSinPosicion()));
    }


}
