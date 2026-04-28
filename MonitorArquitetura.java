import java.io.*;
import java.nio.file.*;
import java.util.Scanner;

public class MonitorArquitetura {

    // Configurações Globais
    private static final String ARQUIVO_FONTE = "dados.txt";   // O arquivo que deve estar no Pen Drive
    private static final String ARQUIVO_DESTINO = "processado.txt"; // Onde os resultados serão salvos

    public static void main(String[] args) {
        System.out.println("=== SISTEMA DE MONITORAMENTO DE DADOS INICIADO ===");
        System.out.println("Aguardando conexao de hardware externo...");

        // 1. Captura o estado inicial do sistema (unidades de disco atuais)
        File[] discosIniciais = File.listRoots();

        // 2. Loop de Monitoramento (Hardware Listening)
        while (true) {
            File[] discosAtuais = File.listRoots();

            // Se o número de discos atuais for maior que o inicial, algo foi conectado
            if (discosAtuais.length > discosIniciais.length) {
                for (File disco : discosAtuais) {
                    if (ehNovaUnidade(disco, discosIniciais)) {
                        System.out.println("\n[HARDWARE] Novo dispositivo detectado: " + disco.getPath());
                        executarFluxoDeDados(disco);
                    }
                }
                // Atualiza o estado para evitar re-processamento
                discosIniciais = discosAtuais;
            }
           
            // Se um disco for removido, atualizamos a lista de referência
            if (discosAtuais.length < discosIniciais.length) {
                discosIniciais = discosAtuais;
                System.out.println("\n[HARDWARE] Dispositivo removido.");
            }

            pausar(2000); // Aguarda 2 segundos para não sobrecarregar a CPU
        }
    }

    /**
     * Lógica de Negócio: O que acontece quando o Pen Drive é inserido.
     */
    private static void executarFluxoDeDados(File caminhoUsb) {
        File arquivoNoUsb = new File(caminhoUsb, ARQUIVO_FONTE);

        if (arquivoNoUsb.exists()) {
            System.out.println("[INFO] Arquivo " + ARQUIVO_FONTE + " encontrado. Iniciando leitura...");
            lerEProcessar(arquivoNoUsb);
        } else {
            System.out.println("[AVISO] Pen Drive inserido, mas " + ARQUIVO_FONTE + " nao foi encontrado.");
        }
    }

    /**
     * Manipulação de Dados: Lê do USB e escreve no computador local.
     */
    private static void lerEProcessar(File entrada) {
        // Usamos BufferedWriter e BufferedReader para eficiência de memória (Arquitetura de Buffers)
        try (BufferedReader leitor = new BufferedReader(new FileReader(entrada));
             BufferedWriter escritor = new BufferedWriter(new FileWriter(ARQUIVO_DESTINO, true))) {

            String linha;
            escritor.write("--- NOVO LOTE DE DADOS ---\n");

            while ((linha = leitor.readLine()) != null) {
                // Exemplo de transformação simples: inverter o texto e colocar em maiúsculo
                String processada = new StringBuilder(linha).reverse().toString().toUpperCase();
               
                escritor.write("DADO: " + processada + "\n");
                System.out.println("Processando linha: " + linha);
            }

            escritor.write("--- FIM DO PROCESSAMENTO ---\n\n");
            System.out.println("[OK] Dados salvos em: " + ARQUIVO_DESTINO);

        } catch (IOException e) {
            System.err.println("[ERRO] Falha ao acessar os dados: " + e.getMessage());
        }
    }

    /**
     * Utilitário: Verifica se um disco específico é novo no sistema.
     */
    private static boolean ehNovaUnidade(File unidade, File[] anteriores) {
        for (File antiga : anteriores) {
            if (antiga.equals(unidade)) return false;
        }
        return true;
    }

    /**
     * Utilitário: Pausa a execução do programa.
     */
    private static void pausar(int milissegundos) {
        try {
            Thread.sleep(milissegundos);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}