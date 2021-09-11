package co.runed.bolster.util.lang;

import java.util.Map;

public interface LangProvider {
    Map<String, String> getLangReplacements();

    Map<String, String> getLangSource();
}
