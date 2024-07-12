package model;

public record SumarioVeiculo(Integer TipoVeiculo,
                             String Valor,
                             String Marca,
                             String Modelo,
                             Integer AnoModelo,
                             String Combustivel,
                             String CodigoFipe,
                             String MesReferencia,
                             Character SiglaCombustivel) {

    @Override
    public String toString() {
        var sumario = """
            Valor: %s,
            Marca: %s,
            Modelo: %s,
            Ano: %d,
            Combustível: %s,
            Código Fipe: %s,
            Mês Referência: %s""";
        return String.format(sumario,
                Valor,
                Marca,
                Modelo,
                AnoModelo,
                Combustivel,
                CodigoFipe,
                MesReferencia);
    }

}






