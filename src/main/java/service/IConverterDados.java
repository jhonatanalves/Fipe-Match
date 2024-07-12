package service;

import java.util.List;

public interface IConverterDados {

    <T> T  obterDados(String json, Class<T> classe);

    <T> List<T> obterlista(String json, Class<T> classe);

    <T> List<T> obterlistaV2(String json, Class<T> classe);
}
