package main;

import model.*;
import service.ConsumirAPI;
import service.ConverterDados;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

//https://deividfortuna.github.io/fipe/v2/
//https://github.com/parallelum/fipe-go

public class Principal {

    private Scanner leitura;
    private ConsumirAPI consumirAPI;
    private ConverterDados converterDados;
    private final String urlBase;
    private final String urlCarros;
    private final String urlMotos;
    private final String urlCaminhoes;
    private String urlCorrente;
    private String categoriaSelecionada;
    private String marcaSelecionada;
    private String modeloSelecionado;
    private String veiculoSelecionado;

    public Principal() {
        leitura = new Scanner(System.in);
        consumirAPI = new ConsumirAPI();
        converterDados = new ConverterDados();
        urlBase = "https://parallelum.com.br/fipe/api/v1/";
        urlCarros = "carros/marcas/";
        urlMotos = "motos/marcas/";
        urlCaminhoes = "caminhoes/marcas/";
        urlCorrente = "";
        categoriaSelecionada = "";
        marcaSelecionada = "";
        modeloSelecionado = "";
        veiculoSelecionado = "";
    }

    public void consultar() {

        var urlCategoria = escolherCategoria();
        if (urlCategoria.isEmpty()) {
            System.err.println("Categoria inválida");
            return;
        }

        List<Marca> marcas = consultarMarcas(urlCategoria);
        System.out.printf("Marcas de veículos para a categoria %s%n", categoriaSelecionada);
        marcas.forEach(t -> System.out.println(t.nome()));

        ListaModelosVeiculos modelos = consultarModelos(marcas, urlCategoria);
        if (modelos == null) {
            System.err.println("Marca inválida");
            return;
        }
        System.out.printf("Modelos da marca %s%n", marcaSelecionada);
        modelos.modelos().stream().forEach(t -> System.out.println(t.nome()));

        List<Veiculo> veiculos = consultarVeiculos(modelos);
        if (veiculos == null) {
            System.err.println("Modelo inválido");
            return;
        }
        System.out.printf("Veículos do modelo %s%n", modeloSelecionado);
        veiculos.stream().forEach(t -> System.out.println(t.nome()));

        System.out.println("Quais informações deseja exibir? \n1 para todos os veículos \n2 para um veículo em particular?");
        var resposta = leitura.nextLine();
        if(resposta.equals("1")){
            List<SumarioVeiculo> sumarioVeiculos = selecionarTodosVeiculos(veiculos);
            System.out.printf("Dados gerais para todos os veículos do modelo %s%n", modeloSelecionado);
            sumarioVeiculos.stream().forEach(t-> {
                System.out.println(t.toString());
                System.out.println("----------------------------------");
            });
        }else{
            SumarioVeiculo sumarioVeiculo = selecionarVeiculo(veiculos);
            if (sumarioVeiculo == null) {
                System.err.println("Veículo inválido");
                return;
            }
            System.out.printf("Dados gerais do modelo %s/%s%n", modeloSelecionado, veiculoSelecionado);
            System.out.println(sumarioVeiculo.toString());
        }

    }

    private String escolherCategoria() {

        System.out.println("""
                Digite uma das opções para consulta:
                Carro
                Moto
                Caminhão
                """);

        var opcaoCategoria = leitura.nextLine();

        var urlCategoria = "";

        if (opcaoCategoria.toLowerCase().contains("carr")) {
            urlCategoria = urlBase + urlCarros;
            categoriaSelecionada = "carro";
        } else if (opcaoCategoria.toLowerCase().contains("mot")) {
            urlCategoria = urlBase + urlMotos;
            categoriaSelecionada = "moto";
        } else if (opcaoCategoria.toLowerCase().contains("caminh")) {
            urlCategoria = urlBase + urlCaminhoes;
            categoriaSelecionada = "caminhão";
        }

        return urlCategoria;
    }

    public List<Marca> consultarMarcas(String urlCategoria) {

        var json = consumirAPI.obterDados(urlCategoria);
        List<Marca> marcas = converterDados.obterlista(json, Marca.class);
        return marcas;
    }

    public ListaModelosVeiculos consultarModelos(List<Marca> marcas, String URL_CATEGORIA) {

        System.out.println("Digite a marca desejada:");

        final var marcaDesejada = leitura.nextLine();

        Optional<Marca> marcaEncontrada = marcas.stream()
                .filter(e -> e.nome().equalsIgnoreCase(marcaDesejada))
                .findFirst();

        ListaModelosVeiculos modelos = null;

        if (marcaEncontrada.isPresent()) {
            urlCorrente = URL_CATEGORIA + marcaEncontrada.get().codigo() + "/modelos";
            marcaSelecionada = marcaEncontrada.get().nome();
            var jasonModelos = consumirAPI.obterDados(urlCorrente);
            modelos = converterDados.obterModelos(jasonModelos, ListaModelosVeiculos.class);
        }

        return modelos;
    }

    public List<Veiculo> consultarVeiculos(ListaModelosVeiculos listaModelosVeiculos) {

        System.out.println("Digite o modelo desejado:");
        final var modeloDesejado = leitura.nextLine();

        Optional<ModeloVeiculo> modeloEncontrado = listaModelosVeiculos.modelos().stream()
                .filter(e -> e.nome().equalsIgnoreCase(modeloDesejado))
                .findFirst();

        List<Veiculo> listaVeiculo = null;

        if (modeloEncontrado.isPresent()) {
            urlCorrente = urlCorrente + "/" + modeloEncontrado.get().codigo() + "/anos";
            modeloSelecionado = modeloEncontrado.get().nome();
            var jsonVeiculos = consumirAPI.obterDados(urlCorrente);
            listaVeiculo = converterDados.obterlista(jsonVeiculos, Veiculo.class);
        }

        return listaVeiculo;
    }

    public SumarioVeiculo selecionarVeiculo(List<Veiculo> listaVeiculo) {

        System.out.println("Digite o veículo desejado:");
        final var veiculoDesejado = leitura.nextLine();

        Optional<Veiculo> veiculoEncontrado = listaVeiculo.stream()
                .filter(e -> e.nome().equalsIgnoreCase(veiculoDesejado))
                .findFirst();

        SumarioVeiculo sumarioVeiculo = null;
        if (veiculoEncontrado.isPresent()) {
            urlCorrente = urlCorrente + "/" + veiculoEncontrado.get().codigo();
            veiculoSelecionado = veiculoEncontrado.get().nome();
            var jasonVeiculo = consumirAPI.obterDados(urlCorrente);
            sumarioVeiculo = converterDados.obterDados(jasonVeiculo, SumarioVeiculo.class);
        }

        return sumarioVeiculo;
    }

    public  List<SumarioVeiculo> selecionarTodosVeiculos(List<Veiculo> listaVeiculo) {

        urlCorrente = urlCorrente + "/";
        List<SumarioVeiculo> sumarios = listaVeiculo.stream().map(t -> {
            var urlCorrenteVeiculo = urlCorrente + t.codigo();
            var jasonVeiculo = consumirAPI.obterDados(urlCorrenteVeiculo);
            return converterDados.obterDados(jasonVeiculo, SumarioVeiculo.class);
        }).collect(Collectors.toList());;

       return sumarios;
    }

}
