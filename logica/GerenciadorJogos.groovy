package logica

import groovy.json.JsonBuilder
import basico.ObjetoBase
import dominio.*

class GerenciadorJogos extends ObjetoBase {
    ServerSocket servidor
    List<Usuario> usuarios
    List<Partida> partidas
    List<Convite> convites


    GerenciadorJogos(int portaServidor) {
        usuarios = [].asSynchronized()
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
                        processarMensagem(usuario, buffer)
                        //def now = new Date()
                        //output << "echo-response($now): " + buffer + "\n"
                    }
                    
                }catch (e){
                    println "Erro na conexão do usuário=$usuario socket=$socket"
                    fecharConexao(usuario)
                }
            }
        }
    }

    void iniciarGerenciaConexoes(){
        /*Iniciando serviço que gerencia conexões*/
        final long tempoGerenciaConexao = 15000
        final long tempoQuedaUsuario = 8000
        final long tempoDerrubarUsuario = tempoQuedaUsuario * 2
        Thread.startDaemon {
            while(true){
                sleep(tempoGerenciaConexao)
                Date dataHora = new Date()
                List<Usuario> usuariosComProblema = usuarios.findAll{Usuario u -> (dataHora.time - u.dtAtualizacao.time) > tempoQuedaUsuario }

                usuariosComProblema.each { Usuario usuario ->
                    Long tempoMiliSegSemAtualizacao = dataHora.time - usuario.dtAtualizacao.time
                    Socket socket = usuario.socket
                    if (tempoMiliSegSemAtualizacao > tempoDerrubarUsuario){
                        fecharConexao(usuario)
                    } else {
//                        OutputStream output = socket.outputStream
//                        output << 'ACK|'
//                        output.flush()
//
                        enviarMensagemReconecao(usuario)
                        /*TODO não é necessário implementar o retorno pois o mesmo já é */
                    }
                }   
                printStatus()
            }

        }
    }

    void processarMensagem(Usuario usuario, String mensagem) {
        println "recebendo mensagem $usuario: $mensagem"

    }



    void enviarMensagem(Usuario usuario, String mensagem) {
        try{
            println "enviarMensagem - $usuario - $mensagem"
            OutputStream output = usuario.socket.outputStream
            output << "$mensagem\n"
            output.flush()
        } catch(e) {
            /*TODO implementar lógica do que fazer no caso de erro de mensagem ao usuário*/
            println "falha na conexão do usuario $usuario"
        }

        // finally {
        //    fecharConexao(usuario)
        //}

    }


    void enviarMensagemReconecao(Usuario usuario) {
        String msg = serialize([keepAlive: new Date().time])
        enviarMensagem(usuario, msg)
    }

    void fecharConexao(Usuario usuario) {  
        try{      
            Socket socket = usuario.socket
            if (socket && !socket.closed && socket.connected) {
                String msg = serialize([close: new Date().time])
                enviarMensagem(usuario, msg)            
                socket.close()   
            }
        } catch (e) {
            println "fecharConexao - conexão do $usuario já estava fechada"
        }

        usuarios.remove(usuario)
          
                 
    }

    private String serialize(Map m) {
        // serialize
        //m = [a: 123, b: 'test']
        //JsonBuilder builder = new JsonBuilder()
        //builder(m)
        return m.inspect()
        //println builder.toString()
    }

    private def deserialize(String msg) {
        return Eval.me(msg)
    }



    void printStatus() {
        println "************SERVER STATUS ****************"
        println "Usuarios conectados: ${usuarios}"
        println "Convitess: ${convites}"
        println "Partida: ${partidas}"
        println "************SERVER STATUS ****************"

        println '\n' * 4
    }


    void jogar(Usuario usuario, String parametros){
    }



}