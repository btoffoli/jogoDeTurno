package logica

import basico.ObjetoBase
import dominio.*

class GerenciadorJogos extends ObjetoBase {
    ServerSocket servidor
    List<Usuario> usuarios
    List<Partida> partidas
    List<Convite> convites

    GerenciadorJogos(int portaServidor) {
        usuarios = []//.asSynchronized()
        partidas = []
        convites = []

        /*Iniciando serviço que gerencia conexões*/
        this.iniciarGerenciaConexoes()

        servidor = new Servidor(portaServidor)
        while(true) {
            servidor.accept { Socket socket ->
                Usuario usuario
                try{
                    println "processando nova conexão..."
                    socket.keepAlive = true
                    usuario = new dominio.Usuario(
                            socket: socket,
                            nome: socket.toString(),
                            apelido: "jogador${usuarios.size()}",
                            ip: socket.inetAddress.hostAddress
                    )
                    usuarios << usuario
                    socket.withStreams { InputStream input, OutputStream output ->
                        /*TODO colocar lógica de atualizacao no usuário p/ qualquer ação do mesmo */
                        def reader = input.newReader()
                        def buffer = reader.readLine()
                        println "servidor recebeu: ${buffer}"
                        def now = new Date()
                        output << "echo-response($now): " + buffer + "\n"
                    }
                    println "encerrando conexão socket=$socket e usuario=${usuario}."
                }catch (e){
                    println "Erro na conexão do usuário=$usuario socket=$socket"

                }
            }
        }
    }

    void iniciarGerenciaConexoes(){
        /*Iniciando serviço que gerencia conexões*/
        final long tempoGerenciaConexao = 5000
        final long tempoQuedaUsuario = 8000
        Thread.startDaemon {
            while(true){
                sleep(tempoGerenciaConexao)
                Date dataHora = new Date()
                List<Usuario> usuariosComProblema = usuarios.findAll{Usuario u -> (dataHora.time - u.dtAtualizacao.time) > tempoQuedaUsuario }

                usuariosComProblema.each { Usuario usuario ->
                    Socket socket = usuario.socket
                    if (socket && socket.closed){
                        usuarios.remove(usuario)
                    } else {
//                        OutputStream output = socket.outputStream
//                        output << 'ACK|'
//                        output.flush()
//
                        enviarMensagem()
                        /*TODO não é necessário implementar o retorno pois o mesmo já é */
                    }
                }

            }

        }
    }

    void processarMensagem(Usuario usuario, String mensagem) {
    }



    void enviarMensagem(Usuario usuario, String mensagem) {
        OutputStream output = socket.outputStream
        output << mensagem
        output.flush()

    }



    void printStatus() {
        println "Usuarios conectados: ${usuarios}"
        println "Convitess: ${convites}"
        println "Canais: ${canais}"
    }


    void jogar(Usuario usuario, String parametros){
    }



}